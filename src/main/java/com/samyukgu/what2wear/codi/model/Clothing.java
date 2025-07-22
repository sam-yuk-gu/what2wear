package com.samyukgu.what2wear.codi.model;

import javafx.scene.image.Image;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Clothing {
    private Long id;
    private String name;
    private String category;
    private Image imagePath; // or URL
}