package com.samyukgu.what2wear.postcomment.service;

import com.samyukgu.what2wear.postcomment.dao.PostCommentDAO;
import com.samyukgu.what2wear.postcomment.dao.PostCommentOracleDAO;
import com.samyukgu.what2wear.postcomment.model.PostComment;
import java.util.List;

// 작성자 : 오수경
public class PostCommentService {

    private final PostCommentDAO dao = new PostCommentOracleDAO();
    List<PostComment> findAll() {
        return null;
    }
    public void createPostComment(PostComment comment) {
        dao.create(comment);
    }
    public void updatePostComment(PostComment comment) {
        dao.update(comment);
    }
    public void deletePostComment(Long id) {
        dao.delete(id);
    }
    public int countByPostId(Long postId) {
        return dao.countByPostId(postId);
    }

}
