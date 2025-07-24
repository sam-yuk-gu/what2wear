package com.samyukgu.what2wear.notification.dao;

import com.samyukgu.what2wear.member.model.Member;
import com.samyukgu.what2wear.notification.Model.Notification;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class NotificationOracleDAO implements NotificationDAO{
    private static String url;
    private static String dbUser;
    private static String dbPassword;

    // 초기 생성 시 properties 읽어오기
    public NotificationOracleDAO() {
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

    // DB와 연결
    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url, dbUser, dbPassword);
    }


    @Override
    public List<Notification> findAllByReceiverIdOrderByDesc(Long receiverId) {
        String sql = """
            SELECT id, receiver_id, sender_id FROM notification
            WHERE receiver_id = ?
            ORDER BY id DESC
            """;

        List<Notification> notifications = new ArrayList<>();

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)
        ) {
            pstmt.setLong(1, receiverId);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Notification notification = new Notification();
                    notification.setId(rs.getLong("id"));
                    notification.setReceiverId(rs.getLong("receiver_id"));
                    notification.setSenderId(rs.getLong("sender_id"));
                    notifications.add(notification);
                }
            }

            return notifications;
        } catch (SQLException e) {
            throw new RuntimeException("Can't retrieve notifications: check receiverId", e);
        }
    }

    // 요청 저장
    @Override
    public void save(Long receiverId, Long senderId) {
        String sql = """
                INSERT INTO notification (id, receiver_id, sender_id)
                VALUES (SEQ_MEMBER.NEXTVAL, ?, ?)
                """;
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)
        ) {
            pstmt.setLong(1,receiverId);
            pstmt.setLong(2,senderId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Can't save notification : Check input receiverId senderId", e);
        }
    }

    @Override
    public void delete(Long receiverId, Long senderId) {
        String sql = """
                DELETE FROM notification
                WHERE receiver_id = ? AND sender_id = ?
        """;
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)
        ) {
            pstmt.setLong(1, receiverId);
            pstmt.setLong(2, senderId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Can't delete friend relationship : check data plz", e);
        }
    }
}
