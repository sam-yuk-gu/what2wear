package com.samyukgu.what2wear.post.service;

import com.samyukgu.what2wear.post.dao.PostDAO;
import com.samyukgu.what2wear.post.model.Post;

import java.util.List;

public class PostService {
    private final PostDAO dao;

    public PostService(PostDAO dao) {
        this.dao = dao;
    }

    public Post getPost(Long id) {
        return dao.findById(id);
    }

    public List<Post> getAllPosts() {
        return dao.findAll();
    }

    public void createPost(Post post) {
        dao.create(post);
    }

    public void updatePost(Post post) {
        dao.update(post);
    }

    public void deletePost(Long id) {
        dao.delete(id);
    }
}