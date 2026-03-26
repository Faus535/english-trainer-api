# Testing Snapshot

## Patterns
- **Object Mothers**: 15 test data builders across modules
- **In-Memory Repositories**: Used in unit tests (InMemorySessionRepository, etc.)
- **Integration Tests**: @SpringBootTest + Testcontainers (PostgreSQL)
- **Unit Tests**: Plain JUnit 5 with mocks and Object Mothers
- **Test task**: `./gradlew test` (unit), `./gradlew integrationTest` (integration, requires Docker)

## Object Mothers (15)

| Mother | Module |
|--------|--------|
| AchievementMother | gamification |
| UserAchievementMother | gamification |
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
| UserProfileMother | user |
| VocabEntryMother | vocabulary |
| VocabMasteryMother | vocabulary |

## Test Classes (117 total)

### Integration Tests (12)
| Test Class | Module |
|------------|--------|
| EnglishTrainerApiApplicationTests | root |
| HealthCheckIntegrationTest | root |
| ActivityDateRepositoryIT | activity |
| AuthIntegrationTest | auth |
| GoogleAuthIntegrationTest | auth |
| SecurityStatusCodeTest | auth |
| ModuleProgressRepositoryIT | moduleprogress |
| SessionRepositoryIT | session |
| SpacedRepetitionRepositoryIT | spacedrepetition |
| UserProfileRepositoryIT | user |
| VocabIntegrationTest | vocabulary |

### Unit Tests (86+)
- Domain tests: SessionTest, SessionGeneratorTest, ConversationTest, UserProfileTest, etc.
- Application tests: AdvanceBlockUseCaseTest, GetBlockExercisesUseCaseTest, CompleteSessionUseCaseTest, etc.
- All use cases have corresponding test classes

## Cross-Module Event Listeners (7)

| Listener | Event | From Module | To Module |
|----------|-------|-------------|-----------|
| WordLearnedListener | WordLearnedEvent | vocabulary | spacedrepetition |
| SrsGraduationListener | ReviewCompletedEvent | spacedrepetition | vocabulary |
| LevelTestCompletedListener | LevelTestCompletedEvent | assessment | learningpath |
| ConversationCompletedEventListener | ConversationCompletedEvent | conversation | user (XP) |
| VocabularyFeedbackEventListener | VocabularyFeedbackEvent | conversation | spacedrepetition |
| GrammarErrorEventListener | VocabularyFeedbackEvent | conversation | errorpattern |
| ConversationCompletedExerciseListener | ConversationCompletedEvent | conversation | exercise |
