package com.samyukgu.what2wear.codi.service;

import com.samyukgu.what2wear.codi.dao.CodiDAO;
import com.samyukgu.what2wear.codi.dto.CodiDetailDTO;
import com.samyukgu.what2wear.codi.dto.CodiListDTO;
import com.samyukgu.what2wear.codi.model.Codi;
import com.samyukgu.what2wear.codi.model.CodiSchedule;
import com.samyukgu.what2wear.wardrobe.model.Wardrobe;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

public class CodiService {
    private final CodiDAO dao;

    public CodiService(CodiDAO dao) { this.dao = dao; }

    // 월별 코디 반환
    public List<CodiSchedule> getMonthlyCodiSchedules(Long memberId, LocalDate date){
        return dao.findMonthlyCodiSchedules(memberId, date);
    }

    // 일자 별 코디 리스트 반환
    public List<CodiListDTO> getCodiList(Long memberId, LocalDate date) {
        return dao.findCodiList(memberId, date);
    }

    // 일정 추가
    public void createCodiSchedule(Long memberId, String title, LocalDate date, int scope, Collection<Wardrobe> selectedOutfits) {
        Codi codi = new Codi();
        codi.setMemberId(memberId);
        codi.setSchedule(title);
        codi.setScheduleDate(date);
        codi.setScope((long) scope);
        codi.setCodiType("S");
        dao.create(codi, selectedOutfits);
    }

    public CodiDetailDTO getCodiDetail(Long memberId, Long codiId) {
        return dao.findCodiScheduleDetail(memberId, codiId);
    }

    public void updateCodiSchedule(Long id, Long memberId, String title, LocalDate date, int scope, Collection<Wardrobe> selectedOutfits) {
        Codi codi = new Codi();
        codi.setId(id);
        codi.setMemberId(memberId);
        codi.setSchedule(title);
        codi.setScheduleDate(date);
        codi.setScope((long) scope);
        codi.setCodiType("S");
        dao.update(codi, selectedOutfits);
    }

    public void deleteCodiSchedule(Long memberId, Long codiId) {
        dao.delete(memberId, codiId);
    }
}
