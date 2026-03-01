package com.library.bookapi.service;

import com.library.bookapi.dto.BookPatchRequest;
import com.library.bookapi.dto.BookRequest;
import com.library.bookapi.dto.BookResponse;
import com.library.bookapi.entity.Book;
import com.library.bookapi.repository.BookRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BookService {

    private final BookRepository bookRepository;

    public List<BookResponse> getAll() {
        return bookRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    public Optional<BookResponse> getById(Long id) {
        return bookRepository.findById(id).map(this::toResponse);
    }

    public BookResponse create(BookRequest request) {
        Book book = toEntity(request);
        return toResponse(bookRepository.save(book));
    }

    public Optional<BookResponse> update(Long id, BookRequest request) {
        return bookRepository.findById(id).map(book -> {
            book.setTitle(request.getTitle());
            book.setAuthor(request.getAuthor());
            book.setIsbn(request.getIsbn());
            book.setPublishedYear(request.getPublishedYear());
            book.setGenre(request.getGenre());
            return toResponse(bookRepository.save(book));
        });
    }

    public Optional<BookResponse> partialUpdate(Long id, BookPatchRequest patch) {
        return bookRepository.findById(id).map(book -> {
            if (patch.getTitle() != null) book.setTitle(patch.getTitle());
            if (patch.getAuthor() != null) book.setAuthor(patch.getAuthor());
            if (patch.getIsbn() != null) book.setIsbn(patch.getIsbn());
            if (patch.getPublishedYear() != null) book.setPublishedYear(patch.getPublishedYear());
            if (patch.getGenre() != null) book.setGenre(patch.getGenre());
            return toResponse(bookRepository.save(book));
        });
    }

    public boolean delete(Long id) {
        if (!bookRepository.existsById(id)) return false;
        bookRepository.deleteById(id);
        return true;
    }

    private BookResponse toResponse(Book book) {
        return BookResponse.builder()
                .id(book.getId())
                .title(book.getTitle())
                .author(book.getAuthor())
                .isbn(book.getIsbn())
                .publishedYear(book.getPublishedYear())
                .genre(book.getGenre())
                .build();
    }

    private Book toEntity(BookRequest request) {
        return Book.builder()
                .title(request.getTitle())
                .author(request.getAuthor())
                .isbn(request.getIsbn())
                .publishedYear(request.getPublishedYear())
                .genre(request.getGenre())
                .build();
    }
}
