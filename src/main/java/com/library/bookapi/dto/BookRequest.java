package com.library.bookapi.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;

@Data
public class BookRequest {
    @NotBlank
    private String title;
    @NotBlank
    private String isbn;
    private Integer publishedYear;
    @DecimalMin("1.0")
    @DecimalMax("5.0")
    private Double rating;     // 1.0 to 5.0 (half-point: 1, 1.5, 2, ... 5)

    // Client sends the author's ID, not the full author object.
    // The service layer resolves it to an Author entity.
    // Nullable — a book may exist without an author assigned yet.
    private Long authorId;

    // At least one category must be assigned.
    @NotEmpty
    private List<Long> categoryIds;
}
