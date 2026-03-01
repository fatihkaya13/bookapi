package com.library.bookapi.service;

import com.library.bookapi.dto.BookResponse;
import com.library.bookapi.dto.CategoryRequest;
import com.library.bookapi.dto.CategoryResponse;
import com.library.bookapi.entity.Book;
import com.library.bookapi.entity.Category;
import com.library.bookapi.repository.BookRepository;
import com.library.bookapi.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final BookRepository bookRepository;
    private final BookService bookService;

    public List<CategoryResponse> getAll() {
        return categoryRepository.findAll().stream()
                .map(this::toResponseWithoutBooks)
                .toList();
    }

    @Transactional(readOnly = true)
    public Optional<CategoryResponse> getById(Long id) {
        // JOIN FETCH loads books in the same query — no N+1
        return categoryRepository.findByIdWithBooks(id)
                .map(this::toResponseWithBooks);
    }

    @Transactional
    public CategoryResponse create(CategoryRequest request) {
        Category category = Category.builder()
                .name(request.getName())
                .description(request.getDescription())
                .build();
        return toResponseWithoutBooks(categoryRepository.save(category));
    }

    @Transactional
    public Optional<CategoryResponse> update(Long id, CategoryRequest request) {
        return categoryRepository.findById(id).map(cat -> {
            cat.setName(request.getName());
            cat.setDescription(request.getDescription());
            return toResponseWithoutBooks(categoryRepository.save(cat));
        });
    }

    public boolean delete(Long id) {
        if (!categoryRepository.existsById(id)) return false;
        // Note: no cascade here. Deleting a category does NOT delete books.
        // Hibernate only removes the rows from the book_category join table.
        // The books themselves remain untouched.
        categoryRepository.deleteById(id);
        return true;
    }

    // ─── Relationship management ──────────────────────────────────────────────

    // Assign a category to a book — operates on the owning side (Book)
    @Transactional
    public Optional<BookResponse> addCategoryToBook(Long bookId, Long categoryId) {
        Optional<Book> bookOpt = bookRepository.findById(bookId);
        Optional<Category> catOpt = categoryRepository.findById(categoryId);

        if (bookOpt.isEmpty() || catOpt.isEmpty()) return Optional.empty();

        Book book = bookOpt.get();
        Category category = catOpt.get();

        // addCategory() sets BOTH sides in memory, and because Book is the
        // owning side, Hibernate will INSERT into book_category on commit.
        book.addCategory(category);
        bookRepository.save(book);

        return Optional.of(bookService.toResponse(book));
    }

    // Remove a category from a book
    @Transactional
    public Optional<BookResponse> removeCategoryFromBook(Long bookId, Long categoryId) {
        Optional<Book> bookOpt = bookRepository.findById(bookId);
        Optional<Category> catOpt = categoryRepository.findById(categoryId);

        if (bookOpt.isEmpty() || catOpt.isEmpty()) return Optional.empty();

        Book book = bookOpt.get();
        book.removeCategory(catOpt.get());
        bookRepository.save(book);

        return Optional.of(bookService.toResponse(book));
    }

    // ─── Stream-based cross-relationship queries ──────────────────────────────

    // Get all books in a category
    @Transactional(readOnly = true)
    public List<BookResponse> getBooksForCategory(Long categoryId) {
        return categoryRepository.findByIdWithBooks(categoryId)
                .map(cat -> cat.getBooks().stream()
                        .map(bookService::toResponse)
                        .toList())
                .orElse(List.of());
    }

    // Get categories that have no books assigned yet
    @Transactional(readOnly = true)
    public List<CategoryResponse> getEmptyCategories() {
        return categoryRepository.findAll().stream()
                .filter(cat -> cat.getBooks().isEmpty())
                .map(this::toResponseWithoutBooks)
                .toList();
    }

    // ─── Mapping helpers ──────────────────────────────────────────────────────

    CategoryResponse toResponseWithBooks(Category category) {
        List<BookResponse> bookResponses = category.getBooks().stream()
                .map(bookService::toResponse)
                .toList();
        return CategoryResponse.builder()
                .id(category.getId())
                .name(category.getName())
                .description(category.getDescription())
                .books(bookResponses)
                .build();
    }

    CategoryResponse toResponseWithoutBooks(Category category) {
        return CategoryResponse.builder()
                .id(category.getId())
                .name(category.getName())
                .description(category.getDescription())
                .books(List.of())
                .build();
    }
}
