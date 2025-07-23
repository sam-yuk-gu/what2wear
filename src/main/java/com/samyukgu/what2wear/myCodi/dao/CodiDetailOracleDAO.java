package com.samyukgu.what2wear.myCodi.dao;

import com.samyukgu.what2wear.myCodi.model.CodiDetail;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class CodiDetailOracleDAO implements CodiDetailDAO {

    private static String url;
    private static String username;
    private static String password;

    public CodiDetailOracleDAO() {
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

    @Override
    public void save(CodiDetail codiDetail) {
        String sql = """
            INSERT INTO codi_detail (codi_id, clothes_id)
            VALUES (?, ?)
            """;

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setLong(1, codiDetail.getCodiId());
            pstmt.setLong(2, codiDetail.getClothesId());

            pstmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("코디 상세 저장 중 오류", e);
        }
    }

    @Override
    public void deleteByCodiId(Long codiId) {
        String sql = """
            DELETE FROM codi_detail 
            WHERE codi_id = ?
            """;

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setLong(1, codiId);
            pstmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("코디 상세 삭제 중 오류", e);
        }
    }

    @Override
    public List<Long> findClothesIdsByCodiId(Long codiId) {
        String sql = """
            SELECT clothes_id 
            FROM codi_detail 
            WHERE codi_id = ?
            """;

        List<Long> clothesIds = new ArrayList<>();

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setLong(1, codiId);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    clothesIds.add(rs.getLong("clothes_id"));
                }
            }

            return clothesIds;

        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("코디 옷 목록 조회 중 오류", e);
        }
    }

    @Override
    public List<Long> findCodiIdsByClothesId(Long clothesId) {
        String sql = """
            SELECT codi_id 
            FROM codi_detail 
            WHERE clothes_id = ?
            """;

        List<Long> codiIds = new ArrayList<>();

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setLong(1, clothesId);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    codiIds.add(rs.getLong("codi_id"));
                }
            }

            return codiIds;

        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("옷이 포함된 코디 목록 조회 중 오류", e);
        }
    }
}