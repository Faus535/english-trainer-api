# Testing

- **Framework**: JUnit 5 + Spring Boot Test
- **Test separation**: Tags (`integration` excluded by default)
- **Total test classes**: 73 (55 unit + 4 integration + 10 Object Mothers + 9 listener tests - some overlap)
- **Integration tests**: 4 (@SpringBootTest)
- **Object Mothers**: 10

## Object Mothers

| Class | Module |
|-------|--------|
| AchievementMother | gamification |
| UserAchievementMother | gamification |
| ReviewItemMother | review |
| UserProfileMother | user |
| AuthUserMother | auth |
| ActivityDateMother | activity |
| ImmerseContentMother | immerse |
| ImmerseExerciseMother | immerse |
| TalkScenarioMother | talk |
| TalkConversationMother | talk |

## Integration Tests

| Class | Focus |
|-------|-------|
| AuthIntegrationTest | Full auth flow with Testcontainers |
| GoogleAuthIntegrationTest | Google OAuth flow |
| HealthCheckIntegrationTest | Actuator health endpoint |
| SecurityStatusCodeTest | Security response codes |

## Unit Tests by Module

| Module | Count | Tests |
|--------|-------|-------|
| activity | 6 | ActivityDateTest, GetActivityCalendarUseCaseTest, GetActivityDatesUseCaseTest, GetStreakUseCaseTest, RecordActivityUseCaseTest, StreakCalculatorTest |
| auth | 11 | AuthUserTest, ChangePasswordUseCaseTest, DeleteAccountUseCaseTest, ForgotPasswordUseCaseTest, GetCurrentUserUseCaseTest, GoogleLoginUseCaseTest, GoogleTokenVerifierTest, LoginUserUseCaseTest, LogoutUserUseCaseTest, RefreshTokenUseCaseTest, RegisterUserUseCaseTest |
| gamification | 7 | CheckAndUnlockAchievementsUseCaseTest, GetAllAchievementsUseCaseTest, GetUserAchievementsUseCaseTest, GetXpLevelUseCaseTest, GrantXpUseCaseTest, UserAchievementTest, XpLevelTest |
| home | 1 | GetHomeUseCaseTest |
| immerse | 11 | GenerateImmerseContentUseCaseTest, GetImmerseContentUseCaseTest, GetImmerseExercisesUseCaseTest, GetImmerseHistoryUseCaseTest, GetImmerseVocabularyUseCaseTest, GetSuggestedImmerseContentUseCaseTest, ImmerseContentSizingTest, ImmerseContentTest, ProcessImmerseContentAsyncServiceTest, SubmitExerciseAnswerUseCaseTest, SubmitImmerseContentUseCaseTest |
| review | 6 | CreateReviewItemFromImmerseUseCaseTest, CreateReviewItemFromTalkUseCaseTest, GetReviewQueueUseCaseTest, GetReviewStatsUseCaseTest, SubmitReviewResultUseCaseTest, ReviewItemTest |
| talk | 4 | EndTalkConversationUseCaseTest, SendTalkMessageUseCaseTest, StartTalkConversationUseCaseTest, TalkConversationTest |
| user | 5 | AddXpUseCaseTest, CreateUserProfileUseCaseTest, DeleteUserProfileUseCaseTest, GetUserProfileUseCaseTest, UserProfileTest |

## Patterns

- One test class per use case
- In-memory repository stubs for unit tests
- Object Mother pattern for test fixtures
- Failing stub ports for error-path testing (e.g., `FailingStubImmerseAiPort`)
- Constructor injection in tests (package-private use case constructors)
