# Testing Snapshot

## Patterns
- Unit tests with in-memory repositories (HashMap-based)
- Integration tests with Testcontainers PostgreSQL 16
- Object Mother pattern for test data creation
- `@Tag("integration")` excluded from `./gradlew test`, run via `./gradlew integrationTest`

## Counts
- **Unit test classes**: 63
- **Integration test classes**: 12
- **Object Mothers**: 18
- **In-Memory Repositories**: 15

## Integration Tests

| Test Class | Module |
|------------|--------|
| EnglishTrainerApiApplicationTests | shared |
| HealthCheckIntegrationTest | shared |
| AuthIntegrationTest | auth |
| GoogleAuthIntegrationTest | auth |
| SecurityStatusCodeTest | auth |
| VocabIntegrationTest | vocabulary |
| ActivityDateRepositoryIT | activity |
| ModuleProgressRepositoryIT | moduleprogress |
| SessionRepositoryIT | session |
| SpacedRepetitionRepositoryIT | spacedrepetition |
| UserProfileRepositoryIT | user |

## Object Mothers (18)
UserProfileMother, ActivityDateMother, LevelTestResultMother, AchievementMother, UserAchievementMother, SpacedRepetitionItemMother, ModuleProgressMother, AuthUserMother, ConversationMother, LearningPathMother, LearningUnitMother, VocabMasteryMother, SessionMother, VocabEntryMother, PhonemeMother, PhonemeDailyAssignmentMother, PhraseMother

## Pre-existing Failures
- 3 tests in GoogleTokenVerifierTest (auth) — Google OAuth token verification
