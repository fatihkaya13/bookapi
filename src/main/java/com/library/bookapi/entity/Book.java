package com.library.bookapi.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "books")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String isbn;
    private Integer publishedYear;
    private String genre;

    // Rating 1.0 to 5.0 (half-point increments: 1, 1.5, 2, ... 4.5, 5)
    @Column(nullable = true)
    private Double rating;

    // ─── OWNING SIDE of the OneToMany/ManyToOne relationship ────────────────
    //
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id", nullable = true)
    private Author author;

    // ─── OWNING SIDE of the ManyToMany relationship ──────────────────────────
    //
    // @JoinTable defines the join table name and its two FK columns:
    //   joinColumns        → the FK pointing back to THIS entity (books.id)
    //   inverseJoinColumns → the FK pointing to the OTHER entity (categories.id)
    //
    // Hibernate creates:
    //   CREATE TABLE book_category (
    //       book_id     BIGINT REFERENCES books(id),
    //       category_id BIGINT REFERENCES categories(id)
    //   )
    //
    // Because Book is the owning side, you MUST call book.getCategories().add(cat)
    // (or the addCategory helper) for Hibernate to INSERT a row into book_category.
    // Calling category.getBooks().add(book) alone does nothing to the DB.
    //
    // fetch = LAZY (default for collections) — loading a book does not load all
    // its categories. Only accessed when you call book.getCategories().
    //
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "book_category",
        joinColumns = @JoinColumn(name = "book_id"),
        inverseJoinColumns = @JoinColumn(name = "category_id")
    )
    @Builder.Default
    private Set<Category> categories = new HashSet<>();

    // ─── INVERSE SIDE of Book→Review (same pattern as Author→Book) ──────────
    //
    // cascade = ALL → saving/deleting a book cascades to its reviews
    // orphanRemoval = true → removing a review from this list deletes it from DB
    //
    // List is fine here (not Set) because OneToMany doesn't suffer from the
    // bag-semantics problem — only ManyToMany does.
    //
    @OneToMany(mappedBy = "book", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    private List<Review> reviews = new ArrayList<>();

    public void addReview(Review review) {
        reviews.add(review);
        review.setBook(this);
    }

    public void removeReview(Review review) {
        reviews.remove(review);
        review.setBook(null);
    }

    // ─── Category helpers (keep both sides in sync) ──────────────────────────
    public void addCategory(Category category) {
        categories.add(category);
        category.getBooks().add(this);   // keep inverse side in sync (in-memory)
    }

    public void removeCategory(Category category) {
        categories.remove(category);
        category.getBooks().remove(this);
    }
}
