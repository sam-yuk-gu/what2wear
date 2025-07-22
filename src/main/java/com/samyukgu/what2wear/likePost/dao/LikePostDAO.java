package com.samyukgu.what2wear.likePost.dao;

public interface LikePostDAO {
    int countLikesByPostId(int postId);     // 게시글 별 좋아요 수 조회
}
