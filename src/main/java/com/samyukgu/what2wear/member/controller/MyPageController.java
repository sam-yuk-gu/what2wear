package com.samyukgu.what2wear.member.controller;

import com.samyukgu.what2wear.common.controller.CustomModalController;
import com.samyukgu.what2wear.common.controller.MainLayoutController;
import com.samyukgu.what2wear.common.util.CircularImageUtil;
import com.samyukgu.what2wear.common.util.HoverEffectUtil;
import com.samyukgu.what2wear.di.DIContainer;
import com.samyukgu.what2wear.member.Session.MemberSession;
import com.samyukgu.what2wear.member.model.Member;
import com.samyukgu.what2wear.member.service.MemberService;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class MyPageController {
    @FXML public StackPane root;
    @FXML ImageView profileImg;
    @FXML private Button changeProfileImgButton;
    @FXML private Button fixNicknameButton;
    @FXML private Label fixMemberPasswordLabel;
    @FXML private Label memberNicknameLabel;
    @FXML private Label memberAccountIdLabel;
    @FXML private Label memberNameLabel;
    @FXML private Label memberEmailLabel;
    @FXML private Label fixMemberInfoLabel;
    @FXML private Label unregisterLabel;

    private MemberService memberService;
    private MemberSession memberSession;
    private Member member;

    @FXML
    public void initialize(){
        setupDI();
        loadMember();
        loadUserInfo();
        setupHoverEffects();
    }

    private void loadUserInfo(){
        memberNicknameLabel.setText(member.getNickname());
        memberAccountIdLabel.setText(member.getAccount_id());
        memberNameLabel.setText(member.getName());
        memberEmailLabel.setText(member.getEmail());
        CircularImageUtil.applyCircularImageToExistingImageView(profileImg, 150.0, member.getProfile_img());
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

    @FXML
    public void handleClickChangeProfileImgButton(){
        // FileChooser 생성
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("프로필 이미지 선택");

        // 파일 확장자 필터 설정 (이미지 파일만)
        FileChooser.ExtensionFilter imageFilter = new FileChooser.ExtensionFilter(
                "이미지 파일 (*.jpg, *.jpeg, *.png)", "*.jpg", "*.jpeg", "*.png");
        fileChooser.getExtensionFilters().add(imageFilter);

        // 초기 디렉토리 설정 (선택사항)
        fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));

        try {
            // 파일 선택 다이얼로그 열기
            Stage stage = (Stage) root.getScene().getWindow();
            File selectedFile = fileChooser.showOpenDialog(stage);

            if (selectedFile != null) {
                // 파일을 바이트 배열로 변환
                byte[] imageBytes = Files.readAllBytes(selectedFile.toPath());

                // 프로필 이미지 변경 서비스 호출
                Member info = memberService.changeProfileImg(member.getId(), imageBytes);
                member.setProfile_img(info.getProfile_img());

                loadUserInfo();
                // 성공 메시지 (선택사항)
                showSuccessModal("성공", "프로필 이미지가 변경되었습니다!",
                        "/assets/icons/greenCheck.png", "#4CAF50");


            } else {
                showSuccessModal("실패", "프로필 이미지 변경에 실패하였습니다.",
                        "/assets/icons/redCheck.png", "#FA7B7F");
            }

        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("파일 읽기 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    @FXML public void handleClickFixNickNameButton(){
        MainLayoutController.loadView("/com/samyukgu/what2wear/member/MemberNicknameFixView.fxml");
    }

    @FXML public void handleClickFixMemberInfoLabel(){
         MainLayoutController.loadView("/com/samyukgu/what2wear/member/MemberInfoFixView.fxml");
    }

    @FXML public void handleClickFixPasswordLabel(){ //
         MainLayoutController.loadView("/com/samyukgu/what2wear/member/MemberPasswordFixView.fxml");
    }

    @FXML public void handleClickUnregisterLabel(){
         MainLayoutController.loadView("/com/samyukgu/what2wear/member/MemberUnregisterView.fxml");
    }

    // 호버 효과 설정
    private void setupHoverEffects() {
        // 클릭 가능한 라벨들에 호버 효과 추가
        HoverEffectUtil.addClickableEffectToLabels(fixMemberInfoLabel, fixMemberPasswordLabel, unregisterLabel);

        // 버튼들에 호버 효과 추가
        HoverEffectUtil.addHoverEffectToButtons(changeProfileImgButton, fixNicknameButton);
    }

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
                    }
            );

            root.getChildren().add(modal);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}