# Value Objects

## Rule: Use Java Records for immutable value objects with validation

### Convention
- Implement as Java **records**
- Validate in the **compact constructor**
- Method `value()` to access the data
- Located in `domain/` package

### Benefits
- **Immutability**: Records are immutable by design
- **Validation**: Business rules enforced at creation
- **Type safety**: Prevents primitive obsession
- **Self-documenting**: Clear domain concepts

### Examples

#### Value Object with validation
```java
public record TaskTitle(String value) {
    public TaskTitle {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Task title cannot be null or empty");
        }
        if (value.length() > 100) {
            throw new IllegalArgumentException("Task title cannot exceed 100 characters");
        }
    }
}
```

#### Simple Value Object
```java
public record TaskCreatedAt(LocalDateTime value) {
    public TaskCreatedAt {
        Objects.requireNonNull(value, "Created at cannot be null");
    }
}
```

#### Enum as Value Object
```java
public enum TaskStatus {
    PENDING,
    IN_PROGRESS,
    COMPLETED;

    public static TaskStatus fromString(String status) {
        return TaskStatus.valueOf(status);
    }

    @Override
    public String toString() {
        return this.name();
    }
}
```
