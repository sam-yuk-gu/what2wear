package com.samyukgu.what2wear.post.controller;

import com.samyukgu.what2wear.common.controller.CustomModalController;
import com.samyukgu.what2wear.common.controller.MainLayoutController;
import com.samyukgu.what2wear.common.controller.PostHeaderController;
import com.samyukgu.what2wear.di.DIContainer;
import com.samyukgu.what2wear.post.service.PostService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

public class EditPostController {

    @FXML private StackPane root;
    @FXML private VBox mainVBox;
    @FXML private Button cancelButton;
    @FXML private Button confirmButton;
    private PostService postService;

    public void initialize() {
        this.postService = DIContainer.getInstance().resolve(PostService.class);

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/samyukgu/what2wear/common/PostHeader.fxml"));
            HBox headerNode = loader.load();
            PostHeaderController controller = loader.getController();

            controller.setTitle("게시글 수정");
            // back 버튼 사용 안 함
            controller.setBackButtonVisible(false);

            // 최상단에 header 삽입
            mainVBox.getChildren().add(0, headerNode);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // 버튼 이벤트 설정
        cancelButton.setOnAction(e -> handleCancelClick());
        confirmButton.setOnAction(e -> handleConfirmClick());
    }

    // 수정 취소 후 목록 확면으로 전환하는 메서드
    private void handleCancelClick() {
        MainLayoutController.loadView("/com/samyukgu/what2wear/post/ListPost.fxml");
    }

    // 수정 확인 후 모달창 띄우는 메서드
    private void handleConfirmClick() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/samyukgu/what2wear/common/CustomModal.fxml"));
            StackPane modal = loader.load();

            CustomModalController controller = loader.getController();
            controller.configure(
                    "게시글 수정 완료",
                    "정상적으로 게시글이 수정되었습니다.",
                    "/assets/icons/greenCheck.png",
                    "#4CAF50",
                    "취소",
                    "확인",
                    () -> root.getChildren().remove(modal),
                    () -> {
                        root.getChildren().remove(modal);
                        MainLayoutController.loadView("/com/samyukgu/what2wear/post/ListPost.fxml");
                        // TODO: 수정된 게시글 저장 처리
                    }
            );

            root.getChildren().add(modal);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
