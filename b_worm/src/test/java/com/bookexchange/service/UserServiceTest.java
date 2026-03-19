package com.bookexchange.service;

import com.bookexchange.dto.request.RegisterRequest;
import com.bookexchange.model.User;
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
public class UserServiceTest {

    @Mock
    private UserService userService;

    @Test
    void testChangePassword() {
        userService.changePassword(1L, "oldPass", "newPass");
        verify(userService).changePassword(anyLong(), anyString(), anyString());
    }

    @Test
    void testDisableUser() {
        userService.disableUser(1L);
        verify(userService).disableUser(anyLong());
    }

    @Test
    void testEnableUser() {
        userService.enableUser(1L);
        verify(userService).enableUser(anyLong());
    }

    @Test
    void testFindByUsername() {
        userService.findByUsername("testuser");
        verify(userService).findByUsername("testuser");
    }

    @Test
    void testGetAllUsers() {
        userService.getAllUsers();
        verify(userService).getAllUsers();
    }

    @Test
    void testGetCurrentUser() {
        userService.getCurrentUser();
        verify(userService).getCurrentUser();
    }

    @Test
    void testGetUserProfile() {
        userService.getUserProfile(1L);
        verify(userService).getUserProfile(anyLong());
    }

    @Test
    void testRegisterUser() {
        RegisterRequest request = new RegisterRequest();
        request.setUsername("newuser");
        request.setEmail("new@test.com");
        request.setPassword("password");
        
        userService.registerUser(request);
        verify(userService).registerUser(any(RegisterRequest.class));
    }

    @Test
    void testUpdateProfile() {
        RegisterRequest request = new RegisterRequest();
        request.setFirstName("Updated");
        request.setLastName("Name");
        
        userService.updateProfile(1L, request);
        verify(userService).updateProfile(anyLong(), any(RegisterRequest.class));
    }
}