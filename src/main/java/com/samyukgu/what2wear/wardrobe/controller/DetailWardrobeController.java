package com.samyukgu.what2wear.wardrobe.controller;

import com.samyukgu.what2wear.common.controller.MainLayoutController;
import com.samyukgu.what2wear.member.Session.MemberSession;
import com.samyukgu.what2wear.member.model.Member;
import com.samyukgu.what2wear.wardrobe.dao.WardrobeOracleDAO;
import com.samyukgu.what2wear.wardrobe.model.Wardrobe;
import com.samyukgu.what2wear.wardrobe.service.WardrobeService;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.ByteArrayInputStream;
import java.net.URL;
import java.util.ResourceBundle;

public class DetailWardrobeController implements Initializable {
    @FXML private ImageView imageView;
    @FXML private Label nameLabel;
    @FXML private Label categoryLabel;
    @FXML private Label sizeLabel;
    @FXML private Label brandLabel;
    @FXML private Label colorLabel;
    @FXML private Label keywordLabel;
    @FXML private Label memoLabel;

    private final WardrobeService wardrobeService = new WardrobeService(new WardrobeOracleDAO());
    private Wardrobe currentWardrobe;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        loadWardrobeDetail();
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
            // 이름 설정
            nameLabel.setText(wardrobe.getName() != null ? wardrobe.getName() : "이름 없음");
            // 카테고리 설정 (ID를 한글로 변환)
            // 옷 db에는 카테고리 id만 있기 때문에 변환을 해줘야함
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

        } catch (Exception e) {
            showError("옷 정보 표시 중 오류가 발생했습니다: " + e.getMessage());
        }
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
    @FXML
    private void handleDelete() {
        if (currentWardrobe == null) {
            showError("삭제할 옷 정보가 없습니다.");
            return;
        }

        // 로그인 정보가 필요할지 고민... sql은 해당 멤머만 삭제 가능하도록 코딩을 했는데
        // 옷 삭제하기 위해서는 조회 -> 상세 -> 삭제 흐름으로 가야함
        // 일단 회원 정보 있을때만 삭제 가능하도록 함
        Member loginMember = MemberSession.getLoginMember();
        if (loginMember == null) {
            showError("로그인이 필요합니다.");
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION,
                "정말 삭제하시겠습니까?", ButtonType.OK, ButtonType.CANCEL);
        alert.setTitle("삭제 확인");
        alert.setHeaderText(null);

        alert.showAndWait().ifPresent(result -> {
            if (result == ButtonType.OK) {
                try {
                    wardrobeService.deleteWardrobe(currentWardrobe.getId(), currentWardrobe.getMemberId());
                    showAlert("삭제되었습니다.");
                    // 데이터 정리
                    WardrobeDetailData.clearSelectedWardrobe();
                    // 목록 페이지로 돌아가기
                    MainLayoutController.loadView("/com/samyukgu/what2wear/wardrobe/wardrobeList.fxml");

                } catch (Exception e) {
                    showError("삭제 중 오류가 발생했습니다: " + e.getMessage());
                }
            }
        });
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

