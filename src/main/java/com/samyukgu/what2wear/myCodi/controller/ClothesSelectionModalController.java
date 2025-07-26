package com.samyukgu.what2wear.myCodi.controller;

import com.samyukgu.what2wear.di.DIContainer;
import com.samyukgu.what2wear.member.Session.MemberSession;
import com.samyukgu.what2wear.wardrobe.model.Wardrobe;
import com.samyukgu.what2wear.wardrobe.service.WardrobeService;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.ByteArrayInputStream;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class ClothesSelectionModalController implements Initializable {

    @FXML private TextField searchField;
    @FXML private FlowPane clothesContainer;
    @FXML private Label categoryLabel;
    @FXML private Label countLabel;
    @FXML private Button confirmButton;
    @FXML private Button cancelButton;
    @FXML private ProgressIndicator loadingIndicator;

    private WardrobeService wardrobeService;
    private MemberSession memberSession;

    private List<Wardrobe> allClothes;
    private List<Wardrobe> filteredClothes;
    private Wardrobe selectedWardrobe;
    private Long categoryId;
    private String categoryName;
    private ClothesSelectionCallback callback;

    // 결과를 받을 콜백 인터페이스
    @FunctionalInterface
    public interface ClothesSelectionCallback {
        void onClothesSelected(Wardrobe clothes);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        DIContainer diContainer = DIContainer.getInstance();
        wardrobeService = diContainer.resolve(WardrobeService.class);
        memberSession = diContainer.resolve(MemberSession.class);
        setupUI();
        setupEventHandlers();
        setupKeyboardShortcuts();
    }

    private void setupKeyboardShortcuts() {
        // Scene이 설정된 후에 실행되도록
        Platform.runLater(() -> {
            if (cancelButton != null && cancelButton.getScene() != null) {
                cancelButton.getScene().setOnKeyPressed(event -> {
                    if (event.getCode() == KeyCode.ESCAPE) {
                        handleCancel();
                    }
                });
            }
        });
    }

    private void setupUI() {
        // 검색 필드 설정
        if (searchField != null) {
            searchField.setPromptText("옷 이름으로 검색");
        }

        // 컨테이너 설정 - 정렬 추가
        if (clothesContainer != null) {
            clothesContainer.setHgap(10);
            clothesContainer.setVgap(10);
            clothesContainer.setPadding(new Insets(10));

            // 위에서부터 채워지도록 정렬 설정
            clothesContainer.setAlignment(Pos.TOP_LEFT);
            // 또는 가운데 정렬을 원한다면:
            // clothesContainer.setAlignment(Pos.TOP_CENTER);
        }

        // 확인 버튼 초기 비활성화
        if (confirmButton != null) {
            confirmButton.setDisable(true);
        }

        // 로딩 인디케이터 숨기기
        if (loadingIndicator != null) {
            loadingIndicator.setVisible(false);
        }
    }

    private void setupEventHandlers() {
        // 검색 기능
        if (searchField != null) {
            searchField.textProperty().addListener((observable, oldValue, newValue) -> {
                filterClothes(newValue);
            });
        }

        // 버튼 이벤트
        if (confirmButton != null) {
            confirmButton.setOnAction(e -> handleConfirm());
        }

        if (cancelButton != null) {
            cancelButton.setOnAction(e -> handleCancel());
        }
    }

    // 모달 초기화 메서드
    public void initializeModal(Long categoryId, String categoryName, ClothesSelectionCallback callback) {
        this.categoryId = categoryId;
        this.categoryName = categoryName;
        this.callback = callback;

        if (categoryLabel != null) {
            categoryLabel.setText(categoryName + " 선택");
        }

        loadClothes();
    }

    private void loadClothes() {
        if (loadingIndicator != null) {
            loadingIndicator.setVisible(true);
        }

        Task<List<Wardrobe>> loadTask = new Task<List<Wardrobe>>() {
            @Override
            protected List<Wardrobe> call() throws Exception {
                if (memberSession == null) {
                    throw new IllegalStateException("로그인된 사용자가 없습니다.");
                }

                List<Wardrobe> allWardrobe = wardrobeService.getAllWardrobe(memberSession.getMember().getId());

                // 카테고리 필터링
                if (categoryId != null) {
                    return allWardrobe.stream()
                            .filter(w -> categoryId.equals(w.getCategoryId()))
                            .collect(Collectors.toList());
                } else {
                    // 기타 카테고리인 경우 모든 옷 반환
                    return allWardrobe;
                }
            }

            @Override
            protected void succeeded() {
                allClothes = getValue();
                filteredClothes = allClothes;
                Platform.runLater(() -> {
                    if (loadingIndicator != null) {
                        loadingIndicator.setVisible(false);
                    }
                    displayClothes();
                    updateCountLabel();
                });
            }

            @Override
            protected void failed() {
                Platform.runLater(() -> {
                    if (loadingIndicator != null) {
                        loadingIndicator.setVisible(false);
                    }
                    showError("옷 정보를 불러오는데 실패했습니다: " + getException().getMessage());
                });
            }
        };

        new Thread(loadTask).start();
    }

    private void displayClothes() {
        if (clothesContainer == null) return;

        clothesContainer.getChildren().clear();

        if (filteredClothes == null || filteredClothes.isEmpty()) {
            VBox emptyBox = new VBox();
            emptyBox.setAlignment(Pos.CENTER);
            emptyBox.setPrefSize(680, 300);

            Label emptyLabel = new Label("해당 카테고리의 옷이 없습니다.");
            emptyLabel.setStyle("-fx-text-fill: #666; -fx-font-size: 16px;");

            Label suggestionLabel = new Label("먼저 옷장에 옷을 추가해주세요.");
            suggestionLabel.setStyle("-fx-text-fill: #999; -fx-font-size: 14px;");

            emptyBox.getChildren().addAll(emptyLabel, suggestionLabel);
            clothesContainer.getChildren().add(emptyBox);
            return;
        }

        for (Wardrobe wardrobe : filteredClothes) {
            VBox clothesItem = createClothesItem(wardrobe);
            clothesContainer.getChildren().add(clothesItem);
        }
    }

    private VBox createClothesItem(Wardrobe wardrobe) {
        VBox itemBox = new VBox();
        itemBox.setAlignment(Pos.CENTER);
        itemBox.setSpacing(5);
        itemBox.setPrefSize(120, 150);
        itemBox.setMaxSize(120, 150);
        itemBox.setMinSize(120, 150);
        itemBox.setPadding(new Insets(5));
        itemBox.setStyle(
                "-fx-background-color: #f8f8f8; " +
                        "-fx-border-color: #e0e0e0; " +
                        "-fx-border-width: 1; " +
                        "-fx-border-radius: 8; " +
                        "-fx-background-radius: 8; " +
                        "-fx-cursor: hand;"
        );

        // 옷 이미지
        ImageView imageView = new ImageView();
        imageView.setFitHeight(80);
        imageView.setFitWidth(80);
        imageView.setPreserveRatio(true);
        imageView.setSmooth(true);
        setClothesImage(imageView, wardrobe);

        // 옷 이름
        Label nameLabel = new Label(wardrobe.getName() != null ? wardrobe.getName() : "이름 없음");
        nameLabel.setAlignment(Pos.CENTER);
        nameLabel.setPrefWidth(110);
        nameLabel.setMaxWidth(110);
        nameLabel.setMaxHeight(40);
        nameLabel.setStyle("-fx-font-size: 11px; -fx-text-alignment: center;");
        nameLabel.setWrapText(true);

        // 브랜드 정보
        if (wardrobe.getBrand() != null && !wardrobe.getBrand().trim().isEmpty()) {
            Label brandLabel = new Label(wardrobe.getBrand());
            brandLabel.setStyle("-fx-font-size: 9px; -fx-text-fill: #888;");
            itemBox.getChildren().addAll(imageView, nameLabel, brandLabel);
        } else {
            itemBox.getChildren().addAll(imageView, nameLabel);
        }

        // 클릭 이벤트
        itemBox.setOnMouseClicked(event -> {
            if (event.getClickCount() == 1) {
                selectClothes(wardrobe, itemBox);
            } else if (event.getClickCount() == 2) {
                // 더블클릭 시 바로 선택 및 모달 닫기
                selectClothes(wardrobe, itemBox);
                handleConfirm();
            }
        });

        // 호버 효과
        itemBox.setOnMouseEntered(e ->
                itemBox.setStyle(
                        "-fx-background-color: #e8e8e8; " +
                                "-fx-border-color: #007acc; " +
                                "-fx-border-width: 2; " +
                                "-fx-border-radius: 8; " +
                                "-fx-background-radius: 8; " +
                                "-fx-cursor: hand;"
                ));

        itemBox.setOnMouseExited(e -> {
            if (selectedWardrobe == null || !selectedWardrobe.getId().equals(wardrobe.getId())) {
                itemBox.setStyle(
                        "-fx-background-color: #f8f8f8; " +
                                "-fx-border-color: #e0e0e0; " +
                                "-fx-border-width: 1; " +
                                "-fx-border-radius: 8; " +
                                "-fx-background-radius: 8; " +
                                "-fx-cursor: hand;"
                );
            }
        });

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

    private void selectClothes(Wardrobe wardrobe, VBox itemBox) {
        // 이전 선택 해제
        if (selectedWardrobe != null) {
            // 모든 아이템의 스타일을 기본으로 리셋
            clothesContainer.getChildren().forEach(node -> {
                if (node instanceof VBox) {
                    node.setStyle(
                            "-fx-background-color: #f8f8f8; " +
                                    "-fx-border-color: #e0e0e0; " +
                                    "-fx-border-width: 1; " +
                                    "-fx-border-radius: 8; " +
                                    "-fx-background-radius: 8; " +
                                    "-fx-cursor: hand;"
                    );
                }
            });
        }

        // 새 선택 적용
        selectedWardrobe = wardrobe;
        itemBox.setStyle(
                "-fx-background-color: #e8f4ff; " +
                        "-fx-border-color: #007acc; " +
                        "-fx-border-width: 3; " +
                        "-fx-border-radius: 8; " +
                        "-fx-background-radius: 8; " +
                        "-fx-cursor: hand;"
        );

        // 확인 버튼 활성화
        if (confirmButton != null) {
            confirmButton.setDisable(false);
        }

        System.out.println("선택된 옷: " + wardrobe.getName());
    }

    private void filterClothes(String searchText) {
        if (allClothes == null) return;

        if (searchText == null || searchText.trim().isEmpty()) {
            filteredClothes = allClothes;
        } else {
            String search = searchText.toLowerCase().trim();
            filteredClothes = allClothes.stream()
                    .filter(w -> w.getName() != null && w.getName().toLowerCase().contains(search))
                    .collect(Collectors.toList());
        }

        displayClothes();
        updateCountLabel();
    }

    private void updateCountLabel() {
        if (countLabel != null && filteredClothes != null) {
            countLabel.setText(filteredClothes.size() + "개의 옷");
        }
    }

    @FXML
    private void handleConfirm() {
        if (selectedWardrobe != null && callback != null) {
            callback.onClothesSelected(selectedWardrobe);
            closeModal();
        }
    }

    @FXML
    private void handleCancel() {
        closeModal();
    }

    private void closeModal() {
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("오류");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
