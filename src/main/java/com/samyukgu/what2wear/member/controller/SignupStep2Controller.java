package com.samyukgu.what2wear.member.controller;

import com.samyukgu.what2wear.di.DIContainer;
import com.samyukgu.what2wear.mail.service.AuthService;
import com.samyukgu.what2wear.mail.service.MailService;
import com.samyukgu.what2wear.member.service.MemberService;
import java.io.IOException;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.util.Duration;

public class SignupStep2Controller {
    @FXML private ImageView signupBanner;
    @FXML private TextField inputIdField;
    @FXML private ComboBox<String> domainCombobox;
    @FXML private Button sendMailButton;
    @FXML private Button checkAuthButton;
    @FXML private TextField authMailField;
    @FXML private Label timerLabel;
    @FXML private Label authResLabel;
    @FXML private TextField inputNameField;
    @FXML private TextField inputNicknameField;
    @FXML private Button checkDuplicateButton;
    @FXML private Label nicknameCheckRes;
    @FXML private Button prevButton;
    @FXML private Button completeButton;

    private MemberService memberService;
    private MailService mailService;
    private AuthService authService;

    private Timeline timer;
    private int timeRemaining = 300; // 5분 = 300초
    private boolean isAuthenticated = false;
    private boolean isNicknameChecked = false;
    private String domain = "";
    private String accountId;
    private String password;

    @FXML
    public void initialize(){
        setCompleteButtonEnabled(false);
        setupUI();
        setUpDomainList();
        setupEventHandlers();
        setupDI();
        setupInitialState();
    }

    private void setupInitialState() {
        // 초기 상태: 인증번호 입력 필드 비활성화
        authMailField.setDisable(true);
        timerLabel.setText("");
        authResLabel.setText("");
        nicknameCheckRes.setText("");
    }

    private void setupEventHandlers() {
        // 입력 필드 변경시 폼 검증
        inputNameField.textProperty().addListener((obs, oldVal, newVal) -> validateForm());
        inputNicknameField.textProperty().addListener((obs, oldVal, newVal) -> {
            isNicknameChecked = false; // 닉네임이 변경되면 중복 체크 다시 해야함
            clearLabel(nicknameCheckRes);
            validateForm();
        });
    }

    @FXML
    private void handleClickSendMailButton(){
        String id = inputIdField.getText().trim();
        String selectedDomain = domainCombobox.getValue();

        if (id.isEmpty() || selectedDomain == null || selectedDomain.isEmpty()) {
            showErrorMessage(authResLabel, "이메일 주소를 정확히 입력해주세요.");
            return;
        }

        String address = id + "@" + selectedDomain;

        try {
            authService.sendAuthCode(address);

            // 인증번호 입력 필드 활성화
            authMailField.setDisable(false);
            authMailField.clear();
            authMailField.requestFocus();

            // 타이머 시작
            startTimer();

            showSuccessMessage(authResLabel, "인증번호가 발송되었습니다.");

        } catch (Exception e) {
            showErrorMessage(authResLabel, "메일 발송에 실패했습니다. 다시 시도해주세요.");
        }
    }

    @FXML
    private void handleClickCheckAuthButton(){
        String id = inputIdField.getText().trim();
        String selectedDomain = domainCombobox.getValue();
        String authCode = authMailField.getText().trim();

        if (authCode.isEmpty()) {
            showErrorMessage(authResLabel, "인증번호를 입력해주세요.");
            return;
        }

        String address = id + "@" + selectedDomain;
        boolean isValid = authService.verifyAuthCode(address, authCode);

        if (isValid) {
            // 인증 성공
            isAuthenticated = true;
            stopTimer();
            authMailField.setDisable(true);
            inputIdField.setDisable(true);
            domainCombobox.setDisable(true);
            sendMailButton.setDisable(true);

            showSuccessMessage(authResLabel, "인증되었습니다.");
            validateForm();
        } else {
            // 인증 실패
            showErrorMessage(authResLabel, "인증번호가 잘못되었습니다.");
        }
    }

    @FXML
    private void handleClickCheckDuplicateButton() {
        String nickname = inputNicknameField.getText().trim();

        if (nickname.isEmpty()) {
            showErrorMessage(nicknameCheckRes, "닉네임을 입력해주세요.");
            return;
        }

        // TODO: 실제 중복 체크 로직 구현 (현재는 임시로 false)
        boolean nicknameExists = false; // memberService.checkNicknameDuplicate(nickname);

        if (nicknameExists) {
            isNicknameChecked = false;
            showErrorMessage(nicknameCheckRes, "이미 존재하는 닉네임입니다.");
        } else {
            isNicknameChecked = true;
            showSuccessMessage(nicknameCheckRes, "사용할 수 있는 닉네임입니다.");
        }

        validateForm();
    }

    @FXML
    private void handleClickCompleteButton(){
        // 회원가입 완료 로직
        String name = inputNameField.getText().trim();
        String nickname = inputNicknameField.getText().trim();
        String email = inputIdField.getText().trim() + "@" + domainCombobox.getValue();

        // TODO: 실제 회원가입 로직 구현
        // memberService.createMember(accountId, password, name, nickname, email);
        System.out.println("accountId : " + accountId);
        System.out.println("password : " + password);
        System.out.println("name : " + name);
        System.out.println("nickname : " + nickname);
        System.out.println("email : " + email);

        switchScene("/com/samyukgu/what2wear/member/LoginView.fxml", "로그인");
    }

    @FXML
    private void handleClickPrevButton(){
        switchScene("/com/samyukgu/what2wear/member/LoginView.fxml", "로그인");
    }

    private void startTimer() {
        stopTimer(); // 기존 타이머 정지
        timeRemaining = 300; // 5분 리셋

        timer = new Timeline(new KeyFrame(Duration.seconds(1), e -> {
            timeRemaining--;
            updateTimerDisplay();

            if (timeRemaining <= 0) {
                // 시간 만료
                stopTimer();
                authMailField.setDisable(true);
                showErrorMessage(authResLabel, "인증 시간이 만료되었습니다. 다시 시도해주세요.");
            }
        }));

        timer.setCycleCount(Animation.INDEFINITE);
        timer.play();
        updateTimerDisplay();
    }

    private void stopTimer() {
        if (timer != null) {
            timer.stop();
        }
        timerLabel.setText("");
    }

    private void updateTimerDisplay() {
        int minutes = timeRemaining / 60;
        int seconds = timeRemaining % 60;
        String timeText = String.format("%d:%02d", minutes, seconds);
        timerLabel.setText(timeText);

        // 빨간색 스타일 적용
        timerLabel.getStyleClass().removeAll("text-red", "text-green");
        timerLabel.getStyleClass().add("text-red");
    }

    private void validateForm() {
        String name = inputNameField.getText().trim();
        String nickname = inputNicknameField.getText().trim();

        boolean isValid = !name.isEmpty()
                && !nickname.isEmpty()
                && isAuthenticated
                && isNicknameChecked;

        setCompleteButtonEnabled(isValid);
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

    private void setCompleteButtonEnabled(boolean enabled) {
        completeButton.setDisable(!enabled);
        completeButton.getStyleClass().removeAll("btn-gray", "btn-green");
        completeButton.getStyleClass().add(enabled ? "btn-green" : "btn-gray");
        completeButton.setOnAction(enabled ? e -> handleClickCompleteButton() : null);
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

    private void setupUI() {
        int arcWidth = 30, arcHeight = 30;
        Rectangle clip = new Rectangle(signupBanner.getFitWidth(), signupBanner.getFitHeight());
        clip.setArcWidth(arcWidth);
        clip.setArcHeight(arcHeight);
        signupBanner.setClip(clip);
    }

    private void setUpDomainList(){
        domainCombobox.getItems().addAll(
                "naver.com",
                "gmail.com",
                "daum.net",
                "nate.com",
                "hotmail.com",
                "outlook.com",
                "yahoo.com",
                "직접입력"
        );

        domainCombobox.setEditable(false);

        // 직접입력 요소를 선택했을시에만 직접 입력 활성화
        domainCombobox.valueProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue ov, String oldValue, String newValue) {
                if (newValue == null) return;

                if ("직접입력".equals(newValue)) {
                    domainCombobox.setEditable(true);
                    domainCombobox.getEditor().clear();
                    domainCombobox.requestFocus();
                } else {
                    domainCombobox.setEditable(false);
                    domain = newValue;
                }
            }
        });

        // 포커스를 잃을 때
        domainCombobox.focusedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                if (!newValue && domainCombobox.isEditable()) {
                    String currentValue = domainCombobox.getValue();
                    if (currentValue == null || currentValue.trim().isEmpty()) {
                        if (domainCombobox.getItems().contains("naver.com")) {
                            domainCombobox.setValue("naver.com");
                        }
                    } else {
                        domainCombobox.setValue(currentValue.trim());
                    }
                    domainCombobox.setEditable(false);
                    domain = domainCombobox.getValue();
                }
            }
        });
    }

    private void setupDI() {
        DIContainer diContainer = DIContainer.getInstance();
        memberService = diContainer.resolve(MemberService.class);
        mailService = diContainer.resolve(MailService.class);
        authService = diContainer.resolve(AuthService.class);
    }

    public void setUserData(String accountId, String password) {
        this.accountId = accountId;
        this.password = password;
    }

    // 컨트롤러가 종료될 때 타이머 정리
    public void cleanup() {
        stopTimer();
        if (authService != null) {
            authService.shutdown();
        }
    }
}