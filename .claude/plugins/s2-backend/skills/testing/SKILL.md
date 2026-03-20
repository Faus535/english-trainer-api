---
name: testing
description: This skill should be used when the user asks to "write tests", "unit test", "integration test", "Object Mother", "test data", "in-memory repository", or "Testcontainers". Defines testing strategy with Object Mothers, In-Memory Repositories, and constructor injection.
---

# Skill: Testing

Defines the testing strategy and patterns for writing maintainable and fast tests.

## When to use

- When writing unit tests for Use Cases
- When creating test doubles (mocks, fakes, in-memory)
- When defining test data with Object Mothers
- When writing integration tests with Testcontainers

## Key rules

### Object Mothers

1. One `{Aggregate}Mother` per aggregate
2. Static `create()` method returns object with defaults
3. Static `builder()` method returns Mother for customization
4. `with{Property}()` methods for fluent customization
5. Located in test package `mothers/`

### In-Memory Repositories

1. Implement the domain repository interface
2. Use `HashMap` for storage
3. Located in test packages
4. Helper methods: `clear()`, `count()`
5. Alternative to mocks for more readable tests

### Constructor Injection

1. No `@Autowired` on fields (field injection)
2. Constructor with all dependencies
3. Spring auto-wires single constructors
4. Facilitates testing with manual injection of mocks/fakes

### Strategy by layer

1. **Unit tests (Application)**: Use Case tests with mocks or in-memory repos
2. **Integration tests (Infrastructure)**: Repository tests with Testcontainers
3. **NO tests in Domain**: Domain logic is tested indirectly via Use Cases

### Test naming convention

- Required pattern: `should{ExpectedResult}When{Condition}()`
- Examples: `shouldCreateUserWhenValidRequest()`, `shouldThrowNotFoundWhenUserDoesNotExist()`

### Integration test pattern (@ServiceConnection)

- Use `IT` suffix + **`@Tag("integration")`** on every IT class
- Declare containers once in a shared `TestcontainersConfiguration` (`@TestConfiguration` + `@ServiceConnection`)
- Import it per test class with `@Import(TestcontainersConfiguration.class)` — no `@DynamicPropertySource` needed
- See [Integration Tests](references/integration-tests.md)

### Controller tests (MockMvc)

- `@WebMvcTest` per controller (not full `@SpringBootTest`)
- Verify: HTTP status, response body, that the Use Case was invoked
- See [Controller Tests](references/controller-tests.md)

### Minimum coverage

- Use Cases: 100% — happy path + all expected error cases
- Repositories: mandatory integration test (IT) with a real database
- Controllers: happy path + at least one error (400 or 404) as integration test

## References

- [Object Mothers](references/object-mothers.md)
- [In-Memory Repositories](references/in-memory-repositories.md)
- [Constructor Injection](references/constructor-injection.md)
- [Integration Tests](references/integration-tests.md)
- [Controller Tests](references/controller-tests.md)
