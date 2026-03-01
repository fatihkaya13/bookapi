package com.library.bookapi.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "authors")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Author {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;

    private Integer birthYear;

    private String nationality;

    // ─── INVERSE SIDE of the OneToMany/ManyToOne relationship ───────────────
    //
    // mappedBy = "author" tells Hibernate:
    //   "The 'author' field on the Book entity is the owning side.
    //    Don't create a second FK column here — just read from there."
    //
    // cascade = CascadeType.ALL means: when we save/delete an Author,
    //   also save/delete all its Books automatically.
    //
    // orphanRemoval = true means: if we remove a Book from this list,
    //   Hibernate will DELETE that book row from the DB (it's an "orphan").
    //
    // fetch = LAZY (default for collections) — Hibernate does NOT load the
    //   books list when you load an Author. It loads them only when you call
    //   author.getBooks(). This avoids loading thousands of books needlessly.
    //
    @OneToMany(
        mappedBy = "author",
        cascade = CascadeType.ALL,
        orphanRemoval = true,
        fetch = FetchType.LAZY
    )
    @Builder.Default
    private List<Book> books = new ArrayList<>();

    // ─── Convenience helpers ─────────────────────────────────────────────────
    //
    // Because Author is the INVERSE side, simply adding to the list is NOT
    // enough for Hibernate to persist the FK. You must also set book.author.
    // These helpers keep both sides of the relationship in sync.
    //
    public void addBook(Book book) {
        books.add(book);
        book.setAuthor(this);   // keeps the owning side in sync
    }

    public void removeBook(Book book) {
        books.remove(book);
        book.setAuthor(null);
    }
}
