package com.samyukgu.what2wear.member.Session;

import com.samyukgu.what2wear.member.model.Member;
import com.samyukgu.what2wear.post.model.Post;

public class MemberSession {
    Member member;

    // 현재 세션의 회원 정보 저장
    public void setMember(Member member){
        this.member = member;
    }

    // 현재 세션의 회원 정보 반환
    public Member getMember() {
        return this.member;
    }

    // 회원 정보 초기화 (로그아웃 시 실행)
    public void clearMember(){
        this.member = null;
    }
}