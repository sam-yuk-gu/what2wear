package com.samyukgu.what2wear.postcomment.dao;

import com.samyukgu.what2wear.member.model.Member;
import com.samyukgu.what2wear.post.model.Post;
import com.samyukgu.what2wear.postcomment.model.PostComment;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import static java.sql.DriverManager.getConnection;

public class PostCommentOracleDAO implements PostCommentDAO {
    private static String url;
    private static String dbUser;
    private static String dbPassword;

    // 초기 생성 시 properties 읽어오기
    public PostCommentOracleDAO() {
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

    @Override
    public List<Post> findAll() {
        return List.of();
    }

    @Override
    public void create(PostComment comment) {
        String sql = """
                INSERT INTO post_comment (id, post_id, member_id, content, created_at)
                VALUES (SEQ_POST_COMMENT.NEXTVAL, ?, ?, ?, ?)
                """;
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, comment.getPostId());
            pstmt.setLong(2, comment.getMemberId());
            pstmt.setString(3, comment.getContent());
            pstmt.setTimestamp(4, new Timestamp(comment.getCreatedAt().getTime()));
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url, dbUser, dbPassword);
    }

    @Override
    public void update(PostComment postComment) {

    }

    @Override
    public void delete(Long id) {

    }
}