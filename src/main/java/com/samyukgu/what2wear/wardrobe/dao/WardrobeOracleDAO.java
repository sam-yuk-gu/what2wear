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
        SELECT * 
        FROM clothes 
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
                        rs.getString("liked"),
                        rs.getBytes("picture"),
                        rs.getString("keyword"),
                        rs.getString("item_size"),
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
            FROM clothes
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
                            rs.getString("liked"),
                            rs.getBytes("picture"),
                            rs.getString("keyword"),
                            rs.getString("item_size"),
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
    @Override
    public void save(Wardrobe wardrobe) {
        String sql = """
                INSERT INTO clothes 
                (id, member_id, category_id, name, liked, memo, picture, keyword, item_size, color, brand, deleted, created_at)
                VALUES (SEQ_CLOTHES.NEXTVAL, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                """;

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setLong(1, wardrobe.getMemberId());
            pstmt.setLong(2, wardrobe.getCategoryId());
            pstmt.setString(3, wardrobe.getName());
            pstmt.setString(4, wardrobe.getLike() != null ? wardrobe.getLike() : "N"); // liked 설정
            pstmt.setString(5, wardrobe.getMemo());

            if (wardrobe.getPicture() != null) {
                pstmt.setBytes(6, wardrobe.getPicture());
            } else {
                pstmt.setNull(6, Types.BLOB);
            }
            pstmt.setString(7, wardrobe.getKeyword());
            pstmt.setString(8, wardrobe.getSize());
            pstmt.setString(9, wardrobe.getColor());
            pstmt.setString(10, wardrobe.getBrand());
            pstmt.setString(11, wardrobe.getDeleted());
            pstmt.setDate(12, new Date(System.currentTimeMillis()));

            pstmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("옷 저장 중 오류");
        }
    }

    // 옷 수정하기
    @Override
    public void update(Wardrobe wardrobe) {
        System.out.println("=== DAO update 메서드 시작 ===");
        System.out.println("업데이트할 옷 ID: " + wardrobe.getId());
        System.out.println("회원 ID: " + wardrobe.getMemberId());
        System.out.println("즐겨찾기 상태: " + wardrobe.getLike());

        String sql = """
    UPDATE clothes SET 
        category_id = ?, 
        name = ?, 
        memo = ?, 
        liked = ?, 
        picture = ?, 
        keyword = ?, 
        item_size = ?, 
        color = ?, 
        brand = ?, 
        deleted = ?
    WHERE id = ? AND member_id = ?
    """;

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setLong(1, wardrobe.getCategoryId());
            pstmt.setString(2, wardrobe.getName());
            pstmt.setString(3, wardrobe.getMemo());
            pstmt.setString(4, wardrobe.getLike()); // liked 컬럼 업데이트

            if (wardrobe.getPicture() != null) {
                pstmt.setBytes(5, wardrobe.getPicture());
            } else {
                pstmt.setNull(5, Types.BLOB);
            }

            pstmt.setString(6, wardrobe.getKeyword());
            pstmt.setString(7, wardrobe.getSize());
            pstmt.setString(8, wardrobe.getColor());
            pstmt.setString(9, wardrobe.getBrand());
            pstmt.setString(10, wardrobe.getDeleted());
            pstmt.setLong(11, wardrobe.getId());
            pstmt.setLong(12, wardrobe.getMemberId());

            System.out.println("SQL 실행: " + sql);
            int affectedRows = pstmt.executeUpdate();
            System.out.println("업데이트된 행 수: " + affectedRows);

            if (affectedRows == 0) {
                System.out.println("WARNING: 업데이트된 행이 없습니다. ID나 member_id가 일치하지 않을 수 있습니다.");
                throw new RuntimeException("업데이트할 데이터가 없습니다. 옷 ID: " + wardrobe.getId());
            }

        } catch (SQLException e) {
            System.out.println("ERROR: SQL 실행 실패 - " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Failed to update wardrobe", e);
        }

        System.out.println("=== DAO update 메서드 완료 ===");
    }

    // 특정 회원의 옷 삭제
    @Override
    public void delete(Long id, Long memberId) {
        String sql = """
    UPDATE clothes 
    SET deleted = 'Y' 
    WHERE id = ? AND member_id = ?
    """;

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setLong(1, id);
            pstmt.setLong(2, memberId);

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows == 0) {
                throw new RuntimeException("삭제할 데이터가 없습니다. 옷 ID: " + id);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to delete wardrobe");
        }
    }

    // 즐겨찾기만 업데이트하는 메서드 추가
    public void updateFavoriteStatus(Long id, Long memberId, String liked) {
        System.out.println("=== 즐겨찾기 상태만 업데이트 ===");
        System.out.println("옷 ID: " + id + ", 회원 ID: " + memberId + ", 즐겨찾기: " + liked);

        String sql = """
    UPDATE clothes 
    SET liked = ? 
    WHERE id = ? AND member_id = ? AND deleted = 'N'
    """;

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, liked);
            pstmt.setLong(2, id);
            pstmt.setLong(3, memberId);

            int affectedRows = pstmt.executeUpdate();
            System.out.println("즐겨찾기 업데이트된 행 수: " + affectedRows);

            if (affectedRows == 0) {
                throw new RuntimeException("즐겨찾기 업데이트할 데이터가 없습니다. 옷 ID: " + id);
            }

        } catch (SQLException e) {
            System.out.println("ERROR: 즐겨찾기 업데이트 실패 - " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Failed to update favorite status", e);
        }
    }
}