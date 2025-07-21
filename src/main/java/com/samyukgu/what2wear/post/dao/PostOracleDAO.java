package com.samyukgu.what2wear.post.dao;

import com.samyukgu.what2wear.post.model.Post;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class PostOracleDAO implements PostDAO {
    private static String url;
    private static String dbUser;
    private static String dbPassword;

    public PostOracleDAO() {
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
    public Post findById(Long id) {
        String sql = """
                SELECT *
                FROM post
                WHERE id = ?
                """;
        try (Connection conn  = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)
        ) {
            pstmt.setLong(1, id);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return new Post (
                        rs.getLong("id"),
                        rs.getLong("member_id"),
                        rs.getLong("cody_id"),
                        rs.getString("title"),
                        rs.getString("content"),
                        rs.getDate("create_at"),
                        rs.getDate("last_updated"),
                        rs.getInt("like_count"),
                        rs.getString("writer_name"));
            }
            return null;
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error By Select Post");
        }
    }

    @Override
    public List<Post> findAll() {
        String sql = """
            SELECT p.*, m.name AS writer_name
            FROM post p
            LEFT OUTER JOIN member m ON p.member_id = m.id
            ORDER BY p.id DESC
        """;

        try (Connection conn  = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)
        ) {
            ResultSet rs = pstmt.executeQuery();
            List<Post> posts = new ArrayList<>();

            while (rs.next()) {
                posts.add (
                        new Post (
                        rs.getLong("id"),
                        rs.getLong("member_id"),
                        rs.getLong("cody_id"),
                        rs.getString("title"),
                        rs.getString("content"),
                        rs.getDate("create_at"),
                        rs.getDate("last_updated"),
                                rs.getInt("like_count"),
                                rs.getString("writer_name")

                        )
                );
            }
            return posts;
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error By Select Post");
        }
    }

    @Override
    public void create(Post post) {
        String sql = """
                    INSERT INTO post (id, member_id, cody_id, title, content, create_at, last_updated, like_count)
                    VALUES (?, ?, ?, ?, ?, ?, ?, ?)
                """;

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setLong(1, post.getId());
            pstmt.setLong(2, post.getMember_id());
            pstmt.setLong(3, post.getCody_id());
            pstmt.setString(4, post.getTitle());
            pstmt.setString(5, post.getContent());
            pstmt.setDate(6, new java.sql.Date(post.getCreate_at().getTime()));
            pstmt.setDate(7, new java.sql.Date(post.getLast_updated().getTime()));
            pstmt.setInt(8, post.getLike_count());

            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error By Save Post");
        }
    }

    @Override
    public void update(Post post) {
        String sql = """
                    UPDATE post SET
                        member_id = ?, cody_id = ?, title = ?, content = ?, create_at = ?, last_updated = ?, like_count = ?
                    WHERE id = ?
                """;

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setLong(1, post.getMember_id());
            pstmt.setLong(2, post.getCody_id());
            pstmt.setString(3, post.getTitle());
            pstmt.setString(4, post.getContent());
            pstmt.setDate(5, new java.sql.Date(post.getLast_updated().getTime()));
            pstmt.setLong(7, post.getId());

            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error By Update Post");
        }
    }

    @Override
    public void delete(Long id) {
        String sql = """
                    DELETE FROM post WHERE id = ?
                """;

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setLong(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error By Delete Post");
        }
    }
}