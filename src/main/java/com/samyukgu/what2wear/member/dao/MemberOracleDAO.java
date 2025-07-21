package com.samyukgu.what2wear.member.dao;

import com.samyukgu.what2wear.member.model.Member;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class MemberOracleDAO implements MemberDAO {
    private static String url;
    private static String dbUser;
    private static String dbPassword;

    public MemberOracleDAO() {
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

    public Member findById(Long id){
        String sql = """
            SELECT *
            FROM member
            WHERE id = ?
            """;
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)
        ) {

            pstmt.setLong(1, id);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return new Member(
                        rs.getLong("id"),
                        rs.getString("account_id"),
                        rs.getString("email"),
                        rs.getString("name"),
                        rs.getString("password"),
                        rs.getBytes("profile_img"),
                        rs.getString("deleted"),
                        rs.getLong("count")
                );
            }
            return null;
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error By Select User");
        }
    }

    public Member findByAccountId(String accountId){
        String sql = """
            SELECT *
            FROM member
            WHERE account_id = ?
            """;
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)
        ) {

            pstmt.setString(1, accountId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return new Member(
                        rs.getLong("id"),
                        rs.getString("account_id"),
                        rs.getString("email"),
                        rs.getString("name"),
                        rs.getString("password"),
                        rs.getBytes("profile_img"),
                        rs.getString("deleted"),
                        rs.getLong("count")
                );
            }
            return null;
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error By Select User");
        }
    }

    public List<Member> findAll(){
        String sql = """
            SELECT *
            FROM member
            """;

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)
        ) {
            ResultSet rs = pstmt.executeQuery();
            List<Member> members = new ArrayList<>();
            while (rs.next()) {
                members.add(
                        new Member(
                                rs.getLong("id"),
                                rs.getString("account_id"),
                                rs.getString("email"),
                                rs.getString("name"),
                                rs.getString("password"),
                                rs.getBytes("profile_img"),
                                rs.getString("deleted"),
                                rs.getLong("count")
                        )
                );
            }

            return members;
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error By Select User");
        }
    }
}
