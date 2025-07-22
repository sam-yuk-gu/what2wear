package com.samyukgu.what2wear.wardrobe.controller;

import com.samyukgu.what2wear.wardrobe.model.Wardrobe;

// 수정 페이지로 데이터를 전달하기 위한 유틸리티 클래스
class WardrobeEditData {
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
