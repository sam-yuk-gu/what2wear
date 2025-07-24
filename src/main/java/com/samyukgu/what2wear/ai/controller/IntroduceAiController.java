package com.samyukgu.what2wear.ai.controller;

import com.samyukgu.what2wear.layout.controller.MainLayoutController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;

public class IntroduceAiController {
    @FXML private ComboBox<String> select_location_title;
    @FXML private ComboBox<String> select_title;
    @FXML private RadioButton otherClosetRadio;
    @FXML private RadioButton myClosetRadio;

    private ToggleGroup closetToggleGroup;
    // 선택된 라디오 버튼 값 저장
    public static boolean isMyClosetSelected;

    // 선택값 저장용 static 변수
    public static String selectedLocation;
    public static String selectedPurpose;

    @FXML
    private void initialize() {
        closetToggleGroup = new ToggleGroup();
        otherClosetRadio.setToggleGroup(closetToggleGroup);
        myClosetRadio.setToggleGroup(closetToggleGroup);

        select_location_title.setValue("서울시 종로구");
        select_title.setValue("공부");


        myClosetRadio.setSelected(true); // 기본값
        isMyClosetSelected = true;

        // 옷장 범위 선택 확인
        closetToggleGroup.selectedToggleProperty().addListener((obs, old, selected) -> {
            if (selected == myClosetRadio) {
                isMyClosetSelected = true;
            } else if (selected == otherClosetRadio) {
                isMyClosetSelected = false;
            }
        });
    }

    @FXML
    private void handleRecommendClick() {
        selectedLocation = select_location_title.getValue();
        selectedPurpose = select_title.getValue();

        if (selectedLocation == null || selectedPurpose == null) {
            System.out.println("지역 또는 외출 목적이 선택되지 않음");
            return;
        }

        MainLayoutController.loadView("/com/samyukgu/what2wear/ai/LoadingAi.fxml");
    }

    @FXML
    public void handleCancelClick(ActionEvent event) {
        MainLayoutController.loadView("/com/samyukgu/what2wear/codi/CodiMainView.fxml");
    }
}
