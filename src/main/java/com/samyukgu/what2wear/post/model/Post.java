package com.samyukgu.what2wear.post.model;

import java.util.Date;

public class Post {
    private Long id;
    private Long member_id;
    private Long cody_id;
    private String title;
    private String content;
    private Date create_at;
    private Date last_updated;
    private int like_count;

    // member의 id와 일치하는 작성자 이름
    private String writer_name;

    // getter 메서드
    public Long getId() {
        return id;
    }
    public Long getMember_id() {
        return member_id;
    }

    public Long getCody_id() {
        return cody_id;
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    public Date getCreate_at() {
        return create_at;
    }

    public Date getLast_updated() {
        return last_updated;
    }

    public int getLike_count() {
        return like_count;
    }

    public String getWriter_name() {
        return writer_name;
    }

    // setter 메서드
    public void setId(Long id) {
        this.id = id;
    }

    public void setMember_id(Long member_id) {
        this.member_id = member_id;
    }

    public void setCody_id(Long cody_id) {
        this.cody_id = cody_id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setCreate_at(Date create_at) {
        this.create_at = create_at;
    }

    public void setLast_updated(Date last_updated) {
        this.last_updated = last_updated;
    }

    public void setLike_count(int like_count) {
        this.like_count = like_count;
    }
    public void setWriter_name(String writer_name) {
        this.writer_name = writer_name;
    }

    // 생성자 (writer_name 없이)
    public Post(Long id, Long member_id, Long cody_id, String title, String content, Date create_at, Date last_updated, int like_count) {
        this.id = id;
        this.member_id = member_id;
        this.cody_id = cody_id;
        this.title = title;
        this.content = content;
        this.create_at = create_at;
        this.last_updated = last_updated;
        this.like_count = like_count;
    }

    // 생성자 (writer_name 포함, 조회용)
    public Post(Long id, Long member_id, Long cody_id, String title, String content,
                Date create_at, Date last_updated, int like_count, String writer_name) {
        this(id, member_id, cody_id, title, content, create_at, last_updated, like_count);
        this.writer_name = writer_name;
    }
}


