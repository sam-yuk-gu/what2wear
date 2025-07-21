package com.samyukgu.what2wear.wardrobe.controller;

import com.samyukgu.what2wear.common.controller.MainLayoutController;
import com.samyukgu.what2wear.di.DIContainer;
import com.samyukgu.what2wear.wardrobe.model.Wardrobe;
import com.samyukgu.what2wear.wardrobe.service.WardrobeService;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;

public class WardrobeController {
    private WardrobeService wardrobeService;

    private Wardrobe selectedItem;

    @FXML
    public void initialize() {
        DIContainer diContainer = DIContainer.getInstance();
        this.wardrobeService = diContainer.resolve(WardrobeService.class);
    }

    @FXML
    private void handleAddWardrobeClick() {
        MainLayoutController.loadView("/com/samyukgu/what2wear/wardrobe/wardrobeAdd.fxml");
    }

    @FXML
    private void handleMyCodiClick() {
        MainLayoutController.loadView("/com/samyukgu/what2wear/myCodi/myCodiList.fxml");
    }

    @FXML
    private void handleImgClick() {
        MainLayoutController.loadView("/com/samyukgu/what2wear/wardrobe/wardrobeDetail.fxml");
    }

}
