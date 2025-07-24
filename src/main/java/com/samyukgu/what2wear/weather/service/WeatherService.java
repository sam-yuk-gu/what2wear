package com.samyukgu.what2wear.weather.service;


import com.samyukgu.what2wear.weather.model.Weather;
import com.samyukgu.what2wear.region.Session.RegionWeatherSession;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Properties;

public class WeatherService {

    private final String serviceKey;

    public WeatherService() {
        Properties props = new Properties();
        try (
                InputStream is = ClassLoader.getSystemResourceAsStream("application.properties");
                InputStreamReader isr = new InputStreamReader(is, StandardCharsets.UTF_8)
        ) {
            props.load(isr);
            this.serviceKey = props.getProperty("weather.api.key");
            if (this.serviceKey == null || this.serviceKey.isBlank()) {
                throw new RuntimeException("weather.api.key is missing in application.properties");
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to load weather API key", e);
        }
    }

    public void updateRegionWeather(Long regionId, int nx, int ny) {
        Weather weather = fetchWeatherFromApi(nx, ny);
        if (weather != null) {
//            RegionWeatherSession.update(regionId, weather);
        }
    }

    public Weather fetchWeatherFromApi(int nx, int ny) {
        try {
            // 요청 시각 계산
            LocalDateTime now = LocalDateTime.now();
            String baseDate = now.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
            int hour = now.getMinute() <= 30 ? now.getHour() - 1 : now.getHour();
            String baseTime = String.format("%02d00", hour);
            String lastUpdateTime = now.format(DateTimeFormatter.ofPattern("yy.MM.dd ")) + hour;

            // 쿼리 파라미터 생성
            StringBuilder urlBuilder = new StringBuilder("http://apis.data.go.kr/1360000/VilageFcstInfoService_2.0/getUltraSrtNcst");
            urlBuilder.append("?").append(URLEncoder.encode("serviceKey", "UTF-8")).append("=").append(serviceKey);
            urlBuilder.append("&").append(URLEncoder.encode("pageNo", "UTF-8")).append("=1");
            urlBuilder.append("&").append(URLEncoder.encode("numOfRows", "UTF-8")).append("=1000");
            urlBuilder.append("&").append(URLEncoder.encode("dataType", "UTF-8")).append("=JSON");
            urlBuilder.append("&").append(URLEncoder.encode("base_date", "UTF-8")).append("=").append(baseDate);
            urlBuilder.append("&").append(URLEncoder.encode("base_time", "UTF-8")).append("=").append(baseTime);
            urlBuilder.append("&").append(URLEncoder.encode("nx", "UTF-8")).append("=").append(nx);
            urlBuilder.append("&").append(URLEncoder.encode("ny", "UTF-8")).append("=").append(ny);

            URL url = new URL(urlBuilder.toString());
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Content-type", "application/json");

            BufferedReader br = new BufferedReader(new InputStreamReader(
                    conn.getResponseCode() >= 200 ? conn.getInputStream() : conn.getErrorStream()
            ));

            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) sb.append(line);
            br.close();


            return parseWeatherFromJson(sb.toString(), lastUpdateTime);

        } catch (Exception e) {
            System.err.println("날씨 API 호출 중 오류 발생: " + e.getMessage());
            return null;
        }
    }

    private Weather parseWeatherFromJson(String json, String lastUpdateTime) {
        Double temp = null;
        Double humid = null;
        Double rainAmount = null;

        JSONObject jObject = new JSONObject(json)
                .getJSONObject("response")
                .getJSONObject("body")
                .getJSONObject("items");
        JSONArray jArray = jObject.getJSONArray("item");

        for (int i = 0; i < jArray.length(); i++) {
            JSONObject obj = jArray.getJSONObject(i);
            String category = obj.getString("category");
            double obsrValue = obj.getDouble("obsrValue");

            switch (category) {
                case "T1H": temp = obsrValue; break;
                case "RN1": rainAmount = obsrValue; break;
                case "REH": humid = obsrValue; break;
            }
        }

        return new Weather(temp, rainAmount, humid, lastUpdateTime);
    }
}