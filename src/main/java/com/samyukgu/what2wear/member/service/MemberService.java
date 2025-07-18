package com.samyukgu.what2wear.member.service;

import com.samyukgu.what2wear.member.dao.MemberDAO;
import com.samyukgu.what2wear.member.model.Member;
import java.util.List;

public class MemberService {
    private final MemberDAO dao;

    public MemberService(MemberDAO dao) {
        this.dao = dao;
    }

    public Member getMember(Long id){
        return dao.findById(id);
    }

    public List<Member> getAllMembers(){
        return dao.findAll();
    }
}
