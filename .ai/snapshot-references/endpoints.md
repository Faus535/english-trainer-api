# API Endpoints

## Auth (10 endpoints)

| Method | Path | Controller | Use Case |
|--------|------|-----------|----------|
| POST | /api/auth/register | RegisterController | RegisterUserUseCase |
| POST | /api/auth/login | LoginController | LoginUserUseCase |
| POST | /api/auth/google | GoogleLoginController | GoogleLoginUseCase |
| POST | /api/auth/refresh | RefreshTokenController | RefreshTokenUseCase |
| POST | /api/auth/logout | LogoutController | LogoutUserUseCase |
| POST | /api/auth/forgot-password | ForgotPasswordController | ForgotPasswordUseCase |
| POST | /api/auth/reset-password | ResetPasswordController | ResetPasswordUseCase |
| GET | /api/auth/me | GetCurrentUserController | GetCurrentUserUseCase |
| PUT | /api/auth/change-password | ChangePasswordController | ChangePasswordUseCase |
| DELETE | /api/auth/account | DeleteAccountController | DeleteAccountUseCase |

## User Profile (4 endpoints)

| Method | Path | Controller | Use Case |
|--------|------|-----------|----------|
| POST | /api/profiles | CreateUserProfileController | CreateUserProfileUseCase |
| GET | /api/profiles/{id} | GetUserProfileController | GetUserProfileUseCase |
| POST | /api/profiles/{id}/xp | AddXpController | AddXpUseCase |
| DELETE | /api/profiles/{id} | DeleteUserProfileController | DeleteUserProfileUseCase |

## Activity (3 endpoints)

| Method | Path | Controller | Use Case |
|--------|------|-----------|----------|
| POST | /api/profiles/{userId}/activity | RecordActivityController | RecordActivityUseCase |
| GET | /api/profiles/{userId}/activity | GetActivityDatesController | GetActivityDatesUseCase |
| GET | /api/profiles/{userId}/streak | GetStreakController | GetStreakUseCase |

## Gamification (5 endpoints)

| Method | Path | Controller | Use Case |
|--------|------|-----------|----------|
| POST | /api/profiles/{userId}/xp | GrantXpController | GrantXpUseCase |
| POST | /api/profiles/{userId}/achievements/check | CheckAchievementsController | CheckAndUnlockAchievementsUseCase |
| GET | /api/profiles/{userId}/achievements | GetUserAchievementsController | GetUserAchievementsUseCase |
| GET | /api/profiles/{userId}/xp-level | GetXpLevelController | GetXpLevelUseCase |
| GET | /api/achievements | GetAllAchievementsController | GetAllAchievementsUseCase |

## Home (1 endpoint)

| Method | Path | Controller | Use Case |
|--------|------|-----------|----------|
| GET | /api/profiles/{userId}/home | GetHomeController | GetHomeUseCase |

## Immerse (8 endpoints)

| Method | Path | Controller | Use Case |
|--------|------|-----------|----------|
| POST | /api/immerse/content | SubmitImmerseContentController | SubmitImmerseContentUseCase |
| POST | /api/immerse/generate | GenerateImmerseContentController | GenerateImmerseContentUseCase |
| GET | /api/immerse/content/{id} | GetImmerseContentController | GetImmerseContentUseCase |
| GET | /api/immerse/content/{id}/exercises | GetImmerseExercisesController | GetImmerseExercisesUseCase |
| GET | /api/immerse/content/{id}/vocabulary | GetImmerseVocabularyController | GetImmerseVocabularyUseCase |
| GET | /api/immerse/content/suggested | GetSuggestedImmerseContentController | GetSuggestedImmerseContentUseCase |
| GET | /api/profiles/{userId}/immerse/history | GetImmerseHistoryController | GetImmerseHistoryUseCase |
| POST | /api/immerse/content/{contentId}/exercises/{exerciseId}/submit | SubmitExerciseAnswerController | SubmitExerciseAnswerUseCase |

## Review (3 endpoints)

| Method | Path | Controller | Use Case |
|--------|------|-----------|----------|
| GET | /api/profiles/{userId}/review/stats | GetReviewStatsController | GetReviewStatsUseCase |
| GET | /api/profiles/{userId}/review/queue | GetReviewQueueController | GetReviewQueueUseCase |
| POST | /api/profiles/{userId}/review/items/{itemId}/result | SubmitReviewResultController | SubmitReviewResultUseCase |

## Talk (7 endpoints)

| Method | Path | Controller | Use Case |
|--------|------|-----------|----------|
| POST | /api/talk/conversations | StartTalkConversationController | StartTalkConversationUseCase |
| POST | /api/talk/conversations/{id}/messages | SendTalkMessageController | SendTalkMessageUseCase |
| POST | /api/talk/conversations/{id}/speech | SubmitTalkSpeechController | SendTalkMessageUseCase |
| POST | /api/talk/conversations/{id}/end | EndTalkConversationController | EndTalkConversationUseCase |
| GET | /api/talk/conversations/{id}/summary | GetTalkConversationSummaryController | GetTalkConversationSummaryUseCase |
| GET | /api/profiles/{userId}/talk/stats | GetTalkStatsController | GetTalkStatsUseCase |
| GET | /api/talk/scenarios | ListTalkScenariosController | ListTalkScenariosUseCase |

## Totals

- **Total endpoints**: 41
- **GET**: 17 | **POST**: 19 | **PUT**: 1 | **DELETE**: 2
