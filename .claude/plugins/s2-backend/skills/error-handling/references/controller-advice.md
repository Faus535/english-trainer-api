# ControllerAdvice

The `ControllerAdvice` is the single point where domain exceptions are mapped to HTTP responses.

## Rules

- One `{Aggregate}ControllerAdvice` per module, package-private
- `ApiError` contains only: a stable error **code** and a **generic message** — no IDs, no internal details
- Log the full exception context (IDs, cause) at ERROR level before returning the response

## Example

```java
@RestControllerAdvice
class UserControllerAdvice {

    private static final Logger log = LoggerFactory.getLogger(UserControllerAdvice.class);

    @ExceptionHandler(UserNotFoundException.class)
    ResponseEntity<ApiError> handleNotFound(UserNotFoundException ex) {
        log.error("User not found: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(new ApiError("USER_NOT_FOUND", "The requested user does not exist"));
    }

    @ExceptionHandler(UserAlreadyExistsException.class)
    ResponseEntity<ApiError> handleConflict(UserAlreadyExistsException ex) {
        log.error("User already exists: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT)
            .body(new ApiError("USER_ALREADY_EXISTS", "A user with that identifier already exists"));
    }

    record ApiError(String code, String message) {}
}
```

## HTTP mappings

| Exception type | HTTP status |
|---|---|
| `NotFoundException` | 404 Not Found |
| `AlreadyExistsException` | 409 Conflict |
| `ValidationException` | 422 Unprocessable Entity |
