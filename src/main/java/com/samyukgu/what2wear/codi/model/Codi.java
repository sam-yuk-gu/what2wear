package com.samyukgu.what2wear.codi.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Blob;
import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Codi {
    Long id;
    Long memberId;
    String name;                // 코디 이름
    String schedule;            // 일정 이름
    LocalDate scheduleDate;
    Long scope;                 // 공개 범위 enum
    String weather;             // 날씨
    byte[] picture;             // 사진
    String codiType;
//    List<Clothes> clothes;    // 상세 조회용 옷 목록

    public Codi(Long id, String name, LocalDate scheduleDate) {
        this.id = id;
        this.name = name;
        this.scheduleDate = scheduleDate;
    }
}