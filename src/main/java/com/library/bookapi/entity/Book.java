package com.library.bookapi.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
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

    // ─── Convenience helpers (keep both sides in sync) ────────────────────────
    public void addCategory(Category category) {
        categories.add(category);
        category.getBooks().add(this);   // keep inverse side in sync (in-memory)
    }

    public void removeCategory(Category category) {
        categories.remove(category);
        category.getBooks().remove(this);
    }
}
