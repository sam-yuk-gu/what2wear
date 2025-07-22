package com.samyukgu.what2wear.codi.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class CodiDetailController {
    @FXML
    private Label welcomeText;

    @FXML
    protected void onHelloButtonClick() {
        welcomeText.setText("코디 페이지");
    }
}