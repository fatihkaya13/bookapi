package com.library.bookapi.validation;

// ─── Chain of Responsibility Pattern ─────────────────────────────────────────
//
// Each implementation is one link in the validation chain.
// Spring collects all @Component classes that implement this interface
// into a List<BookValidator>, ordered by @Order annotation.
//
// The chain runs sequentially: each validator checks one concern.
// If a validator finds a problem, it throws IllegalArgumentException
// and the chain stops. If the field is null (not provided), the
// validator skips it — no unnecessary work.
//
// To add a new validation:
//   1. Create a new @Component class implementing BookValidator
//   2. Add @Order(N) to control where it runs in the chain
//   3. Done — BookService picks it up automatically, no code changes needed
//
public interface BookValidator {
    void validate(BookValidationContext context);
}
