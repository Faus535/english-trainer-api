# Constructor Injection

## Rule: Always use constructor injection for dependencies

### Convention
- No `@Autowired` field injection
- Constructor with all dependencies
- Spring auto-wires single constructor
- Makes testing easy with manual injection

### Benefits
- **Testability**: Easy to inject mocks/fakes
- **Immutability**: Dependencies set once at creation
- **Explicit dependencies**: Clear what a class needs
- **Required dependencies**: Compiler enforces non-null

### Examples

#### Good: Constructor injection
```java
@UseCase
public class CreateUserUseCase {
    private final UserRepository repository;
    private final EventPublisher eventPublisher;

    public CreateUserUseCase(UserRepository repository, EventPublisher eventPublisher) {
        this.repository = repository;
        this.eventPublisher = eventPublisher;
    }

    public void execute(UUID id, String name, String email) {
        // ...
    }
}
```

#### Bad: Field injection
```java
@UseCase
public class CreateUserUseCase {
    @Autowired
    private UserRepository repository;  // Avoid this

    @Autowired
    private EventPublisher eventPublisher;  // Avoid this
}
```

#### Testing with constructor injection
```java
@Test
void shouldCreateUser() {
    var repository = new InMemoryUserRepository();
    var eventPublisher = mock(EventPublisher.class);
    var useCase = new CreateUserUseCase(repository, eventPublisher);

    useCase.execute(UUID.randomUUID(), "John", "john@example.com");

    assertThat(repository.count()).isEqualTo(1);
}
```
