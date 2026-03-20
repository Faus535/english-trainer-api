# Domain Events

## Rule: Use records annotated with @DomainEvent for domain events

### Convention
- Implement as **records**
- Annotate with `@DomainEvent` (jmolecules)
- Name in past tense: `{Entity}{Action}Event`
- Located in `{module}/events/` package
- Create `package-info.java` for Spring Modulith

### Benefits
- **Decoupling**: Modules communicate via events
- **Audit trail**: Events document what happened
- **Async processing**: Events can be handled asynchronously
- **Extensibility**: New listeners without changing domain

### Examples

#### Domain event
```java
@DomainEvent
public record TaskCreatedEvent(
    UUID id,
    String title,
    String description,
    String status,
    LocalDateTime createdAt
) {}
```

#### Package info for Spring Modulith
```java
@org.springframework.modulith.NamedInterface("task-domain-events")
package com.s2grupo.crud.task.events;
```

#### Event listener
```java
@Component
class CreatedTaskEventListener {
    private static final Logger log = LoggerFactory.getLogger(CreatedTaskEventListener.class);

    @ApplicationModuleListener
    void on(TaskCreatedEvent event) {
        log.info("Task created: {}", event);
    }
}
```
