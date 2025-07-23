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

    // 초기 생성 시 properties 읽어오기
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

    // DB와 연결
    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url, dbUser, dbPassword);
    }

    // 회원 저장
    @Override
    public void save(Member member) {
        String sql = """
                INSERT INTO member(id, account_id, email, nickname, name, password, count, deleted)
                VALUES(SEQ_MEMBER.NEXTVAL, ?, ?, ?, ?, ?, 0, 'N')
                """;
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)
        ) {
            pstmt.setString(1, member.getAccount_id());
            pstmt.setString(2, member.getEmail());
            pstmt.setString(3, member.getNickname());
            pstmt.setString(4, member.getName());
            pstmt.setString(5, member.getPassword());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Can't Sign up : Check member's input field", e);
        }
    }

    // 회원 단일 조회 (id)
    @Override
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
                return mapResultSetToMember(rs);
            }
            return null;
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error By findById method", e);
        }
    }

    // 회원 단일 조회 (account_id)
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
                return mapResultSetToMember(rs);
            }
            return null;
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error By findByAccountId method", e);
        }
    }

    // 회원 단일 조회 (account_id, password)
    @Override
    public Member findByAccountIdAndEmail(String accountId, String email) {
        String sql = """
                SELECT * 
                FROM member 
                WHERE account_id = ? and email = ? 
                """;
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)
        ) {

            pstmt.setString(1, accountId);
            pstmt.setString(2, email);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return mapResultSetToMember(rs);
            }
            return null;
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error By findByAccountIdAndEmail method", e);
        }
    }

    // 회원 단일 조회 (name, email) 아이디 찾기에 사용
    @Override
    public Member findByNameAndEmail(String name, String email) {
        String sql = """
                SELECT * 
                FROM member 
                WHERE name = ? and email = ? 
                """;
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)
        ) {

            pstmt.setString(1, name);
            pstmt.setString(2, email);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return mapResultSetToMember(rs);
            }
            return null;
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error By findByNameAndEmail method", e);
        }
    }

    // 회원 단일 조회 (nickname)
    @Override
    public Member findByNickname(String nickname) {
        String sql = """
                SELECT * 
                FROM member 
                WHERE nickname = ?
                """;
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)
        ) {

            pstmt.setString(1, nickname);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return mapResultSetToMember(rs);
            }
            return null;
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error By findByNickname method", e);
        }
    }

    // 모든 회원 조회
    @Override
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
                        mapResultSetToMember(rs)
                );
            }

            return members;
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error By Select User");
        }
    }

    // 회원 단일 조회 (account_id, password) -> 로그인에 사용
    @Override
    public Member findByAccountIdAndPassword(String accountId, String password) {
        String sql = """
                SELECT * 
                FROM member 
                WHERE account_id = ? and password = ? and deleted = 'N'
                """;
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)
        ) {

            pstmt.setString(1, accountId);
            pstmt.setString(2, password);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return mapResultSetToMember(rs);
            }
            return null;
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error By findByAccountIdAndEmail method", e);
        }
    }

    // 회원 비밀번호 업데이트
    @Override
    public Member updatePasswordByAccountId(String accountId, String newPassword) {
        String sql = """
        UPDATE member
        SET password = ?
        WHERE account_id = ?
        """;

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, newPassword);
            pstmt.setString(2, accountId);

            int updated = pstmt.executeUpdate();
            if (updated == 0) {
                // 해당 accountId가 없었거나 이미 비밀번호가 같아서 변경 없음
                return null;
            }
            // 업데이트 성공했으면, 변경된 회원 정보 재조회
            return findByAccountId(accountId);

        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error updating password for accountId=" + accountId, e);
        }
    }

    @Override
    public List<Member> findByNicknameContaining(Long memberId, String keyword) {
        String sql = """
            SELECT *
            FROM member
            WHERE id != ? AND nickname LIKE ?
            """;

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)
        ) {
            pstmt.setLong(1, memberId);
            pstmt.setString(2, "%" + keyword + "%");
            ResultSet rs = pstmt.executeQuery();
            List<Member> members = new ArrayList<>();
            while (rs.next()) {
                members.add(
                        mapResultSetToMember(rs)
                );
            }
            return members;
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error By Search User");
        }
    }

    @Override
    public Member updateNicknameById(Long memberId, String nickname) {
        String sql = """
        UPDATE member
        SET nickname = ?
        WHERE id = ?
        """;

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, nickname);
            pstmt.setLong(2, memberId);

            int updated = pstmt.executeUpdate();
            if (updated == 0) {
                // 해당 memberId가 없었거나 이미 닉네임이 같아서 변경 없음
                return null;
            }
            // 업데이트 성공했으면, 변경된 회원 정보 재조회
            return findById(memberId);

        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error updating nickname for memberId=" + memberId, e);
        }
    }

    @Override
    public Member updateNameById(Long memberId, String name) {
        String sql = """
        UPDATE member
        SET name = ?
        WHERE id = ?
        """;

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, name);
            pstmt.setLong(2, memberId);

            int updated = pstmt.executeUpdate();
            if (updated == 0) {
                // 해당 memberId가 없었거나 이미 이름이 같아서 변경 없음
                return null;
            }
            // 업데이트 성공했으면, 변경된 회원 정보 재조회
            return findById(memberId);

        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error updating nickname for memberId=" + memberId, e);
        }
    }

    @Override
    public Member updateEmailById(Long memberId, String email) {
        String sql = """
        UPDATE member
        SET email = ?
        WHERE id = ?
        """;

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, email);
            pstmt.setLong(2, memberId);

            int updated = pstmt.executeUpdate();
            if (updated == 0) {
                // 해당 memberId가 없었거나 이미 이메일이 같아서 변경 없음
                return null;
            }
            // 업데이트 성공했으면, 변경된 회원 정보 재조회
            return findById(memberId);

        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error updating email for memberId=" + memberId, e);
        }
    }

    @Override
    public Member updatePasswordById(Long memberId, String password) {
        String sql = """
        UPDATE member
        SET password = ?
        WHERE id = ?
        """;

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, password);
            pstmt.setLong(2, memberId);

            System.out.println(findById(memberId));

            int updated = pstmt.executeUpdate();
            if (updated == 0) {
                // 해당 memberId가 없었거나 이미 비밀번호가 같아서 변경 없음
                return null;
            }
            // 업데이트 성공했으면, 변경된 회원 정보 재조회
            return findById(memberId);

        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error updating password for memberId=" + memberId, e);
        }
    }

    @Override
    public Member updateProfileImgById(Long memberId, byte[] profileImg) {
        String sql = """
        UPDATE member
        SET profile_img = ?
        WHERE id = ?
        """;

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setBytes(1, profileImg);
            pstmt.setLong(2, memberId);

            int updated = pstmt.executeUpdate();
            if (updated == 0) {
                // 해당 memberId가 없었거나 이미 이메일이 같아서 변경 없음
                return null;
            }
            // 업데이트 성공했으면, 변경된 회원 정보 재조회
            return findById(memberId);

        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error updating profileImg for memberId=" + memberId, e);
        }
    }

    // DELETED 열을 Y로 변경
    @Override
    public Member updateDeletedById(Long memberId) {
        String sql = """
        UPDATE member
        SET deleted = 'Y'
        WHERE id = ?
        """;

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, memberId);

            int updated = pstmt.executeUpdate();
            if (updated == 0) {
                return null;
            }
            // 업데이트 성공했으면, 변경된 회원 정보 재조회
            return findById(memberId);

        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error updating DELETED for memberId=" + memberId, e);
        }
    }

    // 반복되는 rs to Member 메서드 모듈화 -> (ResultSet 객체 -> Member 반환)
    private Member mapResultSetToMember(ResultSet rs) throws SQLException {
        return new Member(
                rs.getLong("id"),
                rs.getString("account_id"),
                rs.getString("email"),
                rs.getString("name"),
                rs.getString("nickname"),
                rs.getString("password"),
                rs.getBytes("profile_img"),
                rs.getString("deleted"),
                rs.getLong("count")
        );
    }
}
