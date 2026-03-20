---
name: init-project
description: This skill should be used when the user asks to "initialize a project", "scaffold a project", "start a new project", or "create project from scratch". Defines how to set up a Java backend project with Spring Boot, Gradle, base DDD structure, and shared module.
---

# Skill: Init Project

Initializes a complete Java project with the base structure for backend development.

## Parameters

When the user asks to initialize a project, gather:

| Parameter    | Description               | Example             |
|--------------|---------------------------|---------------------|
| project-name | Project name (kebab-case) | `user-service`      |
| base-package | Java base package         | `com.s2grupo.users` |

## Generated structure

```
<project-name>/
├── build.gradle
├── settings.gradle
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── <base-package>/
│   │   │       ├── Application.java
│   │   │       └── shared/
│   │   │           ├── package-info.java
│   │   │           ├── abstracts/
│   │   │           │   └── AggregateRoot.java
│   │   │           ├── events/
│   │   │           │   ├── EventPublisher.java
│   │   │           │   └── SpringEventPublisher.java
│   │   │           └── exceptions/
│   │   │               ├── NotFoundException.java
│   │   │               └── AlreadyExistsException.java
│   │   └── resources/
│   │       ├── application.yml
│   │       └── db/
│   │           └── migration/
│   │               └── .gitkeep
│   └── test/
│       ├── java/
│       │   └── <base-package>/
│       │       └── ApplicationTests.java
│       └── resources/
│           └── application-test.yml
├── docker-compose.yml
└── .gitignore
```

## Generated components

### Build configuration

- **Java**: Latest LTS available with toolchain
- **Spring Boot**: Latest stable version
- **Spring Data JDBC** for persistence
- **Flyway** for migrations
- **PostgreSQL** driver
- **Testcontainers** for integration tests
- **DataFaker** for test data generation

### Shared module

- **AggregateRoot<T>**: Base class for aggregates with domain event support
- **EventPublisher**: Interface for publishing domain events
- **SpringEventPublisher**: Implementation with Spring ApplicationEventPublisher
- **NotFoundException**: Base checked exception for entities not found
- **AlreadyExistsException**: Base checked exception for duplicates

### Infrastructure

- **docker-compose.yml**: PostgreSQL configured for development
- **application.yml**: Spring configuration with datasource and Flyway
- **application-test.yml**: Test configuration with Testcontainers

## Process

1. Ask for project name and base package if not provided
2. Create directory structure
3. Generate `build.gradle` with dependencies
4. Generate `settings.gradle` with project name
5. Generate `libs.versions.toml` with default version catalog
6. Generate shared module classes
7. Generate `Application.java` and `ApplicationTests.java`
8. Generate configuration files (application.yml, docker-compose.yml)
9. Generate `.gitignore`

## Output example

```
Initializing project 'user-service' with package 'com.s2grupo.users'...

Creating project 'user-service'...

✓ Directory structure created
✓ build.gradle configured
✓ Shared module generated (AggregateRoot, EventPublisher, Exceptions)
✓ Docker Compose configured (PostgreSQL)
✓ Flyway configured
✓ Base tests configured (Testcontainers)

Next steps:
  1. cd user-service
  2. docker-compose up -d
  3. ./gradlew test
  4. /s2-create-plan
```

## Validations

- Project directory must not already exist
- Project name must be kebab-case
- Base package must follow Java conventions

## Notes

- The shared module does not include `@UseCase` because it is specific to the Modulith style. It is generated when that
  style is used.
- Base exceptions are checked (extend `Exception`, not `RuntimeException`)
- Flyway migrations go in `src/main/resources/db/migration/`

## References

- [Build Configuration Template](references/build-configuration.md)
- [Shared Module Template](references/shared-module.md)
- [Infrastructure Configuration Template](references/infrastructure-config.md)
