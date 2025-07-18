package com.samyukgu.what2wear.codi.controller;

import com.samyukgu.what2wear.common.controller.MainLayoutController;
import javafx.fxml.FXML;

public class CodiMainController {

    @FXML
    private void handleClickAddCodi() {
        MainLayoutController.loadView("/com/samyukgu/what2wear/codi/AddCodiView.fxml");
    }
}
