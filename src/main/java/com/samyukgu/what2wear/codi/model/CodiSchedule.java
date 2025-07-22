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
}