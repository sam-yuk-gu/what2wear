package com.samyukgu.what2wear.member.service;

import com.samyukgu.what2wear.mail.common.PasswordUtils;
import com.samyukgu.what2wear.member.dao.MemberDAO;
import com.samyukgu.what2wear.member.model.Member;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.util.List;

public class MemberService {
    private final MemberDAO dao;

    public MemberService(MemberDAO dao) {
        this.dao = dao;
    }

    // 로그인 사용자의 정보가 맞다면 Member 리턴
    public Member login(String accountId, String password){
        return dao.findByAccountIdAndPassword(accountId, md5(password));
    }
    
    // id 기반 단일 회원 정보
    public Member getMember(Long id){
        return dao.findById(id);
    }
    
    // 회원가입 메서드
    public boolean signup(String accountId, String email, String nickname, String name, String password){
        try {
            Member member = new Member();
            member.setAccount_id(accountId);
            member.setEmail(email);
            member.setNickname(nickname);
            member.setName(name);
            member.setPassword(md5(password));
            dao.save(member);
            return true;
        }catch (RuntimeException e){
            return false;
        }
    }
    
    // 모든 회원 리턴
    public List<Member> getAllMembers(){
        return dao.findAll();
    }

    // 이름과 이메일로 단일조회하여 Member가 있다면 true, 없다면 false
    public boolean existsByNameAndEmail(String name, String email){
        return dao.findByNameAndEmail(name, email) != null;
    }

    // account_id로 단일조회하여 Member가 있다면 true, 없다면 false
    public boolean existsByAccountId(String accountId){
        return dao.findByAccountId(accountId) != null;
    }
    // 닉네임으로 단일조회하여 Member가 있다면 true, 없다면 false
    public boolean existsByNickname(String nickname){
        return dao.findByNickname(nickname) != null;
    }
    // 이름과 이메일로 회원 아이디 찾아 반환
    public String getAccountIdByNameAndEmail(String name, String email) {
        Member member = dao.findByNameAndEmail(name, email);
        if (member == null) return null;
        return member.getAccount_id();
    }

    // 회원의 accountId 정보를 받아 email로 반환
    public String findEmailByAccountId(String accountId) {
        Member member = dao.findByAccountId(accountId);
        return (member != null) ? member.getEmail() : null;
    }
    
    // 임시 비밀번호 발급, 저장
    public String generateAndSaveTempPassword(String account_id){
        String newPassword = PasswordUtils.generateTempPassword();
        String securedPassword = md5(newPassword);
        dao.updatePasswordByAccountId(account_id, securedPassword);

        return newPassword;
    }
    
    // md5 암호화 메서드
    private String md5(String plain) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] digest = md.digest(plain.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : digest) sb.append(String.format("%02x", b));
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }
}
