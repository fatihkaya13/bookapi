package com.library.bookapi.service;

import com.library.bookapi.dto.BookResponse;
import com.library.bookapi.dto.ReviewRequest;
import com.library.bookapi.dto.ReviewResponse;
import com.library.bookapi.entity.Book;
import com.library.bookapi.entity.Review;
import com.library.bookapi.repository.BookRepository;
import com.library.bookapi.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final BookRepository bookRepository;
    private final BookService bookService;

    // ─── Basic CRUD ──────────────────────────────────────────────────────────

    public List<ReviewResponse> getAll() {
        return reviewRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public Optional<ReviewResponse> getById(Long id) {
        return reviewRepository.findById(id).map(this::toResponse);
    }

    @Transactional(readOnly = true)
    public List<ReviewResponse> getByBookId(Long bookId) {
        return reviewRepository.findByBookIdOrderByReviewDateDesc(bookId).stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public Optional<ReviewResponse> create(ReviewRequest request) {
        // Find the book this review belongs to — if missing, return empty
        return bookRepository.findById(request.getBookId()).map(book -> {
            Review review = Review.builder()
                    .reviewerName(request.getReviewerName())
                    .rating(request.getRating())
                    .comment(request.getComment())
                    .book(book)              // owning side — sets the FK
                    .build();
            // reviewDate is auto-filled by @CreationTimestamp on save
            return toResponse(reviewRepository.save(review));
        });
    }

    @Transactional
    public Optional<ReviewResponse> update(Long id, ReviewRequest request) {
        return reviewRepository.findById(id).map(review -> {
            review.setReviewerName(request.getReviewerName());
            review.setRating(request.getRating());
            review.setComment(request.getComment());
            // reviewDate stays unchanged (updatable = false on the column)
            return toResponse(reviewRepository.save(review));
        });
    }

    public boolean delete(Long id) {
        if (!reviewRepository.existsById(id)) return false;
        reviewRepository.deleteById(id);
        return true;
    }

    // ─── Stream-based cross-relationship queries ──────────────────────────────

    // Average rating for a specific book — uses JPQL AVG in the repository
    @Transactional(readOnly = true)
    public Optional<Double> getAverageRating(Long bookId) {
        if (!bookRepository.existsById(bookId)) return Optional.empty();
        return Optional.ofNullable(reviewRepository.findAverageRatingByBookId(bookId));
    }

    // Books that have zero reviews
    // Uses the repository to get IDs, then streams through bookRepository to map to DTOs.
    @Transactional(readOnly = true)
    public List<BookResponse> getBooksWithNoReviews() {
        return reviewRepository.findBookIdsWithNoReviews().stream()   // Stream<Long>
                .map(bookRepository::findById)                         // Stream<Optional<Book>>
                .flatMap(Optional::stream)                             // Stream<Book> (filters empties)
                .map(bookService::toResponse)                          // Stream<BookResponse>
                .toList();
    }

    // Top-rated books — loads all books, computes avg rating via streams,
    // filters out unreviewed books, sorts descending, returns top N.
    //
    // This demonstrates a pure Java stream approach (no JPQL aggregation).
    // In production with large data, you'd push this to the DB layer.
    @Transactional(readOnly = true)
    public List<BookResponse> getTopRatedBooks(int limit) {
        return bookRepository.findAll().stream()
                .filter(book -> !book.getReviews().isEmpty())
                .sorted(Comparator.comparingDouble((Book book) ->
                        book.getReviews().stream()
                                .mapToInt(r -> r.getRating())
                                .average()
                                .orElse(0.0))
                        .reversed())
                .limit(limit)
                .map(bookService::toResponse)
                .toList();
    }

    // ─── Mapping helper ──────────────────────────────────────────────────────

    private ReviewResponse toResponse(Review review) {
        Book book = review.getBook();
        return ReviewResponse.builder()
                .id(review.getId())
                .reviewerName(review.getReviewerName())
                .rating(review.getRating())
                .comment(review.getComment())
                .reviewDate(review.getReviewDate())
                .bookId(book.getId())
                .bookTitle(book.getTitle())
                .build();
    }
}
