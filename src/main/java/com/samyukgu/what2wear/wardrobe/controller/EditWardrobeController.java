package com.samyukgu.what2wear.wardrobe.controller;

import com.samyukgu.what2wear.di.DIContainer;
import com.samyukgu.what2wear.layout.controller.MainLayoutController;
import com.samyukgu.what2wear.member.Session.MemberSession;
import com.samyukgu.what2wear.member.model.Member;
import com.samyukgu.what2wear.wardrobe.model.Category;
import com.samyukgu.what2wear.wardrobe.model.Wardrobe;
import com.samyukgu.what2wear.wardrobe.service.CategoryService;
import com.samyukgu.what2wear.wardrobe.service.WardrobeService;
import com.samyukgu.what2wear.common.controller.CustomModalController;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.stage.FileChooser;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.net.URL;
import java.nio.file.Files;
import java.util.List;
import java.util.ResourceBundle;

public class EditWardrobeController implements Initializable {

    @FXML private TextField nameField;
    @FXML private ComboBox<Category> categoryField;
    @FXML private ComboBox<String> keywordField;
    @FXML private ComboBox<String> colorField;
    @FXML private TextField sizeField;
    @FXML private TextField brandField;
    @FXML private TextField memoField;
    @FXML private ImageView pictureView;
    @FXML private Button uploadButton;
    @FXML private Button updateButton;

    // 추가: rootPane 필드
    @FXML private StackPane rootPane;

    private byte[] pictureData;
    private Wardrobe currentWardrobe;

    private WardrobeService wardrobeService;
    private CategoryService categoryService;
    private MemberSession memberSession;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        DIContainer diContainer = DIContainer.getInstance();
        wardrobeService = diContainer.resolve(WardrobeService.class);
        categoryService = diContainer.resolve(CategoryService.class);
        memberSession = diContainer.resolve(MemberSession.class);
        setupUI();
        loadCurrentWardrobeData();
        loadCategories();
    }

    private void setupUI() {
        // 업로드 버튼 연결
        uploadButton.setOnAction(e -> handleUploadImage());
        // 키워드 콤보박스 설정
        keywordField.setItems(FXCollections.observableArrayList(
                "캐주얼", "스트릿", "미니멀", "걸리시", "스포티", "클래식",
                "워크웨어", "로맨틱", "시크", "시티보이", "고프코어", "레트로"
        ));
        // 색상 콤보박스 설정
        colorField.setItems(FXCollections.observableArrayList(
                "화이트", "블랙", "그레이", "레드", "핑크", "오렌지", "엘로우",
                "그린", "블루", "퍼플", "브라운"
        ));
        // 폼 검증 설정
        setupFormValidation();
    }

    private void setupFormValidation() {
        // 실시간 검증을 위한 리스너 추가
        nameField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null && newValue.length() > 50) {
                nameField.setText(oldValue);
            }
        });

        sizeField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null && newValue.length() > 10) {
                sizeField.setText(oldValue);
            }
        });

        brandField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null && newValue.length() > 30) {
                brandField.setText(oldValue);
            }
        });

        memoField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null && newValue.length() > 200) {
                memoField.setText(oldValue);
            }
        });
    }

    private void loadCurrentWardrobeData() {
        // DetailWardrobeController에서 전달된 데이터 가져옴
        currentWardrobe = WardrobeEditData.getSelectedWardrobe();

        if (currentWardrobe != null) {
            populateForm(currentWardrobe);
        } else {
            showError("수정할 옷 정보를 불러올 수 없습니다.");
            handleBackClick();
        }
    }

    private void populateForm(Wardrobe wardrobe) {
        try {
            // 기본 정보 설정
            nameField.setText(wardrobe.getName() != null ? wardrobe.getName() : "");
            sizeField.setText(wardrobe.getSize() != null ? wardrobe.getSize() : "");
            brandField.setText(wardrobe.getBrand() != null ? wardrobe.getBrand() : "");
            memoField.setText(wardrobe.getMemo() != null ? wardrobe.getMemo() : "");
            // 키워드 설정
            if (wardrobe.getKeyword() != null) {
                keywordField.setValue(wardrobe.getKeyword());
            }
            // 색상 설정
            if (wardrobe.getColor() != null) {
                colorField.setValue(wardrobe.getColor());
            }
            // 이미지 설정
            setWardrobeImage(wardrobe);
            // 기존 이미지 데이터 보관
            pictureData = wardrobe.getPicture();

        } catch (Exception e) {
            showError("옷 정보를 폼에 설정하는 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    private void setWardrobeImage(Wardrobe wardrobe) {
        try {
            if (wardrobe.getPicture() != null && wardrobe.getPicture().length > 0) {
                ByteArrayInputStream bis = new ByteArrayInputStream(wardrobe.getPicture());
                Image image = new Image(bis);
                if (!image.isError()) {
                    pictureView.setImage(image);
                    return;
                }
            }
        } catch (Exception e) {
            System.err.println("이미지 로딩 실패: " + e.getMessage());
        }
        // 기본 이미지 설정
        try {
            Image defaultImage = new Image(getClass().getResourceAsStream("/images/shoes.png"));
            if (defaultImage != null && !defaultImage.isError()) {
                pictureView.setImage(defaultImage);
            }
        } catch (Exception e) {
            System.err.println("기본 이미지 로딩 실패: " + e.getMessage());
        }
    }

    private void loadCategories() {
        Task<List<Category>> loadTask = new Task<List<Category>>() {
            @Override
            protected List<Category> call() throws Exception {
                return categoryService.getAllCategories();
            }

            @Override
            protected void succeeded() {
                List<Category> categoryList = getValue();
                categoryField.setItems(FXCollections.observableArrayList(categoryList));

                // 현재 카테고리 선택
                if (currentWardrobe != null && currentWardrobe.getCategoryId() != null) {
                    for (Category category : categoryList) {
                        if (category.getId().equals(currentWardrobe.getCategoryId())) {
                            categoryField.setValue(category);
                            break;
                        }
                    }
                }
            }

            @Override
            protected void failed() {
                showError("카테고리 정보를 불러오는데 실패했습니다.");
            }
        };

        new Thread(loadTask).start();
    }

    // 수정된 handleUpdate 메서드 - CustomModal 사용
    @FXML
    private void handleUpdate() {
        if (!validateForm()) {
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/samyukgu/what2wear/common/CustomModal.fxml"));
            StackPane modal = loader.load();

            CustomModalController controller = loader.getController();
            controller.configure(
                    "옷 수정",
                    "옷 정보를 수정하시겠습니까?",
                    "/assets/icons/greenCheck.png", // 아이콘 없음
                    "#79C998", // 초록색
                    "취소",
                    "수정",
                    () -> rootPane.getChildren().remove(modal), // 취소
                    () -> {
                        rootPane.getChildren().remove(modal);
                        performUpdate(); // 실제 수정 실행
                    }
            );

            rootPane.getChildren().add(modal);

        } catch (Exception e) {
            e.printStackTrace();
            showError("모달을 불러오는 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    // 실제 수정 작업을 수행하는 메서드 (새로 추가)
    private void performUpdate() {
        // 수정 버튼 비활성화 (중복 클릭 방지)
        updateButton.setDisable(true);

        Task<Void> updateTask = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                Wardrobe updatedWardrobe = createUpdatedWardrobe();
                wardrobeService.updateWardrobe(updatedWardrobe);
                return null;
            }

            @Override
            protected void succeeded() {
                Platform.runLater(() -> {
                    showUpdateSuccessModal();
                });
            }

            @Override
            protected void failed() {
                Platform.runLater(() -> {
                    // 버튼 재활성화
                    updateButton.setDisable(false);
                    showError("수정 중 오류가 발생했습니다: " + getException().getMessage());
                });
            }
        };

        new Thread(updateTask).start();
    }

    // 수정 성공 모달 (새로 추가)
    private void showUpdateSuccessModal() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/samyukgu/what2wear/common/CustomModal.fxml"));
            StackPane modal = loader.load();

            CustomModalController controller = loader.getController();
            controller.configure(
                    "수정 완료",
                    "옷 정보가 성공적으로 수정되었습니다.",
                    "/assets/icons/greenCheck.png", // 아이콘 없음
                    "#79C998", // 초록색
                    null, // 취소 버튼 없음
                    "확인",
                    null, // 취소 액션 없음
                    () -> {
                        // 확인 버튼 클릭 시
                        rootPane.getChildren().remove(modal);

                        // 데이터 정리
                        WardrobeEditData.clearSelectedWardrobe();

                        // 상세 페이지로 돌아가기
                        MainLayoutController.loadView("/com/samyukgu/what2wear/wardrobe/wardrobeDetail.fxml");
                    }
            );

            rootPane.getChildren().add(modal);

        } catch (Exception e) {
            System.err.println("성공 모달 로딩 실패: " + e.getMessage());

            // 모달 실패 시 기본 처리
            WardrobeEditData.clearSelectedWardrobe();
            MainLayoutController.loadView("/com/samyukgu/what2wear/wardrobe/wardrobeDetail.fxml");
        }
    }

    private boolean validateForm() {
        // 필수 필드 검증
        if (nameField.getText() == null || nameField.getText().trim().isEmpty()) {
            showError("옷 이름을 입력해주세요.");
            nameField.requestFocus();
            return false;
        }
        if (categoryField.getValue() == null) {
            showError("카테고리를 선택해주세요.");
            categoryField.requestFocus();
            return false;
        }
        if (brandField.getText() == null || brandField.getText().trim().isEmpty()) {
            showError("브랜드를 입력해주세요.");
            brandField.requestFocus();
            return false;
        }
        if (sizeField.getText() == null || sizeField.getText().trim().isEmpty()) {
            showError("사이즈를 입력해주세요.");
            sizeField.requestFocus();
            return false;
        }
        if (colorField.getValue() == null) {
            showError("색상을 선택해주세요.");
            colorField.requestFocus();
            return false;
        }
        return true;
    }

    private Wardrobe createUpdatedWardrobe() {
        if (currentWardrobe == null) {
            throw new IllegalStateException("수정할 옷 정보가 없습니다.");
        }

        // 기존 옷 정보를 복사하고 수정된 내용 적용
        Wardrobe updatedWardrobe = new Wardrobe();
        updatedWardrobe.setMemberId(memberSession.getMember().getId());
        // 기본 정보는 유지
        updatedWardrobe.setId(currentWardrobe.getId());
        updatedWardrobe.setDeleted(currentWardrobe.getDeleted());
        updatedWardrobe.setLike(currentWardrobe.getLike()); // 즐겨찾기 상태 유지

        // 수정된 정보 적용
        Category selectedCategory = categoryField.getValue();
        updatedWardrobe.setCategoryId(selectedCategory.getId());
        updatedWardrobe.setName(nameField.getText().trim());
        updatedWardrobe.setKeyword(keywordField.getValue());
        updatedWardrobe.setColor(colorField.getValue());
        updatedWardrobe.setSize(sizeField.getText().trim());
        updatedWardrobe.setBrand(brandField.getText().trim());
        updatedWardrobe.setMemo(memoField.getText() != null ? memoField.getText().trim() : "");
        updatedWardrobe.setPicture(pictureData);

        return updatedWardrobe;
    }

    private void handleUploadImage() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("이미지 선택");

        // 이미지 파일 필터 추가
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("이미지 파일", "*.png", "*.jpg", "*.jpeg", "*.gif", "*.bmp"),
                new FileChooser.ExtensionFilter("PNG 파일", "*.png"),
                new FileChooser.ExtensionFilter("JPEG 파일", "*.jpg", "*.jpeg"),
                new FileChooser.ExtensionFilter("모든 파일", "*.*")
        );

        File selectedFile = fileChooser.showOpenDialog(uploadButton.getScene().getWindow());

        if (selectedFile != null) {
            processSelectedImage(selectedFile);
        }
    }

    private void processSelectedImage(File selectedFile) {
        Task<Void> imageTask = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                // 파일 크기 검증 (5MB 제한)
                long fileSizeInMB = selectedFile.length() / (1024 * 1024);
                if (fileSizeInMB > 5) {
                    throw new IllegalArgumentException("이미지 파일 크기는 5MB 이하여야 합니다.");
                }
                // 이미지 데이터 읽기
                pictureData = Files.readAllBytes(selectedFile.toPath());
                return null;
            }

            @Override
            protected void succeeded() {
                try {
                    // 이미지 미리보기 설정
                    Image image = new Image(selectedFile.toURI().toString());
                    if (!image.isError()) {
                        pictureView.setImage(image);
                    } else {
                        throw new Exception("이미지 파일이 손상되었거나 지원하지 않는 형식입니다.");
                    }
                } catch (Exception e) {
                    showError("이미지 미리보기 실패: " + e.getMessage());
                }
            }

            @Override
            protected void failed() {
                pictureData = currentWardrobe.getPicture(); // 기존 이미지로 되돌림
                showError("이미지 업로드 실패: " + getException().getMessage());
            }
        };
        new Thread(imageTask).start();
    }

    // 수정된 handleCancel 메서드 - CustomModal 사용
    @FXML
    private void handleCancel() {
        handleBackClick();
//        // 변경사항이 있다면 확인 모달 표시
//        if (hasUnsavedChanges()) {
//            try {
//                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/samyukgu/what2wear/common/CustomModal.fxml"));
//                StackPane modal = loader.load();
//
//                CustomModalController controller = loader.getController();
//                controller.configure(
//                        "변경사항 확인",
//                        "저장하지 않은 변경사항이 있습니다.",
//                        "/assets/icons/greenCheck.png", // 아이콘 없음
//                        "#dc3545", // 빨간색
//                        "계속수정",
//                        "취소하기",
//                        () -> rootPane.getChildren().remove(modal), // 계속 수정
//                        () -> {
//                            rootPane.getChildren().remove(modal);
//                            handleBackClick(); // 취소하고 뒤로가기
//                        }
//                );
//
//                rootPane.getChildren().add(modal);
//
//            } catch (Exception e) {
//                e.printStackTrace();
//                // 모달 실패 시 기본 Alert 사용
//                showCancelConfirmationAlert();
//            }
//        } else {
//            handleBackClick();
//        }
    }

    // 기존 Alert 방식 (백업용)
    private void showCancelConfirmationAlert() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION,
                "저장하지 않은 변경사항이 있습니다. 정말로 취소하시겠습니까?",
                ButtonType.YES, ButtonType.NO);
        alert.setTitle("확인");
        alert.setHeaderText(null);
        alert.showAndWait().ifPresent(result -> {
            if (result == ButtonType.YES) {
                handleBackClick();
            }
        });
    }

    @FXML
    private void handleBackClick() {
        // 데이터 정리
        WardrobeEditData.clearSelectedWardrobe();
        MainLayoutController.loadView("/com/samyukgu/what2wear/wardrobe/wardrobeDetail.fxml");
    }

    private boolean hasUnsavedChanges() {
        if (currentWardrobe == null) return false;

        // 각 필드의 변경사항 확인
        String currentName = currentWardrobe.getName() != null ? currentWardrobe.getName() : "";
        String currentBrand = currentWardrobe.getBrand() != null ? currentWardrobe.getBrand() : "";
        String currentSize = currentWardrobe.getSize() != null ? currentWardrobe.getSize() : "";
        String currentMemo = currentWardrobe.getMemo() != null ? currentWardrobe.getMemo() : "";

        boolean nameChanged = !nameField.getText().equals(currentName);
        boolean brandChanged = !brandField.getText().equals(currentBrand);
        boolean sizeChanged = !sizeField.getText().equals(currentSize);
        boolean memoChanged = !memoField.getText().equals(currentMemo);

        boolean keywordChanged = false;
        if (keywordField.getValue() != null) {
            keywordChanged = !keywordField.getValue().equals(currentWardrobe.getKeyword());
        } else if (currentWardrobe.getKeyword() != null) {
            keywordChanged = true;
        }

        boolean colorChanged = false;
        if (colorField.getValue() != null) {
            colorChanged = !colorField.getValue().equals(currentWardrobe.getColor());
        } else if (currentWardrobe.getColor() != null) {
            colorChanged = true;
        }

        boolean categoryChanged = false;
        if (categoryField.getValue() != null && currentWardrobe.getCategoryId() != null) {
            categoryChanged = !categoryField.getValue().getId().equals(currentWardrobe.getCategoryId());
        }

        boolean imageChanged = (pictureData != currentWardrobe.getPicture());

        return nameChanged || brandChanged || sizeChanged || memoChanged ||
                keywordChanged || colorChanged || categoryChanged || imageChanged;
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