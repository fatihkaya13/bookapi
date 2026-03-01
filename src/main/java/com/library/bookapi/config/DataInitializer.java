package com.library.bookapi.config;

import com.library.bookapi.entity.Book;
import com.library.bookapi.repository.BookRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final BookRepository bookRepository;

    @Override
    public void run(String... args) {
        bookRepository.saveAll(List.of(
                Book.builder().title("Clean Code").author("Robert C. Martin").isbn("9780132350884").publishedYear(2008).genre("Programming").build(),
                Book.builder().title("The Pragmatic Programmer").author("Andrew Hunt").isbn("9780135957059").publishedYear(2019).genre("Programming").build(),
                Book.builder().title("Design Patterns").author("Gang of Four").isbn("9780201633610").publishedYear(1994).genre("Software Engineering").build(),
                Book.builder().title("Effective Java").author("Joshua Bloch").isbn("9780134685991").publishedYear(2018).genre("Programming").build(),
                Book.builder().title("Refactoring").author("Martin Fowler").isbn("9780134757599").publishedYear(2018).genre("Software Engineering").build()
        ));
    }
}
