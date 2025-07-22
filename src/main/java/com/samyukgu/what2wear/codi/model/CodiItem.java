package com.samyukgu.what2wear.codi.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CodiItem {
    private String category;
    private String name;
    private String imagePath;
}