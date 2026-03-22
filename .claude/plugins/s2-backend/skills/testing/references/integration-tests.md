# Integration Tests (Repository IT)

Integration tests verify the persistence layer against a real database using Testcontainers with Spring Boot 3.1+ `@ServiceConnection`.

## Conventions

- Class suffix: `IT` (e.g., `UserRepositoryIT`)
- **`@Tag("integration")`** on every IT class — used to exclude them from the normal `test` cycle
- Containers are declared once in a shared `TestcontainersConfiguration` and imported where needed
- No `@DynamicPropertySource` — `@ServiceConnection` handles datasource auto-configuration automatically

## Shared configuration (declare once)

```java
// src/test/java/.../TestcontainersConfiguration.java
@TestConfiguration(proxyBeanMethods = false)
class TestcontainersConfiguration {

    @Bean
    @ServiceConnection
    PostgreSQLContainer<?> postgresContainer() {
        return new PostgreSQLContainer<>("postgres:16");
    }
}
```

## Base class (optional, for convenience)

```java
@Tag("integration")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(TestcontainersConfiguration.class)
public abstract class IntegrationTestBase {
}
```

## Test class

```java
@Tag("integration")
@SpringBootTest
@Import(TestcontainersConfiguration.class)
class UserRepositoryIT {

    @Autowired
    UserRepository repository;

    @Test
    void shouldSaveAndFindUserById() {
        User user = UserMother.create();
        repository.save(user);

        Optional<User> found = repository.findById(user.id());

        assertThat(found).isPresent();
        assertThat(found.get().email()).isEqualTo(user.email());
    }

    @Test
    void shouldReturnEmptyWhenUserDoesNotExist() {
        Optional<User> found = repository.findById(UUID.randomUUID());

        assertThat(found).isEmpty();
    }
}
```

## Why this pattern

- `@ServiceConnection` auto-configures `spring.datasource.*` from the running container — no manual property mapping
- `TestcontainersConfiguration` is shared: all IT tests import the same class, Spring reuses the container when the context is reused
- No `@Testcontainers` / `@Container` annotations scattered across test classes
- Using `IntegrationTestBase` as a convenience base is acceptable to reduce boilerplate
