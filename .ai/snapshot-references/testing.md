# Testing Snapshot

## Patterns
- **Object Mothers**: 15 test data builders (ActivityDateMother, AuthUserMother, ConversationMother, LearningPathMother, LearningUnitMother, LevelTestResultMother, ModuleProgressMother, PhraseMother, SessionMother, SpacedRepetitionItemMother, UserAchievementMother, AchievementMother, UserProfileMother, VocabEntryMother, VocabMasteryMother)
- **In-Memory Repositories**: InMemoryVocabRepository, InMemoryVocabMasteryRepository, InMemoryAchievementRepository, InMemoryUserAchievementRepository, InMemoryMiniGameScoreRepository, InMemoryPhraseRepository, etc.
- **Integration Test Base**: IntegrationTestBase (abstract, @Tag("integration"), @SpringBootTest)
- **Constructor injection**: All tests use constructor injection, no @Autowired in tests

## Test Classes (121 total)

### Integration Tests (11)

| Test Class | Module | What it tests |
|------------|--------|---------------|
| VocabIntegrationTest | vocabulary | CRUD and query operations |
| HealthCheckIntegrationTest | shared | Actuator health endpoint |
| AuthIntegrationTest | auth | Login, register, token refresh |
| GoogleAuthIntegrationTest | auth | Google OAuth flow |
| SecurityStatusCodeTest | auth | 401/403 status codes |
| SessionRepositoryIT | session | Session persistence |
| ModuleProgressRepositoryIT | moduleprogress | Progress persistence |
| SpacedRepetitionRepositoryIT | spacedrepetition | SRS item persistence |
| ActivityDateRepositoryIT | activity | Activity date persistence |
| UserProfileRepositoryIT | user | User profile persistence |

### Unit Tests (77+ classes)

Key test classes by module:
- **session**: GenerateSessionUseCaseTest, CompleteSessionUseCaseTest, AdvanceBlockUseCaseTest, GetBlockExercisesUseCaseTest, RecordExerciseResultUseCaseTest, SessionResponseMapperTest
- **minigame**: SaveGameResultsUseCaseTest, GetWordMatchDataUseCaseTest, GetUnscrambleDataUseCaseTest, GetFillGapDataUseCaseTest
- **user**: CreateUserProfileUseCaseTest, GetUserProfileUseCaseTest, AddXpUseCaseTest, SetAllLevelsUseCaseTest
- **vocabulary**: GetVocabByLevelUseCaseTest, GetRandomVocabUseCaseTest, CreateVocabEntryUseCaseTest, GetUnlearnedVocabUseCaseTest
- **learningpath**: GenerateLearningPathUseCaseTest, GetNextContentUseCaseTest, AdvanceUnitUseCaseTest
- **auth**: RegisterUserUseCaseTest, LoginUserUseCaseTest, GoogleLoginUseCaseTest, GoogleTokenVerifierTest
- **conversation**: StartConversationUseCaseTest, SendMessageUseCaseTest, EndConversationUseCaseTest
- **gamification**: GrantXpUseCaseTest, CheckAndUnlockAchievementsUseCaseTest
- **spacedrepetition**: CompleteReviewUseCaseTest, AddToReviewQueueUseCaseTest, GetDueReviewsUseCaseTest

## Cross-Module Event Listeners (7)

| Subscriber | Event | Module |
|-----------|-------|--------|
| WordLearnedListener | WordLearnedEvent | vocabulary -> spacedrepetition |
| SrsGraduationListener | ReviewCompletedEvent | vocabulary |
| LevelTestCompletedListener | LevelTestCompletedEvent | learningpath |
| ConversationCompletedEventListener | ConversationCompletedEvent | conversation -> user |
| VocabularyFeedbackEventListener | VocabularyFeedbackEvent | conversation -> spacedrepetition |
| ConversationCompletedExerciseListener | ConversationCompletedEvent | exercise |
| GrammarErrorEventListener | GrammarFeedbackEvent | errorpattern |
