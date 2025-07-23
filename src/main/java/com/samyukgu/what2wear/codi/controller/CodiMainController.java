package com.samyukgu.what2wear.codi.controller;

import com.samyukgu.what2wear.codi.dto.CodiListDTO;
import com.samyukgu.what2wear.codi.model.CodiItem;
import com.samyukgu.what2wear.codi.model.CodiSchedule;
//import com.samyukgu.what2wear.codi.model.ScheduleVisibility;
import com.samyukgu.what2wear.codi.service.DummyScheduleRepository;
import com.samyukgu.what2wear.layout.controller.MainLayoutController;
import com.samyukgu.what2wear.member.Session.MemberSession;
import com.samyukgu.what2wear.member.model.Member;
import javafx.event.ActionEvent;
import com.samyukgu.what2wear.codi.service.CodiService;
import com.samyukgu.what2wear.di.DIContainer;
import com.samyukgu.what2wear.layout.controller.MainLayoutController;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.samyukgu.what2wear.codi.model.CodiScope.fromString;
import static com.samyukgu.what2wear.common.util.DateUtils.formatKoreanDate;
import static com.samyukgu.what2wear.common.util.FxStyleUtil.applyHoverTransition;
import static com.samyukgu.what2wear.common.util.ImageUtil.convertToImagePath;

public class CodiMainController {

    private CodiService codiService;
    private LocalDate currentDateSelected;

    @FXML private Label monthLabel;
    @FXML private GridPane calendarGrid;
    @FXML private Label emptyLabel;
    @FXML private VBox scheduleListContainer;
    @FXML private Button aiButton;
    @FXML private Button addButton;

    private Long memberId;
    private MemberSession memberSession;
    private LocalDate currentDate;
    private Map<LocalDate, List<CodiSchedule>> dotScheduleMap; // 날짜별 일정 정보 저장
    private Map<LocalDate, List<CodiSchedule>> detailScheduleMap;

    @FXML
    public void initialize() {
        setupDI();
        setupUser();
        currentDate = LocalDate.now();
        currentDateSelected = currentDate;
        loadScheduleForMonth(currentDate);
        loadScheduleForDay(currentDateSelected);
        renderCalendar(currentDate);
        showScheduleDetail(currentDateSelected);

        applyHoverTransition(aiButton, Color.web("#FFFDF0"), Color.web("#FFF8DA"));
        applyHoverTransition(addButton, Color.web("#F2FBFF"), Color.web("#E0F6FF"));
    }

    private void setupDI() {
        DIContainer diContainer = DIContainer.getInstance();
        codiService = diContainer.resolve(CodiService.class);
        memberSession = diContainer.resolve(MemberSession.class);
    }

    private void setupUser() {
        if (memberSession == null || memberSession.getMember() == null) {
            System.err.println("로그인 정보가 없습니다.");
            return;
        }

        memberId = memberSession.getMember().getId();
    }

    private void loadScheduleForMonth(LocalDate month) {
        List<CodiSchedule> scheduleList = codiService.getMonthlyCodiSchedules(memberId, month);

        dotScheduleMap = new HashMap<>();

        for (CodiSchedule cs : scheduleList) {
            LocalDate date = cs.getDate(); // 날짜

            // 날짜별 리스트 초기화
            dotScheduleMap.computeIfAbsent(date, d -> new ArrayList<>());

            // 점 표시용 CodiSchedule 객체 생성
            CodiSchedule schedule = new CodiSchedule();
            schedule.setDate(date);
            schedule.setVisibility(cs.getVisibility());

            dotScheduleMap.get(date).add(schedule);    // 날짜 별 스케쥴 저장
        }
    }

    // 날짜 별 상세 일정
    private void loadScheduleForDay(LocalDate date) {
        List<CodiListDTO> codiLists = codiService.getCodiList(memberId, date);

        List<CodiSchedule> detailedSchedules = new ArrayList<>();

        for (CodiListDTO dto : codiLists) {
            for (var codiDTO : dto.getCodiList()) {
                // 조건: 일정명이나 코디가 하나라도 있으면 포함
                boolean hasScheduleName = false;
                if (codiDTO.getScheduleName() != null && !codiDTO.getScheduleName().isBlank()) {
                    hasScheduleName = true;
                }
                boolean hasClothes = !codiDTO.getCodiClothesList().isEmpty();

                if (!hasScheduleName && !hasClothes) continue; // 둘 다 없으면 건너뜀

                CodiSchedule schedule = new CodiSchedule();
                schedule.setCodiId(codiDTO.getCodiId());
                schedule.setDate(dto.getScheduleDate());
                schedule.setDescription(codiDTO.getScheduleName());
                schedule.setVisibility(fromString(codiDTO.getScope()));

                if (hasClothes) {
                    List<CodiItem> items = codiDTO.getCodiClothesList().stream().map(clothes -> {
                        CodiItem item = new CodiItem();
                        item.setCategory(clothes.getCategoryName());
                        item.setName(clothes.getClothesName());
                        item.setImagePath(convertToImagePath(clothes.getClothesPicture()));
                        return item;
                    }).toList();

                    schedule.setCodiItems(items);
                }

                detailedSchedules.add(schedule);
            }
        }

        if (detailScheduleMap == null) detailScheduleMap = new HashMap<>();
        detailScheduleMap.put(date, detailedSchedules);


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
            if (dotScheduleMap.containsKey(currentDrawingDate)) {
                List<CodiSchedule> schedules = dotScheduleMap.get(currentDrawingDate);
                HBox dotsBox = new HBox();
                dotsBox.getStyleClass().add("dots-box");

                for (CodiSchedule schedule : schedules) {
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
                    loadScheduleForDay(dateForCell);
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
        changeMonthAndRender(currentDate);
    }

    @FXML
    private void handleNextMonth() {
        currentDate = currentDate.plusMonths(1);
        currentDateSelected = currentDate.withDayOfMonth(1);
        loadScheduleForMonth(currentDate);
        renderCalendar(currentDate);
        showScheduleDetail(currentDateSelected);
        changeMonthAndRender(currentDate);
    }

    private Button getButton(ComboBox<Integer> yearCombo, ComboBox<Integer> monthCombo, Stage popupStage) {
        Button confirmBtn = new Button("확인");
        confirmBtn.setStyle(    // 팝업 버튼: css 연동 불가
            "-fx-pref-width: 50;" +
            "-fx-background-color: #222222;" +
            "-fx-background-radius: 8;" +
            "-fx-font-size: 13;" +
            "-fx-font-family: 'Pretendard SemiBold';" +
            "-fx-cursor: hand;" +
            "-fx-text-fill: white;"
        );

        confirmBtn.setOnAction(e -> {
            Integer selectedYear = yearCombo.getValue();
            Integer selectedMonth = monthCombo.getValue();
            if (selectedYear != null && selectedMonth != null) {
                LocalDate selectedDate = LocalDate.of(selectedYear, selectedMonth, 1);
                changeMonthAndRender(selectedDate);  // ✅ 여기도 재사용
            }
            popupStage.close();
        });
        return confirmBtn;
    }

    private void showScheduleDetail(LocalDate date) {
        List<CodiSchedule> schedules = detailScheduleMap.get(date);
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
            CodiSchedule schedule = schedules.get(i);

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

                Label badgeLabel = new Label(schedule.getVisibility().toLabel());
                badgeLabel.getStyleClass().add("badge-" + schedule.getVisibility().name().toLowerCase());

                HBox descWithBadge = new HBox();
                descWithBadge.setAlignment(Pos.CENTER_LEFT);

                Region spacer = new Region();
                HBox.setHgrow(spacer, Priority.ALWAYS);

                descWithBadge.getChildren().addAll(descLabel, spacer, badgeLabel);

                dateAndDescBox.getChildren().addAll(dateLabel, descWithBadge);
                scheduleBox.getChildren().add(dateAndDescBox);

                List<CodiItem> codiItems = schedule.getCodiItems();
                if (codiItems != null) {
                    for (CodiItem item : codiItems) {
                        scheduleBox.getChildren().add(buildCodiItemBox(item));
                    }
                }

                scheduleListContainer.getChildren().add(scheduleBox);
                scheduleBox.setOnMouseClicked(e -> {
                    Long codiId = schedule.getCodiId();
                    MainLayoutController.loadEditCodiView(codiId);
                });
            } else {
                // 이후 일정들: 설명 라벨만
                VBox otherScheduleBox = new VBox();
                otherScheduleBox.getStyleClass().add("other-schedule-box");

                descLabel.getStyleClass().add("other-desc-label");

                Label badgeLabel = new Label((schedule.getVisibility().toLabel()));
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
//
//                for (CodiItem item : schedule.getCodiItems()) {
//                    otherScheduleBox.getChildren().add(buildCodiItemBox(item));
//                }

                List<CodiItem> items = schedule.getCodiItems();
                if (items != null) {
                    for (CodiItem item : items) {
                        otherScheduleBox.getChildren().add(buildCodiItemBox(item));
                    }
                }

                otherScheduleBox.setOnMouseClicked(e -> {
                    Long codiId = schedule.getCodiId();
                    MainLayoutController.loadEditCodiView(codiId);
                });
                scheduleListContainer.getChildren().add(otherScheduleBox);
            }
        }
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

    @FXML
    private void handleClickAddCodi() {
        MainLayoutController.loadView("/com/samyukgu/what2wear/codi/AddCodiView.fxml");
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

    private void changeMonthAndRender(LocalDate newDate) {
        this.currentDate = newDate;
        this.currentDateSelected = newDate.withDayOfMonth(1); // 선택 초기화

        loadScheduleForMonth(currentDate);          // 월별 스케줄
        loadScheduleForDay(currentDateSelected);    // 상세 스케쥴
        renderCalendar(currentDate);                // 달력
        showScheduleDetail(currentDateSelected);    // 첫 날짜 코디 상세
    }

    // ai 추천 안내 화면으로 전환
    public void handleAiButtonClick(ActionEvent actionEvent) {
        MainLayoutController.loadView("/com/samyukgu/what2wear/ai/IntroduceAi.fxml");
    }
}
