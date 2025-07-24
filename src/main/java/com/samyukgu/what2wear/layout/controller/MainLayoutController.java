package com.samyukgu.what2wear.layout.controller;

import com.samyukgu.what2wear.codi.controller.CodiAddController;
import com.samyukgu.what2wear.codi.controller.CodiEditController;
import com.samyukgu.what2wear.common.controller.CustomModalController;
import com.samyukgu.what2wear.di.DIContainer;
import com.samyukgu.what2wear.member.Session.MemberSession;
import com.samyukgu.what2wear.member.model.Member;
import com.samyukgu.what2wear.notification.controller.NotificationModalController;
import com.samyukgu.what2wear.notification.service.NotificationService;
import com.samyukgu.what2wear.post.controller.DetailPostController;
import com.samyukgu.what2wear.post.controller.EditPostController;
import com.samyukgu.what2wear.post.model.Post;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
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

    @FXML private Button notificationButton;
    @FXML private Button logoutButton;
    @FXML private StackPane contentArea;
    @FXML private Region spacer;

    @FXML private Button logoButton;
    @FXML private Button wardrobeButton;
    @FXML private Button friendButton;
    @FXML private Button boardButton;
    @FXML private Button mypageButton;

    private ImageView notificationBadge;

    // MainLayoutController.getInstance() 접근을 위한 getInstance() 정의
    @Getter
    private static MainLayoutController instance;
    private Button currentSelectedButton;
    private MemberSession memberSession;
    private NotificationService notificationService;

    // 하이라이트 대상 버튼 목록 (logoButton 제외)
    private List<Button> menuButtons;

    @FXML
    public void initialize() {
        instance = this;
        setupDI();
        menuButtons = List.of(wardrobeButton, friendButton, boardButton, mypageButton);   // 버튼 리스트 초기화 (로고 제외)
        loadView("/com/samyukgu/what2wear/codi/CodiMainView.fxml");
        initializeNotificationBadge();
        updateNotificationBadge();
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

    // 알림 배지 초기화
    // 알림 배지 초기화 (이미지 버전) - 더 간단한 방법
    private void initializeNotificationBadge() {
        try {
            // 빨간 점 이미지 로드
            Image redDotImage = new javafx.scene.image.Image(
                    Objects.requireNonNull(getClass().getResourceAsStream("/assets/icons/redDot.png"))
            );

            notificationBadge = new ImageView(redDotImage);
            notificationBadge.setVisible(false); // 초기에는 숨김

            // 부모 컨테이너 확인
            javafx.scene.Parent parent = notificationButton.getParent();

            javafx.scene.layout.HBox hbox = (javafx.scene.layout.HBox) parent;
            int buttonIndex = hbox.getChildren().indexOf(notificationButton);
            hbox.getChildren().add(buttonIndex + 1, notificationBadge);
            // 위치 미세 조정
            notificationBadge.setTranslateX(-15); // 버튼과 겹치게
            notificationBadge.setTranslateY(15);  // 위쪽으로

            System.out.println("알림 배지 이미지 초기화 완료");
        } catch (Exception e) {
            System.out.println("알림 배지 이미지 로드 실패: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // 알림 버튼을 StackPane으로 감싸는 메서드 (이미지 버전)
    private void wrapNotificationButtonWithStackPane() {
        javafx.scene.Parent parent = notificationButton.getParent();

        if (parent instanceof javafx.scene.layout.HBox) {
            javafx.scene.layout.HBox hbox = (javafx.scene.layout.HBox) parent;
            int buttonIndex = hbox.getChildren().indexOf(notificationButton);

            // 기존 버튼 제거
            hbox.getChildren().remove(notificationButton);

            // StackPane으로 감싸기
            javafx.scene.layout.StackPane stackPane = new javafx.scene.layout.StackPane();
            stackPane.getChildren().add(notificationButton);
            stackPane.getChildren().add(notificationBadge);

            // 배지 위치 설정
            javafx.scene.layout.StackPane.setAlignment(notificationBadge, javafx.geometry.Pos.TOP_RIGHT);
            notificationBadge.setTranslateX(-3);
            notificationBadge.setTranslateY(3);

            // StackPane을 원래 위치에 추가
            hbox.getChildren().add(buttonIndex, stackPane);
        }
    }

    // 알림 배지 업데이트 메서드
    private void updateNotificationBadge() {
        try {
            List<Member> currentRequests = notificationService.getRequests(memberSession.getMember().getId());
            boolean hasNotifications = currentRequests != null && !currentRequests.isEmpty();

            if (notificationBadge != null) {
                notificationBadge.setVisible(hasNotifications);
            }
        } catch (Exception e) {
            e.printStackTrace();
            if (notificationBadge != null) {
                notificationBadge.setVisible(false);
            }
        }
    }

    // 기존 handleClickNotification 메서드 수정
    @FXML
    private void handleClickNotification() {
        showNotificationModal();
        // 알림 모달을 열면 배지 숨기기
    }

    // 알림 모달 표시 메서드 수정
    private void showNotificationModal() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/samyukgu/what2wear/notification/NotificationModal.fxml"));
            StackPane modal = loader.load();

            NotificationModalController controller = loader.getController();

            controller.setCloseCallback(() -> {
                contentArea.getChildren().remove(modal);
                updateNotificationBadge(); // 모달 닫을 때 알림 상태 재확인
            });

            List<Member> currentRequests = notificationService.getRequests(memberSession.getMember().getId());
            controller.setFriendRequests(currentRequests);

            contentArea.getChildren().add(modal);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // static 메서드로도 접근 가능하게
    public static void updateNotificationStatus() {
        if (instance != null) {
            instance.updateNotificationBadge();
        }
    }

    // DI로 로그인 세션 가져오기
    private void setupDI(){
        memberSession = DIContainer.getInstance().resolve(MemberSession.class);
        notificationService = DIContainer.getInstance().resolve(NotificationService.class);
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
