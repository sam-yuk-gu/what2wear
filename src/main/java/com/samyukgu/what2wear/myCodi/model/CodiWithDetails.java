package com.samyukgu.what2wear.myCodi.model;

import com.samyukgu.what2wear.wardrobe.model.Wardrobe;

import java.time.LocalDate;
import java.util.List;

public class CodiWithDetails {
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
    private LocalDate createdAt; // ⭐ 추가된 필드
    private List<Wardrobe> clothes; // 코디에 포함된 옷들

    public CodiWithDetails() {
    }

    // 전체 생성자
    public CodiWithDetails(Long id, Long memberId, String name, String schedule, LocalDate scheduleDate,
                           Integer scope, String weather, byte[] picture, String codiType, String deleted,
                           LocalDate createdAt, List<Wardrobe> clothes) {
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
        this.createdAt = createdAt;
        this.clothes = clothes;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getMemberId() {
        return memberId;
    }

    public void setMemberId(Long memberId) {
        this.memberId = memberId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSchedule() {
        return schedule;
    }

    public void setSchedule(String schedule) {
        this.schedule = schedule;
    }

    public LocalDate getScheduleDate() {
        return scheduleDate;
    }

    public void setScheduleDate(LocalDate scheduleDate) {
        this.scheduleDate = scheduleDate;
    }

    public Integer getScope() {
        return scope;
    }

    public void setScope(Integer scope) {
        this.scope = scope;
    }

    public String getWeather() {
        return weather;
    }

    public void setWeather(String weather) {
        this.weather = weather;
    }

    public byte[] getPicture() {
        return picture;
    }

    public void setPicture(byte[] picture) {
        this.picture = picture;
    }

    public String getCodiType() {
        return codiType;
    }

    public void setCodiType(String codiType) {
        this.codiType = codiType;
    }

    public String getDeleted() {
        return deleted;
    }

    public void setDeleted(String deleted) {
        this.deleted = deleted;
    }

    // ⭐ 추가된 getter/setter
    public LocalDate getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDate createdAt) {
        this.createdAt = createdAt;
    }

    public List<Wardrobe> getClothes() {
        return clothes;
    }

    public void setClothes(List<Wardrobe> clothes) {
        this.clothes = clothes;
    }

    // 편의 메서드들
    public int getClothesCount() {
        return clothes != null ? clothes.size() : 0;
    }

    public boolean hasClothes() {
        return clothes != null && !clothes.isEmpty();
    }

    // toString 메서드 (디버깅용)
    @Override
    public String toString() {
        return "CodiWithDetails{" +
                "id=" + id +
                ", memberId=" + memberId +
                ", name='" + name + '\'' +
                ", schedule='" + schedule + '\'' +
                ", scheduleDate=" + scheduleDate +
                ", scope=" + scope +
                ", weather='" + weather + '\'' +
                ", codiType='" + codiType + '\'' +
                ", deleted='" + deleted + '\'' +
                ", createdAt=" + createdAt +
                ", clothesCount=" + getClothesCount() +
                '}';
    }
}