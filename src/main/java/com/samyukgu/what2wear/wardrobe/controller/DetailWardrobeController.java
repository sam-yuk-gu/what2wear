package com.samyukgu.what2wear.wardrobe.controller;

import com.samyukgu.what2wear.common.controller.BasicHeaderController;
import com.samyukgu.what2wear.di.DIContainer;
import com.samyukgu.what2wear.layout.controller.MainLayoutController;
import com.samyukgu.what2wear.member.Session.MemberSession;
import com.samyukgu.what2wear.member.model.Member;
import com.samyukgu.what2wear.wardrobe.dao.WardrobeOracleDAO;
import com.samyukgu.what2wear.wardrobe.model.Wardrobe;
import com.samyukgu.what2wear.wardrobe.service.CategoryService;
import com.samyukgu.what2wear.wardrobe.service.WardrobeService;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import com.samyukgu.what2wear.common.controller.CustomModalController;


import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;

public class DetailWardrobeController implements Initializable {
    @FXML private ImageView imageView;
    @FXML private ImageView heartIcon; // 하트 아이콘
    @FXML private Label nameLabel;
    @FXML private Label categoryLabel;
    @FXML private Label sizeLabel;
    @FXML private Label brandLabel;
    @FXML private Label colorLabel;
    @FXML private Label keywordLabel;
    @FXML private Label memoLabel;
    @FXML private StackPane rootPane;
    @FXML private VBox container;

    private WardrobeService wardrobeService;
    private Wardrobe currentWardrobe;
    private MemberSession memberSession;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        DIContainer diContainer = DIContainer.getInstance();
        wardrobeService = diContainer.resolve(WardrobeService.class);
        memberSession = diContainer.resolve(MemberSession.class);

        // 하트 아이콘 클릭 이벤트 설정
        setupHeartIconClick();
        loadWardrobeDetail();
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/samyukgu/what2wear/common/BasicHeader.fxml"));
            HBox header = loader.load();

            BasicHeaderController controller = loader.getController();
            controller.setTitle("옷 상세");
            controller.setOnBackAction(() -> {
                try {
                    Parent view = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/com/samyukgu/what2wear/wardrobe/wardrobeList.fxml")));
                    rootPane.getChildren().setAll(view);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });

            container.getChildren().add(0, header); // StackPane 맨 위에 삽입
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    private void setupHeartIconClick() {
        if (heartIcon != null) {
            // ImageView의 투명한 부분도 클릭 가능하도록 설정
            heartIcon.setPickOnBounds(true);

            // 마우스 클릭 이벤트 추가
            heartIcon.setOnMouseClicked(event -> {
                System.out.println("하트 아이콘 클릭됨!");
                toggleFavorite();
            });

            // 호버 효과를 위한 CSS 스타일 적용
            heartIcon.setStyle("-fx-cursor: hand;");

            // 호버 효과
            heartIcon.setOnMouseEntered(e -> {
                heartIcon.setOpacity(0.8);
                heartIcon.setScaleX(1.1);
                heartIcon.setScaleY(1.1);
            });

            heartIcon.setOnMouseExited(e -> {
                heartIcon.setOpacity(1.0);
                // 원래 크기로 복원
                heartIcon.setScaleX(1.0);
                heartIcon.setScaleY(1.0);
            });
        } else {
            System.out.println("ERROR: heartIcon이 null입니다!");
        }
    }

    private void loadWardrobeDetail() {
        // ListWardrobeController에서 전달된 데이터 가져오기
        currentWardrobe = WardrobeDetailData.getSelectedWardrobe();
        if (currentWardrobe != null) {
            displayWardrobeInfo(currentWardrobe);
        } else {
            showError("선택된 옷 정보를 불러올 수 없습니다.");
        }
    }

    private void displayWardrobeInfo(Wardrobe wardrobe) {
        try {
            System.out.println("옷 정보 표시 시작");
            System.out.println("옷 ID: " + wardrobe.getId());
            System.out.println("옷 이름: " + wardrobe.getName());
            System.out.println("즐겨찾기 상태: " + wardrobe.getLike());

            // 이름 설정
            nameLabel.setText(wardrobe.getName() != null ? wardrobe.getName() : "이름 없음");
            // 카테고리 설정 (ID를 한글로 변환)
            categoryLabel.setText(getCategoryNameFromId(wardrobe.getCategoryId()));
            // 사이즈 설정
            sizeLabel.setText(wardrobe.getSize() != null ? wardrobe.getSize() : "-");
            // 브랜드 설정
            brandLabel.setText(wardrobe.getBrand() != null ? wardrobe.getBrand() : "-");
            // 색상 설정
            colorLabel.setText(wardrobe.getColor() != null ? wardrobe.getColor() : "-");
            // 키워드 설정
            keywordLabel.setText(wardrobe.getKeyword() != null ? wardrobe.getKeyword() : "-");
            // 메모 설정
            memoLabel.setText(wardrobe.getMemo() != null ? wardrobe.getMemo() : "메모 없음");
            // 이미지 설정
            setWardrobeImage(wardrobe);
            // 하트 아이콘 설정
            updateHeartIcon(wardrobe);

        } catch (Exception e) {
            System.out.println("ERROR: 옷 정보 표시 중 오류 - " + e.getMessage());
            e.printStackTrace();
            showError("옷 정보 표시 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    // 즐겨찾기 토글 메서드 (즉시 UI 반응)
    private void toggleFavorite() {
        System.out.println("toggleFavorite 메서드 호출됨");

        if (currentWardrobe == null) {
            System.out.println("ERROR: currentWardrobe가 null입니다");
            showError("옷 정보가 없습니다.");
            return;
        }

        if (memberSession == null || memberSession.getMember() == null) {
            System.out.println("ERROR: memberSession이 null이거나 멤버가 null입니다");
            showError("로그인이 필요합니다.");
            return;
        }

        // UI 비활성화 (중복 클릭 방지)
        heartIcon.setDisable(true);

        // 현재 즐겨찾기 상태 확인
        boolean currentFavorite = "Y".equals(currentWardrobe.getLike());
        String originalStatus = currentWardrobe.getLike();
        String newStatus = currentFavorite ? "N" : "Y";

        System.out.println("현재 즐겨찾기 상태: " + originalStatus);
        System.out.println("새로운 즐겨찾기 상태: " + newStatus);

        // 1. 먼저 UI를 즉시 업데이트 (사용자에게 즉각적인 피드백)
        currentWardrobe.setLike(newStatus);
        updateHeartIconImmediately(newStatus);

        // 2. 그 다음 백그라운드에서 DB 업데이트
        Task<Void> dbUpdateTask = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                System.out.println("DB 업데이트 시작...");
                // 서비스의 토글 메서드 사용
                wardrobeService.toggleFavoriteStatus(currentWardrobe.getId(), currentWardrobe.getMemberId());
                System.out.println("DB 업데이트 완료");
                return null;
            }

            @Override
            protected void succeeded() {
                System.out.println("즐겨찾기 DB 업데이트 성공");

                // 성공 메시지 표시
                String message = "Y".equals(newStatus) ? "즐겨찾기에 추가되었습니다." : "즐겨찾기에서 해제되었습니다.";
                showSuccessToast(message);

                // UI 재활성화
                Platform.runLater(() -> heartIcon.setDisable(false));
            }

            @Override
            protected void failed() {
                System.out.println("ERROR: DB 업데이트 실패 - " + getException().getMessage());
                getException().printStackTrace();

                // 실패 시 UI를 원래 상태로 되돌림
                Platform.runLater(() -> {
                    currentWardrobe.setLike(originalStatus);
                    updateHeartIconImmediately(originalStatus);
                    showError("즐겨찾기 업데이트에 실패했습니다: " + getException().getMessage());
                    heartIcon.setDisable(false);
                });
            }
        };

        new Thread(dbUpdateTask).start();
    }

    // 즉시 하트 아이콘 업데이트 메서드 (JavaFX Application Thread에서 실행)
    private void updateHeartIconImmediately(String likedStatus) {
        if (heartIcon == null) {
            System.out.println("ERROR: heartIcon이 null입니다!");
            return;
        }

        // JavaFX Application Thread에서 실행되는지 확인
        if (!Platform.isFxApplicationThread()) {
            Platform.runLater(() -> updateHeartIconImmediately(likedStatus));
            return;
        }

        try {
            boolean isFavorite = "Y".equals(likedStatus);
            String iconPath = isFavorite ? "/images/likedY.png" : "/images/likedN.png";

            System.out.println("즉시 하트 아이콘 업데이트 - 즐겨찾기 상태: " + likedStatus + ", 아이콘 경로: " + iconPath);

            // 애니메이션 효과 추가 (선택사항)
            heartIcon.setOpacity(0.7);

            // 이미지 리소스 확인 및 설정
            if (getClass().getResourceAsStream(iconPath) != null) {
                Image heartImage = new Image(getClass().getResourceAsStream(iconPath));
                if (heartImage != null && !heartImage.isError()) {
                    heartIcon.setImage(heartImage);

                    // 애니메이션 효과 복원
                    heartIcon.setOpacity(1.0);

                    // 즐겨찾기 추가시 살짝 확대 효과
                    if (isFavorite) {
                        heartIcon.setScaleX(1.2);
                        heartIcon.setScaleY(1.2);
                        // 0.3초 후 원래 크기로
                        Timeline timeline = new Timeline(
                                new KeyFrame(
                                        Duration.millis(300),
                                        new KeyValue(heartIcon.scaleXProperty(), 1.0),
                                        new KeyValue(heartIcon.scaleYProperty(), 1.0)
                                )
                        );
                        timeline.play();
                    }

                    System.out.println("즉시 하트 아이콘 업데이트 성공: " + iconPath);
                    return;
                }
            }

            // 대체 이미지 경로들 시도
            String[] fallbackPaths = {
                    "/images/heart.png",
                    "/wardrobe/images/" + (isFavorite ? "likedY.png" : "likedN.png"),
                    "/com/samyukgu/what2wear/wardrobe/images/" + (isFavorite ? "likedY.png" : "likedN.png")
            };

            for (String fallbackPath : fallbackPaths) {
                try {
                    if (getClass().getResourceAsStream(fallbackPath) != null) {
                        Image fallbackImage = new Image(getClass().getResourceAsStream(fallbackPath));
                        if (fallbackImage != null && !fallbackImage.isError()) {
                            heartIcon.setImage(fallbackImage);
                            heartIcon.setOpacity(1.0);

                            // heart.png를 사용하는 경우 즐겨찾기 상태에 따라 스타일 적용
                            if (fallbackPath.contains("heart.png")) {
                                if (isFavorite) {
                                    heartIcon.setStyle("-fx-cursor: hand; -fx-effect: dropshadow(gaussian, red, 8, 0.8, 0, 0);");
                                    heartIcon.setOpacity(1.0);
                                } else {
                                    heartIcon.setStyle("-fx-cursor: hand;");
                                    heartIcon.setOpacity(0.6);
                                }
                            }

                            System.out.println("대체 하트 아이콘 사용: " + fallbackPath);
                            return;
                        }
                    }
                } catch (Exception e) {
                    System.out.println("대체 이미지 로딩 실패: " + fallbackPath);
                }
            }

            // 모든 이미지 로딩 실패 시 스타일로만 구분
            System.out.println("WARNING: 모든 하트 이미지 로딩 실패, 스타일로 구분");
            heartIcon.setOpacity(isFavorite ? 1.0 : 0.5);
            if (isFavorite) {
                heartIcon.setStyle("-fx-cursor: hand; -fx-effect: dropshadow(gaussian, red, 5, 0.7, 0, 0);");
            } else {
                heartIcon.setStyle("-fx-cursor: hand;");
            }

        } catch (Exception e) {
            System.err.println("즉시 하트 아이콘 업데이트 실패: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // 기존 하트 아이콘 업데이트 메서드 (호환성 유지)
    private void updateHeartIcon(Wardrobe wardrobe) {
        updateHeartIconImmediately(wardrobe.getLike());
    }

    // 성공 토스트 메시지 (팝업 대신 간단한 알림)
    private void showSuccessToast(String message) {
        // 간단한 토스트 대신 콘솔 로그와 상태 표시
        System.out.println("SUCCESS: " + message);

        // 선택사항: 1초 후에 사라지는 작은 알림 표시
        Platform.runLater(() -> {
            // 임시로 nameLabel의 스타일을 변경해서 피드백 제공
            String originalStyle = nameLabel.getStyle();
            nameLabel.setStyle(originalStyle + " -fx-text-fill: green;");

            // 1초 후 원래 스타일로 복원
            Timeline timeline = new Timeline(
                    new KeyFrame(
                            Duration.seconds(1),
                            e -> nameLabel.setStyle(originalStyle)
                    )
            );
            timeline.play();
        });
    }

    // 카테고리 변환 하는 코드
    private String getCategoryNameFromId(Long categoryId) {
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

    private void setWardrobeImage(Wardrobe wardrobe) {
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
            Image defaultImage = new Image(getClass().getResourceAsStream("/images/sample-shirt.png"));
            if (defaultImage != null && !defaultImage.isError()) {
                imageView.setImage(defaultImage);
            }
        } catch (Exception e) {
            System.err.println("기본 이미지 로딩 실패: " + e.getMessage());
        }
    }

    // 수정 버튼
    @FXML
    private void handleEdit() {
        try {
            WardrobeEditData.setSelectedWardrobe(currentWardrobe);
            MainLayoutController.loadView("/com/samyukgu/what2wear/wardrobe/wardrobeModify.fxml");
        } catch (Exception e) {
            showError("수정 페이지로 이동하는데 실패했습니다: " + e.getMessage());
        }
    }

    // 삭제 버튼
// 수정된 삭제 버튼 메서드
    @FXML
    private void handleDelete() {
        try {
            if (currentWardrobe == null) {
                showError("삭제할 옷 정보가 없습니다.");
                return;
            }
            if (memberSession == null) {
                showError("로그인이 필요합니다.");
                return;
            }

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/samyukgu/what2wear/common/CustomModal.fxml"));
            StackPane modal = loader.load();

            CustomModalController controller = loader.getController();
            controller.configure(
                    "삭제 확인", // 제목 수정
                    "정말로 옷을 삭제하시겠습니까?",
                    "/assets/icons/redCheck.png", // 아이콘 없음
                    "#FA7B7F", // 빨간색으로 변경
                    "취소",
                    "삭제", // 버튼 텍스트 수정
                    () -> rootPane.getChildren().remove(modal), // rootPane 사용
                    () -> {
                        rootPane.getChildren().remove(modal); // rootPane 사용
                        performDeleteWardrobe(); // 별도 메서드로 분리
                    }
            );

            rootPane.getChildren().add(modal); // rootPane 사용

        } catch (Exception e) {
            e.printStackTrace();
            showError("모달을 불러오는 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    // 실제 삭제 작업을 수행하는 메서드 (새로 추가)
    private void performDeleteWardrobe() {
        try {
            // 백그라운드에서 삭제 작업 수행
            Task<Void> deleteTask = new Task<Void>() {
                @Override
                protected Void call() throws Exception {
                    wardrobeService.deleteWardrobe(currentWardrobe.getId(), currentWardrobe.getMemberId());
                    return null;
                }

                @Override
                protected void succeeded() {
                    Platform.runLater(() -> {
                        showDeleteSuccessModal();
                    });
                }

                @Override
                protected void failed() {
                    Platform.runLater(() -> {
                        showError("삭제 중 오류가 발생했습니다: " + getException().getMessage());
                    });
                }
            };

            new Thread(deleteTask).start();

        } catch (Exception e) {
            showError("삭제 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    // 삭제 성공 모달 (새로 추가)
    private void showDeleteSuccessModal() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/samyukgu/what2wear/common/CustomModal.fxml"));
            StackPane modal = loader.load();

            CustomModalController controller = loader.getController();
            controller.configure(
                    "삭제 완료",
                    "옷이 성공적으로 삭제되었습니다.",
                    "/assets/icons/greenCheck.png", // 아이콘 없음
                    "#79C998", // 초록색
                    null, // 취소 버튼 없음
                    "확인",
                    null, // 취소 액션 없음
                    () -> {
                        // 확인 버튼 클릭 시
                        rootPane.getChildren().remove(modal);

                        // 데이터 정리
                        WardrobeDetailData.clearSelectedWardrobe();

                        // 목록 페이지로 돌아가기
                        MainLayoutController.loadView("/com/samyukgu/what2wear/wardrobe/wardrobeList.fxml");
                    }
            );

            rootPane.getChildren().add(modal);

        } catch (Exception e) {
            System.err.println("성공 모달 로딩 실패: " + e.getMessage());

            // 모달 실패 시 기본 Alert 사용
            showAlert("삭제되었습니다.");
            WardrobeDetailData.clearSelectedWardrobe();
            MainLayoutController.loadView("/com/samyukgu/what2wear/wardrobe/wardrobeList.fxml");
        }
    }

    @FXML
    private void handleBackClick() {
        // 데이터 정리
        WardrobeDetailData.clearSelectedWardrobe();
        MainLayoutController.loadView("/com/samyukgu/what2wear/wardrobe/wardrobeList.fxml");
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("오류");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}