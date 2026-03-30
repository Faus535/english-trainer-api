# Testing Snapshot

## Summary
- **Total test classes**: ~83
- **Unit tests**: ~78 classes
- **Integration tests**: ~5 classes
- **Object Mothers**: 19
- **In-Memory Repos**: 24+

## Integration Tests
| Test Class | What it tests |
|------------|---------------|
| AuthIntegrationTest | Auth flow (login, register) |
| GoogleAuthIntegrationTest | Google OAuth flow |
| HealthCheckIntegrationTest | Actuator health endpoint |
| SecurityStatusCodeTest | HTTP status codes for security |
| VocabIntegrationTest | Vocabulary CRUD |

## Object Mothers (19)
AchievementMother, UserAchievementMother, PhonemeMother, PhraseMother, PhonemeDailyAssignmentMother, UserPhonemeProgressMother, VocabMasteryMother, VocabEntryMother, SessionMother, UserProfileMother, LearningPathMother, LearningUnitMother, AuthUserMother, ActivityDateMother, ModuleProgressMother, SpacedRepetitionItemMother, ConversationMother, LevelTestResultMother, MiniTestResultMother

## In-Memory Repositories (24+)
InMemoryActivityDateRepository, InMemoryAuthUserRepository, InMemoryAchievementRepository, InMemoryConversationRepository, InMemoryLearningPathRepository, InMemoryLearningUnitRepository, InMemoryLevelTestResultRepository, InMemoryMiniGameScoreRepository, InMemoryMiniTestResultRepository, InMemoryModuleProgressRepository, InMemoryPhraseRepository, InMemoryRefreshTokenRepository, InMemorySessionRepository, InMemorySpacedRepetitionRepository, InMemoryTestQuestionRepository, InMemoryTestQuestionHistoryRepository, InMemoryUserAchievementRepository, InMemoryUserProfileRepository, InMemoryVocabMasteryRepository, InMemoryVocabRepository, InMemoryPhonemeRepository, InMemoryPhonemePracticePhraseRepository, InMemoryUserPhonemeProgressRepository, InMemoryPhonemeDailyAssignmentRepository

## Patterns
- JUnit 5, `@BeforeEach`, constructor injection
- Checked exceptions in `throws` clauses
- Testcontainers for integration tests (PostgreSQL)
- 3 pre-existing GoogleTokenVerifierTest failures (environment-specific)
