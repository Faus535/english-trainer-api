# One Use Case Per Action

## Rule: Create one use case class per business operation

### Convention
- **Naming**: `{Action}UseCase.java` (e.g., `CreateUserUseCase.java`)
- **Location**: `application/` package
- **Method**: Single public method named `execute`
- **Annotation**: `@UseCase`
- **Assumption**: Input parameters are already validated

### Benefits
- **Single Responsibility**: Each use case handles one business operation
- **Testability**: Easy to unit test with mocked dependencies
- **Reusability**: Use cases can be invoked from different entry points
- **Clarity**: Clear business operations mapping

### Examples

#### Create use case
```java
@UseCase
public class CreateUserUseCase {
    private final UserRepository repository;
    private final ApplicationEventPublisher eventPublisher;

    public CreateUserUseCase(UserRepository repository, ApplicationEventPublisher eventPublisher) {
        this.repository = repository;
        this.eventPublisher = eventPublisher;
    }

    public User execute(UUID id, String name, String email) throws UserAlreadyExistsException {
        if (repository.exists(id)) {
            throw new UserAlreadyExistsException(id);
        }
        User user = User.create(id, name, email, UserStatus.PENDING, LocalDateTime.now());
        repository.save(user);
        eventPublisher.publishEvent(new UserCreatedEvent(user.id(), user.name(), user.email(), user.createdAt()));
        return user;
    }
}
```

#### Find use case
```java
@UseCase
public class GetUserByIdUseCase {
    private final UserRepository repository;

    public GetUserByIdUseCase(UserRepository repository) {
        this.repository = repository;
    }

    public UserDto execute(UUID id) throws UserNotFoundException {
        return repository.findById(id)
            .map(UserDto::from)
            .orElseThrow(() -> new UserNotFoundException(id));
    }
}
```

#### Delete use case
```java
@UseCase
public class DeleteUserUseCase {
    private final UserRepository repository;
    private final ApplicationEventPublisher eventPublisher;

    public DeleteUserUseCase(UserRepository repository, ApplicationEventPublisher eventPublisher) {
        this.repository = repository;
        this.eventPublisher = eventPublisher;
    }

    public void execute(UUID id) throws UserNotFoundException {
        if (!repository.exists(id)) {
            throw new UserNotFoundException(id);
        }
        repository.delete(id);
        eventPublisher.publishEvent(new UserDeletedEvent(id));
    }
}
```
