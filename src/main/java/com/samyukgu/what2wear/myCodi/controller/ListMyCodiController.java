package com.samyukgu.what2wear.myCodi.controller;

<<<<<<< HEAD
import com.samyukgu.what2wear.layout.controller.MainLayoutController;
=======
>>>>>>> dev
import com.samyukgu.what2wear.di.DIContainer;
import com.samyukgu.what2wear.layout.controller.MainLayoutController;
import com.samyukgu.what2wear.member.Session.MemberSession;
import com.samyukgu.what2wear.myCodi.model.CodiWithDetails;
import com.samyukgu.what2wear.myCodi.service.CodiService;
import com.samyukgu.what2wear.wardrobe.model.Wardrobe;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import java.io.ByteArrayInputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class ListMyCodiController implements Initializable {

    @FXML private VBox codiGridContainer;
    @FXML private Pagination pagination;
    @FXML private TextField searchField;
    @FXML private ComboBox<String> sortComboBox;
    @FXML private Button favoriteButton;
    @FXML private VBox statusContainer;
    @FXML private Label statusLabel;

    private CodiService codiService;
    private MemberSession memberSession;

    // 데이터 저장
    private List<CodiWithDetails> originalCodis = new ArrayList<>();
    private List<CodiWithDetails> filteredCodis = new ArrayList<>();
    private boolean showFavoritesOnly = false;

    // 페이지네이션 설정
    private static final int ITEMS_PER_PAGE = 10;
    private static final int ITEMS_PER_ROW = 5;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        DIContainer diContainer = DIContainer.getInstance();
        codiService = diContainer.resolve(CodiService.class);
        memberSession = diContainer.resolve(MemberSession.class);
        setupUI();

        // codiGridContainer 강제 크기 설정
        if (codiGridContainer != null) {
            codiGridContainer.setPrefWidth(988);
            codiGridContainer.setMaxWidth(988);
            codiGridContainer.setFillWidth(true);
        }

        loadCodis();
    }

    private void setupUI() {
        setupSearchFunctionality();
        setupSortFunctionality();
        setupPagination();
    }

    private void setupSearchFunctionality() {
        if (searchField != null) {
            searchField.textProperty().addListener((observable, oldValue, newValue) -> {
                Platform.runLater(this::filterAndDisplayCodis);
            });
        }
    }

    // 정렬 기능 설정 (수정됨)
    private void setupSortFunctionality() {
        if (sortComboBox != null) {
            // 기본값 설정 (최신 순)
            sortComboBox.setValue("최신 순");

            // 선택 변경 이벤트 리스너 추가
            sortComboBox.setOnAction(e -> {
                System.out.println("코디 ComboBox Action 이벤트 발생");
                handleSortChange();
            });

            // 값 변경 리스너도 추가 (더 안전한 방법)
            sortComboBox.valueProperty().addListener((observable, oldValue, newValue) -> {
                if (newValue != null && !newValue.equals(oldValue)) {
                    System.out.println("코디 정렬 옵션 변경: " + oldValue + " -> " + newValue);
                    Platform.runLater(() -> sortCodis());
                }
            });

            System.out.println("코디 정렬 기능 설정 완료");
        } else {
            System.out.println("ERROR: sortComboBox가 null입니다!");
        }
    }

    private void setupPagination() {
        if (pagination != null) {
            // 페이지 변경 시에만 codiGridContainer 업데이트
            pagination.currentPageIndexProperty().addListener((obs, oldIndex, newIndex) -> {
                if (newIndex != null) {
                    displaySpecificPage(newIndex.intValue());
                }
            });

            // 더미 팩토리 설정
            pagination.setPageFactory(pageIndex -> new VBox());
        }
    }

    // 코디 로딩 (수정됨)
    private void loadCodis() {
        showStatus("로딩 중...", true);

        Task<List<CodiWithDetails>> loadTask = new Task<List<CodiWithDetails>>() {
            @Override
            protected List<CodiWithDetails> call() throws Exception {
                if (memberSession == null) {
                    throw new IllegalStateException("로그인된 사용자가 없습니다.");
                }
                return codiService.getAllCodiWithDetailsByMemberId(memberSession.getMember().getId());
            }

            @Override
            protected void succeeded() {
                originalCodis = getValue();
                filteredCodis = new ArrayList<>(originalCodis);
                Platform.runLater(() -> {
                    hideStatus();
                    // 초기 정렬 적용 후 화면 표시
                    sortCodis(); // 기본 "최신 순" 정렬 적용
                    filterAndDisplayCodis();
                });
            }

            @Override
            protected void failed() {
                Platform.runLater(() -> {
                    showStatus("코디 데이터를 불러오는데 실패했습니다.", true);
                });
            }
        };

        new Thread(loadTask).start();
    }

    // 정렬 메서드 (완전히 새로 작성)
    private void sortCodis() {
        if (sortComboBox == null || sortComboBox.getValue() == null) {
            System.out.println("sortComboBox가 null이거나 선택된 값이 없습니다.");
            return;
        }

        String selectedSort = sortComboBox.getValue();
        System.out.println("선택된 코디 정렬 옵션: " + selectedSort);

        switch (selectedSort) {
            case "최신 순":
                // 최신 등록된 코디부터 (ID가 높은 순서대로)
                filteredCodis.sort(Comparator.comparing(CodiWithDetails::getId).reversed());
                System.out.println("코디 최신 순으로 정렬 완료");
                break;

            case "오래된 순":
                // 오래된 코디부터 (ID가 낮은 순서대로)
                filteredCodis.sort(Comparator.comparing(CodiWithDetails::getId));
                System.out.println("코디 오래된 순으로 정렬 완료");
                break;

            case "이름순":
                // 가나다 순 (한글 정렬)
                filteredCodis.sort(Comparator.comparing(codi -> {
                    String name = codi.getName();
                    // null이나 빈 문자열인 경우 맨 뒤로 보내기
                    return name != null ? name.trim() : "zzz";
                }));
                System.out.println("코디 이름순(가나다순)으로 정렬 완료");
                break;

            default:
                System.out.println("알 수 없는 코디 정렬 옵션: " + selectedSort);
                // 기본값은 최신 순
                filteredCodis.sort(Comparator.comparing(CodiWithDetails::getId).reversed());
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
        System.out.println("코디 정렬 옵션 변경됨");
        sortCodis();
    }

    // 필터링 메서드 (수정됨)
    private void filterAndDisplayCodis() {
        // 검색 필터링
        List<CodiWithDetails> filtered = originalCodis.stream()
                .filter(codi -> {
                    if (searchField != null && searchField.getText() != null && !searchField.getText().trim().isEmpty()) {
                        String searchText = searchField.getText().toLowerCase().trim();
                        return codi.getName() != null && codi.getName().toLowerCase().contains(searchText);
                    }
                    return true;
                })
                .collect(Collectors.toList());

        filteredCodis = filtered;

        // 필터링 후 현재 선택된 정렬 옵션 적용
        sortCodis();

        updatePagination();
        Platform.runLater(this::displayAllItems);
    }

    // 페이지네이션을 완전히 무시하고 codiGridContainer만 사용
    private void displayAllItems() {
        if (codiGridContainer == null) {
            System.err.println("codiGridContainer is null!");
            return;
        }

        // 기존 내용 모두 제거
        codiGridContainer.getChildren().clear();
        if (filteredCodis.isEmpty()) {
            showEmptyState(codiGridContainer);
            return;
        }

        // 페이지네이션 없이 모든 아이템 표시 (또는 첫 페이지만)
        int endIndex = Math.min(ITEMS_PER_PAGE, filteredCodis.size());
        List<CodiWithDetails> itemsToShow = filteredCodis.subList(0, endIndex);
        displayCodisOnPage(codiGridContainer, itemsToShow);
        System.out.println("Displayed " + itemsToShow.size() + " codi items directly in codiGridContainer");
    }

    private void displaySpecificPage(int pageIndex) {
        if (codiGridContainer == null) return;

        Platform.runLater(() -> {
            codiGridContainer.getChildren().clear();

            if (filteredCodis.isEmpty()) {
                showEmptyState(codiGridContainer);
                return;
            }
            int startIndex = pageIndex * ITEMS_PER_PAGE;
            int endIndex = Math.min(startIndex + ITEMS_PER_PAGE, filteredCodis.size());
            if (startIndex < filteredCodis.size()) {
                List<CodiWithDetails> pageItems = filteredCodis.subList(startIndex, endIndex);
                displayCodisOnPage(codiGridContainer, pageItems);
            }
        });
    }

    private void updatePagination() {
        if (pagination == null) return;

        int totalPages = Math.max(1, (int) Math.ceil((double) filteredCodis.size() / ITEMS_PER_PAGE));
        pagination.setPageCount(totalPages);
    }

    private void displayCodisOnPage(VBox pageContent, List<CodiWithDetails> codis) {
        HBox currentRow = null;

        double containerWidth = 988.0; // ScrollPane 너비
        double itemWidth = 190.0; // 코디 너비 + 여백
        int itemsPerRow = Math.max(1, (int)(containerWidth / itemWidth));

        for (int i = 0; i < codis.size(); i++) {
            if (i % itemsPerRow == 0) {
                currentRow = new HBox();
                currentRow.setSpacing(15); // 코디 간 간격
                currentRow.setAlignment(Pos.TOP_LEFT);
                currentRow.setPrefWidth(988);
                currentRow.setMaxWidth(988);
                currentRow.setFillHeight(false);
                pageContent.getChildren().add(currentRow);
            }

            VBox itemBox = createCodiItem(codis.get(i));
            if (currentRow != null) {
                currentRow.getChildren().add(itemBox);
            }
        }
    }
    private VBox createCodiItem(CodiWithDetails codi) {
        VBox itemBox = new VBox();
        itemBox.setAlignment(Pos.TOP_CENTER);

        // 고정 크기 설정 (옷장과 동일)
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

        // 이미지 영역을 담을 StackPane
        StackPane imageStack = new StackPane();
        imageStack.setAlignment(Pos.CENTER);
        imageStack.setPrefSize(160, 200);
        imageStack.setMaxSize(160, 200);
        imageStack.setMinSize(160, 200);

        // 코디 이미지 영역 (옷들의 조합을 보여주는 영역)
        VBox imageArea = createCodiImageArea(codi);

        imageStack.getChildren().add(imageArea);

        // 코디 이름
        Label nameLabel = new Label(codi.getName() != null ? codi.getName() : "이름 없음");
        nameLabel.setAlignment(Pos.CENTER);
        nameLabel.setPrefWidth(160);
        nameLabel.setMaxWidth(160);
        nameLabel.setMaxHeight(40); // 높이 제한
        nameLabel.setStyle("-fx-font-size: 11px; -fx-text-alignment: center; -fx-text-fill: #333;");
        nameLabel.setWrapText(true);

        // 클릭 이벤트
        itemBox.setOnMouseClicked(event -> {
            if (!event.isConsumed()) {
                handleCodiClick(codi);
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

    private VBox createCodiImageArea(CodiWithDetails codi) {
        VBox imageArea = new VBox();
        imageArea.setAlignment(Pos.CENTER);
        imageArea.setPrefSize(160, 200);
        imageArea.setMaxSize(160, 200);
        imageArea.setSpacing(3); // 행 간격을 좀 더 줄임

        if (codi.getClothes() == null || codi.getClothes().isEmpty()) {
            Label emptyLabel = new Label("옷이 선택되지 않음");
            emptyLabel.setStyle("-fx-text-fill: #999; -fx-font-size: 12px;");
            imageArea.getChildren().add(emptyLabel);
            return imageArea;
        }

        // 옷들을 3행으로 배치
        HBox topRow = new HBox();
        HBox middleRow = new HBox();
        HBox bottomRow = new HBox();

        topRow.setAlignment(Pos.CENTER);
        middleRow.setAlignment(Pos.CENTER);
        bottomRow.setAlignment(Pos.CENTER);

        topRow.setSpacing(2);
        middleRow.setSpacing(2);
        bottomRow.setSpacing(2);

        List<Wardrobe> clothes = codi.getClothes();
        int totalItems = clothes.size();

        // 3행에 균등하게 배분하기 위한 계산
        int itemsPerRow;
        if (totalItems <= 3) {
            itemsPerRow = 1; // 아이템이 3개 이하면 한 행에 1개씩
        } else if (totalItems <= 6) {
            itemsPerRow = 2; // 4-6개면 한 행에 2개씩
        } else {
            itemsPerRow = Math.min(3, (int) Math.ceil(totalItems / 3.0)); // 7개 이상이면 한 행에 최대 3개
        }

        for (int i = 0; i < clothes.size(); i++) {
            ImageView clothesImage = createSmallClothesImage(clothes.get(i));

            if (i < itemsPerRow) {
                topRow.getChildren().add(clothesImage);
            } else if (i < itemsPerRow * 2) {
                middleRow.getChildren().add(clothesImage);
            } else if (i < itemsPerRow * 3) {
                bottomRow.getChildren().add(clothesImage);
            }
            // 9개 이상인 경우는 처음 9개만 표시
        }

        // 비어있지 않은 행만 추가
        if (!topRow.getChildren().isEmpty()) {
            imageArea.getChildren().add(topRow);
        }
        if (!middleRow.getChildren().isEmpty()) {
            imageArea.getChildren().add(middleRow);
        }
        if (!bottomRow.getChildren().isEmpty()) {
            imageArea.getChildren().add(bottomRow);
        }

        return imageArea;
    }

    private ImageView createSmallClothesImage(Wardrobe wardrobe) {
        ImageView imageView = new ImageView();
        imageView.setFitHeight(40);
        imageView.setFitWidth(40);
        imageView.setPreserveRatio(true);
        imageView.setSmooth(true);

        try {
            if (wardrobe.getPicture() != null && wardrobe.getPicture().length > 0) {
                ByteArrayInputStream bis = new ByteArrayInputStream(wardrobe.getPicture());
                Image image = new Image(bis);
                if (!image.isError()) {
                    imageView.setImage(image);
                    return imageView;
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

        return imageView;
    }

    private void showEmptyState(VBox container) {
        Label emptyLabel = new Label();
        if (showFavoritesOnly) {
            emptyLabel.setText("즐겨찾기한 코디가 없습니다.");
        } else if (searchField != null && !searchField.getText().isEmpty()) {
            emptyLabel.setText("검색 결과가 없습니다.");
        } else {
            emptyLabel.setText("등록된 코디가 없습니다.");
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

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("오류");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void handleCodiClick(CodiWithDetails codi) {
        try {
            MyCodiDetailData.setSelectedCodi(codi);
            MainLayoutController.loadView("/com/samyukgu/what2wear/myCodi/myCodiDetail.fxml");
        } catch (Exception e) {
            showError("상세 페이지로 이동하는데 실패했습니다: " + e.getMessage());
        }
    }

    // 즐겨찾기 필터링 (향후 구현)
    @FXML
    private void filterFavorites() {
        showFavoritesOnly = !showFavoritesOnly;
        updateFavoriteButtonStyle();
        filterAndDisplayCodis();
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
    private void handleAddMyCodiClick() {
        MainLayoutController.loadView("/com/samyukgu/what2wear/myCodi/myCodiAdd.fxml");
    }

    @FXML
    private void handleWardrobeClick() {
        MainLayoutController.loadView("/com/samyukgu/what2wear/wardrobe/wardrobeList.fxml");
    }

    public void refresh() {
        loadCodis();
    }
}