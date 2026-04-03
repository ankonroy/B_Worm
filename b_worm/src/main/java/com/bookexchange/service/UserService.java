package com.bookexchange.service;

import com.bookexchange.dto.request.RegisterRequest;
import com.bookexchange.dto.response.UserResponse;
import com.bookexchange.model.User;

import java.util.List;

public interface UserService {
    UserResponse registerUser(RegisterRequest request);
    User findByUsername(String username);
    User getCurrentUser();
    UserResponse getUserProfile(Long userId);
    UserResponse updateProfile(Long userId, RegisterRequest request);
    void changePassword(Long userId, String oldPassword, String newPassword);
    List<UserResponse> getAllUsers();
    void disableUser(Long userId);
    void enableUser(Long userId);
}
