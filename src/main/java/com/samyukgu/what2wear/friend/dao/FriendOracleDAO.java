package com.samyukgu.what2wear.friend.dao;

import com.samyukgu.what2wear.member.model.Member;
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

public class FriendOracleDAO implements FriendDAO{
    private static String url;
    private static String dbUser;
    private static String dbPassword;

    public FriendOracleDAO() {
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
    
    // 친구 요청
    @Override
    public void save(Long member1Id, Long member2Id) {
        String sql = "{call add_friend_relationship(?, ?, ?)}";

        try (Connection conn = getConnection();
             CallableStatement stmt = conn.prepareCall(sql)
        ) {
            stmt.setLong(1, member1Id);
            stmt.setLong(2, member2Id);
            stmt.setString(3, "N"); // 대기 상태로 요청
            stmt.execute();
        } catch (SQLException e) {
            throw new RuntimeException("Can't request friend : check data plz", e);
        }
    }
    
    // 친구 요청 수락
    @Override
    public void acceptRequest(Long member1Id, Long member2Id) {
        String sql = "{call accept_friend_request(?, ?)}";

        try (Connection conn = getConnection();
             CallableStatement stmt = conn.prepareCall(sql)
        ) {
            stmt.setLong(1, member1Id);
            stmt.setLong(2, member2Id);
            stmt.execute();
        } catch (SQLException e) {
            throw new RuntimeException("Can't accept friend request : check data plz", e);
        }
    }
    
    // 친구 요청 삭제
    @Override
    public void rejectRequest(Long member1Id, Long member2Id) {
        String sql = "{call reject_friend_request(?, ?)}";

        try (Connection conn = getConnection();
             CallableStatement stmt = conn.prepareCall(sql)
        ) {
            stmt.setLong(1, member1Id);
            stmt.setLong(2, member2Id);
            stmt.execute();
        } catch (SQLException e) {
            throw new RuntimeException("Can't reject friend request : check data plz", e);
        }
    }
    
    // 친구 삭제
    @Override
    public void delete(Long member1Id, Long member2Id) {
        String sql = "{call delete_friend_relationship(?, ?)}";
        try (Connection conn = getConnection();
             CallableStatement stmt = conn.prepareCall(sql)
        ) {
            stmt.setLong(1, member1Id);
            stmt.setLong(2, member2Id);
            stmt.execute();
        } catch (SQLException e) {
            throw new RuntimeException("Can't delete friend relationship : check data plz", e);
        }
    }

    // 모든 친구 불러오기
    @Override
    public List<Member> findFriendsAll(Long memberId) {
        String sql = """
            SELECT f.member2_id as friend_id,
                   m.nickname,
                   m.email,
                   m.profile_img
            FROM friend f
            JOIN member m ON f.member2_id = m.id
            WHERE f.member1_id = ? AND f.status = 'Y'
            """;

        List<Member> members = new ArrayList<>();

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)

        ) {
            pstmt.setLong(1, memberId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                members.add(
                        mapResultSetToMember(rs)
                );
            }
            return members;
        } catch (SQLException e) {
            throw new RuntimeException("Can't list up friends : check data plz", e);
        }
    }

    // 친구 요청중인 사용자 불러오기
    @Override
    public List<Member> findPendingFriendRequests(Long memberId) {
        String sql = """
            SELECT f.member2_id as friend_id,
                   m.nickname,
                   m.email,
                   m.profile_img
            FROM friend f
            JOIN member m ON f.member2_id = m.id
            WHERE f.member1_id = ? AND f.status = 'N'
            """;

        List<Member> members = new ArrayList<>();

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)

        ) {
            pstmt.setLong(1, memberId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                members.add(
                        mapResultSetToMember(rs)
                );
            }
            return members;
        } catch (SQLException e) {
            throw new RuntimeException("Can't list up friends : check data plz", e);
        }
    }

    // 단일 사용자에 대해서 친구인지 확인
    @Override
    public boolean isFriend(Long member1Id, Long member2Id) {
        String sql = """
            SELECT 1
            FROM friend f
            WHERE f.member1_id = ? AND member2_id = ? AND f.status = 'Y'
            """;

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)

        ) {
            pstmt.setLong(1, member1Id);
            pstmt.setLong(2, member2Id);
            ResultSet rs = pstmt.executeQuery();

            if(rs.next())
                return true;
            return false;
        } catch (SQLException e) {
            throw new RuntimeException("Can't check relationship of friends : check data plz", e);
        }
    }

    // 단일 사용자가 친구 요청중인지 확인
    @Override
    public boolean isRequestPending(Long member1Id, Long member2Id) {
        String sql = """
            SELECT 1
            FROM friend f
            WHERE f.member1_id = ? AND member2_id = ? AND f.status = 'N'
            """;

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)

        ) {
            pstmt.setLong(1, member1Id);
            pstmt.setLong(2, member2Id);
            ResultSet rs = pstmt.executeQuery();

            if(rs.next())
                return true;
            return false;
        } catch (SQLException e) {
            throw new RuntimeException("Can't check relationship of friends : check data plz", e);
        }
    }

    // 반복되는 rs to Member 메서드 모듈화 -> (ResultSet 객체 -> Member 반환)
    private Member mapResultSetToMember(ResultSet rs) throws SQLException {
        Member member = new Member();
        member.setId(rs.getLong("friend_id"));
        member.setNickname(rs.getString("nickname"));
        member.setEmail(rs.getString("email"));
        member.setProfile_img(rs.getBytes("profile_img"));
        return member;
    }
}
