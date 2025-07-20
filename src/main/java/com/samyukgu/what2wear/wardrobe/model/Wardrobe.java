package com.samyukgu.what2wear.wardrobe.model;

public class Wardrobe {
    private Long id;
    private Long member_id;
    private Long category_id;
    private String name;
    private String memo;
    private String like; // 'Y' or 'N'
    private byte[] picture;
    private String keyword;
    private String size;
    private String color;
    private String brand;
    private String deleted; // 'Y' or 'N'

    public Long getId() {
        return id;
    }

    public Long getMember_id() {
        return member_id;
    }

    public Long getCategory_id() {
        return category_id;
    }

    public String getName() {
        return name;
    }

    public String getMemo() {
        return memo;
    }

    public String getLike() {
        return like;
    }

    public byte[] getPicture() {
        return picture;
    }

    public String getKeyword() {
        return keyword;
    }

    public String getSize() {
        return size;
    }

    public String getColor() {
        return color;
    }

    public String getBrand() {
        return brand;
    }

    public String getDeleted() {
        return deleted;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setMember_id(Long member_id) {
        this.member_id = member_id;
    }

    public void setCategory_id(Long category_id) {
        this.category_id = category_id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setMemo(String memo) {
        this.memo = memo;
    }

    public void setLike(String like) {
        this.like = like;
    }

    public void setPicture(byte[] picture) {
        this.picture = picture;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public void setDeleted(String deleted) {
        this.deleted = deleted;
    }

    public Wardrobe(Long id, Long member_id, Long category_id, String name, String memo, String like, byte[] picture,
                    String keyword, String size, String color, String brand, String deleted) {
        this.id = id;
        this.member_id = member_id;
        this.category_id = category_id;
        this.name = name;
        this.memo = memo;
        this.like = like;
        this.picture = picture;
        this.keyword = keyword;
        this.size = size;
        this.color = color;
        this.brand = brand;
        this.deleted = deleted;
    }
}