package com.samyukgu.what2wear.codi.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class Clothing {
    private String id;
    private String name;
    private String category;
    private String imagePath; // or URL
}