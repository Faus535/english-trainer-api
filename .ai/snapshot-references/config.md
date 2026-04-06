# Configuration

## Stack

| Component | Version |
|-----------|---------|
| Spring Boot | 3.5.3 |
| Java | 25 (toolchain) |
| Build | Gradle |
| Database | PostgreSQL + Flyway |
| Auth | JWT (jjwt 0.12.6) + Google OAuth (google-api-client 2.7.2) |
| AI | Anthropic Claude (claude-haiku-4-5-20251001) |
| Docs | SpringDoc OpenAPI 2.8.4 |
| Testing | Testcontainers 1.20.4 |

## Shared Module (16 classes)

| Category | Classes |
|----------|---------|
| Annotations | @UseCase |
| Base | AggregateRoot |
| Domain errors | NotFoundException, AlreadyExistsException, InvalidValueException |
| Events | DomainEvent |
| Config | AsyncConfig, CorsConfig, SecurityConfig, OpenApiConfig |
| Error handling | GlobalControllerAdvice |
| Security | RequireProfileOwnership, RateLimitingFilter, ProfileOwnershipAspect |
| Infrastructure | PageResponse, AnthropicHealthIndicator |

## Key Properties

| Property | Dev | Prod |
|----------|-----|------|
| Server port | 8081 | ${PORT:8081} |
| DB host | localhost:45432 | ${PGHOST}:${PGPORT} |
| JWT expiry | 1h | configurable |
| JWT refresh | 7d | configurable |
| AI model | claude-haiku-4-5 | configurable |

## Event Listeners (8)

| Subscriber | Event | Module | Action |
|-----------|-------|--------|--------|
| ReviewCompletedGamificationListener | ReviewCompletedEvent | gamification | Grant 5 XP + 20 bonus |
| ImmerseAnsweredGamificationListener | ImmerseExerciseAnsweredEvent | gamification | Grant 10 XP |
| TalkCompletedGamificationListener | TalkConversationCompletedEvent | gamification | Grant 50 XP + 5/turn |
| ImmerseAnsweredReviewListener | ImmerseExerciseAnsweredEvent | review | Create review item |
| TalkCompletedReviewListener | TalkConversationCompletedEvent | review | Create review items |
| TalkCompletedActivityListener | TalkConversationCompletedEvent | activity | Record activity |
| ReviewCompletedActivityListener | ReviewCompletedEvent | activity | Record activity |
| ImmerseSubmittedActivityListener | ImmerseExerciseAnsweredEvent | activity | Record activity |
