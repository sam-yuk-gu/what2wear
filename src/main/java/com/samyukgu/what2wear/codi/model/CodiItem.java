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
    private Long id;

    public CodiItem (String category, String name, String imagePath) {
        this.category = category;
        this.name = name;
        this.imagePath = imagePath;
    }

    public CodiItem (Long id, String name, String imagePath) {
        this.id = id;
        this.name = name;
        this.imagePath = imagePath;
    }
}