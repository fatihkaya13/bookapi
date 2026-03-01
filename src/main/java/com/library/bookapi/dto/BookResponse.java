package com.library.bookapi.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class BookResponse {
    private Long id;
    private String title;
    private String isbn;
    private Integer publishedYear;
    private String genre;

    // Flat author info — no back-reference to avoid circular JSON
    private Long authorId;
    private String authorName;

    // Category names only — no List<CategoryResponse> (that would nest BookResponse again)
    // Flat list of strings is the simplest safe representation.
    private List<String> categoryNames;
}
