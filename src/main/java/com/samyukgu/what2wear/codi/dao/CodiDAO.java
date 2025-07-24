package com.samyukgu.what2wear.codi.dao;

import com.samyukgu.what2wear.codi.dto.CodiDetailDTO;
import com.samyukgu.what2wear.codi.dto.CodiListDTO;
import com.samyukgu.what2wear.codi.model.Codi;
import com.samyukgu.what2wear.codi.model.CodiSchedule;
import com.samyukgu.what2wear.codi.model.CodiScope;
import com.samyukgu.what2wear.wardrobe.model.Wardrobe;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

public interface CodiDAO {
    // 월별 일정 조회 (캘린더 렌더링 용)
    List<CodiSchedule> findMonthlyCodiSchedules(Long memberId, LocalDate date);

    // 일별 일정 조회
    List<CodiListDTO> findCodiList(Long memberId, LocalDate date);

    // 일정 상세 조회
    CodiDetailDTO findCodiScheduleDetail(Long memberId, Long codiId);

    // 일정 등록
    void create(Codi codi, Collection<Wardrobe> selectedOutfits);

    // 일정 수정
    void update(Codi codi, Collection<Wardrobe> selectedOutfits);

    // 일정 삭제
    void delete(Long memberId, Long codiId);

    // 전체 코디 목록 조회
    List<Codi> findAll(Long memberId);

    // 코디명 검색
    List<Codi> findByName(String memberId, String keyword);
    // findByName: (name) >> id, name, picture
}
