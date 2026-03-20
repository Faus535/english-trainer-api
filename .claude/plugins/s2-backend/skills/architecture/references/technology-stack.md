# Technology Stack

## Rule: Use latest stable versions of Java and Spring Boot

### Convention
- **Java**: Latest LTS version (Java 21+)
- **Spring Boot**: Latest stable 3.x release
- **Build Tool**: Gradle (`config.gradle`) or Maven (`pom.xml`)
- **HTTP Client**: Spring Framework 6+ `RestClient` (avoid RestTemplate)
- **Data Access**: Spring Data JDBC or Spring Data JPA for persistence

### Benefits
- **Modern features**: Access to latest language improvements (records, pattern matching)
- **Security**: Latest security patches and fixes
- **Performance**: Optimizations in newer versions
- **Support**: Long-term support from vendors

### Examples

#### RestClient usage (recommended)
```java
@Repository
public class RestClientUserRepository implements UserRepository {
    private final RestClient restClient;

    public RestClientUserRepository(RestClient.Builder builder) {
        this.restClient = builder.baseUrl("https://api.backend.com").build();
    }

    @Override
    public User save(User user) {
        return restClient.post()
            .uri("/users")
            .body(user)
            .retrieve()
            .body(User.class);
    }
}
```

#### Java Records for DTOs
```java
public record User(
    UserId id,
    String name,
    Email email,
    LocalDateTime createdAt
) {
    public User {
        Objects.requireNonNull(id, "User ID cannot be null");
        Objects.requireNonNull(name, "Name cannot be null");
    }
}
```
