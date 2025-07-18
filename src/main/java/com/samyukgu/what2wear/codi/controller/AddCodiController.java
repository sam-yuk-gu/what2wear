package com.samyukgu.what2wear.codi.controller;

import com.samyukgu.what2wear.common.controller.MainLayoutController;
import javafx.fxml.FXML;

public class AddCodiController {

    @FXML
    private void handleClickBack() {
        MainLayoutController.loadView("/com/samyukgu/what2wear/codi/CodiMainView.fxml");
    }
}
