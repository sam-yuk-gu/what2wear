package com.samyukgu.what2wear.codi.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CodiClothesDTO {
    private String categoryName;
    private String clothesName;
    private byte[] clothesPicture;
}
