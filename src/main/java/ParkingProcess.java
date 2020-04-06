import java.sql.*;

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
            String insertTotalTableSql = "INSERT INTO total_parking(park_name,total_num,now_num)" +
                    "VALUES (?,?,null);";
            PreparedStatement preparedStatement = connection.prepareStatement(insertTotalTableSql);
            preparedStatement.setString(1,firstParking[0]);
            preparedStatement.setInt(2,Integer.valueOf(firstParking[1]));
            preparedStatement.execute();

            insertTotalTableSql ="INSERT INTO total_parking(park_name,total_num,now_num)" +
                    "VALUES (?,?,null);";
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

    public void updateParking(){

    }
}
