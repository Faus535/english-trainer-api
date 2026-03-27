# Endpoints Snapshot

## Public Endpoints (9)

| Method | Path | Controller | Module |
|--------|------|------------|--------|
| POST | /api/auth/login | LoginController | auth |
| POST | /api/auth/register | RegisterController | auth |
| POST | /api/auth/google | GoogleLoginController | auth |
| POST | /api/auth/refresh | RefreshTokenController | auth |
| POST | /api/auth/logout | LogoutController | auth |
| POST | /api/auth/forgot-password | ForgotPasswordController | auth |
| POST | /api/auth/reset-password | ResetPasswordController | auth |
| GET | /api/assessments/mini-test | GetMiniTestQuestionsController | assessment |
| GET | /api/achievements | GetAllAchievementsController | gamification |

## Authenticated Endpoints (~105)

### User & Profile (11)
| Method | Path | Controller | Module |
|--------|------|------------|--------|
| GET | /api/auth/me | GetCurrentUserController | auth |
| PUT | /api/auth/change-password | ChangePasswordController | auth |
| POST | /api/profiles | CreateUserProfileController | user |
| GET | /api/profiles/{id} | GetUserProfileController | user |
| DELETE | /api/profiles/{id} | DeleteUserProfileController | user |
| PUT | /api/profiles/{id}/levels | SetAllLevelsController | user |
| PUT | /api/profiles/{id}/test-completed | MarkTestCompletedController | user |
| PUT | /api/profiles/{id}/reset-test | ResetTestController | user |
| PUT | /api/profiles/{id}/modules/{module}/level | UpdateModuleLevelController | user |
| POST | /api/profiles/{id}/sessions | RecordSessionController | user |
| POST | /api/profiles/{id}/xp | AddXpController | user |

### Session (7)
| Method | Path | Controller | Module |
|--------|------|------------|--------|
| POST | /api/profiles/{userId}/sessions/generate | GenerateSessionController | session |
| GET | /api/profiles/{userId}/sessions/current | GetCurrentSessionController | session |
| GET | /api/profiles/{userId}/sessions | GetSessionHistoryController | session |
| PUT | /api/profiles/{userId}/sessions/{sessionId}/complete | CompleteSessionController | session |
| POST | /api/profiles/{profileId}/sessions/{sessionId}/exercises/{exerciseIndex}/result | RecordExerciseResultController | session |
| PUT | /api/profiles/{profileId}/sessions/{sessionId}/blocks/{blockIndex}/advance | AdvanceBlockController | session |
| GET | /api/profiles/{profileId}/sessions/{sessionId}/blocks/{blockIndex}/exercises | GetBlockExercisesController | session |

### Content & Curriculum (13)
| Method | Path | Controller | Module |
|--------|------|------------|--------|
| GET | /api/vocab | GetAllVocabController | vocabulary |
| GET | /api/vocab/level/{level} | GetVocabByLevelController | vocabulary |
| GET | /api/vocab/random | GetRandomVocabController | vocabulary |
| GET | /api/vocab/search | SearchVocabController | vocabulary |
| POST | /api/vocab | CreateVocabEntryController | vocabulary |
| GET | /api/profiles/{userId}/vocabulary/unlearned | GetUnlearnedVocabController | vocabulary |
| GET | /api/profiles/{userId}/vocabulary/progress | GetVocabProgressController | vocabulary |
| GET | /api/phrases | GetPhrasesByLevelController | phrase |
| GET | /api/phrases/random | GetRandomPhrasesController | phrase |
| GET | /api/curriculum/modules | GetModuleDefinitionsController | curriculum |
| GET | /api/curriculum/modules/{name} | GetModuleByNameController | curriculum |
| GET | /api/curriculum/plan | GetCurriculumPlanController | curriculum |
| GET | /api/curriculum/integrators | GetIntegratorsController | curriculum |

### Minigames (6)
| Method | Path | Controller | Module |
|--------|------|------------|--------|
| GET | /api/minigames/word-match | GetWordMatchDataController | minigame |
| GET | /api/minigames/unscramble | GetUnscrambleDataController | minigame |
| GET | /api/minigames/fill-gap | GetFillGapDataController | minigame |
| GET | /api/profiles/{userId}/minigames/scores | GetMiniGameScoresController | minigame |
| POST | /api/profiles/{userId}/minigames/scores | SaveMiniGameScoreController | minigame |
| POST | /api/profiles/{userId}/minigames/results | SaveGameResultsController | minigame |

### Assessment & Analytics (7)
| Method | Path | Controller | Module |
|--------|------|------------|--------|
| GET | /api/profiles/{userId}/assessments/level-test/questions | GetLevelTestQuestionsController | assessment |
| POST | /api/profiles/{userId}/assessments/level-test | SubmitLevelTestController | assessment |
| POST | /api/profiles/{userId}/assessments/mini-test | SubmitMiniTestController | assessment |
| GET | /api/profiles/{userId}/assessments/history | GetTestHistoryController | assessment |
| GET | /api/profiles/{userId}/analytics/summary | GetAnalyticsSummaryController | analytics |
| GET | /api/profiles/{userId}/analytics/progress | GetProgressHistoryController | analytics |
| GET | /api/profiles/{userId}/analytics/activity-heatmap | GetActivityHeatmapController | analytics |

### Conversation & Tutor (14)
| Method | Path | Controller | Module |
|--------|------|------------|--------|
| POST | /api/conversations | StartConversationController | conversation |
| GET | /api/conversations | ListConversationsController | conversation |
| GET | /api/conversations/{id} | GetConversationController | conversation |
| POST | /api/conversations/{id}/messages | SendMessageController | conversation |
| POST | /api/conversations/{id}/messages/stream | StreamMessageController | conversation |
| PUT | /api/conversations/{id}/end | EndConversationController | conversation |
| GET | /api/conversations/stats | GetConversationStatsController | conversation |
| GET | /api/conversations/suggested-topics | SuggestTopicsController | conversation |
| GET | /api/conversations/suggested-goals | SuggestGoalsController | conversation |
| GET | /api/conversations/{id}/exercises | GetConversationExercisesController | exercise |
| GET | /api/profiles/{userId}/tutor/errors | GetUserErrorsController | tutorerror |
| GET | /api/profiles/{userId}/tutor/errors/trend | GetErrorTrendController | tutorerror |
| POST | /api/profiles/{userId}/tutor/errors | RecordTutorErrorController | tutorerror |
| POST | /api/profiles/{userId}/tutor/errors/{errorId}/exercise | GenerateErrorExerciseController | tutorerror |

### Activity & Gamification (12)
| Method | Path | Controller | Module |
|--------|------|------------|--------|
| GET | /api/profiles/{userId}/activity | GetActivityDatesController | activity |
| GET | /api/profiles/{userId}/streak | GetStreakController | activity |
| POST | /api/profiles/{userId}/activity | RecordActivityController | activity |
| GET | /api/profiles/{userId}/achievements | GetUserAchievementsController | gamification |
| POST | /api/profiles/{userId}/achievements/check | CheckAchievementsController | gamification |
| GET | /api/profiles/{userId}/xp-level | GetXpLevelController | gamification |
| POST | /api/profiles/{userId}/xp | GrantXpController | gamification |
| GET | /api/profiles/{userId}/modules | GetAllModuleProgressController | moduleprogress |
| GET | /api/profiles/{userId}/modules/{module}/levels/{level} | GetModuleProgressController | moduleprogress |
| POST | /api/profiles/{userId}/modules/{module}/levels/{level} | InitModuleProgressController | moduleprogress |
| PUT | /api/profiles/{userId}/modules/{module}/levels/{level}/units/{unit} | CompleteUnitController | moduleprogress |
| GET | /api/profiles/{userId}/modules/{module}/levels/{level}/level-up | CheckLevelUpController | moduleprogress |

### Spaced Repetition & Learning Path (7)
| Method | Path | Controller | Module |
|--------|------|------------|--------|
| GET | /api/profiles/{userId}/reviews/due | GetDueReviewsController | spacedrepetition |
| GET | /api/profiles/{userId}/reviews/stats | GetReviewStatsController | spacedrepetition |
| POST | /api/profiles/{userId}/reviews | AddToReviewQueueController | spacedrepetition |
| PUT | /api/profiles/{userId}/reviews/{itemId}/complete | CompleteReviewController | spacedrepetition |
| POST | /api/profiles/{profileId}/learning-path/generate | GenerateLearningPathController | learningpath |
| GET | /api/profiles/{profileId}/learning-path | GetLearningPathController | learningpath |
| GET | /api/profiles/{profileId}/learning-status | GetLearningStatusController | learningpath |

### Reading, Writing, Pronunciation (15)
| Method | Path | Controller | Module |
|--------|------|------------|--------|
| GET | /api/reading/passages | GetPassagesByLevelController | reading |
| GET | /api/reading/passages/{id} | GetPassageByIdController | reading |
| GET | /api/reading/passages/{id}/questions | GetPassageQuestionsController | reading |
| POST | /api/reading/passages/{textId}/answers | SubmitPassageAnswersController | reading |
| POST | /api/profiles/{userId}/reading/submit | SubmitReadingAnswersController | reading |
| GET | /api/writing/exercises | GetWritingExercisesController | writing |
| POST | /api/writing/submissions | SubmitWritingSubmissionController | writing |
| POST | /api/profiles/{userId}/writing/submit | SubmitWritingController | writing |
| GET | /api/profiles/{userId}/writing/history | GetWritingHistoryController | writing |
| GET | /api/pronunciation/minimal-pairs | GetMinimalPairsController | minimalpair |
| POST | /api/profiles/{userId}/pronunciation/minimal-pairs/results | RecordMinimalPairResultController | minimalpair |
| GET | /api/profiles/{userId}/pronunciation/minimal-pairs/stats | GetMinimalPairStatsController | minimalpair |
| GET | /api/profiles/{userId}/pronunciation/errors | GetFrequentErrorsController | pronunciation |
| GET | /api/profiles/{userId}/pronunciation/problematic-sounds | GetProblematicSoundsController | pronunciation |
| POST | /api/profiles/{userId}/pronunciation/errors | RecordPronunciationErrorController | pronunciation |

### Other (8)
| Method | Path | Controller | Module |
|--------|------|------------|--------|
| GET | /api/profiles/{userId}/error-patterns | GetErrorPatternsController | errorpattern |
| POST | /api/vocabulary/{wordId}/context | GenerateContextController | vocabularycontext |
| GET | /api/challenges/today | GetTodayChallengeController | dailychallenge |
| GET | /api/profiles/{userId}/challenges/today | GetUserChallengeProgressController | dailychallenge |
| PUT | /api/profiles/{userId}/challenges/today/progress | UpdateChallengeProgressController | dailychallenge |
| POST | /api/notifications/subscribe | SubscribePushController | notification |
| GET | /api/notifications/preferences | GetNotificationPreferencesController | notification |
| PUT | /api/notifications/preferences | UpdateNotificationPreferencesController | notification |

### Admin (5)
| Method | Path | Controller | Module |
|--------|------|------------|--------|
| GET | /api/admin/vocab | AdminVocabController | admin |
| POST | /api/admin/vocab | AdminVocabController | admin |
| GET | /api/admin/phrases | AdminPhraseController | admin |
| POST | /api/admin/reading/passages | AdminReadingController | admin |
| POST | /api/admin/writing/exercises | AdminWritingController | admin |

**Total: 9 public + ~105 authenticated = ~114 endpoints**
