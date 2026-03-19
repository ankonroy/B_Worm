package com.bookexchange.dto.response;

import com.bookexchange.model.enums.Role;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class UserResponse {
    private Long userId;
    private String username;
    private String email;
    private String firstName;
    private String lastName;
    private String fullName;
    private LocalDate dateOfBirth;
    private Role role;
    private boolean enabled;
    private LocalDateTime createdAt;
    private long booksDonated;
    private long booksReceived;
    private long pendingRequests;
}
