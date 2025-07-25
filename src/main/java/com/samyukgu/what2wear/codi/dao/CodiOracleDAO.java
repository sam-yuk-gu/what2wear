package com.samyukgu.what2wear.codi.dao;

import com.samyukgu.what2wear.codi.dto.CodiClothesDTO;
import com.samyukgu.what2wear.codi.dto.CodiDTO;
import com.samyukgu.what2wear.codi.dto.CodiDetailDTO;
import com.samyukgu.what2wear.codi.dto.CodiListDTO;
import com.samyukgu.what2wear.codi.model.Codi;
import com.samyukgu.what2wear.codi.model.CodiSchedule;
import com.samyukgu.what2wear.codi.model.CodiScope;
import com.samyukgu.what2wear.common.util.CategoryUtil;
import com.samyukgu.what2wear.myCodi.model.CodiDetail;
import com.samyukgu.what2wear.myCodi.model.CodiWithDetails;
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
import java.sql.Date;

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
              AND deleted = 'N'
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

    // 코디 상세 조회
    @Override
    public CodiDetailDTO findCodiScheduleDetail(Long memberId, Long codiId) {
        String sql = """
        SELECT
            c.id,
            c.schedule_date,
            c.scope,
            c.schedule AS schedule_name,
            cl.id AS clothes_id,
            cat.name AS category_name,
            cl.name AS clothes_name,
            cl.picture AS clothes_picture
        FROM codi c
            LEFT JOIN codi_detail cd ON c.id = cd.codi_id
            LEFT JOIN clothes cl ON cd.clothes_id = cl.id
            LEFT JOIN category cat ON cl.category_id = cat.id
        WHERE c.member_id = ?
          AND c.id = ?
          AND c.deleted = 'N'
        ORDER BY c.id DESC, cl.category_id
    """;

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setLong(1, memberId);
            pstmt.setLong(2, codiId);

            try (ResultSet rs = pstmt.executeQuery()) {
                CodiDetailDTO codiInfo = null;
                List<Wardrobe> clothesList = new ArrayList<>();

                while (rs.next()) {
                    if (codiInfo == null) {
                        codiInfo = new CodiDetailDTO();
                        codiInfo.setCodiId(rs.getLong("id"));
                        codiInfo.setScheduleDate(rs.getDate("schedule_date").toLocalDate());
                        codiInfo.setScope(rs.getInt("scope"));
                        codiInfo.setScheduleName(rs.getString("schedule_name"));
                        codiInfo.setClothes(clothesList);
                    }

                    String clothesName = rs.getString("clothes_name");
                    byte[] clothesPicture = rs.getBytes("clothes_picture");
                    Long categoryId = CategoryUtil.getIdByName(rs.getString("category_name"));

                    // 옷 정보가 있다면 DTO 생성
                    Long clothesId = rs.getLong("clothes_id");
                    if (clothesName != null || clothesPicture != null) {
                        Wardrobe clothes = new Wardrobe();
                        clothes.setId(clothesId);
                        clothes.setCategoryId(categoryId);
                        clothes.setName(clothesName);
                        clothes.setPicture(clothesPicture);
                        clothesList.add(clothes);
                    }
                }

                return codiInfo;
            }

        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("코디 상세 조회 실패", e);
        }
    }

    @Override
    public void create(Codi codi, Collection<Wardrobe> selectedOutfits) {
        String insertCodiSql  = """
            INSERT INTO codi (
                id, member_id, schedule, schedule_date, scope, codi_type, deleted
            ) VALUES (
                seq_codi.NEXTVAL, ?, ?, ?, ?, ?, 'N'
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

    @Override
    public void update(Codi codi, Collection<Wardrobe> selectedOutfits) {
        String updateCodiSql = """
        UPDATE codi
        SET schedule = ?, schedule_date = ?, scope = ?
        WHERE id = ? AND member_id = ? AND codi_type = ? AND deleted = 'N'
    """;

        String deleteDetailSql = "DELETE FROM  codi_detail WHERE codi_id = ?";
        String insertDetailSql = "INSERT INTO codi_detail (codi_id, clothes_id) VALUES (?, ?)";

        try (Connection conn = getConnection()) {
            conn.setAutoCommit(false); // 트랜잭션 시작

            // 1. 코디 일정 UPDATE
            try (PreparedStatement updateStmt = conn.prepareStatement(updateCodiSql)) {
                updateStmt.setString(1, codi.getSchedule());
                updateStmt.setDate(2, java.sql.Date.valueOf(codi.getScheduleDate()));
                updateStmt.setLong(3, codi.getScope());
                updateStmt.setLong(4, codi.getId());
                updateStmt.setLong(5, codi.getMemberId());
                updateStmt.setString(6, codi.getCodiType());

                int result = updateStmt.executeUpdate();
                if (result == 0) {
                    conn.rollback();
                    throw new SQLException("CodiOracleDAO: 코디 일정 수정 실패");
                }
            }

            // 2. 기존 코디-옷 관계 삭제
            try (PreparedStatement deleteStmt = conn.prepareStatement(deleteDetailSql)) {
                deleteStmt.setLong(1, codi.getId());
                deleteStmt.executeUpdate();
            }

            // 3. 코디-옷 관계 다시 INSERT
            if (selectedOutfits != null && !selectedOutfits.isEmpty()) {
                try (PreparedStatement insertStmt = conn.prepareStatement(insertDetailSql)) {
                    for (Wardrobe outfit : selectedOutfits) {
                        insertStmt.setLong(1, codi.getId());
                        insertStmt.setLong(2, outfit.getId());
                        insertStmt.addBatch();
                    }
                    insertStmt.executeBatch();
                }
            }

            conn.commit(); // 전체 성공 시 커밋
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("코디 수정 중 오류 발생", e);
        }
    }

    @Override
    public void delete(Long memberId, Long codiId) {
        String deleteDetailsSql = "DELETE FROM codi_detail WHERE codi_id = ?";
        String deleteCodiSql = "UPDATE codi SET deleted = 'Y' WHERE id = ? AND member_id = ?";

        try (Connection conn = getConnection()) {
            conn.setAutoCommit(false); // 트랜잭션 시작

            // 1. codi_detail 삭제
            try (PreparedStatement detailStmt = conn.prepareStatement(deleteDetailsSql)) {
                detailStmt.setLong(1, codiId);
                detailStmt.executeUpdate();
            }

            // 2. codi 논리 삭제
            try (PreparedStatement codiStmt = conn.prepareStatement(deleteCodiSql)) {
                codiStmt.setLong(1, codiId);
                codiStmt.setLong(2, memberId);
                int updated = codiStmt.executeUpdate();

                if (updated == 0) {
                    conn.rollback();
                    throw new SQLException("코디 삭제 실패: 해당 ID가 없거나 권한이 없습니다.");
                }
            }

            conn.commit(); // 전체 성공 시 커밋
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("코디 삭제 중 오류 발생", e);
        }
    }

    @Override
    public List<Codi> findAll(Long memberId) {
        String sql = """
            SELECT *
            FROM codi
            WHERE deleted = 'N'
              AND member_id = ?
        """;

        List<Codi> codis = new ArrayList<>();
        try (Connection conn = getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, memberId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Date sqlDate = rs.getDate("schedule_date");
                    LocalDate scheduleDate = (sqlDate != null) ? sqlDate.toLocalDate() : null;
                    codis.add(new Codi(
                        rs.getLong("id"), // 아이디
                        rs.getLong("member_id"), // 멤버 아이디
                        rs.getString("name"), // 코디명
                        rs.getString("schedule"),   // 스케쥴명
                        scheduleDate, // 날짜
                        rs.getLong("scope"), // 공개범위
                        rs.getString("weather"), // 날씨
                        rs.getBytes("picture"), // 사진
                        rs.getString("codi_type") // 코디 타입
                    ));
                }
            }
            return codis;
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("CodiOracleDAO: Failed to findAll", e);
        }
    }

    @Override
    public List<Codi> findByName(String memberId, String keyword) {
        return List.of();
    }

    private Wardrobe mapResultSetToWardrobe(ResultSet rs) throws SQLException {
        Wardrobe wardrobe = new Wardrobe();
        wardrobe.setId(rs.getLong("clothes_id"));
        wardrobe.setMemberId(rs.getLong("member_id"));
        wardrobe.setCategoryId(rs.getLong("category_id"));
        wardrobe.setName(rs.getString("clothes_name"));
        wardrobe.setMemo(rs.getString("memo"));
        wardrobe.setLike(rs.getString("liked"));
        wardrobe.setPicture(rs.getBytes("clothes_picture"));
        wardrobe.setKeyword(rs.getString("keyword"));
        wardrobe.setSize(rs.getString("item_size"));
        wardrobe.setColor(rs.getString("color"));
        wardrobe.setBrand(rs.getString("brand"));
        wardrobe.setDeleted("N");

        return wardrobe;
    }


    private void mapResultSetToCodiWithDetails(ResultSet rs, CodiDetailDTO codiWithDetails) throws SQLException {
        codiWithDetails.setCodiId(rs.getLong("id"));
        codiWithDetails.setScheduleName(rs.getString("schedule"));
        codiWithDetails.setCodiName(rs.getString("codi_name"));

        Date scheduleDate = rs.getDate("schedule_date");
        if (scheduleDate != null) {
            codiWithDetails.setCodiName(rs.getString("name"));
            codiWithDetails.setScheduleDate(scheduleDate.toLocalDate());
        }

        int scope = rs.getInt("scope");
        if (!rs.wasNull()) {
            codiWithDetails.setScope(scope);
        }
    }

    @Override
    public List<CodiDetailDTO> findAllWithDetails(Long memberId) {
        String sql = """
            SELECT c.*, c.name AS codi_name, cl.id as clothes_id, cl.name as clothes_name, cl.category_id, 
                   cl.memo, cl.liked, cl.picture as clothes_picture, cl.keyword, 
                   cl.item_size, cl.color, cl.brand
            FROM codi c
            LEFT JOIN codi_detail cd ON c.id = cd.codi_id
            LEFT JOIN clothes cl ON cd.clothes_id = cl.id AND cl.deleted = 'N'
            WHERE c.member_id = ? AND c.deleted = 'N' AND c.codi_type = 'W'
            ORDER BY c.id, cl.category_id DESC
            """;
        List<CodiDetailDTO> result = new ArrayList<>();
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setLong(1, memberId);

            try (ResultSet rs = pstmt.executeQuery()) {
                CodiDetailDTO current = null;
                List<Wardrobe> currentClothes = new ArrayList<>();

                while (rs.next()) {
                    Long codiId = rs.getLong("id");

                    // 새로운 코디인 경우
                    if (current == null || !current.getCodiId().equals(codiId)) {
                        // 이전 코디가 있다면 결과에 추가
                        if (current != null) {
                            current.setClothes(new ArrayList<>(currentClothes));
                            result.add(current);
                        }

                        // 새로운 코디 생성
                        current = new CodiDetailDTO();
                        mapResultSetToCodiWithDetails(rs, current);
                        currentClothes.clear();
                    }

                    // 옷 정보가 있는 경우 추가
                    Long clothesId = rs.getLong("clothes_id");
                    if (clothesId > 0) {
                        Wardrobe wardrobe = mapResultSetToWardrobe(rs);
                        currentClothes.add(wardrobe);
                    }
                }

                // 마지막 코디 추가
                if (current != null) {
                    current.setClothes(new ArrayList<>(currentClothes));
                    result.add(current);
                }
            }
            return result;
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("코디 목록 조회 중 오류", e);
        }
    }
}
