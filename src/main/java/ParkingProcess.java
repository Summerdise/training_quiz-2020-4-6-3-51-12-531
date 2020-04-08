import exception.*;

import java.sql.*;
import java.util.*;

public class ParkingProcess {
    public ParkingRepository parkingRepository = new ParkingRepository();

    public void initParkingLot(String message) {
        parkingRepository.init();
        parkingRepository.initParkingSize(message);
    }

    public String updateParking(String carNumber) {
        if (!parkingRepository.isFull(1)) {
            return addParking(1, carNumber);
        } else if (!parkingRepository.isFull(2)) {
            return addParking(2, carNumber);
        } else {
            throw new ParkingLotFullException("非常抱歉，由于车位已满，暂时无法为您停车！");
        }
    }


    public String getParkingChooseFromId(int parkingID) {
        if (parkingID == 1) {
            return "parking_one";
        } else if (parkingID == 2) {
            return "parking_two";
        }
        return null;
    }

    public String addParking(int id, String carNumber) {
        String parkingChoose = getParkingChooseFromId(id);
        int maxParkingNum = parkingRepository.getMaxParkingNum(id);
        int nowParkingNum = parkingRepository.getNowParkingNum(id);
        String parkingLotName = parkingRepository.getParkingLotName(id);
        Map<Integer, String> statFromParkingLot = parkingRepository.getStatFromParkingLot(parkingChoose);
        int finalNo = 0;
        for (int i = 1; i <= maxParkingNum; i++) {
            if (!statFromParkingLot.containsKey(i)) {
                parkingRepository.addJDBCProcess(parkingChoose, i, carNumber);
                parkingRepository.updateTotalTable(id, nowParkingNum + 1);
                finalNo = i;
                break;
            }
        }
        String returnStr = String.format("已将您的车牌号为%s的车辆停到%s停车场%d号车位，" +
                        "停车券为：\"%s,%d,%s\"，请您妥善保存！"
                , carNumber, parkingLotName, finalNo, parkingLotName, finalNo, carNumber);
        return returnStr;
    }

    public String takeOffCar(String input) {
        String[] inputList = input.split(",");
        String parkingLotName = inputList[0];
        int inputNumber = Integer.valueOf(inputList[1]);
        String inputCarNumber = inputList[2];
        int parkingLotId = getParkingLotId(parkingLotName);
        if (isInParkingLot(parkingLotId, inputNumber, inputCarNumber)) {
            parkingRepository.deleteJDBCProcess(parkingLotId, inputNumber);
            int nowParkingNum = parkingRepository.getNowParkingNum(parkingLotId);
            parkingRepository.updateTotalTable(parkingLotId, nowParkingNum - 1);
            return String.format("已为您取到车牌号为%s的车辆，很高兴为您服务，祝您生活愉快！", inputCarNumber);
        } else {
            throw new InvalidTicketException("很抱歉，无法通过您提供的停车券为您找到相应的车辆，请您再次核对停车券是否有效！");
        }
    }

    public int getParkingLotId(String parkingLotName) {
        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;
        try {
            connection = DbUtil.getConnection();
            statement = connection.createStatement();
            String sql = "SELECT id FROM total_parking" +
                    " WHERE park_name='" + parkingLotName + "';";
            resultSet = statement.executeQuery(sql);
            while (resultSet.next()) {
                return resultSet.getInt("id");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            DbUtil.releaseSource(connection, statement, resultSet);
        }
        return 0;
    }

    public boolean isInParkingLot(int inputID, int inputNumber, String inputCarNumber) {
        String parkingChoose = getParkingChooseFromId(inputID);
        Map<Integer, String> statFromParkingLot = parkingRepository.getStatFromParkingLot(parkingChoose);
        return statFromParkingLot.containsKey(inputNumber) && statFromParkingLot.get(inputNumber).equals(inputCarNumber);
    }
}
