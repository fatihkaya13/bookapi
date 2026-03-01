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
    private Double rating;

    // Flat author info — no back-reference to avoid circular JSON
    private Long authorId;
    private String authorName;

    // Category names only — flat to avoid circular nesting
    private List<String> categoryNames;

    // Review summary — computed via streams in service layer
    // We don't embed full ReviewResponse here; just aggregate stats.
    private Integer reviewCount;
    private Double averageRating;
}
