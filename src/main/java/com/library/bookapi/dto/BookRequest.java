package com.library.bookapi.dto;

import lombok.Data;

@Data
public class BookRequest {
    private String title;
    private String isbn;
    private Integer publishedYear;
    private String genre;
    private Double rating;     // 1.0 to 5.0 (half-point: 1, 1.5, 2, ... 5)

    // Client sends the author's ID, not the full author object.
    // The service layer resolves it to an Author entity.
    // Nullable — a book may exist without an author assigned yet.
    private Long authorId;
}
