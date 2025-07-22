package com.samyukgu.what2wear.mail.common;

import java.security.SecureRandom;

public class PasswordUtils {
    static final int PASSWORD_LENGTH = 12;

    // 사용할 문자들: 대문자·소문자·숫자·특수문자
    private static final String CHAR_POOL =
            "ABCDEFGHIJKLMNOPQRSTUVWXYZ" +
                    "abcdefghijklmnopqrstuvwxyz" +
                    "0123456789" +
                    "!@$%^&*";

    private static final SecureRandom random = new SecureRandom();

    // 랜덤 비밀번호 생성 메서드
    public static String generateTempPassword() {
        StringBuilder pw = new StringBuilder(PASSWORD_LENGTH);
        for (int i = 0; i < PASSWORD_LENGTH; i++) {
            // CHAR_POOL 중 랜덤 인덱스 선택
            int idx = random.nextInt(CHAR_POOL.length());
            pw.append(CHAR_POOL.charAt(idx));
        }
        return pw.toString();
    }
}
