package com.samyukgu.what2wear.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ConfigUtil {
    private static final Properties properties = new Properties();

    static {
        try (InputStream input = ConfigUtil.class.getClassLoader().getResourceAsStream("application.properties")) {
            if (input == null) {
                throw new RuntimeException("application.properties 파일을 찾을 수 없습니다.");
            }
            properties.load(input);
        } catch (IOException e) {
            throw new RuntimeException("application.properties 파일 로드 실패", e);
        }
    }


    public static String get(String key) {
        return properties.getProperty(key);
    }
}
