package com.library.bookapi.validation;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

// ─── Link #1 in the chain ────────────────────────────────────────────────────
//
// @Order(1) makes this run first in the chain.
// Checks that rating (if provided) is a half or whole point between 1.0–5.0.
// If rating is null, this link does nothing and the chain moves on.
//
@Component
@Order(1)
public class RatingValidator implements BookValidator {

    @Override
    public void validate(BookValidationContext context) {
        Double rating = context.getRating();
        if (rating == null) return;

        if (rating < 1.0 || rating > 5.0 || rating % 0.5 != 0) {
            throw new IllegalArgumentException(
                    "Rating must be between 1.0 and 5.0 in 0.5 increments (e.g., 1, 1.5, 2, ... 5). Got: " + rating);
        }
    }
}
