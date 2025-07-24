package com.samyukgu.what2wear.myCodi.service;

import com.samyukgu.what2wear.myCodi.dao.CodiDAO;
import com.samyukgu.what2wear.myCodi.dao.CodiDetailDAO;
import com.samyukgu.what2wear.myCodi.model.Codi;
import com.samyukgu.what2wear.myCodi.model.CodiDetail;
import com.samyukgu.what2wear.myCodi.model.CodiWithDetails;

import java.util.List;
import java.util.logging.Logger;

public class CodiService {

    private static final Logger logger = Logger.getLogger(CodiService.class.getName());
    private final CodiDAO codiDAO;
    private final CodiDetailDAO codiDetailDAO;

    public CodiService(CodiDAO codiDAO, CodiDetailDAO codiDetailDAO) {
        this.codiDAO = codiDAO;
        this.codiDetailDAO = codiDetailDAO;
    }


    // 코디 저장 (코디와 옷 관계 함께 저장)
public void saveCodi(Codi codi, List<Long> clothesIds) {
    try {
        validateCodiForSave(codi);
        validateClothesIds(clothesIds);

        // 코디 저장
        codiDAO.save(codi);
        logger.info("코디 저장 완료 - ID: " + codi.getId() + ", Name: " + codi.getName());

        // 코디-옷 관계 저장
        if (clothesIds != null && !clothesIds.isEmpty()) {
            for (Long clothesId : clothesIds) {
                CodiDetail codiDetail = new CodiDetail(codi.getId(), clothesId);
                codiDetailDAO.save(codiDetail);
            }
            logger.info("코디-옷 관계 저장 완료 - 코디 ID: " + codi.getId() + ", 옷 개수: " + clothesIds.size());
        }

    } catch (Exception e) {
        logger.severe("코디 저장 실패 - Name: " + (codi != null ? codi.getName() : "null") +
                ", Error: " + e.getMessage());
        throw new RuntimeException("코디를 저장하는데 실패했습니다: " + e.getMessage(), e);
    }
}

// 코디 수정 (코디와 옷 관계 함께 수정)
public void updateCodi(Codi codi, List<Long> clothesIds) {
    try {
        validateCodiForUpdate(codi);
        validateClothesIds(clothesIds);

        // 기존 코디 존재 여부 확인
        Codi existingCodi = codiDAO.findById(codi.getId(), codi.getMemberId());
        if (existingCodi == null) {
            throw new IllegalArgumentException("수정하려는 코디가 존재하지 않습니다.");
        }

        // 코디 정보 수정
        codiDAO.update(codi);
        logger.info("코디 수정 완료 - ID: " + codi.getId() + ", Name: " + codi.getName());

        // 기존 코디-옷 관계 삭제 후 새로 저장
        codiDetailDAO.deleteByCodiId(codi.getId());

        if (clothesIds != null && !clothesIds.isEmpty()) {
            for (Long clothesId : clothesIds) {
                CodiDetail codiDetail = new CodiDetail(codi.getId(), clothesId);
                codiDetailDAO.save(codiDetail);
            }
            logger.info("코디-옷 관계 수정 완료 - 코디 ID: " + codi.getId() + ", 옷 개수: " + clothesIds.size());
        }

    } catch (Exception e) {
        logger.severe("코디 수정 실패 - ID: " + (codi != null ? codi.getId() : "null") +
                ", Error: " + e.getMessage());
        throw new RuntimeException("코디 정보를 수정하는데 실패했습니다: " + e.getMessage(), e);
    }
}

// 코디 삭제
public void deleteCodi(Long id, Long memberId) {
    try {
        validateIds(id, memberId);

        // 코디 존재 여부 확인
        Codi existingCodi = codiDAO.findById(id, memberId);
        if (existingCodi == null) {
            throw new IllegalArgumentException("삭제하려는 코디가 존재하지 않습니다.");
        }

        // 코디-옷 관계 삭제
        codiDetailDAO.deleteByCodiId(id);

        // 코디 삭제 (논리 삭제)
        codiDAO.delete(id, memberId);

        logger.info("코디 삭제 완료 - ID: " + id + ", MemberID: " + memberId);

    } catch (Exception e) {
        logger.severe("코디 삭제 실패 - ID: " + id + ", MemberID: " + memberId + ", Error: " + e.getMessage());
        throw new RuntimeException("코디를 삭제하는데 실패했습니다: " + e.getMessage(), e);
    }
}

// 특정 코디 조회
public Codi getCodiById(Long id, Long memberId) {
    try {
        validateIds(id, memberId);
        return codiDAO.findById(id, memberId);
    } catch (Exception e) {
        logger.severe("코디 조회 실패 - ID: " + id + ", MemberID: " + memberId + ", Error: " + e.getMessage());
        throw new RuntimeException("코디 정보를 조회하는데 실패했습니다: " + e.getMessage(), e);
    }
}

// 특정 회원의 모든 코디 조회
public List<Codi> getAllCodisByMemberId(Long memberId) {
    try {
        validateMemberId(memberId);
        List<Codi> codis = codiDAO.findAllByMemberId(memberId);
        logger.info("회원 " + memberId + "의 코디 " + codis.size() + "개 조회 완료");
        return codis;
    } catch (Exception e) {
        logger.severe("코디 목록 조회 실패 - MemberID: " + memberId + ", Error: " + e.getMessage());
        throw new RuntimeException("코디 목록을 조회하는데 실패했습니다: " + e.getMessage(), e);
    }
}

// 코디와 옷 정보 함께 조회
public CodiWithDetails getCodiWithDetails(Long id, Long memberId) {
    try {
        validateIds(id, memberId);
        return codiDAO.findCodiWithDetails(id, memberId);
    } catch (Exception e) {
        logger.severe("코디 상세 조회 실패 - ID: " + id + ", MemberID: " + memberId + ", Error: " + e.getMessage());
        throw new RuntimeException("코디 상세 정보를 조회하는데 실패했습니다: " + e.getMessage(), e);
    }
}

// 특정 회원의 모든 코디와 옷 정보 조회
public List<CodiWithDetails> getAllCodiWithDetailsByMemberId(Long memberId) {
    try {
        validateMemberId(memberId);
        List<CodiWithDetails> codis = codiDAO.findAllCodiWithDetailsByMemberId(memberId);
        logger.info("회원 " + memberId + "의 코디 상세 " + codis.size() + "개 조회 완료");
        return codis;
    } catch (Exception e) {
        logger.severe("코디 상세 목록 조회 실패 - MemberID: " + memberId + ", Error: " + e.getMessage());
        throw new RuntimeException("코디 상세 목록을 조회하는데 실패했습니다: " + e.getMessage(), e);
    }
}

// 특정 코디에 포함된 옷 ID 목록 조회
public List<Long> getClothesIdsByCodiId(Long codiId) {
    try {
        if (codiId == null || codiId <= 0) {
            throw new IllegalArgumentException("유효하지 않은 코디 ID입니다.");
        }
        return codiDetailDAO.findClothesIdsByCodiId(codiId);
    } catch (Exception e) {
        logger.severe("코디 옷 목록 조회 실패 - CodiID: " + codiId + ", Error: " + e.getMessage());
        throw new RuntimeException("코디에 포함된 옷 목록을 조회하는데 실패했습니다: " + e.getMessage(), e);
    }
}

// 특정 옷이 포함된 코디 ID 목록 조회
public List<Long> getCodiIdsByClothesId(Long clothesId) {
    try {
        if (clothesId == null || clothesId <= 0) {
            throw new IllegalArgumentException("유효하지 않은 옷 ID입니다.");
        }
        return codiDetailDAO.findCodiIdsByClothesId(clothesId);
    } catch (Exception e) {
        logger.severe("옷이 포함된 코디 목록 조회 실패 - ClothesID: " + clothesId + ", Error: " + e.getMessage());
        throw new RuntimeException("옷이 포함된 코디 목록을 조회하는데 실패했습니다: " + e.getMessage(), e);
    }
}

// 유효성 검증 메서드들
private void validateIds(Long id, Long memberId) {
    if (id == null || id <= 0) {
        throw new IllegalArgumentException("유효하지 않은 코디 ID입니다.");
    }
    if (memberId == null || memberId <= 0) {
        throw new IllegalArgumentException("유효하지 않은 회원 ID입니다.");
    }
}

private void validateMemberId(Long memberId) {
    if (memberId == null || memberId <= 0) {
        throw new IllegalArgumentException("유효하지 않은 회원 ID입니다.");
    }
}

private void validateCodiForSave(Codi codi) {
    if (codi == null) {
        throw new IllegalArgumentException("코디 정보가 필요합니다.");
    }
    if (codi.getMemberId() == null || codi.getMemberId() <= 0) {
        throw new IllegalArgumentException("유효한 회원 ID가 필요합니다.");
    }
    if (codi.getName() == null || codi.getName().trim().isEmpty()) {
        throw new IllegalArgumentException("코디 이름이 필요합니다.");
    }
    if (codi.getName().length() > 255) {
        throw new IllegalArgumentException("코디 이름은 255자를 초과할 수 없습니다.");
    }

    // 추가 필드 검증
    if (codi.getSchedule() != null && codi.getSchedule().length() > 255) {
        throw new IllegalArgumentException("일정은 255자를 초과할 수 없습니다.");
    }
    if (codi.getWeather() != null && codi.getWeather().length() > 100) {
        throw new IllegalArgumentException("날씨 정보는 100자를 초과할 수 없습니다.");
    }

    // 이미지 크기 검증 (5MB 제한)
    if (codi.getPicture() != null && codi.getPicture().length > 5 * 1024 * 1024) {
        throw new IllegalArgumentException("이미지 파일 크기는 5MB를 초과할 수 없습니다.");
    }
}

private void validateCodiForUpdate(Codi codi) {
    validateCodiForSave(codi); // 기본 검증 수행

    if (codi.getId() == null || codi.getId() <= 0) {
        throw new IllegalArgumentException("수정할 코디의 ID가 필요합니다.");
    }
}

private void validateClothesIds(List<Long> clothesIds) {
    if (clothesIds != null) {
        for (Long clothesId : clothesIds) {
            if (clothesId == null || clothesId <= 0) {
                throw new IllegalArgumentException("유효하지 않은 옷 ID가 포함되어 있습니다: " + clothesId);
            }
        }
    }
}}