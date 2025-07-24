package com.samyukgu.what2wear.region.Session;

import com.samyukgu.what2wear.weather.model.Weather;
import com.samyukgu.what2wear.region.model.Region;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

public class RegionWeatherSession {
    @Getter
    @Setter
    private static Region region;

    @Getter
    @Setter
    private static Weather weather;

    // 기본 지역 정보 (로그인 시 초기화)
    static {
        region = new Region(1L, "서울특별시", "", 60L, 127L);  // 초기 지역
        weather = null; // 날씨는 첫 호출 시에 갱신
    }

    public static void reset() {
        region = new Region(1L, "서울특별시", "", 60L, 127L);
        weather = null;
    }
}