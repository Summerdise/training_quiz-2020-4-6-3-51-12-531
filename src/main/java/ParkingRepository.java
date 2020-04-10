import java.sql.*;
import java.util.*;

public class ParkingRepository {
    public void init() {
        Connection connection = null;
        Statement statement = null;
        try {
            connection = DbUtil.getConnection();
            statement = connection.createStatement();
            String resetSql = "DROP TABLE IF EXISTS total_parking;";
            PreparedStatement preparedStatement = connection.prepareStatement(resetSql);
            preparedStatement.execute();
            String createSql = "CREATE TABLE total_parking(" +
                    "id INT PRIMARY KEY," +
                    "park_name VARCHAR(8)," +
                    "total_num INT," +
                    "now_num INT DEFAULT 0);";
            preparedStatement = connection.prepareStatement(createSql);
            preparedStatement.execute();

            resetSql = "DROP TABLE IF EXISTS parking_one;";
            preparedStatement = connection.prepareStatement(resetSql);
            preparedStatement.execute();
            createSql = "CREATE TABLE parking_one(" +
                    "no INT UNIQUE," +
                    "car_num VARCHAR(6));";
            preparedStatement = connection.prepareStatement(createSql);
            preparedStatement.execute();

            resetSql = "DROP TABLE IF EXISTS parking_two;";
            preparedStatement = connection.prepareStatement(resetSql);
            preparedStatement.execute();
            createSql = "CREATE TABLE parking_two(" +
                    "no INT UNIQUE," +
                    "car_num VARCHAR(6));";
            preparedStatement = connection.prepareStatement(createSql);
            preparedStatement.execute();
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            DbUtil.releaseSource(connection, statement);
        }
    }

    public void initParkingSize(String str) {
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
            preparedStatement.setString(1, firstParking[0]);
            preparedStatement.setInt(2, Integer.parseInt(firstParking[1]));
            preparedStatement.execute();

            insertTotalTableSql = "INSERT INTO total_parking(id,park_name,total_num,now_num)" +
                    "VALUES (2,?,?,null);";
            preparedStatement = connection.prepareStatement(insertTotalTableSql);
            preparedStatement.setString(1, secondParking[0]);
            preparedStatement.setInt(2, Integer.parseInt(secondParking[1]));
            preparedStatement.execute();
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            DbUtil.releaseSource(connection, statement);
        }
    }

    public boolean isFull(int id) {
        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;
        try {
            connection = DbUtil.getConnection();
            statement = connection.createStatement();
            String sql = "SELECT total_num,now_num FROM total_parking" +
                    " WHERE id=" + id + ";";
            resultSet = statement.executeQuery(sql);
            while (resultSet.next()) {
                int total = resultSet.getInt("total_num");
                int nowParking = resultSet.getInt("now_num");
                return total == nowParking;
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            DbUtil.releaseSource(connection, statement, resultSet);
        }
        return false;
    }

    public Map<Integer, String> getStatFromParkingLot(String tableName) {
        Map<Integer, String> returnMap = new HashMap<>();
        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;
        try {
            connection = DbUtil.getConnection();
            statement = connection.createStatement();
            String sql = "SELECT * FROM " + tableName + ";";
            resultSet = statement.executeQuery(sql);
            while (resultSet.next()) {
                int parkingId = resultSet.getInt("no");
                String parkingNum = resultSet.getString("car_num");
                returnMap.put(parkingId, parkingNum);
            }
            return returnMap;
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            DbUtil.releaseSource(connection, statement, resultSet);
        }
        return returnMap;
    }

    public int getMaxParkingNum(int id) {
        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;
        try {
            connection = DbUtil.getConnection();
            statement = connection.createStatement();
            String sql = "SELECT total_num FROM total_parking" +
                    " WHERE id=" + id + ";";
            resultSet = statement.executeQuery(sql);
            while (resultSet.next()) {
                return resultSet.getInt("total_num");
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            DbUtil.releaseSource(connection, statement, resultSet);
        }
        return 0;
    }

    public int getNowParkingNum(int id) {
        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;
        try {
            connection = DbUtil.getConnection();
            statement = connection.createStatement();
            String sql = "SELECT now_num FROM total_parking" +
                    " WHERE id=" + id + ";";
            resultSet = statement.executeQuery(sql);
            while (resultSet.next()) {
                return resultSet.getInt("now_num");
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            DbUtil.releaseSource(connection, statement, resultSet);
        }
        return 0;
    }

    public String getParkingLotName(int id) {
        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;
        try {
            connection = DbUtil.getConnection();
            statement = connection.createStatement();
            String sql = "SELECT park_name FROM total_parking" +
                    " WHERE id=" + id + ";";
            resultSet = statement.executeQuery(sql);
            while (resultSet.next()) {
                return resultSet.getString("park_name");
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            DbUtil.releaseSource(connection, statement, resultSet);
        }
        return null;
    }

    public String getParkingChooseFromId(int parkingID) {
        if (parkingID == 1) {
            return "parking_one";
        } else if (parkingID == 2) {
            return "parking_two";
        }
        return null;
    }

    public void addJDBCProcess(String parkingLotName, int no, String carNumber) {
        Connection connection = null;
        Statement statement = null;
        try {
            connection = DbUtil.getConnection();
            statement = connection.createStatement();
            String insertTotalTableSql = "INSERT INTO " + parkingLotName +
                    " (no,car_num) VALUES (?,?);";
            PreparedStatement preparedStatement = connection.prepareStatement(insertTotalTableSql);
            preparedStatement.setInt(1, no);
            preparedStatement.setString(2, carNumber);
            preparedStatement.execute();
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            DbUtil.releaseSource(connection, statement);
        }
    }

    public void deleteJDBCProcess(int inputID, int inputNumber) {
        String parkingChoose = getParkingChooseFromId(inputID);
        Connection connection = null;
        Statement statement = null;
        try {
            connection = DbUtil.getConnection();
            statement = connection.createStatement();
            String sql = "DELETE FROM " + parkingChoose +
                    " WHERE no = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, inputNumber);
            preparedStatement.execute();
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            DbUtil.releaseSource(connection, statement);
        }
    }

    public void updateTotalTable(int id, int nowNumber) {
        Connection connection = null;
        Statement statement = null;
        try {
            connection = DbUtil.getConnection();
            statement = connection.createStatement();
            String sql = "UPDATE total_parking SET now_num=? WHERE id = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, nowNumber);
            preparedStatement.setInt(2, id);
            preparedStatement.execute();
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            DbUtil.releaseSource(connection, statement);
        }
    }
}
