import exception.*;

import java.sql.*;
import java.util.*;

public class ParkingProcess {
    public void init(){
        Connection connection = null;
        Statement statement = null;
        try {
            connection = DbUtil.getConnection();
            statement = connection.createStatement();
            String resetSql = "DROP TABLE IF EXISTS total_parking;";
            PreparedStatement preparedStatement =connection.prepareStatement(resetSql);
            preparedStatement.execute();
            String createSql = "CREATE TABLE total_parking(" +
                    "id INT PRIMARY KEY," +
                    "park_name VARCHAR(8)," +
                    "total_num INT,"+
                    "now_num INT DEFAULT 0);";
            preparedStatement =connection.prepareStatement(createSql);
            preparedStatement.execute();

            resetSql = "DROP TABLE IF EXISTS parking_one;";
            preparedStatement =connection.prepareStatement(resetSql);
            preparedStatement.execute();
            createSql = "CREATE TABLE parking_one(" +
                    "no INT UNIQUE," +
                    "car_num VARCHAR(6));";
            preparedStatement =connection.prepareStatement(createSql);
            preparedStatement.execute();

            resetSql = "DROP TABLE IF EXISTS parking_two;";
            preparedStatement =connection.prepareStatement(resetSql);
            preparedStatement.execute();
            createSql = "CREATE TABLE parking_two(" +
                    "no INT UNIQUE," +
                    "car_num VARCHAR(6));";
            preparedStatement =connection.prepareStatement(createSql);
            preparedStatement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }finally {
            DbUtil.releaseSource(connection,statement);
        }
    }

    public void initParkingSize(String str){
        String[] infoList = str.split(",");
        String[] firstParking = infoList[0].split(":");
        String[] secondParking = infoList[1].split(":");

        Connection connection = null;
        Statement statement = null;

        try {
            connection = DbUtil.getConnection();
            statement = connection.createStatement();
            String insertTotalTableSql = "INSERT INTO total_parking(id,park_name,total_num,now_num)" +
                    "VALUES (1,?,?,null);";
            PreparedStatement preparedStatement = connection.prepareStatement(insertTotalTableSql);
            preparedStatement.setString(1,firstParking[0]);
            preparedStatement.setInt(2,Integer.valueOf(firstParking[1]));
            preparedStatement.execute();

            insertTotalTableSql ="INSERT INTO total_parking(id,park_name,total_num,now_num)" +
                    "VALUES (2,?,?,null);";
            preparedStatement = connection.prepareStatement(insertTotalTableSql);
            preparedStatement.setString(1,secondParking[0]);
            preparedStatement.setInt(2,Integer.valueOf(secondParking[1]));
            preparedStatement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }finally {
            DbUtil.releaseSource(connection,statement);
        }
    }

    public String updateParking(String carNumber){
        if(!isFull(1)){
            return addParking(1,carNumber);
        }else if(!isFull(2)){
            return addParking(2,carNumber);
        }else{
            throw new ParkingLotFullException("非常抱歉，由于车位已满，暂时无法为您停车！");
        }
    }

    public boolean isFull(int id){
        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;
        try {
            connection = DbUtil.getConnection();
            statement = connection.createStatement();
            String sql = "SELECT total_num,now_num FROM total_parking" +
                    " WHERE id="+id+";";
            resultSet= statement.executeQuery(sql);
            while(resultSet.next()){
                int total = resultSet.getInt("total_num");
                int nowParking = resultSet.getInt("now_num");
                return total==nowParking;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }finally {
            DbUtil.releaseSource(connection,statement,resultSet);
        }
        return false;
    }

    public Map<Integer,String> getStatFromParkingLot(String tableName){
        Map<Integer,String> returnMap = new HashMap<>();
        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;
        try {
            connection = DbUtil.getConnection();
            statement = connection.createStatement();
            String sql = "SELECT * FROM " +tableName+";";
            resultSet= statement.executeQuery(sql);
            while(resultSet.next()){
                int parkingId = resultSet.getInt("no");
                String parkingNum = resultSet.getString("car_num");
                returnMap.put(parkingId,parkingNum);
            }
            return returnMap;
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }finally {
            DbUtil.releaseSource(connection,statement,resultSet);
        }
        return returnMap;
    }

    public int getMaxParkingNum(int id){
        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;
        try {
            connection = DbUtil.getConnection();
            statement = connection.createStatement();
            String sql = "SELECT total_num FROM total_parking" +
                    " WHERE id="+id+";";
            resultSet= statement.executeQuery(sql);
            while(resultSet.next()){
                return resultSet.getInt("total_num");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }finally {
            DbUtil.releaseSource(connection,statement,resultSet);
        }
        return 0;
    }

    public int getNowParkingNum(int id){
        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;
        try {
            connection = DbUtil.getConnection();
            statement = connection.createStatement();
            String sql = "SELECT now_num FROM total_parking" +
                    " WHERE id="+id+";";
            resultSet= statement.executeQuery(sql);
            while(resultSet.next()){
                return resultSet.getInt("now_num");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }finally {
            DbUtil.releaseSource(connection,statement,resultSet);
        }
        return 0;
    }

    public String addParking(int id,String carNumber){
        String parkingLotName =null;
        if(id==1){
            parkingLotName = "parking_one";
        }else if(id==2){
            parkingLotName = "parking_two";
        }
        int maxParkingNum = getMaxParkingNum(id);
        int nowParkingNum = getNowParkingNum(id);
        Map<Integer,String> statFromParkingLot = getStatFromParkingLot(parkingLotName);
        int finalNo=0;
        for(int i=1;i<=maxParkingNum;i++){
            if(!statFromParkingLot.containsKey(i)){
                addJDBCProcess(parkingLotName,i,carNumber);
                updateTotalTable(id,nowParkingNum+1);
                finalNo = i;
                break;
            }
        }
        String returnStr = String.format("已将您的车牌号为%s的车辆停到%s停车场%d号车位，" +
                "停车券为：\"%s,%d,%s\"，请您妥善保存！"
                ,carNumber,parkingLotName,finalNo,parkingLotName,finalNo,carNumber);
        return returnStr;
    }

    public void addJDBCProcess(String parkingLotName,int no,String carNumber){
        Connection connection = null;
        Statement statement = null;
        try {
            connection = DbUtil.getConnection();
            statement = connection.createStatement();
            String insertTotalTableSql = "INSERT INTO " +parkingLotName+
                    " (no,car_num) VALUES (?,?);";
            PreparedStatement preparedStatement = connection.prepareStatement(insertTotalTableSql);
            preparedStatement.setInt(1,no);
            preparedStatement.setString(2,carNumber);
            preparedStatement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }finally {
            DbUtil.releaseSource(connection,statement);
        }
    }

    public void updateTotalTable(int id,int nowNumber){
        Connection connection = null;
        Statement statement = null;
        try {
            connection = DbUtil.getConnection();
            statement = connection.createStatement();
            String sql = "UPDATE total_parking SET now_num=? WHERE id = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1,nowNumber);
            preparedStatement.setInt(2,id);
            preparedStatement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }finally {
            DbUtil.releaseSource(connection,statement);
        }
    }
}
