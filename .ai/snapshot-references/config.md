# Config Snapshot

## Security
- JWT auth with stateless sessions (1h access, 7d refresh)
- BCrypt password encoding
- Rate limiting: 10 req/60s on auth endpoints
- Profile ownership via AOP aspect (`@RequireProfileOwnership`)
- Public endpoints: login, register, Google OAuth, refresh, forgot/reset password, logout

## Key Properties
- `server.port=8081`
- `spring.jpa.hibernate.ddl-auto=validate`
- `spring.jpa.open-in-view=false`
- `spring.flyway.enabled=true`, `repair-on-migrate=true`
- `anthropic.model=claude-haiku-4-5-20251001`, `max-tokens=300`

## Dependencies

| Dependency | Version |
|------------|---------|
| Spring Boot | 3.5.3 |
| Java | 25 |
| Spring Data JPA | (Boot managed) |
| Spring Security | (Boot managed) |
| Flyway | (Boot managed, PostgreSQL) |
| JJWT | 0.12.6 |
| SpringDoc OpenAPI | 2.8.4 |
| Google API Client | 2.7.2 |
| PostgreSQL Driver | (Boot managed) |
| Testcontainers | 1.20.4 |

## Cross-Module Events

| Subscriber | Event | Effect |
|-----------|-------|--------|
| GrammarErrorEventListener | GrammarFeedbackEvent | Classifies error patterns |
| LevelTestCompletedListener | LevelTestCompletedEvent | Generates learning path |
| ConversationCompletedExerciseListener | ConversationCompletedEvent | Generates exercises from errors |
| ConversationCompletedEventListener | ConversationCompletedEvent | Awards XP (50 base + 5/turn) |
| VocabularyFeedbackEventListener | VocabularyFeedbackEvent | Adds words to SRS queue |
| WordLearnedListener | WordLearnedEvent | Schedules SRS review |
| SrsGraduationListener | ReviewCompletedEvent | Marks vocab as MASTERED |
