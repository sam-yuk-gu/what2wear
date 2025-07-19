package com.samyukgu.what2wear.post.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.io.IOException;

public class PostCreateController {

    @FXML
    private ImageView backImage;
    
    // 이전 화면으로 되돌아가는 handleBack() 메서드
    @FXML
    private void handleBack(MouseEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/com/samyukgu/what2wear/post/post_list.fxml"));
            Stage stage = (Stage) backImage.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
