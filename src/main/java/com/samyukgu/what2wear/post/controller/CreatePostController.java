package com.samyukgu.what2wear.post.controller;

import com.samyukgu.what2wear.common.controller.CustomModalController;
import com.samyukgu.what2wear.common.controller.MainLayoutController;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;

public class CreatePostController {

    @FXML private StackPane root;
    @FXML private Button cancelButton;
    @FXML private Button registerButton;
    @FXML
    public void initialize() {
        cancelButton.setOnAction(event -> handleCancelClick());
        registerButton.setOnAction(event -> showConfirmationModal());
    }

    // 게시글 등록 취소
    @FXML
    private void handleCancelClick() {
        MainLayoutController.loadView("/com/samyukgu/what2wear/post/ListPost.fxml");
    }

    // 게시글 등록 
    @FXML
    private void handleRegisterClick() {
        MainLayoutController.loadView("/com/samyukgu/what2wear/post/ListPost.fxml");
    }

    // 게시글 등록 확인 모달창 띄우기
    private void showConfirmationModal() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/samyukgu/what2wear/common/CustomModal.fxml"));
            StackPane modal = loader.load();

            CustomModalController controller = loader.getController();
            controller.configure(
                    "게시글 등록",
                    "등록된 게시글은 수정/삭제할 수 있습니다.",
                    "/assets/images/check_icon.png",  
                    "#4CAF50",
                    "취소",
                    "확인",
                    () -> root.getChildren().remove(modal),  // 취소 시 모달 제거
                    () -> {
                        root.getChildren().remove(modal);  // 확인 시 모달 제거
                        MainLayoutController.loadView("/com/samyukgu/what2wear/post/ListPost.fxml"); // 뷰 전환
                    }
            );

            root.getChildren().add(modal);  // 모달 화면 위에 덮기

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
