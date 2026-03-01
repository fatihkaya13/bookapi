package com.library.bookapi.service;

import com.library.bookapi.dto.BookPatchRequest;
import com.library.bookapi.dto.BookRequest;
import com.library.bookapi.dto.BookResponse;
import com.library.bookapi.entity.Author;
import com.library.bookapi.entity.Book;
import com.library.bookapi.repository.AuthorRepository;
import com.library.bookapi.repository.BookRepository;
import com.library.bookapi.validation.BookValidationContext;
import com.library.bookapi.validation.BookValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.OptionalDouble;

@Service
@RequiredArgsConstructor
public class BookService {

    private final BookRepository bookRepository;
    private final AuthorRepository authorRepository;

    // ─── Chain of Responsibility ─────────────────────────────────────────────
    //
    // Spring auto-injects ALL @Component classes that implement BookValidator,
    // ordered by their @Order annotation. Adding a new validator is zero-touch
    // here — just create the @Component and it joins the chain automatically.
    //
    private final List<BookValidator> validators;

    public List<BookResponse> getAll() {
        return bookRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    public Optional<BookResponse> getById(Long id) {
        return bookRepository.findById(id).map(this::toResponse);
    }

    @Transactional
    public BookResponse create(BookRequest request) {
        runValidationChain(toContext(request));
        Book book = toEntity(request);
        return toResponse(bookRepository.save(book));
    }

    @Transactional
    public Optional<BookResponse> update(Long id, BookRequest request) {
        runValidationChain(toContext(request));
        return bookRepository.findById(id).map(book -> {
            book.setTitle(request.getTitle());
            book.setIsbn(request.getIsbn());
            book.setPublishedYear(request.getPublishedYear());
            book.setGenre(request.getGenre());
            book.setRating(request.getRating());

            Author author = resolveAuthor(request.getAuthorId());
            book.setAuthor(author);

            return toResponse(bookRepository.save(book));
        });
    }

    @Transactional
    public Optional<BookResponse> partialUpdate(Long id, BookPatchRequest patch) {
        runValidationChain(toContext(patch));
        return bookRepository.findById(id).map(book -> {
            if (patch.getTitle() != null) book.setTitle(patch.getTitle());
            if (patch.getIsbn() != null) book.setIsbn(patch.getIsbn());
            if (patch.getPublishedYear() != null) book.setPublishedYear(patch.getPublishedYear());
            if (patch.getGenre() != null) book.setGenre(patch.getGenre());
            if (patch.getRating() != null) book.setRating(patch.getRating());

            if (patch.getAuthorId() != null) {
                book.setAuthor(resolveAuthor(patch.getAuthorId()));
            }

            return toResponse(bookRepository.save(book));
        });
    }

    public boolean delete(Long id) {
        if (!bookRepository.existsById(id)) return false;
        bookRepository.deleteById(id);
        return true;
    }

    // ─── Validation chain runner ──────────────────────────────────────────────
    //
    // Iterates through every validator in order. Each link in the chain
    // inspects the context and either:
    //   - skips (field is null → not provided in this request)
    //   - passes (field is valid)
    //   - throws IllegalArgumentException (field is invalid → chain stops)
    //
    private void runValidationChain(BookValidationContext context) {
        validators.forEach(v -> v.validate(context));
    }

    // ─── Context builders ─────────────────────────────────────────────────────
    //
    // Convert DTOs to a validation context. This decouples validators from
    // specific DTO classes — validators only know about BookValidationContext.
    //
    private BookValidationContext toContext(BookRequest request) {
        return BookValidationContext.builder()
                .title(request.getTitle())
                .isbn(request.getIsbn())
                .publishedYear(request.getPublishedYear())
                .genre(request.getGenre())
                .rating(request.getRating())
                .build();
    }

    private BookValidationContext toContext(BookPatchRequest patch) {
        return BookValidationContext.builder()
                .title(patch.getTitle())
                .isbn(patch.getIsbn())
                .publishedYear(patch.getPublishedYear())
                .genre(patch.getGenre())
                .rating(patch.getRating())
                .build();
    }

    // ─── Mapping helpers ─────────────────────────────────────────────────────

    BookResponse toResponse(Book book) {
        Author author = book.getAuthor();

        List<String> categoryNames = book.getCategories().stream()
                .map(cat -> cat.getName())
                .sorted()
                .toList();

        int reviewCount = book.getReviews().size();

        OptionalDouble avg = book.getReviews().stream()
                .mapToInt(r -> r.getRating())
                .average();
        Double avgRating = avg.isPresent() ? avg.getAsDouble() : null;

        return BookResponse.builder()
                .id(book.getId())
                .title(book.getTitle())
                .isbn(book.getIsbn())
                .publishedYear(book.getPublishedYear())
                .genre(book.getGenre())
                .rating(book.getRating())
                .authorId(author != null ? author.getId() : null)
                .authorName(author != null
                        ? author.getFirstName() + " " + author.getLastName()
                        : null)
                .categoryNames(categoryNames)
                .reviewCount(reviewCount)
                .averageRating(avgRating)
                .build();
    }

    private Book toEntity(BookRequest request) {
        Author author = resolveAuthor(request.getAuthorId());
        return Book.builder()
                .title(request.getTitle())
                .isbn(request.getIsbn())
                .publishedYear(request.getPublishedYear())
                .genre(request.getGenre())
                .rating(request.getRating())
                .author(author)
                .build();
    }

    private Author resolveAuthor(Long authorId) {
        return Optional.ofNullable(authorId)
                .flatMap(authorRepository::findById)
                .orElse(null);
    }
}
