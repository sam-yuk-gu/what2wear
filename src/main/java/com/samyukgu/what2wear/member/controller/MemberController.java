package com.samyukgu.what2wear.member.controller;

import com.samyukgu.what2wear.di.DIContainer;
import com.samyukgu.what2wear.member.service.MemberService;
import javafx.fxml.FXML;

public class MemberController {
    private MemberService memberService;

    @FXML
    public void initialize(){
        DIContainer diContainer = DIContainer.getInstance();
        this.memberService = diContainer.resolve(MemberService.class);
    }
}
