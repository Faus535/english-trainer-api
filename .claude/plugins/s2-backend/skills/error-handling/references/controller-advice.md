# ControllerAdvice

The `ControllerAdvice` is the single point where domain exceptions are mapped to HTTP responses.

## Rules

- One `{Aggregate}ControllerAdvice` per module, package-private
- Scoped with `@RestControllerAdvice(basePackages = "...")`
- `ApiError` contains only: a stable error **code** and a **generic message** — no IDs, no internal details
- Log the full exception context (IDs, cause) at ERROR level before returning the response
- Handle all module-specific exceptions; the `GlobalControllerAdvice` is only a safety net

## Example

```java
@RestControllerAdvice(basePackages = "com.example.user")
class UserControllerAdvice {

    private static final Logger log = LoggerFactory.getLogger(UserControllerAdvice.class);

    record ApiError(String code, String message) {}

    @ExceptionHandler(UserNotFoundException.class)
    ResponseEntity<ApiError> handleNotFound(UserNotFoundException ex) {
        log.error("User not found: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(new ApiError("not_found", "User not found"));
    }

    @ExceptionHandler(UserAlreadyExistsException.class)
    ResponseEntity<ApiError> handleConflict(UserAlreadyExistsException ex) {
        log.error("User already exists: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT)
            .body(new ApiError("already_exists", "A user with that identifier already exists"));
    }
}
```

## HTTP mappings

| Exception type | HTTP status |
|---|---|
| `{Aggregate}NotFoundException` | 404 Not Found |
| `{Aggregate}AlreadyExistsException` | 409 Conflict |
| Validation / invalid value | 400 Bad Request |
| `MethodArgumentNotValidException` | 422 Unprocessable Entity |
| Service unavailable | 503 Service Unavailable |
| Rate limit / too many | 429 Too Many Requests |
