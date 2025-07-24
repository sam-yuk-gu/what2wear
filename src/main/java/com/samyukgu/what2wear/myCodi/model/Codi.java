package com.samyukgu.what2wear.myCodi.model;

import java.time.LocalDate;
import java.util.List;

public class Codi {
    private Long id;
    private Long memberId;
    private String name;
    private String schedule;
    private LocalDate scheduleDate;
    private Integer scope;
    private String weather;
    private byte[] picture;
    private String codiType;
    private String deleted;
    private List<Long> clothesIds; // 코디에 포함된 옷들의 ID 리스트

    public Codi() {}

    public Codi(Long id, Long memberId, String name, String schedule, LocalDate scheduleDate,
                Integer scope, String weather, byte[] picture, String codiType, String deleted) {
        this.id = id;
        this.memberId = memberId;
        this.name = name;
        this.schedule = schedule;
        this.scheduleDate = scheduleDate;
        this.scope = scope;
        this.weather = weather;
        this.picture = picture;
        this.codiType = codiType;
        this.deleted = deleted;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getMemberId() { return memberId; }
    public void setMemberId(Long memberId) { this.memberId = memberId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getSchedule() { return schedule; }
    public void setSchedule(String schedule) { this.schedule = schedule; }

    public LocalDate getScheduleDate() { return scheduleDate; }
    public void setScheduleDate(LocalDate scheduleDate) { this.scheduleDate = scheduleDate; }

    public Integer getScope() { return scope; }
    public void setScope(Integer scope) { this.scope = scope; }

    public String getWeather() { return weather; }
    public void setWeather(String weather) { this.weather = weather; }

    public byte[] getPicture() { return picture; }
    public void setPicture(byte[] picture) { this.picture = picture; }

    public String getCodiType() { return codiType; }
    public void setCodiType(String codiType) { this.codiType = codiType; }

    public String getDeleted() { return deleted; }
    public void setDeleted(String deleted) { this.deleted = deleted; }

    public List<Long> getClothesIds() { return clothesIds; }
    public void setClothesIds(List<Long> clothesIds) { this.clothesIds = clothesIds; }
}
