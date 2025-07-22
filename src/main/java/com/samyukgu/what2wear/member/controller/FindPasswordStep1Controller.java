package com.samyukgu.what2wear.member.controller;

import com.samyukgu.what2wear.di.DIContainer;
import com.samyukgu.what2wear.mail.common.PasswordUtils;
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

    // 리스너 및 DI 컨테이너 초기화
    @FXML
    public void initialize(){
        setupDI();
        setupListener();
    }

    // 이전 버튼 클릭하면 로그인으로 이동
    @FXML
    public void handleClickPrevButton(){
        switchScene("/com/samyukgu/what2wear/member/LoginView.fxml", "로그인");
    }

    // 다음 버튼 클릭하면 메일로 비밀번호 전송
    @FXML
    public void handleClickNextButton(){
        String accountId = inputIdField.getText();
        if(!isValidField(accountId))
            return;
        String[] nextPath = decidePath(accountId);
        if(nextPath[0].contains("FindPasswordStep2View.fxml")){
            String email = memberService.findEmailByAccountId(accountId);
            String newPassword = memberService.generateAndSaveTempPassword(accountId);
            mailService.sendPassword(email, newPassword);
        }
        switchScene(nextPath[0], nextPath[1]);
    }

    // 사용자가 있으면 다음 비밀번호 찾기 페이지, 없으면 회원조회 실패
    private String[] decidePath(String accountId){
        String[] nextPath = new String[2];

        if(memberService.existsByAccountId(accountId)){
            nextPath[0] = "/com/samyukgu/what2wear/member/FindPasswordStep2View.fxml";
            nextPath[1] = "비밀번호 찾기";
        }else{
            nextPath[0] = "/com/samyukgu/what2wear/member/AccountNotFoundView.fxml";
            nextPath[1] = "회원조회 실패";
        }
        return nextPath;
    }

    // account_id 유효성 검사
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

    // 컨테이너에 있는 인스턴스 멤버로 할당
    private void setupDI() {
        DIContainer diContainer = DIContainer.getInstance();
        memberService = diContainer.resolve(MemberService.class);
        mailService = diContainer.resolve(MailService.class);
    }
}
