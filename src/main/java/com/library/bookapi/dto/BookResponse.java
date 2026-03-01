package com.library.bookapi.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class BookResponse {
    private Long id;
    private String title;
    private String isbn;
    private Integer publishedYear;
    private String genre;

    // ─── Flat author info — no back-reference ────────────────────────────────
    //
    // Instead of embedding a full AuthorResponse (which contains a List<BookResponse>
    // → which contains authorId → ...), we embed only the fields a client
    // actually needs when viewing a book. This is the "flattening" pattern.
    //
    // The alternative is a nested AuthorSummary DTO with just id+name,
    // but for Phase 2 flat fields keep things simple.
    //
    private Long authorId;
    private String authorName;   // "Robert C. Martin" — computed in service
}
