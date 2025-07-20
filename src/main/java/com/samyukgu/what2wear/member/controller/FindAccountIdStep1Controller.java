package com.samyukgu.what2wear.member.controller;

import com.samyukgu.what2wear.di.DIContainer;
import com.samyukgu.what2wear.member.service.MemberService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

public class FindAccountIdStep1Controller {
    @FXML private TextField inputNameField;
    @FXML private TextField inputMailField;
    @FXML private Button prevButton;
    @FXML private Button nextButton;

    private MemberService memberService;

    @FXML
    public void initialize(){
        setupDI();
    }

    @FXML
    public void handleClickPrevButton(){
        switchScene("/com/samyukgu/what2wear/member/LoginView.fxml", "로그인");
    }

    @FXML
    public void handleClickNextButton(){
        String nextPath = decidePath();
        // todo: 메일 전송
        switchScene(nextPath, "아이디 찾기");
    }

    private String decidePath(){
        String nextPath;
        String name = inputNameField.getText();
        String mail = inputMailField.getText();
        if(!memberService.isExist(name, mail)){
            nextPath = "/com/samyukgu/what2wear/member/FindAccountIdStep2View.fxml";
        }else{
            nextPath = "/com/samyukgu/what2wear/member/AccountNotFoundView.fxml";
        }
        return nextPath;
    }

    private void switchScene(String fxmlPath, String title){
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();

            Stage stage = (Stage) inputMailField.getScene().getWindow();
            Scene scene = new Scene(root, 1280, 768);
            stage.setScene(scene);
            stage.setTitle(title);
            stage.setResizable(false);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void setupDI() {
        DIContainer diContainer = DIContainer.getInstance();
        memberService = diContainer.resolve(MemberService.class);
    }
}
