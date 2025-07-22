package com.samyukgu.what2wear.member.dao;

import com.samyukgu.what2wear.member.model.Member;
import java.util.List;

public interface MemberDAO {
    void save(Member member); // 회원 저장
    Member findById(Long id); // 회원 단일 조회 (id)
    Member findByAccountId(String accountId); // 회원 단일 조회 (account_id)
    Member findByAccountIdAndEmail(String accountId, String email); // 회원 단일 조회 (account_id, password)
    Member findByNameAndEmail(String name, String email); // 회원 단일 조회 (name, email) 아이디 찾기에 사용
    Member findByNickname(String nickname); // 회원 단일 조회 (nickname)
    List<Member> findAll(); // 모든 회원 조회
    Member findByAccountIdAndPassword(String accountId, String password); // 회원 단일 조회 (account_id, password) -> 로그인에 사용
    Member updatePasswordByAccountId(String accountId, String newPassword); // 회원 비밀번호 업데이트
}
