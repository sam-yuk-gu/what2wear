package com.samyukgu.what2wear.codi.controller;

import com.samyukgu.what2wear.codi.model.Codi;
import com.samyukgu.what2wear.codi.model.CodiScope;
import com.samyukgu.what2wear.codi.service.CodiService;
import com.samyukgu.what2wear.common.controller.BasicHeaderController;
import com.samyukgu.what2wear.common.controller.SelectOutfitModalController;
import com.samyukgu.what2wear.common.util.DateUtils;
import com.samyukgu.what2wear.di.DIContainer;
import com.samyukgu.what2wear.layout.controller.MainLayoutController;
import com.samyukgu.what2wear.member.Session.MemberSession;
import com.samyukgu.what2wear.wardrobe.model.Wardrobe;
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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class CodiAddController {

    @FXML private StackPane root;
    @FXML private VBox container;

    @FXML private TextField scheduleNameField;
    @FXML private DatePicker datePicker;
    @FXML private Pane codiDisplayPane;
    @FXML private Button submitButton;

    @FXML private ToggleButton btnAll;
    @FXML private ToggleButton btnFriend;
    @FXML private ToggleButton btnPrivate;

    private Long memberId;
    private MemberSession memberSession;
    private CodiService codiService;

    private ToggleGroup scopeGroup;
    private List<Wardrobe> selectedOutfits;
    private Codi selectedCodi;

    @FXML
    public void initialize() {
        setupDI();
        setupUser();

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

    private void setupDI() {
        DIContainer diContainer = DIContainer.getInstance();
        memberSession = diContainer.resolve(MemberSession.class);
        codiService = diContainer.resolve(CodiService.class);
    }

    private void setupUser() {
        if (memberSession == null || memberSession.getMember() == null) {
            System.err.println("로그인 정보가 없습니다.");
            return;
        }

        memberId = memberSession.getMember().getId();
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
                result -> {
                    root.getChildren().remove(modal);
                    this.selectedOutfits = result.getOutfits();
                    this.selectedCodi = result.getCodi();
                    renderCodiDisplay();
                },
                () -> root.getChildren().remove(modal),
                memberId
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

            ImageView emptyImage = new ImageView(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/assets/images/dummy_1.png"))));
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
                for (Wardrobe w : selectedOutfits) {
                    Image img = new Image(new ByteArrayInputStream(w.getPicture()));
                    ImageView imgView = new ImageView(img);
                    imgView.setFitWidth(60);
                    imgView.setPreserveRatio(true);
                    items.add(imgView);
                }
            } else if (selectedCodi != null) {
//                ImageView img = new ImageView(new Image(getClass().getResourceAsStream(selectedCodi.getImagePath())));
//                img.setFitWidth(100);
//                img.setPreserveRatio(true);
//                items.add(img);
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

    private int getScopeValue(CodiScope scope) {
        return switch (scope) {
            case PUBLIC -> 0;
            case FRIENDS -> 1;
            case PRIVATE -> 2;
        };
    }

    private void handleSubmit() {

        String title = scheduleNameField.getText();
        String date = (datePicker.getValue() != null) ? datePicker.getValue().toString() : null;
        Toggle selected = scopeGroup.getSelectedToggle();

        if ((title == null || title.trim().isEmpty()) && selectedOutfits.isEmpty()) {
            // TODO: 모달창 출력으로 변경
            System.out.println("타이틀과 선택된 옷이 모두 입력되지 않았습니다.");
        } else {
            // TODO: 저장 확인 모달 넣을지말지?
            LocalDate selectedDate = datePicker.getValue();
            CodiScope visibility = getSelectedVisibility();
            int scopeValue = getScopeValue(visibility);
            codiService.createCodiSchedule(memberId, title, selectedDate, scopeValue, selectedOutfits);
            MainLayoutController.loadView("/com/samyukgu/what2wear/codi/CodiMainView.fxml");
        }
    }
}
