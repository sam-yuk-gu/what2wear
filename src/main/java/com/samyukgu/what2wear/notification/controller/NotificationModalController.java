package com.samyukgu.what2wear.notification.controller;

import com.samyukgu.what2wear.common.util.CircularImageUtil;
import com.samyukgu.what2wear.di.DIContainer;
import com.samyukgu.what2wear.member.Session.MemberSession;
import com.samyukgu.what2wear.member.model.Member;
import com.samyukgu.what2wear.notification.service.NotificationService;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import java.util.List;

public class NotificationModalController {

    @FXML private StackPane modalOverlay;
    @FXML private VBox modalContainer;
    @FXML private HBox friendRequestRow;  // 템플릿용 HBox (나중에 숨김처리)
    @FXML private Label templateLabel;
    @FXML private Button acceptButton;
    @FXML private Button rejectButton;

    private NotificationService notificationService;
    private MemberSession memberSession;
    private Member currentMember;
    private List<Member> friendRequests;
    private Runnable closeCallback;  // 모달 닫기 콜백

    @FXML
    public void initialize(){
        setupDI();
        loadMember();
        // 템플릿 HBox를 숨김처리 (실제 요청 목록으로 대체)
        friendRequestRow.setVisible(false);
        friendRequestRow.setManaged(false);
    }

    // 메인 레이아웃에서 친구 요청 목록을 설정하는 메서드
    public void setFriendRequests(List<Member> requests) {
        this.friendRequests = requests;
        displayFriendRequests();
    }

    // 모달 닫기 콜백 설정
    public void setCloseCallback(Runnable callback) {
        this.closeCallback = callback;
    }

    // 친구 요청 목록을 UI에 표시
    private void displayFriendRequests() {
        if (friendRequests == null || friendRequests.isEmpty()) {
            // 요청이 없을 경우 - 가운데 정렬된 컨테이너 생성
            VBox noRequestContainer = new VBox();
            noRequestContainer.setAlignment(javafx.geometry.Pos.CENTER);
            noRequestContainer.setPrefHeight(100);
            noRequestContainer.setStyle("-fx-padding: 20px;");

            Label noRequestLabel = new Label("친구 요청이 존재하지 않습니다.");
            noRequestLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #666666;");
            noRequestLabel.getStyleClass().add("bold-text");

            noRequestContainer.getChildren().add(noRequestLabel);
            modalContainer.getChildren().add(noRequestContainer);
            return;
        }

        // 각 친구 요청에 대해 HBox 생성
        for (Member requester : friendRequests) {
            HBox requestRow = createFriendRequestRow(requester);
            modalContainer.getChildren().add(requestRow);
        }
    }

    // 개별 친구 요청 HBox 생성
    private HBox createFriendRequestRow(Member requester) {
        HBox requestRow = new HBox();
        requestRow.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
        requestRow.setSpacing(15);
        requestRow.setPrefHeight(50);
        requestRow.setStyle("-fx-padding: 10px; -fx-border-color: #e0e0e0; -fx-border-width: 0 0 1 0;");

        // 사용자 프로필 이미지
        ImageView circularImageFromBytes = CircularImageUtil.createCircularImageFromBytes(30.0,
                requester.getProfile_img());
//        CircularImageUtil.makeImageViewCircular(만들어진 이미지 뷰, 50.0);

        // 요청자 정보 라벨
        Label requesterLabel = new Label(requester.getNickname());
        requesterLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");
        requesterLabel.setPrefWidth(200);

        // 수락 버튼
        Button acceptBtn = new Button("수락");
        acceptBtn.setStyle("-fx-font-size: 14px");
        acceptBtn.getStyleClass().add("btn-green");
        acceptBtn.getStyleClass().add("semi-bold-text");
        acceptBtn.setOnAction(e -> handleAcceptRequest(requester));

// 거부 버튼
        Button rejectBtn = new Button("거절");
        rejectBtn.setStyle("-fx-font-size: 14px");
        rejectBtn.getStyleClass().add("btn-red");
        rejectBtn.getStyleClass().add("semi-bold-text");
        rejectBtn.setOnAction(e -> handleRejectRequest(requester));

// 버튼 그룹 HBox 생성
        HBox buttonGroup = new HBox();
        buttonGroup.setSpacing(5); // 버튼 사이 간격을 5px로 설정
        buttonGroup.getChildren().addAll(acceptBtn, rejectBtn);

// Spacer 추가 (라벨과 버튼그룹 사이 공간)
        javafx.scene.layout.Region spacer = new javafx.scene.layout.Region();
        HBox.setHgrow(spacer, javafx.scene.layout.Priority.ALWAYS);

        requestRow.getChildren().addAll(circularImageFromBytes, requesterLabel, spacer, buttonGroup);
        requestRow.setUserData(requester);

        return requestRow;
    }

    // 친구 요청 수락
    private void handleAcceptRequest(Member requester) {
        try {
            notificationService.acceptRequest(currentMember.getId(), requester.getId());

            // UI에서 해당 요청 제거
            removeFriendRequestFromUI(requester);

            System.out.println("친구 요청 수락: " + requester.getNickname());
        } catch (Exception e) {
            e.printStackTrace();
            // 에러 처리 로직 추가 가능
        }
    }

    // 친구 요청 거부
    private void handleRejectRequest(Member requester) {
        try {
            notificationService.rejectRequest(currentMember.getId(), requester.getId());

            // UI에서 해당 요청 제거
            removeFriendRequestFromUI(requester);

            System.out.println("친구 요청 거부: " + requester.getNickname());
        } catch (Exception e) {
            e.printStackTrace();
            // 에러 처리 로직 추가 가능
        }
    }

    private void removeFriendRequestFromUI(Member requester) {
        // userData를 사용해서 해당 요청자의 HBox를 찾아서 제거
        modalContainer.getChildren().removeIf(node -> {
            if (node instanceof HBox) {
                HBox hbox = (HBox) node;
                Member storedMember = (Member) hbox.getUserData();
                return storedMember != null && storedMember.getId().equals(requester.getId());
            }
            return false;
        });

        // 친구 요청 목록에서도 제거
        friendRequests.remove(requester);

        // 모든 요청이 처리되었으면 "모든 친구 요청이 처리되었습니다" 메시지 표시
        if (friendRequests.isEmpty()) {
            VBox completedContainer = new VBox();
            completedContainer.setAlignment(javafx.geometry.Pos.CENTER);
            completedContainer.setPrefHeight(100);
            completedContainer.setStyle("-fx-padding: 20px;");

            Label completedLabel = new Label("모든 친구 요청이 처리되었습니다.");
            completedLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #666666;");
            completedLabel.getStyleClass().add("bold-text");

            completedContainer.getChildren().add(completedLabel);
            modalContainer.getChildren().add(completedContainer);
        }
    }

    // 모달 닫기
    @FXML
    private void closeModal() {
        if (closeCallback != null) {
            closeCallback.run();
        }
    }

    // 컨테이너에 있는 인스턴스 멤버로 할당
    private void setupDI() {
        DIContainer diContainer = DIContainer.getInstance();
        notificationService = diContainer.resolve(NotificationService.class);
        memberSession = diContainer.resolve(MemberSession.class);
    }

    private void loadMember(){
        currentMember = memberSession.getMember();
    }
}