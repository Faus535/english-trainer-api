# Testing Snapshot

## Summary
- **Total test classes**: 87 unit + 12 integration = 99 total
- **Object Mothers**: 15
- **In-Memory Repositories**: 19
- **Test stubs**: 1 (StubAiTutorPort)

## Object Mothers

| Mother | Module |
|--------|--------|
| ActivityDateMother | activity |
| AuthUserMother | auth |
| ConversationMother | conversation |
| LearningPathMother | learningpath |
| LearningUnitMother | learningpath |
| LevelTestResultMother | assessment |
| ModuleProgressMother | moduleprogress |
| PhraseMother | phrase |
| SessionMother | session |
| SpacedRepetitionItemMother | spacedrepetition |
| AchievementMother | gamification |
| UserAchievementMother | gamification |
| UserProfileMother | user |
| VocabEntryMother | vocabulary |
| VocabMasteryMother | vocabulary |

## In-Memory Repositories (19)

InMemoryActivityDateRepository, InMemoryAuthUserRepository, InMemoryAchievementRepository, InMemoryConversationRepository, InMemoryLearningPathRepository, InMemoryLearningUnitRepository, InMemoryLevelTestResultRepository, InMemoryMiniGameScoreRepository, InMemoryMiniTestResultRepository, InMemoryModuleProgressRepository, InMemoryPhraseRepository, InMemoryRefreshTokenRepository, InMemorySessionRepository, InMemorySpacedRepetitionRepository, InMemoryTestQuestionRepository, InMemoryTestQuestionHistoryRepository, InMemoryUserProfileRepository, InMemoryVocabMasteryRepository, InMemoryVocabRepository

## Integration Tests (12)

| Test Class | Module | Type |
|------------|--------|------|
| AuthIntegrationTest | auth | @SpringBootTest |
| GoogleAuthIntegrationTest | auth | @SpringBootTest |
| VocabIntegrationTest | vocabulary | @SpringBootTest |
| HealthCheckIntegrationTest | shared | @SpringBootTest |
| SecurityStatusCodeTest | shared | @SpringBootTest |
| UserProfileRepositoryIT | user | @DataJdbcTest |
| ActivityDateRepositoryIT | activity | @DataJdbcTest |
| SpacedRepetitionRepositoryIT | spacedrepetition | @DataJdbcTest |
| ModuleProgressRepositoryIT | moduleprogress | @DataJdbcTest |
| SessionRepositoryIT | session | @DataJdbcTest |
| EnglishTrainerApiApplicationTests | root | @SpringBootTest |
| IntegrationTestBase | shared | base class |

## Cross-Module Event Listeners (7)

| Listener | Listens To | Module Flow |
|----------|-----------|-------------|
| WordLearnedListener | WordLearnedEvent | spacedrepetition <- vocabulary |
| SrsGraduationListener | ReviewCompletedEvent | vocabulary <- spacedrepetition |
| ConversationCompletedEventListener | ConversationCompletedEvent | user <- conversation |
| LevelTestCompletedListener | LevelTestCompletedEvent | learningpath <- assessment |
| ConversationCompletedExerciseListener | ConversationCompletedEvent | exercise <- conversation |
| VocabularyFeedbackEventListener | VocabularyFeedbackEvent | spacedrepetition <- conversation |
| GrammarErrorEventListener | GrammarFeedbackEvent | errorpattern <- conversation |
