package com.samyukgu.what2wear.codi.controller;

import com.samyukgu.what2wear.common.controller.BasicHeaderController;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import java.io.IOException;
import java.util.Objects;

public class AddCodiController {

    @FXML private StackPane root;
    @FXML private VBox container;

    @FXML private TextField scheduleNameField;
    @FXML private DatePicker datePicker;
    @FXML private Pane codiDisplayPane;
    @FXML private Button submitButton;

    @FXML private ToggleButton btnAll;
    @FXML private ToggleButton btnFriend;
    @FXML private ToggleButton btnPrivate;

    private ToggleGroup visibilityGroup;

    @FXML
    public void initialize() {
        // 1. 헤더 동적 삽입
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/samyukgu/what2wear/common/BasicHeader.fxml"));
            HBox header = loader.load();

            BasicHeaderController controller = loader.getController();
            controller.setTitle("일정 추가");
            controller.setOnBackAction(() -> {
                try {
                    Parent view = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/com/samyukgu/what2wear/codi/CodiMainView.fxml")));
                    root.getChildren().setAll(view);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });

            container.getChildren().add(0, header); // StackPane 맨 위에 삽입
        } catch (IOException e) {
            e.printStackTrace();
        }

        // 2. ToggleGroup 설정
        visibilityGroup = new ToggleGroup();
        btnAll.setToggleGroup(visibilityGroup);
        btnFriend.setToggleGroup(visibilityGroup);
        btnPrivate.setToggleGroup(visibilityGroup);
        visibilityGroup.selectToggle(btnAll); // 기본값

        // 3. 기타 초기화
        submitButton.setOnAction(event -> handleSubmit());
    }

    private void handleSubmit() {
        String title = scheduleNameField.getText();
        String date = (datePicker.getValue() != null) ? datePicker.getValue().toString() : null;
        Toggle selected = visibilityGroup.getSelectedToggle();

        // 실제 저장 로직 TODO
        System.out.println("일정명: " + title);
        System.out.println("날짜: " + date);
        System.out.println("공개범위: " + ((ToggleButton) selected).getText());
    }
}
