package com.samyukgu.what2wear.myCodi.dao;

import com.samyukgu.what2wear.myCodi.model.CodiDetail;
import java.util.List;

public interface CodiDetailDAO {
    // 코디-옷 관계 저장
    void save(CodiDetail codiDetail);

    // 코디에 포함된 모든 옷 삭제
    void deleteByCodiId(Long codiId);

    // 특정 코디에 포함된 옷 ID 목록 조회
    List<Long> findClothesIdsByCodiId(Long codiId);

    // 특정 옷이 포함된 코디 ID 목록 조회
    List<Long> findCodiIdsByClothesId(Long clothesId);
}