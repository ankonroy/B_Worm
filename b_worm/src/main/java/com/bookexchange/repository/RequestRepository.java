package com.bookexchange.repository;

import com.bookexchange.model.Book;
import com.bookexchange.model.Request;
import com.bookexchange.model.User;
import com.bookexchange.model.enums.RequestStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RequestRepository extends JpaRepository<Request, Long> {
    
    // Find all requests by requester
    List<Request> findAllByRequester(User requester);
    
    // Find all requests for a specific book
    List<Request> findAllByBook(Book book);
    
    // Find all requests for a book with specific status
    List<Request> findAllByBookAndStatus(Book book, RequestStatus status);
    
    // Find all requests for a book ordered by creation date
    List<Request> findAllByBookOrderByCreatedAtAsc(Book book);
    
    // Find specific request by book and requester
    Optional<Request> findByBookAndRequester(Book book, User requester);
    
    // Check if a request exists
    boolean existsByBookAndRequesterAndStatus(Book book, User requester, RequestStatus status);
    
    // Count requests for a book with specific status
    long countByBookAndStatus(Book book, RequestStatus status);
    
    // Count ALL requests for a book (ADD THIS METHOD)
    long countByBook(Book book);
    
    // Find all pending requests for a donor's books (useful for notifications)
    List<Request> findAllByBook_DonatedByAndStatus(User donor, RequestStatus status);
    
    // Find all requests made by a user with specific status
    List<Request> findAllByRequesterAndStatus(User requester, RequestStatus status);
}