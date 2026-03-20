# Repository Implementation

## Rule: Implement repositories with Spring Data JDBC and package-private visibility

### Convention
- Annotate with `@Repository`
- **Package-private** visibility (no `public`)
- Constructor injection of Spring Data repository
- Bidirectional mapping: Domain <-> Entity

### Benefits
- **Encapsulation**: Implementation details hidden from other packages
- **Separation**: Domain stays clean from persistence concerns
- **Flexibility**: Easy to change persistence strategy
- **Testability**: Can be replaced with in-memory implementations

### Examples

#### Repository implementation
```java
@Repository
class TaskJdbcRepository implements TaskRepository {
    private final TaskEntityCrudRepository repository;

    TaskJdbcRepository(TaskEntityCrudRepository repository) {
        this.repository = repository;
    }

    @Override
    public boolean exists(UUID id) {
        return repository.existsById(id);
    }

    @Override
    public Optional<Task> findById(UUID id) {
        return repository.findById(id).map(TaskEntity::toDomain);
    }

    @Override
    public void save(Task task) {
        TaskEntity entity = new TaskEntity(task);
        if (repository.existsById(task.id())) {
            entity.markAsExisting();
        }
        repository.save(entity);
    }

    @Override
    public List<Task> findAll() {
        return StreamSupport.stream(repository.findAll().spliterator(), false)
            .map(TaskEntity::toDomain)
            .toList();
    }

    @Override
    public void delete(UUID id) {
        repository.deleteById(id);
    }
}
```

#### Spring Data interface
```java
@Repository
interface TaskEntityCrudRepository extends CrudRepository<TaskEntity, UUID> {
    @Query("SELECT status, COUNT(*) as count FROM tasks GROUP BY status")
    List<TaskByStatusProjection> countByStatusProjection();
}
```
