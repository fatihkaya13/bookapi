package com.library.bookapi.entity;

import jakarta.persistence.*;
import lombok.*;

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
    // This is the owning side because the FK column (author_id) lives in the
    // books table. Hibernate watches THIS field to decide what to write to DB.
    //
    // @JoinColumn(name = "author_id") tells Hibernate the name of the FK column.
    // Without it, Hibernate would generate an ugly default name.
    //
    // fetch = LAZY overrides the ManyToOne default (which is EAGER).
    // Why override? Because EAGER on @ManyToOne means every time you load ANY
    // book (even in a list of 1000 books), Hibernate also runs a SELECT to
    // load the author. With LAZY, the author proxy is only resolved when you
    // actually call book.getAuthor().getFirstName() etc.
    //
    // nullable = true allows books without an author (optional relationship).
    //
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id", nullable = true)
    private Author author;
}
