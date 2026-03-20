---
name: s2-review
description: Validates that code follows the architecture, naming, code quality, testing, and security patterns of S2 Grupo.
model: sonnet
---

# Command: /s2-review

Validates that code follows the patterns defined in the s2-backend skills.

## Syntax

```bash
/s2-review                    # Review entire project
/s2-review <module>           # Review a specific module
/s2-review --architecture     # Architecture only
/s2-review --naming           # Naming conventions only
/s2-review --code-quality     # Code quality only
/s2-review --api-review       # API review only
/s2-review --testing          # Testing only
/s2-review --security         # Security only
```

## Validation Checks

### 1. Architecture Checks (see skill `s2-backend:architecture`)

#### Package structure
- Package-per-aggregate (NOT package-by-layer)
- Each module has domain/, application/, infrastructure/
- Shared module exists with base classes

#### Domain Layer (see skill `s2-backend:domain-design`)
- Does not import from `application` or `infrastructure`
- Can only import from `shared` and Java standard
- NO framework annotations (@Repository, @Service, @RestController)
- Repository interfaces are pure (no extends CrudRepository)
- Aggregates extend AggregateRoot<T>
- Value Objects are records with validation
- Events are records with @DomainEvent
- Exceptions extend Exception (checked, NOT RuntimeException)

#### Application Layer (see skill `s2-backend:modulith-usecases`)
- Only imports from `domain`
- Does NOT import from `infrastructure`
- Use Cases have @UseCase
- Use Cases have a single execute() method
- Constructor injection (no @Autowired)

#### Infrastructure Layer (see skills `s2-backend:persistence` and `s2-backend:api-design`)
- Repository implementations have @Repository and are package-private
- Entities implement Persistable<UUID>
- Controllers have @RestController and are package-private
- One controller per HTTP action
- Controllers inject Use Cases (not Repositories)
- ControllerAdvice per module

### 2. Naming Checks

| Component            | Expected pattern                 | Example                    |
|----------------------|----------------------------------|----------------------------|
| Module               | lowercase singular               | `user`                     |
| Aggregate            | PascalCase                       | `User`                     |
| Value Object         | `{Entity}{Field}`                | `UserName`                 |
| Event                | `{Entity}{Action}Event` (past)   | `UserCreatedEvent`         |
| Use Case             | `{Action}UseCase`                | `CreateUserUseCase`        |
| Controller           | `{Action}{Http}Controller`       | `CreateUserPostController` |
| Base Exception       | `{Entity}Exception`              | `UserException`            |
| Exception            | `{Entity}{Type}Exception`        | `UserNotFoundException`    |
| Repository interface | `{Entity}Repository`             | `UserRepository`           |
| Repository impl      | `{Entity}JdbcRepository`         | `UserJdbcRepository`       |
| Entity               | `{Entity}Entity`                 | `UserEntity`               |
| Mother               | `{Entity}Mother`                 | `UserMother`               |

### 3. Code Quality Checks

#### Visibility
- Domain: Aggregates public, Value Objects public, Repositories public
- Application: Use Cases package-private or public, DTOs public
- Infrastructure: Repositories package-private, Controllers package-private

#### Immutability
- Aggregates: private final fields
- Value Objects: records (immutable by nature)
- Events: records
- DTOs: records

#### Checked Exceptions (see skill `s2-backend:domain-design`)
- Domain exceptions extend Exception (NOT RuntimeException)
- ControllerAdvice maps exceptions to HTTP status codes

### 4. API Review Checks (see skill `s2-backend:api-review`)

- URLs in plural and kebab-case, no verbs in paths
- Parameters in camelCase and consistent across endpoints
- Correct HTTP semantics (methods, status codes)
- Complete Jakarta validations on Request DTOs

### 5. Testing Checks (see skill `s2-backend:testing`)

- Each Use Case has its unit test
- Unit tests use mocks or in-memory repositories
- Each module has Object Mothers with only the necessary methods
- Each Repository has an integration test with Testcontainers

### 6. Security Checks (see skill `s2-backend:security-audit`)

#### Spring Security config
- Security chains have correct `@Order` and a catch-all chain exists
- Filter pipeline in correct order: ApiKeyAuth -> BearerToken -> PostAuthProvisioning -> TenantContext
- Filters always call `chain.doFilter()` (unless intentional short-circuit)
- JWT converter uses `TenantContext.bypass()` and clears in finally

#### Authorization & tenant
- `@PreAuthorize` on all non-public controllers
- No security annotations in domain/application
- `@EnableMethodSecurity` present
- `TenantContextFilter` after `PostAuthProvisioningFilter`
- Repositories use `requireContext()` (not `getContext()`)

#### General hygiene
- Parameterized SQL, parameterized logging, no user input in SpEL
- No secrets in logs, responses, or config
- CORS without wildcard `*` in production
- Actuator restricted, sessions stateless for API chains

## Severity Levels

### ERROR (Critical)
Must be fixed before commit and before merge to develop.
- Inverted dependency (Domain imports Application/Infrastructure)
- Exception extends RuntimeException instead of Exception
- Missing Repository interface in Domain
- Public Repository in Infrastructure
- Chain without catch-all (requests fall through to Spring defaults)
- Controller without `@PreAuthorize` in authenticated chain
- Filter pipeline in wrong order
- `TenantContext` not cleared in finally

### WARNING (Recommended)
Should be fixed. Creates technical debt.
- Incorrect naming
- Missing test
- Constructor with @Autowired
- Object Mother with unused methods
- CORS with wildcard `*` without profile restriction
- `@WithRlsBypass` in HTTP-reachable code
- Sensitive actuator endpoints exposed
- Log injection (concatenation instead of `{}`)

### PASS (Correct)
All good.

## Quality Score

```
Score = (earned points / total points) x 100

Ranges:
- 90-100: Excellent
- 80-89: Good (minor improvements)
- 70-79: Acceptable (needs corrections)
- <70: Insufficient (significant work needed)
```

## Example output

```
/s2-review user

Reviewing module 'user'...

ARCHITECTURE
   Package-per-aggregate correct
   Pure domain (no forbidden dependencies)
   Checked exceptions (extends Exception)
   One controller per action

NAMING
   Aggregate: User.java
   Value Objects: UserName, UserEmail
   Events: UserCreatedEvent (past tense)
   Controllers: CreateUserPostController

CODE QUALITY
   Visibility correct
   Immutability correct
   Constructor injection

TESTING
   Unit tests present
   Object Mother applied correctly
   Integration test with Testcontainers

SECURITY
   Security chains with correct @Order and catch-all
   Filter pipeline in correct order
   @PreAuthorize on all controllers
   TenantContext cleared in finally
   No secrets in logs or responses

SCORE: 100/100
```

## References

- Skill: [skills/architecture/SKILL.md](../skills/architecture/SKILL.md)
- Skill: [skills/domain-design/SKILL.md](../skills/domain-design/SKILL.md)
- Skill: [skills/persistence/SKILL.md](../skills/persistence/SKILL.md)
- Skill: [skills/api-design/SKILL.md](../skills/api-design/SKILL.md)
- Skill: [skills/testing/SKILL.md](../skills/testing/SKILL.md)
- Skill: [skills/modulith-usecases/SKILL.md](../skills/modulith-usecases/SKILL.md)
- Skill: [skills/security-audit/SKILL.md](../skills/security-audit/SKILL.md)
