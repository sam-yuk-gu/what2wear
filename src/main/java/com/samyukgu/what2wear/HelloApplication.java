package com.samyukgu.what2wear;

import com.samyukgu.what2wear.config.ApplicationConfig;
import com.samyukgu.what2wear.di.DIContainer;
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
    public void init() throws Exception{
        // 애플리케이션 시작 -> 의존관계 설정
        ApplicationConfig.configure();
    }

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("/com/samyukgu/what2wear/codi/CodiMainView.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1030, 768);

        // 전역 CSS 연결 및 폰트 등록
        URL styleUrl = getClass().getResource("/styles/style.css");
        if (styleUrl != null) {
            scene.getStylesheets().add(styleUrl.toExternalForm());
        } else {
            throw new IllegalStateException("style.css not found");
        }

        stage.setTitle("Hello!");
        stage.setScene(scene);
        stage.show();
    }

    @Override
    public void stop() throws Exception {
        // 애플리케이션 종료 -> container clear
        DIContainer.getInstance().clear();
    }

    public static void main(String[] args) {
        launch();
    }
}
