import java.io.FileReader;
import java.io.IOException;
import java.sql.*;
import java.net.URL;
import java.util.Objects;
import java.util.Properties;

public class DbUtil {
    public static String URL;
    public static String USER;
    public static String PASSWORD ;
    public static String DRIVER ;


    static{
        try {
            Properties pro = new Properties();
            ClassLoader classLoader = DbUtil.class.getClassLoader();
            URL pathURL = classLoader.getResource("jdbc.properties");
            String path = Objects.requireNonNull(pathURL).getPath();
            pro.load(new FileReader(path));
            URL =pro.getProperty("url");
            USER =pro.getProperty("user");
            PASSWORD =pro.getProperty("password");
            DRIVER = pro.getProperty("driver");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Connection getConnection() throws SQLException, ClassNotFoundException {
        Class.forName(DRIVER);
        return DriverManager.getConnection(URL,USER,PASSWORD);
    }
    public static void releaseSource(Connection conn, Statement statement){
        doubleClose(conn,statement);
    }

    public static void releaseSource(Connection conn, Statement statement, ResultSet res){
        if(null != res){
            try {
                res.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        doubleClose(conn,statement);
    }

    public static void doubleClose(Connection conn,Statement statement){
        if(null!=statement){
            try {
                statement.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        if(null != conn){
            try {
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
