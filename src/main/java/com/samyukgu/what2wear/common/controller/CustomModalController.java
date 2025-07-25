package com.samyukgu.what2wear.common.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Rectangle;

import java.net.URL;
import java.util.ResourceBundle;

public class CustomModalController implements Initializable {

    @FXML private StackPane modalOverlay;
    @FXML private Rectangle dimBackground;

    @FXML private ImageView icon;
    @FXML private Label titleLabel;
    @FXML private Label descLabel;
    @FXML private Button cancelButton;
    @FXML private Button confirmButton;
    @FXML private Button closeButton;
    @FXML private HBox two_button_hbox;

    public CustomModalController() {}

    /* FXML이 로드된 후 자동 호출!  배경 바인딩 설정 */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // modalOverlay의 크기를 dimBackground가 따라가도록 설정
        if (dimBackground != null && modalOverlay != null) {
            dimBackground.widthProperty().bind(modalOverlay.widthProperty());
            dimBackground.heightProperty().bind(modalOverlay.heightProperty());
        }
    }

    public void configure(String title, String desc, String iconPath, String themeColor,
                          String actionText, Runnable onAction) {
        configure(title, desc, iconPath, themeColor, null, actionText, null, onAction);
    }

    /* 모달 초기 설정 메서드 */
    public void configure(String title, String desc, String iconPath, String themeColor,
                          String cancelText, String actionText,
                          Runnable onCancel, Runnable onAction) {

        if (title != null) titleLabel.setText(title);
        if (desc != null) descLabel.setText(desc);

        if (iconPath != null && !iconPath.isEmpty()) {
            try {
                icon.setImage(new Image(getClass().getResourceAsStream(iconPath)));
            } catch (Exception e) {
                System.out.println("아이콘 로딩 실패: " + iconPath);
            }
        }

        // 취소 버튼 처리 - null이면 숨김
        if (cancelText != null) {
            cancelButton.setText(cancelText);
            cancelButton.setVisible(true);
            cancelButton.setManaged(true);  // 레이아웃에서 공간 차지
            cancelButton.setOnAction(e -> {
                if (onCancel != null) onCancel.run();
                modalOverlay.setVisible(false);
            });
        } else {
            cancelButton.setVisible(false);
            cancelButton.setManaged(false);  // 레이아웃에서 공간 차지하지 않음
        }

        // 확인 버튼은 항상 표시
        if (actionText != null) confirmButton.setText(actionText);
        confirmButton.setOnAction(e -> {
            if (onAction != null) onAction.run();
            modalOverlay.setVisible(false);
        });

        // 닫기 버튼 처리
        closeButton.setOnAction(e -> {
            if (onCancel != null) onCancel.run();
            modalOverlay.setVisible(false);
        });

        // 테마 색상 적용
        if (themeColor != null && !themeColor.isEmpty()) {
            confirmButton.setStyle("-fx-background-color: " + themeColor + "; -fx-text-fill: white;");
        }

        // 버튼이 하나만 있을 때 HBox의 정렬을 CENTER로 변경
        if (cancelText == null) {
            two_button_hbox.setAlignment(javafx.geometry.Pos.CENTER);
        } else {
            two_button_hbox.setAlignment(javafx.geometry.Pos.CENTER);  // 또는 기본 정렬 유지
        }
    }

    /* 닫기 버튼 핸들러 */
    @FXML
    private void handleClose(ActionEvent event) {
        modalOverlay.setVisible(false);
    }

    /* 취소 버튼 핸들러 */
    @FXML
    private void handleCancel(ActionEvent event) {
        modalOverlay.setVisible(false);
    }

    /* 확인 버튼 핸들러 */
    @FXML
    private void handleConfirm(ActionEvent event) {
        modalOverlay.setVisible(false);
    }

    /* 오버레이 */
    public StackPane getModalOverlay() {
        return modalOverlay;
    }
}