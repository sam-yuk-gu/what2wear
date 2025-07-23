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

    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url, dbUser, dbPassword);
    }

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
    public List<PostComment> findByPostId(Long postId) {
        String sql = "SELECT * FROM post_comment WHERE post_id = ? ORDER BY created_at ASC";
        List<PostComment> comments = new ArrayList<>();

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setLong(1, postId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    PostComment comment = new PostComment(
                            rs.getLong("id"),
                            rs.getLong("post_id"),
                            rs.getLong("member_id"),
                            rs.getString("content"),
                            rs.getTimestamp("created_at")
                    );
                    comments.add(comment);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return comments;
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

    @Override
    public void update(PostComment postComment) {
        String sql = "UPDATE post_comment SET content = ? WHERE id = ?";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, postComment.getContent());
            pstmt.setLong(2, postComment.getId());
            pstmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void delete(Long id) {
        String sql = """
                DELETE FROM post_comment WHERE id = ?
            """;

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setLong(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error By Delete Post Comment");
        }
    }
}