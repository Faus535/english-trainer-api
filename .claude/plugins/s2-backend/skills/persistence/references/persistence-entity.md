# Persistence Entity

## Rule: Create entities implementing Persistable<UUID> with bidirectional mapping

### Convention
- Annotate with `@Table(name = "table_name")`
- Implement `Persistable<UUID>` for isNew() control
- Use `@Transient boolean exists` for update detection
- Constructor from domain + `toDomain()` method

### Benefits
- **Control**: Explicit control over insert vs update
- **Mapping**: Clean conversion between domain and persistence
- **Spring Data compatible**: Works with Spring Data JDBC
- **Isolation**: Persistence concerns stay in infrastructure

### Examples

#### Persistence entity
```java
@Table(name = "tasks")
public class TaskEntity implements Persistable<UUID> {
    @Id
    private UUID id;
    private String title;
    private String description;
    private String status;
    @Column("created_at")
    private LocalDateTime createdAt;
    @Transient
    private boolean exists = true;

    TaskEntity() {
        // Used by Spring Data
    }

    public TaskEntity(UUID id, String title, String description, String status, LocalDateTime createdAt) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.status = status;
        this.createdAt = createdAt;
    }

    public TaskEntity(Task task) {
        this(task.id(), task.title(), task.description(), task.status().toString(), task.createdAt());
    }

    public Task toDomain() {
        return new Task(id, title, description, TaskStatus.fromString(status), createdAt);
    }

    @Override
    public @Nullable UUID getId() {
        return id;
    }

    @Override
    public boolean isNew() {
        return exists;
    }

    public void markAsExisting() {
        exists = false;
    }
}
```
