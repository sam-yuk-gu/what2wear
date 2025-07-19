package com.samyukgu.what2wear;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.Objects;

public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("/com/samyukgu/what2wear/layout/MainLayout.fxml"));

        Scene scene = new Scene(fxmlLoader.load(), 1280, 768);
        // css 추가
        scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/com/samyukgu/what2wear/post/style.css")).toExternalForm());

        // font 설정
        URL styleUrl = getClass().getResource("/styles/style.css");
        if (styleUrl != null) {
            scene.getStylesheets().add(styleUrl.toExternalForm());
        } else {
            throw new IllegalStateException("style.css not found");
        }

        stage.setResizable(false);  // 크기 고정
        stage.setTitle("내일 뭐 입지?");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}