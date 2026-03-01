package com.library.bookapi.dto;

import lombok.Data;

@Data
public class BookPatchRequest {
    private String title;
    private String isbn;
    private Integer publishedYear;
    private String genre;
    private Double rating;
    private Long authorId;   // nullable — only sent when patching the author
}
