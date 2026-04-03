package com.bookexchange.dto.response;

import com.bookexchange.model.enums.BookCondition;
import com.bookexchange.model.enums.BookStatus;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class BookResponse {
    // Core book fields
    private Long bookId;
    private String title;
    private String author;
    private String isbn;
    private String description;
    private BookCondition condition;
    private BookStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // Donor information
    private Long donorId;
    private String donorName;
    private String donorUsername;
    
    // Taker information
    private Long takerId;
    private String takerName;
    private String takerUsername;
    
    // Request statistics
    private int requestCount;
    private int pendingRequestCount;
    
    // Current user's request status
    private boolean requestedByCurrentUser;
    private String currentUserRequestStatus; // PENDING, APPROVED, REJECTED, CANCELLED
    
    // NEW: List of requests for this book (for donor view)
    private List<RequestResponse> requests;
    
    // Helper methods for templates
    public boolean isAvailable() {
        return status == BookStatus.AVAILABLE;
    }
    
    public boolean isBanned() {
        return status == BookStatus.BANNED;
    }
    
    public boolean isDonor(Long currentUserId) {
        return donorId != null && donorId.equals(currentUserId);
    }
    
    public boolean isTaker(Long currentUserId) {
        return takerId != null && takerId.equals(currentUserId);
    }
}