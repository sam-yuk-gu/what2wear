package com.samyukgu.what2wear.friend.service;

import com.samyukgu.what2wear.member.dao.MemberDAO;

public class FriendService {
    MemberDAO memberDAO;

    public FriendService(MemberDAO memberDAO) {
        this.memberDAO = memberDAO;
    }
}
