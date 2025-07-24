package com.samyukgu.what2wear.wardrobe.model;

public class Wardrobe {
    private Long id;
    private Long member_id;
    private Long category_id;
    private String name;
    private String memo;
    private String liked;
    private byte[] picture;
    private String keyword;
    private String item_size;
    private String color;
    private String brand;
    private String deleted;
    // 추가
    private String imagePath;

    public Wardrobe() {}

    // 모든 필드에 대한 getter & setter
    public void setId(Long id) { this.id = id; }
    public Long getId() { return id; }

    public void setMemberId(Long memberId) {
        this.member_id = memberId;     // 매개변수 memberId를 필드 member_id에 저장
    }
    public void setCategoryId(Long categoryId) {
        this.category_id = categoryId; // 매개변수 categoryId를 필드 category_id에 저장
    }

    public Long getMemberId() { return member_id; }

    public Long getCategoryId() { return category_id; }

    public void setName(String name) { this.name = name; }
    public String getName() { return name; }

    public void setMemo(String memo) { this.memo = memo; }
    public String getMemo() { return memo; }

    public void setLike(String like) { this.liked = like; }
    public String getLike() { return liked; }

    public void setPicture(byte[] picture) { this.picture = picture; }
    public byte[] getPicture() { return picture; }

    public void setKeyword(String keyword) { this.keyword = keyword; }
    public String getKeyword() { return keyword; }

    public void setSize(String item_size) { this.item_size = item_size; }
    public String getSize() { return item_size; }

    public void setColor(String color) { this.color = color; }
    public String getColor() { return color; }

    public void setBrand(String brand) { this.brand = brand; }
    public String getBrand() { return brand; }

    public void setDeleted(String deleted) { this.deleted = deleted; }
    public String getDeleted() { return deleted; }

    public Wardrobe(Long id, Long member_id, Long category_id, String name, String memo, String liked, byte[] picture,
                    String keyword, String item_size, String color, String brand, String deleted) {
        this.id = id;
        this.member_id = member_id;
        this.category_id = category_id;
        this.name = name;
        this.memo = memo;
        this.liked = liked;
        this.picture = picture;
        this.keyword = keyword;
        this.item_size = item_size;
        this.color = color;
        this.brand = brand;
        this.deleted = deleted;
    }

    public byte[] getImage() {
        return picture;
    }
}