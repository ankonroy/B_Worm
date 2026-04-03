package com.bookexchange.dto.request;

import com.bookexchange.model.enums.BookCondition;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class BookRequestTest {

    @Test
    void gettersAndSetters_WorkAsExpected() {
        BookRequest request = new BookRequest();
        request.setTitle("The Pragmatic Programmer");
        request.setAuthor("Andrew Hunt");
        request.setIsbn("9780135957059");
        request.setDescription("Classic software engineering book");
        request.setCondition(BookCondition.GOOD);

        assertThat(request.getTitle()).isEqualTo("The Pragmatic Programmer");
        assertThat(request.getAuthor()).isEqualTo("Andrew Hunt");
        assertThat(request.getIsbn()).isEqualTo("9780135957059");
        assertThat(request.getDescription()).isEqualTo("Classic software engineering book");
        assertThat(request.getCondition()).isEqualTo(BookCondition.GOOD);
    }

    @Test
    void equalsHashCodeAndToString_UseFieldValues() {
        BookRequest first = new BookRequest();
        first.setTitle("Clean Architecture");
        first.setAuthor("Robert C. Martin");

        BookRequest second = new BookRequest();
        second.setTitle("Clean Architecture");
        second.setAuthor("Robert C. Martin");

        assertThat(first).isEqualTo(second);
        assertThat(first.hashCode()).isEqualTo(second.hashCode());
        assertThat(first.toString()).contains("Clean Architecture");
        assertThat(first.canEqual(second)).isTrue();
    }
}
