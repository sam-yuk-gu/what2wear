package com.samyukgu.what2wear.friend.controller;

import com.samyukgu.what2wear.common.controller.CustomModalController;
import com.samyukgu.what2wear.common.util.CircularImageUtil;
import com.samyukgu.what2wear.di.DIContainer;
import com.samyukgu.what2wear.friend.service.FriendService;
import com.samyukgu.what2wear.member.Session.MemberSession;
import com.samyukgu.what2wear.member.model.Member;
import com.samyukgu.what2wear.member.service.MemberService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class FriendSearchController {

    @FXML public StackPane root;
    @FXML Button addFriendButton;
    @FXML private TextField searchField;
    @FXML private VBox searchResultsArea;
    @FXML private ImageView selectedMemberProfileImg;
    @FXML private Label SelectedMemberNickname;

    private List<Member> searchResults;
    private HBox selectedUserBox;
    private Member selectedMember;

    private MemberService memberService;
    private MemberSession memberSession;
    private FriendService friendService;
    private Member member;

    @FXML
    public void initialize() {
        // 테스트용 검색 결과 데이터 생성
        setupList();
        setupDI();
        loadSession();
        renderSearchResults();
        setupEventHandlers();
        hideUserInfoArea();
    }

    /**
     * 테스트용 검색 결과 데이터를 생성하는 메서드
     */
    private void createTestSearchData() {
        searchResults = new ArrayList<>();

        // 테스트용 닉네임 배열
        String[] testNicknames = {
                "김검색", "이찾기", "박결과", "최유저", "정회원",
                "한사용자", "서멤버", "임친구", "조동료", "윤버디",
                "김테스트", "이샘플", "박데모", "최예시", "정시험",
                "한실험", "서검증", "임확인", "조체크", "윤점검",
                "김가나다", "이라마바", "박사아자", "최차카타", "정파하"
        };

        // 로컬 이미지 파일을 byte 배열로 변환
//        byte[] testImageBytes = loadImageAsBytes("/assets/images/cute.jpg");
        byte[] testImageBytes = loadImageAsBytes("/assets/icons/defaultProfile.png");

        // 테스트 검색 결과 데이터 생성
        int resultCount = testNicknames.length;
        for (int i = 0; i < resultCount && i < testNicknames.length; i++) {
            Member searchResult = new Member();
            searchResult.setNickname(testNicknames[i]);
            searchResult.setProfile_img(testImageBytes);

            searchResults.add(searchResult);
        }
    }

    private void hideUserInfoArea() {
        selectedMemberProfileImg.setVisible(false);
        SelectedMemberNickname.setVisible(false);
        addFriendButton.setVisible(false);

        // 또는 텍스트를 안내 문구로 변경
        SelectedMemberNickname.setText("사용자를 선택해보세요!");
        SelectedMemberNickname.setVisible(true);
        SelectedMemberNickname.setStyle("-fx-text-fill: #999999;"); // 회색 텍스트
    }

    private void showUserInfoArea() {
        selectedMemberProfileImg.setVisible(true);
        SelectedMemberNickname.setVisible(true);
        addFriendButton.setVisible(true);
        SelectedMemberNickname.setStyle(""); // 기본 스타일로 복원
    }

    /**
     * 리소스 경로의 이미지 파일을 byte 배열로 변환하는 메서드
     */
    private byte[] loadImageAsBytes(String resourcePath) {
        try {
            // 리소스에서 InputStream 얻기
            InputStream inputStream = getClass().getResourceAsStream(resourcePath);
            if (inputStream == null) {
                System.err.println("이미지 리소스를 찾을 수 없습니다: " + resourcePath);
                return null;
            }

            // InputStream을 byte 배열로 변환
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int length;

            while ((length = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, length);
            }

            inputStream.close();
            outputStream.close();

            return outputStream.toByteArray();

        } catch (IOException e) {
            System.err.println("이미지 파일을 바이트 배열로 변환하는 중 오류 발생: " + e.getMessage());
            return null;
        }
    }

    /**
     * 검색 결과를 렌더링하는 메서드
     */
    private void renderSearchResults() {
        // 기존 검색 결과 제거
        searchResultsArea.getChildren().clear();

        // 멤버 검색 및
        searchResults = memberService.searchMember(memberSession.getMember().getId(), searchField.getText());
        for (Member result : searchResults) {
            HBox userBox = createUserSearchBox(result, result.getProfile_img());
            searchResultsArea.getChildren().add(userBox);
        }
    }

    /**
     * 개별 사용자 검색 결과 HBox를 생성하는 메서드
     */
    private HBox createUserSearchBox(Member member, byte[] imageBytes) {
        HBox userBox = new HBox();
        userBox.setAlignment(Pos.CENTER_LEFT);
        userBox.setPrefHeight(50.0);
        userBox.setSpacing(10.0);
//        userBox.setMaxWidth(Region.USE_PREF_SIZE); // 최대 너비 제한
        userBox.getStyleClass().add("search-user-info-area");

        // 패딩 설정
        userBox.setStyle("-fx-padding: 0 20 0 20;");

        // 프로필 이미지 생성
        ImageView profileImage = CircularImageUtil.createCircularImageFromBytes(30, imageBytes);

        // 닉네임 라벨 생성
        Label nicknameLabel = new Label(member.getNickname());

        // HBox에 요소들 추가
        userBox.getChildren().addAll(profileImage, nicknameLabel);

        // 클릭 이벤트 추가
        userBox.setOnMouseClicked(event -> {
            handleUserSelection(member, userBox);
        });

        // 마우스 호버 효과 (선택적)
        userBox.setOnMouseEntered(event -> {
            if (selectedUserBox != userBox) {
                userBox.setStyle("-fx-padding: 0 20 0 20; -fx-background-color: #f0f0f0;");
            }
        });

        userBox.setOnMouseExited(event -> {
            if (selectedUserBox != userBox) {
                userBox.setStyle("-fx-padding: 0 20 0 20;");
            }
        });

        // todo: 만약 이미 친구관계인 사용자라면 친구 요청 버튼 비활성화(서비스 로직으로 검증하기)
        checkAndUpdateFriendButtonState(member);

        return userBox;
    }
    
    // 친구 추가 버튼 최신화 메서드
    private void checkAndUpdateFriendButtonState(Member member) {
        if(this.member == null)
            return;

        boolean isAlreadyFriend = friendService.isFriend(this.member.getId(), member.getId());
        boolean hasPendingRequest = friendService.isRequestPending(this.member.getId(), member.getId());

        if (isAlreadyFriend) {
            addFriendButton.setText("이미 친구입니다");
            addFriendButton.setDisable(true);
            addFriendButton.getStyleClass().removeAll("btn-green");
            addFriendButton.getStyleClass().add("btn-gray");
        } else if (hasPendingRequest) {
            addFriendButton.setText("요청 대기중");
            addFriendButton.setDisable(true);
            addFriendButton.getStyleClass().removeAll("btn-green");
            addFriendButton.getStyleClass().add("btn-gray");
        } else {
            addFriendButton.setText("친구 요청");
            addFriendButton.setDisable(false);
            addFriendButton.getStyleClass().removeAll("btn-gray");
            addFriendButton.getStyleClass().add("btn-green");
        }
    }

    /**
     * 사용자 선택 시 처리하는 메서드
     */
    private void handleUserSelection(Member selected, HBox userBox) {
        // 이전에 선택된 사용자가 있다면 선택 스타일 제거
        if (selectedUserBox != null) {
            selectedUserBox.getStyleClass().remove("selected-search-user");
            selectedUserBox.setStyle("-fx-padding: 0 20 0 20;");
        }

        // 새로 선택된 사용자에 선택 스타일 추가
        userBox.getStyleClass().add("selected-search-user");
        userBox.setStyle("-fx-padding: 0 20 0 20; -fx-background-color: #E3F2FD;");

        // 사용자 정보 영역 보이게 하기
        showUserInfoArea();

        // 현재 선택된 사용자 HBox 및 Member 업데이트
        selectedUserBox = userBox;
        selectedMember = selected;

        // 오른쪽 영역에 선택된 사용자 정보 표시
        updateSelectedMemberDisplay(selected);

        checkAndUpdateFriendButtonState(selected);
        System.out.println("선택된 사용자: " + selected.getNickname());
    }

    /**
     * 선택된 회원 정보를 오른쪽 영역에 표시하는 메서드
     */
    private void updateSelectedMemberDisplay(Member member) {
        // 닉네임 업데이트
        SelectedMemberNickname.setText(member.getNickname());

        // 원형 프로필 이미지 업데이트
        try {
            if (member.getProfile_img() != null && member.getProfile_img().length > 0) {
                // 원형 이미지로 변경
                CircularImageUtil.makeImageViewCircular(selectedMemberProfileImg, 150);

                ByteArrayInputStream inputStream = new ByteArrayInputStream(member.getProfile_img());
                Image profileImage = new Image(inputStream);
                selectedMemberProfileImg.setImage(profileImage);
                inputStream.close();
            } else {
                // 기본 이미지도 원형으로 설정
                CircularImageUtil.makeImageViewCircular(selectedMemberProfileImg, 150);
                String defaultImagePath = getClass().getResource("/assets/icons/defaultProfile.png").toExternalForm();
                Image defaultImage = new Image(defaultImagePath);
                selectedMemberProfileImg.setImage(defaultImage);
            }
        } catch (Exception e) {
            System.err.println("선택된 회원 프로필 이미지 로드 실패: " + e.getMessage());
            try {
                CircularImageUtil.makeImageViewCircular(selectedMemberProfileImg, 150);
                String defaultImagePath = getClass().getResource("/assets/icons/defaultProfile.png").toExternalForm();
                Image defaultImage = new Image(defaultImagePath);
                selectedMemberProfileImg.setImage(defaultImage);
            } catch (Exception ex) {
                System.err.println("기본 이미지도 로드 실패");
            }
        }
    }

    /**
     * 검색어에 따라 결과를 필터링하는 메서드 (선택적 구현)
     */
    private void filterSearchResults(String searchText) {
        if (searchText == null || searchText.trim().isEmpty()) {
            renderSearchResults();
            return;
        }

        // 검색 결과 필터링
        searchResultsArea.getChildren().clear();

        for (Member result : searchResults) {
            if (result.getNickname().toLowerCase().contains(searchText.toLowerCase())) {
                HBox userBox = createUserSearchBox(result, result.getProfile_img());
                searchResultsArea.getChildren().add(userBox);
            }
        }
    }

    /**
     * 친구 추가 버튼 클릭 시 처리하는 메서드 (FXML에서 호출)
     */
    @FXML
    private void handleAddFriendAction() {
        if (selectedMember != null) {
            friendService.addFriend(member.getId(), selectedMember.getId());
            showConfirmationModal();
            // 성공 메시지 표시 등 추가 UI 업데이트
        } else {
            System.out.println("선택된 회원이 없습니다.");
        }
    }

    /**
     * 검색 버튼 클릭 시 처리하는 메서드 (FXML에서 호출)
     */
    @FXML
    private void handleSearchAction() {
        String searchText = searchField.getText();
        filterSearchResults(searchText);
    }
    
    // 친구 요청 모달
    private void showConfirmationModal() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/samyukgu/what2wear/common/CustomModal.fxml"));
            StackPane modal = loader.load();

            CustomModalController controller = loader.getController();
            controller.configure(
                    "성공",
                    "친구 요청이 완료되었습니다!",
                    "/assets/icons/greenCheck.png",
                    "#4CAF50",
                    "확인",
                    () -> root.getChildren().remove(modal)
            );

            root.getChildren().add(modal);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setupEventHandlers(){
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            filterSearchResults(newValue);
        });
    }

    private void setupDI(){
        DIContainer diContainer = DIContainer.getInstance();
        this.memberService = diContainer.resolve(MemberService.class);
        this.memberSession = diContainer.resolve(MemberSession.class);
        this.friendService = diContainer.resolve(FriendService.class);
    }

    private void loadSession(){
        member = memberSession.getMember();
    }

    private void setupList(){
        searchResults = new ArrayList<>();
    }
}