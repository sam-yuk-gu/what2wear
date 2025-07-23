package com.samyukgu.what2wear.wardrobe.controller;

import com.samyukgu.what2wear.common.controller.MainLayoutController;
import com.samyukgu.what2wear.di.DIContainer;
import com.samyukgu.what2wear.member.Session.MemberSession;
import com.samyukgu.what2wear.member.model.Member;
import com.samyukgu.what2wear.wardrobe.model.Category;
import com.samyukgu.what2wear.wardrobe.model.Wardrobe;
import com.samyukgu.what2wear.wardrobe.service.CategoryService;
import com.samyukgu.what2wear.wardrobe.service.WardrobeService;

import javafx.collections.FXCollections;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;

import java.io.File;
import java.net.URL;
import java.nio.file.Files;
import java.util.List;
import java.util.ResourceBundle;

public class CreateWardrobeController implements Initializable {

    @FXML private TextField nameField;
    @FXML private ComboBox<Category> categoryField;
    @FXML private ComboBox<String> keywordField;
    @FXML private ComboBox<String> colorField;
    @FXML private TextField sizeField;
    @FXML private TextField brandField;
    @FXML private TextField memoField;
    @FXML private ImageView pictureView;
    @FXML private Button uploadButton;
    @FXML private Button saveButton;

    private byte[] pictureData;

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
        loadCategories();
    }

    private void setupUI() {
        // 저장 버튼 연결
        saveButton.setOnAction(this::handleSave);
        // 업로드 버튼 연결
        uploadButton.setOnAction(this::handleUploadImage);
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
        // 폼 검증 추가
        setupFormValidation();
    }

    private void setupFormValidation() {
        // 실시간 검증을 위한 리스너 추가
        nameField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null && newValue.length() > 50) {
                nameField.setText(oldValue); // 50자 제한
            }
        });
        sizeField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null && newValue.length() > 10) {
                sizeField.setText(oldValue); // 10자 제한
            }
        });
        brandField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null && newValue.length() > 30) {
                brandField.setText(oldValue); // 30자 제한
            }
        });
        memoField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null && newValue.length() > 200) {
                memoField.setText(oldValue); // 200자 제한
            }
        });
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
            }

            @Override
            protected void failed() {
                showError("카테고리 정보를 불러오는데 실패했습니다.");
            }
        };

        new Thread(loadTask).start();
    }

    private void handleSave(ActionEvent event) {
        if (!validateForm()) {
            return;
        }

        // 저장 버튼 비활성화 (중복 클릭 방지)
        saveButton.setDisable(true);

        Task<Void> saveTask = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                Wardrobe wardrobe = createWardrobeFromForm();
                wardrobeService.addWardrobe(wardrobe);
                return null;
            }
            // 옷 저장시 옷장 전체 조회 페이지로 이동
            @Override
            protected void succeeded() {
                showAlert("옷이 저장되었습니다.");
                MainLayoutController.loadView("/com/samyukgu/what2wear/wardrobe/wardrobeList.fxml");
            }

            @Override
            protected void failed() {
                saveButton.setDisable(false); // 버튼 재활성화
                showError("저장 중 오류가 발생했습니다: " + getException().getMessage());
            }
        };

        new Thread(saveTask).start();
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

    private Wardrobe createWardrobeFromForm() {
        Wardrobe wardrobe = new Wardrobe();

        Category selectedCategory = categoryField.getValue();
        wardrobe.setMemberId(memberSession.getMember().getId());
        wardrobe.setCategoryId(selectedCategory.getId());
        wardrobe.setName(nameField.getText().trim());
        wardrobe.setLike("N"); // 기본값은 즐겨찾기 없음
        wardrobe.setKeyword(keywordField.getValue());
        wardrobe.setColor(colorField.getValue());
        wardrobe.setSize(sizeField.getText().trim());
        wardrobe.setBrand(brandField.getText().trim());
        wardrobe.setMemo(memoField.getText() != null ? memoField.getText().trim() : "");
        wardrobe.setDeleted("N"); // 옷 삭제시 "Y"로 변경
        wardrobe.setPicture(pictureData);

        return wardrobe;
    }

    private void handleUploadImage(ActionEvent event) {
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
                        showAlert("이미지가 성공적으로 업로드되었습니다.");
                    } else {
                        throw new Exception("이미지 파일이 손상되었거나 지원하지 않는 형식입니다.");
                    }
                } catch (Exception e) {
                    showError("이미지 미리보기 실패: " + e.getMessage());
                }
            }

            @Override
            protected void failed() {
                pictureData = null;
                showError("이미지 업로드 실패: " + getException().getMessage());
            }
        };

        new Thread(imageTask).start();
    }

    // 뒤로가기 버튼
    @FXML
    private void handleBackClick() {
        // 변경사항이 있다면 확인 대화상자 표시
        if (hasUnsavedChanges()) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION,
                    "저장하지 않은 변경사항이 있습니다. 정말로 나가시겠습니까?",
                    ButtonType.YES, ButtonType.NO);
            alert.setTitle("확인");
            alert.setHeaderText(null);

            alert.showAndWait().ifPresent(result -> {
                if (result == ButtonType.YES) {
                    MainLayoutController.loadView("/com/samyukgu/what2wear/wardrobe/wardrobeList.fxml");
                }
            });
        } else {
            MainLayoutController.loadView("/com/samyukgu/what2wear/wardrobe/wardrobeList.fxml");
        }
    }

    private boolean hasUnsavedChanges() {
        return (nameField.getText() != null && !nameField.getText().trim().isEmpty()) ||
                categoryField.getValue() != null ||
                (brandField.getText() != null && !brandField.getText().trim().isEmpty()) ||
                (sizeField.getText() != null && !sizeField.getText().trim().isEmpty()) ||
                colorField.getValue() != null ||
                keywordField.getValue() != null ||
                (memoField.getText() != null && !memoField.getText().trim().isEmpty()) ||
                pictureData != null;
    }

    // 폼 초기화
    @FXML
    private void handleReset() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION,
                "모든 입력 내용이 삭제됩니다. 계속하시겠습니까?",
                ButtonType.YES, ButtonType.NO);
        alert.setTitle("확인");
        alert.setHeaderText(null);

        alert.showAndWait().ifPresent(result -> {
            if (result == ButtonType.YES) {
                resetForm();
            }
        });
    }

    private void resetForm() {
        nameField.clear();
        categoryField.setValue(null);
        brandField.clear();
        sizeField.clear();
        colorField.setValue(null);
        keywordField.setValue(null);
        memoField.clear();
        pictureData = null;

        // 기본 이미지로 리셋
        try {
            Image defaultImage = new Image(getClass().getResourceAsStream("/images/firstImg.png"));
            pictureView.setImage(defaultImage);
        } catch (Exception e) {
            pictureView.setImage(null);
        }
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