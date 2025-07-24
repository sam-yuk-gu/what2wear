package com.samyukgu.what2wear.codi.dao;

import com.samyukgu.what2wear.codi.dto.CodiClothesDTO;
import com.samyukgu.what2wear.codi.dto.CodiDTO;
import com.samyukgu.what2wear.codi.dto.CodiListDTO;
import com.samyukgu.what2wear.codi.model.Codi;
import com.samyukgu.what2wear.codi.model.CodiSchedule;
import com.samyukgu.what2wear.codi.model.CodiScope;
import com.samyukgu.what2wear.wardrobe.model.Wardrobe;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

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
    public List<CodiSchedule> findMonthlyCodiSchedules(Long memberId, LocalDate date) {
        String curYear = date.format(DateTimeFormatter.ofPattern("yyyy-MM"));
        String nextMonth = date.plusMonths(1).format(DateTimeFormatter.ofPattern("yyyy-MM"));

        String sql = """
            SELECT *
            FROM codi
            WHERE member_id = ?
              AND schedule_date >= TO_DATE(?, 'YYYY-MM')
              AND schedule_date <  TO_DATE(?, 'YYYY-MM')
            ORDER BY schedule_date, created_at
        """;

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)
        ) {
            pstmt.setLong(1, memberId);
            pstmt.setString(2, curYear);
            pstmt.setString(3, nextMonth);

            List<CodiSchedule> codiSchedules = new ArrayList<>();

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    CodiScope visibility = CodiScope.fromInt(rs.getInt("scope")); // 또는 rs.getInt("visibility") 등

                    codiSchedules.add(new CodiSchedule(
                            rs.getLong("id"),
                            rs.getDate("schedule_date").toLocalDate(),
                            visibility
                    ));
                }
            }
            return codiSchedules;
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error in findMonthlyCodiSchedules", e);
        }
    }

    @Override
    public List<CodiListDTO> findCodiList(Long memberId, LocalDate date) {
        String fromDate = date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        String toDate = date.plusDays(1).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

        String sql = """
        SELECT
             c.id,
             c.schedule_date,
             c.scope,
             c.schedule AS schedule_name,
             cat.name AS category_name,
             cl.name AS clothes_name,
             cl.picture AS clothes_picture
        FROM codi c
            LEFT JOIN codi_detail cd ON c.id = cd.codi_id
            LEFT JOIN clothes cl ON cd.clothes_id = cl.id
            LEFT JOIN category cat ON cl.category_id = cat.id
        WHERE c.member_id = ?
            AND c.schedule_date >= TO_DATE(?, 'YYYY-MM-DD')
            AND c.schedule_date < TO_DATE(?, 'YYYY-MM-DD')
            AND c.deleted = 'N'
            AND (c.schedule IS NOT NULL OR cl.id IS NOT NULL)
        ORDER BY c.schedule_date, c.id, cat.id
    """;

        Map<LocalDate, List<CodiDTO>> groupedByDate = new LinkedHashMap<>();
        Map<Long, CodiDTO> codiMap = new LinkedHashMap<>();

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)
        ) {
            pstmt.setLong(1, memberId);
            pstmt.setString(2, fromDate);
            pstmt.setString(3, toDate);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Long codiId = rs.getLong("id");
                    LocalDate scheduleDate = rs.getDate("schedule_date").toLocalDate();

                    CodiDTO codi = codiMap.get(codiId);
                    if (codi == null) {
                        codi = new CodiDTO();
                        codi.setCodiId(codiId);
                        codi.setScope(String.valueOf(rs.getInt("scope")));
                        codi.setScheduleName(rs.getString("schedule_name"));
                        codi.setCodiClothesList(new ArrayList<>());
                        codiMap.put(codiId, codi);

                        groupedByDate.computeIfAbsent(scheduleDate, d -> new ArrayList<>()).add(codi);
                    }

                    String clothesName = rs.getString("clothes_name");
                    byte[] clothesPicture = rs.getBytes("clothes_picture");

                    if (clothesName != null || clothesPicture != null) {
                        CodiClothesDTO clothes = new CodiClothesDTO();
                        clothes.setCategoryName(rs.getString("category_name"));
                        clothes.setClothesName(clothesName);
                        clothes.setClothesPicture(clothesPicture);

                        codi.getCodiClothesList().add(clothes);
                    }
                }
            }

            List<CodiListDTO> result = new ArrayList<>();
            for (Map.Entry<LocalDate, List<CodiDTO>> entry : groupedByDate.entrySet()) {
                CodiListDTO dto = new CodiListDTO();
                dto.setScheduleDate(entry.getKey());
                dto.setCodiList(entry.getValue());
                result.add(dto);
            }
            List<CodiListDTO> testResult = new ArrayList<>();
            for (Map.Entry<LocalDate, List<CodiDTO>> entry : groupedByDate.entrySet()) {
                CodiListDTO dto = new CodiListDTO();
                dto.setScheduleDate(entry.getKey());
                dto.setCodiList(entry.getValue());
                testResult.add(dto);
            }
            return result;

        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error in findCodiList", e);
        }
    }

    @Override
    public Codi findCodiScheduleDetail(String memberId, String codiId) {
        return null;
    }

    @Override
    public void create(Codi codi, Collection<Wardrobe> selectedOutfits) {
        String insertCodiSql  = """
            INSERT INTO codi (
                id, member_id, schedule, schedule_date, scope, codi_type, deleted
            ) VALUES (
                SEQ_CODI.NEXTVAL, ?, ?, ?, ?, ?, 'N'
            )
        """;
        String getIdSql = "SELECT SEQ_CODI.CURRVAL FROM dual";
        String insertDetailSql = "INSERT INTO codi_detail (codi_id, clothes_id) VALUES (?, ?)";

        try (Connection conn = getConnection()) {
            conn.setAutoCommit(false); // 트랜잭션 시작

            Long codiId;
            // 코디 저장 > 코디 옷상세정보 순서대로 처리
            // 1. 코디 INSERT
            try (PreparedStatement pstmt = conn.prepareStatement(insertCodiSql)) {
                pstmt.setLong(1, codi.getMemberId());
                pstmt.setString(2, codi.getSchedule());
                pstmt.setDate(3, java.sql.Date.valueOf(codi.getScheduleDate()));
                pstmt.setLong(4, codi.getScope());
                pstmt.setString(5, codi.getCodiType());

                int result = pstmt.executeUpdate();
                if (result == 0) {
                    conn.rollback();
                    throw new SQLException("코디 일정 등록 실패");
                }
            }

            // 2. 코디 ID 조회
            try (PreparedStatement idStmt = conn.prepareStatement(getIdSql);
                 ResultSet rs = idStmt.executeQuery()) {
                if (rs.next()) {
                    codiId = rs.getLong(1);
                    codi.setId(codiId);
                } else {
                    conn.rollback();
                    throw new SQLException("코디 ID 조회 실패");
                }
            }

            if (selectedOutfits != null && !selectedOutfits.isEmpty()) {
                // 3. 코디옷상세정보 INSERT
                try (PreparedStatement detailStmt = conn.prepareStatement(insertDetailSql)) {
                    for (Wardrobe outfit : selectedOutfits) {
                        detailStmt.setLong(1, codiId);
                        detailStmt.setLong(2, outfit.getId());
                        detailStmt.addBatch();
                    }
                    detailStmt.executeBatch();
                }
            }

            conn.commit(); // 전체 성공 시 커밋
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("코디 생성 중 오류 발생", e);
        }
    }

    public void insertCodiDetail(Long codiId, Long clothesId)  {
        String sql = "INSERT INTO codi_detail (codi_id, clothes_id) VALUES (?, ?)";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setLong(1, codiId);
            pstmt.setLong(2, clothesId);

            pstmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error By Insert Codi Detail", e);
        }
    }

    @Override
    public void createCodiSchedule(String memberId, String scheduleName, LocalDate date, List<Number> clothingIds, CodiScope scope) {
        CodiSchedule codiSchedule = new CodiSchedule();
    }

    @Override
    public void updateSchedule(String memberId, String codiId, String scheduleName, LocalDate date, List<Number> clothingIds, CodiScope scope) {

    }

    @Override
    public void deleteSchedule(String memberId, String codiId) {

    }

    @Override
    public List<Codi> findAll(String memberId) {
        return List.of();
    }

    @Override
    public List<Codi> findByName(String memberId, String keyword) {
        return List.of();
    }

}
