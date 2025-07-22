package com.samyukgu.what2wear.likePost.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class LikePostOracleDAO implements LikePostDAO {
    private final Connection conn;

    public LikePostOracleDAO(Connection conn) {
        this.conn = conn;
    }

    @Override
    public int countLikesByPostId(int postId) {
        String sql = "SELECT COUNT(*) FROM like_post WHERE post_id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
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
}
