package com.samyukgu.what2wear.member.controller;

import com.samyukgu.what2wear.di.DIContainer;
import com.samyukgu.what2wear.member.service.MemberService;
import java.io.IOException;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

public class SignupStep2Controller {
    public ImageView signupBanner;
    public TextField inputIdField;
    public ComboBox domainCombobox;
    public Button sendMailButton;
    public PasswordField authMailField;
    public PasswordField inputNameField;
    public PasswordField inputNicknameField;
    public Button checkDuplicateNickname;
    public Button prevButton;
    public Button completeButton;

    private MemberService memberService;

    String domain = "";

    @FXML
    public void initialize(){
        setCompleteButtonEnabled(false);
        setupUI();
        setUpDomainList();
        setupEventHandlers();
        setupDI();
    }

    private void setupEventHandlers() {
        // 닉네임 제한 ++
//        inputPasswordField.addEventHandler(KeyEvent.KEY_RELEASED, e -> handlePasswordFieldChange());
//        inputPasswordCheckField.addEventHandler(KeyEvent.KEY_RELEASED, e -> handlePasswordCheckFieldChange());
    }

    @FXML
    private void handleClickCompleteButton(){
        switchScene("/com/samyukgu/what2wear/member/LoginView.fxml.fxml", "로그인");
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
        // 초기에는 직접 입력 비활성화

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
                    domainCombobox.requestFocus(); // 포커스 주기
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
                    // 포커스를 잃었고 편집 모드일 때
                    String currentValue = (String)domainCombobox.getValue();
                    if (currentValue == null || currentValue.trim().isEmpty()) {
                        // 아무것도 입력하지 않았으면 첫 번째 항목으로 되돌리기
                        if (domainCombobox.getItems().contains("naver.com")) {
                            domainCombobox.setValue("naver.com");
                        }
                    } else {
                        domainCombobox.setValue(currentValue.trim());
                    }
                    domainCombobox.setEditable(false);
                    domain = (String)domainCombobox.getValue();
                }
            }
        });


    }

    private void setupDI() {
        DIContainer diContainer = DIContainer.getInstance();
        memberService = diContainer.resolve(MemberService.class);
//        mailService = diContainer.resolve(MailService.class);
    }
}
