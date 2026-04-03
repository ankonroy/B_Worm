package com.bookexchange.repository;

import com.bookexchange.model.Book;
import com.bookexchange.model.User;
import com.bookexchange.model.enums.BookCondition;
import com.bookexchange.model.enums.BookStatus;
import com.bookexchange.model.enums.Role;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class  BookRepositoryTest {

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private TestEntityManager entityManager;

    private User persistUser(String username) {
        User user = User.builder()
                .username(username)
                .email(username + "@test.com")
                .password("pw")
                .firstName("First")
                .lastName("Last")
                .dateOfBirth(LocalDate.of(1990, 1, 1))
                .role(Role.MEMBER)
                .enabled(true)
                .createdAt(LocalDateTime.now())
                .build();
        return entityManager.persistAndFlush(user);
    }

    private Book persistBook(User donor, User taker, String title, String isbn, BookStatus status, LocalDateTime createdAt) {
        Book book = Book.builder()
                .title(title)
                .author("Author")
                .isbn(isbn)
                .description("Description")
                .condition(BookCondition.GOOD)
                .status(status)
                .createdAt(createdAt)
                .updatedAt(createdAt)
                .donatedBy(donor)
                .takenBy(taker)
                .build();
        return entityManager.persistAndFlush(book);
    }

    @Test
    void countByDonatedBy_ReturnsExpectedCount() {
        User donor = persistUser("donor-count");
        persistBook(donor, null, "A", "111", BookStatus.AVAILABLE, LocalDateTime.now().minusDays(2));
        persistBook(donor, null, "B", "112", BookStatus.REQUESTED, LocalDateTime.now().minusDays(1));

        assertThat(bookRepository.countByDonatedBy(donor)).isEqualTo(2);
    }

    @Test
    void countByTakenBy_ReturnsExpectedCount() {
        User donor = persistUser("donor-taken");
        User taker = persistUser("taker-count");
        persistBook(donor, taker, "Taken A", "221", BookStatus.TAKEN, LocalDateTime.now().minusDays(1));
        persistBook(donor, taker, "Taken B", "222", BookStatus.TAKEN, LocalDateTime.now());

        assertThat(bookRepository.countByTakenBy(taker)).isEqualTo(2);
    }

    @Test
    void findAllAvailableBooks_ReturnsOnlyAvailableInDescendingCreatedAt() {
        User donor = persistUser("donor-available");
        persistBook(donor, null, "Old Available", "331", BookStatus.AVAILABLE, LocalDateTime.now().minusDays(2));
        persistBook(donor, null, "New Available", "332", BookStatus.AVAILABLE, LocalDateTime.now().minusDays(1));
        persistBook(donor, null, "Not Available", "333", BookStatus.REQUESTED, LocalDateTime.now());

        List<Book> books = bookRepository.findAllAvailableBooks();

        assertThat(books).hasSize(2);
        assertThat(books).extracting(Book::getTitle).containsExactly("New Available", "Old Available");
        assertThat(books).allMatch(book -> book.getStatus() == BookStatus.AVAILABLE);
    }

    @Test
    void findAllByDonatedByAndStatusAndTakenBy_WorkAsExpected() {
        User donor = persistUser("donor-list");
        User other = persistUser("other-list");
        User taker = persistUser("taker-list");

        Book mineAvailable = persistBook(donor, null, "Mine", "441", BookStatus.AVAILABLE, LocalDateTime.now());
        persistBook(other, taker, "Other", "442", BookStatus.TAKEN, LocalDateTime.now());

        assertThat(bookRepository.findAllByDonatedBy(donor)).extracting(Book::getBookId).containsExactly(mineAvailable.getBookId());
        assertThat(bookRepository.findAllByStatus(BookStatus.TAKEN)).hasSize(1);
        assertThat(bookRepository.findAllByTakenBy(taker)).hasSize(1);
    }

    @Test
    void findByDonatedByOrderByCreatedAtDesc_AndFindRecentBooks_AreSortedDesc() {
        User donor = persistUser("donor-ordered");
        persistBook(donor, null, "Older", "771", BookStatus.AVAILABLE, LocalDateTime.now().minusDays(3));
        persistBook(donor, null, "Newest", "772", BookStatus.AVAILABLE, LocalDateTime.now().minusDays(1));

        List<Book> byDonor = bookRepository.findByDonatedByOrderByCreatedAtDesc(donor);
        List<Book> recent = bookRepository.findRecentBooks();

        assertThat(byDonor).extracting(Book::getTitle).startsWith("Newest");
        assertThat(recent).extracting(Book::getTitle).contains("Newest");
    }

    @Test
    void searchBooks_MatchesTitleAuthorOrIsbn_IgnoringCase() {
        User donor = persistUser("donor-search");
        persistBook(donor, null, "Domain Driven Design", "999-AAA", BookStatus.AVAILABLE, LocalDateTime.now());
        persistBook(donor, null, "Clean Code", "999-BBB", BookStatus.AVAILABLE, LocalDateTime.now());

        assertThat(bookRepository.searchBooks("domain")).extracting(Book::getTitle)
                .containsExactly("Domain Driven Design");
        assertThat(bookRepository.searchBooks("bbb")).extracting(Book::getTitle)
                .containsExactly("Clean Code");
    }
}
