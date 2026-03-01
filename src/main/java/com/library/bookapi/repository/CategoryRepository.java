package com.library.bookapi.repository;

import com.library.bookapi.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface CategoryRepository extends JpaRepository<Category, Long> {

    Optional<Category> findByName(String name);

    // Load a category with all its books in one query (JOIN FETCH).
    // Without this, accessing category.getBooks() outside a transaction
    // would throw LazyInitializationException.
    @Query("SELECT c FROM Category c LEFT JOIN FETCH c.books WHERE c.id = :id")
    Optional<Category> findByIdWithBooks(Long id);

    // Find all categories that have at least one book assigned
    @Query("SELECT DISTINCT c FROM Category c JOIN c.books b")
    List<Category> findCategoriesWithBooks();

    // Find all categories for a given book — useful for the book detail view
    @Query("SELECT c FROM Category c JOIN c.books b WHERE b.id = :bookId")
    Set<Category> findCategoriesByBookId(Long bookId);
}
