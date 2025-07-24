package com.samyukgu.what2wear.notification.service;

import com.samyukgu.what2wear.friend.dao.FriendDAO;
import com.samyukgu.what2wear.member.dao.MemberDAO;
import com.samyukgu.what2wear.member.model.Member;
import com.samyukgu.what2wear.notification.Model.Notification;
import com.samyukgu.what2wear.notification.dao.NotificationDAO;
import java.util.ArrayList;
import java.util.List;

public class NotificationService {
    private FriendDAO friendDAO;
    private NotificationDAO notificationDAO;
    private MemberDAO memberDAO;

    public NotificationService(FriendDAO friendDAO, NotificationDAO notificationDAO, MemberDAO memberDAO) {
        this.friendDAO = friendDAO;
        this.notificationDAO = notificationDAO;
        this.memberDAO = memberDAO;
    }

    // 친구 요청 불러오기
    public List<Member> getRequests(Long memberId){
        List<Notification> notifications = notificationDAO.findAllByReceiverIdOrderByDesc(memberId);
        List<Member> members = new ArrayList<>();
        for(Notification notification : notifications){
            members.add(memberDAO.findById(notification.getSenderId()));
        }
        return members;
    }

    // 친구 요청 수락
    public void acceptRequest(Long receiverId, Long senderId){
        friendDAO.acceptRequest(receiverId, senderId);
        notificationDAO.delete(receiverId, senderId);
    }

    // 친구 요청 거절
    public void rejectRequest(Long receiverId, Long senderId){
        friendDAO.rejectRequest(receiverId, senderId);
        notificationDAO.delete(receiverId, senderId);
    }


}
