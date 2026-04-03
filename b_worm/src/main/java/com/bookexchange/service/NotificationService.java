package com.bookexchange.service;

import com.bookexchange.dto.response.NotificationResponse;
import com.bookexchange.model.User;

import java.util.List;

public interface NotificationService {
    void sendNotification(User recipient, User sender, com.bookexchange.model.Book book, com.bookexchange.model.enums.NotificationType type, String message);
    List<NotificationResponse> getUserNotifications(User user);
    void markAsRead(Long notificationId, User user);
    void markAllAsRead(User user);
    long getUnreadCount(User user);
}
