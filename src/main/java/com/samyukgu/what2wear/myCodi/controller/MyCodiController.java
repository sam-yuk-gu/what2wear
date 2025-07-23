//package com.samyukgu.what2wear.myCodi.controller;
//
//import com.samyukgu.what2wear.common.controller.MainLayoutController;
//import com.samyukgu.what2wear.di.DIContainer;
//import com.samyukgu.what2wear.myCodi.service.CodiService;
//import javafx.fxml.FXML;
//
//public class MyCodiController {
//    private CodiService myCodiService;
//    private final CodiService codiService = DIContainer.getInstance().resolve(CodiService.class);
//
//    @FXML
//    public void initialize() {
//    }
//
//    @FXML
//    private void handleAddMyCodiClick() {
//        MainLayoutController.loadView("/com/samyukgu/what2wear/mycodi/myCodiAdd.fxml");
//    }
//
//    @FXML
//    private void handleWardrobeClick() {
//        MainLayoutController.loadView("/com/samyukgu/what2wear/wardrobe/wardrobeList.fxml");
//    }
//
//    @FXML
//    private void handleImgClick() {
//        MainLayoutController.loadView("/com/samyukgu/what2wear/mycodi/myCodiDetail.fxml");
//    }
//}
