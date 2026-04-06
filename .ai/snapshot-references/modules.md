# Modules

| Module | Aggregates | Value Objects | Events | Exceptions | Repositories | Use Cases | Controllers |
|--------|-----------|---------------|--------|------------|-------------|-----------|------------|
| activity | ActivityDate | ActivityDateId, StreakInfo | ActivityRecordedEvent | — | ActivityDateRepository | RecordActivity, GetActivityDates, GetActivityCalendar, GetStreak | RecordActivity, GetActivityDates, GetStreak |
| auth | AuthUser | AuthUserId, RefreshToken, PasswordResetToken, GoogleVerifiedUser | — | 8 (Auth, InvalidCredentials, EmailAlreadyExists, GoogleAuth, AccountUsesGoogle, TooManyResetAttempts, InvalidResetToken, InvalidRefreshToken) | AuthUser, RefreshToken, PasswordResetToken | Register, Login, GetCurrentUser, ChangePassword, ForgotPassword, ResetPassword, DeleteAccount, RefreshToken, Logout, GoogleLogin | 10 controllers |
| gamification | Achievement, UserAchievement | AchievementId, UserAchievementId, XpLevel | AchievementUnlockedEvent | — | Achievement, UserAchievement | GrantXp, CheckAndUnlockAchievements, GetAllAchievements, GetUserAchievements, GetXpLevel | 5 controllers |
| home | — | — | — | — | — | GetHome | GetHome |
| immerse | ImmerseContent, ImmerseExercise, ImmerseSubmission | ImmerseExerciseId, ImmerseSubmissionId, ImmerseContentId, VocabularyItem, ContentType, ExerciseType, ImmerseContentStatus, ImmerseContentSizing | ImmerseExerciseAnsweredEvent | 6 (Immerse, ExerciseNotFound, ContentNotFound, AiException, ContentNotProcessed, ContentAccessDenied) | ImmerseExercise, ImmerseSubmission, ImmerseContent | GetHistory, GetContent, GetVocabulary, GetExercises, GenerateContent, GetSuggestedContent, SubmitExerciseAnswer, SubmitContent, ProcessContentAsync | 8 controllers |
| review | ReviewItem, ReviewResult | ReviewItemId, ReviewResultId, ReviewSourceType, ReviewStats | ReviewCompletedEvent | 2 (Review, ReviewItemNotFound) | ReviewItem, ReviewResult | GetReviewStats, GetReviewQueue, SubmitReviewResult, CreateReviewItemFromImmerse, CreateReviewItemFromTalk | 3 controllers |
| talk | TalkConversation, TalkScenario | TalkConversationId, TalkScenarioId, TalkMessageId, TalkMessage, TalkLevel, TalkStatus, TalkEvaluation, TalkCorrection, TalkStats, LevelProfile, LevelProfiles | TalkConversationCompletedEvent | 5 (Talk, MaxConversationsExceeded, ConversationNotFound, AiException, ConversationAlreadyEnded) | TalkConversation, TalkScenario | SendMessage, GetConversationSummary, GetTalkStats, EndConversation, StartConversation, ListScenarios | 7 controllers |
| user | UserProfile | UserProfileId | UserProfileCreatedEvent, XpGrantedEvent | 4 (UserProfile, UserProfileNotFound, InvalidXpAmount, ProfileOwnership) | UserProfile | CreateUserProfile, DeleteUserProfile, AddXp, GetUserProfile | 4 controllers |

## Totals

- **Modules**: 8 (+ shared)
- **Use Cases**: 46
- **Controllers**: 41
- **Domain Events**: 7
- **Repositories**: 14

## Event Flow (Cross-Module)

| Event | Source | Subscribers |
|-------|--------|-------------|
| ImmerseExerciseAnsweredEvent | immerse | review (ImmerseAnsweredReviewListener), activity (ImmerseSubmittedActivityListener), gamification (ImmerseAnsweredGamificationListener) |
| TalkConversationCompletedEvent | talk | review (TalkCompletedReviewListener), activity (TalkCompletedActivityListener), gamification (TalkCompletedGamificationListener) |
| ReviewCompletedEvent | review | activity (ReviewCompletedActivityListener), gamification (ReviewCompletedGamificationListener) |
