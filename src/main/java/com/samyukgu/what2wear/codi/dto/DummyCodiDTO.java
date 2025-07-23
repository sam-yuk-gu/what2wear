package com.samyukgu.what2wear.codi.dto;

import com.samyukgu.what2wear.codi.model.CodiItem;
import com.samyukgu.what2wear.codi.model.CodiScope;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class DummyCodiDTO {
    private String description;
    private LocalDate date;
    private List<CodiItem> codiItems;
    private CodiScope visibility;
}
