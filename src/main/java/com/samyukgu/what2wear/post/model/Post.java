package com.samyukgu.what2wear.post.model;

import lombok.Getter;
import lombok.Setter;
import java.util.Date;

// 작성자 : 오수경
@Getter
@Setter
public class Post {
    private Long id;
    private Long member_id;
    private Long cody_id;
    private String title;
    private String content;
    private String writer_name;     // member의 id와 일치하는 작성자 이름
    private Date create_at;
    private Date last_updated;
    private int like_count;
    private boolean liked;   // 로그인한 유저가 좋아요 눌렀는지 여부

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
        this.liked = liked;
    }

    // 생성자 (writer_name 포함, 조회용)
    public Post(Long id, Long member_id, Long cody_id, String title, String content,
                Date create_at, Date last_updated, int like_count, String writer_name) {
        this(id, member_id, cody_id, title, content, create_at, last_updated, like_count);
        this.writer_name = writer_name;
    }
}


