# Aggregate Root

## Rule: Use immutable aggregate roots that extend AggregateRoot<T>

### Convention
- Extend `AggregateRoot<T>` base class
- All fields are `final` (no setters)
- Constructors are private
- State changes return **new instances**
- Register domain events with `registerEvent()`
- Use Value Objects for attributes

### Benefits
- **Immutability**: Thread-safe and predictable behavior
- **Event sourcing ready**: Domain events track all changes
- **Encapsulation**: Business logic stays in the domain
- **Testability**: Pure functions are easy to test

### Examples

#### Aggregate Root
```java
public class Task extends AggregateRoot<Task> {
    private final UUID id;
    private final TaskTitle title;
    private final TaskDescription description;
    private final TaskStatus status;
    private final TaskCreatedAt createdAt;

    private Task(UUID id, String title, String description, TaskStatus status, LocalDateTime createdAt) {
        this.id = id;
        this.title = new TaskTitle(title);
        this.description = new TaskDescription(description);
        this.status = status;
        this.createdAt = new TaskCreatedAt(createdAt);
    }

    public static Task create(UUID id, String title, String description, TaskStatus status, LocalDateTime createdAt) {
        Task task = new Task(id, title, description, status, createdAt);
        task.registerEvent(new TaskCreatedEvent(id, title, description, status.toString(), createdAt));
        return task;
    }

    public Task update(String newTitle, String newDescription) {
        Task task = new Task(id, newTitle, newDescription, status, createdAt.value());
        task.registerEvent(new TaskUpdatedEvent(id, newTitle, newDescription));
        return task;
    }

    // Getters
    public UUID id() { return id; }
    public String title() { return title.value(); }
    public String description() { return description.value(); }
    public TaskStatus status() { return status; }
    public LocalDateTime createdAt() { return createdAt.value(); }
}
```
