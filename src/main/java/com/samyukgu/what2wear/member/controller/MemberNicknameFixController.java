package com.samyukgu.what2wear.member.controller;

import com.samyukgu.what2wear.common.controller.CustomModalController;
import com.samyukgu.what2wear.common.util.HoverEffectUtil;
import com.samyukgu.what2wear.di.DIContainer;
import com.samyukgu.what2wear.layout.controller.MainLayoutController;
import com.samyukgu.what2wear.member.Session.MemberSession;
import com.samyukgu.what2wear.member.model.Member;
import com.samyukgu.what2wear.member.service.MemberService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.StackPane;

public class MemberNicknameFixController {


    @FXML public StackPane root;
    @FXML private TextField memberInputNickname;
    @FXML private Label duplicateResLabel;
    @FXML private Button cancelButton;
    @FXML private Button confirmButton;
    @FXML private Button duplicateCheckButton;

    private MemberService memberService;
    private MemberSession memberSession;
    private Member member;

    private boolean idAlreadyExists = true;

    @FXML
    public void initialize(){
        setupDI();
        loadMember();
        loadMemberInfo();
        setupField();
        setupEventHandlers();
        setConfirmButton(false);
        setupHoverEffects();
    }

    private void setupEventHandlers() {
        memberInputNickname.addEventHandler(KeyEvent.KEY_RELEASED, e -> {
            duplicateResLabel.setText("");
            idAlreadyExists = true;
            validateForm();
        });
    }

    private void validateForm() {
        boolean isValid = !idAlreadyExists;
        setConfirmButton(isValid);
    }

    private void setConfirmButton(boolean enabled) {
        confirmButton.setDisable(!enabled);
        confirmButton.getStyleClass().removeAll("btn-gray-semi-bold", "btn-green-semi-bold");
        confirmButton.getStyleClass().add(enabled ? "btn-green-semi-bold" : "btn-gray-semi-bold");
        confirmButton.setOnAction(enabled ? e -> handleClickConfirmButton() : null);
    }

    private void setupField(){
        confirmButton.setDisable(true);
    }

    private void setupHoverEffects() {
        // 버튼들에 호버 효과 추가
        HoverEffectUtil.addHoverEffectToButtons(cancelButton, confirmButton, duplicateCheckButton);
    }

    // 사용자 정보 불러오기
    private void loadMemberInfo(){
//        memberNicknameLabel.setText(member.getNickname());
    }

    @FXML public void handleClickConfirmButton(){
        showConfirmationModal();
    }

    @FXML public void handleClickCancelButton(){
        MainLayoutController.loadView("/com/samyukgu/what2wear/member/MyPageView.fxml");
    }

    @FXML public void handleDuplicateCheckButton(){
        if(memberService.existsByNickname(memberInputNickname.getText())){
            duplicateResLabel.setText("이미 존재하는 닉네임입니다.");

            duplicateResLabel.getStyleClass().remove("text-green");
            duplicateResLabel.getStyleClass().add("text-red");
            return;
        }

        duplicateResLabel.setText("사용 가능한 닉네임입니다!");
        duplicateResLabel.getStyleClass().remove("text-red");
        duplicateResLabel.getStyleClass().add("text-green");
        setConfirmButton(true);
    }

    // 컨테이너에서 service와 session 찾아서 필드에 할당
    private void setupDI() {
        DIContainer diContainer = DIContainer.getInstance();
        memberService = diContainer.resolve(MemberService.class);
        memberSession = diContainer.resolve(MemberSession.class);
    }

    // 컨테이너에서 주입받은 memberSession의 Member 인스턴스 필드 변수에 주입
    private void loadMember(){
        member = memberSession.getMember();
    }

    // 요청 모달
    private void showConfirmationModal() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/samyukgu/what2wear/common/CustomModal.fxml"));
            StackPane modal = loader.load();

            CustomModalController controller = loader.getController();
            controller.configure(
                    "닉네임 변경",
                    "정말 닉네임을 변경하시겠습니까 ?",
                    "",
                    "#8E8E8E",
                    "취소",
                    "확인",
                    () -> root.getChildren().remove(modal),
                    () -> {
                        try{
                            String newName = memberInputNickname.getText();
                            root.getChildren().remove(modal);
                            memberService.changeNickname(member.getId(), newName);
                            showSuccessModal("성공", "닉네임이 성공적으로 변경되었습니다!",
                                    "/assets/icons/greenCheck.png", "#4CAF50");
                            member.setNickname(newName);
                        }catch (RuntimeException e){
                            root.getChildren().remove(modal);
                            showSuccessModal("실패", "닉네임 변경에 실패했습니다.",
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
