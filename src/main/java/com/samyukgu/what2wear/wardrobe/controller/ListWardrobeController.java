package com.samyukgu.what2wear.wardrobe.controller;

import com.samyukgu.what2wear.common.controller.MainLayoutController;
import com.samyukgu.what2wear.wardrobe.dao.WardrobeOracleDAO;
import com.samyukgu.what2wear.wardrobe.model.Wardrobe;
import com.samyukgu.what2wear.wardrobe.service.WardrobeService;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import java.io.ByteArrayInputStream;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ListWardrobeController {
    private final WardrobeService wardrobeService = new WardrobeService(new WardrobeOracleDAO());

    // 데이터 저장
    private List<Wardrobe> originalWardrobes = new ArrayList<>();
    private List<Wardrobe> filteredWardrobes = new ArrayList<>();
    private String currentCategory = "ALL";
    private boolean showFavoritesOnly = false;

    // 페이지네이션 설정
    private static final int ITEMS_PER_PAGE = 10;
    private static final int ITEMS_PER_ROW = 5;

    // FXML 컨트롤
    @FXML private VBox contentContainer;
    @FXML private Pagination pagination;
    @FXML private TextField searchField;
    @FXML private ComboBox<String> sortComboBox;
    @FXML private VBox statusContainer;
    @FXML private Label statusLabel;

    // 카테고리 버튼들
    @FXML private Button allButton, topButton, pantsButton, dressButton;
    @FXML private Button bagButton, outerButton, shoesButton, accessoryButton, etcButton;
    @FXML private Button favoriteButton;

    @FXML
    public void initialize() {
        setupUI();
        loadWardrobes();
    }

    private void setupUI() {
        // 검색 기능 설정
        setupSearchFunctionality();

        // 정렬 기능 설정
        setupSortFunctionality();

        // 카테고리 버튼 스타일 설정
        setupCategoryButtons();

        // 페이지네이션 설정
        setupPagination();
    }

    private void setupSearchFunctionality() {
        if (searchField != null) {
            searchField.textProperty().addListener((observable, oldValue, newValue) -> {
                Platform.runLater(() -> filterAndDisplayWardrobes());
            });
        }
    }

    private void setupSortFunctionality() {
        if (sortComboBox != null) {
            sortComboBox.setOnAction(e -> sortWardrobes());
        }
    }

    private void setupCategoryButtons() {
        // 초기 선택 버튼 스타일
        if (allButton != null) {
            updateCategoryButtonStyle(allButton, true);
        }
    }

    private void setupPagination() {
        if (pagination != null) {
            pagination.setPageFactory(this::createPage);
        }
    }

    private void loadWardrobes() {
        showStatus("로딩 중...", true);

        Task<List<Wardrobe>> loadTask = new Task<List<Wardrobe>>() {
            @Override
            protected List<Wardrobe> call() throws Exception {
                long memberId = ImsiSession.getMemberId();
                return wardrobeService.getAllWardrobe(memberId);
            }

            @Override
            protected void succeeded() {
                originalWardrobes = getValue();
                filteredWardrobes = new ArrayList<>(originalWardrobes);
                Platform.runLater(() -> {
                    hideStatus();
                    filterAndDisplayWardrobes();
                });
            }

            @Override
            protected void failed() {
                Platform.runLater(() -> {
                    showStatus("옷장 데이터를 불러오는데 실패했습니다.", true);
                });
            }
        };

        new Thread(loadTask).start();
    }

    private void filterAndDisplayWardrobes() {
        // 카테고리 필터
        Stream<Wardrobe> stream = originalWardrobes.stream();

        if (!"ALL".equals(currentCategory)) {
            stream = stream.filter(w -> {
                String category = getCategoryFromId(w.getCategoryId());
                return currentCategory.equals(category);
            });
        }

        // 즐겨찾기 필터 (기존 DB 구조에서는 "Y"/"N" 문자열 사용)
        if (showFavoritesOnly) {
            stream = stream.filter(w -> "Y".equals(w.getLike()));
        }

        // 검색 필터
        if (searchField != null) {
            String searchText = searchField.getText();
            if (searchText != null && !searchText.trim().isEmpty()) {
                stream = stream.filter(w ->
                        w.getName() != null && w.getName().toLowerCase().contains(searchText.toLowerCase())
                );
            }
        }

        filteredWardrobes = stream.collect(Collectors.toList());

        // 정렬 적용
        sortWardrobes();

        // 페이지네이션 업데이트
        updatePagination();

        // 첫 번째 페이지 표시
        if (pagination != null && pagination.getPageCount() > 0) {
            pagination.setCurrentPageIndex(0);
        }
    }

    private String getCategoryFromId(Long categoryId) {
        if (categoryId == null) return "ETC";

        // 카테고리 ID에 따른 매핑 (실제 DB 구조에 맞게 수정 필요)
        switch (categoryId.intValue()) {
            case 1: return "TOP";
            case 2: return "PANTS";
            case 3: return "DRESS";
            case 4: return "BAG";
            case 5: return "OUTER";
            case 6: return "SHOES";
            case 7: return "ACCESSORY";
            default: return "ETC";
        }
    }

    private void sortWardrobes() {
        if (sortComboBox == null || sortComboBox.getValue() == null) return;

        String selectedSort = sortComboBox.getValue();

        switch (selectedSort) {
            case "등록일 순":
                // ID 기준으로 정렬 (등록 순서)
                filteredWardrobes.sort(Comparator.comparing(Wardrobe::getId).reversed());
                break;
            case "이름순":
                filteredWardrobes.sort(Comparator.comparing(w ->
                        w.getName() != null ? w.getName() : ""));
                break;
            case "최근 착용순":
                // 기존 구조에서는 착용 날짜 필드가 없으므로 ID로 대체
                filteredWardrobes.sort(Comparator.comparing(Wardrobe::getId).reversed());
                break;
        }

        updatePagination();
        if (pagination != null && pagination.getPageCount() > 0) {
            pagination.setCurrentPageIndex(0);
        }
    }

    private void updatePagination() {
        if (pagination == null) return;

        int totalPages = Math.max(1, (int) Math.ceil((double) filteredWardrobes.size() / ITEMS_PER_PAGE));
        pagination.setPageCount(totalPages);
    }

    private VBox createPage(int pageIndex) {
        VBox pageContent = new VBox();
        pageContent.setSpacing(1);

        if (filteredWardrobes.isEmpty()) {
            showEmptyState(pageContent);
            return pageContent;
        }

        int startIndex = pageIndex * ITEMS_PER_PAGE;
        int endIndex = Math.min(startIndex + ITEMS_PER_PAGE, filteredWardrobes.size());

        List<Wardrobe> pageItems = filteredWardrobes.subList(startIndex, endIndex);
        displayWardrobesOnPage(pageContent, pageItems);

        return pageContent;
    }

    private void displayWardrobesOnPage(VBox pageContent, List<Wardrobe> wardrobes) {
        HBox currentRow = null;

        for (int i = 0; i < wardrobes.size(); i++) {
            if (i % ITEMS_PER_ROW == 0) {
                currentRow = new HBox();
                currentRow.setSpacing(0);
                pageContent.getChildren().add(currentRow);
            }

            VBox itemBox = createWardrobeItem(wardrobes.get(i));
            currentRow.getChildren().add(itemBox);
        }
    }

    private VBox createWardrobeItem(Wardrobe wardrobe) {
        VBox itemBox = new VBox();
        itemBox.setAlignment(Pos.CENTER);
        itemBox.setPrefSize(176, 254);
        itemBox.setSpacing(10);
        itemBox.setStyle("-fx-background-color: #e0e0e0; -fx-cursor: hand;");

        // 이미지와 하트 아이콘을 담을 StackPane
        StackPane imageStack = new StackPane();
        imageStack.setAlignment(Pos.BOTTOM_RIGHT);

        // 옷 이미지
        ImageView clothesImage = new ImageView();
        clothesImage.setFitHeight(220);
        clothesImage.setFitWidth(176);
        clothesImage.setPreserveRatio(true);

        // 이미지 설정
        setClothesImage(clothesImage, wardrobe);

        // 하트 아이콘
        ImageView heartIcon = createHeartIcon(wardrobe);

        imageStack.getChildren().addAll(clothesImage, heartIcon);

        // 상품명 라벨
        Label nameLabel = new Label(wardrobe.getName() != null ? wardrobe.getName() : "이름 없음");
        nameLabel.setAlignment(Pos.CENTER);
        nameLabel.setPrefWidth(176);
        nameLabel.setStyle("-fx-font-size: 12; -fx-text-alignment: center;");
        nameLabel.setWrapText(true);

        // 클릭 이벤트 추가
        itemBox.setOnMouseClicked(event -> {
            if (!event.isConsumed()) {
                handleItemClick(wardrobe);
            }
        });

        // 호버 효과
        itemBox.setOnMouseEntered(e ->
                itemBox.setStyle("-fx-background-color: #d0d0d0; -fx-cursor: hand;"));
        itemBox.setOnMouseExited(e ->
                itemBox.setStyle("-fx-background-color: #e0e0e0; -fx-cursor: hand;"));

        itemBox.getChildren().addAll(imageStack, nameLabel);
        return itemBox;
    }

    private void setClothesImage(ImageView imageView, Wardrobe wardrobe) {
        try {
            if (wardrobe.getPicture() != null && wardrobe.getPicture().length > 0) {
                ByteArrayInputStream bis = new ByteArrayInputStream(wardrobe.getPicture());
                Image image = new Image(bis);
                if (!image.isError()) {
                    imageView.setImage(image);
                    return;
                }
            }
        } catch (Exception e) {
            System.err.println("이미지 로딩 실패: " + e.getMessage());
        }

        // 기본 이미지 설정
        try {
            Image defaultImage = new Image(getClass().getResourceAsStream("/images/firstImg.png"));
            if (defaultImage != null && !defaultImage.isError()) {
                imageView.setImage(defaultImage);
            }
        } catch (Exception e) {
            System.err.println("기본 이미지 로딩 실패: " + e.getMessage());
        }
    }

    private ImageView createHeartIcon(Wardrobe wardrobe) {
        ImageView heartIcon = new ImageView();
        heartIcon.setFitHeight(24);
        heartIcon.setFitWidth(24);
        heartIcon.setPreserveRatio(true);

        boolean isFavorite = "Y".equals(wardrobe.getLike());
        updateHeartIcon(heartIcon, isFavorite);

        heartIcon.setOnMouseClicked(event -> {
            event.consume(); // 부모 클릭 이벤트 방지
            toggleFavorite(wardrobe, heartIcon);
        });

        StackPane.setAlignment(heartIcon, Pos.BOTTOM_RIGHT);
        StackPane.setMargin(heartIcon, new Insets(0, 8, 8, 0));

        return heartIcon;
    }

    private void updateHeartIcon(ImageView heartIcon, boolean isFavorite) {
        try {
            String iconPath = isFavorite ? "/images/heart-filled.png" : "/images/heart.png";
            Image heartImage = new Image(getClass().getResourceAsStream(iconPath));
            if (heartImage != null && !heartImage.isError()) {
                heartIcon.setImage(heartImage);
            }
        } catch (Exception e) {
            // 기본 하트 이미지 로딩 시도
            try {
                Image heartImage = new Image(getClass().getResourceAsStream("/images/heart.png"));
                if (heartImage != null && !heartImage.isError()) {
                    heartIcon.setImage(heartImage);
                }
            } catch (Exception ex) {
                System.err.println("하트 아이콘 로딩 실패: " + ex.getMessage());
            }
        }
    }

    private void toggleFavorite(Wardrobe wardrobe, ImageView heartIcon) {
        try {
            boolean currentFavorite = "Y".equals(wardrobe.getLike());
            String newFavoriteStatus = currentFavorite ? "N" : "Y";

            // 모델 업데이트
            wardrobe.setLike(newFavoriteStatus);

            // DB 업데이트
            wardrobeService.updateWardrobe(wardrobe);

            // 아이콘 업데이트
            updateHeartIcon(heartIcon, "Y".equals(newFavoriteStatus));

            // 즐겨찾기 필터가 활성화된 경우 새로고침
            if (showFavoritesOnly) {
                filterAndDisplayWardrobes();
            }
        } catch (Exception e) {
            showAlert("즐겨찾기 업데이트에 실패했습니다: " + e.getMessage());
        }
    }

    private void showEmptyState(VBox container) {
        Label emptyLabel = new Label();
        if (showFavoritesOnly) {
            emptyLabel.setText("즐겨찾기한 옷이 없습니다.");
        } else if (searchField != null && !searchField.getText().isEmpty()) {
            emptyLabel.setText("검색 결과가 없습니다.");
        } else {
            emptyLabel.setText("등록된 옷이 없습니다.");
        }

        emptyLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: #666;");
        VBox emptyBox = new VBox(emptyLabel);
        emptyBox.setAlignment(Pos.CENTER);
        emptyBox.setPrefHeight(200);
        container.getChildren().add(emptyBox);
    }

    private void showStatus(String message, boolean visible) {
        if (statusLabel != null && statusContainer != null) {
            statusLabel.setText(message);
            statusContainer.setVisible(visible);
        }
    }

    private void hideStatus() {
        if (statusContainer != null) {
            statusContainer.setVisible(false);
        }
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("오류");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // 카테고리 필터 메서드들
    @FXML
    private void filterAll() {
        updateCategoryFilter("ALL", allButton);
    }

    @FXML
    private void filterTops() {
        updateCategoryFilter("TOP", topButton);
    }

    @FXML
    private void filterPants() {
        updateCategoryFilter("PANTS", pantsButton);
    }

    @FXML
    private void filterDress() {
        updateCategoryFilter("DRESS", dressButton);
    }

    @FXML
    private void filterBags() {
        updateCategoryFilter("BAG", bagButton);
    }

    @FXML
    private void filterOuter() {
        updateCategoryFilter("OUTER", outerButton);
    }

    @FXML
    private void filterShoes() {
        updateCategoryFilter("SHOES", shoesButton);
    }

    @FXML
    private void filterAccessory() {
        updateCategoryFilter("ACCESSORY", accessoryButton);
    }

    @FXML
    private void filterEtc() {
        updateCategoryFilter("ETC", etcButton);
    }

    @FXML
    private void filterFavorites() {
        showFavoritesOnly = !showFavoritesOnly;
        updateFavoriteButtonStyle();
        filterAndDisplayWardrobes();
    }

    private void updateCategoryFilter(String category, Button selectedButton) {
        // 이전 버튼 스타일 리셋
        resetAllCategoryButtons();

        // 새 버튼 스타일 적용
        updateCategoryButtonStyle(selectedButton, true);

        currentCategory = category;
        filterAndDisplayWardrobes();
    }

    private void resetAllCategoryButtons() {
        Button[] buttons = {allButton, topButton, pantsButton, dressButton,
                bagButton, outerButton, shoesButton, accessoryButton, etcButton};

        for (Button button : buttons) {
            if (button != null) {
                updateCategoryButtonStyle(button, false);
            }
        }
    }

    private void updateCategoryButtonStyle(Button button, boolean selected) {
        if (button == null) return;

        if (selected) {
            button.setStyle("-fx-text-fill: white; -fx-background-color: #007acc; -fx-border-color: #007acc;");
        } else {
            button.setStyle("-fx-text-fill: black; -fx-background-color: transparent; -fx-border-color: transparent;");
        }
    }

    private void updateFavoriteButtonStyle() {
        if (favoriteButton == null) return;

        if (showFavoritesOnly) {
            favoriteButton.setStyle("-fx-background-color: #007acc; -fx-border-color: #007acc; -fx-text-fill: white; -fx-font-size: 13px; -fx-font-weight: normal; -fx-background-radius: 4; -fx-border-radius: 4; -fx-padding: 6 12; -fx-cursor: hand;");
        } else {
            favoriteButton.setStyle("-fx-background-color: transparent; -fx-border-color: #cccccc; -fx-border-width: 1; -fx-text-fill: #000000; -fx-font-size: 13px; -fx-font-weight: normal; -fx-background-radius: 4; -fx-border-radius: 4; -fx-padding: 6 12; -fx-cursor: hand;");
        }
    }

    @FXML
    private void handleSortChange() {
        sortWardrobes();
    }

    // 아이템 클릭 시 상세 페이지로 이동 (기존 DetailWardrobeController 사용)
    private void handleItemClick(Wardrobe wardrobe) {
        try {
            // DetailWardrobeController에서 사용할 수 있도록 데이터 저장
            WardrobeDetailData.setSelectedWardrobe(wardrobe);
            MainLayoutController.loadView("/com/samyukgu/what2wear/wardrobe/wardrobeDetail.fxml");
        } catch (Exception e) {
            showAlert("상세 페이지로 이동하는데 실패했습니다: " + e.getMessage());
        }
    }

    // 추가 버튼 클릭시 옷 추가 페이지로 이동
    @FXML
    private void handleAddWardrobeClick() {
        MainLayoutController.loadView("/com/samyukgu/what2wear/wardrobe/wardrobeAdd.fxml");
    }

    // 나만의 코디 클릭시 나만의 코디 페이지로 이동
    @FXML
    private void handleMyCodiClick() {
        MainLayoutController.loadView("/com/samyukgu/what2wear/myCodi/myCodiList.fxml");
    }

    // 새로고침 메서드 (필요시 호출)
    public void refresh() {
        loadWardrobes();
    }
}

