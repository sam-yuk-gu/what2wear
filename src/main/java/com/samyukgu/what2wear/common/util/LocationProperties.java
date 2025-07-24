package com.samyukgu.what2wear.common.util;

import java.io.InputStream;
import java.util.Properties;

public class LocationProperties {
    private static final Properties properties = new Properties();

    static {
        try (InputStream input = LocationProperties.class.getClassLoader().getResourceAsStream("application.properties")) {
            if (input != null) {
                properties.load(input);
            } else {
                throw new RuntimeException("properties 파일을 찾을 수 없습니다.");
            }
        } catch (Exception e) {
            throw new RuntimeException("설정 파일 로딩 중 오류 발생", e);
        }
    }

    public static String get(String key) {
        return properties.getProperty(key);
    }
}
