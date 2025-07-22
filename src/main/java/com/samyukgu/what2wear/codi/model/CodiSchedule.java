package com.samyukgu.what2wear.codi.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;
import java.util.List;

@Getter
@AllArgsConstructor
public class CodiSchedule {
    private String description;
    private LocalDate date;
    private List<CodiItem> codiItems;
    private CodiScope visibility;
}