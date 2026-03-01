package com.library.bookapi.repository;

import com.library.bookapi.entity.Author;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface AuthorRepository extends JpaRepository<Author, Long> {

    // Derived query — Spring Data generates the SQL from the method name.
    // SELECT * FROM authors WHERE first_name = ? AND last_name = ?
    Optional<Author> findByFirstNameAndLastName(String firstName, String lastName);

    // JPQL query — uses entity/field names, not table/column names.
    // JOIN FETCH forces an EAGER load of the books collection FOR THIS QUERY ONLY.
    // This is the recommended pattern: keep @OneToMany LAZY globally,
    // but use JOIN FETCH in specific queries when you know you need the data.
    // Without JOIN FETCH here, accessing author.getBooks() outside a transaction
    // would throw LazyInitializationException.
    @Query("SELECT a FROM Author a LEFT JOIN FETCH a.books WHERE a.id = :id")
    Optional<Author> findByIdWithBooks(Long id);

    // Find authors who have at least one book
    @Query("SELECT DISTINCT a FROM Author a JOIN a.books b")
    List<Author> findAuthorsWithAtLeastOneBook();
}
