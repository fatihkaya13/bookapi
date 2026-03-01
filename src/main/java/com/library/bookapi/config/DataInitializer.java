package com.library.bookapi.config;

import com.library.bookapi.entity.Author;
import com.library.bookapi.entity.Book;
import com.library.bookapi.entity.Category;
import com.library.bookapi.entity.Review;
import com.library.bookapi.repository.AuthorRepository;
import com.library.bookapi.repository.BookRepository;
import com.library.bookapi.repository.CategoryRepository;
import com.library.bookapi.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final AuthorRepository authorRepository;
    private final BookRepository bookRepository;
    private final CategoryRepository categoryRepository;
    private final ReviewRepository reviewRepository;

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

        // ── 3. Save books with categories ─────────────────────────────────────
        Book cleanCode = Book.builder()
                .title("Clean Code").isbn("9780132350884")
                .publishedYear(2008).genre("Programming").author(martin).build();
        cleanCode.addCategory(programming);
        cleanCode.addCategory(bestPractices);
        cleanCode = bookRepository.save(cleanCode);

        Book pragmatic = Book.builder()
                .title("The Pragmatic Programmer").isbn("9780135957059")
                .publishedYear(2019).genre("Programming").author(hunt).build();
        pragmatic.addCategory(programming);
        pragmatic.addCategory(bestPractices);
        pragmatic = bookRepository.save(pragmatic);

        Book designPatternsBook = Book.builder()
                .title("Design Patterns").isbn("9780201633610")
                .publishedYear(1994).genre("Software Engineering").author(gamma).build();
        designPatternsBook.addCategory(softwareEngineering);
        designPatternsBook.addCategory(designPatterns);
        designPatternsBook = bookRepository.save(designPatternsBook);

        Book effectiveJava = Book.builder()
                .title("Effective Java").isbn("9780134685991")
                .publishedYear(2018).genre("Programming").author(bloch).build();
        effectiveJava.addCategory(programming);
        effectiveJava.addCategory(bestPractices);
        effectiveJava = bookRepository.save(effectiveJava);

        Book refactoring = Book.builder()
                .title("Refactoring").isbn("9780134757599")
                .publishedYear(2018).genre("Software Engineering").author(fowler).build();
        refactoring.addCategory(softwareEngineering);
        refactoring.addCategory(bestPractices);
        refactoring = bookRepository.save(refactoring);

        // Clean Architecture — intentionally left with NO reviews (for /books/no-reviews)
        Book cleanArch = Book.builder()
                .title("Clean Architecture").isbn("9780134494166")
                .publishedYear(2017).genre("Software Engineering").author(martin).build();
        cleanArch.addCategory(softwareEngineering);
        cleanArch.addCategory(designPatterns);
        bookRepository.save(cleanArch);

        // ── 4. Seed reviews ───────────────────────────────────────────────────
        //
        // review.book is set on the owning side → writes the book_id FK.
        // @CreationTimestamp fills reviewDate automatically.
        //
        // Clean Code — 3 reviews (avg 4.67)
        reviewRepository.save(Review.builder()
                .reviewerName("Alice").rating(5).comment("A must-read for every programmer.")
                .book(cleanCode).build());
        reviewRepository.save(Review.builder()
                .reviewerName("Bob").rating(4).comment("Great examples, slightly repetitive.")
                .book(cleanCode).build());
        reviewRepository.save(Review.builder()
                .reviewerName("Charlie").rating(5).comment("Changed the way I write code.")
                .book(cleanCode).build());

        // The Pragmatic Programmer — 2 reviews (avg 5.0)
        reviewRepository.save(Review.builder()
                .reviewerName("Diana").rating(5).comment("Timeless advice.")
                .book(pragmatic).build());
        reviewRepository.save(Review.builder()
                .reviewerName("Eve").rating(5).comment("Best software book I've read.")
                .book(pragmatic).build());

        // Design Patterns — 2 reviews (avg 3.5)
        reviewRepository.save(Review.builder()
                .reviewerName("Frank").rating(4).comment("Dense but valuable.")
                .book(designPatternsBook).build());
        reviewRepository.save(Review.builder()
                .reviewerName("Grace").rating(3).comment("Dated examples but concepts still apply.")
                .book(designPatternsBook).build());

        // Effective Java — 1 review (avg 5.0)
        reviewRepository.save(Review.builder()
                .reviewerName("Hank").rating(5).comment("Essential for Java developers.")
                .book(effectiveJava).build());

        // Refactoring — 1 review (avg 4.0)
        reviewRepository.save(Review.builder()
                .reviewerName("Ivy").rating(4).comment("Practical and well-organized.")
                .book(refactoring).build());

        // Clean Architecture — 0 reviews (for testing /books/no-reviews endpoint)
    }
}
