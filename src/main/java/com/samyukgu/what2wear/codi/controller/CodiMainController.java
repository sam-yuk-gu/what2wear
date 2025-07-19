package com.samyukgu.what2wear.codi.controller;

import com.samyukgu.what2wear.codi.model.CodiItem;
import com.samyukgu.what2wear.codi.model.CodiSchedule;
import com.samyukgu.what2wear.codi.service.DummyScheduleRepository;
import com.samyukgu.what2wear.common.controller.MainLayoutController;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Popup;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public class CodiMainController {

    private LocalDate currentDateSelected;

    @FXML private Label monthLabel;
    @FXML private GridPane calendarGrid;
    @FXML private Label selectedDateLabel;
    @FXML private Label scheduleLabel;
    @FXML private VBox codiListContainer;
    @FXML private Label emptyLabel;

    private LocalDate currentDate;
    private Map<LocalDate, List<CodiSchedule>> scheduleMap; // 날짜별 일정 정보 저장

    @FXML
    public void initialize() {
        currentDate = LocalDate.now();
        currentDateSelected = currentDate;
        loadScheduleForMonth(currentDate);
        renderCalendar(currentDate);
        showScheduleDetail(currentDateSelected);
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
                List<CodiSchedule> schedules = scheduleMap.get(currentDrawingDate);
                HBox dotsBox = new HBox();
                dotsBox.getStyleClass().add("dots-box");
//                dotsBox.setAlignment(Pos.TOP_RIGHT);

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

    @FXML
    private void handleMonthLabelClick() {
        Popup popup = new Popup();
        popup.setAutoHide(true);

        VBox popupContent = new VBox(10);
        popupContent.setStyle("-fx-background-color: white; -fx-border-color: gray; -fx-padding: 10; -fx-background-radius: 8; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.25), 10, 0, 0, 2);");

        ComboBox<Integer> yearCombo = new ComboBox<>();
        ComboBox<Integer> monthCombo = new ComboBox<>();

        int currentYear = LocalDate.now().getYear();
        for (int i = currentYear - 5; i <= currentYear + 5; i++) yearCombo.getItems().add(i);
        for (int i = 1; i <= 12; i++) monthCombo.getItems().add(i);

        yearCombo.setValue(currentDate.getYear());
        monthCombo.setValue(currentDate.getMonthValue());

        Button confirmBtn = getButton(yearCombo, monthCombo, popup);

        popupContent.getChildren().addAll(new Label("연도"), yearCombo, new Label("월"), monthCombo, confirmBtn);
        popup.getContent().add(popupContent);

        // monthLabel 위치 기준으로 popup 띄우기
        double x = monthLabel.localToScreen(0, 0).getX();
        double y = monthLabel.localToScreen(0, monthLabel.getHeight()).getY();

        popup.show(monthLabel.getScene().getWindow(), x, y);
    }

    private Button getButton(ComboBox<Integer> yearCombo, ComboBox<Integer> monthCombo, Popup popup) {
        Button confirmBtn = new Button("확인");
        confirmBtn.setOnAction(e -> {
            // 선택된 연도와 월의 1일로 currentDate 및 currentDateSelected 초기화
            LocalDate selectedMonthFirstDay = LocalDate.of(yearCombo.getValue(), monthCombo.getValue(), 1);

            currentDate = selectedMonthFirstDay;
            currentDateSelected = selectedMonthFirstDay;

            loadScheduleForMonth(currentDate);
            renderCalendar(currentDate);
            showScheduleDetail(currentDateSelected);

            popup.hide();
        });
        return confirmBtn;
    }

    private void showScheduleDetail(LocalDate date) {
        List<CodiSchedule> schedules = scheduleMap.get(date);
        selectedDateLabel.setText(formatKoreanDate(date));

        if (schedules == null || schedules.isEmpty()) {
            scheduleLabel.setText("");
            codiListContainer.getChildren().clear();
            emptyLabel.setVisible(true);
            emptyLabel.setManaged(true);
            return;
        }

        scheduleLabel.setText(schedules.get(0).getDescription()); // 예시로 첫 설명만
        codiListContainer.getChildren().clear();
        emptyLabel.setVisible(false);
        emptyLabel.setManaged(false);

        for (CodiSchedule schedule : schedules) {
            for (CodiItem item : schedule.getCodiItems()) {
                HBox itemBox = buildCodiItemBox(item);
                codiListContainer.getChildren().add(itemBox);
            }
        }
    }

    private HBox buildCodiItemBox(CodiItem item) {
        ImageView imageView = new ImageView(new Image(item.getImagePath()));
        imageView.setFitWidth(50);
        imageView.setFitHeight(50);

        VBox textBox = new VBox(
                new Label(item.getCategory()),
                new Label(item.getName())
        );
        textBox.setSpacing(5);

        HBox box = new HBox(imageView, textBox);
        box.setSpacing(10);
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
}
