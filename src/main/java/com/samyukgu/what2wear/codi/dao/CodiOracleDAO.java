package com.samyukgu.what2wear.codi.dao;

import com.samyukgu.what2wear.codi.model.Codi;
import com.samyukgu.what2wear.codi.model.CodiScope;
import com.samyukgu.what2wear.member.model.Member;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class CodiOracleDAO implements CodiDAO {
    private static String url;
    private static String dbUser;
    private static String dbPassword;

    // 초기 생성 시 properties 읽어오기
    public CodiOracleDAO() {
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
    public List<Codi> findMonthlyCodiSchedules(String memberId, LocalDate date) {
        String sql = """
        SELECT *
        FROM codi
        WHERE member_id = ?
        AND TO_CHAR(schedule_date, 'YYYYMM') = ?
        AND deleted = 'N'
        ORDER BY schedule_date
        """;

        List<Codi> codis = new ArrayList<>();
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)
        ) {
            pstmt.setLong(1, Long.parseLong(memberId));
            pstmt.setString(2, date.format(java.time.format.DateTimeFormatter.ofPattern("yyyyMM")));

            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                codis.add(mapResultSetToCodi(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error in findMonthlyCodiSchedules", e);
        }
        return codis;
    }

//    private Clothes mapToClothes(ResultSet rs) throws SQLException {
//        return new Clothes(
//                rs.getString("category_name"),
//                rs.getString("name"),
//                rs.getBytes("picture")
//        );
//    }

    @Override
    public Codi findCodiScheduleDetail(String memberId, String codiId) {
        String sql = """
        SELECT *
        FROM codi
        WHERE id = ?
          AND member_id = ?
          AND deleted = 'N'
        """;

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)
        ) {
            pstmt.setLong(1, Long.parseLong(codiId));
            pstmt.setLong(2, Long.parseLong(memberId));
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                Codi codi = mapResultSetToCodi(rs);

                String clothesSql = """
                    SELECT c.name, c.picture, cat.name AS category_name
                    FROM clothes c
                    JOIN category cat ON c.category_id = cat.id
                    JOIN codi_detail cd ON cd.clothes_id = c.id
                    WHERE cd.codi_id = ?
                """;

//                try (PreparedStatement clothesStmt = conn.prepareStatement(clothesSql)) {
//                    clothesStmt.setLong(1, codi.getId());
//                    ResultSet clothesRs = clothesStmt.executeQuery();
//                    List<Clothes> clothesList = new ArrayList<>();
//                    while (clothesRs.next()) {
//                        clothesList.add(mapToClothes(clothesRs));
//                    }
//                    codi.setClothes(clothesList);
//                }

                // 코디에 연결된 옷 ID들 추가 조회
                String detailSql = "SELECT clothes_id FROM codi_detail WHERE codi_id = ?";
                try (PreparedStatement detailStmt = conn.prepareStatement(detailSql)) {
                    detailStmt.setLong(1, codi.getId());
                    ResultSet detailRs = detailStmt.executeQuery();

                    List<Long> clothingIds = new ArrayList<>();
                    while (detailRs.next()) {
                        clothingIds.add(detailRs.getLong("clothes_id"));
                    }
//                    codi.setClothingIds(clothingIds);
                }

                return codi;
            }

            return null;

        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error in findCodiScheduleDetail", e);
        }
    }


    @Override
    public void createCodiSchedule(String memberId, String scheduleName, LocalDate date,
                                   List<Number> clothingIds, CodiScope scope) {
        String insertCodiSql = """
        INSERT INTO codi(id, member_id, schedule, schedule_date, scope, codi_type, deleted)
        VALUES (SEQ_CODI.NEXTVAL, ?, ?, ?, ?, 'S', 'N')
        """;

        String insertDetailSql = """
        INSERT INTO codi_detail(codi_id, clothes_id) VALUES (?, ?)
        """;

        try (Connection conn = getConnection()) {
            conn.setAutoCommit(false); // 트랜잭션 시작

            Long generatedCodiId = null;

            // 코디 메인 삽입
            try (PreparedStatement pstmt = conn.prepareStatement(insertCodiSql, new String[] {"id"})) {
                pstmt.setLong(1, Long.parseLong(memberId));
                pstmt.setString(2, scheduleName);
                pstmt.setDate(3, java.sql.Date.valueOf(date));
                pstmt.setInt(4, scope.getValue());

                pstmt.executeUpdate();

                // 생성된 코디 ID 가져오기
                try (ResultSet rs = pstmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        generatedCodiId = rs.getLong(1);
                    } else {
                        throw new SQLException("Codi ID 생성 실패");
                    }
                }
            }

            // 코디에 연결된 옷들 삽입
            try (PreparedStatement pstmt = conn.prepareStatement(insertDetailSql)) {
                for (Number clothesId : clothingIds) {
                    pstmt.setLong(1, generatedCodiId);
                    pstmt.setLong(2, clothesId.longValue());
                    pstmt.addBatch();
                }
                pstmt.executeBatch();
            }

            conn.commit(); // 성공 시 커밋

        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error in createCodiSchedule", e);
        }
    }


    @Override
    public void updateSchedule(String memberId, String codiId, String scheduleName,
                               LocalDate date, List<Number> clothingIds, CodiScope scope) {

        String updateCodiSql = """
        UPDATE codi
        SET schedule = ?, schedule_date = ?, scope = ?
        WHERE id = ? AND member_id = ? AND deleted = 'N'
        """;

        String deleteDetailSql = """
        DELETE FROM codi_detail WHERE codi_id = ?
        """;

        String insertDetailSql = """
        INSERT INTO codi_detail(codi_id, clothes_id) VALUES (?, ?)
        """;

        try (Connection conn = getConnection()) {
            conn.setAutoCommit(false);

            // 1. 일정 정보 업데이트
            try (PreparedStatement pstmt = conn.prepareStatement(updateCodiSql)) {
                pstmt.setString(1, scheduleName);
                pstmt.setDate(2, java.sql.Date.valueOf(date));
                pstmt.setInt(3, scope.getValue());
                pstmt.setLong(4, Long.parseLong(codiId));
                pstmt.setLong(5, Long.parseLong(memberId));
                pstmt.executeUpdate();
            }

            // 2. 기존 옷 연결 정보 삭제
            try (PreparedStatement pstmt = conn.prepareStatement(deleteDetailSql)) {
                pstmt.setLong(1, Long.parseLong(codiId));
                pstmt.executeUpdate();
            }

            // 3. 새로운 옷 목록 삽입
            try (PreparedStatement pstmt = conn.prepareStatement(insertDetailSql)) {
                for (Number clothesId : clothingIds) {
                    pstmt.setLong(1, Long.parseLong(codiId));
                    pstmt.setLong(2, clothesId.longValue());
                    pstmt.addBatch();
                }
                pstmt.executeBatch();
            }

            conn.commit();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error in updateSchedule", e);
        }
    }


    @Override
    public void deleteSchedule(String memberId, String codiId) {
        String sql = """
        UPDATE codi
        SET deleted = 'Y'
        WHERE id = ? AND member_id = ?
        """;

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)
        ) {
            pstmt.setLong(1, Long.parseLong(codiId));
            pstmt.setLong(2, Long.parseLong(memberId));
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error in deleteSchedule", e);
        }
    }


    @Override
    public List<Codi> findAll(String memberId) {
        String sql = """
        SELECT *
        FROM codi
        WHERE member_id = ? AND deleted = 'N'
        ORDER BY schedule_date DESC
        """;

        List<Codi> codis = new ArrayList<>();

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)
        ) {
            pstmt.setLong(1, Long.parseLong(memberId));
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                codis.add(mapResultSetToCodi(rs));
            }

        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error in findAll", e);
        }

        return codis;
    }


    @Override
    public List<Codi> findByName(String memberId, String keyword) {
        String sql = """
        SELECT *
        FROM codi
        WHERE member_id = ?
          AND deleted = 'N'
          AND (LOWER(name) LIKE ? OR LOWER(schedule) LIKE ?)
        ORDER BY schedule_date DESC
        """;

        List<Codi> codis = new ArrayList<>();

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)
        ) {
            pstmt.setLong(1, Long.parseLong(memberId));
            String likeKeyword = "%" + keyword.toLowerCase() + "%";
            pstmt.setString(2, likeKeyword);
            pstmt.setString(3, likeKeyword);

            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                codis.add(mapResultSetToCodi(rs));
            }

        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error in findByName", e);
        }

        return codis;
    }


    // --------------------------------------------------

    private Codi mapResultSetToCodi(ResultSet rs) throws SQLException {
        Codi codi = new Codi();
        codi.setId(rs.getLong("id"));
        codi.setName(rs.getString("name"));
        codi.setSchedule(rs.getString("schedule"));
        codi.setScheduleDate(rs.getDate("schedule_date").toLocalDate());
        codi.setScope(CodiScope.fromInt(rs.getInt("scope"))); // enum 변환 필요
        codi.setWeather(rs.getString("weather"));
        codi.setPicture(rs.getBytes("picture"));
        return codi;
    }
}
