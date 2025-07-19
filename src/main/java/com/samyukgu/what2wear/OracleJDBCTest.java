package com.samyukgu.what2wear;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class OracleJDBCTest {
    public static void main(String[] args) {
        String url = "jdbc:oracle:thin:@localhost:1521/XEPDB1";
        String user = "osukyeong";
        String password = "su1211";

        try {
            Connection conn = DriverManager.getConnection(url, user, password);
            System.out.println("✅ Oracle DB 연결 성공!");
            conn.close();
        } catch (SQLException e) {
            System.out.println("❌ Oracle DB 연결 실패");
            e.printStackTrace();
        }
    }
}
