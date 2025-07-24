package com.samyukgu.what2wear.common.util;

import com.samyukgu.what2wear.codi.model.CodiItem;
import com.samyukgu.what2wear.wardrobe.model.Wardrobe;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

public class ModelConverter {

    public static List<Wardrobe> convertToWardrobeList(List<CodiItem> codiItems) {
        return codiItems.stream().map(item -> {
            Wardrobe w = new Wardrobe();
            w.setId(item.getId());
            w.setName(item.getName());
            w.setCategoryId(getCategoryIdFromName(item.getCategory()));
            try {
                FileInputStream fis = new FileInputStream(item.getImagePath());
                w.setPicture(fis.readAllBytes());
                fis.close();
            } catch (IOException e) {
                e.printStackTrace();
                w.setPicture(null); // 실패했을 경우 null 처리
            }
            return w;
        }).collect(Collectors.toList());
    }

    // 이름 → ID 매핑
    private static Long getCategoryIdFromName(String name) {
        return switch (name) {
            case "상의" -> 1L;
            case "바지" -> 2L;
            case "신발" -> 3L;
            case "가방" -> 4L;
            case "원피스/스커트" -> 5L;
            case "아우터" -> 6L;
            case "악세사리" -> 7L;
            case "기타" -> 8L;
            default -> null;
        };
    }


}