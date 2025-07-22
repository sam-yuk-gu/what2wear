package com.samyukgu.what2wear.postcomment.controller;

import com.samyukgu.what2wear.common.controller.CustomModalController;
import com.samyukgu.what2wear.di.DIContainer;
import com.samyukgu.what2wear.member.Session.MemberSession;
import com.samyukgu.what2wear.member.service.MemberService;
import com.samyukgu.what2wear.post.model.Post;
import com.samyukgu.what2wear.postcomment.dao.PostCommentDAO;
import com.samyukgu.what2wear.postcomment.model.PostComment;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import lombok.Setter;

import java.io.IOException;
import java.util.List;

public class CommentItemController {
    @FXML
    private StackPane root;
    @FXML private Label comment_author;
    @FXML private Label comment_date;
    @FXML private Label commentTextLabel;
    @Setter
    @FXML private VBox commentBox;

    @FXML private javafx.scene.control.TextField editTextField;

    // 회원 세션
    private MemberService memberService;
    private MemberSession memberSession;

    private Post currentPost;
    private Long comment_id;



    public void setComment(Long id, String author, String date, String content) {
        this.comment_id = id;
        comment_author.setText(author);
        comment_date.setText(date);
        commentTextLabel.setText(content);
    }

    @Setter
    private Long commentId;

    @FXML
    private void initialize() {
        // 회원 정보 불러오기
        setupDI();

        // 댓글 수정 필드 비활성화
        editTextField.setVisible(false);
    }

    // 회원 정보 불러오기
    private void setupDI() {
        DIContainer diContainer = DIContainer.getInstance();
        memberService = diContainer.resolve(MemberService.class);
        memberSession = diContainer.resolve(MemberSession.class);
    }

    // 댓글 조회하기
    private void loadComments() {
        PostCommentDAO commentDAO = DIContainer.getInstance().resolve(PostCommentDAO.class);
        List<PostComment> comments = commentDAO.findByPostId(currentPost.getId());

//        commentBox.getChildren().clear();

        for (PostComment comment : comments) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/samyukgu/what2wear/postcomment/CommentItem.fxml"));
                HBox commentItem = loader.load();

                CommentItemController controller = loader.getController();
                controller.setComment(
                        comment.getId(),
                        "사용자" + comment.getMemberId(), // 실제 구현 시 memberService로 이름 매핑 가능
                        comment.getCreatedAt().toString(),
                        comment.getContent()
                );

                //                commentBox.getChildren().add(commentItem);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    // 댓글 수정 적용하기
    private void applyEdit() {
        String newText = editTextField.getText();
        if (newText != null && !newText.isEmpty()) {
            commentTextLabel.setText(newText);
        }
        editTextField.setVisible(false);
        commentTextLabel.setVisible(true);
    }

    // 댓글 수정하기
    @FXML
    private void handleEditClick() {
        editTextField.setText(commentTextLabel.getText());
        commentTextLabel.setVisible(false);
        editTextField.setVisible(true);
        editTextField.requestFocus();
        editTextField.positionCaret(editTextField.getText().length());
        editTextField.setOnAction(event -> applyEdit());
    }

    // 댓글 삭제 클릭 시 모달창 띄우기
    public void handleDeleteClick(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/samyukgu/what2wear/common/CustomModal.fxml"));
            StackPane modal = loader.load();

            CustomModalController controller = loader.getController();
            controller.configure(
                    "댓글을 삭제하시겠습니까?",
                    "삭제된 댓글 정보는 저장되지 않습니다.",
                    "/assets/icons/redCheck.png",
                    "#FA7B7F",
                    "취소",
                    "확인",
                    () -> root.getChildren().remove(modal),
                    () -> {
                        // DB 삭제
                        PostCommentDAO commentDAO = DIContainer.getInstance().resolve(PostCommentDAO.class);
                        commentDAO.delete(comment_id);

                        // 모달 제거
                        root.getChildren().remove(modal);

                        // 화면에서 댓글 제거
                        // 3. 댓글 UI 제거
                        if (commentBox != null) {
                            ((Pane) commentBox.getParent()).getChildren().remove(commentBox);
                        }
                    }
            );

            root.getChildren().add(modal);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setComment(PostComment comment) {
        setComment(
                comment.getId(),
                "사용자" + comment.getMemberId(),
                comment.getCreatedAt().toString(),
                comment.getContent()
        );
    }

}