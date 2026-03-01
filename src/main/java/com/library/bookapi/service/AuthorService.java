package com.library.bookapi.service;

import com.library.bookapi.dto.AuthorPatchRequest;
import com.library.bookapi.dto.AuthorRequest;
import com.library.bookapi.dto.AuthorResponse;
import com.library.bookapi.dto.BookResponse;
import com.library.bookapi.entity.Author;
import com.library.bookapi.repository.AuthorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthorService {

    private final AuthorRepository authorRepository;
    private final BookService bookService;  // reuse toResponse mapping

    public List<AuthorResponse> getAll() {
        // getAll without books — just author data, no JOIN needed
        return authorRepository.findAll().stream()
                .map(this::toResponseWithoutBooks)
                .toList();
    }

    // Uses JOIN FETCH query to load author + all their books in ONE SQL query.
    // Without JOIN FETCH, accessing author.getBooks() would issue a second
    // SELECT — that's the "N+1 problem" when done in a loop.
    @Transactional(readOnly = true)
    public Optional<AuthorResponse> getById(Long id) {
        return authorRepository.findByIdWithBooks(id)
                .map(this::toResponseWithBooks);
    }

    @Transactional
    public AuthorResponse create(AuthorRequest request) {
        Author author = toEntity(request);
        return toResponseWithoutBooks(authorRepository.save(author));
    }

    @Transactional
    public Optional<AuthorResponse> update(Long id, AuthorRequest request) {
        return authorRepository.findById(id).map(author -> {
            author.setFirstName(request.getFirstName());
            author.setLastName(request.getLastName());
            author.setBirthYear(request.getBirthYear());
            author.setNationality(request.getNationality());
            return toResponseWithoutBooks(authorRepository.save(author));
        });
    }

    @Transactional
    public Optional<AuthorResponse> partialUpdate(Long id, AuthorPatchRequest patch) {
        return authorRepository.findById(id).map(author -> {
            if (patch.getFirstName() != null) author.setFirstName(patch.getFirstName());
            if (patch.getLastName() != null) author.setLastName(patch.getLastName());
            if (patch.getBirthYear() != null) author.setBirthYear(patch.getBirthYear());
            if (patch.getNationality() != null) author.setNationality(patch.getNationality());
            return toResponseWithoutBooks(authorRepository.save(author));
        });
    }

    public boolean delete(Long id) {
        if (!authorRepository.existsById(id)) return false;
        authorRepository.deleteById(id);
        // Because Author has cascade = ALL + orphanRemoval = true,
        // deleting the Author also deletes all its books automatically.
        return true;
    }

    // ─── Stream-based cross-relationship queries ─────────────────────────────

    // Find all books by a given author — uses JOIN FETCH to load books eagerly
    // for this specific query, then maps them to DTOs via BookService.
    @Transactional(readOnly = true)
    public List<BookResponse> getBooksForAuthor(Long authorId) {
        return authorRepository.findByIdWithBooks(authorId)
                .map(author -> author.getBooks().stream()         // Stream<Book>
                        .map(bookService::toResponse)             // Stream<BookResponse>
                        .toList())
                .orElse(List.of());
    }

    // Find authors who have at least one book — uses DISTINCT JOIN query
    @Transactional(readOnly = true)
    public List<AuthorResponse> getAuthorsWithBooks() {
        return authorRepository.findAuthorsWithAtLeastOneBook().stream()
                .map(this::toResponseWithoutBooks)
                .toList();
    }

    // ─── Mapping helpers ─────────────────────────────────────────────────────

    private AuthorResponse toResponseWithBooks(Author author) {
        // author.getBooks() is safe here because the caller used findByIdWithBooks
        // which JOIN FETCHes the books in the same query. No lazy load risk.
        List<BookResponse> bookResponses = author.getBooks().stream()
                .map(bookService::toResponse)
                .toList();

        return AuthorResponse.builder()
                .id(author.getId())
                .firstName(author.getFirstName())
                .lastName(author.getLastName())
                .birthYear(author.getBirthYear())
                .nationality(author.getNationality())
                .books(bookResponses)
                .build();
    }

    private AuthorResponse toResponseWithoutBooks(Author author) {
        return AuthorResponse.builder()
                .id(author.getId())
                .firstName(author.getFirstName())
                .lastName(author.getLastName())
                .birthYear(author.getBirthYear())
                .nationality(author.getNationality())
                .books(List.of())
                .build();
    }

    Author toEntity(AuthorRequest request) {
        return Author.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .birthYear(request.getBirthYear())
                .nationality(request.getNationality())
                .build();
    }
}
