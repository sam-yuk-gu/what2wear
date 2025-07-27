package com.samyukgu.what2wear.post.dao;

import com.samyukgu.what2wear.post.model.Post;
import java.util.List;

// 작성자 : 오수경
public interface PostDAO {
    Post findById(Long id);     // 게시글 상세 조회
    List<Post> findAll(Long currentMemberId);   // 게시글 전체 조회 (좋아요 포함)
    void create(Post post);   // 게시글 등록
    void update(Post post);   // 게시글 수정
    void delete(Long id);   // 게시글 삭제
    List<Post> search(String keyword, String type);
    void likePost(Long postId, Long memberId);  // 좋아요 등록
}
