# Repository Interface

## Rule: Define repository interfaces in domain layer without framework dependencies

### Convention
- **Interface only** in `domain/` package
- No Spring or JPA annotations
- Define domain operations (not CRUD)
- Implementation lives in `infrastructure/`

### Benefits
- **Decoupling**: Domain doesn't depend on persistence technology
- **Testability**: Easy to create test doubles
- **Flexibility**: Can switch persistence implementation
- **Clean architecture**: Dependency inversion principle

### Examples

#### Repository interface
```java
public interface TaskRepository {
    boolean exists(UUID id);
    Optional<Task> findById(UUID id);
    void save(Task task);
    List<Task> findAll();
    void delete(UUID id);
}
```

#### Projections repository (optional)
```java
public interface TaskProjectionsRepository {
    List<TaskByStatusProjection> findAllGroupByStatus();
}
```

#### Projection record
```java
public record TaskByStatusProjection(String status, long count) {}
```
