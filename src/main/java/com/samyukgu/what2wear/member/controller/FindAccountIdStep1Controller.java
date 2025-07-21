package com.samyukgu.what2wear.member.controller;

import com.samyukgu.what2wear.di.DIContainer;
import com.samyukgu.what2wear.mail.service.MailService;
import com.samyukgu.what2wear.member.common.MemberConstants;
import com.samyukgu.what2wear.member.service.MemberService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;

import java.io.IOException;

public class FindAccountIdStep1Controller {
    @FXML private Label nameCheckResLabel;
    @FXML private Label emailCheckResLabel;
    @FXML private TextField inputNameField;
    @FXML private TextField inputMailField;
    @FXML private Button prevButton;
    @FXML private Button nextButton;

    private MemberService memberService;
    private MailService mailService;

    @FXML
    public void initialize(){
        setupDI();
        setupListener();
    }

    @FXML
    public void handleClickPrevButton(){
        switchScene("/com/samyukgu/what2wear/member/LoginView.fxml", "로그인");
    }

    @FXML
    public void handleClickNextButton(){
        String name = inputNameField.getText();
        String mail = inputMailField.getText();
        if(!isValidField(name, mail))
            return;
        String[] nextPath = decidePath(name, mail);
        // todo: 메일 전송
        switchScene(nextPath[0], nextPath[1]);
    }

    private void setupListener(){
        // inputNameField 변경 시 emailCheckResLabel 초기화
        inputNameField.textProperty().addListener((observable, oldValue, newValue) -> {
            nameCheckResLabel.setText("");
        });

        // inputMailField 변경 시 emailCheckResLabel 초기화
        inputMailField.textProperty().addListener((observable, oldValue, newValue) -> {
            emailCheckResLabel.setText("");
        });
    }

    private String[] decidePath(String name, String mail){
        String[] nextPath = new String[2];

        if(!memberService.isExist(name, mail)){
            nextPath[0] = "/com/samyukgu/what2wear/member/FindAccountIdStep2View.fxml";
            nextPath[1] = "아이디 찾기";
        }else{
            nextPath[0] = "/com/samyukgu/what2wear/member/AccountNotFoundView.fxml";
            nextPath[1] = "회원조회 실패";
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

    private boolean isValidField(String name, String mail){
        boolean flag = true;

        if(name.isEmpty()){
            nameCheckResLabel.setText("이름을 입력해주세요");
            flag = false;
        }

        if(!mail.matches(MemberConstants.EMAIL_PATTERN)){
            emailCheckResLabel.setText("올바른 형식의 이메일 주소가 아닙니다.");
            flag = false;
        }

        return flag;
    }

    private void setupDI() {
        DIContainer diContainer = DIContainer.getInstance();
        memberService = diContainer.resolve(MemberService.class);
        mailService = diContainer.resolve(MailService.class);
    }
}
