package com.samyukgu.what2wear.post.controller;

import com.samyukgu.what2wear.common.controller.CustomModalController;
import com.samyukgu.what2wear.common.controller.MainLayoutController;
import com.samyukgu.what2wear.post.model.Post;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

public class DetailPostController {

    @FXML private StackPane root;
    @FXML private Label titleLabel;     // 게시글 제목
    @FXML private Label contentLabel;   // 게시글 내용
    @FXML private Label authorLabel;    // 게시글 작성자
    @FXML private Label dateLabel;      // 게시글 작성일
    @FXML private Button likeButton;    // 좋아요 버튼
    @FXML private ImageView likeIcon;   // 좋아요 아이콘
    @FXML private Label likesLabel;     // 좋아요 수


    @FXML private Label commentTextLabel;      // 원래 댓글 내용
    @FXML private TextField editTextField;     // 수정용 입력창

    @FXML private VBox commentBox; // 댓글 전체 HBox (삭제 대상)

    private int likeCount;      // 좋아요 카운트
    private boolean isLiked = false;       // 기본: false

    // Post Model 게시글 데이터로 설정
    public void setPost(Post post) {
        titleLabel.setText(post.getTitle());
        contentLabel.setText(post.getContent());
        authorLabel.setText(post.getAuthor());
        dateLabel.setText(post.getDate());

        // 게시글 좋아요 수로 설정
        likeCount = post.getLikes();
        likesLabel.setText(String.valueOf(likeCount));
    }
    
    @FXML
    private void initialize() {
        // editTextField는 숨김 처리
        editTextField.setVisible(false);

        // 초기 hover 효과 및 리스너
        likeButton.setOnMouseEntered(e -> likeButton.setStyle("-fx-scale-x: 1.1; -fx-scale-y: 1.1;"));
        likeButton.setOnMouseExited(e -> likeButton.setStyle("-fx-scale-x: 1.0; -fx-scale-y: 1.0;"));
    }

    // 하트 버튼 클릭 시 변화하는 이벤트 메서드
    @FXML
    private void handleLikeClick() {
        isLiked = !isLiked;
        if (isLiked) {
            likeCount++; // 홀수번 클릭 시 1 증가
            likeIcon.setImage(new Image(getClass().getResourceAsStream("/assets/images/fill_heart_icon.png")));
        } else {
            likeCount--; // 짝수번 클릭 시 1 감소
            likeIcon.setImage(new Image(getClass().getResourceAsStream("/assets/images/empty_heart_icon.png")));
        }
        likesLabel.setText(String.valueOf(likeCount));
    }

    // 뒤로가기 메서드
    @FXML
    private void handleBack() {
        MainLayoutController.loadView("/com/samyukgu/what2wear/post/ListPost.fxml");
    }

    // 댓글 삭제 모달창 띄우는 메서드
    public void handleDeleteClick(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/samyukgu/what2wear/common/CustomModal.fxml"));
            StackPane modal = loader.load();

            CustomModalController controller = loader.getController();
            controller.configure(
                    "댓글을 삭제하시겠습니까?",
                    "삭제된 댓글 정보는 저장되지 않습니다.",
                    "/assets/images/red_check_icon.png",
                    "#FA7B7F",
                    "취소",
                    "확인",
                    () -> root.getChildren().remove(modal),  // 취소 시 모달 제거
                    () -> {
                        root.getChildren().remove(modal);  // 확인 시 모달 제거
                        // 댓글 영역에서 해당 댓글(HBox)를 제거
                        ((VBox) commentBox.getParent()).getChildren().remove(commentBox);
                    }
            );

            root.getChildren().add(modal);  // 모달 화면 위에 덮기

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 댓글 수정 메서드
    @FXML
    private void handleEditClick() {
        // 댓글 내용을 TextField로 옮기기
        editTextField.setText(commentTextLabel.getText());

        // Label 숨기고 TextField 보이게
        commentTextLabel.setVisible(false);
        editTextField.setVisible(true);

        // focus 및 커서 위치
        editTextField.requestFocus();
        editTextField.positionCaret(editTextField.getText().length());

        // Enter 입력 시 저장
        editTextField.setOnAction(event -> applyEdit());
    }

    // 댓글 수정 입력란 메서드
    private void applyEdit() {
        String newText = editTextField.getText();
        if (newText != null && !newText.isEmpty()) {
            commentTextLabel.setText(newText);
        }

        // 다시 Label 보이고, TextField 숨김
        editTextField.setVisible(false);
        commentTextLabel.setVisible(true);
    }
}
