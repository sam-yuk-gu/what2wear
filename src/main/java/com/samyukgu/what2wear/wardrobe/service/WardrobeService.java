package com.samyukgu.what2wear.wardrobe.service;

import com.samyukgu.what2wear.wardrobe.dao.WardrobeDAO;
import com.samyukgu.what2wear.wardrobe.model.Wardrobe;

import java.util.List;

public class WardrobeService {

    private final WardrobeDAO dao;

    public WardrobeService(WardrobeDAO dao) {
        this.dao = dao;
    }
    // 특정 옷 검색
    public Wardrobe getWardrobeById(Long id, Long memberId) {
        return dao.findById(id,  memberId);
    }

    // 전체 옷 검색
    public List<Wardrobe> getAllWardrobe(Long memberId) {
        return dao.findAll(memberId);
    }
    // 옷 추가
    public void addWardrobe(Wardrobe wardrobe){
        dao.save(wardrobe);
    }
    // 옷 수정
    public void updateWardrobe(Wardrobe wardrobe){
        dao.update(wardrobe);
    }
    // 옷 삭제
    public void deleteWardrobe(Long id, Long memberId) {
        dao.delete(id, memberId);
    }
}
