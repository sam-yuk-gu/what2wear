package com.samyukgu.what2wear.codi.dto;

import com.samyukgu.what2wear.codi.model.CodiItem;
import com.samyukgu.what2wear.codi.model.CodiScope;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class CodiScheduleDTO {
    private Long codiId;
    private LocalDate date;
    private CodiScope visibility;
}
