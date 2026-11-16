package com.clubmanager.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;


public class DatabaseConnection {

    private static final String URL      = "jdbc:mysql://localhost:3306/club_db";
    private static final String USER     = "root";
    private static final String PASSWORD = "Masala@7878!";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}

// So this file opens the jdbc connector to mysql