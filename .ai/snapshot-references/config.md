# Config Snapshot

## Stack
- **Framework**: Spring Boot 3.5.3, Java 25
- **Persistence**: Spring Data JPA, PostgreSQL, Flyway
- **Security**: JWT (JJWT 0.12.6), Google OAuth (google-api-client 2.7.2)
- **AI**: Anthropic Claude (claude-haiku-4-5-20251001)
- **API Docs**: springdoc-openapi 2.8.4
- **Testing**: JUnit 5, Mockito, Testcontainers 1.20.4

## Security
- JWT tokens: 60min access, 7-day refresh
- Google OAuth integration
- Rate limiting: 10 req/60s on login, register, forgot-password
- @RequireProfileOwnership for resource access control
- CORS: configurable origins (dev: localhost:4200)
- CSRF disabled, stateless sessions

## Configuration Classes
- `SecurityConfig` (@EnableWebSecurity, @EnableMethodSecurity) — JWT filter, rate limiting, CORS
- `CorsConfig` — allowed origins, methods, credentials
- `OpenApiConfig` — Swagger with Bearer JWT scheme

## Shared Module
- **Annotations**: @UseCase, @RequireProfileOwnership, @DomainEvent
- **Base classes**: AggregateRoot<T>, PageResponse
- **Exceptions**: NotFoundException, AlreadyExistsException, InvalidValueException
- **GlobalControllerAdvice**: maps to 400, 403, 404, 409, 422
- **Security**: RateLimitingFilter, ProfileOwnershipAspect
- **Health**: AnthropicHealthIndicator

## Key Properties
- `server.port`: 8081
- `spring.jpa.hibernate.ddl-auto`: validate
- `anthropic.model`: claude-haiku-4-5-20251001
- `anthropic.max-tokens`: 300 (writing: 600)

## Dependencies (build.gradle)

| Dependency | Version |
|------------|---------|
| Spring Boot | 3.5.3 |
| Java | 25 |
| JJWT | 0.12.6 |
| Google API Client | 2.7.2 |
| springdoc-openapi | 2.8.4 |
| Flyway | (managed) |
| PostgreSQL | (managed) |
| Testcontainers | 1.20.4 |
