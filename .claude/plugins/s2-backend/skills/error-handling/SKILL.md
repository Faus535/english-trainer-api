---
name: error-handling
description: This skill should be used when the user asks about "error handling", "exception flow", "ControllerAdvice", "domain exceptions", "ApiError", "exception hierarchy", or "map exceptions to HTTP". Defines the complete error flow from domain to HTTP response.
---

# Skill: Error Handling

Defines the complete error flow: domain → application → controller → HTTP response.

## When to use

- When designing the exception hierarchy for a module
- When implementing ControllerAdvice
- When deciding how to propagate or catch exceptions

## Key rules

### Exception hierarchy

1. `DomainException` extends `Exception` (checked) — base exception per aggregate
2. Specific exceptions extend the base: `UserNotFoundException extends UserException`
3. Located in `domain/error/`

### Flow

1. Domain throws a checked exception
2. Use Case declares `throws {AggregateException}` — **does NOT catch it**
3. Controller method declares `throws {AggregateException}`
4. `ControllerAdvice` is the **only** place that maps exceptions to HTTP status

### ApiError — no confidential data in responses

- Error responses MUST NOT expose: entity IDs, internal names, database details, stack traces, or any
  application-internal information
- `ApiError` contains only a stable **error code** (e.g., `USER_NOT_FOUND`) and a **generic user-facing message**
- Full exception details (IDs, context, cause) go to the **log at ERROR level**, never in the response body

### Standard HTTP mappings

| Exception type           | HTTP status              |
|--------------------------|--------------------------|
| `NotFoundException`      | 404 Not Found            |
| `AlreadyExistsException` | 409 Conflict             |
| `ValidationException`    | 422 Unprocessable Entity |

## References

- [ControllerAdvice](references/controller-advice.md)

## Cross-references

- **s2-backend:domain-design** — Exception hierarchy and domain exceptions
- **s2-backend:api-design** — Controller structure and ControllerAdvice placement
- **s2-backend:logging** — Logging exception details without leaking them to responses
