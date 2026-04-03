package com.bookexchange.service.impl;

import com.bookexchange.dto.request.RegisterRequest;
import com.bookexchange.dto.response.UserResponse;
import com.bookexchange.exception.BadRequestException;
import com.bookexchange.model.User;
import com.bookexchange.model.enums.Role;
import com.bookexchange.repository.UserRepository;
import com.bookexchange.service.UserService;
import lombok.RequiredArgsConstructor;

import org.hibernate.validator.internal.util.stereotypes.Lazy;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final @Lazy PasswordEncoder passwordEncoder;


    @Override
    public UserResponse registerUser(RegisterRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new BadRequestException("Username already exists");
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException("Email already exists");
        }

        User user = User.builder()
            .username(request.getUsername())
            .email(request.getEmail())
            .password(passwordEncoder.encode(request.getPassword()))
            .firstName(request.getFirstName())
            .lastName(request.getLastName())
            .dateOfBirth(request.getDateOfBirth())
            .role(Role.MEMBER)
            .enabled(true)
            .createdAt(LocalDateTime.now())
            .build();

        user = userRepository.save(user);

        return mapToResponse(user);
    }

    @Override
    public User findByUsername(String username) {
        return userRepository.findByUsername(username)
            .orElseThrow(() -> new BadRequestException("User not found: " + username));
    }

    @Override
    public User getCurrentUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return findByUsername(username);
    }

    @Override
    public UserResponse getUserProfile(Long userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new BadRequestException("User not found"));
        
        UserResponse response = mapToResponse(user);
        response.setBooksDonated(0L);
        response.setBooksReceived(0L);
        response.setPendingRequests(0L);
        return response;
    }

    @Override
    public UserResponse updateProfile(Long userId, RegisterRequest request) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new BadRequestException("User not found"));
        
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setEmail(request.getEmail());
        
        user = userRepository.save(user);
        return mapToResponse(user);
    }

    @Override
    public void changePassword(Long userId, String oldPassword, String newPassword) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new BadRequestException("User not found"));
        
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new BadRequestException("Old password is incorrect");
        }
        
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    @Override
    public List<UserResponse> getAllUsers() {
        return userRepository.findAll().stream()
            .map(this::mapToResponse)
            .collect(Collectors.toList());
    }

    @Override
    public void disableUser(Long userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new BadRequestException("User not found"));
        
        user.setEnabled(false);
        user.setDisabledAt(LocalDateTime.now());
        userRepository.save(user);
        // TODO: send notification
    }

    @Override
    public void enableUser(Long userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new BadRequestException("User not found"));
        
        user.setEnabled(true);
        user.setDisabledAt(null);
        userRepository.save(user);
        // TODO: send notification
    }

    private UserResponse mapToResponse(User user) {
        UserResponse response = new UserResponse();
        response.setUserId(user.getUserId());
        response.setUsername(user.getUsername());
        response.setEmail(user.getEmail());
        response.setFirstName(user.getFirstName());
        response.setLastName(user.getLastName());
        response.setFullName(user.getFullName());
        response.setDateOfBirth(user.getDateOfBirth());
        response.setRole(user.getRole());
        response.setEnabled(user.isEnabled());
        response.setCreatedAt(user.getCreatedAt());
        return response;
    }
}

