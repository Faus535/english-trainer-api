# Object Mothers

## Rule: Use Object Mother pattern for test data creation

### Convention
- One `{Aggregate}Mother` per aggregate
- Default values for all properties
- Static `create()` returns object with defaults
- Static `builder()` returns Mother for customization
- `with{Property}()` methods for fluent customization
- **Only generate methods that will actually be used in tests**. Do not create `with{Property}()` for all properties by default. Add each method only when a test needs it

### Benefits
- **Consistency**: Uniform test data creation
- **Maintainability**: Changes only in one place
- **Readability**: Clear intent in tests
- **Flexibility**: Easy customization when needed

### Examples

#### Object Mother implementation
Start with only the `with{Property}()` methods your tests actually need. Add more as tests require them.

```java
public class UserMother {
    private UUID id = UUID.randomUUID();
    private String name = "John Doe";
    private String email = "john.doe@example.com";
    private UserStatus status = UserStatus.PENDING;
    private LocalDateTime createdAt = LocalDateTime.now();

    public static User create() {
        return builder().build();
    }

    public static UserMother builder() {
        return new UserMother();
    }

    public User build() {
        return new User(id, name, email, status, createdAt);
    }

    // Add with{Property}() only when a test needs to vary that field:
    public UserMother withId(UUID id) {
        this.id = id;
        return this;
    }

    public UserMother withStatus(UserStatus status) {
        this.status = status;
        return this;
    }
}
```

#### Usage in tests
```java
@Test
void shouldCreateUser() {
    // With defaults
    User user = UserMother.create();

    // With customization — only fields the test cares about
    User activeUser = UserMother.builder()
        .withStatus(UserStatus.ACTIVE)
        .build();
}
```
