package com.samyukgu.what2wear.myCodi.model;

import java.util.Date;

public class MyCodi {
    private Long id;
    private Long member_id;
    private String schedule;
    private Date create_at;
    private Long scope;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getMember_id() {
        return member_id;
    }

    public void setMember_id(Long member_id) {
        this.member_id = member_id;
    }

    public String getSchedule() {
        return schedule;
    }

    public void setSchedule(String schedule) {
        this.schedule = schedule;
    }

    public Date getCreate_at() {
        return create_at;
    }

    public void setCreate_at(Date create_at) {
        this.create_at = create_at;
    }

    public Long getScope() {
        return scope;
    }

    public void setScope(Long scope) {
        this.scope = scope;
    }

    public String getWeather() {
        return weather;
    }

    public void setWeather(String weather) {
        this.weather = weather;
    }

    public String getCodi_type() {
        return codi_type;
    }

    public void setCodi_type(String codi_type) {
        this.codi_type = codi_type;
    }

    private String weather;
    private String codi_type;

    public MyCodi(Long id, Long member_id, String schedule,
                  Date create_at, Long scope, String weather, String codi_type) {
        this.id = id;
        this.member_id = member_id;
        this.schedule = schedule;
        this.create_at = create_at;
        this.scope = scope;
        this.weather = weather;
        this.codi_type = codi_type;
    }

}
