package com.samyukgu.what2wear.ai.controller;

import com.samyukgu.what2wear.common.controller.CustomModalController;
import com.samyukgu.what2wear.layout.controller.MainLayoutController;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.StackPane;
import java.io.IOException;

// 추천 받기
public class RecommendAiController {
    @FXML StackPane root;
    @FXML
    private void handleRetryClick() {
        // AI 안내 화면으로 전환
        MainLayoutController.loadView("/com/samyukgu/what2wear/ai/LoadingAi.fxml");
    }

    // 저장 로직 또는 알림창
    @FXML
    private void handleSaveClick() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/samyukgu/what2wear/common/CustomModal.fxml"));
        StackPane modal = loader.load();

        CustomModalController controller = loader.getController();
        controller.configure(
                "코디 저장 완료",
                "정상적으로 코디가 저장되었습니다.",
                "/assets/icons/greenCheck.png",
                "#79C998",
                "취소",
                "확인",
                () -> root.getChildren().remove(modal),
                () -> {
                    root.getChildren().remove(modal);
                    // 게시글 목록으로 이동
                    MainLayoutController.loadView("/com/samyukgu/what2wear/codi/CodiMainView.fxml");
                }
        );

        root.getChildren().add(modal);
    }
}