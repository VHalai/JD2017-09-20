package by.it.mustaphin.jd03_03.connection;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class ConnectionCreator {
    private static Connection con = null;
    private static Properties prop = new Properties();

    static {
        try {
            Class.forName("com.jdbc.mysql.Driver");
            prop.load(ConnectionCreator.class.getResourceAsStream("dbConnection.properties"));
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Connection getConnection() throws SQLException {
        if (null != con || con.isClosed()) {
            return DriverManager.getConnection(prop.getProperty("url"), prop.getProperty("login"), prop.getProperty("password"));
        }
        return con;
    }

    public static void main(String[] args) throws SQLException {
        Connection connection = getConnection();
        if (connection.isClosed()) {
            System.out.println("Connection is closed");
        }
    }
}
