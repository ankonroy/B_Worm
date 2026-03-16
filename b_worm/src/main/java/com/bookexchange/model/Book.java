package com.bookexchange.model;

import com.bookexchange.model.enums.BookCondition;
import com.bookexchange.model.enums.BookStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "books")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Book {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long bookId;

    @Column(nullable = false)
    private String title;

    private String author;

    private String isbn;

    private String description;

    @Enumerated(EnumType.STRING)
    private BookCondition condition;

    @Enumerated(EnumType.STRING)
    private BookStatus status = BookStatus.AVAILABLE;

    private LocalDateTime createdAt = LocalDateTime.now();

    private LocalDateTime updatedAt = LocalDateTime.now();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "donated_by_id", nullable = false)
    private User donatedBy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "taken_by_id")
    private User takenBy;

    @OneToMany(mappedBy = "book", cascade = CascadeType.ALL)
    private List<Request> requests;

    // Helper methods
    public boolean isAvailable() {
        return status == BookStatus.AVAILABLE;
    }

    public boolean isBanned() {
        return status == BookStatus.BANNED;
    }

    public boolean isDonatedBy(User user) {
        return donatedBy != null && donatedBy.equals(user);
    }

    @PreUpdate
    public void preUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
