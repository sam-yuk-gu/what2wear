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
import javafx.stage.Stage;

import java.io.IOException;

public class FindPasswordStep1Controller {
    @FXML private TextField inputIdField;
    @FXML private Button nextButton;
    @FXML private Button prevButton;
    @FXML private Label idCheckResLabel;

    private MemberService memberService;
    private MailService mailService;

    @FXML
    public void initialize(){
        setupDI();
        setupListener();
    }

    public void handleClickPrevButton(){
        switchScene("/com/samyukgu/what2wear/member/LoginView.fxml", "로그인");
    }

    public void handleClickNextButton(){
        if(!isValidField(inputIdField.getText()))
            return;
        String[] nextPath = decidePath(inputIdField.getText());
        // todo: nextPath가 비밀번호 찾기면 메일 전송해야함
        switchScene(nextPath[0], nextPath[1]);
    }

    private String[] decidePath(String accountId){
        String[] nextPath = new String[2];

        if(!memberService.isExist(accountId)){
            nextPath[0] = "/com/samyukgu/what2wear/member/FindPasswordStep2View.fxml";
            nextPath[1] = "비밀번호 찾기";
        }else{
            nextPath[0] = "/com/samyukgu/what2wear/member/AccountNotFoundView.fxml";
            nextPath[1] = "회원조회 실패";
        }
        return nextPath;
    }

    private boolean isValidField(String id){
        boolean flag =
                id.length() >= MemberConstants.ID_MIN_LENGTH &&
                id.length() <= MemberConstants.ID_MAX_LENGTH &&
                id.matches(MemberConstants.ID_PATTERN);
        if(!flag)
            idCheckResLabel.setText(MemberConstants.ID_CONSTANT);
        return flag;
    }

    private void setupListener(){
        // inputIdField 변경 시 idCheckResLabel 초기화
        inputIdField.textProperty().addListener((observable, oldValue, newValue) -> {
            idCheckResLabel.setText("");
        });
    }

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

    private void setupDI() {
        DIContainer diContainer = DIContainer.getInstance();
        memberService = diContainer.resolve(MemberService.class);
        mailService = diContainer.resolve(MailService.class);
    }
}
