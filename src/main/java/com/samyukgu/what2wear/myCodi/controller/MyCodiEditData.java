package com.samyukgu.what2wear.myCodi.controller;

import com.samyukgu.what2wear.myCodi.model.CodiWithDetails;

public class MyCodiEditData {

    private static CodiWithDetails selectedCodi;

     //수정할 코디 정보 설정
    public static void setSelectedCodi(CodiWithDetails codi) {
        selectedCodi = codi;
        System.out.println("MyCodiEditData - 수정할 코디 설정: " +
                (codi != null ? codi.getName() : "null"));
    }

    //수정할 코디 정보 가져오기
    public static CodiWithDetails getSelectedCodi() {
        System.out.println("MyCodiEditData - 수정할 코디 반환: " +
                (selectedCodi != null ? selectedCodi.getName() : "null"));
        return selectedCodi;
    }

    // 선택된 코디 정보 초기화
    public static void clearSelectedCodi() {
        System.out.println("MyCodiEditData - 코디 정보 초기화");
        selectedCodi = null;
    }

    //선택된 코디가 있는지 확인
    public static boolean hasSelectedCodi() {
        boolean hasData = selectedCodi != null;
        System.out.println("MyCodiEditData - 코디 정보 존재 여부: " + hasData);
        return hasData;
    }
}