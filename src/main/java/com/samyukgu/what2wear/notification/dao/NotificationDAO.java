package com.samyukgu.what2wear.notification.dao;

import com.samyukgu.what2wear.notification.Model.Notification;
import java.util.List;

// 작성자 : 백승준
public interface NotificationDAO {
    List<Notification> findAllByReceiverIdOrderByDesc(Long receiverId); // 친구 요청 알림 최신순으로 조회
    void save(Long receiverId, Long senderId); // 알림 등록
    void delete(Long receiverId, Long senderId); // 알림 제거
}
