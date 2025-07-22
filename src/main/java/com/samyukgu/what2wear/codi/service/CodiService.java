package com.samyukgu.what2wear.codi.service;

import com.samyukgu.what2wear.codi.dao.CodiDAO;
import com.samyukgu.what2wear.codi.dto.CodiListDTO;
import com.samyukgu.what2wear.codi.dto.CodiScheduleDTO;
import com.samyukgu.what2wear.codi.model.Codi;
import com.samyukgu.what2wear.wardrobe.model.Wardrobe;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

public class CodiService {
    private final CodiDAO dao;

    public CodiService(CodiDAO dao) { this.dao = dao; }

    // 월별 코디 반환
    public List<CodiScheduleDTO> getMonthlyCodiSchedules(Long memberId, LocalDate date){
        return dao.findMonthlyCodiSchedules(memberId, date);
    }

    // 일자 별 코디 리스트 반환
    public List<CodiListDTO> getCodiList(String memberId, LocalDate date) {
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

    //
}
