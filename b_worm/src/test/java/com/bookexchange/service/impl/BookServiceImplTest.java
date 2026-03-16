package com.bookexchange.service.impl;

import com.bookexchange.dto.request.BookRequest;
import com.bookexchange.dto.response.BookResponse;
import com.bookexchange.exception.BadRequestException;
import com.bookexchange.model.Book;
import com.bookexchange.model.Notification;
import com.bookexchange.model.Request;
import com.bookexchange.model.User;
import com.bookexchange.model.enums.BookCondition;
import com.bookexchange.model.enums.BookStatus;
import com.bookexchange.model.enums.NotificationType;
import com.bookexchange.model.enums.RequestStatus;
import com.bookexchange.model.enums.Role;
import com.bookexchange.repository.BookRepository;
import com.bookexchange.repository.RequestRepository;
import com.bookexchange.service.NotificationService;
import com.bookexchange.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookServiceImplTest {

    @Mock
    private BookRepository bookRepository;

    @Mock
    private RequestRepository requestRepository;

    @Mock
    private NotificationService notificationService;

    @Mock
    private UserService userService;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private BookServiceImpl bookService;

    @Captor
    private ArgumentCaptor<Book> bookCaptor;

    @Captor
    private ArgumentCaptor<Request> requestCaptor;

    private User donor;
    private User requester1;
    private User requester2;
    private User admin;
    private Book testBook;
    private BookRequest bookRequest;
    private Request pendingRequest1;
    private Request pendingRequest2;

    @BeforeEach
    void setUp() {
        // Setup donor user
        donor = new User();
        donor.setUserId(1L);
        donor.setUsername("donor");
        donor.setEmail("donor@test.com");
        donor.setFirstName("John");
        donor.setLastName("Doe");
        donor.setRole(Role.MEMBER);
        donor.setEnabled(true);

        // Setup requester 1
        requester1 = new User();
        requester1.setUserId(2L);
        requester1.setUsername("requester1");
        requester1.setEmail("requester1@test.com");
        requester1.setFirstName("Jane");
        requester1.setLastName("Smith");
        requester1.setRole(Role.MEMBER);
        requester1.setEnabled(true);

        // Setup requester 2
        requester2 = new User();
        requester2.setUserId(3L);
        requester2.setUsername("requester2");
        requester2.setEmail("requester2@test.com");
        requester2.setFirstName("Bob");
        requester2.setLastName("Johnson");
        requester2.setRole(Role.MEMBER);
        requester2.setEnabled(true);

        // Setup admin
        admin = new User();
        admin.setUserId(4L);
        admin.setUsername("admin");
        admin.setEmail("admin@test.com");
        admin.setFirstName("Admin");
        admin.setLastName("User");
        admin.setRole(Role.ADMIN);
        admin.setEnabled(true);

        // Setup test book
        testBook = Book.builder()
                .bookId(10L)
                .title("The Great Gatsby")
                .author("F. Scott Fitzgerald")
                .isbn("9780743273565")
                .description("A classic novel")
                .condition(BookCondition.GOOD)
                .status(BookStatus.AVAILABLE)
                .donatedBy(donor)
                .createdAt(LocalDateTime.now())
                .build();

        // Setup book request DTO
        bookRequest = new BookRequest();
        bookRequest.setTitle("New Book");
        bookRequest.setAuthor("New Author");
        bookRequest.setIsbn("1234567890");
        bookRequest.setDescription("Description");
        bookRequest.setCondition(BookCondition.NEW);

        // Setup pending requests
        pendingRequest1 = Request.builder()
                .requestId(100L)
                .book(testBook)
                .requester(requester1)
                .status(RequestStatus.PENDING)
                .createdAt(LocalDateTime.now().minusDays(1))
                .build();

        pendingRequest2 = Request.builder()
                .requestId(101L)
                .book(testBook)
                .requester(requester2)
                .status(RequestStatus.PENDING)
                .createdAt(LocalDateTime.now().minusHours(12))
                .build();

        // Setup security context mock
        lenient().when(securityContext.getAuthentication()).thenReturn(authentication);
        lenient().when(authentication.getName()).thenReturn(donor.getUsername());
        SecurityContextHolder.setContext(securityContext);
        lenient().when(userService.findByUsername(donor.getUsername())).thenReturn(donor);
    }

    @Test
    void testDonateBook_ShouldSaveBook_WhenRequestIsValid() {
        // Arrange
        when(bookRepository.save(any(Book.class))).thenAnswer(invocation -> {
            Book book = invocation.getArgument(0);
            book.setBookId(20L);
            return book;
        });

        // Act
        Book result = bookService.donateBook(bookRequest, donor);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getBookId()).isEqualTo(20L);
        assertThat(result.getTitle()).isEqualTo(bookRequest.getTitle());
        assertThat(result.getAuthor()).isEqualTo(bookRequest.getAuthor());
        assertThat(result.getIsbn()).isEqualTo(bookRequest.getIsbn());
        assertThat(result.getDescription()).isEqualTo(bookRequest.getDescription());
        assertThat(result.getCondition()).isEqualTo(bookRequest.getCondition());
        assertThat(result.getStatus()).isEqualTo(BookStatus.AVAILABLE);
        assertThat(result.getDonatedBy()).isEqualTo(donor);

        verify(bookRepository).save(any(Book.class));
    }

    @Test
    void testGetAllBooks_WithNoFilter_ShouldReturnAllBooks() {
        // Arrange
        List<Book> books = List.of(testBook, 
                Book.builder().bookId(11L).title("Another Book").status(BookStatus.AVAILABLE).build());
        when(bookRepository.findAll()).thenReturn(books);
        when(requestRepository.countByBookAndStatus(any(), any())).thenReturn(0L);
        when(requestRepository.countByBook(any())).thenReturn(0L);
        when(requestRepository.findByBookAndRequester(any(), any())).thenReturn(Optional.empty());

        // Act
        List<BookResponse> results = bookService.getAllBooks("all");

        // Assert
        assertThat(results).hasSize(2);
        verify(bookRepository).findAll();
    }

    @Test
    void testGetAllBooks_WithAvailableFilter_ShouldReturnAvailableBooks() {
        // Arrange
        List<Book> availableBooks = List.of(testBook);
        when(bookRepository.findAllAvailableBooks()).thenReturn(availableBooks);
        when(requestRepository.countByBookAndStatus(any(), any())).thenReturn(0L);
        when(requestRepository.countByBook(any())).thenReturn(0L);
        when(requestRepository.findByBookAndRequester(any(), any())).thenReturn(Optional.empty());

        // Act
        List<BookResponse> results = bookService.getAllBooks("available");

        // Assert
        assertThat(results).hasSize(1);
        verify(bookRepository).findAllAvailableBooks();
    }

    @Test
    void testGetAllBooks_WithMyDonationsFilter_WhenUserLoggedIn_ShouldReturnUserDonations() {
        // Arrange
        List<Book> donatedBooks = List.of(testBook);
        when(bookRepository.findAllByDonatedBy(donor)).thenReturn(donatedBooks);
        when(requestRepository.countByBookAndStatus(any(), any())).thenReturn(0L);
        when(requestRepository.countByBook(any())).thenReturn(0L);
        when(requestRepository.findByBookAndRequester(any(), any())).thenReturn(Optional.empty());

        // Act
        List<BookResponse> results = bookService.getAllBooks("mydonations");

        // Assert
        assertThat(results).hasSize(1);
        verify(bookRepository).findAllByDonatedBy(donor);
    }

    @Test
    void testGetAllBooks_WithMyDonationsFilter_WhenUserNotLoggedIn_ShouldReturnEmptyList() {
        // Arrange
        when(securityContext.getAuthentication()).thenReturn(null);
        SecurityContextHolder.setContext(securityContext);

        // Act
        List<BookResponse> results = bookService.getAllBooks("mydonations");

        // Assert
        assertThat(results).isEmpty();
    }

    @Test
    void testSearchBooks_ShouldReturnMatchingBooks() {
        // Arrange
        String keyword = "Gatsby";
        List<Book> searchResults = List.of(testBook);
        when(bookRepository.searchBooks(keyword)).thenReturn(searchResults);
        when(requestRepository.countByBookAndStatus(any(), any())).thenReturn(0L);
        when(requestRepository.countByBook(any())).thenReturn(0L);
        when(requestRepository.findByBookAndRequester(any(), any())).thenReturn(Optional.empty());

        // Act
        List<BookResponse> results = bookService.searchBooks(keyword);

        // Assert
        assertThat(results).hasSize(1);
        verify(bookRepository).searchBooks(keyword);
    }

    @Test
    void testGetBookDetails_WhenBookExists_ShouldReturnBookDetails() {
        // Arrange
        when(bookRepository.findById(10L)).thenReturn(Optional.of(testBook));
        when(requestRepository.countByBookAndStatus(testBook, RequestStatus.PENDING)).thenReturn(2L);
        when(requestRepository.countByBook(testBook)).thenReturn(2L);
        when(requestRepository.findByBookAndRequester(testBook, donor)).thenReturn(Optional.empty());

        // Act
        BookResponse result = bookService.getBookDetails(10L, donor);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getBookId()).isEqualTo(10L);
        assertThat(result.getTitle()).isEqualTo("The Great Gatsby");
        assertThat(result.getDonorName()).isEqualTo("John Doe");
        assertThat(result.getDonorUsername()).isEqualTo("donor");
        assertThat(result.getRequestCount()).isEqualTo(2);
        assertThat(result.getPendingRequestCount()).isEqualTo(2);
        assertThat(result.isRequestedByCurrentUser()).isFalse();
    }

    @Test
    void testGetBookDetails_WhenBookDoesNotExist_ShouldThrowException() {
        // Arrange
        when(bookRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> bookService.getBookDetails(999L, donor))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("Book not found");
    }

    @Test
    void testRequestBook_ShouldCreateRequest_WhenValid() {
        // Arrange
        when(bookRepository.findById(10L)).thenReturn(Optional.of(testBook));
        when(requestRepository.existsByBookAndRequesterAndStatus(testBook, requester1, RequestStatus.PENDING))
                .thenReturn(false);
        when(requestRepository.existsByBookAndRequesterAndStatus(testBook, requester1, RequestStatus.APPROVED))
                .thenReturn(false);
        when(requestRepository.save(any(Request.class))).thenAnswer(i -> i.getArgument(0));
        doNothing().when(notificationService).sendNotification(any(), any(), any(), any(), anyString());

        // Act
        bookService.requestBook(10L, requester1);

        // Assert
        verify(requestRepository).save(requestCaptor.capture());
        Request savedRequest = requestCaptor.getValue();
        assertThat(savedRequest.getBook()).isEqualTo(testBook);
        assertThat(savedRequest.getRequester()).isEqualTo(requester1);
        assertThat(savedRequest.getStatus()).isEqualTo(RequestStatus.PENDING);

        verify(bookRepository).save(bookCaptor.capture());
        Book updatedBook = bookCaptor.getValue();
        assertThat(updatedBook.getStatus()).isEqualTo(BookStatus.REQUESTED);

        verify(notificationService).sendNotification(
                eq(donor), eq(requester1), eq(testBook), eq(NotificationType.REQUEST_RECEIVED), anyString());
    }

    @Test
    void testRequestBook_WhenRequestingOwnBook_ShouldThrowException() {
        // Arrange
        when(bookRepository.findById(10L)).thenReturn(Optional.of(testBook));

        // Act & Assert
        assertThatThrownBy(() -> bookService.requestBook(10L, donor))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("You cannot request your own book");
    }

    @Test
    void testRequestBook_WhenBookIsBanned_ShouldThrowException() {
        // Arrange
        testBook.setStatus(BookStatus.BANNED);
        when(bookRepository.findById(10L)).thenReturn(Optional.of(testBook));

        // Act & Assert
        assertThatThrownBy(() -> bookService.requestBook(10L, requester1))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("banned");
    }

    @Test
    void testRequestBook_WhenBookIsTaken_ShouldThrowException() {
        // Arrange
        testBook.setStatus(BookStatus.TAKEN);
        when(bookRepository.findById(10L)).thenReturn(Optional.of(testBook));

        // Act & Assert
        assertThatThrownBy(() -> bookService.requestBook(10L, requester1))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("already been taken");
    }

    @Test
    void testRequestBook_WhenAlreadyHavePendingRequest_ShouldThrowException() {
        // Arrange
        when(bookRepository.findById(10L)).thenReturn(Optional.of(testBook));
        when(requestRepository.existsByBookAndRequesterAndStatus(testBook, requester1, RequestStatus.PENDING))
                .thenReturn(true);

        // Act & Assert
        assertThatThrownBy(() -> bookService.requestBook(10L, requester1))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("already have a pending request");
    }

    @Test
    void testApproveRequest_ShouldApproveAndRejectOthers() {
        // Arrange
        when(requestRepository.findById(100L)).thenReturn(Optional.of(pendingRequest1));
        when(requestRepository.findAllByBookAndStatus(testBook, RequestStatus.PENDING))
                .thenReturn(List.of(pendingRequest1, pendingRequest2));
        when(requestRepository.save(any(Request.class))).thenAnswer(i -> i.getArgument(0));
        when(bookRepository.save(any(Book.class))).thenAnswer(i -> i.getArgument(0));
        doNothing().when(notificationService).sendNotification(any(), any(), any(), any(), anyString());

        // Act
        bookService.approveRequest(100L, donor);

        // Assert
        // Verify approved request
        verify(requestRepository, times(2)).save(requestCaptor.capture());
        List<Request> savedRequests = requestCaptor.getAllValues();
        
        Request approvedRequest = savedRequests.stream()
                .filter(r -> r.getRequestId().equals(100L))
                .findFirst().orElse(null);
        assertThat(approvedRequest).isNotNull();
        assertThat(approvedRequest.getStatus()).isEqualTo(RequestStatus.APPROVED);

        Request rejectedRequest = savedRequests.stream()
                .filter(r -> r.getRequestId().equals(101L))
                .findFirst().orElse(null);
        assertThat(rejectedRequest).isNotNull();
        assertThat(rejectedRequest.getStatus()).isEqualTo(RequestStatus.REJECTED);

        // Verify book updated
        verify(bookRepository).save(bookCaptor.capture());
        Book updatedBook = bookCaptor.getValue();
        assertThat(updatedBook.getStatus()).isEqualTo(BookStatus.TAKEN);
        assertThat(updatedBook.getTakenBy()).isEqualTo(requester1);

        // Verify notifications sent
        verify(notificationService, times(2)).sendNotification(any(), any(), any(), any(), anyString());
    }

    @Test
    void testApproveRequest_WhenNotDonor_ShouldThrowException() {
        // Arrange
        when(requestRepository.findById(100L)).thenReturn(Optional.of(pendingRequest1));

        // Act & Assert
        assertThatThrownBy(() -> bookService.approveRequest(100L, requester1))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("Not authorized");
    }

    @Test
    void testApproveRequest_WhenRequestNotPending_ShouldThrowException() {
        // Arrange
        pendingRequest1.setStatus(RequestStatus.APPROVED);
        when(requestRepository.findById(100L)).thenReturn(Optional.of(pendingRequest1));

        // Act & Assert
        assertThatThrownBy(() -> bookService.approveRequest(100L, donor))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("not pending");
    }

    @Test
    void testRejectRequest_ShouldRejectAndUpdateBookIfNoPendingLeft() {
        // Arrange
        when(requestRepository.findById(100L)).thenReturn(Optional.of(pendingRequest1));
        when(requestRepository.save(any(Request.class))).thenAnswer(i -> i.getArgument(0));
        when(requestRepository.countByBookAndStatus(testBook, RequestStatus.PENDING)).thenReturn(0L);
        when(bookRepository.save(any(Book.class))).thenAnswer(i -> i.getArgument(0));
        doNothing().when(notificationService).sendNotification(any(), any(), any(), any(), anyString());

        // Act
        bookService.rejectRequest(100L, donor);

        // Assert
        verify(requestRepository).save(requestCaptor.capture());
        Request rejectedRequest = requestCaptor.getValue();
        assertThat(rejectedRequest.getStatus()).isEqualTo(RequestStatus.REJECTED);

        verify(bookRepository).save(bookCaptor.capture());
        Book updatedBook = bookCaptor.getValue();
        assertThat(updatedBook.getStatus()).isEqualTo(BookStatus.AVAILABLE);

        verify(notificationService).sendNotification(
                eq(requester1), eq(donor), eq(testBook), eq(NotificationType.REQUEST_REJECTED), anyString());
    }

    @Test
    void testCancelRequest_ShouldCancelAndUpdateBookIfNoPendingLeft() {
        // Arrange
        when(bookRepository.findById(10L)).thenReturn(Optional.of(testBook));
        when(requestRepository.findByBookAndRequester(testBook, requester1))
                .thenReturn(Optional.of(pendingRequest1));
        when(requestRepository.save(any(Request.class))).thenAnswer(i -> i.getArgument(0));
        when(requestRepository.countByBookAndStatus(testBook, RequestStatus.PENDING)).thenReturn(0L);
        when(bookRepository.save(any(Book.class))).thenAnswer(i -> i.getArgument(0));

        // Act
        bookService.cancelRequest(10L, requester1);

        // Assert
        verify(requestRepository).save(requestCaptor.capture());
        Request cancelledRequest = requestCaptor.getValue();
        assertThat(cancelledRequest.getStatus()).isEqualTo(RequestStatus.CANCELLED);

        verify(bookRepository).save(bookCaptor.capture());
        Book updatedBook = bookCaptor.getValue();
        assertThat(updatedBook.getStatus()).isEqualTo(BookStatus.AVAILABLE);
    }

    @Test
    void testBanBook_ShouldSetStatusToBanned() {
        // Arrange
        when(bookRepository.findById(10L)).thenReturn(Optional.of(testBook));
        when(bookRepository.save(any(Book.class))).thenAnswer(i -> i.getArgument(0));
        doNothing().when(notificationService).sendNotification(any(), any(), any(), any(), anyString());

        // Act
        bookService.banBook(10L, admin);

        // Assert
        verify(bookRepository).save(bookCaptor.capture());
        Book bannedBook = bookCaptor.getValue();
        assertThat(bannedBook.getStatus()).isEqualTo(BookStatus.BANNED);

        verify(notificationService).sendNotification(
                eq(donor), eq(admin), eq(testBook), eq(NotificationType.BOOK_BANNED), anyString());
    }

    @Test
    void testUnbanBook_ShouldSetStatusToAvailable() {
        // Arrange
        testBook.setStatus(BookStatus.BANNED);
        when(bookRepository.findById(10L)).thenReturn(Optional.of(testBook));
        when(bookRepository.save(any(Book.class))).thenAnswer(i -> i.getArgument(0));

        // Act
        bookService.unbanBook(10L, admin);

        // Assert
        verify(bookRepository).save(bookCaptor.capture());
        Book unbannedBook = bookCaptor.getValue();
        assertThat(unbannedBook.getStatus()).isEqualTo(BookStatus.AVAILABLE);
    }

    @Test
    void testGetUserDonations_ShouldReturnBooksDonatedByUser() {
        // Arrange
        List<Book> donations = List.of(testBook);
        when(bookRepository.findAllByDonatedBy(donor)).thenReturn(donations);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn(donor.getUsername());
        when(userService.findByUsername(donor.getUsername())).thenReturn(donor);
        when(requestRepository.countByBookAndStatus(any(), any())).thenReturn(0L);
        when(requestRepository.countByBook(any())).thenReturn(0L);
        when(requestRepository.findByBookAndRequester(any(), any())).thenReturn(Optional.empty());

        // Act
        List<BookResponse> results = bookService.getUserDonations(donor);

        // Assert
        assertThat(results).hasSize(1);
        assertThat(results.get(0).getTitle()).isEqualTo("The Great Gatsby");
    }

    @Test
    void testGetRequestsForDonator_ShouldReturnBooksWithRequests() {
        // Arrange
        List<Book> donatedBooks = List.of(testBook);
        when(bookRepository.findAllByDonatedBy(donor)).thenReturn(donatedBooks);
        when(requestRepository.findAllByBookOrderByCreatedAtAsc(testBook))
                .thenReturn(List.of(pendingRequest1, pendingRequest2));
        when(requestRepository.countByBookAndStatus(any(), any())).thenReturn(2L);
        when(requestRepository.countByBook(any())).thenReturn(2L);
        when(requestRepository.findByBookAndRequester(any(), any())).thenReturn(Optional.empty());
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn(donor.getUsername());
        when(userService.findByUsername(donor.getUsername())).thenReturn(donor);

        // Act
        List<BookResponse> results = bookService.getRequestsForDonator(donor);

        // Assert
        assertThat(results).hasSize(1);
        BookResponse result = results.get(0);
        assertThat(result.getRequestCount()).isEqualTo(2);
        assertThat(result.getPendingRequestCount()).isEqualTo(2);
    }
}