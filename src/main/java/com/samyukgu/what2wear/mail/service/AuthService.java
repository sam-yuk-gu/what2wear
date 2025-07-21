package com.samyukgu.what2wear.mail.service;

import com.samyukgu.what2wear.mail.common.MailConstants;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public class AuthService{
    private final Map<String, AuthCodeData> authCodes = new ConcurrentHashMap<>();
    private final MailService mailService;
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    public AuthService(MailService mailService) {
        this.mailService = mailService;
        startCleanupScheduler();
    }

    // 인증코드 생성 및 발송
    public void sendAuthCode(String email) {
        String authCode = generateAuthCode();
        LocalDateTime expiredAt = LocalDateTime.now().plusMinutes(MailConstants.EXPIRATION_MINUTES);

        // 메모리에 저장
        authCodes.put(email, new AuthCodeData(authCode, expiredAt));

        // 메일 발송
        mailService.sendAuth(email, authCode);
    }

    // 인증코드 검증
    public boolean verifyAuthCode(String email, String inputCode) {
        AuthCodeData storedData = authCodes.get(email);

        if (storedData == null) {
            return false; // 존재하지 않음
        }

        // 만료 체크
        if (LocalDateTime.now().isAfter(storedData.getExpiredAt())) {
            authCodes.remove(email); // 만료된 코드 삭제
            return false;
        }

        boolean isValid = storedData.getCode().equals(inputCode);

        // 인증 성공시 코드 삭제 (일회성)
        if (isValid) {
            authCodes.remove(email);
        }

        return isValid;
    }

    // 인증코드 재발송 (이전 코드 무효화)
    public void resendAuthCode(String email) {
        authCodes.remove(email); // 기존 코드 삭제
        sendAuthCode(email);
    }

    // 특정 이메일의 인증코드 삭제
    public void cancelAuthCode(String email) {
        authCodes.remove(email);
    }

    private String generateAuthCode() {
        Random random = new Random();
        return String.format("%06d", random.nextInt(1000000));
    }

    // 만료된 코드들 정리 (1분마다 실행)
    private void startCleanupScheduler() {
        scheduler.scheduleAtFixedRate(() -> {
            LocalDateTime now = LocalDateTime.now();
            authCodes.entrySet().removeIf(entry ->
                    now.isAfter(entry.getValue().getExpiredAt()));
        }, 1, 1, TimeUnit.MINUTES);
    }

    // 애플리케이션 종료시 스케줄러 정리
    public void shutdown() {
        scheduler.shutdown();
    }

    // 내부 클래스
    private static class AuthCodeData {
        private final String code;
        private final LocalDateTime expiredAt;

        public AuthCodeData(String code, LocalDateTime expiredAt) {
            this.code = code;
            this.expiredAt = expiredAt;
        }

        public String getCode() {
            return code;
        }

        public LocalDateTime getExpiredAt() {
            return expiredAt;
        }
    }
}