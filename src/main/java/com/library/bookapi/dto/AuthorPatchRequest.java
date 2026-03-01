package com.library.bookapi.dto;

import lombok.Data;

@Data
public class AuthorPatchRequest {
    private String firstName;
    private String lastName;
    private Integer birthYear;
    private String nationality;
}
