package com.samyukgu.what2wear.wardrobe.controller;

import com.samyukgu.what2wear.di.DIContainer;
import com.samyukgu.what2wear.wardrobe.service.WardrobeService;
import javafx.fxml.FXML;

public class WardrobeController {
    private WardrobeService wardrobeService;

    @FXML
    public void initialize() {
        DIContainer diContainer = DIContainer.getInstance();
        this.wardrobeService = diContainer.resolve(WardrobeService.class);
    }
}
