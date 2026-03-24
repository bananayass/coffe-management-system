package dao;

import java.sql.Connection;
import java.sql.DriverManager;

public class DBConnection {
    public static Connection getConnection() throws Exception {
        String url = "jdbc:mysql://localhost:3306/coffee_db";
        String user = "root";
        String password = "";

        return DriverManager.getConnection(url, user, password);
    }
}