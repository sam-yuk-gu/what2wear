package com.samyukgu.what2wear.ai.service;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.samyukgu.what2wear.config.ConfigUtil;

import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URLEncoder;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

public class WeatherAiService {
    private final String SERVICE_KEY;
    public WeatherAiService() {
        // 기상청 api 키
        this.SERVICE_KEY = ConfigUtil.get("weather.api.key");
    }

    public Map<String, String> getWeatherInfo(String location) throws Exception {
        // 예시: 서울시 종로구 → 서울, 종로구 → 서울 격자값 사용
        int nx = 60; // 서울 종로구
        int ny = 127;

        String baseDate = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String baseTime = "0500"; // 오전 5시 기준 예보

        String urlStr = "https://apis.data.go.kr/1360000/VilageFcstInfoService_2.0/getVilageFcst?"
                + "serviceKey=" + URLEncoder.encode(SERVICE_KEY, "UTF-8")
                + "&pageNo=1&numOfRows=1000&dataType=JSON"
                + "&base_date=" + baseDate
                + "&base_time=" + baseTime
                + "&nx=" + nx
                + "&ny=" + ny;

        URL url = new URL(urlStr);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");

        InputStreamReader reader = new InputStreamReader(conn.getInputStream());
        JsonObject json = JsonParser.parseReader(reader).getAsJsonObject();

        JsonObject response = json.getAsJsonObject("response");
        JsonObject body = response.getAsJsonObject("body");
        JsonObject items = body.getAsJsonObject("items");
        JsonArray itemArray = items.getAsJsonArray("item");

        String high = "", low = "", sensible = "";

        for (JsonElement el : itemArray) {
            JsonObject obj = el.getAsJsonObject();
            String category = obj.get("category").getAsString();
            String fcstTime = obj.get("fcstTime").getAsString();

            if (category.equals("TMX")) {
                high = obj.get("fcstValue").getAsString();
            } else if (category.equals("TMN")) {
                low = obj.get("fcstValue").getAsString();
            } else if (category.equals("SENS")) {
                if (fcstTime.equals("1500")) { // 오후 3시 체감온도
                    sensible = obj.get("fcstValue").getAsString();
                }
            }
        }
        Map<String, String> result = new HashMap<>();
        result.put("high", high);
        result.put("low", low);
        result.put("sensible", sensible);

        return result;
    }
}
