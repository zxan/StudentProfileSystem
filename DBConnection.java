package com.kiet.studentjdbc;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public final class DBConnection {

    // createDatabaseIfNotExist=true creates student_jdbc_db on first run
    private static final String URL =
            "jdbc:mysql://localhost:3306/student_jdbc_db"
            + "?createDatabaseIfNotExist=true&useSSL=false&allowPublicKeyRetrieval=true";
    private static final String USER = "root";
    private static final String PASSWORD = "";   // XAMPP default

    private DBConnection() {
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}
