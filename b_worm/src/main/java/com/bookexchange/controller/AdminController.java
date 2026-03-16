package com.bookexchange.controller;

import com.bookexchange.service.BookService;
import com.bookexchange.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final UserService userService;
    private final BookService bookService;

    @GetMapping("/dashboard")
    public String dashboard() {
        return "admin/dashboard";
    }

    @GetMapping("/users")
    public String users(Model model) {
        model.addAttribute("users", userService.getAllUsers());
        return "admin/users";
    }

    @PostMapping("/users/{id}/disable")
    public String disableUser(@PathVariable Long id) {
        userService.disableUser(id);
        return "redirect:/admin/users";
    }

    @PostMapping("/users/{id}/enable")
    public String enableUser(@PathVariable Long id) {
        userService.enableUser(id);
        return "redirect:/admin/users";
    }

    @GetMapping("/books")
    public String books(Model model) {
        // Get all books including banned
        model.addAttribute("books", bookService.getAllBooks("all"));
        return "admin/books";
    }

    @PostMapping("/books/{id}/ban")
    public String banBook(@PathVariable Long id) {
        bookService.banBook(id, null);
        return "redirect:/admin/books";
    }

    @PostMapping("/books/{id}/unban")
    public String unbanBook(@PathVariable Long id) {
        bookService.unbanBook(id, null);
        return "redirect:/admin/books";
    }
}
