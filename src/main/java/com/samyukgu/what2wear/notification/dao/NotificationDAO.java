package com.samyukgu.what2wear.notification.dao;

import com.samyukgu.what2wear.notification.Model.Notification;
import java.util.List;

public interface NotificationDAO {
    List<Notification> findAllByReceiverIdOrderByDesc(Long receiverId);
    void save(Long receiverId, Long senderId);
    void delete(Long receiverId, Long senderId);
}
