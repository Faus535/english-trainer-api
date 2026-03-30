# Config Snapshot

## Security
- JWT Auth: HS256, 60min access token, 7-day refresh token
- Rate Limiting: 10 req/60s on auth endpoints
- `@RequireProfileOwnership` AOP aspect for user-scoped endpoints
- Public endpoints: auth, phonetics catalog, mini-test, daily challenge

## Dependencies
| Dependency | Version |
|------------|---------|
| Spring Boot | 3.5.3 |
| Java | 25 |
| JJWT | 0.12.6 |
| SpringDoc OpenAPI | 2.8.4 |
| Google API Client | 2.7.2 |
| Testcontainers | 1.20.4 |

## Event Listeners (7)
| Subscriber | Event | Effect |
|-----------|-------|--------|
| WordLearnedListener | WordLearnedEvent | Adds word to SRS |
| SrsGraduationListener | ReviewCompletedEvent | Graduates mastered vocab |
| LevelTestCompletedListener | LevelTestCompletedEvent | Generates learning path |
| ConversationCompletedEventListener | ConversationCompletedEvent | Grants XP |
| ConversationCompletedExerciseListener | ConversationCompletedEvent | Generates exercises |
| GrammarErrorEventListener | GrammarFeedbackEvent | Records error patterns |
| VocabularyFeedbackEventListener | VocabularyFeedbackEvent | Adds vocab to SRS |
