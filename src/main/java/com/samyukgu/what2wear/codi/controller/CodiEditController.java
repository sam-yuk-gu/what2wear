package com.samyukgu.what2wear.codi.controller;

import com.samyukgu.what2wear.codi.dto.CodiDetailDTO;
import com.samyukgu.what2wear.codi.model.Codi;
import com.samyukgu.what2wear.codi.model.CodiScope;
import com.samyukgu.what2wear.codi.service.CodiService;
import com.samyukgu.what2wear.common.controller.BasicHeaderController;
import com.samyukgu.what2wear.common.controller.CustomModalController;
import com.samyukgu.what2wear.common.controller.EditOutfitModalController;
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
import java.util.stream.Collectors;

import static com.samyukgu.what2wear.common.util.CategoryUtil.getNameById;

public class CodiEditController {

    @FXML private StackPane root;
    @FXML private VBox container;

    @FXML private TextField scheduleNameField;
    @FXML private DatePicker datePicker;
    @FXML private Pane codiDisplayPane;
    @FXML private Button editButton;
    @FXML private Button deleteButton;

    @FXML private ToggleButton btnAll;
    @FXML private ToggleButton btnFriend;
    @FXML private ToggleButton btnPrivate;

    private Long memberId;
    private MemberSession memberSession;
    private CodiService codiService;

    private Long editCodiId = null;

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
        datePicker.setValue(LocalDate.now()); // 기본 선택 날짜 = 오늘
        datePicker.getEditor().setOnMouseClicked(event -> {
            datePicker.show(); // show()를 명시적으로 호출
        });

        codiDisplayPane.setOnMouseClicked(event -> showSelectOutfitModal());
        editButton.setOnAction(event -> {
            try {
                handleEdit();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        deleteButton.setOnAction(event -> {
            try {
                handleDelete();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
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

    public void setEditMode(Long codiId) {
        this.editCodiId = codiId;
        loadCodiForEdit();
    }

    // 수정 데이터 불러오기
    private void loadCodiForEdit() {
        CodiDetailDTO codiInfo = codiService.getCodiDetail(memberId, editCodiId);

        if (codiInfo == null) return;

        // 인풋값 받아온 데이터로 수정
        if (codiInfo.getScheduleName() != null) {
            scheduleNameField.setText(codiInfo.getScheduleName());
        }
        if (codiInfo.getScheduleDate() != null) {
            datePicker.setValue(codiInfo.getScheduleDate());
        }

        switch (codiInfo.getScope()) {
            case 0 -> btnAll.setSelected(true);
            case 1 -> btnFriend.setSelected(true);
            case 2 -> btnPrivate.setSelected(true);
        }

        selectedOutfits = codiInfo.getClothes();
        renderCodiDisplay(); // 다시 그리기
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
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/samyukgu/what2wear/common/EditOutfitModal.fxml"));
            StackPane modal = loader.load();

            EditOutfitModalController controller = loader.getController();
            List<Wardrobe> wardrobeList = selectedOutfits.stream()
                    .map(item -> {
                        Wardrobe w = new Wardrobe();
                        w.setId(item.getId());
                        w.setCategoryId(item.getCategoryId());
                        w.setName(item.getName());
                        try {
                            w.setPicture(item.getPicture());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        return w;
                    })
                    .collect(Collectors.toList());

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
            List<Wardrobe> outfits = selectedOutfits;

            GridPane grid = new GridPane();
            grid.setHgap(30);
            grid.setVgap(20);
            grid.setAlignment(Pos.TOP_LEFT);

            int columnCount = 3;

            for (int i = 0; i < outfits.size(); i++) {
                Wardrobe w = outfits.get(i);

                // 바깥쪽 HBox
                HBox itemBox = new HBox();
                itemBox.getStyleClass().add("item-box");
                itemBox.setAlignment(Pos.CENTER_LEFT);
                itemBox.setSpacing(20); // 이미지와 텍스트 사이 간격 넉넉히

                // 이미지
                Image img = new Image(new ByteArrayInputStream(w.getPicture()));
                ImageView imgView = new ImageView(img);
                imgView.setFitWidth(80);
                imgView.setPreserveRatio(true);

                // 안쪽 VBox (카테고리 + 이름)
                VBox textBox = new VBox(5); // 텍스트 간 간격
                textBox.setAlignment(Pos.CENTER_LEFT);

                Label categoryLabel = new Label(getNameById(w.getCategoryId()));
                categoryLabel.setStyle("-fx-font-family: 'Pretendard SemiBold'; -fx-font-size: 16;");

                Label nameLabel = new Label(w.getName());
                nameLabel.setStyle("-fx-font-size: 13; -fx-font-family: 'Pretendard Regular'");
                nameLabel.setWrapText(true);
                nameLabel.setMaxWidth(100);

                textBox.getChildren().addAll(categoryLabel, nameLabel);
                itemBox.getChildren().addAll(imgView, textBox);

                grid.add(itemBox, i % columnCount, i / columnCount);
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

    private void handleDelete() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/samyukgu/what2wear/common/CustomModal.fxml"));
        StackPane modal = loader.load();
        CustomModalController controller = loader.getController();
        controller.configure(
                "일정을 삭제하시겠습니까?",
                "삭제된 일정 정보는 저장되지 않습니다.",
                "/assets/icons/redCheck.png",
                "#FA7B7F",
                "취소",
                "삭제",
                () -> root.getChildren().remove(modal),
                () -> {
                    codiService.deleteCodiSchedule(memberId, editCodiId);
                    root.getChildren().remove(modal);
                    MainLayoutController.loadView("/com/samyukgu/what2wear/codi/CodiMainView.fxml");
                }
        );

        root.getChildren().add(modal);
    }

    private void handleEdit() throws IOException {
        String title = scheduleNameField.getText();
        if ((title == null || title.trim().isEmpty()) && selectedOutfits.isEmpty()) {

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/samyukgu/what2wear/common/CustomModal.fxml"));
            StackPane modal = loader.load();
            CustomModalController controller = loader.getController();
            controller.configure(
                    "입력 내용 확인",
                    "일정명을 입력하거나, 코디에 포함할 옷을 선택해주세요.",
                    "/assets/icons/redCheck.png",
                    "#FA7B7F",
                    "취소",
                    "삭제",
                    () -> root.getChildren().remove(modal),
                    () -> {
                        codiService.deleteCodiSchedule(memberId, editCodiId);
                        root.getChildren().remove(modal);
                        MainLayoutController.loadView("/com/samyukgu/what2wear/codi/CodiMainView.fxml");
                    }
            );

            root.getChildren().add(modal);
            // TODO: 모달창 출력으로 변경
            System.out.println("타이틀과 선택된 옷이 모두 입력되지 않았습니다.");
        } else {
            LocalDate selectedDate = datePicker.getValue();
            CodiScope visibility = getSelectedVisibility();
            int scopeValue = getScopeValue(visibility);
            codiService.updateCodiSchedule(editCodiId, memberId, title, selectedDate, scopeValue, selectedOutfits);
            MainLayoutController.loadView("/com/samyukgu/what2wear/codi/CodiMainView.fxml");
        }
    }
}
