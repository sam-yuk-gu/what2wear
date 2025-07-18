package com.samyukgu.what2wear.common.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.StackPane;
import javafx.scene.Parent;
import java.io.IOException;

public class MainLayoutController {
    @FXML private StackPane contentArea;

    private void setContent(String fxmlPath) {
        try {
            Parent view = FXMLLoader.load(getClass().getResource(fxmlPath));
            contentArea.getChildren().setAll(view);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleCloset() {
        setContent("/com/samyukgu/what2wear/closet/ClosetView.fxml");
    }

    @FXML
    private void handleFriend() {
        setContent("/com/samyukgu/what2wear/friend/FriendView.fxml");
    }

    @FXML
    private void handleBoard() {
        setContent("/com/samyukgu/what2wear/board/BoardView.fxml");
    }

    @FXML
    private void handleMyPage() {
        setContent("/com/samyukgu/what2wear/mypage/MyPageView.fxml");
    }
}
