package com.samyukgu.what2wear.post.service;

import com.samyukgu.what2wear.likePost.dao.LikePostDAO;
import com.samyukgu.what2wear.post.dao.PostDAO;
import com.samyukgu.what2wear.post.model.Post;
import javafx.geometry.Pos;

import java.util.ArrayList;
import java.util.List;

public class PostService {
    private final PostDAO dao;
    private final LikePostDAO likePostDAO;

    public PostService(PostDAO postDAO, LikePostDAO likePostDAO) {
        this.dao = postDAO;
        this.likePostDAO = likePostDAO;
    }

    public Post getPost(Long id) {
        return dao.findById(id);
    }

    public List<Post> getAllPosts(Long currentMemberId) {
        return dao.findAll(currentMemberId);
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

    public List<Post> searchPost(String keyword, String type) {
        return dao.search(keyword, type);
    }

    public void likePost(Long postId, Long memberId) {
        if (likePostDAO.isAlreadyLiked(postId, memberId)) {
            likePostDAO.unlike(postId, memberId); // 좋아요 취소
        } else {
            likePostDAO.like(postId, memberId);   // 좋아요 추가
        }
    }

}