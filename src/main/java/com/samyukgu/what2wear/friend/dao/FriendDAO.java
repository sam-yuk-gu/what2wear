package com.samyukgu.what2wear.friend.dao;

import com.samyukgu.what2wear.member.model.Member;
import java.util.List;
// 작성자 : 백승준
public interface FriendDAO {
    void save(Long member1Id, Long member2Id); // 친구 추가
    void acceptRequest(Long member1Id, Long member2Id); // 친구 요청 수락 거절 업데이트
    void rejectRequest(Long member1Id, Long member2Id); // 친구 요청 수락 거절 업데이트
    void delete(Long member1Id, Long member2Id); // 친구 삭제
    List<Member> findFriendsAll(Long memberId); // 모든 친구 호출
    List<Member> findPendingFriendRequests(Long memberId); // 친구 요청 리스트 조회
    boolean isFriend(Long member1Id, Long member2Id); // 친구 관계 검증
    boolean isRequestPending(Long member1Id, Long member2Id); // 친구 요청 검증


    // 수락된 친구 ID 목록 조회
    List<Long> getAcceptedFriendIds(Long myId);
}
