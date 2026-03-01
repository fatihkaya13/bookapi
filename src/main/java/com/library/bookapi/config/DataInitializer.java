package com.library.bookapi.config;

import com.library.bookapi.entity.Author;
import com.library.bookapi.entity.Book;
import com.library.bookapi.entity.Category;
import com.library.bookapi.repository.AuthorRepository;
import com.library.bookapi.repository.BookRepository;
import com.library.bookapi.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final AuthorRepository authorRepository;
    private final BookRepository bookRepository;
    private final CategoryRepository categoryRepository;

    @Override
    public void run(String... args) {

        // ── 1. Save authors ───────────────────────────────────────────────────
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

        // ── 2. Save categories ────────────────────────────────────────────────
        Category programming = categoryRepository.save(
                Category.builder().name("Programming").description("General programming books").build());

        Category softwareEngineering = categoryRepository.save(
                Category.builder().name("Software Engineering").description("Software design and architecture").build());

        Category bestPractices = categoryRepository.save(
                Category.builder().name("Best Practices").description("Clean code and professional practices").build());

        Category designPatterns = categoryRepository.save(
                Category.builder().name("Design Patterns").description("Reusable object-oriented solutions").build());

        // ── 3. Save books and assign categories via the owning side ───────────
        //
        // We build the book first, then call addCategory() which updates BOTH
        // sides in memory (book.categories and category.books).
        // bookRepository.save() then flushes the join table rows.
        //
        Book cleanCode = Book.builder()
                .title("Clean Code").isbn("9780132350884")
                .publishedYear(2008).genre("Programming").author(martin).build();
        cleanCode.addCategory(programming);
        cleanCode.addCategory(bestPractices);      // ← two categories
        bookRepository.save(cleanCode);

        Book pragmatic = Book.builder()
                .title("The Pragmatic Programmer").isbn("9780135957059")
                .publishedYear(2019).genre("Programming").author(hunt).build();
        pragmatic.addCategory(programming);
        pragmatic.addCategory(bestPractices);
        bookRepository.save(pragmatic);

        Book designPatternsBook = Book.builder()
                .title("Design Patterns").isbn("9780201633610")
                .publishedYear(1994).genre("Software Engineering").author(gamma).build();
        designPatternsBook.addCategory(softwareEngineering);
        designPatternsBook.addCategory(designPatterns);
        bookRepository.save(designPatternsBook);

        Book effectiveJava = Book.builder()
                .title("Effective Java").isbn("9780134685991")
                .publishedYear(2018).genre("Programming").author(bloch).build();
        effectiveJava.addCategory(programming);
        effectiveJava.addCategory(bestPractices);
        bookRepository.save(effectiveJava);

        Book refactoring = Book.builder()
                .title("Refactoring").isbn("9780134757599")
                .publishedYear(2018).genre("Software Engineering").author(fowler).build();
        refactoring.addCategory(softwareEngineering);
        refactoring.addCategory(bestPractices);
        bookRepository.save(refactoring);

        Book cleanArch = Book.builder()
                .title("Clean Architecture").isbn("9780134494166")
                .publishedYear(2017).genre("Software Engineering").author(martin).build();
        cleanArch.addCategory(softwareEngineering);
        cleanArch.addCategory(designPatterns);
        bookRepository.save(cleanArch);
    }
}
