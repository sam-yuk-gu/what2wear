package com.samyukgu.what2wear.ai.controller;

import com.samyukgu.what2wear.common.controller.MainLayoutController;
import javafx.fxml.FXML;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;

public class IntroduceAiController {

    @FXML private RadioButton agreeLocationRadio;
    @FXML private RadioButton disagreeLocationRadio;

    @FXML private RadioButton otherClosetRadio;
    @FXML private RadioButton myClosetRadio;

    private ToggleGroup locationGroup;
    private ToggleGroup closetToggleGroup;

    @FXML
    private void initialize() {
        // ToggleGroup 논리적으로 묶기 (위치 동의)
        locationGroup = new ToggleGroup();
        agreeLocationRadio.setToggleGroup(locationGroup);
        disagreeLocationRadio.setToggleGroup(locationGroup);

        // ToggleGroup 논리적으로 묶기 (옷장 범위)
        closetToggleGroup = new ToggleGroup();
        otherClosetRadio.setToggleGroup(closetToggleGroup);
        myClosetRadio.setToggleGroup(closetToggleGroup);
    }

    // 추천받기 버튼 클릭 시
    @FXML
    private void handleRecommendClick() {
        // 위치 동의 여부
        boolean isLocationAgreed = agreeLocationRadio.isSelected();

        // 선택된 옷장
        String closetChoice = myClosetRadio.isSelected() ? "나의 옷장" : "다른 사람 옷장";

        System.out.println("위치 동의 여부: " + isLocationAgreed);
        System.out.println("선택한 옷장: " + closetChoice);

        MainLayoutController.loadView("/com/samyukgu/what2wear/ai/LoadingAi.fxml");
    }
}
