package com.samyukgu.what2wear.member.controller;

import com.samyukgu.what2wear.di.DIContainer;
import com.samyukgu.what2wear.member.common.MemberConstants;
import com.samyukgu.what2wear.member.service.MemberService;
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
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

public class SignupStep1Controller {
    @FXML private Label passwordRes;
    @FXML private Label passwordCheckRes;
    @FXML private HBox signupBanner;
    @FXML private TextField inputIdField;
    @FXML private PasswordField inputPasswordField;
    @FXML private PasswordField inputPasswordCheckField;
    @FXML private Button prevButton;
    @FXML private Button nextButton;
    @FXML private Label duplicateResLabel;

    private MemberService memberService;

    private boolean idAlreadyExists = true;

    // 초기화
    @FXML
    public void initialize(){
        setNextButtonEnabled(false);
        setupUI();
        setupEventHandlers();
        setupDI();
    }

    // 이전 버튼 클릭하면 로그인으로 이동
    @FXML
    private void handleClickPrevButton(){
        switchScene("/com/samyukgu/what2wear/member/LoginView.fxml", "로그인");
    }

    // 다음 버튼 클릭하면 회원가입 다음 스텝으로 이동
    @FXML
    private void handleClickNextButton(){
        String accountId = inputIdField.getText();
        String password = inputPasswordField.getText();
        switchSceneWithUserData("/com/samyukgu/what2wear/member/SignupStep2View.fxml", "회원 가입", accountId, password);
    }
    
    // 중복확인 버튼 기능
    @FXML
    private void handleClickCheckDuplicateButton() {
        String id = inputIdField.getText();
        boolean idPatternOk = isValidId(id);
        idAlreadyExists = memberService.existsByAccountId(id);

        if (!idPatternOk) {
            showErrorMessage(duplicateResLabel, MemberConstants.ID_CONSTANT);
        } else if (idAlreadyExists) {
            showErrorMessage(duplicateResLabel, "이미 존재하는 아이디입니다.");
        } else {
            showSuccessMessage(duplicateResLabel, "사용 가능한 아이디입니다.");
        }

        validateForm();
    }
    
    // 초기 회원가입 배너 사이즈 조정 및 css 적용
    private void setupUI() {
        int arcWidth = 30, arcHeight = 30;
//        Rectangle clip = new Rectangle(signupBanner.getFitWidth(), signupBanner.getFitHeight());
//        clip.setArcWidth(arcWidth);
//        clip.setArcHeight(arcHeight);
//        signupBanner.setClip(clip);
    }

    // id, password, password check 필드에 이벤트 할당
    private void setupEventHandlers() {
        inputIdField.addEventHandler(KeyEvent.KEY_RELEASED, e -> handleIdFieldChange());
        inputPasswordField.addEventHandler(KeyEvent.KEY_RELEASED, e -> handlePasswordFieldChange());
        inputPasswordCheckField.addEventHandler(KeyEvent.KEY_RELEASED, e -> handlePasswordCheckFieldChange());
    }
    
    // 패스워드 필드가 바뀔때마다 유효성 검증
    private void handlePasswordFieldChange() {
        String password = inputPasswordField.getText();

        if (isValidPasswordLength(password) && !isValidPassword(password)) {
            showErrorMessage(passwordRes, MemberConstants.PW_CONSTANT);
        } else {
            clearLabel(passwordRes);
        }

        // 패스워드 확인 필드가 비어있지 않다면 일치 여부 확인
        if (!inputPasswordCheckField.getText().isEmpty()) {
            validatePasswordMatch();
        }
        validateForm();
    }

    // 패스워드 확인 필드가 바뀔때마다 유효성 검증
    private void handlePasswordCheckFieldChange() {
        validatePasswordMatch();
        validateForm();
    }
    
    // 두개의 비밀번호가 일치하는지
    private void validatePasswordMatch() {
        String password = inputPasswordField.getText();
        String passwordCheck = inputPasswordCheckField.getText();

        if (isValidPasswordLength(passwordCheck) && !password.equals(passwordCheck)) {
            showErrorMessage(passwordCheckRes, "비밀번호가 일치하지 않습니다.");
        } else {
            clearLabel(passwordCheckRes);
        }
    }

    // ID 조건에 만족하는지
    private boolean isValidId(String id) {
        return id.matches(MemberConstants.ID_PATTERN);
    }

    // 비밀번호 조건에 만족하는지 (Member.common.MemberConstants.PASSWORD_PATTERN)
    private boolean isValidPassword(String password) {
        return password.matches(MemberConstants.PASSWORD_PATTERN);
    }

    // 비밀번호 길이가 만족하는지 (Member.common.MemberConstants.PASSWORD_MIN_LENGTH)
    private boolean isValidPasswordLength(String password){
        return password.length() >= MemberConstants.PASSWORD_MIN_LENGTH;
    }

    // Label의 메시지를 에러 메시지로 출력
    private void showErrorMessage(Label label, String message) {
        label.setText(message);
        label.getStyleClass().removeAll("text-red", "text-green");
        label.getStyleClass().add("text-red");
    }

    // Label의 메시지를 성공 메시지로 출력
    private void showSuccessMessage(Label label, String message) {
        label.setText(message);
        label.getStyleClass().removeAll("text-red", "text-green");
        label.getStyleClass().add("text-green");
    }

    // Label 값 초기화
    private void clearLabel(Label label) {
        label.setText("");
        label.getStyleClass().removeAll("text-red", "text-green");
    }

    // 아이디 필드의 값이 변경될때마다 유효성 검증 초기화 및 출력메시지 삭제
    private void handleIdFieldChange() {
        idAlreadyExists = true;
        clearLabel(duplicateResLabel);
        validateForm();
    }
    
    // 모든 조건이 만족하는지 확인
    private void validateForm() {
        String id = inputIdField.getText();
        String password = inputPasswordField.getText();
        String passwordCheck = inputPasswordCheckField.getText();

        boolean isValid = isValidId(id)
                && !idAlreadyExists
                && isValidPassword(password)
                && password.equals(passwordCheck);

        setNextButtonEnabled(isValid);
    }
    
    // 버튼 상태 변경 (활성화, 비활성화)
    private void setNextButtonEnabled(boolean enabled) {
        nextButton.setDisable(!enabled);
        nextButton.getStyleClass().removeAll("btn-gray", "btn-green");
        nextButton.getStyleClass().add(enabled ? "btn-green" : "btn-gray");
        nextButton.setOnAction(enabled ? e -> handleClickNextButton() : null);
    }
    
    // scene 이동 메서드
    private void switchScene(String fxmlPath, String title){
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();

            Stage stage = (Stage) signupBanner.getScene().getWindow();
            Scene scene = new Scene(root, 1280, 768);
            stage.setScene(scene);
            stage.setTitle(title);
            stage.setResizable(false);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // scene 이동 메서드 (다음 컨트롤러의 필드에 값 미리 set)
    private void switchSceneWithUserData(String fxmlPath, String title, String accountId, String password) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();

            // 컨트롤러 가져오기
            SignupStep2Controller controller = loader.getController();
            controller.setUserData(accountId, password);

            Stage stage = (Stage) inputIdField.getScene().getWindow();
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
    }
}
