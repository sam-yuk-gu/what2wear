package com.samyukgu.what2wear.wardrobe.service;

import com.samyukgu.what2wear.wardrobe.dao.WardrobeDAO;
import com.samyukgu.what2wear.wardrobe.model.Wardrobe;

import java.util.List;

public class WardrobeService {

    private final WardrobeDAO wardrobeDAO;

    public WardrobeService(WardrobeDAO wardrobeDAO) {
        this.wardrobeDAO = wardrobeDAO;
    }
    // 특정 옷 검색
    public Wardrobe getWardrobeById(Long id, Long memberId) {
        return wardrobeDAO.findById(id,  memberId);
    }

    // 전체 옷 검색
    public List<Wardrobe> getAllWardrobe(Long memberId) {
        return wardrobeDAO.findAll(memberId);
    }
    // 옷 추가
    public void addWardrobe(Wardrobe wardrobe){
        wardrobeDAO.save(wardrobe);
    }
    // 옷 수정
    public void updateWardrobe(Wardrobe wardrobe){
        wardrobeDAO.update(wardrobe);
    }
    // 옷 삭제
    public void deleteWardrobe(Long id, Long memberId) {
        wardrobeDAO.delete(id, memberId);
    }
}
