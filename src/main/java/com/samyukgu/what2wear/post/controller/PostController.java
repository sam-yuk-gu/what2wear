package com.samyukgu.what2wear.post.controller;

import com.samyukgu.what2wear.di.DIContainer;
import com.samyukgu.what2wear.post.service.PostService;
import javafx.fxml.FXML;

public class PostController {
    private PostService postService;

    @FXML
    public void initialize(){
        DIContainer diContainer = DIContainer.getInstance();
        this.postService = diContainer.resolve(PostService.class);
    }
}
