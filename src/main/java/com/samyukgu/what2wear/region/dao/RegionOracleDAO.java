package com.samyukgu.what2wear.region.dao;

import com.samyukgu.what2wear.region.model.Region;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.Properties;

public class RegionOracleDAO implements RegionDAO {
    private static String url;
    private static String dbUser;
    private static String dbPassword;

    public RegionOracleDAO() {
        Properties props = new Properties();
        try (InputStream is = ClassLoader.getSystemResourceAsStream("application.properties");
             InputStreamReader isr = new InputStreamReader(is, StandardCharsets.UTF_8)) {
            props.load(isr);
            url = props.getProperty("db.url");
            dbUser = props.getProperty("db.user");
            dbPassword = props.getProperty("db.password");
        } catch (Exception e) {
            throw new RuntimeException("DB 연결 정보 로딩 실패", e);
        }
    }

    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url, dbUser, dbPassword);
    }

    @Override
    public void saveAll(List<Region> regions) {
        String sql = """
            INSERT INTO region (id, region_parent, region_child, nx, ny)
            VALUES (SEQ_REGION.NEXTVAL, ?, ?, ?, ?)
        """;

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            for (Region region : regions) {
                pstmt.setString(1, region.getRegionParent());
                pstmt.setString(2, region.getRegionChild());
                pstmt.setLong(3, region.getNx());
                pstmt.setLong(4, region.getNy());
                pstmt.addBatch();
            }
            pstmt.executeBatch();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Region 저장 실패", e);
        }
    }
}