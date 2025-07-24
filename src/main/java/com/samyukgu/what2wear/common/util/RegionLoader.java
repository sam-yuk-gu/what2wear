package com.samyukgu.what2wear.common.util;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.samyukgu.what2wear.region.model.Region;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class RegionLoader {
    public static List<Region> loadRegions() {
        try (InputStreamReader reader = new InputStreamReader(
                RegionLoader.class.getResourceAsStream("/data/region.json"), "UTF-8")) {
            return new Gson().fromJson(reader, new TypeToken<List<Region>>() {}.getType());
        } catch (Exception e) {
            e.printStackTrace();
            return List.of(); // 빈 리스트 반환
        }
    }
}
