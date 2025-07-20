package com.samyukgu.what2wear.common.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

public class PostHeaderController {

    @FXML private Button backButton;
    @FXML private Label header_text;

    private Runnable onBackAction;

    @FXML
    public void initialize() {
        // 기본 상태는 백버튼 숨김
        setBackButtonVisible(false);
    }

    public void setBackButtonVisible(boolean visible) {
        if (backButton != null) {
            backButton.setVisible(visible);
        }
    }

    public void setOnBackAction(Runnable action) {
        this.onBackAction = action;
        setBackButtonVisible(true);
    }

    // 뒤로가기 버튼 핸들러
    @FXML
    private void handleBack() {
        if (onBackAction != null) {
            onBackAction.run();
        }
    }

    // 헤더 타이틀 설정
    public void setTitle(String title) {
        if (header_text != null) {
            header_text.setText(title);
        }
    }
}
