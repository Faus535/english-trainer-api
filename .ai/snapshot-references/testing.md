# Testing Snapshot

## Patterns
- **Object Mothers (15)**: ActivityDateMother, AchievementMother, AuthUserMother, ConversationMother, LearningPathMother, LearningUnitMother, LevelTestResultMother, ModuleProgressMother, PhraseMother, SessionMother, SpacedRepetitionItemMother, UserAchievementMother, UserProfileMother, VocabEntryMother, VocabMasteryMother
- **In-Memory Repositories (20)**: InMemoryActivityDateRepository, InMemoryAuthUserRepository, InMemoryAchievementRepository, InMemoryConversationRepository, InMemoryLearningPathRepository, InMemoryLearningUnitRepository, InMemoryLevelTestResultRepository, InMemoryMiniGameScoreRepository, InMemoryMiniTestResultRepository, InMemoryModuleProgressRepository, InMemoryPhraseRepository, InMemoryRefreshTokenRepository, InMemorySessionRepository, InMemorySpacedRepetitionRepository, InMemoryTestQuestionRepository, InMemoryTestQuestionHistoryRepository, InMemoryUserAchievementRepository, InMemoryUserProfileRepository, InMemoryVocabMasteryRepository, InMemoryVocabRepository
- **Test Infrastructure**: TestcontainersConfiguration (PostgreSQL), IntegrationTestBase (@SpringBootTest + Testcontainers)

## Test Classes

### Integration Tests (13)

| Test Class | Type | Module |
|------------|------|--------|
| EnglishTrainerApiApplicationTests | context load | shared |
| HealthCheckIntegrationTest | HTTP health | shared |
| SecurityStatusCodeTest | auth status codes | auth |
| AuthIntegrationTest | register/login flow | auth |
| GoogleAuthIntegrationTest | Google OAuth | auth |
| VocabIntegrationTest | vocab endpoints | vocabulary |
| ActivityDateRepositoryIT | persistence | activity |
| ModuleProgressRepositoryIT | persistence | moduleprogress |
| SessionRepositoryIT | persistence | session |
| SpacedRepetitionRepositoryIT | persistence | spacedrepetition |
| UserProfileRepositoryIT | persistence | user |
| InvalidUuidPathVariableIT | global error handling | shared |
| TestcontainersConfiguration | test setup | shared |

### Unit Tests (~72)

| Test Class | Tests | Module |
|------------|-------|--------|
| RecordActivityServiceTest | activity recording | activity |
| GetStreakServiceTest | streak calculation | activity |
| SubmitLevelTestServiceTest | level test submission | assessment |
| SubmitMiniTestServiceTest | mini test submission | assessment |
| RegisterUserServiceTest | user registration | auth |
| LoginUserServiceTest | user login | auth |
| GoogleLoginServiceTest | Google OAuth | auth |
| GoogleTokenVerifierTest | token verification | auth |
| StartConversationServiceTest | conversation start | conversation |
| SendMessageServiceTest | message sending | conversation |
| EndConversationServiceTest | conversation end | conversation |
| GetTodayChallengeServiceTest | daily challenge | dailychallenge |
| GenerateExercisesServiceTest | exercise generation | exercise |
| GrantXpServiceTest | XP granting | gamification |
| CheckAchievementsServiceTest | achievement unlock | gamification |
| GenerateLearningPathServiceTest | path generation | learningpath |
| AdvanceUnitServiceTest | unit advancement | learningpath |
| MasteryCalculatorTest | mastery scoring | learningpath |
| GetFillGapDataServiceTest | fill gap minigame | minigame |
| GetWordMatchDataServiceTest | word match minigame | minigame |
| GetUnscrambleDataServiceTest | unscramble minigame | minigame |
| SaveGameResultsServiceTest | game results | minigame |
| CompleteUnitServiceTest | unit completion | moduleprogress |
| GenerateSessionServiceTest | session generation | session |
| AdvanceBlockServiceTest | block advancement | session |
| CompleteSessionServiceTest | session completion | session |
| SessionGeneratorTest | exercise distribution | session |
| CompleteReviewServiceTest | SRS review | spacedrepetition |
| AddXpServiceTest | XP addition | user |
| CreateUserProfileServiceTest | profile creation | user |
| GetVocabByLevelServiceTest | vocab filtering | vocabulary |
| SubmitWritingServiceTest | writing submission | writing |
| +(~40 more across all modules) | | |

**Total: ~72 unit + ~13 integration = ~122 test classes**
