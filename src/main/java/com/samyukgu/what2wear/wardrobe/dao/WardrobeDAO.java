package com.samyukgu.what2wear.wardrobe.dao;

import com.samyukgu.what2wear.wardrobe.model.Wardrobe;

import java.util.List;

public interface WardrobeDAO {

    // 해당 아이디에 해당하는 옷을 조회
    Wardrobe findById(Long id, Long memberId);
    // 해당 아이디에 해당하는 옷 조회
    List<Wardrobe> findAll(Long memberId);
    // 옷 저장
    void save(Wardrobe wardrobe);
    // 옷 업데이트
    void update(Wardrobe wardrobe);
    // 옷 삭제
    void delete(Long id, Long memberId);

}
