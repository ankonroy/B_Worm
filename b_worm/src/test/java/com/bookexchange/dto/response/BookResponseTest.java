package com.bookexchange.dto.response;

import com.bookexchange.model.enums.BookCondition;
import com.bookexchange.model.enums.BookStatus;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class BookResponseTest {

    @Test
    void gettersSettersAndFlags_WorkWithExpectedState() {
        BookResponse response = new BookResponse();
        LocalDateTime now = LocalDateTime.now();

        response.setBookId(10L);
        response.setTitle("Refactoring");
        response.setAuthor("Martin Fowler");
        response.setIsbn("9780201485677");
        response.setDescription("Improving the Design of Existing Code");
        response.setCondition(BookCondition.LIKE_NEW);
        response.setStatus(BookStatus.AVAILABLE);
        response.setCreatedAt(now.minusDays(1));
        response.setUpdatedAt(now);
        response.setDonorId(1L);
        response.setDonorName("Donor User");
        response.setDonorUsername("donor");
        response.setTakerId(2L);
        response.setTakerName("Taker User");
        response.setTakerUsername("taker");
        response.setRequestCount(3);
        response.setPendingRequestCount(1);
        response.setRequestedByCurrentUser(true);
        response.setCurrentUserRequestStatus("PENDING");

        assertThat(response.getBookId()).isEqualTo(10L);
        assertThat(response.getTitle()).isEqualTo("Refactoring");
        assertThat(response.getCondition()).isEqualTo(BookCondition.LIKE_NEW);
        assertThat(response.getRequestCount()).isEqualTo(3);
        assertThat(response.getPendingRequestCount()).isEqualTo(1);
        assertThat(response.isRequestedByCurrentUser()).isTrue();
        assertThat(response.isAvailable()).isTrue();
        assertThat(response.isBanned()).isFalse();
        assertThat(response.isDonor(1L)).isTrue();
        assertThat(response.isDonor(9L)).isFalse();
        assertThat(response.isTaker(2L)).isTrue();
        assertThat(response.isTaker(7L)).isFalse();
    }

    @Test
    void helperMethods_ReflectStatusChanges() {
        BookResponse response = new BookResponse();
        response.setStatus(BookStatus.BANNED);

        assertThat(response.isAvailable()).isFalse();
        assertThat(response.isBanned()).isTrue();
    }

    @Test
    void equalsHashCodeAndToString_UseDataFields() {
        BookResponse first = new BookResponse();
        first.setBookId(42L);
        first.setTitle("DDD");

        BookResponse second = new BookResponse();
        second.setBookId(42L);
        second.setTitle("DDD");

        assertThat(first).isEqualTo(second);
        assertThat(first.hashCode()).isEqualTo(second.hashCode());
        assertThat(first.toString()).contains("bookId=42");
        assertThat(first.canEqual(second)).isTrue();
    }
}
