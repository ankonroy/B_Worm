package com.bookexchange.dto.response;

import com.bookexchange.model.enums.RequestStatus;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class RequestResponse {
    private Long requestId;
    private Long bookId;
    private String bookTitle;
    private Long requesterId;
    private String requesterName;
    private Long donorId;
    private String donorName;
    private RequestStatus status;
    private LocalDateTime createdAt;
}
