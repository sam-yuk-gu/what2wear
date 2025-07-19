package com.samyukgu.what2wear;

import com.samyukgu.what2wear.config.ApplicationConfig;
import com.samyukgu.what2wear.di.DIContainer;
import com.samyukgu.what2wear.member.dao.MemberOracleDAO;
import com.samyukgu.what2wear.member.service.MemberService;
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
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("/com/samyukgu/what2wear/layout/MainLayout.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1280, 768);
        // css 추가
        scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/com/samyukgu/what2wear/post/style.css")).toExternalForm());
        // font 설정
        URL styleUrl = getClass().getResource("/styles/style.css");

        // 태스트
        System.setProperty("file.encoding", "UTF-8");
        MemberService service = new MemberService(new MemberOracleDAO());
        service.getAllMembers().forEach(member -> {
            System.out.println("ID: " + member.getId() + ", Name: " + member.getName());
        });



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

    @Override
    public void stop() throws Exception {
        // 애플리케이션 종료 -> container clear
        DIContainer.getInstance().clear();
    }

    public static void main(String[] args) {
        launch();
    }
}