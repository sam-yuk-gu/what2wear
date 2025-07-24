package com.samyukgu.what2wear.member.service;

import com.samyukgu.what2wear.mail.common.PasswordUtils;
import com.samyukgu.what2wear.member.common.MemberConstants;
import com.samyukgu.what2wear.member.dao.MemberDAO;
import com.samyukgu.what2wear.member.model.Member;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
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
            e.printStackTrace();
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

    // 키워드로 사용자 닉네임 검색
    public List<Member> searchMember(Long memberId,String keyword){
        return dao.findByNicknameContaining(memberId, keyword);
    }

    // 닉네임 변경
    public Member changeNickname(Long memberId, String nickname){
        return dao.updateNicknameById(memberId, nickname);
    }

    // 이름 변경
    public Member changeName(Long memberId, String name){
        return dao.updateNameById(memberId, name);
    }

    // 이메일 변경
    public Member changeEmail(Long memberId, String email){
        return dao.updateEmailById(memberId, email);
    }

    // 비밀번호 변경
    public Member changePassword(Long memberId, String password){
        return dao.updatePasswordById(memberId, md5(password));
    }
    
    // 비밀번호 검증
    public boolean isPasswordMatched(Long memberId, String password){
        Member member = dao.findById(memberId);
        return member.getPassword().equals(md5(password));
    }

    // 회원 탈퇴 (deleted -> 'Y')
    public Member unregisterMember(Long memberId){
        return dao.updateDeletedById(memberId);
    }

    // 프로필 사진 변경
    public Member changeProfileImg(Long memberId,byte[] img){
        return dao.updateProfileImgById(memberId, img);
    }

    public boolean isValidToChange(String existCurPassword,String curPassword, String newPassword, String confirmNewPassword){
        if(!existCurPassword.equals(md5(curPassword))) // DB에 등록된 비밀번호와 사용자가 입력한 현재 비밀번호가 다를 경우
            return false;

        if(!newPassword.equals(confirmNewPassword)) // 입력한 새 비밀번호와 다시 입력한 새 비밀번호의 입력값이 다를 경우
            return false;

        if(!newPassword.matches(MemberConstants.PASSWORD_PATTERN)) // 패스워드 패턴이랑 일치하지 않으면
            return false;

        if(newPassword.length()<MemberConstants.PASSWORD_MIN_LENGTH) // 비밀번호 최소 길이보다 작으면
            return false;

        if(newPassword.length()>MemberConstants.PASSWORD_MAX_LENGTH) // 비밀번호 최대 길이보다 크면
            return false;

        return true;
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
