package com.samyukgu.what2wear.common.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

public class BasicHeaderController {

    @FXML private Button backButton;
    @FXML private Label titleLabel;

    private Runnable onBackAction;

    @FXML
    public void initialize() {
        setBackButtonVisible(false); // 기본은 비활성화
    }

    public void setTitle(String title) {
        titleLabel.setText(title);
    }

    public void setOnBackAction(Runnable action) {
        this.onBackAction = action;
        setBackButtonVisible(true);
    }

    public void setBackButtonVisible(boolean visible) {
        backButton.setVisible(visible);
    }

    @FXML
    private void handleBack() {
        if (onBackAction != null) {
            onBackAction.run();
        }
    }
}
