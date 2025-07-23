package com.samyukgu.what2wear.member.controller;

import com.samyukgu.what2wear.common.controller.CustomModalController;
import com.samyukgu.what2wear.common.controller.MainLayoutController;
import com.samyukgu.what2wear.common.util.HoverEffectUtil;
import com.samyukgu.what2wear.di.DIContainer;
import com.samyukgu.what2wear.member.Session.MemberSession;
import com.samyukgu.what2wear.member.common.MemberConstants;
import com.samyukgu.what2wear.member.model.Member;
import com.samyukgu.what2wear.member.service.MemberService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.layout.StackPane;

public class MemberPasswordFixController {
    @FXML private Label passwordRule;
    @FXML private StackPane root;
    @FXML private PasswordField curPassword;
    @FXML private PasswordField newPassword;
    @FXML private PasswordField confirmNewPassword;
    @FXML private Button cancelButton;
    @FXML private Button confirmButton;
    private MemberService memberService;
    private MemberSession memberSession;
    private Member member;

    @FXML
    public void initialize(){
        setupDI();
        setupField();
        loadMember();
        setupHoverEffects();
    }

    private void setupField() {
        passwordRule.setText(MemberConstants.PW_CONSTANT);
    }

    private void setupHoverEffects() {
        // 버튼들에 호버 효과 추가
        HoverEffectUtil.addHoverEffectToButtons(cancelButton, confirmButton);
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

    @FXML public void handleClickConfirmButton(){
        showConfirmationModal();
    }

    @FXML public void handleClickCancelButton(){
        MainLayoutController.loadView("/com/samyukgu/what2wear/member/MyPageView.fxml");
    }

    // 요청 모달
    private void showConfirmationModal() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/samyukgu/what2wear/common/CustomModal.fxml"));
            StackPane modal = loader.load();

            CustomModalController controller = loader.getController();
            controller.configure(
                    "비밀번호 변경",
                    "정말 비밀번호를 변경하시겠습니까 ?",
                    "",
                    "#8E8E8E",
                    "취소",
                    "확인",
                    () -> root.getChildren().remove(modal),
                    () -> {
                        try{
                            String curP = curPassword.getText();
                            String newP = newPassword.getText();
                            String confirmNewP = confirmNewPassword.getText();

                            root.getChildren().remove(modal);

                            if(memberService.isValidToChange(member.getPassword(),curP, newP, confirmNewP)){
                                Member info = memberService.changePassword(member.getId(), newP);
                                
                                showSuccessModal("성공", "비밀번호가 성공적으로 변경되었습니다!",
                                        "/assets/icons/greenCheck.png", "#4CAF50");
                                member.setPassword(info.getPassword());
                            }else{
                                showSuccessModal("실패", "값이 조건에 맞는지 다시 확인해주세요.",
                                        "/assets/icons/redCheck.png", "#FA7B7F");
                            }
                        }catch (RuntimeException e){
                            root.getChildren().remove(modal);
                            showSuccessModal("실패", "비밀번호 변경에 실패했습니다.",
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
