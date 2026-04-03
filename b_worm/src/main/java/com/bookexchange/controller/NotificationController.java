package com.bookexchange.controller;

import com.bookexchange.model.User;
import com.bookexchange.service.NotificationService;
import com.bookexchange.service.UserService;
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
public class NotificationController {

    private final NotificationService notificationService;
    private final UserService userService;  // Add UserService dependency

    private User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getPrincipal())) {
            String username = auth.getName();
            log.debug("Getting current user: {}", username);
            return userService.findByUsername(username);
        }
        log.debug("No authenticated user found");
        return null;
    }

    @GetMapping("/notifications")
    public String notifications(Model model) {
        User currentUser = getCurrentUser();
        if (currentUser == null) {
            return "redirect:/auth/login";
        }
        
        log.debug("Fetching notifications for user: {}", currentUser.getUsername());
        model.addAttribute("notifications", notificationService.getUserNotifications(currentUser));
        model.addAttribute("unreadCount", notificationService.getUnreadCount(currentUser));
        model.addAttribute("content", "notifications/list");
        return "layouts/main";
    }

    @PostMapping("/notifications/{id}/read")
    public String markAsRead(@PathVariable Long id) {
        User currentUser = getCurrentUser();
        if (currentUser == null) {
            return "redirect:/auth/login";
        }
        
        log.debug("Marking notification {} as read for user: {}", id, currentUser.getUsername());
        notificationService.markAsRead(id, currentUser);
        return "redirect:/notifications";
    }

    @PostMapping("/notifications/read-all")
    public String markAllAsRead() {
        User currentUser = getCurrentUser();
        if (currentUser == null) {
            return "redirect:/auth/login";
        }
        
        log.debug("Marking all notifications as read for user: {}", currentUser.getUsername());
        notificationService.markAllAsRead(currentUser);
        return "redirect:/notifications";
    }
}