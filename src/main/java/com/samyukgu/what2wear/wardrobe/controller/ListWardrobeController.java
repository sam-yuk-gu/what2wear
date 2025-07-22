package com.samyukgu.what2wear.wardrobe.controller;

import com.samyukgu.what2wear.layout.controller.MainLayoutController;
import com.samyukgu.what2wear.member.Session.MemberSession;
import com.samyukgu.what2wear.member.model.Member;
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
    private String currentCategory = "전체";
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

        // contentContainer 강제 크기 설정
        if (contentContainer != null) {
            contentContainer.setPrefWidth(988);
            contentContainer.setMaxWidth(988);
            contentContainer.setFillWidth(true);

        }
        loadWardrobes();
    }

    // 페이지네이션을 완전히 무시하고 contentContainer만 사용
    private void displayAllItems() {
        if (contentContainer == null) {
            System.err.println("contentContainer is null!");
            return;
        }

        // 기존 내용 모두 제거
        contentContainer.getChildren().clear();
        if (filteredWardrobes.isEmpty()) {
            showEmptyState(contentContainer);
            return;
        }

        // 페이지네이션 없이 모든 아이템 표시 (또는 첫 페이지만)
        int endIndex = Math.min(ITEMS_PER_PAGE, filteredWardrobes.size());
        List<Wardrobe> itemsToShow = filteredWardrobes.subList(0, endIndex);
        displayWardrobesOnPage(contentContainer, itemsToShow);
        System.out.println("Displayed " + itemsToShow.size() + " items directly in contentContainer");
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
            // 페이지 변경 시에만 contentContainer 업데이트
            pagination.currentPageIndexProperty().addListener((obs, oldIndex, newIndex) -> {
                if (newIndex != null) {
                    displaySpecificPage(newIndex.intValue());
                }
            });

            // 더미 팩토리 설정
            pagination.setPageFactory(pageIndex -> new VBox());
        }
    }
    private void displaySpecificPage(int pageIndex) {
        if (contentContainer == null) return;

        Platform.runLater(() -> {
            contentContainer.getChildren().clear();

            if (filteredWardrobes.isEmpty()) {
                showEmptyState(contentContainer);
                return;
            }
            int startIndex = pageIndex * ITEMS_PER_PAGE;
            int endIndex = Math.min(startIndex + ITEMS_PER_PAGE, filteredWardrobes.size());
            if (startIndex < filteredWardrobes.size()) {
                List<Wardrobe> pageItems = filteredWardrobes.subList(startIndex, endIndex);
                displayWardrobesOnPage(contentContainer, pageItems);
            }
        });
    }

    private VBox createPageForPagination(int pageIndex) {
        VBox pageContent = createPage(pageIndex);
        return pageContent;
    }

    private void loadWardrobes() {
        showStatus("로딩 중...", true);

        Task<List<Wardrobe>> loadTask = new Task<List<Wardrobe>>() {
            @Override
            protected List<Wardrobe> call() throws Exception {
                Member member = MemberSession.getLoginMember();
                if (member == null) {
                    throw new IllegalStateException("로그인된 사용자가 없습니다.");
                }
                long memberId = member.getId();
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
        Stream<Wardrobe> stream = originalWardrobes.stream();
        if (!"전체".equals(currentCategory)) {
            stream = stream.filter(w -> {
                String category = getCategoryFromId(w.getCategoryId());
                return currentCategory.equals(category);
            });
        }

        if (showFavoritesOnly) {
            stream = stream.filter(w -> "Y".equals(w.getLike()));
        }

        if (searchField != null) {
            String searchText = searchField.getText();
            if (searchText != null && !searchText.trim().isEmpty()) {
                stream = stream.filter(w ->
                        w.getName() != null && w.getName().toLowerCase().contains(searchText.toLowerCase())
                );
            }
        }

        filteredWardrobes = stream.collect(Collectors.toList());
        sortWardrobes();
        updatePagination();
        Platform.runLater(() -> displayAllItems());
    }

    private String getCategoryFromId(Long categoryId) {
        if (categoryId == null) return "기타";

        switch (categoryId.intValue()) {
            case 1: return "상의";
            case 2: return "바지";
            case 3: return "원피스/스커트";
            case 4: return "가방";
            case 5: return "아우터";
            case 6: return "신발";
            case 7: return "악세사리";
            default: return "기타";
        }
    }

    private void sortWardrobes() {
        if (sortComboBox == null || sortComboBox.getValue() == null) return;

        String selectedSort = sortComboBox.getValue();

        switch (selectedSort) {
            case "등록일 순":
                // 옷 ID 기준으로 정렬 (등록 순서)
                filteredWardrobes.sort(Comparator.comparing(Wardrobe::getId).reversed());
                break;
            case "이름순":
                filteredWardrobes.sort(Comparator.comparing(w ->
                        w.getName() != null ? w.getName() : ""));
                break;
            case "최근 착용순":
                // 이부분 아직 더 고민이 필요..
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
        pageContent.setSpacing(10);
        pageContent.setPrefWidth(988); // ScrollPane 너비에 맞춤
        pageContent.setMaxWidth(988);
        pageContent.setFillWidth(true);

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

        double containerWidth = 988.0; // ScrollPane 너비
        double itemWidth = 190.0; // 옷 너비 + 여백
        int itemsPerRow = Math.max(1, (int)(containerWidth / itemWidth));

        for (int i = 0; i < wardrobes.size(); i++) {
            if (i % itemsPerRow == 0) {
                currentRow = new HBox();
                currentRow.setSpacing(15); // 옷 간 간격
                currentRow.setAlignment(Pos.TOP_LEFT);
                currentRow.setPrefWidth(988);
                currentRow.setMaxWidth(988);
                currentRow.setFillHeight(false);
                pageContent.getChildren().add(currentRow);
            }

            VBox itemBox = createWardrobeItem(wardrobes.get(i));
            if (currentRow != null) {
                currentRow.getChildren().add(itemBox);
            }
        }
    }

    private VBox createWardrobeItem(Wardrobe wardrobe) {
        VBox itemBox = new VBox();
        itemBox.setAlignment(Pos.TOP_CENTER);

        // 고정 크기 설정
        itemBox.setPrefSize(176, 260);
        itemBox.setMaxSize(176, 260);
        itemBox.setMinSize(176, 260);

        itemBox.setSpacing(8);
        itemBox.setStyle(
                "-fx-background-color: #f8f8f8; " +
                        "-fx-border-color: #e0e0e0; " +
                        "-fx-border-width: 1; " +
                        "-fx-border-radius: 8; " +
                        "-fx-background-radius: 8; " +
                        "-fx-cursor: hand; " +
                        "-fx-padding: 8;"
        );

        // 이미지와 하트 아이콘을 담을 StackPane
        StackPane imageStack = new StackPane();
        imageStack.setAlignment(Pos.CENTER);
        imageStack.setPrefSize(160, 200);
        imageStack.setMaxSize(160, 200);
        imageStack.setMinSize(160, 200);

        // 옷 이미지
        ImageView clothesImage = new ImageView();
        clothesImage.setFitHeight(200);
        clothesImage.setFitWidth(160);
        clothesImage.setPreserveRatio(true);
        clothesImage.setSmooth(true);

        // 이미지 설정
        setClothesImage(clothesImage, wardrobe);

        // 하트 아이콘 (리소스 오류 방지)
        ImageView heartIcon = createHeartIcon(wardrobe);

        imageStack.getChildren().addAll(clothesImage, heartIcon);

        // 상품명 라벨
        Label nameLabel = new Label(wardrobe.getName() != null ? wardrobe.getName() : "이름 없음");
        nameLabel.setAlignment(Pos.CENTER);
        nameLabel.setPrefWidth(160);
        nameLabel.setMaxWidth(160);
        nameLabel.setMaxHeight(40); // 높이 제한
        nameLabel.setStyle("-fx-font-size: 11px; -fx-text-alignment: center; -fx-text-fill: #333;");
        nameLabel.setWrapText(true);

        // 클릭 이벤트 추가
        itemBox.setOnMouseClicked(event -> {
            if (!event.isConsumed()) {
                handleItemClick(wardrobe);
            }
        });

        // 호버 효과
        itemBox.setOnMouseEntered(e ->
                itemBox.setStyle(
                        "-fx-background-color: #e8e8e8; " +
                                "-fx-border-color: #007acc; " +
                                "-fx-border-width: 1; " +
                                "-fx-border-radius: 8; " +
                                "-fx-background-radius: 8; " +
                                "-fx-cursor: hand; " +
                                "-fx-padding: 8;"
                ));
        itemBox.setOnMouseExited(e ->
                itemBox.setStyle(
                        "-fx-background-color: #f8f8f8; " +
                                "-fx-border-color: #e0e0e0; " +
                                "-fx-border-width: 1; " +
                                "-fx-border-radius: 8; " +
                                "-fx-background-radius: 8; " +
                                "-fx-cursor: hand; " +
                                "-fx-padding: 8;"
                ));

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
        heartIcon.setFitHeight(20);
        heartIcon.setFitWidth(20);
        heartIcon.setPreserveRatio(true);

        boolean isFavorite = "Y".equals(wardrobe.getLike());

        // 리소스 오류 방지를 위한 안전한 아이콘 설정
        setHeartIconSafe(heartIcon, isFavorite);

        heartIcon.setOnMouseClicked(event -> {
            event.consume(); // 부모 클릭 이벤트 방지
            toggleFavorite(wardrobe, heartIcon);
        });

        // 하트 아이콘 위치 조정
        StackPane.setAlignment(heartIcon, Pos.TOP_RIGHT);
        StackPane.setMargin(heartIcon, new Insets(5, 5, 0, 0));

        return heartIcon;
    }

    // 안전한 하트 아이콘 설정 메서드
    private void setHeartIconSafe(ImageView heartIcon, boolean isFavorite) {
        try {
            String iconPath = isFavorite ? "/images/heart-filled.png" : "/images/heart.png";

            // 먼저 리소스가 존재하는지 확인
            if (getClass().getResourceAsStream(iconPath) != null) {
                Image heartImage = new Image(getClass().getResourceAsStream(iconPath));
                if (heartImage != null && !heartImage.isError()) {
                    heartIcon.setImage(heartImage);
                    return;
                }
            }

            // 리소스가 없으면 기본 하트 아이콘 시도
            createDefaultHeartIcon(heartIcon, isFavorite);

        } catch (Exception e) {
            System.err.println("하트 아이콘 로딩 실패, 기본 아이콘으로 대체: " + e.getMessage());
            createDefaultHeartIcon(heartIcon, isFavorite);
        }
    }

    // 기본 하트 아이콘 생성
    private void createDefaultHeartIcon(ImageView heartIcon, boolean isFavorite) {
        // 간단한 텍스트 기반 하트 이미지 생성하거나 숨김
        heartIcon.setVisible(true);
    }

    private void updateHeartIcon(ImageView heartIcon, boolean isFavorite) {
        setHeartIconSafe(heartIcon, isFavorite);
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
        updateCategoryFilter("전체", allButton);
    }

    @FXML
    private void filterTops() {
        updateCategoryFilter("상의", topButton);
    }

    @FXML
    private void filterPants() {
        updateCategoryFilter("바지", pantsButton);
    }

    @FXML
    private void filterDress() {
        updateCategoryFilter("원피스/스커트", dressButton);
    }

    @FXML
    private void filterBags() {
        updateCategoryFilter("가방", bagButton);
    }

    @FXML
    private void filterOuter() {
        updateCategoryFilter("아우터", outerButton);
    }

    @FXML
    private void filterShoes() {
        updateCategoryFilter("신발", shoesButton);
    }

    @FXML
    private void filterAccessory() {
        updateCategoryFilter("악세사리", accessoryButton);
    }

    @FXML
    private void filterEtc() {
        updateCategoryFilter("기타", etcButton);
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

    // 아이템 클릭 시 상세 페이지로 이동
    private void handleItemClick(Wardrobe wardrobe) {
        try {
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