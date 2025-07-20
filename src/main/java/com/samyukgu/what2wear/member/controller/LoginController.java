package com.samyukgu.what2wear.member.controller;

import com.samyukgu.what2wear.di.DIContainer;
import java.io.IOException;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

public class LoginController {
    @FXML private static LoginController instance;
    @FXML private TextField inputIdField;
    @FXML private PasswordField inputPasswordField;
    @FXML private Button loginButton;
    @FXML private Label signupLabel;
    @FXML private Label findIdLabel;
    @FXML private Label findPasswordLabel;

    @FXML
   ImageView loginBanner;

    @FXML
    public void initialize(){
        instance = this;

        int arcWidth = 30, arcHeight = 30;

        Rectangle clip = new Rectangle(loginBanner.getFitWidth(), loginBanner.getFitHeight());
        clip.setArcWidth(arcWidth);
        clip.setArcHeight(arcHeight);

        loginBanner.setClip(clip);

        DIContainer diContainer = DIContainer.getInstance();
    }

    @FXML
    private void handleClickLoginButton(){
        // 로그인 버튼 클릭시 회원 검증 후 메인 페이지 이동 
        /*
        :todo:  회원 검증 로직 추가
                유저 세션에 유저 정보 담기
        */
        switchScene("/com/samyukgu/what2wear/layout/MainLayout.fxml", "내일 뭐 입지?");
    }

    @FXML
    private void handleClickSignupLabel(){
        switchScene("/com/samyukgu/what2wear/member/SignupStep1View.fxml", "회원가입");
    }

    @FXML
    private void handleClickFindIdLabel(){
        switchScene("/com/samyukgu/what2wear/member/FindIdView.fxml", "아이디 찾기");
    }

    @FXML
    private void handleClickFindPasswordLabel(){
        switchScene("/com/samyukgu/what2wear/member/FindPasswordView.fxml", "아이디 찾기");
    }



    private void switchScene(String fxmlPath, String title){
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();

            Stage stage = (Stage) loginBanner.getScene().getWindow();
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
