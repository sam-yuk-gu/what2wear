package com.samyukgu.what2wear.wardrobe.controller;

import com.samyukgu.what2wear.di.DIContainer;
import com.samyukgu.what2wear.layout.controller.MainLayoutController;
import com.samyukgu.what2wear.member.Session.MemberSession;
import com.samyukgu.what2wear.member.model.Member;
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
    private WardrobeService wardrobeService;
    private MemberSession memberSession;

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
        DIContainer diContainer = DIContainer.getInstance();
        wardrobeService = diContainer.resolve(WardrobeService.class);
        memberSession = diContainer.resolve(MemberSession.class);
        setupUI();

        // contentContainer 강제 크기 설정
        if (contentContainer != null) {
            contentContainer.setPrefWidth(988);
            contentContainer.setMaxWidth(988);
            contentContainer.setFillWidth(true);
        }
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

    // 정렬 기능 설정
    private void setupSortFunctionality() {
        if (sortComboBox != null) {
            // 기본값 설정 (최신 순)
            sortComboBox.setValue("최신 순");

            // 선택 변경 이벤트 리스너 추가
            sortComboBox.setOnAction(e -> {
                System.out.println("ComboBox Action 이벤트 발생");
                handleSortChange();
            });

            // 값 변경 리스너도 추가 (더 안전한 방법)
            sortComboBox.valueProperty().addListener((observable, oldValue, newValue) -> {
                if (newValue != null && !newValue.equals(oldValue)) {
                    System.out.println("정렬 옵션 변경: " + oldValue + " -> " + newValue);
                    Platform.runLater(() -> sortWardrobes());
                }
            });

            System.out.println("정렬 기능 설정 완료");
        } else {
            System.out.println("ERROR: sortComboBox가 null입니다!");
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

    // 옷장 로딩
    private void loadWardrobes() {
        showStatus("로딩 중...", true);

        Task<List<Wardrobe>> loadTask = new Task<List<Wardrobe>>() {
            @Override
            protected List<Wardrobe> call() throws Exception {
                if (memberSession == null) {
                    throw new IllegalStateException("로그인된 사용자가 없습니다.");
                }
                long memberId = memberSession.getMember().getId();
                return wardrobeService.getAllWardrobe(memberId);
            }

            @Override
            protected void succeeded() {
                originalWardrobes = getValue();
                filteredWardrobes = new ArrayList<>(originalWardrobes);
                Platform.runLater(() -> {
                    hideStatus();
                    // 초기 정렬 적용 후 화면 표시
                    sortWardrobes(); // 기본 "최신 순" 정렬 적용
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
    // 정렬 메서드
    private void sortWardrobes() {
        if (sortComboBox == null || sortComboBox.getValue() == null) {
            System.out.println("sortComboBox가 null이거나 선택된 값이 없습니다.");
            return;
        }

        String selectedSort = sortComboBox.getValue();
        System.out.println("선택된 정렬 옵션: " + selectedSort);

        switch (selectedSort) {
            case "최신 순":
                // 최신 등록된 옷부터 (ID가 높은 순서대로)
                filteredWardrobes.sort(Comparator.comparing(Wardrobe::getId).reversed());
                System.out.println("최신 순으로 정렬 완료");
                break;

            case "오래된 순":
                // 오래된 옷부터 (ID가 낮은 순서대로)
                filteredWardrobes.sort(Comparator.comparing(Wardrobe::getId));
                System.out.println("오래된 순으로 정렬 완료");
                break;

            case "이름순":
                // 가나다 순 (한글 정렬)
                filteredWardrobes.sort(Comparator.comparing(wardrobe -> {
                    String name = wardrobe.getName();
                    // null이나 빈 문자열인 경우 맨 뒤로 보내기
                    return name != null ? name.trim() : "zzz";
                }));
                System.out.println("이름순(가나다순)으로 정렬 완료");
                break;

            default:
                System.out.println("알 수 없는 정렬 옵션: " + selectedSort);
                // 기본값은 최신 순
                filteredWardrobes.sort(Comparator.comparing(Wardrobe::getId).reversed());
                break;
        }

        // 정렬 후 페이지네이션 업데이트
        updatePagination();

        // 첫 번째 페이지로 이동
        if (pagination != null && pagination.getPageCount() > 0) {
            pagination.setCurrentPageIndex(0);
        }

        // 화면 새로고침
        Platform.runLater(() -> displayAllItems());
    }

    @FXML
    private void handleSortChange() {
        System.out.println("정렬 옵션 변경됨");
        sortWardrobes();
    }

    // 필터링 메서드
    private void filterAndDisplayWardrobes() {
        Stream<Wardrobe> stream = originalWardrobes.stream();

        // 카테고리 필터링
        if (!"전체".equals(currentCategory)) {
            stream = stream.filter(w -> {
                String category = getCategoryFromId(w.getCategoryId());
                return currentCategory.equals(category);
            });
        }

        // 즐겨찾기 필터링
        if (showFavoritesOnly) {
            stream = stream.filter(w -> "Y".equals(w.getLike()));
        }

        // 검색어 필터링
        if (searchField != null) {
            String searchText = searchField.getText();
            if (searchText != null && !searchText.trim().isEmpty()) {
                stream = stream.filter(w ->
                        w.getName() != null && w.getName().toLowerCase().contains(searchText.toLowerCase())
                );
            }
        }

        filteredWardrobes = stream.collect(Collectors.toList());

        // 필터링 후 현재 선택된 정렬 옵션 적용
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

    private void updatePagination() {
        if (pagination == null) return;

        int totalPages = Math.max(1, (int) Math.ceil((double) filteredWardrobes.size() / ITEMS_PER_PAGE));
        pagination.setPageCount(totalPages);
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

        // 하트 아이콘 (개선된 버전)
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

    // 개선된 하트 아이콘 생성 메서드
    private ImageView createHeartIcon(Wardrobe wardrobe) {
        ImageView heartIcon = new ImageView();
        heartIcon.setFitHeight(25);  // 크기를 약간 키움
        heartIcon.setFitWidth(25);
        heartIcon.setPreserveRatio(true);
        heartIcon.setPickOnBounds(true); // 투명한 부분도 클릭 가능
        heartIcon.setSmooth(true);

        boolean isFavorite = "Y".equals(wardrobe.getLike());

        // 하트 아이콘 이미지 설정
        setHeartIconImage(heartIcon, isFavorite);

        // 클릭 이벤트 설정 (개선됨)
        heartIcon.setOnMouseClicked(event -> {
            event.consume(); // 부모 클릭 이벤트 방지
            System.out.println("하트 아이콘 클릭됨 - 옷 ID: " + wardrobe.getId() + ", 현재 상태: " + wardrobe.getLike());
            toggleFavorite(wardrobe, heartIcon);
        });

        // 호버 효과 추가
        heartIcon.setOnMouseEntered(e -> {
            heartIcon.setOpacity(0.8);
            heartIcon.setScaleX(1.1);
            heartIcon.setScaleY(1.1);
        });

        heartIcon.setOnMouseExited(e -> {
            heartIcon.setOpacity(1.0);
            heartIcon.setScaleX(1.0);
            heartIcon.setScaleY(1.0);
        });

        // 하트 아이콘 위치 조정
        StackPane.setAlignment(heartIcon, Pos.TOP_RIGHT);
        StackPane.setMargin(heartIcon, new Insets(5, 5, 0, 0));

        return heartIcon;
    }

    // 개선된 하트 아이콘 이미지 설정 메서드
    private void setHeartIconImage(ImageView heartIcon, boolean isFavorite) {
        try {
            String iconPath = isFavorite ? "/images/likedY.png" : "/images/likedN.png";

            // 먼저 likedY.png, likedN.png 시도
            if (getClass().getResourceAsStream(iconPath) != null) {
                Image heartImage = new Image(getClass().getResourceAsStream(iconPath));
                if (heartImage != null && !heartImage.isError()) {
                    heartIcon.setImage(heartImage);
                    return;
                }
            }

            // 대체 경로들 시도
            String[] fallbackPaths = {
                    "/wardrobe/images/" + (isFavorite ? "likedY.png" : "likedN.png"),
                    "/com/samyukgu/what2wear/wardrobe/images/" + (isFavorite ? "likedY.png" : "likedN.png"),
                    "/images/heart.png"
            };

            for (String fallbackPath : fallbackPaths) {
                try {
                    if (getClass().getResourceAsStream(fallbackPath) != null) {
                        Image fallbackImage = new Image(getClass().getResourceAsStream(fallbackPath));
                        if (fallbackImage != null && !fallbackImage.isError()) {
                            heartIcon.setImage(fallbackImage);

                            // heart.png를 사용하는 경우 스타일로 구분
                            if (fallbackPath.contains("heart.png")) {
                                heartIcon.setOpacity(isFavorite ? 1.0 : 0.6);
                                if (isFavorite) {
                                    heartIcon.setStyle("-fx-cursor: hand; -fx-effect: dropshadow(gaussian, gold, 3, 0.7, 0, 0);");
                                } else {
                                    heartIcon.setStyle("-fx-cursor: hand;");
                                }
                            }
                            return;
                        }
                    }
                } catch (Exception e) {
                    System.err.println("대체 하트 이미지 로딩 실패: " + fallbackPath);
                }
            }

            // 모든 이미지 로딩 실패 시 기본 설정
            System.err.println("WARNING: 모든 하트 이미지 로딩 실패");
            heartIcon.setOpacity(isFavorite ? 1.0 : 0.5);
            heartIcon.setStyle("-fx-cursor: hand;");

        } catch (Exception e) {
            System.err.println("하트 아이콘 이미지 설정 실패: " + e.getMessage());
        }
    }

    private void updateHeartIcon(ImageView heartIcon, boolean isFavorite) {
        setHeartIconImage(heartIcon, isFavorite);
    }

    // 최적화된 즐겨찾기 토글 메서드
    private void toggleFavorite(Wardrobe wardrobe, ImageView heartIcon) {
        try {
            boolean currentFavorite = "Y".equals(wardrobe.getLike());
            String newFavoriteStatus = currentFavorite ? "N" : "Y";

            System.out.println("즐겨찾기 토글 - 옷 ID: " + wardrobe.getId() +
                    ", 현재: " + wardrobe.getLike() + " -> 새로운: " + newFavoriteStatus);

            // 1. 즉시 UI 업데이트 (사용자 반응성 향상)
            wardrobe.setLike(newFavoriteStatus);
            updateHeartIcon(heartIcon, "Y".equals(newFavoriteStatus));

            // 2. 즐겨찾기 필터가 활성화된 경우 아이템 숨기기/보이기 처리
            if (showFavoritesOnly && "N".equals(newFavoriteStatus)) {
                // 즐겨찾기가 해제되고 즐겨찾기 필터가 활성화된 경우
                // 해당 아이템을 화면에서 제거
                Platform.runLater(() -> {
                    // filteredWardrobes 리스트에서 제거
                    filteredWardrobes.remove(wardrobe);
                    // 화면 새로고침
                    displayAllItems();
                });
            }

            // 3. 백그라운드에서 서버 업데이트
            Task<Void> updateTask = new Task<Void>() {
                @Override
                protected Void call() throws Exception {
                    wardrobeService.toggleFavoriteStatus(wardrobe.getId(), wardrobe.getMemberId());
                    return null;
                }

                @Override
                protected void succeeded() {
                    System.out.println("즐겨찾기 서버 업데이트 성공 - 옷 ID: " + wardrobe.getId());
                    // originalWardrobes 리스트도 업데이트
                    for (Wardrobe original : originalWardrobes) {
                        if (original.getId().equals(wardrobe.getId())) {
                            original.setLike(newFavoriteStatus);
                            break;
                        }
                    }
                }

                @Override
                protected void failed() {
                    Platform.runLater(() -> {
                        System.err.println("즐겨찾기 서버 업데이트 실패: " + getException().getMessage());

                        // 실패 시 UI를 원래 상태로 되돌림
                        String originalStatus = "Y".equals(newFavoriteStatus) ? "N" : "Y";
                        wardrobe.setLike(originalStatus);
                        updateHeartIcon(heartIcon, "Y".equals(originalStatus));

                        // 즐겨찾기 필터가 활성화된 경우 목록 복원
                        if (showFavoritesOnly) {
                            if ("Y".equals(originalStatus)) {
                                // 원래 즐겨찾기였다면 다시 추가
                                if (!filteredWardrobes.contains(wardrobe)) {
                                    filteredWardrobes.add(wardrobe);
                                    displayAllItems();
                                }
                            }
                        }

                        showAlert("즐겨찾기 업데이트에 실패했습니다: " + getException().getMessage());
                    });
                }
            };

            // 데몬 스레드로 실행하여 빠른 응답성 확보
            Thread updateThread = new Thread(updateTask);
            updateThread.setDaemon(true);
            updateThread.start();

        } catch (Exception e) {
            System.err.println("즐겨찾기 토글 실패: " + e.getMessage());
            e.printStackTrace();

            Platform.runLater(() -> {
                showAlert("즐겨찾기 업데이트에 실패했습니다: " + e.getMessage());
            });
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