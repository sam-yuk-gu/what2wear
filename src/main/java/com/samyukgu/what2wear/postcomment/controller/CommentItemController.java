package com.samyukgu.what2wear.postcomment.controller;

import com.samyukgu.what2wear.common.controller.CustomModalController;
import com.samyukgu.what2wear.common.util.CircularImageUtil;
import com.samyukgu.what2wear.di.DIContainer;
import com.samyukgu.what2wear.member.Session.MemberSession;
import com.samyukgu.what2wear.member.model.Member;
import com.samyukgu.what2wear.member.service.MemberService;
import com.samyukgu.what2wear.postcomment.dao.PostCommentDAO;
import com.samyukgu.what2wear.postcomment.model.PostComment;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import lombok.Setter;
import java.io.ByteArrayInputStream;

public class CommentItemController {

    @FXML private StackPane root;
    @FXML private VBox commentBox;
    @FXML private Label comment_author;
    @FXML private Label comment_date;
    @FXML private Label commentTextLabel;
    @FXML private TextField editTextField;
    @FXML private ImageView profileImageView;

    @Setter private Long commentId;

    private MemberService memberService;
    private MemberSession memberSession;

    private Long memberId; // 댓글 작성자 ID
    private Long comment_id;

    @FXML
    private void initialize() {
        setupDI();
        editTextField.setVisible(false);
    }

    private void setupDI() {
        DIContainer diContainer = DIContainer.getInstance();
        memberService = diContainer.resolve(MemberService.class);
        memberSession = diContainer.resolve(MemberSession.class);
    }

    public void setComment(Long id, String date, String content, Long writerId) {
        this.comment_id = id;
        this.memberId = writerId;
        comment_date.setText(date);
        commentTextLabel.setText(content);

        Member writer = memberSession.getMember();
        if (writer != null) {
            comment_author.setText(writer.getNickname());
            byte[] imgByte = writer.getProfile_img();
            CircularImageUtil.applyCircularImageToExistingImageView(profileImageView, 64.0, imgByte);
        } else {
            comment_author.setText("알 수 없음");
            profileImageView.setImage(new Image(getClass().getResourceAsStream("/assets/icons/defaultProfile.png")));
        }
    }

    public void setComment(PostComment comment) {
        setComment(
                comment.getId(),
                comment.getCreatedAt().toString(),
                comment.getContent(),
                comment.getMemberId()
        );
    }

    private void applyEdit() {
        String newText = editTextField.getText();
        if (newText != null && !newText.isEmpty()) {
            commentTextLabel.setText(newText);

            // DB 반영
            PostCommentDAO commentDAO = DIContainer.getInstance().resolve(PostCommentDAO.class);
            PostComment updatedComment = new PostComment();
            updatedComment.setId(comment_id);
            updatedComment.setContent(newText);
            commentDAO.update(updatedComment);
        }

        editTextField.setVisible(false);
        commentTextLabel.setVisible(true);
    }

    @FXML
    private void handleEditClick() {
        editTextField.setText(commentTextLabel.getText());
        commentTextLabel.setVisible(false);
        editTextField.setVisible(true);
        editTextField.requestFocus();
        editTextField.positionCaret(editTextField.getText().length());
        editTextField.setOnAction(event -> applyEdit());
    }

    public void handleDeleteClick(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/samyukgu/what2wear/common/CustomModal.fxml"));
            StackPane modal = loader.load();

            PostCommentDAO commentDAO = DIContainer.getInstance().resolve(PostCommentDAO.class);
            commentDAO.delete(comment_id);

            root.getChildren().remove(modal);
            if (root.getParent() instanceof Pane parentPane) {
                parentPane.getChildren().remove(root);
            }

            root.getChildren().add(modal);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
