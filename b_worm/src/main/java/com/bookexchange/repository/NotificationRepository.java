package com.bookexchange.repository;

import com.bookexchange.model.Notification;
import com.bookexchange.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    
    // Find by user (for backward compatibility)
    List<Notification> findAllByUserOrderByCreatedAtDesc(User user);
    
    // Find by recipient (new method)
    List<Notification> findAllByRecipientOrderByCreatedAtDesc(User recipient);
    
    // Find unread by user
    List<Notification> findAllByUserAndIsReadFalse(User user);
    
    // Find unread by recipient
    List<Notification> findAllByRecipientAndIsReadFalse(User recipient);
    
    // Count unread by user
    long countByUserAndIsReadFalse(User user);
    
    // Count unread by recipient
    long countByRecipientAndIsReadFalse(User recipient);
    
    // Mark all as read by user
    @Modifying
    @Query("UPDATE Notification n SET n.isRead = true WHERE n.user = :user")
    void markAllAsReadByUser(@Param("user") User user);
    
    // Mark all as read by recipient
    @Modifying
    @Query("UPDATE Notification n SET n.isRead = true WHERE n.recipient = :recipient")
    void markAllAsReadByRecipient(@Param("recipient") User recipient);
    
    // Optional: Find by either user or recipient (if you need both)
    @Query("SELECT n FROM Notification n WHERE n.user = :user OR n.recipient = :user ORDER BY n.createdAt DESC")
    List<Notification> findAllByUserOrRecipientOrderByCreatedAtDesc(@Param("user") User user);
}