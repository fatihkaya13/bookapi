package com.library.bookapi.controller;

import com.library.bookapi.dto.AuthorPatchRequest;
import com.library.bookapi.dto.AuthorRequest;
import com.library.bookapi.dto.AuthorResponse;
import com.library.bookapi.dto.BookResponse;
import com.library.bookapi.service.AuthorService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/authors")
@RequiredArgsConstructor
public class AuthorController {

    private final AuthorService authorService;

    @GetMapping
    public List<AuthorResponse> getAll() {
        return authorService.getAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<AuthorResponse> getById(@PathVariable Long id) {
        return authorService.getById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // GET /authors/{id}/books — traverse the relationship via the API
    @GetMapping("/{id}/books")
    public List<BookResponse> getBooks(@PathVariable Long id) {
        return authorService.getBooksForAuthor(id);
    }

    // GET /authors?withBooks=true — filter authors by whether they have books
    @GetMapping(params = "withBooks")
    public List<AuthorResponse> getWithBooks() {
        return authorService.getAuthorsWithBooks();
    }

    @PostMapping
    public ResponseEntity<AuthorResponse> create(@RequestBody AuthorRequest request) {
        AuthorResponse created = authorService.create(request);
        return ResponseEntity
                .created(URI.create("/authors/" + created.getId()))
                .body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<AuthorResponse> update(
            @PathVariable Long id,
            @RequestBody AuthorRequest request) {
        return authorService.update(id, request)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PatchMapping("/{id}")
    public ResponseEntity<AuthorResponse> partialUpdate(
            @PathVariable Long id,
            @RequestBody AuthorPatchRequest patch) {
        return authorService.partialUpdate(id, patch)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        return authorService.delete(id)
                ? ResponseEntity.noContent().build()
                : ResponseEntity.notFound().build();
    }
}
