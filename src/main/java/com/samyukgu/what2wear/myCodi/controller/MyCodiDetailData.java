package com.samyukgu.what2wear.myCodi.controller;

import com.samyukgu.what2wear.myCodi.model.CodiWithDetails;

public class MyCodiDetailData {
    private static CodiWithDetails selectedCodi;

    public static void setSelectedCodi(CodiWithDetails codi) {
        selectedCodi = codi;
    }

    public static CodiWithDetails getSelectedCodi() {
        return selectedCodi;
    }

    public static void clearSelectedCodi() {
        selectedCodi = null;
    }
}