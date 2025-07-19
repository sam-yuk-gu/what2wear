package com.samyukgu.what2wear.post.controller;

import com.samyukgu.what2wear.common.CustomModalController;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.io.IOException;

public class PostCreateController {

    @FXML private StackPane root;
    @FXML private Button registerButton;

    @FXML
    public void initialize() {
        registerButton.setOnAction(event -> showConfirmationModal());
    }

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

                        // post_list.fxml로 이동
                        try {
                            Parent postList = FXMLLoader.load(getClass().getResource("/com/samyukgu/what2wear/post/post_list.fxml"));
                            Scene scene = new Scene(postList);
                            // 현재 stage 얻기
                            Stage stage = (Stage) root.getScene().getWindow();
                            stage.setScene(scene);
                            stage.show();
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
            );

            root.getChildren().add(modal);  // 모달 화면 위에 덮기

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 뒤로가기 메서드
    @FXML
    public void handleBack(MouseEvent mouseEvent) {
        System.out.println("뒤로가기 클릭됨");
    }
}
