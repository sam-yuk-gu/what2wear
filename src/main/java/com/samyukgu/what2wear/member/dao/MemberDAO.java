package com.samyukgu.what2wear.member.dao;

import com.samyukgu.what2wear.member.model.Member;
import java.util.List;

public interface MemberDAO {
    Member findById(Long id);
    List<Member> findAll();
}
