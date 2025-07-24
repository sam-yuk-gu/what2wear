package com.samyukgu.what2wear.member.dao;

import com.samyukgu.what2wear.member.model.Member;
import java.util.List;

public interface MemberDAO {
    void save(Member member); // 회원 저장
    Member findById(Long id); // 회원 단일 조회 (id)
    Member findByAccountId(String accountId); // 회원 단일 조회 (account_id)
    Member findByAccountIdAndEmail(String accountId, String email); // 회원 단일 조회 (account_id, email)
    Member findByNameAndEmail(String name, String email); // 회원 단일 조회 (name, email) 아이디 찾기에 사용
    Member findByNickname(String nickname); // 회원 단일 조회 (nickname)
    List<Member> findAll(); // 모든 회원 조회
    Member findByAccountIdAndPassword(String accountId, String password); // 회원 단일 조회 (account_id, password) -> 로그인에 사용
    Member updatePasswordByAccountId(String accountId, String newPassword); // 회원 비밀번호 업데이트
    List<Member> findByNicknameContaining(Long memberId,String keyword); // 닉네임 검색 기능
    Member updateNicknameById(Long memberId, String password); // 닉네임 변경
    Member updateNameById(Long memberId, String name); // 이름 변경
    Member updateEmailById(Long memberId, String email); // 이메일 변경
    Member updatePasswordById(Long memberId, String password); // 비밀번호 변경
    Member updateProfileImgById(Long memberId, byte[] profileImg); // 프로필 사진 변경
    Member updateDeletedById(Long memberId); // 회원 탈퇴
}
