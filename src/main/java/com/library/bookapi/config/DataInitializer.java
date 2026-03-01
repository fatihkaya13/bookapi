package com.library.bookapi.config;

import com.library.bookapi.entity.Author;
import com.library.bookapi.entity.Book;
import com.library.bookapi.repository.AuthorRepository;
import com.library.bookapi.repository.BookRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final AuthorRepository authorRepository;
    private final BookRepository bookRepository;

    @Override
    public void run(String... args) {

        // ── Save authors first (no books yet) ────────────────────────────────
        Author martin = authorRepository.save(
                Author.builder().firstName("Robert C.").lastName("Martin")
                        .birthYear(1952).nationality("American").build());

        Author hunt = authorRepository.save(
                Author.builder().firstName("Andrew").lastName("Hunt")
                        .birthYear(1964).nationality("American").build());

        Author gamma = authorRepository.save(
                Author.builder().firstName("Erich").lastName("Gamma")
                        .birthYear(1961).nationality("Swiss").build());

        Author bloch = authorRepository.save(
                Author.builder().firstName("Joshua").lastName("Bloch")
                        .birthYear(1961).nationality("American").build());

        Author fowler = authorRepository.save(
                Author.builder().firstName("Martin").lastName("Fowler")
                        .birthYear(1963).nationality("British").build());

        // ── Save books, setting the owning side (book.author) ─────────────────
        //
        // We set author on the Book (owning side) — this writes the author_id FK.
        // We do NOT call author.addBook() here because we're using bookRepository
        // directly. Both approaches work; addBook() is useful when you want
        // Hibernate to cascade the save through the Author side.
        //
        bookRepository.save(Book.builder()
                .title("Clean Code").isbn("9780132350884")
                .publishedYear(2008).genre("Programming").author(martin).build());

        bookRepository.save(Book.builder()
                .title("The Pragmatic Programmer").isbn("9780135957059")
                .publishedYear(2019).genre("Programming").author(hunt).build());

        bookRepository.save(Book.builder()
                .title("Design Patterns").isbn("9780201633610")
                .publishedYear(1994).genre("Software Engineering").author(gamma).build());

        bookRepository.save(Book.builder()
                .title("Effective Java").isbn("9780134685991")
                .publishedYear(2018).genre("Programming").author(bloch).build());

        bookRepository.save(Book.builder()
                .title("Refactoring").isbn("9780134757599")
                .publishedYear(2018).genre("Software Engineering").author(fowler).build());

        // ── One author with multiple books (demonstrates OneToMany in action) ──
        bookRepository.save(Book.builder()
                .title("Clean Architecture").isbn("9780134494166")
                .publishedYear(2017).genre("Software Engineering").author(martin).build());
    }
}
