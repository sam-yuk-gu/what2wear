package com.samyukgu.what2wear.myCodi.controller;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class ClothesSelectionModal {

    public static void showModal(Stage parentStage, Long categoryId, String categoryName,
                                 ClothesSelectionModalController.ClothesSelectionCallback callback) {
        try {
            // FXML 로드
            FXMLLoader loader = new FXMLLoader(
                    ClothesSelectionModal.class.getResource("/com/samyukgu/what2wear/myCodi/clothesSelectionModal.fxml")
            );
            Parent root = loader.load();

            // 컨트롤러 가져오기
            ClothesSelectionModalController controller = loader.getController();

            // 모달 스테이지 생성
            Stage modalStage = new Stage();
            modalStage.setTitle(categoryName + " 선택");
            modalStage.initModality(Modality.WINDOW_MODAL);
            modalStage.initOwner(parentStage);
            modalStage.initStyle(StageStyle.UTILITY);
            modalStage.setResizable(true);
            modalStage.setMinWidth(600);
            modalStage.setMinHeight(500);

            // 씬 설정
            Scene scene = new Scene(root, 700, 550);
            modalStage.setScene(scene);

            // 컨트롤러 초기화
            controller.initializeModal(categoryId, categoryName, callback);

            // 모달 표시
            modalStage.showAndWait();

        } catch (Exception e) {
            System.err.println("모달창 로드 중 오류: " + e.getMessage());
            e.printStackTrace();
        }
    }
}