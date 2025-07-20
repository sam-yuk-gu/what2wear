package com.samyukgu.what2wear.wardrobe.dao;

import com.samyukgu.what2wear.wardrobe.model.Wardrobe;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class WardrobeOracleDAO implements WardrobeDAO {

    private static String url;
    private static String username;
    private static String password;

    public WardrobeOracleDAO() {
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

    // 해당 아이디의 옷 상세 조회
    @Override
    public Wardrobe findById(Long id, Long memberId) {
        String sql = """
        select * 
        FROM wardrobe 
        WHERE id = ?
        AND member_id = ?
        AND deleted = 'N'
        """;
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)
        ) {
            pstmt.setLong(1, id);
            pstmt.setLong(2, memberId);

            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return new Wardrobe(
                        rs.getLong("id"),
                        rs.getLong("member_id"),
                        rs.getLong("category_id"),
                        rs.getString("name"),
                        rs.getString("memo"),
                        rs.getString("like"),
                        rs.getBytes("picture"),
                        rs.getString("keyword"),
                        rs.getString("size"),
                        rs.getString("color"),
                        rs.getString("brand"),
                        rs.getString("deleted")
                );
            }
            return null;
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("check your Wardrobe Id");
        }
    }

    // 해당 아이디의 모든 옷을 조회
    @Override
    public List<Wardrobe> findAll(Long memberId) {
        String sql = """
            SELECT *
            FROM wardrobe
            WHERE deleted = 'N'
              AND member_id = ?
            """;

            List<Wardrobe> wardrobes = new ArrayList<>();

            try (Connection conn = getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(sql)) {
                    pstmt.setLong(1, memberId);
                    try (ResultSet rs = pstmt.executeQuery()) {
                        while (rs.next()) {
                           wardrobes.add(new Wardrobe(
                                rs.getLong("id"),
                                rs.getLong("member_id"),
                                rs.getLong("category_id"),
                                rs.getString("name"),
                                rs.getString("memo"),
                                rs.getString("like"),
                                rs.getBytes("picture"),
                                rs.getString("keyword"),
                                rs.getString("size"),
                                rs.getString("color"),
                                rs.getString("brand"),
                                rs.getString("deleted")
                            ));
                    }
                }
                return wardrobes;
            } catch (SQLException e) {
                    e.printStackTrace();
                    throw new RuntimeException("Failed to find Wardrobes");
            }
    }
    // 옷 저장하기
    public void save(Wardrobe wardrobe) {
        String sql = """
                INSERT INTO wardrobe 
                (id, member_id, category_id, name, memo, like, picture, keyword, size, color, brand, deleted)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                """;

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setLong(1, wardrobe.getId());
            pstmt.setLong(2, wardrobe.getMember_id()); // 특정 회원 ID
            pstmt.setLong(3, wardrobe.getCategory_id());
            pstmt.setString(4, wardrobe.getName());
            pstmt.setString(5, wardrobe.getMemo());
            pstmt.setString(6, wardrobe.getLike());
            pstmt.setBytes(7, wardrobe.getPicture());
            pstmt.setString(8, wardrobe.getKeyword());
            pstmt.setString(9, wardrobe.getSize());
            pstmt.setString(10, wardrobe.getColor());
            pstmt.setString(11, wardrobe.getBrand());
            pstmt.setString(12, wardrobe.getDeleted());

            pstmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to save wardrobe");
        }
    }
    // 옷 수정하기
    public void update(Wardrobe wardrobe) {
        String sql = """
    UPDATE wardrobe SET 
        member_id = ?, 
        category_id = ?, 
        name = ?, 
        memo = ?, 
        like = ?, 
        picture = ?, 
        keyword = ?, 
        size = ?, 
        color = ?, 
        brand = ?, 
        deleted = ?
    WHERE id = ? AND member_id = ?
    """;

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setLong(1, wardrobe.getMember_id());
            pstmt.setLong(2, wardrobe.getCategory_id());
            pstmt.setString(3, wardrobe.getName());
            pstmt.setString(4, wardrobe.getMemo());
            pstmt.setString(5, wardrobe.getLike());
            pstmt.setBytes(6, wardrobe.getPicture());
            pstmt.setString(7, wardrobe.getKeyword());
            pstmt.setString(8, wardrobe.getSize());
            pstmt.setString(9, wardrobe.getColor());
            pstmt.setString(10, wardrobe.getBrand());
            pstmt.setString(11, wardrobe.getDeleted());
            pstmt.setLong(12, wardrobe.getId());
            pstmt.setLong(13, wardrobe.getMember_id()); // 검증 추가

            pstmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to update wardrobe");
        }
    }
    // 특정 회원의 옷 삭제
    @Override
    public void delete(Long id, Long memberId) {
        String sql = """
    UPDATE wardrobe 
    SET deleted = 'Y' 
    WHERE id = ? AND member_id = ?
    """;

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setLong(1, id);
            pstmt.setLong(2, memberId); // member_id 일치하는 경우에만 삭제

            pstmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to delete wardrobe");
        }
    }
}
