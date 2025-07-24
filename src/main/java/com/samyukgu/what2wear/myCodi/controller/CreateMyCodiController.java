package com.samyukgu.what2wear.myCodi.controller;

<<<<<<< HEAD
import com.samyukgu.what2wear.layout.controller.MainLayoutController;
=======
>>>>>>> dev
import com.samyukgu.what2wear.di.DIContainer;
import com.samyukgu.what2wear.layout.controller.MainLayoutController;
import com.samyukgu.what2wear.member.Session.MemberSession;
import com.samyukgu.what2wear.member.model.Member;
import com.samyukgu.what2wear.member.service.MemberService;
import com.samyukgu.what2wear.myCodi.model.Codi;
import com.samyukgu.what2wear.myCodi.service.CodiService;
import com.samyukgu.what2wear.wardrobe.model.Wardrobe;
import com.samyukgu.what2wear.wardrobe.service.WardrobeService;

import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;

import java.io.ByteArrayInputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.stream.Stream;

public class CreateMyCodiController implements Initializable {

    // FXML 기본 필드들
    @FXML private TextField codiNameField;
    @FXML private TextField topField;
    @FXML private TextField bottomField;
    @FXML private TextField shoesField;
    @FXML private TextField bagField;
    @FXML private TextField dressField;
    @FXML private TextField outerField;
    @FXML private TextField accessoryField;
    @FXML private TextField etcField;

    // 상태 표시 라벨들
    @FXML private Label selectedCountLabel;
    @FXML private Label topStatusLabel;
    @FXML private Label bottomStatusLabel;
    @FXML private Label shoesStatusLabel;
    @FXML private Label bagStatusLabel;
    @FXML private Label dressStatusLabel;
    @FXML private Label outerStatusLabel;
    @FXML private Label accessoryStatusLabel;
    @FXML private Label etcStatusLabel;

    // AnchorPane들
    @FXML private AnchorPane topPane;
    @FXML private AnchorPane bottomPane;
    @FXML private AnchorPane shoesPane;
    @FXML private AnchorPane bagPane;
    @FXML private AnchorPane dressPane;
    @FXML private AnchorPane outerPane;
    @FXML private AnchorPane accessoryPane;
    @FXML private AnchorPane etcPane;

    // 이미지뷰들
    @FXML private ImageView topImageView;
    @FXML private ImageView bottomImageView;
    @FXML private ImageView shoesImageView;
    @FXML private ImageView bagImageView;
    @FXML private ImageView dressImageView;
    @FXML private ImageView outerImageView;
    @FXML private ImageView accessoryImageView;
    @FXML private ImageView etcImageView;

    // 원과 플러스 아이콘들
    @FXML private Circle topCircle;
    @FXML private Circle bottomCircle;
    @FXML private Circle shoesCircle;
    @FXML private Circle bagCircle;
    @FXML private Circle dressCircle;
    @FXML private Circle outerCircle;
    @FXML private Circle accessoryCircle;
    @FXML private Circle etcCircle;

    @FXML private ImageView topPlusIcon;
    @FXML private ImageView bottomPlusIcon;
    @FXML private ImageView shoesPlusIcon;
    @FXML private ImageView bagPlusIcon;
    @FXML private ImageView dressPlusIcon;
    @FXML private ImageView outerPlusIcon;
    @FXML private ImageView accessoryPlusIcon;
    @FXML private ImageView etcPlusIcon;

    // 버튼들
    @FXML private Button resetButton;
    @FXML private Button saveButton;

    // 서비스 객체들
    private CodiService codiService;
    private MemberSession memberSession;

    // 선택된 옷 정보들
    private Wardrobe selectedTop, selectedBottom, selectedShoes, selectedBag;
    private Wardrobe selectedDress, selectedOuter, selectedAccessory, selectedEtc;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        DIContainer diContainer = DIContainer.getInstance();
        memberSession = diContainer.resolve(MemberSession.class);
        codiService = diContainer.resolve(CodiService.class);
        System.out.println("CreateMyCodiController 초기화 시작");
        try {
            setupUI();
            setupFormValidation();
            updateSelectedCount();
            System.out.println("CreateMyCodiController 초기화 완료");
        } catch (Exception e) {
            System.err.println("초기화 중 오류: " + e.getMessage());
            e.printStackTrace();
            showError("초기화 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    private void setupUI() {
        System.out.println("UI 설정 시작");

        // 코디 이름 필드 설정
        if (codiNameField != null) {
            codiNameField.setPromptText("코디 이름을 입력하세요 (최대 255자)");
        }

        // 초기 카운터 설정
        updateSelectedCount();
        System.out.println("UI 설정 완료");
    }

    private void setupFormValidation() {
        System.out.println("폼 검증 설정 시작");

        // 코디 이름 길이 제한 (255자)
        if (codiNameField != null) {
            codiNameField.textProperty().addListener((observable, oldValue, newValue) -> {
                if (newValue != null && newValue.length() > 255) {
                    codiNameField.setText(oldValue);
                }
            });
        }

        System.out.println("폼 검증 설정 완료");
    }

    // 카테고리별 옷 선택 메서드들
    @FXML
    private void handleTopClick() {
        System.out.println("상의 클릭됨");
        showClothesSelectionDialog("상의", 1L, (clothes) -> {
            selectedTop = clothes;
            updateCategoryUI(CategoryType.TOP, clothes);
            updateSelectedCount();
        });
    }

    @FXML
    private void handleBottomClick() {
        System.out.println("바지 클릭됨");
        showClothesSelectionDialog("바지", 2L, (clothes) -> {
            selectedBottom = clothes;
            updateCategoryUI(CategoryType.BOTTOM, clothes);
            updateSelectedCount();
        });
    }

    @FXML
    private void handleShoesClick() {
        System.out.println("신발 클릭됨");
        showClothesSelectionDialog("신발", 6L, (clothes) -> {
            selectedShoes = clothes;
            updateCategoryUI(CategoryType.SHOES, clothes);
            updateSelectedCount();
        });
    }

    @FXML
    private void handleBagClick() {
        System.out.println("가방 클릭됨");
        showClothesSelectionDialog("가방", 4L, (clothes) -> {
            selectedBag = clothes;
            updateCategoryUI(CategoryType.BAG, clothes);
            updateSelectedCount();
        });
    }

    @FXML
    private void handleDressClick() {
        System.out.println("원피스/스커트 클릭됨");
        showClothesSelectionDialog("원피스/스커트", 3L, (clothes) -> {
            selectedDress = clothes;
            updateCategoryUI(CategoryType.DRESS, clothes);
            updateSelectedCount();
        });
    }

    @FXML
    private void handleOuterClick() {
        System.out.println("아우터 클릭됨");
        showClothesSelectionDialog("아우터", 5L, (clothes) -> {
            selectedOuter = clothes;
            updateCategoryUI(CategoryType.OUTER, clothes);
            updateSelectedCount();
        });
    }

    @FXML
    private void handleAccessoryClick() {
        System.out.println("악세사리 클릭됨");
        showClothesSelectionDialog("악세사리", 7L, (clothes) -> {
            selectedAccessory = clothes;
            updateCategoryUI(CategoryType.ACCESSORY, clothes);
            updateSelectedCount();
        });
    }

    @FXML
    private void handleEtcClick() {
        System.out.println("기타 클릭됨");
        showClothesSelectionDialog("기타", 8L, (clothes) -> {
            selectedEtc = clothes;
            updateCategoryUI(CategoryType.ETC, clothes);
            updateSelectedCount();
        });
    }

    private void showClothesSelectionDialog(String categoryName, Long categoryId, ClothesSelectionCallback callback) {
        try {
            if (memberSession == null) {
                showError("로그인이 필요합니다.");
                return;
            }

            // 현재 Stage 가져오기
            Stage currentStage = (Stage) codiNameField.getScene().getWindow();

            // 실제 모달창 호출
            ClothesSelectionModal.showModal(currentStage, categoryId, categoryName,
                    new ClothesSelectionModalController.ClothesSelectionCallback() {
                        @Override
                        public void onClothesSelected(Wardrobe clothes) {
                            // 선택된 옷 정보를 전달
                            callback.onClothesSelected(clothes);
                        }
                    });

        } catch (Exception e) {
            System.err.println("옷 선택 중 오류: " + e.getMessage());
            e.printStackTrace();
            showError("옷 선택 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    private Wardrobe createTempWardrobe(String name, Long categoryId, Long memberId) {
        Wardrobe clothes = new Wardrobe();
        clothes.setId(System.currentTimeMillis()); // 임시 ID
        clothes.setName(name);
        clothes.setCategoryId(categoryId);
        clothes.setMemberId(memberId);
        clothes.setDeleted("N");
        return clothes;
    }

    // 카테고리별 UI 업데이트
    private void updateCategoryUI(CategoryType categoryType, Wardrobe clothes) {
        switch (categoryType) {
            case TOP:
                updateTopUI(clothes);
                break;
            case BOTTOM:
                updateBottomUI(clothes);
                break;
            case SHOES:
                updateShoesUI(clothes);
                break;
            case BAG:
                updateBagUI(clothes);
                break;
            case DRESS:
                updateDressUI(clothes);
                break;
            case OUTER:
                updateOuterUI(clothes);
                break;
            case ACCESSORY:
                updateAccessoryUI(clothes);
                break;
            case ETC:
                updateEtcUI(clothes);
                break;
        }
    }

    private void updateTopUI(Wardrobe clothes) {
        if (clothes != null) {
            // 플러스 아이콘 숨기기
            if (topCircle != null) topCircle.setVisible(false);
            if (topPlusIcon != null) topPlusIcon.setVisible(false);

            // 옷 이미지 표시
            if (topImageView != null) {
                topImageView.setVisible(true);
                setClothesImage(topImageView, clothes);
            }

            // 텍스트 업데이트
            if (topField != null) topField.setText(clothes.getName());
            if (topStatusLabel != null) topStatusLabel.setText(clothes.getName());

            // 선택 상태 스타일 적용
            if (topPane != null) {
                topPane.setStyle("-fx-cursor: hand; -fx-background-color: #e8f4ff; -fx-border-color: #007acc; -fx-border-width: 2; -fx-border-radius: 8; -fx-background-radius: 8;");
            }
        }
    }

    private void updateBottomUI(Wardrobe clothes) {
        if (clothes != null) {
            if (bottomCircle != null) bottomCircle.setVisible(false);
            if (bottomPlusIcon != null) bottomPlusIcon.setVisible(false);

            if (bottomImageView != null) {
                bottomImageView.setVisible(true);
                setClothesImage(bottomImageView, clothes);
            }

            if (bottomField != null) bottomField.setText(clothes.getName());
            if (bottomStatusLabel != null) bottomStatusLabel.setText(clothes.getName());

            if (bottomPane != null) {
                bottomPane.setStyle("-fx-cursor: hand; -fx-background-color: #e8f4ff; -fx-border-color: #007acc; -fx-border-width: 2; -fx-border-radius: 8; -fx-background-radius: 8;");
            }
        }
    }

    private void updateShoesUI(Wardrobe clothes) {
        if (clothes != null) {
            if (shoesCircle != null) shoesCircle.setVisible(false);
            if (shoesPlusIcon != null) shoesPlusIcon.setVisible(false);

            if (shoesImageView != null) {
                shoesImageView.setVisible(true);
                setClothesImage(shoesImageView, clothes);
            }

            if (shoesField != null) shoesField.setText(clothes.getName());
            if (shoesStatusLabel != null) shoesStatusLabel.setText(clothes.getName());

            if (shoesPane != null) {
                shoesPane.setStyle("-fx-cursor: hand; -fx-background-color: #e8f4ff; -fx-border-color: #007acc; -fx-border-width: 2; -fx-border-radius: 8; -fx-background-radius: 8;");
            }
        }
    }

    private void updateBagUI(Wardrobe clothes) {
        if (clothes != null) {
            if (bagCircle != null) bagCircle.setVisible(false);
            if (bagPlusIcon != null) bagPlusIcon.setVisible(false);

            if (bagImageView != null) {
                bagImageView.setVisible(true);
                setClothesImage(bagImageView, clothes);
            }

            if (bagField != null) bagField.setText(clothes.getName());
            if (bagStatusLabel != null) bagStatusLabel.setText(clothes.getName());

            if (bagPane != null) {
                bagPane.setStyle("-fx-cursor: hand; -fx-background-color: #e8f4ff; -fx-border-color: #007acc; -fx-border-width: 2; -fx-border-radius: 8; -fx-background-radius: 8;");
            }
        }
    }

    private void updateDressUI(Wardrobe clothes) {
        if (clothes != null) {
            if (dressCircle != null) dressCircle.setVisible(false);
            if (dressPlusIcon != null) dressPlusIcon.setVisible(false);

            if (dressImageView != null) {
                dressImageView.setVisible(true);
                setClothesImage(dressImageView, clothes);
            }

            if (dressField != null) dressField.setText(clothes.getName());
            if (dressStatusLabel != null) dressStatusLabel.setText(clothes.getName());

            if (dressPane != null) {
                dressPane.setStyle("-fx-cursor: hand; -fx-background-color: #e8f4ff; -fx-border-color: #007acc; -fx-border-width: 2; -fx-border-radius: 8; -fx-background-radius: 8;");
            }
        }
    }

    private void updateOuterUI(Wardrobe clothes) {
        if (clothes != null) {
            if (outerCircle != null) outerCircle.setVisible(false);
            if (outerPlusIcon != null) outerPlusIcon.setVisible(false);

            if (outerImageView != null) {
                outerImageView.setVisible(true);
                setClothesImage(outerImageView, clothes);
            }

            if (outerField != null) outerField.setText(clothes.getName());
            if (outerStatusLabel != null) outerStatusLabel.setText(clothes.getName());

            if (outerPane != null) {
                outerPane.setStyle("-fx-cursor: hand; -fx-background-color: #e8f4ff; -fx-border-color: #007acc; -fx-border-width: 2; -fx-border-radius: 8; -fx-background-radius: 8;");
            }
        }
    }

    private void updateAccessoryUI(Wardrobe clothes) {
        if (clothes != null) {
            if (accessoryCircle != null) accessoryCircle.setVisible(false);
            if (accessoryPlusIcon != null) accessoryPlusIcon.setVisible(false);

            if (accessoryImageView != null) {
                accessoryImageView.setVisible(true);
                setClothesImage(accessoryImageView, clothes);
            }

            if (accessoryField != null) accessoryField.setText(clothes.getName());
            if (accessoryStatusLabel != null) accessoryStatusLabel.setText(clothes.getName());

            if (accessoryPane != null) {
                accessoryPane.setStyle("-fx-cursor: hand; -fx-background-color: #e8f4ff; -fx-border-color: #007acc; -fx-border-width: 2; -fx-border-radius: 8; -fx-background-radius: 8;");
            }
        }
    }

    private void updateEtcUI(Wardrobe clothes) {
        if (clothes != null) {
            if (etcCircle != null) etcCircle.setVisible(false);
            if (etcPlusIcon != null) etcPlusIcon.setVisible(false);

            if (etcImageView != null) {
                etcImageView.setVisible(true);
                setClothesImage(etcImageView, clothes);
            }

            if (etcField != null) etcField.setText(clothes.getName());
            if (etcStatusLabel != null) etcStatusLabel.setText(clothes.getName());

            if (etcPane != null) {
                etcPane.setStyle("-fx-cursor: hand; -fx-background-color: #e8f4ff; -fx-border-color: #007acc; -fx-border-width: 2; -fx-border-radius: 8; -fx-background-radius: 8;");
            }
        }
    }

    private void setClothesImage(ImageView imageView, Wardrobe clothes) {
        try {
            if (clothes.getPicture() != null && clothes.getPicture().length > 0) {
                ByteArrayInputStream bis = new ByteArrayInputStream(clothes.getPicture());
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
            Image defaultImage = new Image(getClass().getResourceAsStream("/images/default-clothes.png"));
            if (defaultImage != null && !defaultImage.isError()) {
                imageView.setImage(defaultImage);
            }
        } catch (Exception e) {
            System.err.println("기본 이미지 로딩 실패: " + e.getMessage());
        }
    }

    @FXML
    private void handleSaveClick() {
        System.out.println("저장 버튼 클릭됨");

        try {
            if (!validateForm()) {
                System.out.println("폼 검증 실패");
                return;
            }

            // 저장 버튼 비활성화
            if (saveButton != null) {
                saveButton.setDisable(true);
            }

            System.out.println("폼 검증 통과, 저장 작업 시작");

            Task<Void> saveTask = new Task<Void>() {
                @Override
                protected Void call() throws Exception {
                    System.out.println("백그라운드 저장 작업 시작");

                    Codi codi = createCodiFromForm();
                    System.out.println("Codi 객체 생성 완료: " + codi.getName());

                    List<Long> clothesIds = collectSelectedClothesIds();
                    System.out.println("선택된 옷 ID 목록: " + clothesIds);

                    // 디버깅을 위한 상세 정보 출력
                    if (selectedTop != null) System.out.println("상의: " + selectedTop.getName() + " (ID: " + selectedTop.getId() + ")");
                    if (selectedBottom != null) System.out.println("바지: " + selectedBottom.getName() + " (ID: " + selectedBottom.getId() + ")");
                    if (selectedShoes != null) System.out.println("신발: " + selectedShoes.getName() + " (ID: " + selectedShoes.getId() + ")");
                    if (selectedBag != null) System.out.println("가방: " + selectedBag.getName() + " (ID: " + selectedBag.getId() + ")");

                    codiService.saveCodi(codi, clothesIds);
                    System.out.println("코디 저장 완료");

                    return null;
                }

                @Override
                protected void succeeded() {
                    System.out.println("저장 성공");
                    showSuccess("코디가 성공적으로 저장되었습니다!");
                    navigateToCodiList();
                }

                private void showSuccess(String message) {
                    System.out.println("성공: " + message);
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("성공");
                    alert.setHeaderText(null);
                    alert.setContentText(message);
                    alert.showAndWait();
                }

                @Override
                protected void failed() {
                    System.err.println("저장 실패: " + getException().getMessage());
                    getException().printStackTrace();

                    // 저장 버튼 재활성화
                    if (saveButton != null) {
                        saveButton.setDisable(false);
                    }

                    showError("저장 중 오류가 발생했습니다: " + getException().getMessage());
                }
            };

            Thread saveThread = new Thread(saveTask);
            saveThread.setDaemon(true);
            saveThread.start();

        } catch (Exception e) {
            System.err.println("저장 준비 중 오류: " + e.getMessage());
            e.printStackTrace();

            // 저장 버튼 재활성화
            if (saveButton != null) {
                saveButton.setDisable(false);
            }

            showError("저장 준비 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    @FXML
    private void handleResetClick() {
        System.out.println("초기화 버튼 클릭됨");

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION,
                "모든 입력 내용이 삭제됩니다. 계속하시겠습니까?",
                ButtonType.YES, ButtonType.NO);
        alert.setTitle("확인");
        alert.setHeaderText(null);

        alert.showAndWait().ifPresent(result -> {
            if (result == ButtonType.YES) {
                resetForm();
                System.out.println("폼 초기화 완료");
            }
        });
    }

    @FXML
    private void handleBackClick() {
        System.out.println("뒤로가기 버튼 클릭됨");

        if (hasUnsavedChanges()) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION,
                    "저장하지 않은 변경사항이 있습니다. 정말로 나가시겠습니까?",
                    ButtonType.YES, ButtonType.NO);
            alert.setTitle("확인");
            alert.setHeaderText(null);

            alert.showAndWait().ifPresent(result -> {
                if (result == ButtonType.YES) {
                    navigateToCodiList();
                }
            });
        } else {
            navigateToCodiList();
        }
    }

    private boolean validateForm() {
        System.out.println("폼 검증 시작");

        if (codiNameField == null || codiNameField.getText() == null || codiNameField.getText().trim().isEmpty()) {
            System.out.println("코디 이름이 비어있음");
            showError("코디 이름을 입력해주세요.");
            if (codiNameField != null) {
                codiNameField.requestFocus();
            }
            return false;
        }

        // 최소 하나의 옷은 선택되어야 함
        List<Long> clothesIds = collectSelectedClothesIds();
        if (clothesIds.isEmpty()) {
            System.out.println("선택된 옷이 없음");
            showError("최소 하나의 옷을 선택해주세요.");
            return false;
        }

        System.out.println("폼 검증 통과 - 코디명: " + codiNameField.getText() + ", 선택된 옷: " + clothesIds.size() + "개");
        return true;
    }

    private Codi createCodiFromForm() {
        System.out.println("Codi 객체 생성 시작");

        Codi codi = new Codi();

        if (memberSession == null) {
            throw new IllegalStateException("로그인된 사용자가 없습니다.");
        }

        codi.setMemberId(memberSession.getMember().getId());
        codi.setName(codiNameField.getText().trim());
        codi.setDeleted("N");
        codi.setCodiType("N"); // 기본값

        System.out.println("Codi 객체 생성 완료 - MemberID: " + memberSession.getMember().getId() + ", Name: " + codi.getName());
        return codi;
    }

    private List<Long> collectSelectedClothesIds() {
        List<Long> clothesIds = new ArrayList<>();

        if (selectedTop != null) clothesIds.add(selectedTop.getId());
        if (selectedBottom != null) clothesIds.add(selectedBottom.getId());
        if (selectedShoes != null) clothesIds.add(selectedShoes.getId());
        if (selectedBag != null) clothesIds.add(selectedBag.getId());
        if (selectedDress != null) clothesIds.add(selectedDress.getId());
        if (selectedOuter != null) clothesIds.add(selectedOuter.getId());
        if (selectedAccessory != null) clothesIds.add(selectedAccessory.getId());
        if (selectedEtc != null) clothesIds.add(selectedEtc.getId());

        System.out.println("수집된 옷 ID들: " + clothesIds);
        return clothesIds;
    }

    private boolean hasUnsavedChanges() {
        boolean hasChanges = (codiNameField != null && codiNameField.getText() != null && !codiNameField.getText().trim().isEmpty()) ||
                selectedTop != null || selectedBottom != null || selectedShoes != null ||
                selectedBag != null || selectedDress != null || selectedOuter != null ||
                selectedAccessory != null || selectedEtc != null;

        System.out.println("변경사항 확인: " + hasChanges);
        return hasChanges;
    }

    private void resetForm() {
        System.out.println("폼 초기화 시작");

        // 텍스트 필드 초기화
        if (codiNameField != null) codiNameField.clear();
        if (topField != null) topField.clear();
        if (bottomField != null) bottomField.clear();
        if (shoesField != null) shoesField.clear();
        if (bagField != null) bagField.clear();
        if (dressField != null) dressField.clear();
        if (outerField != null) outerField.clear();
        if (accessoryField != null) accessoryField.clear();
        if (etcField != null) etcField.clear();

        // 선택된 옷들 초기화
        selectedTop = selectedBottom = selectedShoes = selectedBag = null;
        selectedDress = selectedOuter = selectedAccessory = selectedEtc = null;

        // UI 초기화
        resetAllUI();
        updateSelectedCount();

        System.out.println("폼 초기화 완료");
    }

    private void resetAllUI() {
        String defaultStyle = "-fx-cursor: hand; -fx-background-color: transparent; -fx-border-radius: 8; -fx-background-radius: 8;";

        // 패널 스타일 초기화
        AnchorPane[] panes = {topPane, bottomPane, shoesPane, bagPane, dressPane, outerPane, accessoryPane, etcPane};
        for (AnchorPane pane : panes) {
            if (pane != null) pane.setStyle(defaultStyle);
        }

        // 이미지뷰 숨기기
        ImageView[] imageViews = {topImageView, bottomImageView, shoesImageView, bagImageView, dressImageView, outerImageView, accessoryImageView, etcImageView};
        for (ImageView imageView : imageViews) {
            if (imageView != null) {
                imageView.setVisible(false);
                imageView.setImage(null);
            }
        }

        // 원과 플러스 아이콘 다시 표시
        Circle[] circles = {topCircle, bottomCircle, shoesCircle, bagCircle, dressCircle, outerCircle, accessoryCircle, etcCircle};
        for (Circle circle : circles) {
            if (circle != null) circle.setVisible(true);
        }

        ImageView[] plusIcons = {topPlusIcon, bottomPlusIcon, shoesPlusIcon, bagPlusIcon, dressPlusIcon, outerPlusIcon, accessoryPlusIcon, etcPlusIcon};
        for (ImageView plusIcon : plusIcons) {
            if (plusIcon != null) plusIcon.setVisible(true);
        }

        // 상태 라벨 초기화
        if (topStatusLabel != null) topStatusLabel.setText("클릭하여 선택");
        if (bottomStatusLabel != null) bottomStatusLabel.setText("클릭하여 선택");
        if (shoesStatusLabel != null) shoesStatusLabel.setText("클릭하여 선택");
        if (bagStatusLabel != null) bagStatusLabel.setText("클릭하여 선택");
        if (dressStatusLabel != null) dressStatusLabel.setText("선택사항");
        if (outerStatusLabel != null) outerStatusLabel.setText("선택사항");
        if (accessoryStatusLabel != null) accessoryStatusLabel.setText("선택사항");
        if (etcStatusLabel != null) etcStatusLabel.setText("선택사항");
    }

    private void updateSelectedCount() {
        if (selectedCountLabel != null) {
            int count = (int) Stream.of(selectedTop, selectedBottom, selectedShoes, selectedBag,
                            selectedDress, selectedOuter, selectedAccessory, selectedEtc)
                    .filter(Objects::nonNull)
                    .count();
            selectedCountLabel.setText("선택된 아이템: " + count + "개");
        }
    }

    private void navigateToCodiList() {
        try {
            MainLayoutController.loadView("/com/samyukgu/what2wear/myCodi/myCodiList.fxml");
            System.out.println("코디 목록 페이지로 이동 완료");
        } catch (Exception e) {
            System.err.println("페이지 이동 중 오류: " + e.getMessage());
            e.printStackTrace();
            showError("페이지 이동 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    private void showAlert(String message) {
        System.out.println("알림: " + message);
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
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

    // 열거형과 인터페이스
    private enum CategoryType {
        TOP, BOTTOM, SHOES, BAG, DRESS, OUTER, ACCESSORY, ETC
    }

    @FunctionalInterface
    private interface ClothesSelectionCallback {
        void onClothesSelected(Wardrobe clothes);
    }
}