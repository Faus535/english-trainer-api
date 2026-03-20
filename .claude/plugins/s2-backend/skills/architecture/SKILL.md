---
name: architecture
description: This skill should be used when the user asks about "package structure", "module organization", "where to place a class", "technology stack", "project dependencies", or "create a new module". Defines the package-per-aggregate structure and base technology decisions.
---

# Skill: Architecture

Defines the base project structure and fundamental technology decisions.

## When to use

- When creating a new module or aggregate
- When deciding where to place a new class
- When reviewing the project's package structure
- When configuring dependencies or technologies

## Key rules

### Package structure

1. **Package-per-aggregate**: Each aggregate groups all its code (domain, application, infrastructure) under a single
   root package. DO NOT use package-by-layer.
2. **Layers within each aggregate**: Each aggregate has `domain/`, `application/`, `infrastructure/` sub-packages.
3. **Shared module**: Cross-cutting components (AggregateRoot, EventPublisher, base exceptions) live in a `shared/`
   module.

### Technology stack

1. **Java**: Latest LTS version available (21+)
2. **Spring Boot**: Latest stable version
3. **Build tool**: Gradle or Maven
4. **Data access**: Spring Data JDBC or Spring Data JPA
5. **HTTP client**: Spring `RestClient` (not RestTemplate)
6. **Migrations**: Flyway
7. **Testing**: JUnit 5, Mockito, Testcontainers, DataFaker

### Module communication

1. **Events only**: Modules communicate exclusively via domain events (`ApplicationEvent`). Never import `application/`
   or `infrastructure/` classes from another module.
2. **`shared/` (s2-commons)**: Shared types (`Page<T>`, `Criteria`, `AggregateRoot`, etc.) come from
   `com.s2grupo.commons`. The `shared/` folder hosts this library until it is a stable import.
3. **Anti-Corruption Layer**: External integrations use adapters in `infrastructure/`. Never let external models leak
   into the domain.

## References

- [Package per Aggregate](references/package-per-aggregate.md)
- [Technology Stack](references/technology-stack.md)
