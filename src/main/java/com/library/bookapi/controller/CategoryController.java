package com.library.bookapi.controller;

import com.library.bookapi.dto.BookResponse;
import com.library.bookapi.dto.CategoryRequest;
import com.library.bookapi.dto.CategoryResponse;
import com.library.bookapi.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @GetMapping
    public List<CategoryResponse> getAll() {
        return categoryService.getAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<CategoryResponse> getById(@PathVariable Long id) {
        return categoryService.getById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // GET /categories/{id}/books
    @GetMapping("/{id}/books")
    public List<BookResponse> getBooks(@PathVariable Long id) {
        return categoryService.getBooksForCategory(id);
    }

    // GET /categories?empty=true — categories with no books
    @GetMapping(params = "empty")
    public List<CategoryResponse> getEmpty() {
        return categoryService.getEmptyCategories();
    }

    @PostMapping
    public ResponseEntity<CategoryResponse> create(@RequestBody CategoryRequest request) {
        CategoryResponse created = categoryService.create(request);
        return ResponseEntity
                .created(URI.create("/categories/" + created.getId()))
                .body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CategoryResponse> update(
            @PathVariable Long id,
            @RequestBody CategoryRequest request) {
        return categoryService.update(id, request)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        return categoryService.delete(id)
                ? ResponseEntity.noContent().build()
                : ResponseEntity.notFound().build();
    }

    // ─── Relationship management endpoints ───────────────────────────────────

    // PUT /categories/{categoryId}/books/{bookId} — assign category to book
    @PutMapping("/{categoryId}/books/{bookId}")
    public ResponseEntity<BookResponse> addToBook(
            @PathVariable Long categoryId,
            @PathVariable Long bookId) {
        return categoryService.addCategoryToBook(bookId, categoryId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // DELETE /categories/{categoryId}/books/{bookId} — remove category from book
    @DeleteMapping("/{categoryId}/books/{bookId}")
    public ResponseEntity<BookResponse> removeFromBook(
            @PathVariable Long categoryId,
            @PathVariable Long bookId) {
        return categoryService.removeCategoryFromBook(bookId, categoryId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
