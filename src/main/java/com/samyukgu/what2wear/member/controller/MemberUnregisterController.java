package com.samyukgu.what2wear.member.controller;

import com.samyukgu.what2wear.common.controller.CustomModalController;
import com.samyukgu.what2wear.common.util.HoverEffectUtil;
import com.samyukgu.what2wear.di.DIContainer;
import com.samyukgu.what2wear.layout.controller.MainLayoutController;
import com.samyukgu.what2wear.member.Session.MemberSession;
import com.samyukgu.what2wear.member.model.Member;
import com.samyukgu.what2wear.member.service.MemberService;
import java.io.IOException;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class MemberUnregisterController {
    @FXML private StackPane root;
    @FXML private PasswordField curPassword;
    @FXML private Button cancelButton;
    @FXML private Button confirmButton;
    private MemberService memberService;
    private MemberSession memberSession;
    private Member member;
    private boolean isUnregister = false;

    @FXML
    public void initialize(){
        setupDI();
        loadMember();
        setupHoverEffects();
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
                    "회원 탈퇴",
                    "정말 탈퇴하시겠습니까?",
                    "/assets/icons/redCheck.png",
                    "#FA7B7F",
                    "취소",
                    "확인",
                    () -> root.getChildren().remove(modal),
                    () -> {
                        try{
                            root.getChildren().remove(modal);

                            if(memberService.isPasswordMatched(member.getId(), curPassword.getText())){
                                Member info = memberService.unregisterMember(member.getId());

                                showSuccessModal("성공", "회원 탈퇴가 완료되었습니다.",
                                        "/assets/icons/greenCheck.png", "#4CAF50");
                                member.setDeleted(info.getPassword());
                                isUnregister = true;
                            }else{
                                showSuccessModal("실패", "회원 탈퇴에 실패했습니다.",
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
                        if(isUnregister){
                            memberSession.clearMember();
                            switchScene("/com/samyukgu/what2wear/member/LoginView.fxml", "로그인");
                        }
                        else
                            MainLayoutController.loadView("/com/samyukgu/what2wear/member/MyPageView.fxml");
                    }
            );

            root.getChildren().add(modal);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void switchScene(String fxmlPath, String title){
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent newRoot = loader.load();  // 변수명 변경으로 혼동 방지

            // 현재 Stage 가져오기 (기존 화면에서)
            Stage stage = (Stage) this.root.getScene().getWindow();
            Scene scene = new Scene(newRoot, 1280, 768);
            stage.setScene(scene);
            stage.setTitle(title);
            stage.setResizable(false);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
