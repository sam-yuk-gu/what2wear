package com.samyukgu.what2wear.myCodi.controller;

import com.samyukgu.what2wear.common.controller.BasicHeaderController;
import com.samyukgu.what2wear.layout.controller.MainLayoutController;
import com.samyukgu.what2wear.di.DIContainer;
import com.samyukgu.what2wear.member.Session.MemberSession;
import com.samyukgu.what2wear.myCodi.model.CodiWithDetails;
import com.samyukgu.what2wear.myCodi.service.CodiService;
import com.samyukgu.what2wear.wardrobe.model.Wardrobe;
import com.samyukgu.what2wear.common.controller.CustomModalController;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;

public class DetailMyCodiController implements Initializable {

    @FXML private TextField codiNameField;
    @FXML private FlowPane codiFlowPane; // GridPane 대신 FlowPane 사용
    @FXML private VBox detailVBox; // 오른쪽 상세 정보 컨테이너

    // 통계 정보 Label들
    @FXML private Label totalItemsLabel;
    @FXML private Label createdDateLabel;

    // 추가: rootPane 필드
    @FXML private StackPane rootPane;
    @FXML private VBox mainContainer;

    private CodiService codiService;
    private MemberSession memberSession;

    private CodiWithDetails currentCodi;

    // 카테고리 이름 매핑
    private final Map<Long, String> categoryNames = Map.of(
            1L, "상의",
            2L, "바지",
            3L, "원피스/스커트",
            4L, "가방",
            5L, "아우터",
            6L, "신발",
            7L, "악세사리",
            8L, "기타"
    );

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        DIContainer diContainer = DIContainer.getInstance();
        memberSession = diContainer.resolve(MemberSession.class);
        codiService = diContainer.resolve(CodiService.class);
        System.out.println("DetailMyCodiController 초기화 시작");

        // FlowPane 설정
        if (codiFlowPane != null) {
            codiFlowPane.setHgap(15); // 수평 간격
            codiFlowPane.setVgap(15); // 수직 간격
            codiFlowPane.setAlignment(Pos.TOP_LEFT);
            codiFlowPane.setPrefWrapLength(650); // 줄바꿈 기준 너비
        }

        loadCodiDetail();

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/samyukgu/what2wear/common/BasicHeader.fxml"));
            HBox header = loader.load();

            BasicHeaderController controller = loader.getController();
            controller.setTitle("나만의 코디 상세");
            controller.setOnBackAction(() -> {
                try {
                    Parent view = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/com/samyukgu/what2wear/myCodi/myCodiList.fxml")));
                    rootPane.getChildren().setAll(view);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });

            mainContainer.getChildren().add(0, header); // StackPane 맨 위에 삽입
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadCodiDetail() {
        currentCodi = MyCodiDetailData.getSelectedCodi();
        if (currentCodi != null) {
            System.out.println("선택된 코디: " + currentCodi.getName() + " (ID: " + currentCodi.getId() + ")");
            displayCodiInfo(currentCodi);
        } else {
            System.err.println("선택된 코디 정보가 없습니다.");
            showError("선택된 코디 정보를 불러올 수 없습니다.");
            handleBackClick();
        }
    }

    private void displayCodiInfo(CodiWithDetails codi) {
        try {
            // 코디 이름 설정
            codiNameField.setText(codi.getName() != null ? codi.getName() : "이름 없음");
            codiNameField.setEditable(false);

            // 기존 내용 초기화
            if (codiFlowPane != null) {
                codiFlowPane.getChildren().clear();
            }
            if (detailVBox != null) {
                detailVBox.getChildren().clear();

                // 제목 다시 추가
                Label titleLabel = new Label("선택된 아이템 상세");
                titleLabel.setStyle("-fx-font-family: Pretendard Regular; -fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #333;");
                detailVBox.getChildren().add(titleLabel);
            }

            // 코디에 포함된 옷들을 순서대로 표시
            if (codi.getClothes() != null && !codi.getClothes().isEmpty()) {
                displayClothesInFlowPane(codi);
                System.out.println("총 " + codi.getClothes().size() + "개 아이템 표시 완료");
            } else {
                System.out.println("코디에 포함된 옷이 없습니다.");
                updateStatistics(0);
            }

        } catch (Exception e) {
            System.err.println("코디 정보 표시 중 오류: " + e.getMessage());
            e.printStackTrace();
            showError("코디 정보 표시 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    private void displayClothesInFlowPane(CodiWithDetails codi) {
        if (codi.getClothes() == null || codi.getClothes().isEmpty()) {
            updateStatistics(0);
            return;
        }

        // 옷들을 카테고리별로 정렬 (카테고리 순서대로 표시)
        List<Wardrobe> sortedClothes = codi.getClothes().stream()
                .sorted((w1, w2) -> {
                    Long cat1 = w1.getCategoryId() != null ? w1.getCategoryId() : 8L;
                    Long cat2 = w2.getCategoryId() != null ? w2.getCategoryId() : 8L;
                    return cat1.compareTo(cat2);
                })
                .collect(Collectors.toList());

        System.out.println("정렬된 옷 목록:");
        sortedClothes.forEach(w -> {
            Long catId = w.getCategoryId() != null ? w.getCategoryId() : 8L;
            System.out.println("  " + categoryNames.getOrDefault(catId, "기타") + ": " + w.getName());
        });

        // FlowPane에 옷 아이템들 추가
        for (Wardrobe wardrobe : sortedClothes) {
            VBox itemBox = createClothesItemBox(wardrobe);
            codiFlowPane.getChildren().add(itemBox);

            // 상세 정보도 순서대로 추가
            HBox detailBox = createDetailItemBox(wardrobe);
            detailVBox.getChildren().add(detailBox);
        }

        // 통계 정보 업데이트
        updateStatistics(codi.getClothes().size());
    }

    private VBox createClothesItemBox(Wardrobe wardrobe) {
        VBox itemBox = new VBox();
        itemBox.setAlignment(Pos.CENTER);
        itemBox.setSpacing(6);
        itemBox.setPrefSize(200, 130); // 크기 고정
        itemBox.setMaxSize(200, 130);
        itemBox.setStyle(
                "-fx-background-color: white; " +
                        "-fx-background-radius: 8; " +
                        "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.08), 3, 0, 0, 1); " +
                        "-fx-padding: 10;"
        );

        // 옷 이미지
        ImageView imageView = new ImageView();
        imageView.setFitHeight(100);
        imageView.setFitWidth(100);
        imageView.setPreserveRatio(true);
        setClothesImage(imageView, wardrobe);

        itemBox.getChildren().addAll(imageView);

        return itemBox;
    }

    private HBox createDetailItemBox(Wardrobe wardrobe) {
        HBox detailBox = new HBox();
        detailBox.setAlignment(Pos.CENTER_LEFT);
        detailBox.setSpacing(10);
        detailBox.setStyle(
                "-fx-background-color: white; " +
                        "-fx-background-radius: 6; " +
                        "-fx-padding: 10; " +
                        "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.05), 2, 0, 0, 1); " +
                        "-fx-margin: 0 0 8 0;"
        );

        // 카테고리 이름
        Long categoryId = wardrobe.getCategoryId() != null ? wardrobe.getCategoryId() : 8L;
        String categoryName = categoryNames.getOrDefault(categoryId, "기타");

        Label categoryLabel = new Label(categoryName + ":");
        categoryLabel.setStyle("-fx-font-family: Pretendard Regular;  -fx-font-size: 12px; -fx-font-weight: bold; -fx-text-fill: #495057; -fx-min-width: 80;");

        // 상세 정보
        String detailText = buildDetailText(wardrobe);
        Label detailLabel = new Label(detailText);
        detailLabel.setStyle("-fx-font-family: Pretendard Regular;  -fx-font-size: 11px; -fx-text-fill: #333; -fx-wrap-text: true;");
        HBox.setHgrow(detailLabel, javafx.scene.layout.Priority.ALWAYS);

        detailBox.getChildren().addAll(categoryLabel, detailLabel);

        return detailBox;
    }

    private String buildDetailText(Wardrobe wardrobe) {
        StringBuilder detail = new StringBuilder();

        // 이름만 기본으로 표시
        detail.append(wardrobe.getName() != null ? wardrobe.getName() : "이름 없음");

        // 색상이 있으면 괄호 안에 추가
        if (wardrobe.getColor() != null && !wardrobe.getColor().trim().isEmpty()) {
            detail.append(" (").append(wardrobe.getColor()).append(")");
        }

        // 사이즈가 있으면 하이픈 뒤에 추가
        if (wardrobe.getSize() != null && !wardrobe.getSize().trim().isEmpty()) {
            detail.append(" - ").append(wardrobe.getSize());
        }

        return detail.toString();
    }

    private void updateStatistics(int totalItems) {
        if (totalItemsLabel != null) {
            totalItemsLabel.setText(totalItems + "개");
        }

        if (createdDateLabel != null && currentCodi != null) {
            // 등록일 표시 (현재는 ID 기반으로 대략적인 정보 표시)
            createdDateLabel.setText("코디 ID: " + currentCodi.getId());
        }
    }

    private void setClothesImage(ImageView imageView, Wardrobe wardrobe) {
        try {
            // 먼저 데이터베이스의 이미지를 시도
            if (wardrobe.getPicture() != null && wardrobe.getPicture().length > 0) {
                ByteArrayInputStream bis = new ByteArrayInputStream(wardrobe.getPicture());
                Image image = new Image(bis);
                if (!image.isError()) {
                    imageView.setImage(image);
                    System.out.println("옷 이미지 로딩 성공: " + wardrobe.getName());
                    return;
                }
            }
        } catch (Exception e) {
            System.err.println("옷 이미지 로딩 실패: " + wardrobe.getName() + " - " + e.getMessage());
        }

        // 기본 이미지 설정
        try {
            Image defaultImage = new Image(getClass().getResourceAsStream("/images/firstImg.png"));
            if (defaultImage != null && !defaultImage.isError()) {
                imageView.setImage(defaultImage);
                System.out.println("기본 이미지로 설정: " + wardrobe.getName());
            }
        } catch (Exception e) {
            System.err.println("기본 이미지 로딩 실패: " + e.getMessage());
        }
    }

    @FXML
    private void handleEdit() {
        System.out.println("수정 버튼 클릭됨");
        try {
            if (currentCodi == null) {
                showError("수정할 코디 정보가 없습니다.");
                return;
            }

            MyCodiEditData.setSelectedCodi(currentCodi);
            MainLayoutController.loadView("/com/samyukgu/what2wear/myCodi/myCodiModify.fxml");
        } catch (Exception e) {
            System.err.println("수정 페이지 이동 실패: " + e.getMessage());
            e.printStackTrace();
            showError("수정 페이지로 이동하는데 실패했습니다: " + e.getMessage());
        }
    }

    // 수정된 handleDelete 메서드 - CustomModal 사용
    @FXML
    private void handleDelete() {
        System.out.println("삭제 버튼 클릭됨");

        if (currentCodi == null) {
            showError("삭제할 코디 정보가 없습니다.");
            return;
        }

        if (memberSession == null) {
            showError("로그인이 필요합니다.");
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/samyukgu/what2wear/common/CustomModal.fxml"));
            StackPane modal = loader.load();

            CustomModalController controller = loader.getController();
            controller.configure(
                    "삭제 확인",
                    "코디를 정말 삭제하시겠습니까?",
                    "/assets/icons/redCheck.png", // 아이콘 없음
                    "#FA7B7F", // 빨간색
                    "취소",
                    "삭제",
                    () -> rootPane.getChildren().remove(modal), // 취소
                    () -> {
                        rootPane.getChildren().remove(modal);
                        performDeleteCodi(); // 실제 삭제 실행
                    }
            );

            rootPane.getChildren().add(modal);

        } catch (Exception e) {
            e.printStackTrace();
            showError("모달을 불러오는 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    // 실제 삭제 작업을 수행하는 메서드 (새로 추가)
    private void performDeleteCodi() {
        try {
            // 백그라운드에서 삭제 작업 수행
            Task<Void> deleteTask = new Task<Void>() {
                @Override
                protected Void call() throws Exception {
                    System.out.println("코디 삭제 시작: " + currentCodi.getId());
                    codiService.deleteCodi(currentCodi.getId(), currentCodi.getMemberId());
                    System.out.println("코디 삭제 완료");
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
                        System.err.println("코디 삭제 실패: " + getException().getMessage());
                        getException().printStackTrace();
                        showError("삭제 중 오류가 발생했습니다: " + getException().getMessage());
                    });
                }
            };

            new Thread(deleteTask).start();

        } catch (Exception e) {
            System.err.println("코디 삭제 실패: " + e.getMessage());
            e.printStackTrace();
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
                    "코디가 성공적으로 삭제되었습니다.",
                    "/assets/icons/greenCheck.png", // 아이콘 없음
                    "#4CAF50", // 초록색
                    null, // 취소 버튼 없음
                    "확인",
                    null, // 취소 액션 없음
                    () -> {
                        // 확인 버튼 클릭 시
                        rootPane.getChildren().remove(modal);

                        // 데이터 정리
                        MyCodiDetailData.clearSelectedCodi();

                        // 목록 페이지로 돌아가기
                        MainLayoutController.loadView("/com/samyukgu/what2wear/myCodi/myCodiList.fxml");
                    }
            );

            rootPane.getChildren().add(modal);

        } catch (Exception e) {
            System.err.println("성공 모달 로딩 실패: " + e.getMessage());

            // 모달 실패 시 기본 처리
            showAlert("코디가 성공적으로 삭제되었습니다.");
            MyCodiDetailData.clearSelectedCodi();
            MainLayoutController.loadView("/com/samyukgu/what2wear/myCodi/myCodiList.fxml");
        }
    }

    // 기존 Alert 방식 (백업용)
    private void showDeleteConfirmationAlert() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION,
                "'" + currentCodi.getName() + "' 코디를 정말 삭제하시겠습니까?\n삭제된 코디는 복구할 수 없습니다.",
                ButtonType.YES, ButtonType.NO);
        alert.setTitle("삭제 확인");
        alert.setHeaderText(null);

        alert.showAndWait().ifPresent(result -> {
            if (result == ButtonType.YES) {
                performDeleteCodi();
            }
        });
    }

    @FXML
    private void handleBackClick() {
        System.out.println("뒤로가기 버튼 클릭됨");
        MyCodiDetailData.clearSelectedCodi();
        MainLayoutController.loadView("/com/samyukgu/what2wear/myCodi/myCodiList.fxml");
    }

    private void showAlert(String message) {
        System.out.println("알림: " + message);
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("알림");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showError(String message) {
        System.err.println("오류: " + message);
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("오류");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}