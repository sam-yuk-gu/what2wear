package com.samyukgu.what2wear.likePost.model;

import com.samyukgu.what2wear.post.model.Post;

// 작성자 : 오수경
public class LikePost {
    private Post post;

    public String getTitle() {
        return post.getTitle();
    }
    public String getContent() {
        return post.getContent();
    }
}
