package com.samyukgu.what2wear.post.controller;

import com.samyukgu.what2wear.common.controller.CustomModalController;
import com.samyukgu.what2wear.layout.MainLayoutController;
import com.samyukgu.what2wear.common.controller.PostHeaderController;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;

public class CreatePostController {

    @FXML private StackPane root;
    @FXML private Button cancelButton;
    @FXML private Button registerButton;
    @FXML private TextArea content_title; // 내용 입력란

    @FXML
    public void initialize() {
        // PostHeader 로드 및 설정
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/samyukgu/what2wear/common/PostHeader.fxml"));
            HBox headerNode = loader.load();
            PostHeaderController controller = loader.getController();
            controller.setTitle("게시글 등록");
            // back 버튼 사용 안 함
            controller.setBackButtonVisible(false);
            root.getChildren().add(0, headerNode);

            // 내용 자동 줄 바꿈 활성화
            content_title.setWrapText(true);
            
            // 내용 자동 줄 바꿈 계산
            content_title.textProperty().addListener((obs, oldText, newText) -> {
                int approxCharsPerLine = 109;       // 한 줄에 들어갈 수 있는 평균 문자 수 (810px 기준)
                int lineBreaks = newText.split("\n").length;
                int wrappedLines = newText.length() / approxCharsPerLine;

                int totalLines = Math.max(1, lineBreaks + wrappedLines);
                content_title.setPrefHeight(24 * totalLines + 20);      // 줄 수 × 줄 높이 + 패딩
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

        content_title.setWrapText(true);


        // 버튼 클릭 이벤트
        cancelButton.setOnAction(event -> handleCancelClick());
        registerButton.setOnAction(event -> showConfirmationModal());
    }

    // 취소 버튼 클릭 → 게시글 목록 화면으로 이동
    private void handleCancelClick() {
        MainLayoutController.loadView("/com/samyukgu/what2wear/post/ListPost.fxml");
    }

    // 모달 띄우기 → 확인 클릭 시 게시글 등록
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
                        MainLayoutController.loadView("/com/samyukgu/what2wear/post/ListPost.fxml");
                        // TODO: 게시글 등록 로직 추가
                    }
            );

            root.getChildren().add(modal);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
