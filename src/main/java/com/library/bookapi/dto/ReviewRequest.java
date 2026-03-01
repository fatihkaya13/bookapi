package com.library.bookapi.dto;

import lombok.Data;

@Data
public class ReviewRequest {
    private String reviewerName;
    private Integer rating;    // 1–5
    private String comment;
    private Long bookId;       // which book this review is for
}
