package com.samyukgu.what2wear.post.controller;

import com.samyukgu.what2wear.common.controller.CustomModalController;
import com.samyukgu.what2wear.common.controller.MainLayoutController;
import com.samyukgu.what2wear.common.controller.PostHeaderController;
import com.samyukgu.what2wear.post.model.Post;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

public class DetailPostController {

    @FXML private StackPane root;
    @FXML private PostHeaderController header_paneController;
    @FXML private Label titleLabel;
    @FXML private Label contentLabel;
    @FXML private Label authorLabel;
    @FXML private Label dateLabel;
    @FXML private Button likeButton;
    @FXML private ImageView likeIcon;
    @FXML private Label likesLabel;
    @FXML private Label commentTextLabel;
    @FXML private TextField editTextField;
    @FXML private VBox commentBox;
    @FXML private Button editPostButton;
    @FXML private Button deletePostButton;

    private int likeCount;
    private boolean isLiked = false;
    private Post currentPost;
    private final int CURRENT_USER_ID = 101;

    @FXML
    private void initialize() {
        hideEditDeleteButtons();

        header_paneController.setTitle("게시글 조회");
        header_paneController.setOnBackAction(() ->
                MainLayoutController.loadView("/com/samyukgu/what2wear/post/ListPost.fxml")
        );

        editTextField.setVisible(false);

        editPostButton.setOnAction(event -> handlePostEditClick());
        deletePostButton.setOnAction(event -> handlePostDeleteClick());

        likeButton.setOnMouseEntered(e -> likeButton.setStyle("-fx-scale-x: 1.1; -fx-scale-y: 1.1;"));
        likeButton.setOnMouseExited(e -> likeButton.setStyle("-fx-scale-x: 1.0; -fx-scale-y: 1.0;"));
    }

    public void setPostData(Post post) {
        this.currentPost = post;
        displayPostContent(post);


        // 추가
        System.out.println("현재 사용자 ID = " + CURRENT_USER_ID);
        System.out.println("게시글 작성자 ID = " + post.getMember_id());

        checkAndShowButtons(post);
    }

    private void displayPostContent(Post post) {
        titleLabel.setText(post.getTitle());
        contentLabel.setText(post.getContent());
        authorLabel.setText(post.getMember_id().toString());
        dateLabel.setText(post.getCreate_at().toString());

        likeCount = 0;  // 임시 설정, 실제 DB 값 사용 가능
        likesLabel.setText(String.valueOf(likeCount));
    }

    private void checkAndShowButtons(Post post) {
        if (post.getMember_id() != null && post.getMember_id().equals(CURRENT_USER_ID)) {
            showEditDeleteButtons();
        } else {
            hideEditDeleteButtons();
        }
    }

    private void showEditDeleteButtons() {
        System.out.println("editPostButton is null? " + (editPostButton == null));
        System.out.println("deletePostButton is null? " + (deletePostButton == null));

        editPostButton.setVisible(true);
        deletePostButton.setVisible(true);
        editPostButton.setManaged(true);
        deletePostButton.setManaged(true);
    }


    private void hideEditDeleteButtons() {
        editPostButton.setVisible(false);
        deletePostButton.setVisible(false);
        editPostButton.setManaged(false);
        deletePostButton.setManaged(false);
    }

    @FXML
    private void handleLikeClick() {
        isLiked = !isLiked;
        if (isLiked) {
            likeCount++;
            likeIcon.setImage(new Image(getClass().getResourceAsStream("/assets/icons/fillHeart.png")));
        } else {
            likeCount--;
            likeIcon.setImage(new Image(getClass().getResourceAsStream("/assets/icons/emptyHeart.png")));
        }
        likesLabel.setText(String.valueOf(likeCount));
    }

    @FXML
    private void handleBack() {
        MainLayoutController.loadView("/com/samyukgu/what2wear/post/ListPost.fxml");
    }

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
                        root.getChildren().remove(modal);
                        ((VBox) commentBox.getParent()).getChildren().remove(commentBox);
                    }
            );

            root.getChildren().add(modal);
        } catch (Exception e) {
            e.printStackTrace();
        }
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

    private void applyEdit() {
        String newText = editTextField.getText();
        if (newText != null && !newText.isEmpty()) {
            commentTextLabel.setText(newText);
        }
        editTextField.setVisible(false);
        commentTextLabel.setVisible(true);
    }

    public void handlePostEditClick() {
        MainLayoutController.loadView("/com/samyukgu/what2wear/post/EditPost.fxml");
    }

    public void handlePostDeleteClick() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/samyukgu/what2wear/common/CustomModal.fxml"));
            StackPane modal = loader.load();

            CustomModalController controller = loader.getController();
            controller.configure(
                    "게시글을 삭제하시겠습니까?",
                    "삭제된 게시글 정보는 저장되지 않습니다.",
                    "/assets/icons/redCheck.png",
                    "#FA7B7F",
                    "취소",
                    "확인",
                    () -> root.getChildren().remove(modal),
                    () -> {
                        root.getChildren().remove(modal);
                        MainLayoutController.loadView("/com/samyukgu/what2wear/post/ListPost.fxml");
                        // TODO: 게시글 삭제 로직 구현 필요
                    }
            );

            root.getChildren().add(modal);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
