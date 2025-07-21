package com.samyukgu.what2wear.post.controller;

import com.samyukgu.what2wear.common.controller.CustomModalController;
import com.samyukgu.what2wear.common.controller.MainLayoutController;
import com.samyukgu.what2wear.common.controller.PostHeaderController;
import com.samyukgu.what2wear.di.DIContainer;
import com.samyukgu.what2wear.post.model.Post;
import com.samyukgu.what2wear.post.service.PostService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;

import java.util.Date;

public class CreatePostController {

    @FXML private StackPane root;
    @FXML private Button cancelButton;
    @FXML private Button registerButton;
    @FXML private TextField input_title;
    @FXML private TextArea content_title;

    private final PostService postService = DIContainer.getInstance().resolve(PostService.class);

    @FXML
    public void initialize() {
        // 헤더 설정
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/samyukgu/what2wear/common/PostHeader.fxml"));
            HBox headerNode = loader.load();
            PostHeaderController controller = loader.getController();
            controller.setTitle("게시글 등록");
            controller.setBackButtonVisible(false);
            root.getChildren().add(0, headerNode);
        } catch (Exception e) {
            e.printStackTrace();
        }

        content_title.setWrapText(true);

        // 자동 줄 바꿈 높이 계산
        content_title.textProperty().addListener((obs, oldText, newText) -> {
            int approxCharsPerLine = 109;
            int lineBreaks = newText.split("\n").length;
            int wrappedLines = newText.length() / approxCharsPerLine;
            int totalLines = Math.max(1, lineBreaks + wrappedLines);
            content_title.setPrefHeight(24 * totalLines + 20);
        });

        // 버튼 이벤트
        cancelButton.setOnAction(event -> handleCancelClick());
        registerButton.setOnAction(event -> showConfirmationModal());
    }

    private void handleCancelClick() {
        MainLayoutController.loadView("/com/samyukgu/what2wear/post/ListPost.fxml");
    }

    private void showConfirmationModal() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/samyukgu/what2wear/common/CustomModal.fxml"));
            StackPane modal = loader.load();

            CustomModalController controller = loader.getController();
            controller.configure(
                    "게시글 등록 완료",
                    "정상적으로 게시글이 등록되었습니다.",
                    "/assets/icons/greenCheck.png",
                    "#4CAF50",
                    "취소",
                    "확인",
                    () -> root.getChildren().remove(modal),
                    () -> {
                        root.getChildren().remove(modal);

                        // 제목, 내용 가져오기
                        String title = input_title.getText();
                        String content = content_title.getText();

                        // Post 객체 생성 (ID는 임시로 currentTimeMillis 사용)
                        Post newPost = new Post(
                                System.currentTimeMillis(),
                                200+1L, // member_id - 추후 로그인 세션과 연동 가능
                                300+1L, // cody_id - 코디 연동 구현 시 수정
                                title,
                                content,
                                new Date(),
                                new Date(),
                                0
                                );

                        // 게시글 저장 (DI 사용)
                        postService.createPost(newPost);

                        // 목록으로 이동 (자동 새로고침됨)
                        MainLayoutController.loadView("/com/samyukgu/what2wear/post/ListPost.fxml");
                    }
            );

            root.getChildren().add(modal);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
