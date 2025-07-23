package com.samyukgu.what2wear.codi.dto;

import com.samyukgu.what2wear.codi.model.CodiItem;
import com.samyukgu.what2wear.wardrobe.model.Wardrobe;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CodiDetailDTO {
    Long codiId;
    LocalDate scheduleDate;
    int scope;
    String scheduleName;
    List<Wardrobe> clothes;
}
