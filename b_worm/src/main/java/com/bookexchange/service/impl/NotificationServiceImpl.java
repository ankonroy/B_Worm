package com.bookexchange.service.impl;

import com.bookexchange.dto.response.NotificationResponse;
import com.bookexchange.model.Book;
import com.bookexchange.model.Notification;
import com.bookexchange.model.User;
import com.bookexchange.model.enums.NotificationType;
import com.bookexchange.repository.NotificationRepository;
import com.bookexchange.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;

    @Override
    public void sendNotification(User recipient, User sender, Book book, NotificationType type, String message) {
        if (recipient == null) {
            log.error("Cannot send notification: recipient is null");
            return;
        }

        Notification notification = Notification.builder()
            .recipient(recipient)        // Sets recipient_id
            .user(recipient)              // Sets user_id (same user)
            .sender(sender)               // Sets sender_id
            .book(book)                   // Sets book_id
            .message(message)              // The notification message
            .type(type)                    // REQUEST_RECEIVED, etc.
            .isRead(false)                 // New notifications are unread
            .createdAt(LocalDateTime.now()) // Current timestamp
            .build();

        notificationRepository.save(notification);
        log.info("Notification sent to user: {} about book: {}", recipient.getUsername(), 
                 book != null ? book.getTitle() : "unknown");
    }

    @Override
    public List<NotificationResponse> getUserNotifications(User user) {
        List<Notification> notifications = notificationRepository.findAllByUserOrderByCreatedAtDesc(user);
        return notifications.stream()
            .map(this::mapToResponse)
            .collect(Collectors.toList());
    }

    @Override
    public void markAsRead(Long notificationId, User user) {
        Notification notification = notificationRepository.findById(notificationId)
            .orElseThrow(() -> new RuntimeException("Notification not found"));

        if (!notification.getUser().equals(user) && !notification.getRecipient().equals(user)) {
            throw new RuntimeException("Unauthorized");
        }

        notification.setRead(true);
        notificationRepository.save(notification);
        log.info("Notification {} marked as read for user: {}", notificationId, user.getUsername());
    }

    @Override
    public void markAllAsRead(User user) {
        // Use the new method name
        notificationRepository.markAllAsReadByUser(user);
        log.info("All notifications marked as read for user: {}", user.getUsername());
    }

    @Override
    public long getUnreadCount(User user) {
        return notificationRepository.countByUserAndIsReadFalse(user);
    }

    private NotificationResponse mapToResponse(Notification notification) {
        NotificationResponse response = new NotificationResponse();
        response.setNotificationId(notification.getNotificationId());
        response.setMessage(notification.getMessage());
        response.setType(notification.getType());
        response.setRead(notification.isRead());
        response.setCreatedAt(notification.getCreatedAt());
        
        if (notification.getBook() != null) {
            response.setBookId(notification.getBook().getBookId());
            response.setBookTitle(notification.getBook().getTitle());
        }
        
        if (notification.getSender() != null) {
            response.setSenderId(notification.getSender().getUserId());
            response.setSenderName(notification.getSender().getFullName());
        }
        
        return response;
    }
}