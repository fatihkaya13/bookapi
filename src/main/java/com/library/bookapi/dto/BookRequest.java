package com.library.bookapi.dto;

import lombok.Data;

@Data
public class BookRequest {
    private String title;
    private String author;
    private String isbn;
    private Integer publishedYear;
    private String genre;
}
