package com.samyukgu.what2wear.myCodi.dao;

import com.samyukgu.what2wear.myCodi.model.Codi;
import com.samyukgu.what2wear.myCodi.model.CodiWithDetails;
import com.samyukgu.what2wear.wardrobe.model.Wardrobe;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class CodiOracleDAO implements CodiDAO {

    private static String url;
    private static String username;
    private static String password;

    public CodiOracleDAO() {
        Properties props = new Properties();
        try (InputStream is = ClassLoader.getSystemResourceAsStream("application.properties");
             InputStreamReader isr = new InputStreamReader(is, StandardCharsets.UTF_8)) {
            props.load(isr);
            url = props.getProperty("db.url");
            username = props.getProperty("db.user");
            password = props.getProperty("db.password");
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("check your database connection");
        }
    }

    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url, username, password);
    }

    @Override
    public void save(Codi codi) {
        String insertSql = """
            INSERT INTO codi 
            (id, member_id, name, schedule, schedule_date, scope, weather, picture, codi_type, deleted,created_at)
            VALUES (SEQ_CODI.NEXTVAL, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
            """;

        String currvalSql = "SELECT SEQ_CODI.CURRVAL FROM dual";

        try (Connection conn = getConnection()) {
            // INSERT
            try (PreparedStatement pstmt = conn.prepareStatement(insertSql)) {
                pstmt.setLong(1, codi.getMemberId());
                pstmt.setString(2, codi.getName());
                pstmt.setString(3, codi.getSchedule());

                if (codi.getScheduleDate() != null) {
                    pstmt.setDate(4, Date.valueOf(codi.getScheduleDate()));
                } else {
                    pstmt.setNull(4, Types.DATE);
                }

                pstmt.setInt(5, 2);

                pstmt.setString(6, codi.getWeather());

                if (codi.getPicture() != null) {
                    pstmt.setBytes(7, codi.getPicture());
                } else {
                    pstmt.setNull(7, Types.BLOB);
                }

                pstmt.setString(8, "W");
                pstmt.setString(9, "N");
                pstmt.setDate(10, new Date(System.currentTimeMillis()));

                pstmt.executeUpdate();
            }

            // ID 조회
            try (PreparedStatement keyStmt = conn.prepareStatement(currvalSql);
                 ResultSet rs = keyStmt.executeQuery()) {
                if (rs.next()) {
                    long generatedId = rs.getLong(1);
                    codi.setId(generatedId);
                } else {
                    throw new SQLException("생성된 코디 ID 조회 실패");
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("코디 저장 중 오류", e);
        }
    }

    @Override
    public void update(Codi codi) {
        String sql = """
            UPDATE codi SET 
                name = ?, 
                schedule = ?, 
                schedule_date = ?, 
                scope = ?, 
                weather = ?, 
                picture = ?, 
                codi_type = ?, 
                deleted = ?
            WHERE id = ? AND member_id = ?
            """;

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, codi.getName());
            pstmt.setString(2, codi.getSchedule());

            if (codi.getScheduleDate() != null) {
                pstmt.setDate(3, Date.valueOf(codi.getScheduleDate()));
            } else {
                pstmt.setNull(3, Types.DATE);
            }

            if (codi.getScope() != null) {
                pstmt.setInt(4, codi.getScope());
            } else {
                pstmt.setNull(4, Types.INTEGER);
            }

            pstmt.setString(5, codi.getWeather());

            if (codi.getPicture() != null) {
                pstmt.setBytes(6, codi.getPicture());
            } else {
                pstmt.setNull(6, Types.BLOB);
            }

            pstmt.setString(7, codi.getCodiType());
            pstmt.setString(8, codi.getDeleted());
            pstmt.setLong(9, codi.getId());
            pstmt.setLong(10, codi.getMemberId());

            pstmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("코디 수정 중 오류", e);
        }
    }

    @Override
    public void delete(Long id, Long memberId) {
        String sql = """
            UPDATE codi 
            SET deleted = 'Y' 
            WHERE id = ? AND member_id = ?
            """;

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setLong(1, id);
            pstmt.setLong(2, memberId);

            pstmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("코디 삭제 중 오류", e);
        }
    }

    @Override
    public Codi findById(Long id, Long memberId) {
        String sql = """
            SELECT * 
            FROM codi 
            WHERE id = ? AND member_id = ? AND deleted = 'N'
            """;

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setLong(1, id);
            pstmt.setLong(2, memberId);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToCodi(rs);
                }
            }
            return null;

        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("코디 조회 중 오류", e);
        }
    }

    @Override
    public List<Codi> findAllByMemberId(Long memberId) {
        String sql = """
            SELECT * 
            FROM codi 
            WHERE member_id = ? AND deleted = 'N'
            ORDER BY id DESC
            """;

        List<Codi> codis = new ArrayList<>();

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setLong(1, memberId);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    codis.add(mapResultSetToCodi(rs));
                }
            }
            return codis;

        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("코디 목록 조회 중 오류", e);
        }
    }

    @Override
    public CodiWithDetails findCodiWithDetails(Long id, Long memberId) {
        String sql = """
            SELECT c.*, cl.id as clothes_id, cl.name as clothes_name, cl.category_id, 
                   cl.memo, cl.liked, cl.picture as clothes_picture, cl.keyword, 
                   cl.item_size, cl.color, cl.brand
            FROM codi c
            LEFT JOIN codi_detail cd ON c.id = cd.codi_id
            LEFT JOIN clothes cl ON cd.clothes_id = cl.id AND cl.deleted = 'N'
            WHERE c.id = ? AND c.member_id = ? AND c.deleted = 'N'
            """;

        CodiWithDetails codiWithDetails = null;
        List<Wardrobe> clothes = new ArrayList<>();

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setLong(1, id);
            pstmt.setLong(2, memberId);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    if (codiWithDetails == null) {
                        codiWithDetails = new CodiWithDetails();
                        mapResultSetToCodiWithDetails(rs, codiWithDetails);
                    }

                    Long clothesId = rs.getLong("clothes_id");
                    if (clothesId > 0) {
                        Wardrobe wardrobe = mapResultSetToWardrobe(rs);
                        clothes.add(wardrobe);
                    }
                }
            }

            if (codiWithDetails != null) {
                codiWithDetails.setClothes(clothes);
            }

            return codiWithDetails;

        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("코디 상세 조회 중 오류", e);
        }
    }

    @Override
    public List<CodiWithDetails> findAllCodiWithDetailsByMemberId(Long memberId) {
        String sql = """
            SELECT c.*, cl.id as clothes_id, cl.name as clothes_name, cl.category_id, 
                   cl.memo, cl.liked, cl.picture as clothes_picture, cl.keyword, 
                   cl.item_size, cl.color, cl.brand
            FROM codi c
            LEFT JOIN codi_detail cd ON c.id = cd.codi_id
            LEFT JOIN clothes cl ON cd.clothes_id = cl.id AND cl.deleted = 'N'
            WHERE c.member_id = ? AND c.deleted = 'N'
            ORDER BY c.id DESC
            """;

        List<CodiWithDetails> result = new ArrayList<>();

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setLong(1, memberId);

            try (ResultSet rs = pstmt.executeQuery()) {
                CodiWithDetails current = null;
                List<Wardrobe> currentClothes = new ArrayList<>();

                while (rs.next()) {
                    Long codiId = rs.getLong("id");

                    // 새로운 코디인 경우
                    if (current == null || !current.getId().equals(codiId)) {
                        // 이전 코디가 있다면 결과에 추가
                        if (current != null) {
                            current.setClothes(new ArrayList<>(currentClothes));
                            result.add(current);
                        }

                        // 새로운 코디 생성
                        current = new CodiWithDetails();
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

    private Codi mapResultSetToCodi(ResultSet rs) throws SQLException {
        Codi codi = new Codi();
        codi.setId(rs.getLong("id"));
        codi.setMemberId(rs.getLong("member_id"));
        codi.setName(rs.getString("name"));
        codi.setSchedule(rs.getString("schedule"));

        Date scheduleDate = rs.getDate("schedule_date");
        if (scheduleDate != null) {
            codi.setScheduleDate(scheduleDate.toLocalDate());
        }

        int scope = rs.getInt("scope");
        if (!rs.wasNull()) {
            codi.setScope(scope);
        }

        codi.setWeather(rs.getString("weather"));
        codi.setPicture(rs.getBytes("picture"));
        codi.setCodiType(rs.getString("codi_type"));
        codi.setDeleted(rs.getString("deleted"));

        return codi;
    }

    private void mapResultSetToCodiWithDetails(ResultSet rs, CodiWithDetails codiWithDetails) throws SQLException {
        codiWithDetails.setId(rs.getLong("id"));
        codiWithDetails.setMemberId(rs.getLong("member_id"));
        codiWithDetails.setName(rs.getString("name"));
        codiWithDetails.setSchedule(rs.getString("schedule"));

        Date scheduleDate = rs.getDate("schedule_date");
        if (scheduleDate != null) {
            codiWithDetails.setScheduleDate(scheduleDate.toLocalDate());
        }

        int scope = rs.getInt("scope");
        if (!rs.wasNull()) {
            codiWithDetails.setScope(scope);
        }

        codiWithDetails.setWeather(rs.getString("weather"));
        codiWithDetails.setPicture(rs.getBytes("picture"));
        codiWithDetails.setCodiType(rs.getString("codi_type"));
        codiWithDetails.setDeleted(rs.getString("deleted"));
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
}