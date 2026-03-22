# One Controller Per Action

## Convention

- **Naming**: `{Action}Controller.java` (e.g., `CreateUserController`, `GetUserProfileController`)
- **Location**: `infrastructure/controller/` package
- **Visibility**: Package-private (no `public` modifier)
- **Method**: Single method named `handle`, delegates entirely to the Use Case
- **Injection**: Constructor only, no `@Autowired`

## POST — create resource → 201 Created

```java
@RestController
class CreateUserController {
    private final CreateUserUseCase useCase;

    CreateUserController(CreateUserUseCase useCase) {
        this.useCase = useCase;
    }

    @PostMapping("/users")
    @ResponseStatus(HttpStatus.CREATED)
    void handle(@Valid @RequestBody CreateUserRequest request) throws UserException {
        useCase.execute(request.name(), request.email());
    }

    record CreateUserRequest(
        @NotBlank @Size(max = 100) String name,
        @NotBlank String email
    ) {}
}
```

## GET by ID — read single resource → 200 OK

```java
@RestController
class GetUserByIdController {
    private final GetUserByIdUseCase useCase;

    GetUserByIdController(GetUserByIdUseCase useCase) {
        this.useCase = useCase;
    }

    @GetMapping("/users/{id}")
    ResponseEntity<UserResponse> handle(@PathVariable UUID id) throws UserException {
        return ResponseEntity.ok(UserResponse.fromDto(useCase.execute(id)));
    }
}
```

## PUT — full replacement → 200 OK

All fields required in the body. Idempotent.

```java
@RestController
class UpdateUserController {
    private final UpdateUserUseCase useCase;

    UpdateUserController(UpdateUserUseCase useCase) {
        this.useCase = useCase;
    }

    @PutMapping("/users/{id}")
    ResponseEntity<UserResponse> handle(
        @PathVariable UUID id,
        @Valid @RequestBody UpdateUserRequest request
    ) throws UserException {
        return ResponseEntity.ok(UserResponse.fromDto(useCase.execute(id, request.name(), request.email())));
    }

    record UpdateUserRequest(
        @NotBlank @Size(max = 100) String name,
        @NotBlank String email
    ) {}
}
```

## PATCH — partial update → 200 OK

Fields are nullable; only non-null fields are applied.

```java
@RestController
class PatchUserController {
    private final PatchUserUseCase useCase;

    PatchUserController(PatchUserUseCase useCase) {
        this.useCase = useCase;
    }

    @PatchMapping("/users/{id}")
    ResponseEntity<UserResponse> handle(
        @PathVariable UUID id,
        @Valid @RequestBody PatchUserRequest request
    ) throws UserException {
        return ResponseEntity.ok(UserResponse.fromDto(useCase.execute(id, request.name(), request.email())));
    }

    record PatchUserRequest(
        @Size(max = 100) String name,  // nullable — only updated if provided
        String email
    ) {}
}
```

## DELETE — remove resource → 204 No Content

No response body.

```java
@RestController
class DeleteUserController {
    private final DeleteUserUseCase useCase;

    DeleteUserController(DeleteUserUseCase useCase) {
        this.useCase = useCase;
    }

    @DeleteMapping("/users/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void handle(@PathVariable UUID id) throws UserException {
        useCase.execute(id);
    }
}
```
