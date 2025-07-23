package com.samyukgu.what2wear.friend.controller;

import static com.samyukgu.what2wear.common.util.FxStyleUtil.applyHoverTransition;

import com.samyukgu.what2wear.codi.dto.DummyCodiDTO;
import com.samyukgu.what2wear.codi.model.CodiItem;
import com.samyukgu.what2wear.codi.model.CodiScope;
import com.samyukgu.what2wear.codi.service.DummyScheduleRepository;
import com.samyukgu.what2wear.layout.controller.MainLayoutController;
import com.samyukgu.what2wear.common.util.CircularImageUtil;
import com.samyukgu.what2wear.di.DIContainer;
import com.samyukgu.what2wear.friend.service.FriendService;
import com.samyukgu.what2wear.member.Session.MemberSession;
import com.samyukgu.what2wear.member.model.Member;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class FriendMainController {
    private LocalDate currentDateSelected;

    @FXML private Button searchButton;
    @FXML private Label monthLabel;
    @FXML private GridPane calendarGrid;
    @FXML private Label emptyLabel;
    @FXML private VBox scheduleListContainer;
    @FXML private Button addButton;
    @FXML private HBox friendListArea;

    private LocalDate currentDate;
    private Map<LocalDate, List<DummyCodiDTO>> scheduleMap; // 날짜별 일정 정보 저장
    private List<Member> friendList;
    private MemberSession memberSession;
    private FriendService friendService;
    private VBox selectedFriendBox;
    private Member member;

    @FXML
    public void initialize() {
        currentDate = LocalDate.now();
        currentDateSelected = currentDate;
        loadScheduleForMonth(currentDate);
        renderCalendar(currentDate);
        showScheduleDetail(currentDateSelected);
        setupDI();
        loadMember();
        applyHoverTransition(addButton, Color.web("#F2FBFF"), Color.web("#E0F6FF"));

        // 테스트용 친구 데이터 생성
//        createTestFriendData();
        loadFriendList();
    }

    /**
     * 테스트용 친구 데이터를 생성하는 메서드
     */
    private void createTestFriendData() {
        friendList = new ArrayList<>();

        // 테스트용 닉네임 배열
        String[] testNicknames = {
                "김아무개", "이친구", "박동료", "최베프", "정단짝",
                "한동반", "서파트너", "임메이트", "조친구", "윤버디",
                "김아무개", "이친구", "박동료", "최베프", "정단짝",
                "한동반", "서파트너", "임메이트", "조친구", "윤버디",
                "한동반", "서파트너", "임메이트", "조친구", "윤버디"
        };

        // 로컬 이미지 파일을 byte 배열로 변환
        byte[] testImageBytes = loadImageAsBytes("/assets/images/cute.jpg");
//        byte[] testImageBytes = loadImageAsBytes("/assets/icons/defaultProfile.png");

        // 테스트 친구 데이터 생성 (더 많은 친구로 스크롤 테스트)
        int friendCount = testNicknames.length; // 스크롤 테스트를 위해 더 많은 친구 생성
        for (int i = 0; i < friendCount && i < testNicknames.length; i++) {
            Member testFriend = new Member();
            testFriend.setNickname(testNicknames[i]);
            // profile_img는 byte[] 타입이므로 바이트 배열 설정
            testFriend.setProfile_img(testImageBytes);

            friendList.add(testFriend);
        }
    }

    /**
     * todo: 테스트 멤버 메서드 지울 때 같이 지우기
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

    private void loadFriendList() {
        if (friendList == null) {
            friendList = new ArrayList<>();
        }
        friendList = friendService.getFriends(member.getId());

        renderFriendList();
    }

    private void renderFriendList() {
        // 기존 친구 목록 제거 (새로고침 시 중복 방지)
        friendListArea.getChildren().clear();

        if(friendList == null || friendList.isEmpty()){
            showEmptyFriendMessage();
            return;
        }

        boolean isFirstFriend = true;
        for (Member friend : friendList) {
            // Member의 profile_img가 byte[] 타입이므로 바이트 배열 버전 사용
            VBox friendBox = createFriendBoxWithBytes(friend, friend.getProfile_img());
            friendListArea.getChildren().add(friendBox);

            if (isFirstFriend) {
                handleFriendSelection(friend, friendBox);
                isFirstFriend = false;
            }
        }

        // 가로 스크롤 설정
        setupHorizontalScroll();
    }


    /**
     * HBox에 가로 스크롤 기능을 추가하는 메서드
     */
    private void setupHorizontalScroll() {
        // friendListArea가 ScrollPane 안에 있지 않다면 ScrollPane으로 감싸기
        if (!(friendListArea.getParent() instanceof ScrollPane)) {
            // 기존 부모에서 friendListArea 제거
            Parent parent = friendListArea.getParent();

            if (parent instanceof Pane) {
                ((Pane) parent).getChildren().remove(friendListArea);

                // ScrollPane 생성 및 설정
                ScrollPane scrollPane = new ScrollPane();
                scrollPane.setContent(friendListArea);
                scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED); // 필요시 가로 스크롤바 표시
                scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER); // 세로 스크롤바 숨김
                scrollPane.setFitToHeight(true); // 높이에 맞춤
                scrollPane.setPrefHeight(friendListArea.getPrefHeight());
                scrollPane.setPrefWidth(friendListArea.getPrefWidth());

                // ScrollPane을 원래 부모에 추가
                ((Pane) parent).getChildren().add(scrollPane);
            }
        }
    }

    /**
     * FXML에서 ScrollPane을 직접 사용하는 경우의 대안 방법
     *
     * FXML 구조를 다음과 같이 변경:
     * <ScrollPane hbarPolicy="AS_NEEDED" vbarPolicy="NEVER" fitToHeight="true"
     *             prefHeight="76.0" prefWidth="1280.0" styleClass="bg-friend-area">
     *    <content>
     *       <HBox fx:id="friendListArea" prefHeight="76.0" styleClass="bg-friend-area">
     *       </HBox>
     *    </content>
     * </ScrollPane>
     */
    private void renderFriendListWithFXMLScrollPane() {
        // 기존 친구 목록 제거 (새로고침 시 중복 방지)
        friendListArea.getChildren().clear();

        for (Member friend : friendList) {
            // Member의 profile_img가 byte[] 타입이므로 바이트 배열 버전 사용
            VBox friendBox = createFriendBoxWithBytes(friend, friend.getProfile_img());
            friendListArea.getChildren().add(friendBox);
        }

        // FXML에서 이미 ScrollPane으로 감싸져 있다면 추가 설정 불필요
    }

    /**
     * 개별 친구 VBox를 생성하는 메서드 (바이트 배열용)
     */
    private VBox createFriendBoxWithBytes(Member friend, byte[] imageBytes) {
        VBox friendBox = new VBox();
        friendBox.setAlignment(Pos.CENTER);
        friendBox.setPrefHeight(150.0);
        friendBox.setPrefWidth(70.0);
        // 기본 스타일 클래스 제거 - 클릭 시에만 추가

        // 프로필 이미지 생성 (바이트 배열 사용)
//        ImageView profileImage = createProfileImageFromBytes(imageBytes);
//        ImageView profileImage = ImgToImageView.createProfileImageFromBytes(45.0, imageBytes);
        ImageView profileImage = CircularImageUtil.createCircularImageFromBytes(45.0, imageBytes);

        // 닉네임 라벨 생성
        Label nicknameLabel = new Label(friend.getNickname());

        // VBox에 요소들 추가
        friendBox.getChildren().addAll(profileImage, nicknameLabel);

        // 클릭 이벤트 추가 (선택적)
        friendBox.setOnMouseClicked(event -> {
            handleFriendSelection(friend, friendBox);
        });

        return friendBox;
    }

    /**
     * 친구 선택 시 처리하는 메서드
     */
    private void handleFriendSelection(Member selectedFriend, VBox friendBox) {
        // 이전에 선택된 친구가 있다면 선택 스타일 제거
        if (selectedFriendBox != null) {
            selectedFriendBox.getStyleClass().remove("bg-selected-friend-area");
        }

        // 새로 선택된 친구에 선택 스타일 추가
        friendBox.getStyleClass().add("bg-selected-friend-area");

        // 현재 선택된 친구 VBox 업데이트
        selectedFriendBox = friendBox;

        // 친구 선택 시 수행할 작업
        System.out.println("선택된 친구: " + selectedFriend.getNickname());

        // 예: 다른 UI 업데이트 로직 추가
    }

    /**
     * 친구 목록을 업데이트하는 메서드 (외부에서 호출 가능)
     */
    public void updateFriendList(List<Member> newFriendList) {
        this.friendList = newFriendList;
        renderFriendList();
    }

    /**
     * 특정 친구를 추가하는 메서드
     */
    public void addFriend(Member newFriend) {
        if (friendList == null) {
            friendList = new ArrayList<>();
        }
        friendList.add(newFriend);
        renderFriendList();
    }

    /**
     * 특정 친구를 제거하는 메서드
     */
    public void removeFriend(Member friendToRemove) {
        if (friendList != null) {
            friendList.remove(friendToRemove);
            renderFriendList();
        }
    }

    private void loadScheduleForMonth(LocalDate month) {
        // scheduleMap = ScheduleService.getMonthlySchedule(month);
        scheduleMap = DummyScheduleRepository.getMonthlySchedule(month);
    }

    private void renderCalendar(LocalDate baseDate) {
        calendarGrid.getChildren().clear();

        String[] dayNames = {"일", "월", "화", "수", "목", "금", "토"};
        for (int i = 0; i < 7; i++) {
            Label dayHeader = new Label(dayNames[i]);
            dayHeader.getStyleClass().add("day-header");

            // 첫번째 행 border radius 처리
            if (i == 0) {
                dayHeader.getStyleClass().add("start-header");
            } else if (i == 6) {
                dayHeader.getStyleClass().add("end-header");
            }
            calendarGrid.add(dayHeader, i, 0);
        }

        int row = 1, col = 0;

        LocalDate firstDayOfMonth = baseDate.withDayOfMonth(1);
        int startDayOfWeek = firstDayOfMonth.getDayOfWeek().getValue() % 7;
        int daysInMonth = baseDate.lengthOfMonth();

        LocalDate prevMonth = baseDate.minusMonths(1);
        int daysInPrevMonth = prevMonth.lengthOfMonth();

        int totalSlots = startDayOfWeek + daysInMonth;
        int totalRows = (int) Math.ceil(totalSlots / 7.0);
        int totalCells = totalRows * 7;

        for (int i = 0; i < totalCells; i++) {
            VBox dayCell = new VBox();
            dayCell.getStyleClass().add("day-cell");

            // 마지막 행 border radius 처리
            if (col == 0 && row == totalRows) {
                dayCell.getStyleClass().add("left-bottom-cell");
            } else if (col == 6 && row == totalRows) {
                dayCell.getStyleClass().add("right-bottom-cell");
            }

            boolean isCurrentMonth;
            LocalDate currentDrawingDate;

            if (i < startDayOfWeek) {
                int day = daysInPrevMonth - startDayOfWeek + 1 + i;
                currentDrawingDate = prevMonth.withDayOfMonth(day);
                isCurrentMonth = false;
            } else if (i >= startDayOfWeek + daysInMonth) {
                int day = i - (startDayOfWeek + daysInMonth) + 1;
                currentDrawingDate = baseDate.plusMonths(1).withDayOfMonth(day);
                isCurrentMonth = false;
            } else {
                int day = i - startDayOfWeek + 1;
                currentDrawingDate = baseDate.withDayOfMonth(day);
                isCurrentMonth = true;
            }

            Label dayLabel = new Label(String.valueOf(currentDrawingDate.getDayOfMonth()));
            dayLabel.getStyleClass().add("day-circle"); // 날짜 프레임 스타일 적용

            if (currentDrawingDate.equals(currentDateSelected)) {
                dayLabel.getStyleClass().add("day-circle-selected"); // 선택된 날짜일 경우
            }

            if (!isCurrentMonth) {
                dayLabel.getStyleClass().add("outside-month");
            } else {
                dayLabel.getStyleClass().add("inside-month");
            }

            // 일정 점 렌더링
            if (scheduleMap.containsKey(currentDrawingDate)) {
                List<DummyCodiDTO> schedules = scheduleMap.get(currentDrawingDate);
                HBox dotsBox = new HBox();
                dotsBox.getStyleClass().add("dots-box");

                for (DummyCodiDTO schedule : schedules) {
                    Label dot = new Label("●");
                    dot.setStyle("-fx-font-size: 11;");

                    switch (schedule.getVisibility()) {
                        case PUBLIC -> dot.setStyle(dot.getStyle() + "-fx-text-fill: #60C0DB;");
                        case FRIENDS -> dot.setStyle(dot.getStyle() + "-fx-text-fill: #FFBB3C;");
                        case PRIVATE -> dot.setStyle(dot.getStyle() + "-fx-text-fill: #808080;");
                    }

                    dotsBox.getChildren().add(dot);
                }

                dayCell.getChildren().addAll(dayLabel, dotsBox);
            } else {
                Label dot = new Label(" ");
                dot.setStyle("-fx-font-size: 10; -fx-text-fill: transparent;");
                dayCell.getChildren().addAll(dayLabel, dot);
            }

            dayCell.setUserData(currentDrawingDate);

            final LocalDate dateForCell = currentDrawingDate;
            if (isCurrentMonth) {
                dayCell.setOnMouseClicked(e -> {
                    currentDateSelected = dateForCell;
                    renderCalendar(currentDate);
                    showScheduleDetail(dateForCell);
                });
                dayCell.setCursor(Cursor.HAND);
            }

            calendarGrid.add(dayCell, col, row);
            col++;
            if (col > 6) {
                col = 0;
                row++;
            }
        }

        monthLabel.setText(baseDate.getYear() + "년 " + baseDate.getMonthValue() + "월");
        highlightSelectedDay();
    }

    private void highlightSelectedDay() {
        if (currentDateSelected == null) return;

        for (Node node : calendarGrid.getChildren()) {
            if (!(node instanceof VBox dayCell)) continue;  // 요일 헤더는 건너뜀

            if (dayCell.getChildren().isEmpty()) continue;

            LocalDate cellDate = (LocalDate) dayCell.getUserData();
            Label label = (Label) dayCell.getChildren().getFirst();

            label.getStyleClass().remove("day-circle-selected");

            if (cellDate.equals(currentDateSelected)) {
                label.getStyleClass().add("day-circle-selected");
            } else {
                dayCell.setStyle(null);
                label.setStyle(null);
            }
        }
    }

    @FXML
    private void handlePrevMonth() {
        currentDate = currentDate.minusMonths(1);
        currentDateSelected = currentDate.withDayOfMonth(1);
        loadScheduleForMonth(currentDate);
        renderCalendar(currentDate);
        showScheduleDetail(currentDateSelected);
    }

    @FXML
    private void handleNextMonth() {
        currentDate = currentDate.plusMonths(1);
        currentDateSelected = currentDate.withDayOfMonth(1);
        loadScheduleForMonth(currentDate);
        renderCalendar(currentDate);
        showScheduleDetail(currentDateSelected);
    }

    // 팝업 css 연결 불가능: 메서드 내부에서 처리
    @FXML
    private void handleMonthLabelClick() {
        Stage popupStage = new Stage(StageStyle.UNDECORATED);
        popupStage.initModality(Modality.NONE); // 창 밖 클릭 허용
        popupStage.initOwner(monthLabel.getScene().getWindow());
        popupStage.setAlwaysOnTop(true);

        VBox popupContent = new VBox(30);
        popupContent.setPadding(new Insets(18));
        popupContent.setStyle(
                "-fx-background-color: white;" +
                        "-fx-background-radius: 20;" +
                        "-fx-border-color: #ECEAF3;" +
                        "-fx-border-radius: 20;" +
                        "-fx-pref-width: 140;" +
                        "-fx-alignment: center-right"
        );

        Label yearLabel = new Label("연도");
        yearLabel.setStyle(
                "-fx-font-size: 13;" +
                        "-fx-font-family: 'Pretendard SemiBold';" +
                        "-fx-text-fill: #323232;"
        );
        ComboBox<Integer> yearCombo = new ComboBox<>();
        yearCombo.setMaxWidth(Double.MAX_VALUE);
        yearCombo.setStyle(
                "-fx-pref-height: 30;" +
                        "-fx-background-color: white;" +
                        "-fx-border-color: #C4C4C4;" +
                        "-fx-border-radius: 8;" +
                        "-fx-background-radius: 8;" +
                        "-fx-font-size: 13;" +
                        "-fx-font-family: 'Pretendard Medium';" +
                        "-fx-text-fill: #323232;"
        );
        VBox yearBox = new VBox(10);
        yearBox.getChildren().addAll(yearLabel, yearCombo);

        Label monthLabel = new Label("월");
        monthLabel.setStyle(
                "-fx-font-size: 13;" +
                        "-fx-font-family: 'Pretendard SemiBold';" +
                        "-fx-text-fill: #323232;"
        );
        ComboBox<Integer> monthCombo = new ComboBox<>();
        monthCombo.setMaxWidth(Double.MAX_VALUE);
        monthCombo.setStyle(
                "-fx-pref-height: 30;" +
                        "-fx-background-color: white;" +
                        "-fx-border-color: #C4C4C4;" +
                        "-fx-border-radius: 8;" +
                        "-fx-background-radius: 8;" +
                        "-fx-font-size: 13;" +
                        "-fx-font-family: 'Pretendard Medium';" +
                        "-fx-text-fill: #323232;"
        );
        VBox monthBox = new VBox(10);
        monthBox.getChildren().addAll(monthLabel, monthCombo);

        VBox selectionBox = new VBox(15);
        selectionBox.getChildren().addAll(yearBox, monthBox);

        Button confirmBtn = getButton(yearCombo, monthCombo, popupStage);

        popupContent.getChildren().addAll(selectionBox, confirmBtn);

        // 드롭다운용 Scene
        Scene scene = new Scene(popupContent);
        popupStage.setScene(scene);

        double x = this.monthLabel.localToScreen(0, 0).getX() - 25;
        double y = this.monthLabel.localToScreen(0, this.monthLabel.getHeight()).getY() + 10;
        popupStage.setX(x);
        popupStage.setY(y);

        popupStage.focusedProperty().addListener((obs, wasFocused, isNowFocused) -> {
            if (!isNowFocused) popupStage.close();
        }); // 포커스 잃으면 자동으로 닫기

        popupStage.show();

        // 현재 연/월 설정
        int currentYear = LocalDate.now().getYear();
        for (int i = currentYear - 15; i <= currentYear + 10; i++) yearCombo.getItems().add(i);
        for (int i = 1; i <= 12; i++) monthCombo.getItems().add(i);

        yearCombo.setValue(currentDate.getYear());
        monthCombo.setValue(currentDate.getMonthValue());
    }

    private Button getButton(ComboBox<Integer> yearCombo, ComboBox<Integer> monthCombo, Stage popupStage) {
        Button confirmBtn = new Button("확인");
        confirmBtn.setStyle(
                "-fx-pref-width: 50;" +
                        "-fx-background-color: #222222;" +
                        "-fx-background-radius: 8;" +
                        "-fx-font-size: 13;" +
                        "-fx-font-family: 'Pretendard SemiBold';" +
                        "-fx-cursor: hand;" +
                        "-fx-text-fill: white;"
        );

        confirmBtn.setOnAction(e -> {
            LocalDate selectedMonthFirstDay = LocalDate.of(
                    yearCombo.getValue(), monthCombo.getValue(), 1
            );
            currentDate = selectedMonthFirstDay;
            currentDateSelected = selectedMonthFirstDay;

            loadScheduleForMonth(currentDate);
            renderCalendar(currentDate);
            showScheduleDetail(currentDateSelected);
            popupStage.close();
        });
        return confirmBtn;
    }

    private void showScheduleDetail(LocalDate date) {
        List<DummyCodiDTO> schedules = scheduleMap.get(date);
        scheduleListContainer.getChildren().clear();
        emptyLabel.setVisible(false);
        emptyLabel.setManaged(false);

        if (schedules == null || schedules.isEmpty()) {
            // 일정이 없을 때는 날짜만 표시
            Label dateLabel = new Label(formatKoreanDate(date));
            dateLabel.getStyleClass().add("date-box");
            scheduleListContainer.getChildren().add(dateLabel);

            emptyLabel.setVisible(true);
            emptyLabel.setManaged(true);
            return;
        }

        for (int i = 0; i < schedules.size(); i++) {
            DummyCodiDTO schedule = schedules.get(i);

            Label descLabel = new Label("│ " + schedule.getDescription());
            descLabel.getStyleClass().add("desc-label");

            if (i == 0) {
                // 첫 번째 일정: 날짜 + 설명 그룹핑
                VBox scheduleBox = new VBox();
                scheduleBox.getStyleClass().add("schedule-box");

                VBox dateAndDescBox = new VBox();
                dateAndDescBox.setSpacing(8);
                dateAndDescBox.getStyleClass().add("first-schedule-box");

                Label dateLabel = new Label(formatKoreanDate(date));
                dateLabel.getStyleClass().add("date-box");

                Label badgeLabel = new Label(getVisibilityLabel(schedule.getVisibility()));
                badgeLabel.getStyleClass().add("badge-" + schedule.getVisibility().name().toLowerCase());

                HBox descWithBadge = new HBox();
                descWithBadge.setAlignment(Pos.CENTER_LEFT);

                Region spacer = new Region();
                HBox.setHgrow(spacer, Priority.ALWAYS);

                descWithBadge.getChildren().addAll(descLabel, spacer, badgeLabel);

                dateAndDescBox.getChildren().addAll(dateLabel, descWithBadge);
                scheduleBox.getChildren().add(dateAndDescBox);

                for (CodiItem item : schedule.getCodiItems()) {
                    scheduleBox.getChildren().add(buildCodiItemBox(item));
                }

                scheduleListContainer.getChildren().add(scheduleBox);
            } else {
                // 이후 일정들: 설명 라벨만
                VBox otherScheduleBox = new VBox();
                otherScheduleBox.getStyleClass().add("other-schedule-box");

                descLabel.getStyleClass().add("other-desc-label");

                Label badgeLabel = new Label(getVisibilityLabel(schedule.getVisibility()));
                badgeLabel.getStyleClass().add("badge-" + schedule.getVisibility().name().toLowerCase());

                Region spacer = new Region();
                HBox.setHgrow(spacer, Priority.ALWAYS);

                HBox descWithBadge = new HBox(descLabel, spacer, badgeLabel);
                descWithBadge.getStyleClass().add("desc-with-badge");
                descWithBadge.setAlignment(Pos.CENTER_LEFT);
                descWithBadge.setMaxWidth(Double.MAX_VALUE);
                VBox.setVgrow(descWithBadge, Priority.ALWAYS);

                descLabel.setMaxWidth(Double.MAX_VALUE);
                HBox.setHgrow(descLabel, Priority.NEVER);
                HBox.setHgrow(badgeLabel, Priority.NEVER);

                otherScheduleBox.getChildren().add(descWithBadge);

                for (CodiItem item : schedule.getCodiItems()) {
                    otherScheduleBox.getChildren().add(buildCodiItemBox(item));
                }

                scheduleListContainer.getChildren().add(otherScheduleBox);
            }
        }
    }

    private String getVisibilityLabel(CodiScope visibility) {
        return switch (visibility) {
            case PUBLIC -> "전체공개";
            case FRIENDS -> "친구공개";
            case PRIVATE -> "비공개";
        };
    }

    private HBox buildCodiItemBox(CodiItem item) {
        ImageView imageView = new ImageView(new Image(item.getImagePath()));
        imageView.getStyleClass().add("item-image");
        imageView.setFitWidth(80);
        imageView.setFitHeight(80);

        Label itemCategory = new Label(item.getCategory());
        itemCategory.getStyleClass().add("item-category");
        Label itemName = new Label(item.getName());
        itemName.getStyleClass().add("item-name");

        VBox textBox = new VBox(
                itemCategory,
                itemName
        );
        textBox.getStyleClass().add("text-box");

        HBox box = new HBox(imageView, textBox);
        box.setAlignment(Pos.CENTER_LEFT);
        box.setSpacing(15);
        return box;
    }

    private String formatKoreanDate(LocalDate date) {
        DayOfWeek dayOfWeek = date.getDayOfWeek();
        String[] koreanDays = {"월", "화", "수", "목", "금", "토", "일"};
        return date.getMonthValue() + "월 " + date.getDayOfMonth() + "일 " + koreanDays[dayOfWeek.getValue() - 1] + "요일";
    }

    @FXML
    private void handleClickAddCodi() {
        MainLayoutController.loadView("/com/samyukgu/what2wear/codi/AddCodiView.fxml");
    }

    /*
    * 영역
    */


    private void showEmptyFriendMessage() {
        VBox emptyStateBox = new VBox();
        emptyStateBox.setAlignment(Pos.CENTER);
        emptyStateBox.setPrefHeight(friendListArea.getPrefHeight());
        emptyStateBox.setPrefWidth(friendListArea.getPrefWidth());
        emptyStateBox.setSpacing(15);
        emptyStateBox.setStyle("-fx-background-color: #FFFFFF");

        // 메인 메시지
        Label mainMessage = new Label("아직 친구가 없어요!");
        mainMessage.setStyle("-fx-font-size: 16px; -fx-text-fill: #666666;");

        // 서브 메시지
        Label subMessage = new Label("사용자를 검색해서 친구를 찾아보세요!");
        subMessage.setStyle("-fx-font-size: 14px; -fx-text-fill: #999999;");

        mainMessage.getStyleClass().add("bold-text");
        subMessage.getStyleClass().add("bold-text");

        // 요소들을 VBox에 추가
        emptyStateBox.getChildren().addAll(mainMessage, subMessage);

        // 빈 상태 박스를 친구 목록 영역에 추가
        friendListArea.getChildren().add(emptyStateBox);

        // 스타일 클래스 추가 (필요시)
        emptyStateBox.getStyleClass().add("empty-state-container");
    }

    @FXML public void handleClickSearchButton(){
        MainLayoutController.loadView("/com/samyukgu/what2wear/friend/FriendSearchView.fxml");
    }

    // 컨테이너에 있는 인스턴스 멤버로 할당
    private void setupDI() {
        DIContainer diContainer = DIContainer.getInstance();
        memberSession = diContainer.resolve(MemberSession.class);
        friendService = diContainer.resolve(FriendService.class);
    }

    private void loadMember(){
        this.member = memberSession.getMember();
    }
}
