package com.samyukgu.what2wear.codi.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CodiDTO {
    String scope;
    String scheduleName;
    List<CodiClothesDTO> codiClothesList;
    Long codiId;

    public CodiDTO (String scope, String scheduleName, List<CodiClothesDTO> codiClothesList) {
        this.scope = scope;
        this.scheduleName = scheduleName;
        this.codiClothesList = codiClothesList;
    }
}

