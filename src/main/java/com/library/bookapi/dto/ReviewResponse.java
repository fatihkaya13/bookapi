package com.library.bookapi.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class ReviewResponse {
    private Long id;
    private String reviewerName;
    private Integer rating;
    private String comment;
    private LocalDateTime reviewDate;

    // Flat book info — same pattern as BookResponse.authorId/authorName
    // No nested BookResponse here to avoid circular references.
    private Long bookId;
    private String bookTitle;
}
