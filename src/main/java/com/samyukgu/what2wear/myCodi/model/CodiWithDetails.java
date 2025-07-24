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
    private List<Wardrobe> clothes; // 코디에 포함된 옷들

    public CodiWithDetails() {
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

    public List<Wardrobe> getClothes() {
        return clothes;
    }

    public void setClothes(List<Wardrobe> clothes) {
        this.clothes = clothes;
    }
}
