package com.bookexchange.repository;

import com.bookexchange.model.Book;
import com.bookexchange.model.User;
import com.bookexchange.model.enums.BookStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {
    List<Book> findAllByStatus(BookStatus status);
    List<Book> findAllByDonatedBy(User user);
    List<Book> findAllByTakenBy(User user);
    
    @Query("SELECT b FROM Book b WHERE b.status = 'AVAILABLE' ORDER BY b.createdAt DESC")
    List<Book> findAllAvailableBooks();
    
    @Query("SELECT b FROM Book b WHERE LOWER(b.title) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           "OR LOWER(b.author) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           "OR LOWER(b.isbn) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Book> searchBooks(@Param("keyword") String keyword);
    
    List<Book> findByDonatedByOrderByCreatedAtDesc(User user);
    
    @Query("SELECT b FROM Book b ORDER BY b.createdAt DESC")
    List<Book> findRecentBooks();
    
    long countByDonatedBy(User user);
    long countByTakenBy(User user);
}
