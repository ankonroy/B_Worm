package com.bookexchange.service;

import com.bookexchange.dto.request.BookRequest;
import com.bookexchange.dto.response.BookResponse;
import com.bookexchange.model.Book;
import com.bookexchange.model.User;

import java.util.List;

public interface BookService {
    Book donateBook(BookRequest request, User currentUser);
    List<BookResponse> getAllBooks(String filter);
    List<BookResponse> searchBooks(String keyword);
    BookResponse getBookDetails(Long bookId, User currentUser);
    List<BookResponse> getUserDonations(User user);
    void requestBook(Long bookId, User currentUser);
    void approveRequest(Long requestId, User donor);
    void rejectRequest(Long requestId, User donor);
    void cancelRequest(Long bookId, User requester);
    void banBook(Long bookId, User admin);
    void unbanBook(Long bookId, User admin);
    List<BookResponse> getRequestsForDonator(User user);
    List<BookResponse> getUserRequests(User user);
}
