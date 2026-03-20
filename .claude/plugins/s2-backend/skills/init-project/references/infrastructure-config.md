# Infrastructure Configuration Template

## Rule: Configure development and test infrastructure

### Convention
- Docker Compose for local development database
- Spring profiles for environment-specific configuration
- Testcontainers for integration test isolation

### docker-compose.yml

```yaml
services:
  postgres:
    image: postgres:16-alpine
    container_name: {project-name}-postgres
    environment:
      POSTGRES_DB: {project-name-underscored}
      POSTGRES_USER: dev
      POSTGRES_PASSWORD: dev
    ports:
      - "5432:5432"
    volumes:
      - postgres-data:/var/lib/postgresql/data

volumes:
  postgres-data:
```

### application.yml

```yaml
spring:
  application:
    name: {project-name}
  datasource:
    url: jdbc:postgresql://localhost:5432/{project-name-underscored}
    username: dev
    password: dev
    driver-class-name: org.postgresql.Driver
  flyway:
    enabled: true
    locations: classpath:db/migration
```

### application-test.yml

```yaml
spring:
  datasource:
    url: jdbc:tc:postgresql:16-alpine:///{project-name-underscored}
    driver-class-name: org.testcontainers.jdbc.ContainerDatabaseDriver
  flyway:
    enabled: true
    locations: classpath:db/migration
```

### Application.java

```java
package {base-package};

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
```

### ApplicationTests.java

```java
package {base-package};

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
class ApplicationTests {
    @Test
    void contextLoads() {
    }
}
```

### .gitignore

```
# Gradle
.gradle/
build/

# IDE
.idea/
*.iml
.vscode/

# OS
.DS_Store
Thumbs.db

# Environment
.env
```

### Notes
- Use PostgreSQL 16+ Alpine for smaller image size
- Testcontainers JDBC URL (`jdbc:tc:`) auto-starts containers
- `@ActiveProfiles("test")` activates the test configuration
- Flyway runs automatically on both development and test profiles
