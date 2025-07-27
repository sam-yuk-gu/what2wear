package com.samyukgu.what2wear.likePost.model;

import com.samyukgu.what2wear.post.model.Post;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;

public class LikePost {
    private Post post;

    public LikePost(Post post, int likeCount) {
        this.post = post;
    }

    public String getTitle() {
        return post.getTitle();
    }

    public String getContent() {
        return post.getContent();
    }

    public Long getWriter() {
        return post.getMember_id();
    }
}
