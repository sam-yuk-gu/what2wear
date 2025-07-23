package com.samyukgu.what2wear.common.util;

import javafx.scene.control.Button;
import javafx.scene.control.Label;

/**
 * UI 컴포넌트에 호버 효과를 추가하는 유틸리티 클래스
 */
public class HoverEffectUtil {

    // 색상 상수
    private static final String BLUE_COLOR = "#0078d4";
    private static final String RED_COLOR = "#dc3545";
    private static final String GRAY_COLOR = "#333333";
    private static final String LIGHT_GRAY_COLOR = "#666666";
    private static final String WHITE_COLOR = "#ffffff";
    private static final String LIGHT_BLUE_BG = "#f0f8ff";
    private static final String LIGHT_RED_BG = "#fff5f5";
    private static final String BORDER_COLOR = "#cccccc";
    private static final String HOVER_BG_COLOR = "#e6f3ff";

    /**
     * 일반 클릭 가능한 라벨에 호버 효과 추가
     * @param label 효과를 적용할 라벨
     */
    public static void addClickableLabelEffect(Label label) {
        if (label == null) return;

        if (label == null) return;

        // 기본 스타일 설정
        label.setStyle(label.getStyle() +
                "-fx-cursor: hand; ");

        // 호버 진입 이벤트
        label.setOnMouseEntered(e -> {
            label.setStyle(label.getStyle() +
                    "-fx-underline: true; " +
                    "-fx-font-weight: bold; " +
                    "-fx-background-color: #f0f8ff;");
        });

        // 호버 종료 이벤트
        label.setOnMouseExited(e -> {
            String currentStyle = label.getStyle();
            currentStyle = currentStyle.replaceAll("-fx-underline: true; ", "");
            currentStyle = currentStyle.replaceAll("-fx-font-weight: bold; ", "");
            currentStyle = currentStyle.replaceAll("-fx-background-color: #f0f8ff;", "");

            label.setStyle(currentStyle +
                    "-fx-underline: false;");
        });
    }

    /**
     * 위험한 액션 라벨(회원 탈퇴 등)에 빨간색 호버 효과 추가
     * @param label 효과를 적용할 라벨
     */
    public static void addDangerLabelEffect(Label label) {
        if (label == null) return;

        // 기본 스타일 설정
        label.setStyle(label.getStyle() +
                "-fx-cursor: hand; " +
                "-fx-text-fill: " + LIGHT_GRAY_COLOR + "; " +
                "-fx-padding: 5px 10px; " +
                "-fx-background-radius: 4px;");

        // 호버 진입 이벤트
        label.setOnMouseEntered(e -> {
            label.setStyle(label.getStyle() +
                    "-fx-text-fill: " + RED_COLOR + "; " +
                    "-fx-underline: true; " +
                    "-fx-font-weight: bold; " +
                    "-fx-background-color: " + LIGHT_RED_BG + ";");
        });

        // 호버 종료 이벤트
        label.setOnMouseExited(e -> {
            String currentStyle = removeHoverStyles(label.getStyle(),
                    "-fx-text-fill: " + RED_COLOR + "; ",
                    "-fx-underline: true; ",
                    "-fx-font-weight: bold; ",
                    "-fx-background-color: " + LIGHT_RED_BG + ";");

            label.setStyle(currentStyle +
                    "-fx-text-fill: " + LIGHT_GRAY_COLOR + "; " +
                    "-fx-underline: false;");
        });
    }

    /**
     * 버튼에 호버 효과 추가
     * @param button 효과를 적용할 버튼
     */
    public static void addButtonHoverEffect(Button button) {
        if (button == null) return;

        // 원래 스타일을 버튼의 userData에 저장
        String originalStyle = button.getStyle();
        button.setUserData(originalStyle);

        // 기본 스타일 설정
        button.setStyle(button.getStyle() +
                "-fx-cursor: hand; " +
                "-fx-border-color: " + BORDER_COLOR + "; " +
                "-fx-border-width: 1px; " +
                "-fx-border-radius: 6px; " +
                "-fx-background-radius: 6px; " +
                "-fx-padding: 8px 16px;");

        // 호버 진입 이벤트
        button.setOnMouseEntered(e -> {
            button.setStyle(button.getStyle() +
                    "-fx-background-color: " + HOVER_BG_COLOR + "; " +
                    "-fx-border-color: " + BLUE_COLOR + "; " +
                    "-fx-text-fill: " + BLUE_COLOR + "; " +
                    "-fx-scale-x: 1.05; " +
                    "-fx-scale-y: 1.05;");
        });

        // 호버 종료 이벤트
        button.setOnMouseExited(e -> {
            // 원래 스타일을 userData에서 가져와서 복원
            String restoredStyle = (String) button.getUserData();
            if (restoredStyle == null) restoredStyle = "";

            button.setStyle(restoredStyle +
                    "-fx-cursor: hand; " +
                    "-fx-border-color: " + BORDER_COLOR + "; " +
                    "-fx-border-width: 1px; " +
                    "-fx-border-radius: 6px; " +
                    "-fx-background-radius: 6px; " +
                    "-fx-padding: 8px 16px; " +
                    "-fx-scale-x: 1.0; " +
                    "-fx-scale-y: 1.0;");
        });

        // 클릭 효과
        button.setOnMousePressed(e -> {
            button.setStyle(button.getStyle() +
                    "-fx-scale-x: 0.98; " +
                    "-fx-scale-y: 0.98;");
        });

        button.setOnMouseReleased(e -> {
            String currentStyle = removeHoverStyles(button.getStyle(),
                    "-fx-scale-x: 0.98; ",
                    "-fx-scale-y: 0.98;");
            button.setStyle(currentStyle);
        });
    }

    /**
     * 커스텀 색상으로 라벨 호버 효과 추가
     * @param label 효과를 적용할 라벨
     * @param normalColor 기본 상태 색상
     * @param hoverColor 호버 상태 색상
     * @param hoverBgColor 호버 상태 배경색
     */
    public static void addCustomLabelEffect(Label label, String normalColor,
                                            String hoverColor, String hoverBgColor) {
        if (label == null) return;

        // 기본 스타일 설정
        label.setStyle(label.getStyle() +
                "-fx-cursor: hand; " +
                "-fx-text-fill: " + normalColor + "; " +
                "-fx-padding: 5px 10px; " +
                "-fx-background-radius: 4px;");

        // 호버 진입 이벤트
        label.setOnMouseEntered(e -> {
            label.setStyle(label.getStyle() +
                    "-fx-text-fill: " + hoverColor + "; " +
                    "-fx-underline: true; " +
                    "-fx-font-weight: bold; " +
                    "-fx-background-color: " + hoverBgColor + ";");
        });

        // 호버 종료 이벤트
        label.setOnMouseExited(e -> {
            String currentStyle = removeHoverStyles(label.getStyle(),
                    "-fx-text-fill: " + hoverColor + "; ",
                    "-fx-underline: true; ",
                    "-fx-font-weight: bold; ",
                    "-fx-background-color: " + hoverBgColor + ";");

            label.setStyle(currentStyle +
                    "-fx-text-fill: " + normalColor + "; " +
                    "-fx-underline: false;");
        });
    }

    /**
     * 여러 개의 라벨에 일괄적으로 클릭 가능한 효과 적용
     * @param labels 효과를 적용할 라벨들
     */
    public static void addClickableEffectToLabels(Label... labels) {
        for (Label label : labels) {
            addClickableLabelEffect(label);
        }
    }

    /**
     * 여러 개의 버튼에 일괄적으로 호버 효과 적용
     * @param buttons 효과를 적용할 버튼들
     */
    public static void addHoverEffectToButtons(Button... buttons) {
        for (Button button : buttons) {
            addButtonHoverEffect(button);
        }
    }

    /**
     * 스타일 문자열에서 특정 스타일들을 제거하는 헬퍼 메서드
     * @param currentStyle 현재 스타일 문자열
     * @param stylesToRemove 제거할 스타일들
     * @return 스타일이 제거된 문자열
     */
    private static String removeHoverStyles(String currentStyle, String... stylesToRemove) {
        String result = currentStyle;
        for (String style : stylesToRemove) {
            result = result.replaceAll(style.replace("(", "\\(").replace(")", "\\)"), "");
        }
        return result;
    }
}