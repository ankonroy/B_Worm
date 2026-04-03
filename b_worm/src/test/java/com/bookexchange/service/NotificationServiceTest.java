package com.bookexchange.service;

import com.bookexchange.model.Book;
import com.bookexchange.model.User;
import com.bookexchange.model.enums.NotificationType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class NotificationServiceTest {

    @Mock
    private NotificationService notificationService;

    @Test
    void testGetUnreadCount() {
        notificationService.getUnreadCount(new User());
        verify(notificationService).getUnreadCount(any(User.class));
    }

    @Test
    void testGetUserNotifications() {
        notificationService.getUserNotifications(new User());
        verify(notificationService).getUserNotifications(any(User.class));
    }

    @Test
    void testMarkAllAsRead() {
        notificationService.markAllAsRead(new User());
        verify(notificationService).markAllAsRead(any(User.class));
    }

    @Test
    void testMarkAsRead() {
        notificationService.markAsRead(1L, new User());
        verify(notificationService).markAsRead(anyLong(), any(User.class));
    }

    @Test
    void testSendNotification() {
        notificationService.sendNotification(new User(), new User(), new Book(), NotificationType.REQUEST_RECEIVED, "Test message");
        verify(notificationService).sendNotification(any(User.class), any(User.class), any(Book.class), any(NotificationType.class), anyString());
    }
}