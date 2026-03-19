package com.bookexchange.controller;

import com.bookexchange.dto.request.RegisterRequest;
import com.bookexchange.dto.response.UserResponse;
import com.bookexchange.model.User;
import com.bookexchange.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
@Slf4j
public class ProfileController {

    private final UserService userService;

    private User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getPrincipal())) {
            String username = auth.getName();
            log.debug("Getting current user: {}", username);
            return userService.findByUsername(username);
        }
        return null;
    }

    @GetMapping("/profile")
    public String viewMyProfile(Model model) {
        User currentUser = getCurrentUser();
        if (currentUser == null) {
            return "redirect:/auth/login";
        }
        
        UserResponse userProfile = userService.getUserProfile(currentUser.getUserId());
        model.addAttribute("user", userProfile);
        model.addAttribute("isOwnProfile", true);
        model.addAttribute("content", "profile/view");
        return "layouts/main";
    }

    @GetMapping("/profile/{username}")
    public String viewProfile(@PathVariable String username, Model model) {
        User currentUser = getCurrentUser();
        User profileUser = userService.findByUsername(username);
        
        UserResponse userProfile = userService.getUserProfile(profileUser.getUserId());
        model.addAttribute("user", userProfile);
        model.addAttribute("isOwnProfile", currentUser != null && currentUser.getUsername().equals(username));
        model.addAttribute("content", "profile/view");
        return "layouts/main";
    }

    @GetMapping("/profile/edit")
    public String editProfile(Model model) {
        User currentUser = getCurrentUser();
        if (currentUser == null) {
            return "redirect:/auth/login";
        }
        
        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setFirstName(currentUser.getFirstName());
        registerRequest.setLastName(currentUser.getLastName());
        registerRequest.setEmail(currentUser.getEmail());
        registerRequest.setDateOfBirth(currentUser.getDateOfBirth());
        
        model.addAttribute("registerRequest", registerRequest);
        model.addAttribute("content", "profile/edit");
        return "layouts/main";
    }

    @PostMapping("/profile/update")
    public String updateProfile(@Valid @ModelAttribute RegisterRequest request) {
        User currentUser = getCurrentUser();
        if (currentUser == null) {
            return "redirect:/auth/login";
        }
        
        userService.updateProfile(currentUser.getUserId(), request);
        return "redirect:/profile";
    }

    @GetMapping("/profile/change-password")
    public String changePasswordForm(Model model) {
        User currentUser = getCurrentUser();
        if (currentUser == null) {
            return "redirect:/auth/login";
        }
        
        model.addAttribute("content", "profile/change-password");
        return "layouts/main";
    }

    @PostMapping("/profile/change-password")
    public String changePassword(@RequestParam String oldPassword, 
                                @RequestParam String newPassword,
                                @RequestParam String confirmPassword) {
        User currentUser = getCurrentUser();
        if (currentUser == null) {
            return "redirect:/auth/login";
        }
        
        if (!newPassword.equals(confirmPassword)) {
            return "redirect:/profile/change-password?error=password_mismatch";
        }
        
        userService.changePassword(currentUser.getUserId(), oldPassword, newPassword);
        return "redirect:/profile?success=password_changed";
    }
}