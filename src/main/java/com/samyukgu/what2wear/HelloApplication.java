package com.samyukgu.what2wear;

import com.samyukgu.config.ApplicationConfig;
import com.samyukgu.di.DIContainer;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class HelloApplication extends Application {
    @Override
    public void init() throws Exception{
        // 애플리케이션 시작 -> 의존관계 설정
        ApplicationConfig.configure();
    }

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("hello-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 320, 240);
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