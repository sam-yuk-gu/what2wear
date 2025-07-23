package com.samyukgu.what2wear.common.util;

import java.util.HashMap;
import java.util.Map;

public class CategoryUtil {
    // 이름 → ID 매핑
    private static final Map<String, Long> NAME_TO_ID = new HashMap<>();

    // ID → 이름 매핑
    private static final Map<Long, String> ID_TO_NAME = new HashMap<>();

    static {
        NAME_TO_ID.put("상의", 1L);
        NAME_TO_ID.put("바지", 2L);
        NAME_TO_ID.put("신발", 3L);
        NAME_TO_ID.put("가방", 4L);
        NAME_TO_ID.put("원피스/스커트", 5L);
        NAME_TO_ID.put("아우터", 6L);
        NAME_TO_ID.put("악세사리", 7L);
        NAME_TO_ID.put("기타", 8L);

        // 역방향 매핑 생성
        for (Map.Entry<String, Long> entry : NAME_TO_ID.entrySet()) {
            ID_TO_NAME.put(entry.getValue(), entry.getKey());
        }
    }

    /**
     * 카테고리 이름으로 ID 반환
     */
    public static Long getIdByName(String name) {
        return NAME_TO_ID.getOrDefault(name, -1L);
    }

    /**
     * ID로 카테고리 이름 반환
     */
    public static String getNameById(Long id) {
        return ID_TO_NAME.getOrDefault(id, "알 수 없음");
    }
}
