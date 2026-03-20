# Request Validation in Controllers

## Rule: Validate requests using Jakarta Validation in internal records

### Convention
- Define request DTOs as **internal records** within the controller
- Use Jakarta Validation annotations (`@NotNull`, `@NotBlank`, `@Size`, etc.)
- Use `@Valid` annotation on the request parameter
- Validation happens at controller level, use cases assume valid input

### Benefits
- **Encapsulation**: Request structure is internal to the controller
- **Clear validation**: Declarative validation rules
- **Early feedback**: Invalid requests rejected before reaching business logic
- **Automatic error responses**: Spring handles validation error responses

### Examples

#### Request with validation
```java
@RestController
class CreateTaskPostController {
    // ...

    @PostMapping(path = "/tasks", consumes = "application/json")
    @ResponseStatus(HttpStatus.CREATED)
    void handle(@Valid @RequestBody CreateTaskRequest request) {
        useCase.execute(request.id(), request.title(), request.description());
    }

    record CreateTaskRequest(
        @NotNull(message = "ID is required")
        UUID id,

        @NotBlank(message = "Title is required")
        @Size(max = 100, message = "Title cannot exceed 100 characters")
        String title,

        @Size(max = 500, message = "Description cannot exceed 500 characters")
        String description
    ) {}
}
```

#### Update request (ID in path, not body)
```java
record UpdateTaskRequest(
    @NotBlank(message = "Title is required")
    @Size(max = 100) String title,

    @Size(max = 500) String description
) {}
```
