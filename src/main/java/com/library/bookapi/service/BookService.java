package com.library.bookapi.service;

import com.library.bookapi.dto.BookPatchRequest;
import com.library.bookapi.dto.BookRequest;
import com.library.bookapi.dto.BookResponse;
import com.library.bookapi.entity.Author;
import com.library.bookapi.entity.Book;
import com.library.bookapi.repository.AuthorRepository;
import com.library.bookapi.repository.BookRepository;
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

    public List<BookResponse> getAll() {
        return bookRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    public Optional<BookResponse> getById(Long id) {
        return bookRepository.findById(id).map(this::toResponse);
    }

    // @Transactional ensures the whole method runs inside one DB transaction.
    // This matters because we might load an Author AND save a Book — both must
    // succeed or both must roll back. It also keeps the Hibernate session open
    // for the duration, so lazy-loaded fields are accessible within this method.
    @Transactional
    public BookResponse create(BookRequest request) {
        Book book = toEntity(request);
        return toResponse(bookRepository.save(book));
    }

    @Transactional
    public Optional<BookResponse> update(Long id, BookRequest request) {
        return bookRepository.findById(id).map(book -> {
            book.setTitle(request.getTitle());
            book.setIsbn(request.getIsbn());
            book.setPublishedYear(request.getPublishedYear());
            book.setGenre(request.getGenre());

            // Resolve the new author from the DB if an authorId was sent
            Author author = resolveAuthor(request.getAuthorId());
            book.setAuthor(author);

            return toResponse(bookRepository.save(book));
        });
    }

    @Transactional
    public Optional<BookResponse> partialUpdate(Long id, BookPatchRequest patch) {
        return bookRepository.findById(id).map(book -> {
            if (patch.getTitle() != null) book.setTitle(patch.getTitle());
            if (patch.getIsbn() != null) book.setIsbn(patch.getIsbn());
            if (patch.getPublishedYear() != null) book.setPublishedYear(patch.getPublishedYear());
            if (patch.getGenre() != null) book.setGenre(patch.getGenre());

            // Only update author if the patch includes an authorId key
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

    // ─── Mapping helpers ─────────────────────────────────────────────────────

    BookResponse toResponse(Book book) {
        Author author = book.getAuthor();

        List<String> categoryNames = book.getCategories().stream()
                .map(cat -> cat.getName())
                .sorted()
                .toList();

        // ─── Stream-computed review stats ────────────────────────────────────
        //
        // book.getReviews() triggers a lazy load (safe inside @Transactional).
        //
        // IntStream.average() returns OptionalDouble — which may be empty
        // if the list is empty. We use orElse(null) via a ternary to avoid
        // returning 0.0 for an unreviewed book (null is more accurate).
        //
        int reviewCount = book.getReviews().size();

        // OptionalDouble wraps the result — empty if no reviews exist.
        // We convert to Double (boxed) so the JSON shows null, not 0.0.
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
                .author(author)
                .build();
    }

    // Looks up an Author by id, returns null if id is null.
    // Using Optional.ofNullable + map is the idiomatic stream-style null-safe way.
    private Author resolveAuthor(Long authorId) {
        return Optional.ofNullable(authorId)
                .flatMap(authorRepository::findById)
                .orElse(null);
    }
}
