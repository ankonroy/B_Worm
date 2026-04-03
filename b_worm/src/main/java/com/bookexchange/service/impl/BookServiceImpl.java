package com.bookexchange.service.impl;

import com.bookexchange.dto.request.BookRequest;
import com.bookexchange.dto.response.BookResponse;
import com.bookexchange.dto.response.RequestResponse;
import com.bookexchange.exception.BadRequestException;
import com.bookexchange.model.*;
import com.bookexchange.model.enums.*;
import com.bookexchange.repository.*;
import com.bookexchange.service.BookService;
import com.bookexchange.service.NotificationService;
import com.bookexchange.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class BookServiceImpl implements BookService {

    private final BookRepository bookRepository;
    private final RequestRepository requestRepository;
    private final NotificationService notificationService;
    private final UserService userService;

    @Override
    public Book donateBook(BookRequest request, User currentUser) {
        Book book = Book.builder()
            .title(request.getTitle())
            .author(request.getAuthor())
            .isbn(request.getIsbn())
            .description(request.getDescription())
            .condition(request.getCondition())
            .status(BookStatus.AVAILABLE)
            .donatedBy(currentUser)
            .build();

        book = bookRepository.save(book);
        log.info("Book donated: {} by user: {}", book.getTitle(), currentUser.getUsername());
        return book;
    }

    @Override
    public List<BookResponse> getAllBooks(String filter) {
        User currentUser = getCurrentUser();
        List<Book> books;
        
        if ("available".equalsIgnoreCase(filter)) {
            books = bookRepository.findAllAvailableBooks();
            log.debug("Filtering available books, count: {}", books.size());
        } else if ("mydonations".equalsIgnoreCase(filter)) {
            if (currentUser == null) {
                log.debug("No authenticated user for mydonations filter");
                return List.of();
            }
            books = bookRepository.findAllByDonatedBy(currentUser);
            log.debug("Filtering my donations for user: {}, count: {}", currentUser.getUsername(), books.size());
        } else {
            books = bookRepository.findAll();
            log.debug("Showing all books, count: {}", books.size());
        }

        return books.stream()
            .map(book -> mapToResponse(book, currentUser))
            .collect(Collectors.toList());
    }

    @Override
    public List<BookResponse> searchBooks(String keyword) {
        User currentUser = getCurrentUser();
        List<Book> books = bookRepository.searchBooks(keyword);
        log.debug("Search books with keyword: {}, found: {}", keyword, books.size());
        return books.stream()
            .map(book -> mapToResponse(book, currentUser))
            .collect(Collectors.toList());
    }

    @Override
    public BookResponse getBookDetails(Long bookId, User currentUser) {
        Book book = bookRepository.findById(bookId)
            .orElseThrow(() -> new BadRequestException("Book not found with id: " + bookId));
        
        return mapToResponse(book, currentUser);
    }

    @Override
    public List<BookResponse> getUserDonations(User user) {
        List<Book> books = bookRepository.findAllByDonatedBy(user);
        return books.stream()
            .map(book -> mapToResponse(book, getCurrentUser()))
            .collect(Collectors.toList());
    }

    @Override
    public void requestBook(Long bookId, User requester) {
        Book book = bookRepository.findById(bookId)
            .orElseThrow(() -> new BadRequestException("Book not found"));

        // Check if user is trying to request their own book
        if (book.getDonatedBy().getUserId().equals(requester.getUserId())) {
            throw new BadRequestException("You cannot request your own book");
        }

        // Check if book is banned
        if (book.getStatus() == BookStatus.BANNED) {
            throw new BadRequestException("This book has been banned and cannot be requested");
        }

        // Check if book is already taken
        if (book.getStatus() == BookStatus.TAKEN) {
            throw new BadRequestException("This book has already been taken");
        }

        // Check if user already has a pending request for this book
        if (requestRepository.existsByBookAndRequesterAndStatus(book, requester, RequestStatus.PENDING)) {
            throw new BadRequestException("You already have a pending request for this book");
        }

        // Check if user already has an approved request for this book
        if (requestRepository.existsByBookAndRequesterAndStatus(book, requester, RequestStatus.APPROVED)) {
            throw new BadRequestException("You already have an approved request for this book");
        }

        Request request = Request.builder()
            .book(book)
            .requester(requester)
            .status(RequestStatus.PENDING)
            .build();

        requestRepository.save(request);

        // IMPORTANT: Only change book status to REQUESTED if it's currently AVAILABLE
        // This allows multiple requests while indicating the book has interest
        if (book.getStatus() == BookStatus.AVAILABLE) {
            book.setStatus(BookStatus.REQUESTED);
            bookRepository.save(book);
        }

        // Send notification to donor
        notificationService.sendNotification(
            book.getDonatedBy(), 
            requester, 
            book, 
            NotificationType.REQUEST_RECEIVED, 
            requester.getFullName() + " requested your book '" + book.getTitle() + "'"
        );
        
        log.info("Book request created: {} by user: {}", book.getTitle(), requester.getUsername());
    }

    @Override
    public void approveRequest(Long requestId, User donor) {
        Request request = requestRepository.findById(requestId)
            .orElseThrow(() -> new BadRequestException("Request not found"));

        if (!request.getBook().isDonatedBy(donor)) {
            throw new BadRequestException("Not authorized to approve this request");
        }

        if (request.getStatus() != RequestStatus.PENDING) {
            throw new BadRequestException("Request is not pending");
        }

        // Reject all other pending requests for this book
        List<Request> otherRequests = requestRepository.findAllByBookAndStatus(request.getBook(), RequestStatus.PENDING);
        otherRequests.stream()
            .filter(r -> !r.getRequestId().equals(requestId))
            .forEach(r -> {
                r.setStatus(RequestStatus.REJECTED);
                requestRepository.save(r);
                notificationService.sendNotification(
                    r.getRequester(), 
                    donor, 
                    r.getBook(), 
                    NotificationType.REQUEST_REJECTED, 
                    "Your request for '" + r.getBook().getTitle() + "' was rejected because another request was approved."
                );
            });

        // Approve this request
        request.setStatus(RequestStatus.APPROVED);
        requestRepository.save(request);

        // Update book - set as TAKEN and assign to requester
        request.getBook().setStatus(BookStatus.TAKEN);
        request.getBook().setTakenBy(request.getRequester());
        bookRepository.save(request.getBook());

        // Notify the approved requester
        notificationService.sendNotification(
            request.getRequester(), 
            donor, 
            request.getBook(), 
            NotificationType.REQUEST_APPROVED, 
            "Your request for '" + request.getBook().getTitle() + "' has been approved! You can now collect the book."
        );
        
        log.info("Request approved: {} by donor: {}", requestId, donor.getUsername());
    }

    @Override
    public void rejectRequest(Long requestId, User donor) {
        Request request = requestRepository.findById(requestId)
            .orElseThrow(() -> new BadRequestException("Request not found"));

        if (!request.getBook().isDonatedBy(donor)) {
            throw new BadRequestException("Not authorized to reject this request");
        }

        if (request.getStatus() != RequestStatus.PENDING) {
            throw new BadRequestException("Request is not pending");
        }

        request.setStatus(RequestStatus.REJECTED);
        requestRepository.save(request);

        notificationService.sendNotification(
            request.getRequester(), 
            donor, 
            request.getBook(), 
            NotificationType.REQUEST_REJECTED, 
            "Your request for '" + request.getBook().getTitle() + "' was rejected."
        );

        // Check if there are any pending requests left
        long pendingCount = requestRepository.countByBookAndStatus(request.getBook(), RequestStatus.PENDING);
        
        // If no pending requests and book is not taken, set back to AVAILABLE
        if (pendingCount == 0 && request.getBook().getStatus() != BookStatus.TAKEN) {
            request.getBook().setStatus(BookStatus.AVAILABLE);
            bookRepository.save(request.getBook());
        }
        
        log.info("Request rejected: {} by donor: {}", requestId, donor.getUsername());
    }

    @Override
    public void cancelRequest(Long bookId, User requester) {
        Book book = bookRepository.findById(bookId)
            .orElseThrow(() -> new BadRequestException("Book not found"));

        Request request = requestRepository.findByBookAndRequester(book, requester)
            .orElseThrow(() -> new BadRequestException("No request found for this book"));

        if (request.getStatus() != RequestStatus.PENDING) {
            throw new BadRequestException("Cannot cancel this request as it is not pending");
        }

        request.setStatus(RequestStatus.CANCELLED);
        requestRepository.save(request);

        // Check if there are any pending requests left
        long pendingCount = requestRepository.countByBookAndStatus(book, RequestStatus.PENDING);
        
        // If no pending requests and book is not taken, set back to AVAILABLE
        if (pendingCount == 0 && book.getStatus() != BookStatus.TAKEN) {
            book.setStatus(BookStatus.AVAILABLE);
            bookRepository.save(book);
        }
        
        log.info("Request cancelled: book {} by user: {}", bookId, requester.getUsername());
    }

    @Override
    public void banBook(Long bookId, User admin) {
        Book book = bookRepository.findById(bookId)
            .orElseThrow(() -> new BadRequestException("Book not found"));

        book.setStatus(BookStatus.BANNED);
        bookRepository.save(book);

        notificationService.sendNotification(
            book.getDonatedBy(), 
            admin, 
            book, 
            NotificationType.BOOK_BANNED, 
            "Your book '" + book.getTitle() + "' has been banned by admin."
        );
        
        log.info("Book banned: {} by admin: {}", bookId, admin.getUsername());
    }

    @Override
    public void unbanBook(Long bookId, User admin) {
        Book book = bookRepository.findById(bookId)
            .orElseThrow(() -> new BadRequestException("Book not found"));

        book.setStatus(BookStatus.AVAILABLE);
        bookRepository.save(book);
        
        log.info("Book unbanned: {} by admin: {}", bookId, admin.getUsername());
    }

    @Override
    public List<BookResponse> getRequestsForDonator(User donor) {
        // Get all books donated by this user
        List<Book> books = bookRepository.findAllByDonatedBy(donor);
        User currentUser = getCurrentUser();
        
        return books.stream()
            .map(book -> {
                BookResponse response = mapToResponse(book, currentUser);
                
                // Get all requests for this book
                List<Request> requests = requestRepository.findAllByBookOrderByCreatedAtAsc(book);
                
                // Convert to RequestResponse DTOs
                List<RequestResponse> requestResponses = requests.stream()
                    .map(req -> {
                        RequestResponse reqResponse = new RequestResponse();
                        reqResponse.setRequestId(req.getRequestId());
                        reqResponse.setBookId(book.getBookId());
                        reqResponse.setBookTitle(book.getTitle());
                        reqResponse.setRequesterId(req.getRequester().getUserId());
                        reqResponse.setRequesterName(req.getRequester().getFullName());
                        reqResponse.setDonorId(donor.getUserId());
                        reqResponse.setDonorName(donor.getFullName());
                        reqResponse.setStatus(req.getStatus());
                        reqResponse.setCreatedAt(req.getCreatedAt());
                        return reqResponse;
                    })
                    .collect(Collectors.toList());
                
                response.setRequests(requestResponses);
                return response;
            })
            .collect(Collectors.toList());
    }

    @Override
    public List<BookResponse> getUserRequests(User user) {
        // This would need implementation based on your requirements
        // For now, returning empty list
        return List.of();
    }

    private User getCurrentUser() {
        try {
            String username = SecurityContextHolder.getContext().getAuthentication().getName();
            if (username != null && !"anonymousUser".equals(username)) {
                return userService.findByUsername(username);
            }
        } catch (Exception e) {
            log.error("Error getting current user", e);
        }
        return null;
    }

    private BookResponse mapToResponse(Book book, User currentUser) {
        BookResponse response = new BookResponse();
        response.setBookId(book.getBookId());
        response.setTitle(book.getTitle());
        response.setAuthor(book.getAuthor());
        response.setIsbn(book.getIsbn());
        response.setDescription(book.getDescription());
        response.setCondition(book.getCondition());
        response.setStatus(book.getStatus());
        response.setCreatedAt(book.getCreatedAt());
        response.setUpdatedAt(book.getUpdatedAt());
        
        // Donor information
        if (book.getDonatedBy() != null) {
            response.setDonorId(book.getDonatedBy().getUserId());
            response.setDonorName(book.getDonatedBy().getFullName());
            response.setDonorUsername(book.getDonatedBy().getUsername());
        }
        
        // Taker information
        if (book.getTakenBy() != null) {
            response.setTakerId(book.getTakenBy().getUserId());
            response.setTakerName(book.getTakenBy().getFullName());
            response.setTakerUsername(book.getTakenBy().getUsername());
        }
        
        // Request statistics
        long pendingCount = requestRepository.countByBookAndStatus(book, RequestStatus.PENDING);
        long totalCount = requestRepository.countByBook(book);
        
        response.setRequestCount((int) totalCount);
        response.setPendingRequestCount((int) pendingCount);
        
        // Current user's request status (if logged in)
        if (currentUser != null) {
            requestRepository.findByBookAndRequester(book, currentUser).ifPresent(request -> {
                response.setRequestedByCurrentUser(true);
                response.setCurrentUserRequestStatus(request.getStatus().name());
            });
        } else {
            response.setRequestedByCurrentUser(false);
        }
        
        return response;
    }
}