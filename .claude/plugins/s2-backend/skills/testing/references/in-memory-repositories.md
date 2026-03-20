# In-Memory Repositories

## Rule: Create InMemory repository implementations for unit testing

### Convention
- Located in **test packages**
- Implement domain repository interface
- Use `HashMap` for storage
- Reset state between tests

### Benefits
- **Fast tests**: No database or network
- **Isolation**: Tests don't share state
- **Predictable**: Deterministic behavior
- **Simple**: Easy to implement and understand

### Examples

#### In-memory repository
```java
public class InMemoryUserRepository implements UserRepository {
    private final Map<UUID, User> users = new HashMap<>();

    @Override
    public boolean exists(UUID id) {
        return users.containsKey(id);
    }

    @Override
    public Optional<User> findById(UUID id) {
        return Optional.ofNullable(users.get(id));
    }

    @Override
    public void save(User user) {
        users.put(user.id(), user);
    }

    @Override
    public List<User> findAll() {
        return new ArrayList<>(users.values());
    }

    @Override
    public void delete(UUID id) {
        users.remove(id);
    }

    // Test helper methods
    public void clear() {
        users.clear();
    }

    public int count() {
        return users.size();
    }
}
```

#### Usage in tests
```java
class CreateUserUseCaseTest {
    private InMemoryUserRepository repository;
    private CreateUserUseCase useCase;

    @BeforeEach
    void setUp() {
        repository = new InMemoryUserRepository();
        useCase = new CreateUserUseCase(repository);
    }

    @Test
    void shouldCreateUser() {
        UUID id = UUID.randomUUID();

        useCase.execute(id, "John", "john@example.com");

        assertThat(repository.exists(id)).isTrue();
    }
}
```
