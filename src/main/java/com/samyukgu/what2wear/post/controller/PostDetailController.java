package com.samyukgu.what2wear.post.controller;

import com.samyukgu.what2wear.post.model.Post;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.io.IOException;

public class PostDetailController {

    @FXML private Label titleLabel;
    @FXML private Label contentLabel;
    @FXML private Label authorLabel;
    @FXML private Label dateLabel;
    @FXML private Button likeButton;
    @FXML private ImageView likeIcon;
    @FXML private Label likesLabel;
    @FXML private StackPane root;

    private int likeCount; // 좋아요 수 선언
    private boolean isLiked = false; // 기본값: false

    // 게시글 데이터 설정
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

    // 뒤로가기 메서드 (게시글 목록 화면으로 이동)
    @FXML
    private void handleBack() {
        try {
            Parent postList = FXMLLoader.load(getClass().getResource("/com/samyukgu/what2wear/post/post_list.fxml"));
            Scene scene = new Scene(postList);
            Stage stage = (Stage) root.getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
