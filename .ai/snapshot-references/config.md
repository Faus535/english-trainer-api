# Config Snapshot

## Security
- JWT Auth: HS256, 60min access token, 7-day refresh token
- Rate Limiting: RateLimitingFilter on auth endpoints
- Profile Ownership: @RequireProfileOwnership annotation + ProfileOwnershipAspect
- Google OAuth: google-api-client 2.7.2
- CORS: CorsConfig (configurable origins)
- Method Security: @EnableMethodSecurity

## Shared Module
- AggregateRoot<T> base class for domain aggregates
- DomainEvent marker
- GlobalControllerAdvice: handles NotFoundException, AlreadyExistsException, InvalidValueException, ProfileOwnershipException, OptimisticLocking
- @UseCase annotation (extends @Service)
- PageResponse utility for paginated responses
- AnthropicHealthIndicator for AI health checks

## Dependencies

| Dependency | Version |
|------------|---------|
| Spring Boot | 3.5.3 |
| Java | 25 |
| Spring Data JPA | (Boot managed) |
| Spring Security | (Boot managed) |
| Flyway | (Boot managed) |
| PostgreSQL | (Boot managed) |
| JJWT | 0.12.6 |
| Google API Client | 2.7.2 |
| springdoc-openapi | 2.8.4 |
| Testcontainers | 1.20.4 |
| Spring WebFlux | (Boot managed, for SSE streaming) |
| Spring Mail | (Boot managed) |

## Application Properties
- server.port: 8081
- Anthropic AI: claude-haiku-4-5-20251001 model
- Flyway: enabled, repair-on-migrate
- Dev: PostgreSQL localhost:45432
- Prod: Railway environment variables (PGHOST, JWT_SECRET, CORS_ALLOWED_ORIGINS, GOOGLE_CLIENT_ID)
