package com.bookexchange.controller;

import com.bookexchange.dto.request.BookRequest;
import com.bookexchange.model.User;
import com.bookexchange.service.BookService;
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
public class BookController {

    private final BookService bookService;
    private final UserService userService;

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

    @GetMapping("/books")
    public String listBooks(@RequestParam(required = false) String filter, Model model) {
        log.debug("Listing books with filter: {}", filter);
        model.addAttribute("books", bookService.getAllBooks(filter != null ? filter : "all"));
        model.addAttribute("filter", filter);
        model.addAttribute("content", "books/list");
        return "layouts/main";
    }

    @GetMapping("/books/search")
    public String searchBooks(@RequestParam String q, Model model) {
        log.debug("Searching books with query: {}", q);
        model.addAttribute("books", bookService.searchBooks(q));
        model.addAttribute("searchQuery", q);
        model.addAttribute("content", "books/list");
        return "layouts/main";
    }

    @GetMapping("/books/{id}")
    public String bookDetails(@PathVariable Long id, Model model) {
        log.debug("Getting book details for id: {}", id);
        User currentUser = getCurrentUser();
        model.addAttribute("book", bookService.getBookDetails(id, currentUser));
        model.addAttribute("currentUserId", currentUser != null ? currentUser.getUserId() : null);
        model.addAttribute("content", "books/detail");
        return "layouts/main";
    }

    @GetMapping("/books/donate")
    public String donateForm(Model model) {
        log.debug("Showing donate form");
        model.addAttribute("bookRequest", new BookRequest());
        model.addAttribute("content", "books/donate");
        return "layouts/main";
    }

    @PostMapping("/books/donate")
    public String donateBook(@Valid @ModelAttribute BookRequest bookRequest) {
        log.debug("Donating book: {}", bookRequest.getTitle());
        User currentUser = getCurrentUser();
        if (currentUser == null) {
            return "redirect:/auth/login";
        }
        bookService.donateBook(bookRequest, currentUser);
        return "redirect:/books";
    }

    @PostMapping("/books/{id}/request")
    public String requestBook(@PathVariable Long id) {
        log.debug("Requesting book id: {}", id);
        User currentUser = getCurrentUser();
        if (currentUser == null) {
            return "redirect:/auth/login";
        }
        bookService.requestBook(id, currentUser);
        return "redirect:/books/" + id;
    }

    @PostMapping("/books/requests/{requestId}/approve")
    public String approveRequest(@PathVariable Long requestId) {
        log.debug("Approving request id: {}", requestId);
        User currentUser = getCurrentUser();
        if (currentUser == null) {
            return "redirect:/auth/login";
        }
        bookService.approveRequest(requestId, currentUser);
        return "redirect:/books/requests/received";
    }

    @PostMapping("/books/requests/{requestId}/reject")
    public String rejectRequest(@PathVariable Long requestId) {
        log.debug("Rejecting request id: {}", requestId);
        User currentUser = getCurrentUser();
        if (currentUser == null) {
            return "redirect:/auth/login";
        }
        bookService.rejectRequest(requestId, currentUser);
        return "redirect:/books/requests/received";
    }

    @PostMapping("/books/{id}/cancel-request")
    public String cancelRequest(@PathVariable Long id) {
        log.debug("Cancelling request for book id: {}", id);
        User currentUser = getCurrentUser();
        if (currentUser == null) {
            return "redirect:/auth/login";
        }
        bookService.cancelRequest(id, currentUser);
        return "redirect:/books/" + id;
    }

    @GetMapping("/books/requests/received")
    public String receivedRequests(Model model) {
        log.debug("Showing received requests");
        User currentUser = getCurrentUser();
        if (currentUser == null) {
            return "redirect:/auth/login";
        }
        model.addAttribute("books", bookService.getRequestsForDonator(currentUser));
        model.addAttribute("content", "books/received-requests");
        return "layouts/main";
    }

    @GetMapping("/books/requests/my-requests")
    public String myRequests(Model model) {
        log.debug("Showing my requests");
        User currentUser = getCurrentUser();
        if (currentUser == null) {
            return "redirect:/auth/login";
        }
        model.addAttribute("books", bookService.getUserRequests(currentUser));
        model.addAttribute("content", "books/my-requests");
        return "layouts/main";
    }
}