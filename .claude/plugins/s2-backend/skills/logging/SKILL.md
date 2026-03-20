---
name: logging
description: This skill should be used when the user asks about "logging", "logger", "LoggerFactory", "log levels", "actuator", or "health check". Defines logging conventions with SLF4J and Spring Actuator.
---

# Skill: Logging

Defines logging conventions, log levels, and traceability patterns.

## When to use

- When adding logging to a class
- When setting up Spring Actuator health checks

## Key rules

### Logger declaration
Static logger per class — no Lombok:
```java
private static final Logger log = LoggerFactory.getLogger(ClassName.class);
```

### Log levels
- **INFO**: Use Cases for mutation operations (create, update, delete)
- **DEBUG**: Repositories and infrastructure adapters
- **ERROR**: Unexpected exceptions in ControllerAdvice
- **NEVER** log personal data: passwords, tokens, DNI, emails in sensitive contexts

### Health check
- Use Spring Actuator: expose `/actuator/health` at minimum
- Add `management.endpoints.web.exposure.include=health,info` in `application.properties`

## Cross-references

- **s2-backend:architecture** — Infrastructure package placement
- **s2-backend:api-design** — Controller/filter layer
