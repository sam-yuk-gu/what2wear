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

    /**
    *   1.다음 버튼 클릭
     *  2.필드값 유효성 검증
     *  3.입력한 정보로 아이디를 가입된 정보를 찾으면 메일 전송 및 nextPath 성공 페이지로 지정
     *  4.nextPath 실패 페이지로 지정
     *  5.Scene 이동
    */
    @FXML
    public void handleClickNextButton(){
        String name = inputNameField.getText();
        String email = inputMailField.getText();
        if(!isValidField(name, email))
            return;
        String[] nextPath = decidePath(name, email);
        if (nextPath[0].contains("FindAccountIdStep2View.fxml")) {
            String accountId = memberService.getAccountIdByNameAndEmail(name, email);
            mailService.sendId(email, accountId);
        }
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

    // 사용자가 있으면 다음 아이디 찾기 페이지, 없으면 회원조회 실패
    private String[] decidePath(String name, String mail){
        String[] nextPath = new String[2];

        if(memberService.existsByNameAndEmail(name, mail)){
            nextPath[0] = "/com/samyukgu/what2wear/member/FindAccountIdStep2View.fxml";
            nextPath[1] = "아이디 찾기";
        }else{
            nextPath[0] = "/com/samyukgu/what2wear/member/AccountNotFoundView.fxml";
            nextPath[1] = "회원조회 실패";
        }
        return nextPath;
    }

    // scene 이동 메서드
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
    
    // 이름과 이메일 유효성 검사
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
    
    // 컨테이너에 있는 인스턴스 멤버로 할당
    private void setupDI() {
        DIContainer diContainer = DIContainer.getInstance();
        memberService = diContainer.resolve(MemberService.class);
        mailService = diContainer.resolve(MailService.class);
    }
}
