# Shared Module Template

## Rule: Provide base classes for DDD patterns

### Convention
- Located in `{base-package}/shared/`
- Contains abstract classes, interfaces, and base exceptions
- No business logic — only cross-cutting infrastructure

### AggregateRoot Base Class

```java
package {base-package}.shared.abstracts;

import org.springframework.data.annotation.Transient;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class AggregateRoot<T> {

    @Transient
    private final List<Object> domainEvents = new ArrayList<>();

    protected void registerEvent(Object event) {
        domainEvents.add(event);
    }

    public List<Object> domainEvents() {
        return Collections.unmodifiableList(domainEvents);
    }

    public void clearDomainEvents() {
        domainEvents.clear();
    }

    public abstract T id();
}
```

### EventPublisher Interface

```java
package {base-package}.shared.events;

public interface EventPublisher {
    void publish(Object event);
}
```

### SpringEventPublisher Implementation

```java
package {base-package}.shared.events;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
public class SpringEventPublisher implements EventPublisher {

    private final ApplicationEventPublisher publisher;

    public SpringEventPublisher(ApplicationEventPublisher publisher) {
        this.publisher = publisher;
    }

    @Override
    public void publish(Object event) {
        publisher.publishEvent(event);
    }
}
```

### Base Exceptions

```java
package {base-package}.shared.exceptions;

public class NotFoundException extends Exception {
    public NotFoundException(String message) {
        super(message);
    }
}
```

```java
package {base-package}.shared.exceptions;

public class AlreadyExistsException extends Exception {
    public AlreadyExistsException(String message) {
        super(message);
    }
}
```

### package-info.java

```java
@org.springframework.modulith.ApplicationModule(
    allowedDependencies = {}
)
package {base-package}.shared;
```

### Notes
- `@UseCase` annotation is NOT included in shared — it is specific to the Modulith style
- Base exceptions are checked (extend `Exception`, not `RuntimeException`)
- AggregateRoot uses `@Transient` from Spring Data to exclude events from persistence
