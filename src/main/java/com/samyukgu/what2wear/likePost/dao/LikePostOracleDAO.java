package com.samyukgu.what2wear.likePost.dao;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.*;
import java.util.Properties;

// 작성자 : 오수경
public class LikePostOracleDAO implements LikePostDAO {
    private static String url;
    private static String dbUser;
    private static String dbPassword;

    public LikePostOracleDAO() {
        Properties props = new Properties();
        try (InputStream is = ClassLoader.getSystemResourceAsStream("application.properties");
             InputStreamReader isr = new InputStreamReader(is, StandardCharsets.UTF_8)) {
            props.load(isr);
            url = props.getProperty("db.url");
            dbUser = props.getProperty("db.user");
            dbPassword = props.getProperty("db.password");

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("check your db info");
        }
    }

    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url, dbUser, dbPassword);
    }

    @Override
    public int countLikesByPostId(int postId) {
        String sql = "SELECT COUNT(*) FROM like_post WHERE post_id = ?";
        try (Connection conn  = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)
        ) {
            pstmt.setInt(1, postId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error By Select LikePost");
        }
        return 0;
    }

    public boolean isAlreadyLiked(Long postId, Long memberId) {
        String sql = "SELECT COUNT(*) FROM like_post WHERE post_id = ? AND member_id = ?";
        try (Connection conn  = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)
        ) {
            pstmt.setLong(1, postId);
            pstmt.setLong(2, memberId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) return rs.getInt(1) > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public void like(Long postId, Long memberId) {
        String sql = "INSERT INTO like_post (id, post_id, member_id, created_at) VALUES (SEQ_LIKE_POST.NEXTVAL, ?, ?, SYSDATE)";
        try (Connection conn  = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)
        ) {
            pstmt.setLong(1, postId);
            pstmt.setLong(2, memberId);
            pstmt.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void unlike(Long postId, Long memberId) {
        String sql = "DELETE FROM like_post WHERE post_id = ? AND member_id = ?";
        try (Connection conn  = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)
        ) {
            pstmt.setLong(1, postId);
            pstmt.setLong(2, memberId);
            pstmt.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
