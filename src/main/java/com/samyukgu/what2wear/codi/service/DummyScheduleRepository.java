package com.samyukgu.what2wear.codi.service;

import com.samyukgu.what2wear.codi.model.CodiItem;
import com.samyukgu.what2wear.codi.model.CodiSchedule;
import com.samyukgu.what2wear.codi.model.ScheduleVisibility;

import java.time.LocalDate;
import java.util.*;

public class DummyScheduleRepository {

    public static Map<LocalDate, List<CodiSchedule>> getMonthlySchedule(LocalDate date) {
        Map<LocalDate, List<CodiSchedule>> map = new HashMap<>();

        map.put(LocalDate.of(2025, 6, 9), List.of(
                new CodiSchedule("친구들 모임", LocalDate.of(2025, 6, 9), List.of(
                        new CodiItem("상의", "반팔 티셔츠", "file:resources/assets/icons/logo.png")
                ), ScheduleVisibility.PUBLIC)
        ));

        map.put(LocalDate.of(2025, 7, 9), List.of(
                new CodiSchedule("혜화 연극 관람", LocalDate.of(2025, 7, 9), List.of(
                        new CodiItem("상의", "반팔 티셔츠", "file:resources/assets/icons/logo.png")
                ), ScheduleVisibility.PUBLIC)
        ));

        map.put(LocalDate.of(2025, 7, 15), List.of(
                new CodiSchedule("외부 미팅", LocalDate.of(2025, 7, 15), List.of(
                        new CodiItem("상의", "하늘색 셔츠", "file:resources/assets/icons/logo.png"),
                        new CodiItem("하의", "슬랙스", "file:resources/assets/icons/logo.png"),
                        new CodiItem("하의", "슬랙스", "file:resources/assets/icons/logo.png")
                ), ScheduleVisibility.FRIENDS),
                new CodiSchedule("팀 회의", LocalDate.of(2025, 7, 15), List.of(
                        new CodiItem("하의", "슬랙스", "file:resources/assets/icons/logo.png"),
                        new CodiItem("하의", "슬랙스", "file:resources/assets/icons/logo.png"),
                        new CodiItem("하의", "슬랙스", "file:resources/assets/icons/logo.png"),
                        new CodiItem("하의", "슬랙스", "file:resources/assets/icons/logo.png"),
                        new CodiItem("하의", "슬랙스", "file:resources/assets/icons/logo.png"),
                        new CodiItem("하의", "슬랙스", "file:resources/assets/icons/logo.png")
                ), ScheduleVisibility.PUBLIC),
                new CodiSchedule("저녁 약속", LocalDate.of(2025, 7, 15), List.of(
                        new CodiItem("아우터", "자켓", "file:resources/assets/icons/logo.png"),
                        new CodiItem("하의", "슬랙스", "file:resources/assets/icons/logo.png"),
                        new CodiItem("하의", "슬랙스", "file:resources/assets/icons/logo.png")
                ), ScheduleVisibility.PRIVATE)
        ));

        map.put(LocalDate.of(2025, 7, 21), List.of(
                new CodiSchedule("스터디 모임", LocalDate.of(2025, 7, 21), List.of(
                        new CodiItem("상의", "맨투맨", "file:resources/assets/icons/logo.png")
                ), ScheduleVisibility.FRIENDS)
        ));

        return map;
    }
}
