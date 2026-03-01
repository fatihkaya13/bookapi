package com.library.bookapi.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "categories")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    private String description;

    // ─── INVERSE SIDE of the ManyToMany ──────────────────────────────────────
    //
    // mappedBy = "categories" points to the field name on Book (the owning side).
    // This side does NOT define the join table — Book does.
    // Hibernate ignores changes made only here; you must always add/remove
    // through the Book side (or use the addCategory() helper below that
    // keeps both sides in sync).
    //
    // Why Set instead of List?
    //   With @ManyToMany, using List causes Hibernate to DELETE ALL rows from
    //   the join table and re-INSERT them every time you add/remove one item
    //   (the "bag semantics" problem). Set avoids this — Hibernate only issues
    //   the targeted INSERT or DELETE for the changed row.
    //
    // fetch = LAZY (already the default for collections) — do not load all
    //   books in a category when you just need the category name.
    //
    @ManyToMany(mappedBy = "categories", fetch = FetchType.LAZY)
    @Builder.Default
    private Set<Book> books = new HashSet<>();
}
