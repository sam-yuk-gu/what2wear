package com.samyukgu.what2wear.member.controller;

import com.samyukgu.what2wear.common.controller.CustomModalController;
import com.samyukgu.what2wear.common.util.HoverEffectUtil;
import com.samyukgu.what2wear.di.DIContainer;
import com.samyukgu.what2wear.layout.controller.MainLayoutController;
import com.samyukgu.what2wear.mail.service.AuthService;
import com.samyukgu.what2wear.member.Session.MemberSession;
import com.samyukgu.what2wear.member.common.MemberConstants;
import com.samyukgu.what2wear.member.model.Member;
import com.samyukgu.what2wear.member.service.MemberService;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;

public class MemberEmailFixController {
    @FXML public StackPane root;

    @FXML private Button cancelButton;
    @FXML private Button sendButton;
    @FXML private TextField authMailField;
    @FXML private TextField inputEmailField;
    @FXML private Label timerLabel;
    @FXML private Label authResLabel;
    @FXML private Label inputResLabel;
    @FXML private Button confirmButton;
    @FXML private Button authButton;

    private MemberService memberService;
    private MemberSession memberSession;
    private AuthService authService;
    private Member member;
    private Timeline timer;
    private int timeRemaining = 300; // 5분 = 300초

    @FXML
    public void initialize(){
        setupDI();
        loadMember();
        setupField();
        setupHoverEffects();
    }

    // 초기 입력 필드(인증, 확인, ) 비활성화
    private void setupField(){
        authMailField.setDisable(true);
        confirmButton.setDisable(true);
        authButton.setDisable(true);
    }

    @FXML public void handleClickConfirmButton(){
        showConfirmationModal();
    }

    @FXML public void handleClickCancelButton(){
        MainLayoutController.loadView("/com/samyukgu/what2wear/member/MemberInfoFixView.fxml");
    }

    // 호버 효과 설정
    private void setupHoverEffects() {
        // 버튼들에 호버 효과 추가
        HoverEffectUtil.addHoverEffectToButtons(cancelButton, confirmButton, sendButton, authButton);
    }

    @FXML
    private void handleClickSendMailButton(){
        String address = inputEmailField.getText();

        if (!inputEmailField.getText().matches(MemberConstants.EMAIL_PATTERN)) {
            showErrorMessage(inputResLabel, "이메일 주소를 정확히 입력해주세요.");
            return;
        }

        try {
            authService.sendAuthCode(address);

            // 인증번호 입력 필드 활성화
            authMailField.setDisable(false);
            authMailField.clear();
            authMailField.requestFocus();
            authButton.setDisable(false);

            // 타이머 시작
            startTimer();

            showSuccessMessage(inputResLabel, "인증번호가 발송되었습니다.");

            authMailField.setDisable(false);

        } catch (Exception e) {
            showErrorMessage(inputResLabel, "메일 발송에 실패했습니다. 다시 시도해주세요.");
        }
    }

    // 입력한 인증번호와 저장되어있는 인증번호와 같은지 검증
    @FXML
    private void handleClickCheckAuthButton(){
        String address = inputEmailField.getText();
        String authCode = authMailField.getText().trim();

        if (authCode.isEmpty()) {
            showErrorMessage(authResLabel, "인증번호를 입력해주세요.");
            return;
        }

        boolean isValid = authService.verifyAuthCode(address, authCode);

        if (isValid) {
            // 인증 성공
            stopTimer();
            authMailField.setDisable(true);
            inputEmailField.setDisable(true);
            sendButton.setDisable(true);
            confirmButton.setDisable(false);
            confirmButton.getStyleClass().remove("btn-white-semi-bold");
            confirmButton.getStyleClass().add("btn-green-semi-bold");

            showSuccessMessage(authResLabel, "인증되었습니다.");
        } else {
            // 인증 실패
            showErrorMessage(authResLabel, "인증번호가 잘못되었습니다.");
        }
    }

    // 컨테이너에서 service와 session 찾아서 필드에 할당
    private void setupDI() {
        DIContainer diContainer = DIContainer.getInstance();
        memberService = diContainer.resolve(MemberService.class);
        memberSession = diContainer.resolve(MemberSession.class);
        authService = diContainer.resolve(AuthService.class);
    }

    // 컨테이너에서 주입받은 memberSession의 Member 인스턴스 필드 변수에 주입
    private void loadMember(){
        member = memberSession.getMember();
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

    // 타이머 멈추기
    private void stopTimer() {
        if (timer != null) {
            timer.stop();
        }
        timerLabel.setText("");
    }

    // 타이머 실시간 반영
    private void updateTimerDisplay() {
        int minutes = timeRemaining / 60;
        int seconds = timeRemaining % 60;
        String timeText = String.format("%d:%02d", minutes, seconds);
        timerLabel.setText(timeText);

        // 빨간색 스타일 적용
        timerLabel.getStyleClass().removeAll("text-red", "text-green");
        timerLabel.getStyleClass().add("text-red");
    }

    // label에 에러 메시지를 출력
    private void showErrorMessage(Label label, String message) {
        label.setText(message);
        label.getStyleClass().removeAll("text-red", "text-green");
        label.getStyleClass().add("text-red");
    }

    // label에 성공 메시지를 출력
    private void showSuccessMessage(Label label, String message) {
        label.setText(message);
        label.getStyleClass().removeAll("text-red", "text-green");
        label.getStyleClass().add("text-green");
    }

    // 요청 모달
    private void showConfirmationModal() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/samyukgu/what2wear/common/CustomModal.fxml"));
            StackPane modal = loader.load();

            CustomModalController controller = loader.getController();
            controller.configure(
                    "메일 변경",
                    "정말 메일 주소를 변경하시겠습니까 ?",
                    "",
                    "#8E8E8E",
                    "취소",
                    "확인",
                    () -> root.getChildren().remove(modal),
                    () -> {
                        try{
                            // todo : 메일 주소 필드 변수로 유지하기
                            String newEmail = inputEmailField.getText();
                            root.getChildren().remove(modal);
                            memberService.changeEmail(member.getId(), newEmail);
                            showSuccessModal("성공", "메일 주소가 성공적으로 변경되었습니다!",
                                    "/assets/icons/greenCheck.png", "#4CAF50");
                            member.setEmail(newEmail);
                        }catch (RuntimeException e){
                            root.getChildren().remove(modal);
                            showSuccessModal("실패", "메일 주소 변경에 실패했습니다.",
                                    "/assets/icons/redCheck.png", "#FA7B7F");
                            e.printStackTrace();
                        }
                    }
            );

            root.getChildren().add(modal);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 결과 모달
    private void showSuccessModal(String title, String desc, String iconPath, String themeColor) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/samyukgu/what2wear/common/CustomModal.fxml"));
            StackPane modal = loader.load();

            CustomModalController controller = loader.getController();
            controller.configure(
                    title,
                    desc,
                    iconPath,
                    themeColor,
                    "확인",
                    () -> {
                        root.getChildren().remove(modal);
                        MainLayoutController.loadView("/com/samyukgu/what2wear/member/MyPageView.fxml");
                    }
            );

            root.getChildren().add(modal);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
