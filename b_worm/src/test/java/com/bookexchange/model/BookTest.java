package com.bookexchange.model;

import com.bookexchange.model.enums.BookCondition;
import com.bookexchange.model.enums.BookStatus;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class BookTest {

    @Test
    void testBuilder() {
        User donator = new User();
        donator.setUserId(1L);

        Book book = Book.builder()
                .title("The Matrix")
                .author("Wachowski")
                .isbn("12345")
                .description("Sci-Fi")
                .condition(BookCondition.GOOD)
                .status(BookStatus.AVAILABLE)
                .donatedBy(donator)
                .build();

        assertNotNull(book);
        assertEquals("The Matrix", book.getTitle());
        assertEquals("Wachowski", book.getAuthor());
        assertEquals(BookStatus.AVAILABLE, book.getStatus());
        assertEquals(donator, book.getDonatedBy());
    }

    @Test
    void testGettersAndSetters() {
        Book book = new Book();
        book.setBookId(1L);
        book.setTitle("Test Title");
        book.setAuthor("Test Author");
        book.setIsbn("000-000");
        book.setDescription("Test Desc");
        book.setCondition(BookCondition.NEW);
        book.setStatus(BookStatus.REQUESTED);

        LocalDateTime now = LocalDateTime.now();
        book.setCreatedAt(now);
        book.setUpdatedAt(now);

        assertEquals(1L, book.getBookId());
        assertEquals("Test Title", book.getTitle());
        assertEquals("Test Author", book.getAuthor());
        assertEquals("000-000", book.getIsbn());
        assertEquals("Test Desc", book.getDescription());
        assertEquals(BookCondition.NEW, book.getCondition());
        assertEquals(BookStatus.REQUESTED, book.getStatus());
        assertEquals(now, book.getCreatedAt());
        assertEquals(now, book.getUpdatedAt());
    }

    @Test
    void testIsAvailable() {
        Book book = new Book();
        book.setStatus(BookStatus.AVAILABLE);
        assertTrue(book.isAvailable());

        book.setStatus(BookStatus.TAKEN);
        assertFalse(book.isAvailable());
    }

    @Test
    void testIsBanned() {
        Book book = new Book();
        book.setStatus(BookStatus.BANNED);
        assertTrue(book.isBanned());

        book.setStatus(BookStatus.AVAILABLE);
        assertFalse(book.isBanned());
    }

    @Test
    void testIsDonatedBy() {
        User donator = new User();
        donator.setUserId(1L);

        User otherUser = new User();
        otherUser.setUserId(2L);

        Book book = new Book();
        book.setDonatedBy(donator);

        assertTrue(book.isDonatedBy(donator));
        assertFalse(book.isDonatedBy(otherUser));
    }

    @Test
    void testPreUpdate() {
        Book book = new Book();
        LocalDateTime originalTime = LocalDateTime.now().minusDays(1);
        book.setUpdatedAt(originalTime);

        book.preUpdate();

        assertNotNull(book.getUpdatedAt());
        assertNotEquals(originalTime, book.getUpdatedAt());
        assertTrue(book.getUpdatedAt().isAfter(originalTime));
    }

    @Test
    void testEqualsAndHashCode() {
        LocalDateTime now = LocalDateTime.now();

        Book book1 = new Book();
        book1.setBookId(1L);
        book1.setTitle("Title");
        book1.setCreatedAt(now);
        book1.setUpdatedAt(now);

        Book book2 = new Book();
        book2.setBookId(1L);
        book2.setTitle("Title");
        book2.setCreatedAt(now);
        book2.setUpdatedAt(now);

        assertEquals(book1, book2);
        assertEquals(book1.hashCode(), book2.hashCode());
    }
}
