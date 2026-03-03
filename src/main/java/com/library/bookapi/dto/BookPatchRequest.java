package com.library.bookapi.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class BookPatchRequest {
    @NotBlank
    private String title;
    @NotBlank
    private String isbn;
    private Integer publishedYear;
    @DecimalMin("1.0")
    @DecimalMax("5.0")
    private Double rating;
    private Long authorId;   // nullable — only sent when patching the author
}
