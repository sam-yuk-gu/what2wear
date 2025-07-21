package com.samyukgu.what2wear.common.controller;

import com.samyukgu.what2wear.codi.model.Clothing;
import com.samyukgu.what2wear.codi.model.Codi;
import com.samyukgu.what2wear.codi.model.ScheduleVisibility;
import com.samyukgu.what2wear.common.model.SelectionResult;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.shape.Rectangle;

import java.net.URL;
import java.time.LocalDate;
import java.util.*;
import java.util.function.Consumer;

public class SelectOutfitModalController implements Initializable {

    @FXML private StackPane modalOverlay;
    @FXML private Rectangle dimBackground;

    @FXML private ToggleButton wardrobeTab;
    @FXML private ToggleButton codiTab;

    @FXML private VBox wardrobeView;
    @FXML private VBox codiView;

    @FXML private TextField wardrobeSearchField;
    @FXML private TextField codiSearchField;

    @FXML private FlowPane wardrobeItemPane;
    @FXML private FlowPane codiItemPane;
    @FXML private HBox categoryButtonBox;

    @FXML private Button cancelButton;
    @FXML private Button confirmButton;

    private boolean isWardrobeSelected = true;
    private Map<String, Clothing> selectedClothes = new HashMap<>();
    private Codi selectedCodi = null;

    private String wardrobeSearchText = "";
    private String codiSearchText = "";

    private Consumer<SelectionResult> onConfirm;
    private Runnable onCancel;

    private final List<String> categories = Arrays.asList("전체", "상의", "바지", "신발", "가방", "원피스/스커트", "아우터", "악세사리", "기타");
    private String currentCategoryFilter = "전체";

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        dimBackground.widthProperty().bind(modalOverlay.widthProperty());
        dimBackground.heightProperty().bind(modalOverlay.heightProperty());

        wardrobeTab.setOnAction(e -> switchToWardrobe());
        codiTab.setOnAction(e -> switchToCodi());
        setupCategoryButtons();

        wardrobeSearchField.setOnAction(e -> {
            wardrobeSearchText = wardrobeSearchField.getText().trim();
            renderWardrobeItems();
        });

        codiSearchField.setOnAction(e -> {
            codiSearchText = codiSearchField.getText().trim();
            renderCodiItems();
        });

        wardrobeSearchField.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal) {
                wardrobeSearchField.getParent().setStyle("-fx-border-color: #454545; -fx-border-width: 1.5; -fx-background-radius: 10;");
            } else {
                wardrobeSearchField.getParent().setStyle("-fx-border-color: #CCCCCC; -fx-border-width: 1; -fx-background-radius: 10;");
            }
        });
        codiSearchField.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal) {
                codiSearchField.getParent().setStyle("-fx-border-color: #454545; -fx-border-width: 1.5; -fx-background-radius: 10; -fx-padding: 6;");
            } else {
                codiSearchField.getParent().setStyle("-fx-border-color: #CCCCCC; -fx-border-width: 1; -fx-background-radius: 10; -fx-padding: 6;");
            }
        });
    }

    public void configure(List<Clothing> outfits, Codi codi, String scheduleName,
                          LocalDate date, ScheduleVisibility visibility,
                          Consumer<SelectionResult> onConfirm, Runnable onCancel) {
        this.onConfirm = onConfirm;
        this.onCancel = onCancel;
        if (outfits != null) {
            for (Clothing c : outfits) {
                selectedClothes.put(c.getCategory(), c);
            }
        }
        this.selectedCodi = codi;

        // 초기 탭 설정 및 더미 데이터 로드
        switchToWardrobe();
        loadDummyWardrobeItems();
        loadDummyCodiItems();
    }

    private void switchToWardrobe() {
        isWardrobeSelected = true;
        wardrobeView.setVisible(true);
        codiView.setVisible(false);
        wardrobeTab.setSelected(true);
        codiTab.setSelected(false);
    }

    private void switchToCodi() {
        isWardrobeSelected = false;
        wardrobeView.setVisible(false);
        codiView.setVisible(true);
        wardrobeTab.setSelected(false);
        codiTab.setSelected(true);
    }

    private void loadDummyWardrobeItems() {
        allWardrobeItems = Arrays.asList(
                new Clothing("1", "흰 셔츠", "상의", "/assets/images/dummy-3.png"),
                new Clothing("2", "청바지", "상의", "/assets/images/dummy-2.png"),
                new Clothing("3", "운동화", "신발", "/assets/images/dummy-4.png"),
                new Clothing("4", "운동화", "신발", "/assets/images/dummy-4.png"),
                new Clothing("5", "운동화", "신발", "/assets/images/dummy-4.png"),
                new Clothing("6", "운동화", "신발", "/assets/images/dummy-4.png"),
                new Clothing("7", "운동화", "신발", "/assets/images/dummy-4.png"),
                new Clothing("8", "운동화", "신발", "/assets/images/dummy-4.png"),
                new Clothing("9", "운동화", "신발", "/assets/images/dummy-4.png"),
                new Clothing("10", "운동화", "신발", "/assets/images/dummy-4.png"),
                new Clothing("11", "운동화", "신발", "/assets/images/dummy-4.png")
        );

        renderWardrobeItems();
    }

    private List<Codi> allCodiItems = new ArrayList<>();

    private void loadDummyCodiItems() {
        codiItemPane.getChildren().clear();

        List<Codi> dummy = Arrays.asList(
                new Codi("C1", "캐주얼룩", "/assets/images/dummy-5.png"),
                new Codi("C2", "데이트룩", "/assets/images/dummy-6.png")
        );

        for (Codi c : dummy) {
            VBox box = createCodiBox(c);
            codiItemPane.getChildren().add(box);
        }

        allCodiItems = Arrays.asList(
                new Codi("C1", "캐주얼룩", "/assets/images/dummy-5.png"),
                new Codi("C2", "데이트룩", "/assets/images/dummy-6.png")
        );
        renderCodiItems(); // 별도 렌더 함수 호출
    }

    private VBox createClothingBox(Clothing item) {
        VBox box = new VBox();
        box.getStyleClass().add("clothing-box");

        ImageView itemImage;
        try {
            Image image = new Image(Objects.requireNonNull(getClass().getResourceAsStream(item.getImagePath())));
            itemImage = new ImageView(image);
        } catch (Exception e) {
            itemImage = new ImageView();
            System.out.println("이미지 로딩 실패: " + item.getImagePath());
        }

        itemImage.setFitWidth(95);
        itemImage.setFitHeight(95);
        itemImage.setPreserveRatio(true);

        // 오버레이 및 체크 아이콘 (기본은 숨김)
        Rectangle overlay = new Rectangle(95, 95, javafx.scene.paint.Color.rgb(0, 0, 0, 0.4));
        overlay.setArcWidth(12);
        overlay.setArcHeight(12);
        overlay.setVisible(false);

        ImageView checkIcon = new ImageView(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/assets/icons/check.png"))));
        checkIcon.setFitWidth(40);
        checkIcon.setPreserveRatio(true);
        checkIcon.setVisible(false);

        StackPane imageContainer = new StackPane(itemImage, overlay, checkIcon);
        imageContainer.setPrefSize(80, 80);

        box.getChildren().add(imageContainer);
        box.setUserData(item);

        // 선택 로직
        box.setOnMouseClicked(e -> {
            String category = item.getCategory();
            boolean alreadySelected = selectedClothes.containsKey(category);

            if (!alreadySelected || !selectedClothes.get(category).getId().equals(item.getId())) {
                selectedClothes.put(category, item);
            } else {
                selectedClothes.remove(category);
            }
            refreshWardrobeSelection();
        });

        // 선택 시 오버레이/체크 아이콘 표시
        box.setId("clothingBox-" + item.getId()); // 식별자 부여
        imageContainer.setUserData(new Node[] { overlay, checkIcon }); // 토글용 저장

        return box;
    }

    private void refreshWardrobeSelection() {
        for (Node node : wardrobeItemPane.getChildren()) {
            VBox box = (VBox) node;
            Clothing clothing = (Clothing) box.getUserData();
            boolean selected = selectedClothes.containsKey(clothing.getCategory())
                    && selectedClothes.get(clothing.getCategory()).getId().equals(clothing.getId());

            StackPane imageContainer = (StackPane) box.getChildren().get(0); // 이미지+오버레이+체크
            Node[] overlays = (Node[]) imageContainer.getUserData();
            Rectangle overlay = (Rectangle) overlays[0];
            ImageView checkIcon = (ImageView) overlays[1];

            overlay.setVisible(selected);
            checkIcon.setVisible(selected);
        }
    }

    private VBox createCodiBox(Codi codi) {
        VBox box = new VBox();
        box.setSpacing(5);
        box.setAlignment(Pos.CENTER);
        box.setPrefWidth(100);

        // 이미지 로딩
        ImageView codiImage;
        try {
            Image image = new Image(Objects.requireNonNull(getClass().getResourceAsStream(codi.getImagePath())));
            codiImage = new ImageView(image);
        } catch (Exception e) {
            codiImage = new ImageView();
            System.out.println("코디 이미지 로딩 실패: " + codi.getImagePath());
        }
        codiImage.setFitWidth(100);
        codiImage.setPreserveRatio(true);

        // 오버레이 + 체크
        Rectangle overlay = new Rectangle(100, 100, javafx.scene.paint.Color.rgb(0, 0, 0, 0.4));
        overlay.setArcWidth(12);
        overlay.setArcHeight(12);
        overlay.setVisible(false);

        ImageView checkIcon = new ImageView(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/assets/icons/check.png"))));
        checkIcon.setFitWidth(40);
        checkIcon.setPreserveRatio(true);
        checkIcon.setVisible(false);

        StackPane imageContainer = new StackPane(codiImage, overlay, checkIcon);
        imageContainer.setPrefSize(100, 100);

        box.getChildren().add(imageContainer);
        box.setUserData(codi);

        // 클릭 시 선택 로직
        box.setOnMouseClicked(e -> {
            if (selectedCodi != null && selectedCodi.getId().equals(codi.getId())) {
                selectedCodi = null;
            } else {
                selectedCodi = codi;
            }
            refreshCodiSelection();
        });

        imageContainer.setUserData(new Node[] { overlay, checkIcon });
        return box;
    }

    // refreshCodiSelection 수정 (오버레이 반영)
    private void refreshCodiSelection() {
        for (Node node : codiItemPane.getChildren()) {
            VBox box = (VBox) node;
            Codi c = (Codi) box.getUserData();
            StackPane imageContainer = (StackPane) box.getChildren().get(0);
            Node[] overlays = (Node[]) imageContainer.getUserData();
            Rectangle overlay = (Rectangle) overlays[0];
            ImageView checkIcon = (ImageView) overlays[1];

            boolean selected = selectedCodi != null && selectedCodi.getId().equals(c.getId());
            overlay.setVisible(selected);
            checkIcon.setVisible(selected);
        }
    }

    private List<Clothing> allWardrobeItems = new ArrayList<>();


    private void setCategoryFilter(String category) {
        this.currentCategoryFilter = category;
        renderWardrobeItems(); // 새로 그림
    }

    private void renderWardrobeItems() {
        wardrobeItemPane.getChildren().clear();

        for (Clothing item : allWardrobeItems) {
            boolean categoryMatch = currentCategoryFilter.equals("전체") || item.getCategory().equals(currentCategoryFilter);
            boolean searchMatch = wardrobeSearchText.isEmpty() || item.getName().contains(wardrobeSearchText);

            if (categoryMatch && searchMatch) {
                VBox box = createClothingBox(item);
                wardrobeItemPane.getChildren().add(box);
            }
        }

        refreshWardrobeSelection();
    }

    private void renderCodiItems() {
        codiItemPane.getChildren().clear();

        for (Codi c : allCodiItems) {
            if (codiSearchText.isEmpty() || c.getName().contains(codiSearchText)) {
                VBox box = createCodiBox(c);
                codiItemPane.getChildren().add(box);
            }
        }

        refreshCodiSelection();
    }

    private void setupCategoryButtons() {
        for (String category : categories) {
            Button btn = new Button(category);
            btn.setOnAction(e -> setCategoryFilter(category));
            btn.getStyleClass().add("category-button");
            categoryButtonBox.getChildren().add(btn);
        }
    }

    @FXML
    private void handleCancel() {
        if (onCancel != null) onCancel.run();
        modalOverlay.setVisible(false);
    }

    @FXML
    private void handleConfirm() {
        if (onConfirm != null) {
            List<Clothing> outfits = new ArrayList<>(selectedClothes.values());
            onConfirm.accept(new SelectionResult(outfits, selectedCodi));
        }
        modalOverlay.setVisible(false);
    }
}
