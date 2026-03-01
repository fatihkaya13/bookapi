package com.library.bookapi.dto;

import lombok.Data;

@Data
public class BookRequest {
    private String title;
    private String isbn;
    private Integer publishedYear;
    private String genre;

    // Client sends the author's ID, not the full author object.
    // The service layer resolves it to an Author entity.
    // Nullable — a book may exist without an author assigned yet.
    private Long authorId;
}
