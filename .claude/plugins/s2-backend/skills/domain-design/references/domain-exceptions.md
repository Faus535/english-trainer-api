# Domain Exceptions

## Rule: Extend Exception, never RuntimeException

### Convention
- All domain exceptions extend `Exception` (checked)
- One base exception per aggregate: `{Aggregate}Exception`
- Specific exceptions extend the base
- Located in `domain/error/` package

### Benefits
- **Explicit handling**: Compiler forces handling
- **Clear contracts**: Method signatures show possible failures
- **Safer code**: Can't accidentally ignore exceptions
- **Documentation**: Self-documenting error cases

### Examples

#### Base exception
```java
public class UserException extends Exception {
    public UserException(String message) {
        super(message);
    }
}
```

#### Specific exceptions
```java
public class UserNotFoundException extends UserException {
    public UserNotFoundException(UUID id) {
        super("User not found with id: " + id);
    }
}

public class UserAlreadyExistsException extends UserException {
    public UserAlreadyExistsException(UUID id) {
        super("User already exists with id: " + id);
    }
}

public class InvalidUserNameException extends UserException {
    public InvalidUserNameException(String name) {
        super("Invalid user name: " + name);
    }
}
```

#### Use case with checked exception
```java
@UseCase
public class GetUserByIdUseCase {
    public User execute(UUID id) throws UserNotFoundException {
        return repository.findById(id)
            .orElseThrow(() -> new UserNotFoundException(id));
    }
}
```

#### Use case throwing checked exception
```java
@UseCase
public class CreateUserUseCase {
    public User execute(UUID id, String name, String email) throws UserAlreadyExistsException {
        if (repository.exists(id)) {
            throw new UserAlreadyExistsException(id);
        }
        User user = User.create(id, name, email, UserStatus.PENDING, LocalDateTime.now());
        repository.save(user);
        return user;
    }
}
```
