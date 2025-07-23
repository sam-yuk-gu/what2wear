package com.samyukgu.what2wear.codi.model;

import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CodiSchedule {
    private LocalDate date;
    private String description;
    private CodiScope visibility;
    private List<CodiItem> codiItems;
    private Long codiId;

    public CodiSchedule(LocalDate date, String description, CodiScope visibility) {
        this.date = date;
        this.description = description;
        this.visibility = visibility;
    }

    public CodiSchedule(LocalDate date, String description, CodiScope visibility, List<CodiItem> codiItems) {
        this.date = date;
        this.description = description;
        this.visibility = visibility;
        this.codiItems = codiItems;
    }

    public CodiSchedule (Long codiId, LocalDate date, CodiScope visibility) {
        this.codiId = codiId;
        this.date = date;
        this.visibility = visibility;
    }
}