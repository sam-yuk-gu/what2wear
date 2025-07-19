package com.samyukgu.what2wear.codi.model;

import java.time.LocalDate;
import java.util.List;

public class CodiSchedule {
    private LocalDate date;
    private String description;
    private List<CodiItem> codiItems;
    private ScheduleVisibility visibility;

    // 기본 생성자
    public CodiSchedule() {}

    public CodiSchedule(String description, List<CodiItem> codiItems) {
        this.description = description;
        this.codiItems = codiItems;
    }

    public CodiSchedule(String description, LocalDate date, List<CodiItem> codiItems, ScheduleVisibility visibility) {
        this.date = date;
        this.description = description;
        this.codiItems = codiItems;
        this.visibility = visibility;
    }

    // getters, setters
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<CodiItem> getCodiItems() {
        return codiItems;
    }

    public void setCodiItems(List<CodiItem> codiItems) {
        this.codiItems = codiItems;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public ScheduleVisibility getVisibility() {
        return visibility;
    }

    public void setVisibility(ScheduleVisibility visibility) {
        this.visibility = visibility;
    }
}