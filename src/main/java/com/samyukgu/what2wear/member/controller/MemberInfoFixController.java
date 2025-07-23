package com.samyukgu.what2wear.member.controller;

import com.samyukgu.what2wear.common.controller.MainLayoutController;
import com.samyukgu.what2wear.common.util.HoverEffectUtil;
import com.samyukgu.what2wear.di.DIContainer;
import com.samyukgu.what2wear.member.Session.MemberSession;
import com.samyukgu.what2wear.member.model.Member;
import com.samyukgu.what2wear.member.service.MemberService;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

public class MemberInfoFixController {
    @FXML private Label memberEmailLabel;
    @FXML private Label memberNameLabel;
    @FXML private Button fixNameButton;
    @FXML private Button fixNicknameButton;
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

    // 사용자 정보 불러오기
    private void loadMemberInfo(){
        memberNameLabel.setText(member.getName());
        memberEmailLabel.setText(member.getEmail());
    }

    private void setupHoverEffects() {
        // 버튼들에 호버 효과 추가
        HoverEffectUtil.addHoverEffectToButtons(fixNameButton, fixNicknameButton);
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

    @FXML public void handleClickFixNameButton(){
        MainLayoutController.loadView("/com/samyukgu/what2wear/member/MemberNameFixView.fxml");
    }

    @FXML public void handleClickFixEmailButton(){
        MainLayoutController.loadView("/com/samyukgu/what2wear/member/MemberEmailFixView.fxml");
    }
}
