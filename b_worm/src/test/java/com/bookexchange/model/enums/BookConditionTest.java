package com.bookexchange.model.enums;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class BookConditionTest {

    @Test
    void valueOf_ReturnsMatchingEnum() {
        assertThat(BookCondition.valueOf("GOOD")).isEqualTo(BookCondition.GOOD);
    }

    @Test
    void values_ReturnAllDeclaredConstants() {
        assertThat(BookCondition.values())
                .containsExactly(BookCondition.NEW, BookCondition.LIKE_NEW, BookCondition.GOOD,
                        BookCondition.ACCEPTABLE, BookCondition.POOR);
    }
}
