package com.samyukgu.what2wear.codi.controller;

import com.samyukgu.what2wear.codi.model.Clothing;
import com.samyukgu.what2wear.codi.model.Codi;
import com.samyukgu.what2wear.codi.model.CodiScope;
import com.samyukgu.what2wear.common.controller.BasicHeaderController;
import com.samyukgu.what2wear.common.controller.SelectOutfitModalController;
import com.samyukgu.what2wear.common.util.DateUtils;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.util.StringConverter;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
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

    private ToggleGroup scopeGroup;

    private List<Clothing> selectedOutfits;
    private Codi selectedCodi;

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
        setupScopeToggleGroup();

        // 3. 기타 초기화
        datePicker.setConverter(new StringConverter<>() {
            @Override
            public String toString(LocalDate date) {
                return DateUtils.formatWithKoreanDay(date);
            }

            @Override
            public LocalDate fromString(String string) {
                return DateUtils.parseKoreanFormattedDate(string);
            }
        });

        datePicker.getEditor().setCursor(Cursor.HAND);
        datePicker.setEditable(false); // 직접 입력 막기
        datePicker.setValue(LocalDate.now()); // 기본 선택 날짜 = 오늘
        datePicker.getEditor().setOnMouseClicked(event -> {
            datePicker.show(); // show()를 명시적으로 호출
        });

        codiDisplayPane.setOnMouseClicked(event -> showSelectOutfitModal());
        submitButton.setOnAction(event -> handleSubmit());
    }

    private void setupScopeToggleGroup() {
        scopeGroup = new ToggleGroup();

        btnAll.setToggleGroup(scopeGroup);
        btnFriend.setToggleGroup(scopeGroup);
        btnPrivate.setToggleGroup(scopeGroup);

        // 디폴트 선택
        btnAll.setSelected(true);

        // 재선택 시 선택 해제 방지
        preventDeselect(btnAll);
        preventDeselect(btnFriend);
        preventDeselect(btnPrivate);
    }

    private void preventDeselect(ToggleButton toggleButton) {
        toggleButton.setOnAction(event -> {
            if (!toggleButton.isSelected()) {
                toggleButton.setSelected(true);
            }
        });
    }

    private void showSelectOutfitModal() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/samyukgu/what2wear/common/SelectOutfitModal.fxml"));
            StackPane modal = loader.load();

            SelectOutfitModalController controller = loader.getController();
            controller.configure(
                    selectedOutfits,
                    selectedCodi,
                    scheduleNameField.getText(),
                    datePicker.getValue(),
                    getSelectedVisibility(),
                    (result) -> {
                        root.getChildren().remove(modal);
                        this.selectedOutfits = result.getOutfits();
                        this.selectedCodi = result.getCodi();
                        renderCodiDisplay();
                    },
                    () -> root.getChildren().remove(modal)
            );

            root.getChildren().add(modal);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void renderCodiDisplay() {
        codiDisplayPane.getChildren().clear();

        if ((selectedOutfits == null || selectedOutfits.isEmpty()) && selectedCodi == null) {
            // Empty state 표시
            VBox emptyBox = new VBox();
            emptyBox.setSpacing(10);
            emptyBox.setAlignment(Pos.CENTER);

            ImageView emptyImage = new ImageView(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/assets/images/dummy-1.png"))));
            emptyImage.setFitHeight(60);
            emptyImage.setPreserveRatio(true);

            Label prompt = new Label("코디를 등록해보세요");
            prompt.setStyle("-fx-text-fill: #888; -fx-font-size: 14;");

            emptyBox.getChildren().addAll(emptyImage, prompt);
            emptyBox.setPrefSize(400, 120);
            codiDisplayPane.getChildren().add(emptyBox);
        } else {
            GridPane grid = new GridPane();
            grid.setHgap(15);
            grid.setVgap(15);
            int column = 3;

            List<ImageView> items = new ArrayList<>();

            if (selectedOutfits != null) {
                for (Clothing c : selectedOutfits) {
                    ImageView img = new ImageView(new Image(getClass().getResourceAsStream(c.getImagePath())));
                    img.setFitWidth(60);
                    img.setPreserveRatio(true);
                    items.add(img);
                }
            } else if (selectedCodi != null) {
                ImageView img = new ImageView(new Image(getClass().getResourceAsStream(selectedCodi.getImagePath())));
                img.setFitWidth(100);
                img.setPreserveRatio(true);
                items.add(img);
            }

            for (int i = 0; i < items.size(); i++) {
                grid.add(items.get(i), i % column, i / column);
            }

            codiDisplayPane.getChildren().add(grid);
        }
    }


    private CodiScope getSelectedVisibility() {
        if (btnAll.isSelected()) return CodiScope.PUBLIC;
        if (btnFriend.isSelected()) return CodiScope.FRIENDS;
        return CodiScope.PRIVATE;
    }

    private void handleSubmit() {
        String title = scheduleNameField.getText();
        String date = (datePicker.getValue() != null) ? datePicker.getValue().toString() : null;
        Toggle selected = scopeGroup.getSelectedToggle();

        // 실제 저장 로직 TODO
        System.out.println("일정명: " + title);
        System.out.println("날짜: " + date);
        System.out.println("공개범위: " + ((ToggleButton) selected).getText());
    }
}
