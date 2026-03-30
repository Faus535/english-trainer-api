# Config Snapshot

## Security
- JWT Auth: HS256, 60min access token, 7-day refresh token
- Rate Limiting: RateLimitingFilter on auth endpoints (10 req/60s)
- Profile Ownership: AOP aspect with @RequireProfileOwnership annotation
- CORS: configurable origins (default localhost:4200), credentials enabled
- Public endpoints: /api/auth/*, /actuator/health, /swagger-ui/**, /v3/api-docs/**

## Error Handling
- GlobalControllerAdvice: NotFoundException(404), AlreadyExistsException(409), InvalidValueException(400), MethodArgumentNotValidException(422), ProfileOwnershipException(403), ObjectOptimisticLockingFailureException(409), MethodArgumentTypeMismatchException(400)
- SessionControllerAdvice: session-domain exceptions

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
| Anthropic (AI) | claude-haiku-4-5-20251001 |

## Profiles
- **dev**: localhost:45432 PostgreSQL, SQL logging, Google OAuth test client
- **prod**: Railway env vars (PGHOST, PGPORT, etc.), JWT_SECRET, GOOGLE_CLIENT_ID, CORS_ALLOWED_ORIGINS

## Cross-Module Events (7 listeners)

| Listener | Event | Effect |
|----------|-------|--------|
| WordLearnedListener | WordLearnedEvent | Schedules SRS review |
| SrsGraduationListener | ReviewCompletedEvent | Graduates vocab to MASTERED |
| ConversationCompletedEventListener | ConversationCompletedEvent | Awards XP (REQUIRES_NEW) |
| VocabularyFeedbackEventListener | VocabularyFeedbackEvent | Adds vocab to SRS queue |
| ConversationCompletedExerciseListener | ConversationCompletedEvent | Generates exercises |
| GrammarErrorEventListener | GrammarFeedbackEvent | Records error patterns |
| LevelTestCompletedListener | LevelTestCompletedEvent | Generates learning path |
