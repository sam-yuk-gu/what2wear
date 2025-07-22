package com.samyukgu.what2wear.myCodi.dao;

import com.samyukgu.what2wear.myCodi.model.MyCodi;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.*;
import java.util.List;
import java.util.Properties;

public class MyCodiOracleDAO implements MyCodiDAO {

    private static String url;
    private static String username;
    private static String password;

    public MyCodiOracleDAO() {
        Properties props = new Properties();
        try (InputStream is = ClassLoader.getSystemResourceAsStream("application.properties");
             InputStreamReader isr = new InputStreamReader(is, StandardCharsets.UTF_8)) {
            props.load(isr);
            url = props.getProperty("db.url");
            username = props.getProperty("db.user");
            password = props.getProperty("db.password");

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("check your database connection");
        }
    }

    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url, username, password);
    }
    // 해당 아이디의 1건 코디 조회
    @Override
    public MyCodi findById(Long id, Long memberId) {
        String sql = """
                SELECT *
                FROM codi
                WHERE id = ?
                AND member_id = ?
                """;
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setLong(1, id);
            pstmt.setLong(2, memberId);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new MyCodi(
                            rs.getLong("id"),
                            rs.getLong("member_id"),
                            rs.getString("schedule"),
                            rs.getDate("create_at"),
                            rs.getLong("scope"),
                            rs.getString("weather"),
                            rs.getString("codi_type")
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // 해당 회원의 전체 코디 목록 조회
    @Override
    public List<MyCodi> findAll(Long memberId) {
        String sql = """
                SELECT *
                FROM codi 
                WHERE member_id = ?
                """;

        List<MyCodi> codiList = new java.util.ArrayList<>();
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setLong(1, memberId);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    MyCodi codi = new MyCodi(
                            rs.getLong("id"),
                            rs.getLong("member_id"),
                            rs.getString("schedule"),
                            rs.getDate("create_at"),
                            rs.getLong("scope"),
                            rs.getString("weather"),
                            rs.getString("codi_type")
                    );
                    codiList.add(codi);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return codiList;
    }

    // 코디 저장
    @Override
    public void save(MyCodi myCodi) {
        String sql = """
                    INSERT INTO codi (id, member_id, schedule, create_at, scope, weather, codi_type) 
                    VALUES (?, ?, ?, ?, ?, ?, ?)
                    """;

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setLong(1, myCodi.getId());
            pstmt.setLong(2, myCodi.getMember_id());
            pstmt.setString(3, myCodi.getSchedule());
            pstmt.setDate(4, new java.sql.Date(myCodi.getCreate_at().getTime()));
            pstmt.setLong(5, myCodi.getScope());
            pstmt.setString(6, myCodi.getWeather());
            pstmt.setString(7, myCodi.getCodi_type());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void delete(Long id, Long memberId) {
        String sql =
                """
                DELETE FROM codi WHERE id = ?
                AND member_id = ?
                """;
        try (Connection conn = getConnection();
             var pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, id);
            pstmt.setLong(2, memberId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
