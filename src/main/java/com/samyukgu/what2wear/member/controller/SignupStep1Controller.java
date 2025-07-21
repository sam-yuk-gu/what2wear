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
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

public class SignupStep1Controller {
    @FXML private Label passwordRes;
    @FXML private Label passwordCheckRes;
    @FXML private ImageView signupBanner;
    @FXML private TextField inputIdField;
    @FXML private PasswordField inputPasswordField;
    @FXML private PasswordField inputPasswordCheckField;
    @FXML private Button prevButton;
    @FXML private Button nextButton;
    @FXML private Label duplicateResLabel;

    private MemberService memberService;

    private boolean idAlreadyExists = true;

    @FXML
    public void initialize(){
        setNextButtonEnabled(false);
        setupUI();
        setupEventHandlers();
        setupDI();
    }

    @FXML
    private void handleClickPrevButton(){
        switchScene("/com/samyukgu/what2wear/member/LoginView.fxml", "로그인");
    }

    @FXML
    private void handleClickNextButton(){
        String accountId = inputIdField.getText();
        String password = inputPasswordField.getText();
        switchSceneWithUserData("/com/samyukgu/what2wear/member/SignupStep2View.fxml", "회원 가입", accountId, password);
    }

    @FXML
    private void handleClickCheckDuplicateButton() {
        String id = inputIdField.getText();
        boolean idPatternOk = isValidId(id);
        idAlreadyExists = false; // TODO: 실제 중복 체크 로직 구현하기

        if (!idPatternOk) {
            showErrorMessage(duplicateResLabel, MemberConstants.ID_CONSTANT);
        } else if (idAlreadyExists) {
            showErrorMessage(duplicateResLabel, "이미 존재하는 아이디입니다.");
        } else {
            showSuccessMessage(duplicateResLabel, "사용 가능한 아이디입니다.");
        }

        validateForm();
    }

    private void setupUI() {
        int arcWidth = 30, arcHeight = 30;
        Rectangle clip = new Rectangle(signupBanner.getFitWidth(), signupBanner.getFitHeight());
        clip.setArcWidth(arcWidth);
        clip.setArcHeight(arcHeight);
        signupBanner.setClip(clip);
    }

    private void setupEventHandlers() {
        inputIdField.addEventHandler(KeyEvent.KEY_RELEASED, e -> handleIdFieldChange());
        inputPasswordField.addEventHandler(KeyEvent.KEY_RELEASED, e -> handlePasswordFieldChange());
        inputPasswordCheckField.addEventHandler(KeyEvent.KEY_RELEASED, e -> handlePasswordCheckFieldChange());
    }

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

    private void handlePasswordCheckFieldChange() {
        validatePasswordMatch();
        validateForm();
    }

    private void validatePasswordMatch() {
        String password = inputPasswordField.getText();
        String passwordCheck = inputPasswordCheckField.getText();

        if (isValidPasswordLength(passwordCheck) && !password.equals(passwordCheck)) {
            showErrorMessage(passwordCheckRes, "비밀번호가 일치하지 않습니다.");
        } else {
            clearLabel(passwordCheckRes);
        }
    }

    private boolean isValidId(String id) {
        return id.matches(MemberConstants.ID_PATTERN);
    }

    private boolean isValidPassword(String password) {
        return password.matches(MemberConstants.PASSWORD_PATTERN);
    }

    private boolean isValidPasswordLength(String password){
        return password.length() >= MemberConstants.PASSWORD_MIN_LENGTH;
    }

    private void showErrorMessage(Label label, String message) {
        label.setText(message);
        label.getStyleClass().removeAll("text-red", "text-green");
        label.getStyleClass().add("text-red");
    }

    private void showSuccessMessage(Label label, String message) {
        label.setText(message);
        label.getStyleClass().removeAll("text-red", "text-green");
        label.getStyleClass().add("text-green");
    }

    private void clearLabel(Label label) {
        label.setText("");
        label.getStyleClass().removeAll("text-red", "text-green");
    }


    private void handleIdFieldChange() {
        idAlreadyExists = true;
        clearLabel(duplicateResLabel);
        validateForm();
    }

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

    private void setNextButtonEnabled(boolean enabled) {
        nextButton.setDisable(!enabled);
        nextButton.getStyleClass().removeAll("btn-gray", "btn-green");
        nextButton.getStyleClass().add(enabled ? "btn-green" : "btn-gray");
        nextButton.setOnAction(enabled ? e -> handleClickNextButton() : null);
    }

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

    private void setupDI() {
        DIContainer diContainer = DIContainer.getInstance();
        memberService = diContainer.resolve(MemberService.class);
    }
}
