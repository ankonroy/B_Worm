package com.bookexchange.dto.response;

import com.bookexchange.model.enums.NotificationType;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class NotificationResponse {
    private Long notificationId;
    private String message;
    private NotificationType type;
    private boolean read;
    private LocalDateTime createdAt;
    private Long bookId;
    private String bookTitle;
    private Long senderId;
    private String senderName;
}
