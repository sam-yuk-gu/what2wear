package com.samyukgu.what2wear.mail.service;

import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.MimeUtility;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

public class MailService {
    private static String address;
    private static String password;
    private static String fromName;
    
    // 초기화시에 application.properties에서 값 읽어오기
    public MailService() {
        Properties props = new Properties();
        try (InputStream is = ClassLoader.getSystemResourceAsStream("application.properties");
             InputStreamReader isr = new InputStreamReader(is, StandardCharsets.UTF_8)) {
            props.load(isr);
            address = props.getProperty("mail.address");
            password = props.getProperty("mail.password");
            fromName = "내일뭐입지";
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("can't read properties data");
        }
    }
    
    // 인증번호 발송 메서드
    public void sendAuth(String toMail, String authCode){
        String subject = "[ 내일뭐입지 ] 본인 인증을 위한 인증코드 안내메일입니다.";
        StringBuffer contents = new StringBuffer();
        contents.append("본인확인 인증코드입니다.\n");
        contents.append("본인 확인을 위해 아래의 인증코드를 화면에 입력해주세요.<br>");
        contents.append("<p><strong>").append(authCode).append("</strong></p><br>");
        contents.append("인증코드는 5분간 유효합니다.<br>");

        sendMail(toMail, subject, contents.toString());
    }

    // 회원이 가입한 (account_id) 발송
    public void sendId(String toMail, String accountId){
        String subject = "[ 내일뭐입지 ] 회원님의 아이디 정보입니다.";
        StringBuffer contents = new StringBuffer();
        contents.append("회원님이 생성하신 아이디는\n");
        contents.append("<p><strong>").append(accountId).append("</strong>입니다</p><br>");
        contents.append("로그인에 문제가 있거나 추가 도움이 필요하시면 언제든지 문의해주세요.");

        sendMail(toMail, subject, contents.toString());
    }

    // 임시 비밀번호 발송 및 임시 비밀번호 반환
    public String sendPassword(String toMail, String password){
        String subject = "[ 내일뭐입지 ] 회원님의 임시 비밀번호입니다.";
        StringBuffer contents = new StringBuffer();
        contents.append("회원님이 임시 비밀번호는\n");
        contents.append("<p><strong>").append(password).append("</strong>입니다</p><br>");
        contents.append("로그인에 문제가 있거나 추가 도움이 필요하시면 언제든지 문의해주세요.");

        sendMail(toMail, subject, contents.toString());

        return String.valueOf(Math.random());
    }
    
    // 메일 전송 메서드
    private void sendMail(String toMail, String subject, String contents){
        Properties props = new Properties();
        props.put("mail.smtp.host", "smtp.gmail.com"); // use Gmail
        props.put("mail.smtp.port", "587"); // set port

        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true"); // use TLS

        Session mailSession = Session.getInstance(props,
                new Authenticator() { // set authenticator
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(address, password);
                    }
                });

        try {
            MimeMessage message = new MimeMessage(mailSession);

            message.setFrom(new InternetAddress(address, MimeUtility.encodeText(fromName, "UTF-8", "B"))); // 한글의 경우 encoding 필요
            message.setRecipients(
                    Message.RecipientType.TO,
                    InternetAddress.parse(toMail)
            );
            message.setSubject(subject);
            message.setContent(contents.toString(), "text/html;charset=UTF-8"); // 내용 설정 (HTML 형식)
            message.setSentDate(new java.util.Date());

            Transport t = mailSession.getTransport("smtp");
            t.connect(address, password);
            t.sendMessage(message, message.getAllRecipients());
            t.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
