package com.bookexchange.dto.request;

import com.bookexchange.model.enums.BookCondition;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class BookRequest {
    @NotBlank(message = "Title is required")
    private String title;

    private String author;

    private String isbn;

    private String description;

    private BookCondition condition;
}
