package com.samyukgu.what2wear.ai.controller;

import com.samyukgu.what2wear.layout.controller.MainLayoutController;
import javafx.animation.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Circle;
import javafx.util.Duration;

public class LoadingAiController {

    @FXML StackPane root;
    @FXML private Circle circle1;
    @FXML private Circle circle2;
    @FXML private Circle circle3;

    private boolean isCanceled = false; // 나가기 버튼 눌렀는지 여부 확인

    @FXML
    public void initialize() {
        // 스피너 애니메이션 효과
        playScaleAnimation(circle1, 0);
        playScaleAnimation(circle2, 200);
        playScaleAnimation(circle3, 400);
        
        // 5초 후 대기 화면으로 전환
        PauseTransition delay = new PauseTransition(Duration.seconds(5));
        delay.setOnFinished( event -> {
            // 나가기 버튼 안 눌렀을 경우에만
            if(!isCanceled) {
                // ai 추천 결과 화면으로 전환
                switchToRecommendAi();
            }
        });
        delay.play();
    }

    private void switchToRecommendAi() {
        try {
            // AI 추천 결과 화면으로 전환
            MainLayoutController.loadView("/com/samyukgu/what2wear/ai/RecommendAi.fxml");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 나가기 버튼 클릭 시 메인 화면으로 이동
    public void handleCancelClick(ActionEvent actionEvent) {
        isCanceled = true;
        try {
            MainLayoutController.loadView("/com/samyukgu/what2wear/codi/CodiMainView.fxml");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void playScaleAnimation(Circle circle, int delayMillis) {
        ScaleTransition scale = new ScaleTransition(Duration.millis(500), circle);
        scale.setFromX(0.5); // 작게 시작
        scale.setFromY(0.5);
        scale.setToX(1.5);  // 크게 끝
        scale.setToY(1.5); 
        scale.setAutoReverse(true);
        scale.setCycleCount(Animation.INDEFINITE);
        scale.setDelay(Duration.millis(delayMillis));
        scale.setInterpolator(Interpolator.EASE_BOTH);  // 부드럽게
        scale.play();
    }
}
