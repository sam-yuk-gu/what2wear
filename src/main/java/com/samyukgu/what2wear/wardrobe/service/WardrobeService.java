package com.samyukgu.what2wear.wardrobe.service;

import com.samyukgu.what2wear.wardrobe.dao.WardrobeDAO;
import com.samyukgu.what2wear.wardrobe.dao.WardrobeOracleDAO;
import com.samyukgu.what2wear.wardrobe.model.Wardrobe;

import java.util.List;
import java.util.logging.Logger;

public class WardrobeService {

    private static final Logger logger = Logger.getLogger(WardrobeService.class.getName());
    private final WardrobeDAO wardrobeDAO;

    public WardrobeService(WardrobeDAO wardrobeDAO) {
        this.wardrobeDAO = wardrobeDAO;
    }

    // 로그인 한 회원이 가지고 있는 특정 옷 조회
    public Wardrobe getWardrobeById(Long id, Long memberId) {
        try {
            validateIds(id, memberId);
            return wardrobeDAO.findById(id, memberId);
        } catch (Exception e) {
            logger.severe("옷 조회 실패 - ID: " + id + ", MemberID: " + memberId + ", Error: " + e.getMessage());
            throw new RuntimeException("옷 정보를 조회하는데 실패했습니다: " + e.getMessage(), e);
        }
    }

    // 로그인한 회원이 가지고 있는 전체 옷 조회
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

    // 로그인한 회원이 새로운 옷 추가
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

    // 로그인 회원의 옷 수정
    public void updateWardrobe(Wardrobe wardrobe) {
        try {
            logger.info("=== WardrobeService updateWardrobe 시작 ===");
            logger.info("수정할 옷 ID: " + wardrobe.getId());
            logger.info("회원 ID: " + wardrobe.getMemberId());
            logger.info("즐겨찾기 상태: " + wardrobe.getLike());

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

    // 로그인한 회원이 가지고 있는 옷 삭제
    public void deleteWardrobe(Long id, Long memberId) {
        try {
            validateIds(id, memberId);
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

    // WardrobeService.java의 최적화된 toggleFavoriteStatus 메서드
    public void toggleFavoriteStatus(Long id, Long memberId) {
        try {
            logger.info("즐겨찾기 토글 - 옷 ID: " + id + ", 회원 ID: " + memberId);

            validateIds(id, memberId);

            // 현재 옷 정보 조회
            Wardrobe wardrobe = wardrobeDAO.findById(id, memberId);
            if (wardrobe == null) {
                throw new IllegalArgumentException("해당 옷이 존재하지 않습니다.");
            }

            // 즐겨찾기 상태 토글
            boolean currentFavorite = "Y".equals(wardrobe.getLike());
            String newFavoriteStatus = currentFavorite ? "N" : "Y";

            logger.info("즐겨찾기 변경: " + wardrobe.getLike() + " -> " + newFavoriteStatus);

            // ★ 최적화: 즐겨찾기 전용 업데이트 메서드 사용 ★
            if (wardrobeDAO instanceof WardrobeOracleDAO) {
                // 즐겨찾기만 업데이트하는 빠른 쿼리 사용
                ((WardrobeOracleDAO) wardrobeDAO).updateFavoriteStatus(id, memberId, newFavoriteStatus);
            } else {
                // 일반 업데이트 메서드 사용 (fallback)
                wardrobe.setLike(newFavoriteStatus);
                wardrobeDAO.update(wardrobe);
            }

            logger.info("즐겨찾기 변경 완료 - ID: " + id);
        } catch (Exception e) {
            logger.severe("즐겨찾기 변경 실패 - ID: " + id + ", Error: " + e.getMessage());
            throw new RuntimeException("즐겨찾기 상태를 변경하는데 실패했습니다: " + e.getMessage(), e);
        }
    }

    // 기존 즐겨찾기 메서드 (boolean 매개변수 사용)
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

    // 로그인 한 회원의 카테고리별 옷 갯수 조회
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

    // 즐겨 찾기한 옷 조회
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

    // 옷 이름 검색
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

        // 필수 필드 검증 - 사진, 이름, 카테고리만 필수
        if (wardrobe.getPicture() == null || wardrobe.getPicture().length == 0) {
            throw new IllegalArgumentException("사진이 필요합니다.");
        }
        if (wardrobe.getName() == null || wardrobe.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("옷 이름이 필요합니다.");
        }
        if (wardrobe.getCategoryId() == null || wardrobe.getCategoryId() <= 0) {
            throw new IllegalArgumentException("유효한 카테고리가 필요합니다.");
        }

        // 길이 제한 검증
        if (wardrobe.getName().length() > 50) {
            throw new IllegalArgumentException("옷 이름은 50자를 초과할 수 없습니다.");
        }

        // 선택 필드들 검증 - null 허용, 값이 있을 때만 길이 검증
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

    // 추가
    public Wardrobe getWardrobeByName(String name, Long memberId) {
        return wardrobeDAO.findByNameAndMember(name, memberId);
    }
}