package com.library.bookapi.repository;

import com.library.bookapi.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long> {

    // Derived query — Spring Data reads the method name:
    //   findBy + Book_Id → WHERE book_id = ?
    //   OrderByReviewDateDesc → ORDER BY review_date DESC
    List<Review> findByBookIdOrderByReviewDateDesc(Long bookId);

    // Average rating for a book — returns null if no reviews exist
    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.book.id = :bookId")
    Double findAverageRatingByBookId(Long bookId);

    // Books that have zero reviews — LEFT JOIN where reviews side is null
    @Query("SELECT DISTINCT b.id FROM Book b LEFT JOIN b.reviews r WHERE r IS NULL")
    List<Long> findBookIdsWithNoReviews();
}
