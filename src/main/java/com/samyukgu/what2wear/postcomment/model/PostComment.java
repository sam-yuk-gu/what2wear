package com.samyukgu.what2wear.postcomment.model;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class PostComment {
    private Long id;
    private Long postId;
    private Long memberId;
    private String content;
    private Date createdAt;

    public PostComment(Long id, Long postId, Long memberId, String content, Date createdAt) {
        this.id = id;
        this.postId = postId;
        this.memberId = memberId;
        this.content = content;
        this.createdAt = createdAt;
    }

    public PostComment() {

    }
}

