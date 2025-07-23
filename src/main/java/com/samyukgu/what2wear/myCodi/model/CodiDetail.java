package com.samyukgu.what2wear.myCodi.model;

public class CodiDetail {
    private Long codiId;
    private Long clothesId;

    public CodiDetail() {}

    public CodiDetail(Long codiId, Long clothesId) {
        this.codiId = codiId;
        this.clothesId = clothesId;
    }

    public Long getCodiId() { return codiId; }
    public void setCodiId(Long codiId) { this.codiId = codiId; }

    public Long getClothesId() { return clothesId; }
    public void setClothesId(Long clothesId) { this.clothesId = clothesId; }
}
