package com.samyukgu.what2wear.wardrobe.service;

import com.samyukgu.what2wear.wardrobe.dao.WardrobeDAO;
import com.samyukgu.what2wear.wardrobe.model.Wardrobe;

import java.util.List;
import java.util.logging.Logger;

/**
 * 옷장 관련 비즈니스 로직을 처리하는 서비스 클래스
 * 기존 DAO 구조를 유지하면서 향상된 기능 제공
 */
public class WardrobeService {

    private static final Logger logger = Logger.getLogger(WardrobeService.class.getName());
    private final WardrobeDAO wardrobeDAO;

    public WardrobeService(WardrobeDAO wardrobeDAO) {
        this.wardrobeDAO = wardrobeDAO;
    }

    /**
     * 특정 옷 조회
     * @param id 옷 ID
     * @param memberId 회원 ID
     * @return 옷 정보
     */
    public Wardrobe getWardrobeById(Long id, Long memberId) {
        try {
            validateIds(id, memberId);
            return wardrobeDAO.findById(id, memberId);
        } catch (Exception e) {
            logger.severe("옷 조회 실패 - ID: " + id + ", MemberID: " + memberId + ", Error: " + e.getMessage());
            throw new RuntimeException("옷 정보를 조회하는데 실패했습니다: " + e.getMessage(), e);
        }
    }

    /**
     * 회원의 모든 옷 조회
     * @param memberId 회원 ID
     * @return 옷 목록
     */
    public List<Wardrobe> getAllWardrobe(Long memberId) {
        try {
            validateMemberId(memberId);
            List<Wardrobe> wardrobes = wardrobeDAO.findAll(memberId);
            logger.info("회원 " + memberId + "의 옷 " + wardrobes.size() + "개 조회 완료");
            return wardrobes;
        } catch (Exception e) {
            logger.severe("전체 옷 조회 실패 - MemberID: " + memberId + ", Error: " + e.getMessage());
            throw new RuntimeException("옷장 데이터를 조회하는데 실패했습니다: " + e.getMessage(), e);
        }
    }

    /**
     * 새로운 옷 추가
     * @param wardrobe 추가할 옷 정보
     */
    public void addWardrobe(Wardrobe wardrobe) {
        try {
            validateWardrobeForSave(wardrobe);
            wardrobeDAO.save(wardrobe);
            logger.info("새 옷 추가 완료 - Name: " + wardrobe.getName() + ", MemberID: " + wardrobe.getMemberId());
        } catch (Exception e) {
            logger.severe("옷 추가 실패 - Name: " + (wardrobe != null ? wardrobe.getName() : "null") +
                    ", Error: " + e.getMessage());
            throw new RuntimeException("옷을 저장하는데 실패했습니다: " + e.getMessage(), e);
        }
    }

    /**
     * 옷 정보 수정
     * @param wardrobe 수정할 옷 정보
     */
    public void updateWardrobe(Wardrobe wardrobe) {
        try {
            validateWardrobeForUpdate(wardrobe);

            // 기존 데이터 존재 여부 확인
            Wardrobe existingWardrobe = wardrobeDAO.findById(wardrobe.getId(), wardrobe.getMemberId());
            if (existingWardrobe == null) {
                throw new IllegalArgumentException("수정하려는 옷이 존재하지 않습니다.");
            }

            wardrobeDAO.update(wardrobe);
            logger.info("옷 수정 완료 - ID: " + wardrobe.getId() + ", Name: " + wardrobe.getName());
        } catch (Exception e) {
            logger.severe("옷 수정 실패 - ID: " + (wardrobe != null ? wardrobe.getId() : "null") +
                    ", Error: " + e.getMessage());
            throw new RuntimeException("옷 정보를 수정하는데 실패했습니다: " + e.getMessage(), e);
        }
    }

    /**
     * 옷 삭제 (소프트 삭제)
     * @param id 옷 ID
     * @param memberId 회원 ID
     */
    public void deleteWardrobe(Long id, Long memberId) {
        try {
            validateIds(id, memberId);

            // 기존 데이터 존재 여부 확인
            Wardrobe existingWardrobe = wardrobeDAO.findById(id, memberId);
            if (existingWardrobe == null) {
                throw new IllegalArgumentException("삭제하려는 옷이 존재하지 않습니다.");
            }

            wardrobeDAO.delete(id, memberId);
            logger.info("옷 삭제 완료 - ID: " + id + ", MemberID: " + memberId);
        } catch (Exception e) {
            logger.severe("옷 삭제 실패 - ID: " + id + ", MemberID: " + memberId + ", Error: " + e.getMessage());
            throw new RuntimeException("옷을 삭제하는데 실패했습니다: " + e.getMessage(), e);
        }
    }

    /**
     * 즐겨찾기 상태 업데이트
     * @param id 옷 ID
     * @param memberId 회원 ID
     * @param isFavorite 즐겨찾기 여부
     */
    public void updateFavoriteStatus(Long id, Long memberId, boolean isFavorite) {
        try {
            validateIds(id, memberId);

            Wardrobe wardrobe = wardrobeDAO.findById(id, memberId);
            if (wardrobe == null) {
                throw new IllegalArgumentException("해당 옷이 존재하지 않습니다.");
            }

            wardrobe.setLike(isFavorite ? "Y" : "N");
            wardrobeDAO.update(wardrobe);

            logger.info("즐겨찾기 상태 변경 완료 - ID: " + id + ", 즐겨찾기: " + isFavorite);
        } catch (Exception e) {
            logger.severe("즐겨찾기 상태 변경 실패 - ID: " + id + ", Error: " + e.getMessage());
            throw new RuntimeException("즐겨찾기 상태를 변경하는데 실패했습니다: " + e.getMessage(), e);
        }
    }

    /**
     * 카테고리별 옷 개수 조회
     * @param memberId 회원 ID
     * @param categoryId 카테고리 ID
     * @return 해당 카테고리의 옷 개수
     */
    public long getWardrobeCountByCategory(Long memberId, Long categoryId) {
        try {
            validateIds(categoryId, memberId);
            List<Wardrobe> allWardrobes = wardrobeDAO.findAll(memberId);

            return allWardrobes.stream()
                    .filter(w -> categoryId.equals(w.getCategoryId()))
                    .count();
        } catch (Exception e) {
            logger.severe("카테고리별 옷 개수 조회 실패 - CategoryID: " + categoryId +
                    ", MemberID: " + memberId + ", Error: " + e.getMessage());
            throw new RuntimeException("카테고리별 옷 개수를 조회하는데 실패했습니다: " + e.getMessage(), e);
        }
    }

    /**
     * 즐겨찾기한 옷 목록 조회
     * @param memberId 회원 ID
     * @return 즐겨찾기한 옷 목록
     */
    public List<Wardrobe> getFavoriteWardrobes(Long memberId) {
        try {
            validateMemberId(memberId);
            List<Wardrobe> allWardrobes = wardrobeDAO.findAll(memberId);

            return allWardrobes.stream()
                    .filter(w -> "Y".equals(w.getLike()))
                    .collect(java.util.stream.Collectors.toList());
        } catch (Exception e) {
            logger.severe("즐겨찾기 옷 조회 실패 - MemberID: " + memberId + ", Error: " + e.getMessage());
            throw new RuntimeException("즐겨찾기 옷 목록을 조회하는데 실패했습니다: " + e.getMessage(), e);
        }
    }

    /**
     * 옷 이름으로 검색
     * @param memberId 회원 ID
     * @param keyword 검색 키워드
     * @return 검색 결과 목록
     */
    public List<Wardrobe> searchWardrobeByName(Long memberId, String keyword) {
        try {
            validateMemberId(memberId);
            if (keyword == null || keyword.trim().isEmpty()) {
                return getAllWardrobe(memberId);
            }

            List<Wardrobe> allWardrobes = wardrobeDAO.findAll(memberId);
            String searchKeyword = keyword.trim().toLowerCase();

            return allWardrobes.stream()
                    .filter(w -> w.getName() != null &&
                            w.getName().toLowerCase().contains(searchKeyword))
                    .collect(java.util.stream.Collectors.toList());
        } catch (Exception e) {
            logger.severe("옷 검색 실패 - MemberID: " + memberId +
                    ", Keyword: " + keyword + ", Error: " + e.getMessage());
            throw new RuntimeException("옷을 검색하는데 실패했습니다: " + e.getMessage(), e);
        }
    }

    // 유효성 검증 메서드들
    private void validateIds(Long id, Long memberId) {
        if (id == null) {
            throw new IllegalArgumentException("옷 ID가 필요합니다.");
        }
        if (memberId == null) {
            throw new IllegalArgumentException("회원 ID가 필요합니다.");
        }
        if (id <= 0) {
            throw new IllegalArgumentException("유효하지 않은 옷 ID입니다.");
        }
        if (memberId <= 0) {
            throw new IllegalArgumentException("유효하지 않은 회원 ID입니다.");
        }
    }

    private void validateMemberId(Long memberId) {
        if (memberId == null) {
            throw new IllegalArgumentException("회원 ID가 필요합니다.");
        }
        if (memberId <= 0) {
            throw new IllegalArgumentException("유효하지 않은 회원 ID입니다.");
        }
    }

    private void validateWardrobeForSave(Wardrobe wardrobe) {
        if (wardrobe == null) {
            throw new IllegalArgumentException("옷 정보가 필요합니다.");
        }
        if (wardrobe.getMemberId() == null || wardrobe.getMemberId() <= 0) {
            throw new IllegalArgumentException("유효한 회원 ID가 필요합니다.");
        }
        if (wardrobe.getName() == null || wardrobe.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("옷 이름이 필요합니다.");
        }
        if (wardrobe.getName().length() > 50) {
            throw new IllegalArgumentException("옷 이름은 50자를 초과할 수 없습니다.");
        }
        if (wardrobe.getCategoryId() == null || wardrobe.getCategoryId() <= 0) {
            throw new IllegalArgumentException("유효한 카테고리가 필요합니다.");
        }

        // 추가 필드 검증
        if (wardrobe.getBrand() != null && wardrobe.getBrand().length() > 30) {
            throw new IllegalArgumentException("브랜드명은 30자를 초과할 수 없습니다.");
        }
        if (wardrobe.getSize() != null && wardrobe.getSize().length() > 10) {
            throw new IllegalArgumentException("사이즈는 10자를 초과할 수 없습니다.");
        }
        if (wardrobe.getMemo() != null && wardrobe.getMemo().length() > 200) {
            throw new IllegalArgumentException("메모는 200자를 초과할 수 없습니다.");
        }

        // 이미지 크기 검증 (5MB 제한)
        if (wardrobe.getPicture() != null && wardrobe.getPicture().length > 5 * 1024 * 1024) {
            throw new IllegalArgumentException("이미지 파일 크기는 5MB를 초과할 수 없습니다.");
        }
    }

    private void validateWardrobeForUpdate(Wardrobe wardrobe) {
        validateWardrobeForSave(wardrobe); // 기본 검증 수행

        if (wardrobe.getId() == null || wardrobe.getId() <= 0) {
            throw new IllegalArgumentException("수정할 옷의 ID가 필요합니다.");
        }
    }

    /**
     * 통계 정보 조회
     * @param memberId 회원 ID
     * @return 통계 정보 맵
     */
    public java.util.Map<String, Object> getWardrobeStatistics(Long memberId) {
        try {
            validateMemberId(memberId);
            List<Wardrobe> allWardrobes = wardrobeDAO.findAll(memberId);

            java.util.Map<String, Object> stats = new java.util.HashMap<>();
            stats.put("totalCount", allWardrobes.size());
            stats.put("favoriteCount", allWardrobes.stream()
                    .mapToInt(w -> "Y".equals(w.getLike()) ? 1 : 0)
                    .sum());

            // 카테고리별 통계
            java.util.Map<Long, Long> categoryStats = allWardrobes.stream()
                    .filter(w -> w.getCategoryId() != null)
                    .collect(java.util.stream.Collectors.groupingBy(
                            Wardrobe::getCategoryId,
                            java.util.stream.Collectors.counting()));
            stats.put("categoryStats", categoryStats);

            // 색상별 통계
            java.util.Map<String, Long> colorStats = allWardrobes.stream()
                    .filter(w -> w.getColor() != null && !w.getColor().trim().isEmpty())
                    .collect(java.util.stream.Collectors.groupingBy(
                            w -> w.getColor().trim(),
                            java.util.stream.Collectors.counting()));
            stats.put("colorStats", colorStats);

            return stats;
        } catch (Exception e) {
            logger.severe("통계 정보 조회 실패 - MemberID: " + memberId + ", Error: " + e.getMessage());
            throw new RuntimeException("통계 정보를 조회하는데 실패했습니다: " + e.getMessage(), e);
        }
    }

    /**
     * 옷장이 비어있는지 확인
     * @param memberId 회원 ID
     * @return 비어있으면 true
     */
    public boolean isWardrobeEmpty(Long memberId) {
        try {
            validateMemberId(memberId);
            List<Wardrobe> wardrobes = wardrobeDAO.findAll(memberId);
            return wardrobes.isEmpty();
        } catch (Exception e) {
            logger.severe("옷장 상태 확인 실패 - MemberID: " + memberId + ", Error: " + e.getMessage());
            return true; // 에러 시 안전하게 true 반환
        }
    }

    /**
     * 대량 삭제 (여러 옷을 한번에 삭제)
     * @param ids 삭제할 옷 ID 목록
     * @param memberId 회원 ID
     * @return 성공적으로 삭제된 개수
     */
    public int bulkDeleteWardrobes(List<Long> ids, Long memberId) {
        if (ids == null || ids.isEmpty()) {
            throw new IllegalArgumentException("삭제할 옷 ID 목록이 필요합니다.");
        }

        validateMemberId(memberId);
        int deletedCount = 0;

        for (Long id : ids) {
            try {
                deleteWardrobe(id, memberId);
                deletedCount++;
            } catch (Exception e) {
                logger.warning("ID " + id + " 삭제 실패: " + e.getMessage());
                // 개별 삭제 실패는 로그만 남기고 계속 진행
            }
        }

        logger.info("대량 삭제 완료 - 요청: " + ids.size() + "개, 성공: " + deletedCount + "개");
        return deletedCount;
    }

    /**
     * 최근 추가된 옷 조회
     * @param memberId 회원 ID
     * @param limit 조회할 개수
     * @return 최근 추가된 옷 목록
     */
    public List<Wardrobe> getRecentWardrobes(Long memberId, int limit) {
        try {
            validateMemberId(memberId);
            if (limit <= 0) {
                throw new IllegalArgumentException("조회할 개수는 1 이상이어야 합니다.");
            }

            List<Wardrobe> allWardrobes = wardrobeDAO.findAll(memberId);

            return allWardrobes.stream()
                    .sorted((w1, w2) -> w2.getId().compareTo(w1.getId())) // ID 역순으로 정렬
                    .limit(limit)
                    .collect(java.util.stream.Collectors.toList());

        } catch (Exception e) {
            logger.severe("최근 옷 조회 실패 - MemberID: " + memberId +
                    ", Limit: " + limit + ", Error: " + e.getMessage());
            throw new RuntimeException("최근 추가된 옷 목록을 조회하는데 실패했습니다: " + e.getMessage(), e);
        }
    }
}