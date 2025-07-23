package com.samyukgu.what2wear.codi.dao;

import com.samyukgu.what2wear.codi.dto.CodiListDTO;
import com.samyukgu.what2wear.codi.model.Codi;
import com.samyukgu.what2wear.codi.model.CodiSchedule;
import com.samyukgu.what2wear.codi.model.CodiScope;
import com.samyukgu.what2wear.wardrobe.model.Wardrobe;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

public interface CodiDAO {
    // 1. 월별 일정 조회 (코디 요약 정보 리스트)
    List<CodiSchedule> findMonthlyCodiSchedules(Long memberId, LocalDate date);
    // date: 2025-07-01처럼 전달하면, 7월 전체로 처리
    // response: year-month-day, List<Codi : id, name, >
    List<CodiListDTO> findCodiList(String memberId, LocalDate date);

    // getCodiScheduleDetail 일정 상세 조회 (member_id, codi_id) >>
    // 2. 일정 상세 조회
    Codi findCodiScheduleDetail(String memberId, String codiId);
    // response: date, List<Codi: schedule(일정명), scope, <List<Clothes : category, name, image>>>

    // date(연월일) 전달하면 해당 날짜에 해당하는 일정 출력
    // 1. 날짜, 2. 리스트 (일정명, 속한 옷들)
    // 3. 일정 등록
    void create(Codi codi, Collection<Wardrobe> selectedOutfits);
    void insertCodiDetail(Long codiId, Long clothesId);
    void createCodiSchedule(String memberId, String scheduleName, LocalDate date, List<Number> clothingIds, CodiScope scope);

    // 4. 일정 수정: editSchedule
    void updateSchedule(
        String memberId,
        String codiId,
        String scheduleName,
        LocalDate date,
        List<Number> clothingIds,
        CodiScope scope
    );

    // 5. 일정 삭제: deleteSchedule
    void deleteSchedule(String memberId, String codiId);

    // 6. 전체 코디 목록 조회
    List<Codi> findAll(String memberId);

    // 7. 코디명 검색
    List<Codi> findByName(String memberId, String keyword);
    // findByName: (name) >> id, name, picture
}
