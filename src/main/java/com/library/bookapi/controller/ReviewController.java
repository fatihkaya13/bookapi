package com.library.bookapi.controller;

import com.library.bookapi.dto.BookResponse;
import com.library.bookapi.dto.ReviewRequest;
import com.library.bookapi.dto.ReviewResponse;
import com.library.bookapi.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    // ─── Basic CRUD ──────────────────────────────────────────────────────────

    @GetMapping("/reviews")
    public List<ReviewResponse> getAll() {
        return reviewService.getAll();
    }

    @GetMapping("/reviews/{id}")
    public ResponseEntity<ReviewResponse> getById(@PathVariable Long id) {
        return reviewService.getById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // GET /books/{bookId}/reviews — nested resource style
    @GetMapping("/books/{bookId}/reviews")
    public List<ReviewResponse> getByBook(@PathVariable Long bookId) {
        return reviewService.getByBookId(bookId);
    }

    @PostMapping("/reviews")
    public ResponseEntity<ReviewResponse> create(@RequestBody ReviewRequest request) {
        return reviewService.create(request)
                .map(review -> ResponseEntity
                        .created(URI.create("/reviews/" + review.getId()))
                        .body(review))
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/reviews/{id}")
    public ResponseEntity<ReviewResponse> update(
            @PathVariable Long id,
            @RequestBody ReviewRequest request) {
        return reviewService.update(id, request)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/reviews/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        return reviewService.delete(id)
                ? ResponseEntity.noContent().build()
                : ResponseEntity.notFound().build();
    }

    // ─── Cross-relationship query endpoints ──────────────────────────────────

    // GET /books/{bookId}/average-rating
    @GetMapping("/books/{bookId}/average-rating")
    public ResponseEntity<Double> getAverageRating(@PathVariable Long bookId) {
        return reviewService.getAverageRating(bookId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // GET /books/no-reviews — books that haven't been reviewed yet
    @GetMapping("/books/no-reviews")
    public List<BookResponse> getBooksWithNoReviews() {
        return reviewService.getBooksWithNoReviews();
    }

    // GET /books/top-rated?limit=5 — top N books by average rating
    @GetMapping("/books/top-rated")
    public List<BookResponse> getTopRated(@RequestParam(defaultValue = "5") int limit) {
        return reviewService.getTopRatedBooks(limit);
    }
}
