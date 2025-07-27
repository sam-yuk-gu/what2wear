package com.samyukgu.what2wear.likePost.model;

import com.samyukgu.what2wear.post.model.Post;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;

public class LikePost {
    private Post post;

    public String getTitle() {
        return post.getTitle();
    }
    public String getContent() {
        return post.getContent();
    }
}
