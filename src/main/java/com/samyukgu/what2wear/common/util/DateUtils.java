package com.samyukgu.what2wear.common.util;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class DateUtils {

    private static final DateTimeFormatter KOREAN_DAY_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy. MM. dd (E)", Locale.KOREAN);

    public static String formatWithKoreanDay(LocalDate date) {
        return date != null ? date.format(KOREAN_DAY_FORMATTER) : "";
    }

    public static LocalDate parseKoreanFormattedDate(String text) {
        return (text != null && !text.isEmpty()) ? LocalDate.parse(text, KOREAN_DAY_FORMATTER) : null;
    }

    public static DateTimeFormatter getKoreanDayFormatter() {
        return KOREAN_DAY_FORMATTER;
    }
}