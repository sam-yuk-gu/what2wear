package com.samyukgu.what2wear.likePost.dao;

// 작성자 : 오수경
public interface LikePostDAO {
    int countLikesByPostId(int postId);     // 게시글 별 좋아요 수 조회

    void like(Long postId, Long memberId);

    void unlike(Long postId, Long memberId);

    boolean isAlreadyLiked(Long postId, Long memberId);
}
