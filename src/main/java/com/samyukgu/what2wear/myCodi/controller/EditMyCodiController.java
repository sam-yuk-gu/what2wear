package com.samyukgu.what2wear.myCodi.controller;

import com.samyukgu.what2wear.di.DIContainer;
import com.samyukgu.what2wear.layout.controller.MainLayoutController;
import com.samyukgu.what2wear.member.Session.MemberSession;
import com.samyukgu.what2wear.member.model.Member;
import com.samyukgu.what2wear.myCodi.dao.CodiDetailOracleDAO;
import com.samyukgu.what2wear.myCodi.dao.CodiOracleDAO;
import com.samyukgu.what2wear.myCodi.model.Codi;
import com.samyukgu.what2wear.myCodi.model.CodiWithDetails;
import com.samyukgu.what2wear.myCodi.service.CodiService;
import com.samyukgu.what2wear.wardrobe.dao.WardrobeOracleDAO;
import com.samyukgu.what2wear.wardrobe.model.Wardrobe;
import com.samyukgu.what2wear.wardrobe.service.WardrobeService;
import com.samyukgu.what2wear.common.controller.CustomModalController;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class EditMyCodiController implements Initializable {

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
    @FXML private Button cancelButton;
    @FXML private Button updateButton;

    // 추가: rootPane 필드
    @FXML private StackPane rootPane;

    // 서비스 객체들
    private CodiService codiService;
    private MemberSession memberSession;

    // 현재 수정 중인 코디 정보
    private CodiWithDetails currentCodi;

    // 선택된 옷 정보들
    private Wardrobe selectedTop, selectedBottom, selectedShoes, selectedBag;
    private Wardrobe selectedDress, selectedOuter, selectedAccessory, selectedEtc;

    // 원본 옷 정보들
    private Map<String, Wardrobe> originalClothes = new HashMap<>();
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        DIContainer diContainer = DIContainer.getInstance();
        codiService = diContainer.resolve(CodiService.class);
        memberSession = diContainer.resolve(MemberSession.class);

        System.out.println("EditMyCodiController 초기화 시작");
        try {
            setupUI();
            loadCurrentCodiData();
            setupFormValidation();
            System.out.println("EditMyCodiController 초기화 완료");
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

        // 모든 텍스트 필드를 읽기 전용으로 설정
        setFieldsReadOnly();

        System.out.println("UI 설정 완료");
    }

    private void setFieldsReadOnly() {
        TextField[] fields = {topField, bottomField, shoesField, bagField,
                dressField, outerField, accessoryField, etcField};

        for (TextField field : fields) {
            if (field != null) {
                field.setEditable(false);
            }
        }
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

    private void loadCurrentCodiData() {
        // MyCodiEditData에서 선택된 코디 정보 가져오기
        currentCodi = MyCodiEditData.getSelectedCodi();

        if (currentCodi != null) {
            System.out.println("수정할 코디 로드: " + currentCodi.getName());
            populateForm(currentCodi);
        } else {
            System.err.println("수정할 코디 정보가 없습니다.");
            showError("수정할 코디 정보를 불러올 수 없습니다.");
            handleBackClick();
        }
    }

    private void populateForm(CodiWithDetails codi) {
        try {
            // 코디 이름 설정
            if (codiNameField != null) {
                codiNameField.setText(codi.getName() != null ? codi.getName() : "");
            }

            // 기존 선택 초기화
            clearAllSelections();

            // 옷 정보들을 카테고리별로 분류하여 설정
            if (codi.getClothes() != null && !codi.getClothes().isEmpty()) {
                System.out.println("코디에 포함된 옷 개수: " + codi.getClothes().size());

                for (Wardrobe wardrobe : codi.getClothes()) {
                    Long categoryId = wardrobe.getCategoryId() != null ? wardrobe.getCategoryId() : 8L;
                    System.out.println("옷: " + wardrobe.getName() + ", 카테고리: " + categoryId);

                    switch (categoryId.intValue()) {
                        case 1: // 상의
                            selectedTop = wardrobe;
                            originalClothes.put("top", wardrobe);
                            updateCategoryUI(CategoryType.TOP, wardrobe);
                            break;
                        case 2: // 바지
                            selectedBottom = wardrobe;
                            originalClothes.put("bottom", wardrobe);
                            updateCategoryUI(CategoryType.BOTTOM, wardrobe);
                            break;
                        case 3: // 원피스/스커트
                            selectedDress = wardrobe;
                            originalClothes.put("dress", wardrobe);
                            updateCategoryUI(CategoryType.DRESS, wardrobe);
                            break;
                        case 4: // 가방
                            selectedBag = wardrobe;
                            originalClothes.put("bag", wardrobe);
                            updateCategoryUI(CategoryType.BAG, wardrobe);
                            break;
                        case 5: // 아우터
                            selectedOuter = wardrobe;
                            originalClothes.put("outer", wardrobe);
                            updateCategoryUI(CategoryType.OUTER, wardrobe);
                            break;
                        case 6: // 신발
                            selectedShoes = wardrobe;
                            originalClothes.put("shoes", wardrobe);
                            updateCategoryUI(CategoryType.SHOES, wardrobe);
                            break;
                        case 7: // 악세사리
                            selectedAccessory = wardrobe;
                            originalClothes.put("accessory", wardrobe);
                            updateCategoryUI(CategoryType.ACCESSORY, wardrobe);
                            break;
                        default: // 기타
                            selectedEtc = wardrobe;
                            originalClothes.put("etc", wardrobe);
                            updateCategoryUI(CategoryType.ETC, wardrobe);
                            break;
                    }
                }
            }

            updateSelectedCount();
            System.out.println("폼 데이터 설정 완료");

        } catch (Exception e) {
            System.err.println("코디 정보 폼 설정 오류: " + e.getMessage());
            showError("코디 정보를 폼에 설정하는 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    private void clearAllSelections() {
        selectedTop = selectedBottom = selectedShoes = selectedBag = null;
        selectedDress = selectedOuter = selectedAccessory = selectedEtc = null;
        originalClothes.clear();

        // 모든 필드 초기화
        TextField[] fields = {topField, bottomField, shoesField, bagField,
                dressField, outerField, accessoryField, etcField};
        for (TextField field : fields) {
            if (field != null) field.clear();
        }
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
                            callback.onClothesSelected(clothes);
                        }
                    });

        } catch (Exception e) {
            System.err.println("옷 선택 중 오류: " + e.getMessage());
            e.printStackTrace();
            showError("옷 선택 중 오류가 발생했습니다: " + e.getMessage());
        }
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
        } else {
            // 선택 해제 시 원래 상태로 되돌리기
            resetTopUI();
        }
    }

    private void resetTopUI() {
        if (topCircle != null) topCircle.setVisible(true);
        if (topPlusIcon != null) topPlusIcon.setVisible(true);
        if (topImageView != null) topImageView.setVisible(false);
        if (topField != null) topField.clear();
        if (topStatusLabel != null) topStatusLabel.setText("클릭하여 변경");
        if (topPane != null) {
            topPane.setStyle("-fx-cursor: hand; -fx-background-color: transparent; -fx-border-radius: 8; -fx-background-radius: 8;");
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
        } else {
            resetBottomUI();
        }
    }

    private void resetBottomUI() {
        if (bottomCircle != null) bottomCircle.setVisible(true);
        if (bottomPlusIcon != null) bottomPlusIcon.setVisible(true);
        if (bottomImageView != null) bottomImageView.setVisible(false);
        if (bottomField != null) bottomField.clear();
        if (bottomStatusLabel != null) bottomStatusLabel.setText("클릭하여 변경");
        if (bottomPane != null) {
            bottomPane.setStyle("-fx-cursor: hand; -fx-background-color: transparent; -fx-border-radius: 8; -fx-background-radius: 8;");
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
        } else {
            resetShoesUI();
        }
    }

    private void resetShoesUI() {
        if (shoesCircle != null) shoesCircle.setVisible(true);
        if (shoesPlusIcon != null) shoesPlusIcon.setVisible(true);
        if (shoesImageView != null) shoesImageView.setVisible(false);
        if (shoesField != null) shoesField.clear();
        if (shoesStatusLabel != null) shoesStatusLabel.setText("클릭하여 변경");
        if (shoesPane != null) {
            shoesPane.setStyle("-fx-cursor: hand; -fx-background-color: transparent; -fx-border-radius: 8; -fx-background-radius: 8;");
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
        } else {
            resetBagUI();
        }
    }

    private void resetBagUI() {
        if (bagCircle != null) bagCircle.setVisible(true);
        if (bagPlusIcon != null) bagPlusIcon.setVisible(true);
        if (bagImageView != null) bagImageView.setVisible(false);
        if (bagField != null) bagField.clear();
        if (bagStatusLabel != null) bagStatusLabel.setText("클릭하여 변경");
        if (bagPane != null) {
            bagPane.setStyle("-fx-cursor: hand; -fx-background-color: transparent; -fx-border-radius: 8; -fx-background-radius: 8;");
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
        } else {
            resetDressUI();
        }
    }

    private void resetDressUI() {
        if (dressCircle != null) dressCircle.setVisible(true);
        if (dressPlusIcon != null) dressPlusIcon.setVisible(true);
        if (dressImageView != null) dressImageView.setVisible(false);
        if (dressField != null) dressField.clear();
        if (dressStatusLabel != null) dressStatusLabel.setText("선택사항");
        if (dressPane != null) {
            dressPane.setStyle("-fx-cursor: hand; -fx-background-color: transparent; -fx-border-radius: 8; -fx-background-radius: 8;");
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
        } else {
            resetOuterUI();
        }
    }

    private void resetOuterUI() {
        if (outerCircle != null) outerCircle.setVisible(true);
        if (outerPlusIcon != null) outerPlusIcon.setVisible(true);
        if (outerImageView != null) outerImageView.setVisible(false);
        if (outerField != null) outerField.clear();
        if (outerStatusLabel != null) outerStatusLabel.setText("선택사항");
        if (outerPane != null) {
            outerPane.setStyle("-fx-cursor: hand; -fx-background-color: transparent; -fx-border-radius: 8; -fx-background-radius: 8;");
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
        } else {
            resetAccessoryUI();
        }
    }

    private void resetAccessoryUI() {
        if (accessoryCircle != null) accessoryCircle.setVisible(true);
        if (accessoryPlusIcon != null) accessoryPlusIcon.setVisible(true);
        if (accessoryImageView != null) accessoryImageView.setVisible(false);
        if (accessoryField != null) accessoryField.clear();
        if (accessoryStatusLabel != null) accessoryStatusLabel.setText("선택사항");
        if (accessoryPane != null) {
            accessoryPane.setStyle("-fx-cursor: hand; -fx-background-color: transparent; -fx-border-radius: 8; -fx-background-radius: 8;");
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
        } else {
            resetEtcUI();
        }
    }

    private void resetEtcUI() {
        if (etcCircle != null) etcCircle.setVisible(true);
        if (etcPlusIcon != null) etcPlusIcon.setVisible(true);
        if (etcImageView != null) etcImageView.setVisible(false);
        if (etcField != null) etcField.clear();
        if (etcStatusLabel != null) etcStatusLabel.setText("선택사항");
        if (etcPane != null) {
            etcPane.setStyle("-fx-cursor: hand; -fx-background-color: transparent; -fx-border-radius: 8; -fx-background-radius: 8;");
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
    // 스냅샷 생성 관련 메서드들
    private byte[] createCodiSnapshot() {
        try {
            System.out.println("코디 스냅샷 생성 시작");

            // 현재 스레드가 FX Application Thread인지 확인
            if (!Platform.isFxApplicationThread()) {
                System.err.println("스냅샷은 FX Application Thread에서만 생성할 수 있습니다.");
                return null;
            }

            // 선택된 옷들 수집
            List<Wardrobe> selectedClothes = collectSelectedClothes();
            if (selectedClothes.isEmpty()) {
                System.out.println("선택된 옷이 없어 스냅샷을 생성하지 않습니다.");
                return null;
            }

            // 새로운 GridPane 생성 (스냅샷 전용)
            GridPane snapshotGrid = createSnapshotGridPane(selectedClothes);

            if (snapshotGrid == null) {
                System.err.println("스냅샷용 GridPane 생성에 실패했습니다.");
                return null;
            }

            // 임시 Scene 생성 (스냅샷을 위해)
            Scene tempScene = new Scene(snapshotGrid);

            // 스타일 적용 (배경색 등)
            snapshotGrid.setStyle("-fx-background-color: white; -fx-padding: 20;");

            // GridPane의 스냅샷 생성
            WritableImage snapshot = snapshotGrid.snapshot(null, null);

            if (snapshot == null) {
                System.err.println("스냅샷 생성에 실패했습니다.");
                return null;
            }

            // WritableImage를 BufferedImage로 변환
            BufferedImage bufferedImage = SwingFXUtils.fromFXImage(snapshot, null);

            if (bufferedImage == null) {
                System.err.println("BufferedImage 변환에 실패했습니다.");
                return null;
            }

            // BufferedImage를 byte[]로 변환
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(bufferedImage, "PNG", baos);
            byte[] imageBytes = baos.toByteArray();

            System.out.println("코디 스냅샷 생성 완료 - 크기: " + imageBytes.length + " bytes");
            return imageBytes;

        } catch (IOException e) {
            System.err.println("스냅샷 생성 중 I/O 오류: " + e.getMessage());
            e.printStackTrace();
            return null;
        } catch (Exception e) {
            System.err.println("스냅샷 생성 중 예상치 못한 오류: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    private List<Wardrobe> collectSelectedClothes() {
        List<Wardrobe> selectedClothes = new ArrayList<>();

        if (selectedTop != null) selectedClothes.add(selectedTop);
        if (selectedBottom != null) selectedClothes.add(selectedBottom);
        if (selectedShoes != null) selectedClothes.add(selectedShoes);
        if (selectedBag != null) selectedClothes.add(selectedBag);
        if (selectedDress != null) selectedClothes.add(selectedDress);
        if (selectedOuter != null) selectedClothes.add(selectedOuter);
        if (selectedAccessory != null) selectedClothes.add(selectedAccessory);
        if (selectedEtc != null) selectedClothes.add(selectedEtc);

        return selectedClothes;
    }

    private GridPane createSnapshotGridPane(List<Wardrobe> selectedClothes) {
        try {
            // 적절한 그리드 크기 계산 (옷 개수에 따라)
            int itemCount = selectedClothes.size();
            int cols = Math.min(itemCount, 3); // 최대 3열
            int rows = (int) Math.ceil((double) itemCount / cols); // 필요한 행 수

            GridPane grid = new GridPane();
            grid.setHgap(15); // 수평 간격
            grid.setVgap(15); // 수직 간격
            grid.setAlignment(Pos.CENTER);

            // 컬럼 제약 조건 설정
            for (int i = 0; i < cols; i++) {
                ColumnConstraints colConstraint = new ColumnConstraints();
                colConstraint.setMinWidth(120);
                colConstraint.setPrefWidth(120);
                colConstraint.setHalignment(HPos.CENTER);
                grid.getColumnConstraints().add(colConstraint);
            }

            // 로우 제약 조건 설정
            for (int i = 0; i < rows; i++) {
                RowConstraints rowConstraint = new RowConstraints();
                rowConstraint.setMinHeight(140);
                rowConstraint.setPrefHeight(140);
                rowConstraint.setValignment(VPos.CENTER);
                grid.getRowConstraints().add(rowConstraint);
            }

            // 선택된 옷들을 그리드에 배치
            for (int i = 0; i < selectedClothes.size(); i++) {
                Wardrobe clothes = selectedClothes.get(i);
                int col = i % cols;
                int row = i / cols;

                VBox clothesBox = createClothesBox(clothes);
                grid.add(clothesBox, col, row);
            }

            return grid;

        } catch (Exception e) {
            System.err.println("스냅샷 GridPane 생성 중 오류: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    private VBox createClothesBox(Wardrobe clothes) {
        VBox box = new VBox();
        box.setAlignment(Pos.CENTER);
        box.setSpacing(8);
        box.setPrefWidth(120);
        box.setPrefHeight(140);

        // 이미지뷰 생성
        ImageView imageView = new ImageView();
        imageView.setFitWidth(100);
        imageView.setFitHeight(100);
        imageView.setPreserveRatio(true);
        imageView.setSmooth(true);

        // 이미지 설정
        setSnapshotClothesImage(imageView, clothes);

        box.getChildren().addAll(imageView);

        return box;
    }

    private void setSnapshotClothesImage(ImageView imageView, Wardrobe clothes) {
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
            System.err.println("스냅샷 이미지 로딩 실패: " + e.getMessage());
        }

        // 기본 이미지 설정
        try {
            Image defaultImage = new Image(getClass().getResourceAsStream("/images/default-clothes.png"));
            if (defaultImage != null && !defaultImage.isError()) {
                imageView.setImage(defaultImage);
            } else {
                // 기본 이미지도 없으면 색상 배경으로 대체
                imageView.setStyle("-fx-background-color: #f0f0f0; -fx-border-color: #cccccc; -fx-border-width: 1;");
            }
        } catch (Exception e) {
            System.err.println("기본 이미지 로딩 실패: " + e.getMessage());
            imageView.setStyle("-fx-background-color: #f0f0f0; -fx-border-color: #cccccc; -fx-border-width: 1;");
        }
    }

    private String getCategoryName(Long categoryId) {
        if (categoryId == null) return "기타";

        switch (categoryId.intValue()) {
            case 1: return "상의";
            case 2: return "바지";
            case 3: return "원피스/스커트";
            case 4: return "가방";
            case 5: return "아우터";
            case 6: return "신발";
            case 7: return "악세사리";
            case 8: return "기타";
            default: return "기타";
        }
    }

    // 수정된 handleUpdate 메서드 - CustomModal 사용
    @FXML
    private void handleUpdate() {
        System.out.println("수정 버튼 클릭됨");

        try {
            if (!validateForm()) {
                System.out.println("폼 검증 실패");
                return;
            }

            // 변경사항 확인
            if (!hasUnsavedChanges()) {
                showInfo("변경된 내용이 없습니다.");
                return;
            }

            // 선택된 옷이 있는지 확인 (스냅샷 생성을 위해)
            List<Wardrobe> selectedClothes = collectSelectedClothes();
            String confirmMessage = "코디 정보를 수정하시겠습니까?";

//            if (selectedClothes.isEmpty()) {
//                confirmMessage = "선택된 옷이 없어 스냅샷이 생성되지 않습니다.\n그래도 수정하시겠습니까?";
//            } else {
//                confirmMessage = "선택된 " + selectedClothes.size() + "개 옷의 스냅샷과 함께 코디를 수정하시겠습니까?";
//            }

            // CustomModal로 확인창 표시
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/samyukgu/what2wear/common/CustomModal.fxml"));
            StackPane modal = loader.load();

            CustomModalController controller = loader.getController();
            controller.configure(
                    "코디 수정",
                    confirmMessage,
                    "/assets/icons/greenCheck.png",
                    "#4CAF50",
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

    // 실제 수정 작업을 수행하는 메서드 (스냅샷 포함)
    private void performUpdate() {
        System.out.println("폼 검증 통과, 수정 작업 시작");

        // 수정 버튼 비활성화
        if (updateButton != null) {
            updateButton.setDisable(true);
        }

        // FX Application Thread에서 스냅샷 미리 생성
        byte[] snapshotBytes = createCodiSnapshot();
        System.out.println("스냅샷 생성 완료: " + (snapshotBytes != null ? snapshotBytes.length + " bytes" : "null"));

        Task<Void> updateTask = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                System.out.println("백그라운드 수정 작업 시작");

                Codi updatedCodi = createUpdatedCodi();
                System.out.println("수정된 Codi 객체 생성 완료: " + updatedCodi.getName());

                // 미리 생성된 스냅샷 설정
                if (snapshotBytes != null) {
                    updatedCodi.setPicture(snapshotBytes);
                    System.out.println("코디 스냅샷 업데이트 완료");
                } else {
                    System.out.println("코디 스냅샷 생성 실패 - 기존 이미지 유지");
                    // 기존 이미지를 유지하거나 null로 설정할지 결정
                    // 여기서는 기존 이미지를 유지하도록 설정하지 않음 (새로운 스냅샷으로 교체)
                }

                List<Long> clothesIds = collectSelectedClothesIds();
                System.out.println("선택된 옷 ID 목록: " + clothesIds);

                // 디버깅을 위한 상세 정보 출력
                if (selectedTop != null) System.out.println("상의: " + selectedTop.getName() + " (ID: " + selectedTop.getId() + ")");
                if (selectedBottom != null) System.out.println("바지: " + selectedBottom.getName() + " (ID: " + selectedBottom.getId() + ")");
                if (selectedShoes != null) System.out.println("신발: " + selectedShoes.getName() + " (ID: " + selectedShoes.getId() + ")");
                if (selectedBag != null) System.out.println("가방: " + selectedBag.getName() + " (ID: " + selectedBag.getId() + ")");
                if (selectedDress != null) System.out.println("원피스/스커트: " + selectedDress.getName() + " (ID: " + selectedDress.getId() + ")");
                if (selectedOuter != null) System.out.println("아우터: " + selectedOuter.getName() + " (ID: " + selectedOuter.getId() + ")");
                if (selectedAccessory != null) System.out.println("악세사리: " + selectedAccessory.getName() + " (ID: " + selectedAccessory.getId() + ")");
                if (selectedEtc != null) System.out.println("기타: " + selectedEtc.getName() + " (ID: " + selectedEtc.getId() + ")");

                codiService.updateCodi(updatedCodi, clothesIds);
                System.out.println("코디 수정 완료");

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
                    System.err.println("수정 실패: " + getException().getMessage());
                    getException().printStackTrace();

                    // 수정 버튼 재활성화
                    if (updateButton != null) {
                        updateButton.setDisable(false);
                    }

                    showError("수정 중 오류가 발생했습니다: " + getException().getMessage());
                });
            }
        };

        Thread updateThread = new Thread(updateTask);
        updateThread.setDaemon(true);
        updateThread.start();
    }

    // 수정 성공 모달
    private void showUpdateSuccessModal() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/samyukgu/what2wear/common/CustomModal.fxml"));
            StackPane modal = loader.load();

            CustomModalController controller = loader.getController();
            controller.configure(
                    "수정 완료",
                    "코디가 성공적으로 수정되었습니다.",
                    "/assets/icons/greenCheck.png",
                    "#4CAF50",
                    null, // 취소 버튼 없음
                    "확인",
                    null, // 취소 액션 없음
                    () -> {
                        // 확인 버튼 클릭 시
                        rootPane.getChildren().remove(modal);

                        // 데이터 정리하고 상세 페이지로 이동
                        MyCodiEditData.clearSelectedCodi();
                        MainLayoutController.loadView("/com/samyukgu/what2wear/myCodi/myCodiList.fxml");
                    }
            );

            rootPane.getChildren().add(modal);

        } catch (Exception e) {
            System.err.println("성공 모달 로딩 실패: " + e.getMessage());

            // 모달 실패 시 기본 처리
            showSuccess("코디가 성공적으로 수정되었습니다!");
            MyCodiEditData.clearSelectedCodi();
            navigateToCodiList();
        }
    }

    // 수정된 handleResetClick 메서드 - CustomModal 사용
    @FXML
    private void handleResetClick() {
        System.out.println("초기화 버튼 클릭됨");

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/samyukgu/what2wear/common/CustomModal.fxml"));
            StackPane modal = loader.load();

            CustomModalController controller = loader.getController();
            controller.configure(
                    "초기화 확인",
                    "원래 상태로 되돌리시겠습니까?",
                    null, // 아이콘 없음
                    "#ffc107", // 노란색
                    "취소",
                    "초기화",
                    () -> rootPane.getChildren().remove(modal), // 취소
                    () -> {
                        rootPane.getChildren().remove(modal);
                        populateForm(currentCodi); // 원래 데이터로 다시 로드
                        System.out.println("폼 초기화 완료");
                    }
            );

            rootPane.getChildren().add(modal);

        } catch (Exception e) {
            e.printStackTrace();
            // 모달 실패 시 기존 Alert 사용
            showResetConfirmationAlert();
        }
    }

    // 기존 Alert 방식 (백업용)
    private void showResetConfirmationAlert() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION,
                "원래 상태로 되돌리시겠습니까?",
                ButtonType.YES, ButtonType.NO);
        alert.setTitle("확인");
        alert.setHeaderText(null);

        alert.showAndWait().ifPresent(result -> {
            if (result == ButtonType.YES) {
                populateForm(currentCodi); // 원래 데이터로 다시 로드
                System.out.println("폼 초기화 완료");
            }
        });
    }

    // 수정된 handleCancel 메서드 - CustomModal 사용
    @FXML
    private void handleCancel() {
        System.out.println("취소 버튼 클릭됨");

        if (hasUnsavedChanges()) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/samyukgu/what2wear/common/CustomModal.fxml"));
                StackPane modal = loader.load();

                CustomModalController controller = loader.getController();
                controller.configure(
                        "변경사항 확인",
                        "저장하지 않은 변경사항이 있습니다.",
                        null, // 아이콘 없음
                        "#dc3545", // 빨간색
                        "계속 수정",
                        "취소하기",
                        () -> rootPane.getChildren().remove(modal), // 계속 수정
                        () -> {
                            rootPane.getChildren().remove(modal);
                            handleBackClick(); // 취소하고 뒤로가기
                        }
                );

                rootPane.getChildren().add(modal);

            } catch (Exception e) {
                e.printStackTrace();
                // 모달 실패 시 기존 Alert 사용
                showCancelConfirmationAlert();
            }
        } else {
            handleBackClick();
        }
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
        System.out.println("뒤로가기 버튼 클릭됨");
        MyCodiEditData.clearSelectedCodi();
        MainLayoutController.loadView("/com/samyukgu/what2wear/myCodi/myCodiDetail.fxml");
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

    private Codi createUpdatedCodi() {
        System.out.println("수정된 Codi 객체 생성 시작");

        if (currentCodi == null) {
            throw new IllegalStateException("수정할 코디 정보가 없습니다.");
        }

        Codi updatedCodi = new Codi();

        // 기본 정보는 유지 - 모든 필드를 정확히 복사
        updatedCodi.setId(currentCodi.getId());
        updatedCodi.setMemberId(currentCodi.getMemberId());
        updatedCodi.setSchedule(currentCodi.getSchedule());
        updatedCodi.setScheduleDate(currentCodi.getScheduleDate());
        updatedCodi.setScope(currentCodi.getScope());
        updatedCodi.setWeather(currentCodi.getWeather());
        updatedCodi.setCodiType(currentCodi.getCodiType());
        updatedCodi.setDeleted(currentCodi.getDeleted());

        // ⭐ 중요: createdAt 필드 추가 (이제 모델에 추가됨)
        updatedCodi.setCreatedAt(currentCodi.getCreatedAt());

        // 수정된 정보 적용
        updatedCodi.setName(codiNameField.getText().trim());

        // picture는 performUpdate에서 설정하므로 여기서는 설정하지 않음

        // 필수값 검증
        if (updatedCodi.getId() == null) {
            throw new IllegalStateException("코디 ID가 null입니다.");
        }
        if (updatedCodi.getMemberId() == null) {
            throw new IllegalStateException("멤버 ID가 null입니다.");
        }
        if (updatedCodi.getName() == null || updatedCodi.getName().trim().isEmpty()) {
            throw new IllegalStateException("코디 이름이 null이거나 비어있습니다.");
        }

        // 디버깅 정보 출력
        System.out.println("수정된 Codi 객체 생성 완료:");
        System.out.println("- ID: " + updatedCodi.getId());
        System.out.println("- Name: " + updatedCodi.getName());
        System.out.println("- MemberID: " + updatedCodi.getMemberId());
        System.out.println("- CreatedAt: " + updatedCodi.getCreatedAt());
        System.out.println("- CodiType: " + updatedCodi.getCodiType());
        System.out.println("- Deleted: " + updatedCodi.getDeleted());

        return updatedCodi;
    }
    private void debugCurrentCodiInfo() {
        if (currentCodi != null) {
            System.out.println("=== 현재 코디 정보 ===");
            System.out.println("ID: " + currentCodi.getId());
            System.out.println("Name: " + currentCodi.getName());
            System.out.println("MemberID: " + currentCodi.getMemberId());
            System.out.println("CreatedAt: " + currentCodi.getCreatedAt());
            System.out.println("CodiType: " + currentCodi.getCodiType());
            System.out.println("Deleted: " + currentCodi.getDeleted());
            System.out.println("Schedule: " + currentCodi.getSchedule());
            System.out.println("ScheduleDate: " + currentCodi.getScheduleDate());
            System.out.println("Scope: " + currentCodi.getScope());
            System.out.println("Weather: " + currentCodi.getWeather());
            System.out.println("Clothes count: " + (currentCodi.getClothes() != null ? currentCodi.getClothes().size() : 0));
            System.out.println("Picture size: " + (currentCodi.getPicture() != null ? currentCodi.getPicture().length + " bytes" : "null"));
            System.out.println("=====================");
        } else {
            System.out.println("currentCodi is null!");
        }
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
        if (currentCodi == null) return false;

        // 코디 이름 변경 확인
        String currentName = currentCodi.getName() != null ? currentCodi.getName() : "";
        String newName = codiNameField.getText() != null ? codiNameField.getText().trim() : "";
        boolean nameChanged = !newName.equals(currentName);

        // 옷 변경 확인
        Set<Long> currentClothesIds = currentCodi.getClothes() != null ?
                currentCodi.getClothes().stream().map(Wardrobe::getId).collect(Collectors.toSet()) :
                new HashSet<>();
        Set<Long> newClothesIds = new HashSet<>(collectSelectedClothesIds());

        boolean clothesChanged = !currentClothesIds.equals(newClothesIds);

        boolean hasChanges = nameChanged || clothesChanged;
        System.out.println("변경사항 확인: 이름변경=" + nameChanged + ", 옷변경=" + clothesChanged + ", 전체변경=" + hasChanges);
        return hasChanges;
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

    private void showSuccess(String message) {
        System.out.println("성공: " + message);
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("성공");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showInfo(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("정보");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
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

    // 추가 유틸리티 메서드들
    private boolean canCreateSnapshot() {
        List<Wardrobe> selectedClothes = collectSelectedClothes();
        return !selectedClothes.isEmpty();
    }

    private void preserveExistingPicture(Codi updatedCodi) {
        if (currentCodi != null && currentCodi.getPicture() != null) {
            updatedCodi.setPicture(currentCodi.getPicture());
            System.out.println("기존 코디 이미지를 유지합니다.");
        }
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