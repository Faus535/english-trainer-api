# english-trainer-api

Backend API for English training. Stack: Spring Boot, Spring Data JDBC, Java, Gradle.

## Architecture

- **Style**: CQRS (Command/Query Responsibility Segregation)
- **Base package**: `com.s2grupo.carmen.englishtrainer`
- **Structure**: Package-per-aggregate (domain/application/infrastructure per module)
- **Domain**: Immutable Aggregate Roots, Value Objects as records, Domain Events with @DomainEvent
- **Exceptions**: Checked exceptions (extends Exception, never RuntimeException)
- **Persistence**: Spring Data JDBC with Persistable<UUID> Entities
- **API**: One controller per HTTP action, package-private, validation with Jakarta Validation
- **Testing**: Object Mothers, In-Memory Repositories, Testcontainers

## Reference Skills

IMPORTANT: Before generating code, ALWAYS consult the relevant skills in `.claude/plugins/s2-backend/skills/`. Each skill contains rules and references with code examples that MUST be followed.

| Skill | When to consult | Path |
|-------|----------------|------|
| architecture | Create module, place class, package structure | `.claude/plugins/s2-backend/skills/architecture/` |
| domain-design | Aggregates, Value Objects, Events, Repositories, Exceptions | `.claude/plugins/s2-backend/skills/domain-design/` |
| persistence | Persistable Entities, Repository implementations, Flyway | `.claude/plugins/s2-backend/skills/persistence/` |
| api-design | Controllers, validation, checked exceptions, ControllerAdvice | `.claude/plugins/s2-backend/skills/api-design/` |
| testing | Object Mothers, In-Memory Repos, constructor injection, tests | `.claude/plugins/s2-backend/skills/testing/` |
| modulith-usecases | Use Cases with @Service, execute() method | `.claude/plugins/s2-backend/skills/modulith-usecases/` |
| error-handling | Error handling, ControllerAdvice | `.claude/plugins/s2-backend/skills/error-handling/` |
| security | Security, tenant context, JWT, RLS | `.claude/plugins/s2-backend/skills/security/` |
| logging | Logging and traceability | `.claude/plugins/s2-backend/skills/logging/` |
| init-project | Initialize project, shared module, config | `.claude/plugins/s2-backend/skills/init-project/` |

## Coding Workflow

1. **Before writing code**: Read the `SKILL.md` and files in `references/` of the relevant skills
2. **Follow the rules**: Apply the conventions defined in the skills (naming, structure, patterns)
3. **Check the examples**: The `references/*.md` files contain concrete code examples
4. **Validate**: Use the review skills to verify the code follows the conventions
