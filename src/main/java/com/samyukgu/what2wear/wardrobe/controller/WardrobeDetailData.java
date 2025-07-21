package com.samyukgu.what2wear.wardrobe.controller;

import com.samyukgu.what2wear.wardrobe.model.Wardrobe;

// 상세 페이지로 데이터를 전달하기 위한 유틸리티 클래스
class WardrobeDetailData {
    private static Wardrobe selectedWardrobe;

    public static void setSelectedWardrobe(Wardrobe wardrobe) {
        selectedWardrobe = wardrobe;
    }

    public static Wardrobe getSelectedWardrobe() {
        return selectedWardrobe;
    }

    public static void clearSelectedWardrobe() {
        selectedWardrobe = null;
    }
}
