package com.samyukgu.what2wear.member.model;

public class Member {
    Long id;
    String account_id;
    String email;
    String name;
    String password;
    byte[] profile_img;
    String deleted;
    Long count;

    public Long getId() {
        return id;
    }

    public String getAccount_id() {
        return account_id;
    }

    public String getEmail() {
        return email;
    }

    public String getName() {
        return name;
    }

    public String getPassword() {
        return password;
    }

    public byte[] getProfile_img() {
        return profile_img;
    }

    public String getDeleted() {
        return deleted;
    }

    public Long getCount() {
        return count;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setAccount_id(String account_id) {
        this.account_id = account_id;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setProfile_img(byte[] profile_img) {
        this.profile_img = profile_img;
    }

    public void setDeleted(String deleted) {
        this.deleted = deleted;
    }

    public void setCount(Long count) {
        this.count = count;
    }

    public Member(Long id, String account_id, String email, String name, String password, byte[] profile_img,
                  String deleted,
                  Long count) {
        this.id = id;
        this.account_id = account_id;
        this.email = email;
        this.name = name;
        this.password = password;
        this.profile_img = profile_img;
        this.deleted = deleted;
        this.count = count;
    }
}
