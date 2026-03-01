package com.library.bookapi.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class CategoryResponse {
    private Long id;
    private String name;
    private String description;

    // Same circular-reference prevention as AuthorResponse:
    // CategoryResponse → List<BookResponse>  (BookResponse does NOT contain CategoryResponse)
    // Breaking the cycle at the DTO layer keeps JSON safe.
    private List<BookResponse> books;
}
