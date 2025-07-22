package com.samyukgu.what2wear.friend.dao;

import com.samyukgu.what2wear.member.model.Member;
import java.util.List;

public interface FriendDAO {
    void save(Long member1Id, Long member2Id); // 친구 추가
    void acceptRequest(Long member1Id, Long member2Id); // 친구 요청 수락 거절 업데이트
    void rejectRequest(Long member1Id, Long member2Id); // 친구 요청 수락 거절 업데이트
    void delete(Long member1Id, Long member2Id); // 친구 삭제
    List<Member> findFriendsAll(Long memberId); // 모든 친구 호출
    List<Member> findPendingFriendRequests(Long memberId);
    boolean isFriend(Long member1Id, Long member2Id);
    boolean isRequestPending(Long member1Id, Long member2Id);
}
