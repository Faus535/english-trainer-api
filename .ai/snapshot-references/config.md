# Config Snapshot

## Security
- JWT Auth: HS256, 60min access token, 7-day refresh token
- Rate Limiting: 10 requests/60s on auth endpoints (RateLimitingFilter)
- Profile Ownership: @RequireProfileOwnership AOP aspect
- CORS: Configurable origins (default localhost:4200), GET/POST/PUT/DELETE/OPTIONS
- Public endpoints: login, register, google, refresh, forgot/reset-password, logout, mini-test questions, minimal-pairs

## Dependencies

| Dependency | Version |
|------------|---------|
| Spring Boot | 3.5.3 |
| Java | 25 |
| Spring Data JPA | (managed by Boot) |
| Spring Security | (managed by Boot) |
| PostgreSQL | (managed by Boot) |
| Flyway | (managed by Boot) |
| JJWT | 0.12.6 |
| Google API Client | 2.7.2 |
| springdoc-openapi | 2.8.4 |
| Testcontainers | (managed by Boot) |

## Key Properties
- `server.port`: 8081
- `spring.jpa.hibernate.ddl-auto`: validate
- `anthropic.model`: claude-haiku-4-5-20251001 (max-tokens: 300)
- `anthropic.writing-model`: claude-haiku-4-5-20251001 (max-tokens: 600)

## Shared Module
- AggregateRoot base class
- DomainEvent interface
- @UseCase annotation (wraps @Service)
- GlobalControllerAdvice: NotFoundException(404), AlreadyExistsException(409), MethodArgumentNotValidException(422), InvalidValueException(400), ProfileOwnershipException(403), ObjectOptimisticLockingFailureException(409)
- PageResponse generic pagination record
- AnthropicHealthIndicator
