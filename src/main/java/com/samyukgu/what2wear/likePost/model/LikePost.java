package com.samyukgu.what2wear.likePost.model;

import com.samyukgu.what2wear.post.model.Post;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;

public class LikePost {
    private Post post;
    private IntegerProperty likeCount;

    public LikePost(Post post, int likeCount) {
        this.post = post;
        this.likeCount = new SimpleIntegerProperty(likeCount);
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

    public  IntegerProperty getLikeCount() {
        return likeCount;
    }
}
