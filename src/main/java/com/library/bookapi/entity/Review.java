package com.library.bookapi.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "reviews")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String reviewerName;

    // rating 1–5
    @Column(nullable = false)
    private Integer rating;

    private String comment;

    // ─── @CreationTimestamp ──────────────────────────────────────────────────
    //
    // This is a Hibernate-specific annotation (not standard JPA).
    // When the entity is first persisted (INSERT), Hibernate automatically
    // sets this field to the current timestamp. You never set it manually.
    //
    // updatable = false means this column won't be included in UPDATE SQL —
    // once the review date is set at creation, it never changes.
    //
    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime reviewDate;

    // ─── OWNING SIDE — same pattern as Book→Author ──────────────────────────
    //
    // The reviews table gets a book_id FK column.
    // fetch = LAZY overrides the ManyToOne EAGER default.
    // nullable = false → every review MUST belong to a book (mandatory relationship).
    //
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_id", nullable = false)
    private Book book;
}
