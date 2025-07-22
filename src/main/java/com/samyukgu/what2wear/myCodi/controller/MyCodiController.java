package com.samyukgu.what2wear.myCodi.controller;

import com.samyukgu.what2wear.layout.controller.MainLayoutController;
import com.samyukgu.what2wear.di.DIContainer;
import com.samyukgu.what2wear.myCodi.service.MyCodiService;
import javafx.fxml.FXML;
import javafx.scene.control.Button;

public class MyCodiController {
    private MyCodiService myCodiService;

    @FXML
    public void initialize() {
        DIContainer diContainer = DIContainer.getInstance();
        this.myCodiService = diContainer.resolve(MyCodiService.class);
    }

    @FXML
    private void handleAddMyCodiClick() {
        MainLayoutController.loadView("/com/samyukgu/what2wear/mycodi/myCodiAdd.fxml");
    }

    @FXML
    private void handleWardrobeClick() {
        MainLayoutController.loadView("/com/samyukgu/what2wear/wardrobe/wardrobeList.fxml");
    }

    @FXML
    private void handleImgClick() {
        MainLayoutController.loadView("/com/samyukgu/what2wear/mycodi/myCodiDetail.fxml");
    }
}
