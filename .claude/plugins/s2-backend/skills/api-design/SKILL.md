---
name: api-design
description: This skill should be used when the user asks to "create an endpoint", "new controller", "REST API", "request validation", "ControllerAdvice", or "HTTP error handling". Defines patterns for creating new REST APIs. For reviewing existing APIs, use api-review instead.
---

# Skill: API Design

Defines patterns for the REST layer: controllers, request validation, and exception-to-HTTP mapping.

## When to use

- When creating REST endpoints
- When defining request validations
- When implementing HTTP error handling with ControllerAdvice
- When designing controller structure

**Note:** For reviewing or auditing existing REST controllers, use the **api-review** skill instead.

## Key rules

### One Controller Per Action

1. **One controller per HTTP operation** (no god controllers)
2. Naming: `{Action}Controller.java` (e.g., `CreateUserController`, `GetUserProfileController`)
3. **Package-private** visibility (no `public`)
4. Single `handle()` method that delegates to the Use Case
5. Use Case injected via constructor (no `@Autowired`)

### Request Validation

1. Request DTOs as **internal records** of the controller
2. Validation with Jakarta Validation (`@NotNull`, `@NotBlank`, `@Size`, etc.)
3. `@Valid` on the request parameter
4. Validation happens in controller; Use Cases assume valid input

### Controller Advice

1. One `{Aggregate}ControllerAdvice` per module
2. **Package-private** visibility
3. Maps domain exceptions to HTTP status codes
4. Internal `ApiError` record for consistent responses

**Note:** Domain exceptions are defined in the domain layer. Consult the **domain-design** skill for exception hierarchy
and rules.

### PUT/PATCH semantics

- `PUT`: replaces the full resource; all fields required in the body; responds `200 OK` with updated resource
- `PATCH`: partial update; nullable fields in the Request DTO; responds `200 OK` with updated resource

### Pagination controller pattern (s2-commons)

- Controller receives loose params (`page`, `size`, `sortBy`, `orderBy`) and builds `Criteria`
- `Page<T>`, `Criteria`, `Filter`, `Filters`, `FilterOperator` come from `com.s2grupo.commons`
- **Do NOT** use Spring's `Pageable` as a controller parameter
- See [Pagination Controller](references/pagination-controller.md)

## References

- MUST [One Controller Per Action](references/one-controller-per-action.md)
- [Request Validation](references/request-validation.md)
- [Controller Advice](references/controller-advice.md)
- [Checked Exceptions](references/checked-exceptions.md)
- [Pagination Controller](references/pagination-controller.md)