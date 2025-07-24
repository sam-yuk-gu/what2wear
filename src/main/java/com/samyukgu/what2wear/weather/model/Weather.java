package com.samyukgu.what2wear.weather.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Weather {
    private Double temp;              // 기온 (℃)
    private Double rainAmount;        // 강수량 (mm)
    private Double humid;             // 습도 (%)
    private String lastUpdateTime;    // 갱신 시각 (예: "2025-07-23T07:00")
}