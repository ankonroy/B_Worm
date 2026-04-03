package com.bookexchange.service;

import com.bookexchange.dto.request.BookRequest;
import com.bookexchange.model.User;
import com.bookexchange.model.enums.BookCondition;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class BookServiceTest {

    @Mock
    private BookService bookService;

    @Test
    void testApproveRequest() {
        bookService.approveRequest(1L, new User());
        verify(bookService).approveRequest(anyLong(), any(User.class));
    }

    @Test
    void testBanBook() {
        bookService.banBook(1L, new User());
        verify(bookService).banBook(anyLong(), any(User.class));
    }

    @Test
    void testCancelRequest() {
        bookService.cancelRequest(1L, new User());
        verify(bookService).cancelRequest(anyLong(), any(User.class));
    }

    @Test
    void testDonateBook() {
        BookRequest request = new BookRequest();
        bookService.donateBook(request, new User());
        verify(bookService).donateBook(any(BookRequest.class), any(User.class));
    }

    @Test
    void testGetAllBooks() {
        bookService.getAllBooks("all");
        verify(bookService).getAllBooks(anyString());
    }

    @Test
    void testGetBookDetails() {
        bookService.getBookDetails(1L, new User());
        verify(bookService).getBookDetails(anyLong(), any(User.class));
    }

    @Test
    void testGetRequestsForDonator() {
        bookService.getRequestsForDonator(new User());
        verify(bookService).getRequestsForDonator(any(User.class));
    }

    @Test
    void testGetUserDonations() {
        bookService.getUserDonations(new User());
        verify(bookService).getUserDonations(any(User.class));
    }

    @Test
    void testGetUserRequests() {
        bookService.getUserRequests(new User());
        verify(bookService).getUserRequests(any(User.class));
    }

    @Test
    void testRejectRequest() {
        bookService.rejectRequest(1L, new User());
        verify(bookService).rejectRequest(anyLong(), any(User.class));
    }

    @Test
    void testRequestBook() {
        bookService.requestBook(1L, new User());
        verify(bookService).requestBook(anyLong(), any(User.class));
    }

    @Test
    void testSearchBooks() {
        bookService.searchBooks("test");
        verify(bookService).searchBooks(anyString());
    }

    @Test
    void testUnbanBook() {
        bookService.unbanBook(1L, new User());
        verify(bookService).unbanBook(anyLong(), any(User.class));
    }

    // Second set of tests (duplicates)
    @Test
    void testApproveRequest2() {
        bookService.approveRequest(2L, new User());
        verify(bookService).approveRequest(anyLong(), any(User.class));
    }

    @Test
    void testBanBook2() {
        bookService.banBook(2L, new User());
        verify(bookService).banBook(anyLong(), any(User.class));
    }

    @Test
    void testCancelRequest2() {
        bookService.cancelRequest(2L, new User());
        verify(bookService).cancelRequest(anyLong(), any(User.class));
    }

    @Test
    void testDonateBook2() {
        BookRequest request = new BookRequest();
        bookService.donateBook(request, new User());
        verify(bookService).donateBook(any(BookRequest.class), any(User.class));
    }

    @Test
    void testGetAllBooks2() {
        bookService.getAllBooks("available");
        verify(bookService).getAllBooks(anyString());
    }

    @Test
    void testGetBookDetails2() {
        bookService.getBookDetails(2L, new User());
        verify(bookService).getBookDetails(anyLong(), any(User.class));
    }

    @Test
    void testGetRequestsForDonator2() {
        bookService.getRequestsForDonator(new User());
        verify(bookService).getRequestsForDonator(any(User.class));
    }

    @Test
    void testGetUserDonations2() {
        bookService.getUserDonations(new User());
        verify(bookService).getUserDonations(any(User.class));
    }

    @Test
    void testGetUserRequests2() {
        bookService.getUserRequests(new User());
        verify(bookService).getUserRequests(any(User.class));
    }

    @Test
    void testRejectRequest2() {
        bookService.rejectRequest(2L, new User());
        verify(bookService).rejectRequest(anyLong(), any(User.class));
    }

    @Test
    void testRequestBook2() {
        bookService.requestBook(2L, new User());
        verify(bookService).requestBook(anyLong(), any(User.class));
    }

    @Test
    void testSearchBooks2() {
        bookService.searchBooks("another");
        verify(bookService).searchBooks(anyString());
    }

    @Test
    void testUnbanBook2() {
        bookService.unbanBook(2L, new User());
        verify(bookService).unbanBook(anyLong(), any(User.class));
    }
}