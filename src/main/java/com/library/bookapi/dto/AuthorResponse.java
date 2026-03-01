package com.library.bookapi.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class AuthorResponse {
    private Long id;
    private String firstName;
    private String lastName;
    private Integer birthYear;
    private String nationality;

    // ─── Circular reference prevention ──────────────────────────────────────
    //
    // We include the books list here, but as BookResponse DTOs — NOT as Book
    // entities. BookResponse does NOT contain an AuthorResponse field.
    // This breaks the cycle:
    //
    //   Entity world:  Author <──────────────> Book  (bidirectional, dangerous)
    //   DTO world:     AuthorResponse ──> BookResponse  (one direction, safe)
    //
    // When you only need a list of an author's book titles/ids, you can also
    // create a slimmer DTO (e.g., BookSummary) with just id+title to avoid
    // over-fetching. We use full BookResponse here for clarity.
    //
    private List<BookResponse> books;
}
