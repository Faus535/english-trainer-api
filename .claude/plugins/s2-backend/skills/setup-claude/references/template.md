# CLAUDE.md Template

Generic template for generating the `CLAUDE.md` file. Replace all `{placeholders}` with actual values detected from the project.

## Placeholder rules

- `{project-name}` — from `settings.gradle` or `pom.xml`
- `{base-package}` — from `Application.java` package declaration
- `{stack}` — detected dependencies (e.g., "Spring Boot, Spring Data JDBC, Elasticsearch, Java, Gradle")
- `{persistence-line}` — one entry per persistence tech detected (e.g., "Spring Data JDBC (Persistable<UUID>), Elasticsearch")
- `{repo-naming-rows}` — one naming row per persistence tech (e.g., `{Entity}JdbcRepository`, `{Entity}ElasticRepository`)
- `{security-skills}` — include security skills only if security dependencies are detected
- `{security-section}` — include the Security architecture line only if security dependencies are detected
- `{style-line}` — architectural style description (see "Architectural styles" below)
- `{style-skill}` — skill reference for the chosen style
- `{style-naming-rows}` — naming rows specific to the style (e.g., Use Case naming for Modulith)
- `{style-principles}` — _(deprecated, principles moved to Critical Rules)_

## Common repository naming by technology

| Technology | Suffix | Example |
|---|---|---|
| Spring Data JDBC | `JdbcRepository` | `UserJdbcRepository` |
| Spring Data JPA | `JpaRepository` | `UserJpaRepository` |
| Elasticsearch | `ElasticRepository` | `UserElasticRepository` |
| Redis | `RedisRepository` | `UserRedisRepository` |
| MongoDB | `MongoRepository` | `UserMongoRepository` |

## Architectural styles

### Modulith (default)
- `{style-line}`: `Modulith — one Use Case per business action (@UseCase + single execute() method)`
- `{style-skill}`: `- **s2-backend:modulith-usecases** — Use Cases with @UseCase, execute() method`
- `{style-naming-rows}`:
  ```
  | Use Case | `{Action}UseCase` | `CreateUserUseCase` |
  ```
- `{style-principles}`: _(not used — principles are now in Critical Rules)_

### CQRS (coming soon)
### Event-Driven (coming soon)

---

```markdown
# {project-name}

Java backend project. Base package: `{base-package}`. Stack: {stack}.

## Architecture

- **Style**: {style-line}
- **Structure**: Package-per-aggregate (domain/application/infrastructure per module)
- **Domain**: Immutable Aggregate Roots, Value Objects as records, Domain Events
- **Exceptions**: Checked exceptions (extends Exception, NEVER RuntimeException)
- **Persistence**: {persistence-line}
- **API**: One controller per HTTP action, package-private, Jakarta Validation
- **Testing**: Object Mothers, In-Memory Repositories, Testcontainers for integration
{security-section}

## Critical Rules

- **NO SENSITIVE DATA**: NEVER log, expose in responses, or include in error messages: passwords, tokens, personal IDs (DNI), emails in sensitive contexts, database details, stack traces. This is the #1 rule of the project.
- **`.claudeignore`**: Ensure `.env`, `*credentials*`, `*secret*`, `application-local.yml`, and private keys are listed in `.claudeignore` so they are never read or referenced.
- **Domain purity**: ZERO framework imports in domain — no Spring, no Jakarta
- **Module isolation**: Modules communicate via domain events OR exposed contracts (public Use Case interfaces). Never import `application/` or `infrastructure/` from another module.
- **Aggregate references**: By ID only — never hold a reference to another aggregate instance
- **Constructor injection**: No `@Autowired` anywhere — constructor injection only

## Commands

| Command | Description |
|---|---|
| `/s2-create-plan` | Create implementation plan with vertical phases |
| `/s2-execute-plan` | Execute current plan phase (compile → test → review → commit) |
| `/s2-review` | Validate architecture, naming, and code quality |
| `/s2-generate-api-docs` | Generate API docs for frontend team |

## Skills Reference

Consult these skills when generating or modifying code:

- **s2-backend:architecture** — Package-per-aggregate structure, module organization
- **s2-backend:domain-design** — Aggregates, Value Objects, Events, Repository interfaces
- **s2-backend:persistence** — Entities, Repository implementations, Flyway migrations
- **s2-backend:api-design** — Controllers, request/response DTOs, Jakarta Validation
- **s2-backend:error-handling** — Exception hierarchy, ControllerAdvice, domain-to-HTTP mapping
- **s2-backend:testing** — Object Mothers, In-Memory Repositories, Testcontainers
- **s2-backend:logging** — SLF4J conventions, log levels, Spring Actuator
{style-skill}
{security-skills}

## Naming Conventions

| Component | Pattern | Example |
|---|---|---|
| Module | lowercase singular | `user` |
| Aggregate | PascalCase | `User` |
| Value Object | `{Entity}{Field}` | `UserName` |
| Event | `{Entity}{Action}Event` (past) | `UserCreatedEvent` |
{style-naming-rows}
| Controller | `{Action}{Http}Controller` | `CreateUserPostController` |
| Exception | `{Entity}{Type}Exception` | `UserNotFoundException` |
{repo-naming-rows}
| Entity | `{Entity}Entity` | `UserEntity` |
| Domain Service | `{Action}DomainService` | `TransferFundsDomainService` |
| Object Mother | `{Entity}Mother` | `UserMother` |
| Test method | `should{Result}When{Condition}` | `shouldCreateUserWhenValidRequest` |
| Migration | `V{x.y.z}__{module}_{desc}.sql` | `V1.0.0__user_init.sql` |
```

## Conditional sections

### {security-section} — only if security dependencies detected
```
- **Security**: Spring Security with filter pipeline, @PreAuthorize, tenant context
```

### {security-skills} — only if security dependencies detected
```
- **s2-backend:security** — Authentication, authorization, filter pipeline
- **s2-backend:security-audit** — Security chain validation, tenant context, RLS
```

### {repo-naming-rows} — one row per detected persistence technology
Example with JDBC + Elasticsearch:
```
| Repository impl | `{Entity}JdbcRepository` | `UserJdbcRepository` |
| Search repository | `{Entity}ElasticRepository` | `UserElasticRepository` |
```
