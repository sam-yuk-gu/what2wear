package com.samyukgu.what2wear.myCodi.dao;

import com.samyukgu.what2wear.myCodi.model.Codi;
import com.samyukgu.what2wear.myCodi.model.CodiWithDetails;

import java.util.List;

public interface CodiDAO {
    // 코디 저장
    void save(Codi codi);

    // 코디 업데이트
    void update(Codi codi);

    // 코디 삭제 (논리 삭제)
    void delete(Long id, Long memberId);

    // 특정 코디 조회
    Codi findById(Long id, Long memberId);

    // 특정 회원의 모든 코디 조회
    List<Codi> findAllByMemberId(Long memberId);

    // 코디와 함께 옷 정보도 조회
    CodiWithDetails findCodiWithDetails(Long id, Long memberId);

    // 특정 회원의 모든 코디와 옷 정보 조회
    List<CodiWithDetails> findAllCodiWithDetailsByMemberId(Long memberId);
}
