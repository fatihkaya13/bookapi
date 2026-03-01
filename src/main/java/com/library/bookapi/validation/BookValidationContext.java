package com.library.bookapi.validation;

import lombok.Builder;
import lombok.Data;

// Carries the fields that need validation.
// Built from either BookRequest or BookPatchRequest — decouples validators
// from specific DTO classes. Fields are nullable: a null field means
// "not being set in this request" so validators should skip it.
@Data
@Builder
public class BookValidationContext {
    private String title;
    private String isbn;
    private Integer publishedYear;
    private String genre;
    private Double rating;
}
