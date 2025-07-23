package com.samyukgu.what2wear.friend.service;

import com.samyukgu.what2wear.friend.dao.FriendDAO;
import com.samyukgu.what2wear.member.dao.MemberDAO;
import com.samyukgu.what2wear.member.model.Member;
import java.util.List;

public class FriendService {
    MemberDAO memberDAO;
    FriendDAO friendDAO;

    public FriendService(MemberDAO memberDAO, FriendDAO friendDAO) {
        this.memberDAO = memberDAO;
        this.friendDAO = friendDAO;
    }
    
    // 친구 관계 검증
    public boolean isFriend(Long memberId1, Long memberId2){
        return friendDAO.isFriend(memberId1, memberId2);
    }

    // 친구 관계 검증
    public boolean isRequestPending(Long memberId1, Long memberId2){
        return friendDAO.isRequestPending(memberId1, memberId2);
    }

    // 사용자 친구 목록 조회
    public List<Member> getFriends(Long memberId){
        return friendDAO.findFriendsAll(memberId);
    }
    
    // 친구 요청
    public void addFriend(Long member1Id, Long member2Id){
        friendDAO.save(member1Id, member2Id);
    }

    // 친구 요청 수락
    public void acceptFriendRequest(Long member1Id, Long member2Id){
        friendDAO.acceptRequest(member1Id, member2Id);
    };

    // 친구 요청 거절
    public void rejectFriendRequest(Long member1Id, Long member2Id){
        friendDAO.rejectRequest(member1Id, member2Id);
    };

    // 친구 삭제
    public void deleteFriend(Long member1Id, Long member2Id){
        friendDAO.delete(member1Id, member2Id);
    }
}
