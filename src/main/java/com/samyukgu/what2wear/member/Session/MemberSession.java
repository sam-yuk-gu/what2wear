package com.samyukgu.what2wear.member.Session;

import com.samyukgu.what2wear.member.model.Member;

public class MemberSession {
    private static Member member;
//    Member member;

    // 현재 세션의 회원 정보 저장
    public void setMember(Member member){
        this.member = member;
    }

    // 회원 정보 초기화 (로그아웃 시 실행)
    public void clearMember(){
        this.member = null;
    }

    public static Member getLoginMember() {
        return member;
    }
}