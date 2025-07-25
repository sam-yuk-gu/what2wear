package com.samyukgu.what2wear.member.controller;

import java.io.IOException;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

public class SignupFailureController {
    @FXML
    private Button prevButton;
    // 이전 버튼 클릭하면 로그인으로 이동
    public void handleClickPrevButton(){
        switchScene("/com/samyukgu/what2wear/member/LoginView.fxml", "로그인");
    }

    // scene 이동 메서드
    private void switchScene(String fxmlPath, String title){
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();

            Stage stage = (Stage) prevButton.getScene().getWindow();
            Scene scene = new Scene(root, 1280, 768);
            stage.setScene(scene);
            stage.setTitle(title);
            stage.setResizable(false);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
