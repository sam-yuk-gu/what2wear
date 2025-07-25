package com.samyukgu.what2wear.post.service;

import com.samyukgu.what2wear.post.dao.PostDAO;
import com.samyukgu.what2wear.post.model.Post;
import javafx.geometry.Pos;

import java.util.ArrayList;
import java.util.List;

public class PostService {
    private final PostDAO dao;

    public PostService(PostDAO postDAO) {
        this.dao = postDAO;
    }

//    public List<LikePost> getPostsWithLikeCounts() {
//        List<Post> posts = dao.findAll();
//        List<LikePost> result = new ArrayList<>();
//
//        for (Post post : posts) {
//            // 게시글 ID로 좋아요 수 계산
//            int likeCount = likePostDAO.countLikesByPostId(post.getId().intValue());
//            // Post + 좋아요 수 묶어서 반환
//            result.add(new LikePost(post, likeCount));
//        }
//
//        return result;  // null 제거
//    }


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

    public List<Post> searchPost(String keyword, String type) {
        return dao.search(keyword, type);
    }
}