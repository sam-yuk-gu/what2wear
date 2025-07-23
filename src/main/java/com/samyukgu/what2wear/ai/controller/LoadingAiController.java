package com.samyukgu.what2wear.ai.controller;

import javafx.animation.*;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import javafx.util.Duration;

public class LoadingAiController {

    @FXML StackPane root;
    @FXML private Circle circle1;
    @FXML private Circle circle2;
    @FXML private Circle circle3;

    @FXML
    public void initialize() {
        // 스피너 애니메이션 효과
        playScaleAnimation(circle1, 0);
        playScaleAnimation(circle2, 200);
        playScaleAnimation(circle3, 400);
        
        // 5초 후 대기 화면으로 전환
        PauseTransition delay = new PauseTransition(Duration.seconds(5));
        delay.setOnFinished(event -> switchToRecommendAi());
        delay.play();
    }

    private void switchToRecommendAi() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/samyukgu/what2wear/ai/RecommendAi.fxml"));
            Parent recommentRoot = loader.load();

            Stage stage = (Stage) root.getScene().getWindow();
            stage.setScene(new Scene(recommentRoot));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void playScaleAnimation(Circle circle, int delayMillis) {
        ScaleTransition scale = new ScaleTransition(Duration.millis(600), circle);
        scale.setFromX(1.0);
        scale.setFromY(1.0);
        scale.setToX(1.5);
        scale.setToY(1.5);
        scale.setAutoReverse(true);
        scale.setCycleCount(Animation.INDEFINITE);
        scale.setDelay(Duration.millis(delayMillis));
        scale.play();
    }
}
