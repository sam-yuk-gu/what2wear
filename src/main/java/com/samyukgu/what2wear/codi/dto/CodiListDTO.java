package com.samyukgu.what2wear.codi.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CodiListDTO {
    LocalDate scheduleDate;
    List<CodiDTO> codiList;
}
