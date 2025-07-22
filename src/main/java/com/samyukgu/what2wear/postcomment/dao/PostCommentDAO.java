package com.samyukgu.what2wear.postcomment.dao;

import com.samyukgu.what2wear.post.model.Post;
import com.samyukgu.what2wear.postcomment.model.PostComment;

import java.util.List;

public interface PostCommentDAO {
    List<Post> findAll();   // 게시글 댓글 전체 조회

    void create(PostComment postComment);   // 게시글 댓글 등록
    void update(PostComment postComment);   // 게시글 댓글 수정
    void delete(Long id);   // 게시글 댓글 삭제
}
