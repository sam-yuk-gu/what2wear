package com.samyukgu.what2wear.codi.model;

public class CodiItem {
    private String category;
    private String name;
    private String imagePath;

    public CodiItem() {}

    public CodiItem(String category, String name, String imagePath) {
        this.category = category;
        this.name = name;
        this.imagePath = imagePath;
    }

    // getters, setters
    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }
}