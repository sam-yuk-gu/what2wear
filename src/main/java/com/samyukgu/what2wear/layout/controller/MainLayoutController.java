package com.samyukgu.what2wear.layout.controller;

import com.samyukgu.what2wear.codi.controller.CodiAddController;
import com.samyukgu.what2wear.codi.controller.CodiEditController;
import com.samyukgu.what2wear.di.DIContainer;
import com.samyukgu.what2wear.member.Session.MemberSession;
import com.samyukgu.what2wear.member.model.Member;
import com.samyukgu.what2wear.post.controller.DetailPostController;
import com.samyukgu.what2wear.post.controller.EditPostController;
import com.samyukgu.what2wear.post.model.Post;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import lombok.Getter;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import javafx.stage.Stage;

public class MainLayoutController {

    @FXML private Button logoutButton;
    @FXML private StackPane contentArea;
    @FXML private Region spacer;

    @FXML private Button logoButton;
    @FXML private Button wardrobeButton;
    @FXML private Button friendButton;
    @FXML private Button boardButton;
    @FXML private Button mypageButton;


    // MainLayoutController.getInstance() 접근을 위한 getInstance() 정의
    @Getter
    private static MainLayoutController instance;
    private Button currentSelectedButton;
    private MemberSession memberSession;

    // 하이라이트 대상 버튼 목록 (logoButton 제외)
    private List<Button> menuButtons;

    @FXML
    public void initialize() {
        instance = this;
        setupDI();
        menuButtons = List.of(wardrobeButton, friendButton, boardButton, mypageButton);   // 버튼 리스트 초기화 (로고 제외)
        loadView("/com/samyukgu/what2wear/codi/CodiMainView.fxml");
        VBox.setVgrow(spacer, Priority.ALWAYS);     // 최대 여백 설정
    }

    public static void loadView(String fxmlPath) {
        try {
            Parent view = FXMLLoader.load(Objects.requireNonNull(MainLayoutController.class.getResource(fxmlPath)));
            instance.contentArea.getChildren().setAll(view);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // PostDetailController에 저장한 게시글 데이터 설정
    public static void loadPostDetailView(Post post) {
        try {
            FXMLLoader loader = new FXMLLoader(MainLayoutController.class.getResource("/com/samyukgu/what2wear/post/DetailPost.fxml"));
            Parent view = loader.load();

            // Controller 가져와서 게시글 데이터 주입
            DetailPostController controller = loader.getController();
            controller.setPostData(post);

            // 메인 화면에 해당 뷰 출력
            instance.contentArea.getChildren().setAll(view);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // PostEditController에 저장한 게시글 데이터 설정
    public static void loadEditPostView(Post post) {
        try {
            FXMLLoader loader = new FXMLLoader(MainLayoutController.class.getResource("/com/samyukgu/what2wear/post/EditPost.fxml"));
            Parent view = loader.load();

            EditPostController controller = loader.getController();
            controller.setPostData(post); // post 데이터 넘겨줌

            instance.contentArea.getChildren().setAll(view);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void loadEditCodiView(Long codiId) {
        try {
            FXMLLoader loader = new FXMLLoader(MainLayoutController.class.getResource("/com/samyukgu/what2wear/codi/CodiEditView.fxml"));
            Parent view = loader.load();

            CodiEditController controller = loader.getController();
            controller.setEditMode(codiId);

            instance.contentArea.getChildren().setAll(view);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // 로고 버튼은 하이라이트 제외, 나머지는 선택 상태 갱신
    private void selectMenu(Button selectedButton) {
        for (Button button : menuButtons) {
            button.getStyleClass().remove("selected");
        }

        if (menuButtons.contains(selectedButton)) {
            selectedButton.getStyleClass().add("selected");
            currentSelectedButton = selectedButton;
        } else {
            currentSelectedButton = null;
        }
    }

    @FXML
    private void handleClickCodi() {
        selectMenu(logoButton); // 모든 하이라이트 제거, 자신은 X
        loadView("/com/samyukgu/what2wear/codi/CodiMainView.fxml");
    }

    @FXML
    private void handleClickFriend() {
        selectMenu(friendButton);
        loadView("/com/samyukgu/what2wear/friend/FriendMainView.fxml");
    }

    @FXML
    private void handleClickWardrobe() {
        selectMenu(wardrobeButton);
        loadView("/com/samyukgu/what2wear/wardrobe/wardrobeList.fxml");
    }

    // 게시판 탭 연동
    @FXML
    private void handleClickBoard() {
        selectMenu(boardButton); // 버튼 하이라이트 처리
        loadView("/com/samyukgu/what2wear/post/ListPost.fxml");
    }

    // 마이페이지 탭 연동
    @FXML
    private void handleClickMyPage() {
        selectMenu(mypageButton); // 버튼 하이라이트 처리
        loadView("/com/samyukgu/what2wear/member/MyPageView.fxml");
    }

    // 로그아웃 탭 연동
    @FXML
    private void handleClickLogout() {
        showConfirmationModal();
    }

    // DI로 로그인 세션 가져오기
    private void setupDI(){
        memberSession = DIContainer.getInstance().resolve(MemberSession.class);
    }

    private void showConfirmationModal() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/samyukgu/what2wear/common/CustomModal.fxml"));
            StackPane modal = loader.load();

            CustomModalController controller = loader.getController();
            controller.configure(
                    "로그아웃",
                    "로그아웃 하시겠습니까?",
                    "/assets/icons/redCheck.png",
                    "#FA7B7F",
                    "취소",
                    "확인",
                    () -> contentArea.getChildren().remove(modal),  // root → contentArea
                    () -> {
                        contentArea.getChildren().remove(modal);    // root → contentArea
                        memberSession.clearMember();
                        switchScene("/com/samyukgu/what2wear/member/LoginView.fxml", "로그인");
                    }
            );

            contentArea.getChildren().add(modal);  // root → contentArea

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void switchScene(String fxmlPath, String title){
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent newRoot = loader.load();

            // 현재 Stage 가져오기 (기존 화면에서) - contentArea 사용
            Stage stage = (Stage) this.contentArea.getScene().getWindow();
            Scene scene = new Scene(newRoot, 1280, 768);
            stage.setScene(scene);
            stage.setTitle(title);
            stage.setResizable(false);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
