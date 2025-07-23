package com.samyukgu.what2wear.member.controller;

import com.samyukgu.what2wear.common.controller.CustomModalController;
import com.samyukgu.what2wear.common.controller.MainLayoutController;
import com.samyukgu.what2wear.common.util.HoverEffectUtil;
import com.samyukgu.what2wear.di.DIContainer;
import com.samyukgu.what2wear.member.Session.MemberSession;
import com.samyukgu.what2wear.member.model.Member;
import com.samyukgu.what2wear.member.service.MemberService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.StackPane;

public class MemberNameFixController {
    @FXML public StackPane root;
    @FXML private TextField memberInputName;
    @FXML private Button cancelButton;
    @FXML private Button confirmButton;
    private MemberService memberService;
    private MemberSession memberSession;
    private Member member;

    @FXML
    public void initialize(){
        setupDI();
        loadMember();
        loadMemberInfo();
        setupHoverEffects();
    }

    @FXML public void handleClickConfirmButton(){
        showConfirmationModal();
    }

    @FXML public void handleClickCancelButton(){
        MainLayoutController.loadView("/com/samyukgu/what2wear/member/MemberInfoFixView.fxml");
    }

    // 사용자 정보 불러오기
    private void loadMemberInfo(){
        memberInputName.setText(member.getName());
    }

    // 호버 효과 설정
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

    // 요청 모달
    private void showConfirmationModal() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/samyukgu/what2wear/common/CustomModal.fxml"));
            StackPane modal = loader.load();

            CustomModalController controller = loader.getController();
            controller.configure(
                    "이름 변경",
                    "정말 이름을 변경하시겠습니까 ?",
                    "",
                    "#8E8E8E",
                    "취소",
                    "확인",
                    () -> root.getChildren().remove(modal),
                    () -> {
                        try{
                            String newName = memberInputName.getText();
                            root.getChildren().remove(modal);
                            memberService.changeName(member.getId(), newName);
                            showSuccessModal("성공", "이름이 성공적으로 변경되었습니다!",
                                    "/assets/icons/greenCheck.png", "#4CAF50");
                            member.setName(newName);
                        }catch (RuntimeException e){
                            root.getChildren().remove(modal);
                            showSuccessModal("실패", "이름 변경에 실패했습니다.",
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
