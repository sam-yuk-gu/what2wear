package com.samyukgu.what2wear.common.controller;

import com.samyukgu.what2wear.codi.model.Codi;
import com.samyukgu.what2wear.codi.model.CodiItem;
import com.samyukgu.what2wear.codi.model.CodiScope;
import com.samyukgu.what2wear.common.model.SelectionResult;
import com.samyukgu.what2wear.di.DIContainer;
import com.samyukgu.what2wear.wardrobe.model.Wardrobe;
import com.samyukgu.what2wear.wardrobe.service.WardrobeService;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.shape.Rectangle;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

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

    private final WardrobeService wardrobeService = DIContainer.getInstance().resolve(WardrobeService.class);

    private Long memberId;

    public void setMemberId(Long memberId) {
        this.memberId = memberId;
        loadWardrobeForMember(); // 멤버 ID가 세팅되면 즉시 옷 불러오기
    }

    private void loadWardrobeForMember() {
        if (wardrobeService != null && memberId != null) {
            List<Wardrobe> wardrobeList = wardrobeService.getAllWardrobe(memberId);
            allWardrobeItems = wardrobeList.stream()
                    .map(wardrobe -> {
                        CodiItem item = new CodiItem();
                        item.setId(wardrobe.getId());
                        item.setName(wardrobe.getName());
                        item.setCategory(getCategoryName(wardrobe.getCategoryId()));
                        item.setImagePath(convertImageBytesToPath(wardrobe.getPicture()));
                        return item;
                    })
                    .collect(Collectors.toList());
            renderWardrobeItems();
        } else {
            System.err.println("wardrobeService 또는 memberId가 null입니다.");
        }
    }

    private boolean isWardrobeSelected = true;
    private final Map<String, Wardrobe> selectedClothes = new HashMap<>();
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

        codiView.setManaged(false);

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
                codiSearchField.getParent().setStyle("-fx-border-color: #454545; -fx-border-width: 1.5; -fx-background-radius: 10;");
            } else {
                codiSearchField.getParent().setStyle("-fx-border-color: #CCCCCC; -fx-border-width: 1; -fx-background-radius: 10;");
            }
        });
    }

    public void configure(List<Wardrobe> outfits, Codi codi, String scheduleName,
                          LocalDate date, CodiScope visibility,
                          Consumer<SelectionResult> onConfirm, Runnable onCancel,
                          Long memberId) {
        this.onConfirm = onConfirm;
        this.onCancel = onCancel;
        if (outfits != null) {
            for (Wardrobe c : outfits) {
                selectedClothes.put(getCategoryName(c.getCategoryId()), c);
            }
        }
        setMemberId(memberId);
        this.selectedCodi = codi;

        switchToWardrobe();
        codiItemPane.getChildren().clear();
        loadWardrobeItemsFromService(memberId);
        renderCodiItems(); // 코디 아이템 렌더링
    }

    private void loadWardrobeItemsFromService(Long memberId) {
//        try {
//            List<Wardrobe> wardrobeList = wardrobeService.getAllWardrobe(memberId);
//            allWardrobeItems = wardrobeList.stream()
//                    .map(w -> new Clothing(
//                            String.valueOf(w.getId()),
//                            w.getName(),
//                            getCategoryName(w.getCategoryId()),
//                            convertImageBytesToPath(w.getPicture())  // 또는 이미지 처리 방식에 맞게 수정
//                    ))
//                    .collect(Collectors.toList());
//            renderWardrobeItems();
//        } catch (Exception e) {
//            System.out.println("옷장 불러오기 실패: " + e.getMessage());
//        }
    }

    private String getCategoryName(Long categoryId) {
        if (categoryId == null) return "기타";
        return switch (categoryId.intValue()) {
            case 1 -> "상의";
            case 2 -> "바지";
            case 3 -> "신발";
            case 4 -> "가방";
            case 5 -> "원피스/스커트";
            case 6 -> "아우터";
            case 7 -> "악세사리";
            case 8 -> "기타";
            default -> "기타";
        };
    }

    private String convertImageBytesToPath(byte[] pictureBytes) {
        if (pictureBytes == null) return "/assets/default-clothes.png"; // 기본 이미지

        try {
            File tempFile = File.createTempFile("clothes_", ".png");
            try (FileOutputStream fos = new FileOutputStream(tempFile)) {
                fos.write(pictureBytes);
            }
            return tempFile.toURI().toString(); // JavaFX ImageView에서 사용 가능
        } catch (IOException e) {
            e.printStackTrace();
            return "/assets/default-clothes.png";
        }
    }

    private void switchToWardrobe() {
        isWardrobeSelected = true;
        wardrobeView.setVisible(true);
        codiView.setVisible(false);
        wardrobeTab.setSelected(true);
        codiTab.setSelected(false);
        wardrobeView.setManaged(true);
        codiView.setManaged(false);
    }

    private void switchToCodi() {
        isWardrobeSelected = false;
        wardrobeView.setVisible(false);
        codiView.setVisible(true);
        wardrobeTab.setSelected(false);
        codiTab.setSelected(true);
        wardrobeView.setManaged(false);
        codiView.setManaged(true);
    }

    private void loadWardrobeItems() {
        renderWardrobeItems();
    }

    private List<Codi> allCodiItems = new ArrayList<>();

    private VBox createClothingBox(CodiItem item) {
        VBox box = new VBox();
        box.getStyleClass().add("clothing-box");

        ImageView itemImage;
        try {
            itemImage = new ImageView(item.getImagePath());
        } catch (Exception e) {
            itemImage = new ImageView();
            System.out.println("이미지 로딩 실패: " + item.getImagePath());
        }

        itemImage.setFitWidth(95);
        itemImage.setFitHeight(95);
        itemImage.setPreserveRatio(true);

        // 오버레이 및 체크 아이콘 (기본은 숨김)
        Rectangle overlay = new Rectangle(107, 107, javafx.scene.paint.Color.rgb(0, 0, 0, 0.4));
        overlay.setArcWidth(12);
        overlay.setArcHeight(12);
        overlay.setVisible(false);

        ImageView checkIcon = new ImageView(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/assets/icons/check-white.png"))));
        checkIcon.setFitWidth(40);
        checkIcon.setPreserveRatio(true);
        checkIcon.setVisible(false);

        StackPane imageContainer = new StackPane(itemImage, overlay, checkIcon);
        imageContainer.setPrefSize(107, 107);

        box.getChildren().add(imageContainer);
        box.setUserData(item);

        // 선택 로직
        box.setOnMouseClicked(e -> {
            String category = item.getCategory();
            boolean alreadySelected = selectedClothes.containsKey(category);

            Wardrobe matched = wardrobeService.getAllWardrobe(memberId).stream()
                    .filter(w -> Objects.equals(w.getId(), item.getId()))
                    .findFirst()
                    .orElse(null);

            if (!alreadySelected || !Objects.equals(selectedClothes.get(category).getId(), item.getId())) {
                if (matched != null) selectedClothes.put(category, matched);
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
            CodiItem clothing = (CodiItem) box.getUserData();
//            Clothing clothing = (Clothing) box.getUserData();
            boolean selected = selectedClothes.containsKey(clothing.getCategory())
                    && selectedClothes.get(clothing.getCategory()).getId().equals(clothing.getId());

            StackPane imageContainer = (StackPane) box.getChildren().getFirst(); // 이미지+오버레이+체크
            Node[] overlays = (Node[]) imageContainer.getUserData();
            Rectangle overlay = (Rectangle) overlays[0];
            ImageView checkIcon = (ImageView) overlays[1];

            overlay.setVisible(selected);
            checkIcon.setVisible(selected);
        }
    }

    // TODO: 코디 연결 필요
    private VBox createCodiBox(Codi codi) {
        VBox box = new VBox();
        box.getStyleClass().add("codi-box");
        box.setSpacing(5);
        box.setAlignment(Pos.CENTER);
        box.setPrefWidth(100);

        // 이미지 로딩
        ImageView codiImage;
//        try {
//            Image image = new Image(Objects.requireNonNull(getClass().getResourceAsStream(codi.getImagePath())));
//            codiImage = new ImageView(image);
//        } catch (Exception e) {
//            codiImage = new ImageView();
////            System.out.println("코디 이미지 로딩 실패: " + codi.getImagePath());
//        }
//        codiImage.getStyleClass().add("codi-image");
//        codiImage.setPreserveRatio(false);

        Rectangle clip = new Rectangle(130, 180);
        clip.setArcWidth(20);
        clip.setArcHeight(20);
//        codiImage.setClip(clip);

        // 오버레이 + 체크
        Rectangle overlay = new Rectangle(130, 180, javafx.scene.paint.Color.rgb(0, 0, 0, 0.4));
        overlay.setArcWidth(20);
        overlay.setArcHeight(20);
        overlay.setVisible(false);

        ImageView checkIcon = new ImageView(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/assets/icons/check-white.png"))));
        checkIcon.setFitWidth(45);
        checkIcon.setPreserveRatio(true);
        checkIcon.setVisible(false);

//        StackPane imageContainer = new StackPane(codiImage, overlay, checkIcon);
//        imageContainer.setPrefSize(130, 180);
//
//        box.getChildren().add(imageContainer);
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

//        imageContainer.setUserData(new Node[] { overlay, checkIcon });
        return box;
    }

    private void refreshCodiSelection() {
        for (Node node : codiItemPane.getChildren()) {
            VBox box = (VBox) node;
            Codi c = (Codi) box.getUserData();
            StackPane imageContainer = (StackPane) box.getChildren().getFirst();
            Node[] overlays = (Node[]) imageContainer.getUserData();
            Rectangle overlay = (Rectangle) overlays[0];
            ImageView checkIcon = (ImageView) overlays[1];

            boolean selected = selectedCodi != null && selectedCodi.getId().equals(c.getId());
            overlay.setVisible(selected);
            checkIcon.setVisible(selected);
        }
    }

    private List<CodiItem> allWardrobeItems = new ArrayList<>();


    private void setCategoryFilter(String category) {
        this.currentCategoryFilter = category;
        renderWardrobeItems(); // 새로 그림
    }

    private void renderWardrobeItems() {
        wardrobeItemPane.getChildren().clear();

        for (CodiItem item : allWardrobeItems) {
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

    private Button selectedCategoryButton = null;

    private void setupCategoryButtons() {
        for (String category : categories) {
            Button btn = new Button(category);
            btn.getStyleClass().add("category-button");

            // 기본 선택: "전체"
            if (category.equals("전체")) {
                btn.getStyleClass().add("category-button-selected");
                selectedCategoryButton = btn;
            }

            btn.setOnAction(e -> {
                if (selectedCategoryButton != null) {
                    selectedCategoryButton.getStyleClass().remove("category-button-selected");
                }

                btn.getStyleClass().add("category-button-selected");
                selectedCategoryButton = btn;

                setCategoryFilter(category);
            });

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
            List<Wardrobe> outfits = new ArrayList<>(selectedClothes.values());
            onConfirm.accept(new SelectionResult(outfits, selectedCodi));
        }
        modalOverlay.setVisible(false);
    }
}
