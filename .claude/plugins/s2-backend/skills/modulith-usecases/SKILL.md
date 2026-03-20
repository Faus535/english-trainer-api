---
name: modulith-usecases
description: This skill should be used when the user asks to "create a use case", "application layer", "business operation", "CRUD operations", "@UseCase annotation", or "execute method". Defines the Modulith architectural style with one Use Case per business action. Complements shared skills (architecture, domain-design, persistence, api-design, testing).
---

# Skill: Modulith Use Cases

Defines the Modulith architectural style where the application layer is organized with **one Use Case per business
action**.

## When to use

- When implementing the application layer of a module
- When creating CRUD operations, queries, or state changes
- When defining orchestration between domain and ports
- When the chosen architectural style is **Modulith**

## Key rules

### One Use Case per action

1. **Naming**: `{Action}UseCase.java` (e.g., `CreateUserUseCase.java`)
2. **Location**: `application/` package
3. **Single method**: public `execute()`
4. **Annotation**: `@UseCase`
5. **Assumption**: Input parameters are already validated (validation in controller)

### Application layer structure

```
application/
├── CreateUserUseCase.java
├── GetUserByIdUseCase.java
├── FindAllUsersUseCase.java
├── UpdateUserUseCase.java
├── DeleteUserUseCase.java
└── UserDto.java
```

### CRUD patterns

- **Create**: Validate non-existence -> Create aggregate -> Save -> Publish events
- **Read**: `@Transactional(readOnly=true)` -> Find -> Throw NotFoundException if not found
- **Update**: Find -> Call domain update() -> Save new instance -> Publish events
- **Delete**: Find -> Call domain delete() -> Delete -> Publish events

### Principles

1. **Orchestration, not business logic**: Logic lives in the domain
2. **Single responsibility**: One Use Case = one operation
3. **Always publish events** after mutations
4. **Use domain exceptions** (checked exceptions)
5. **Constructor injection** without `@Autowired`

### Pagination pattern (s2-commons)

- The Use Case receives a `Criteria` object (from `com.s2grupo.commons.data.domain.Criteria`)
- The controller constructs `Criteria` with `page`, `size`, `sortBy`, `orderBy`, and `Filters`
- The Use Case returns `Page<{Entity}Dto>` (from `com.s2grupo.commons.shared.Page`)
- `@Transactional(readOnly=true)` is mandatory on query Use Cases

### DTO rules

- The response DTO (`{Entity}Dto`) is defined by the **Use Case** in the `application/` package
- The controller maps `{Entity}Dto → {Entity}Response` using a static `fromDto()` method in the response record
- The Use Case has NO knowledge of HTTP response format (does not know what `Response` is)

## Cross-references

This style complements the shared skills:

- **s2-backend:architecture** - Package structure
- **s2-backend:domain-design** - Aggregate Roots, Value Objects, Events
- **s2-backend:persistence** - Repository implementations, Entities
- **s2-backend:api-design** - Controllers, validation, ControllerAdvice
- **s2-backend:testing** - Object Mothers, In-Memory Repositories

## References

- [One Use Case Per Action](references/one-use-case-per-action.md)
