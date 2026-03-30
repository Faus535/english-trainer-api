# Endpoints Snapshot

## Public Endpoints (~12)

| Method | Path | Controller | Module |
|--------|------|------------|--------|
| POST | /api/auth/register | RegisterController | auth |
| POST | /api/auth/login | LoginController | auth |
| POST | /api/auth/google | GoogleLoginController | auth |
| POST | /api/auth/refresh | RefreshTokenController | auth |
| POST | /api/auth/forgot-password | ForgotPasswordController | auth |
| POST | /api/auth/reset-password | ResetPasswordController | auth |
| POST | /api/auth/logout | LogoutController | auth |
| GET | /api/auth/me | GetCurrentUserController | auth |
| GET | /api/assessments/mini-test | GetMiniTestQuestionsController | assessment |
| GET | /api/phrases | GetPhrasesByLevelController | phrase |
| GET | /api/phrases/random | GetRandomPhrasesController | phrase |
| GET | /api/vocab | GetAllVocabController | vocabulary |

## Authenticated Endpoints (~101)

### Activity (3)
| Method | Path | Controller |
|--------|------|------------|
| GET | /api/profiles/{userId}/activity | GetActivityDatesController |
| GET | /api/profiles/{userId}/streak | GetStreakController |
| POST | /api/profiles/{userId}/activity | RecordActivityController |

### Admin (4)
| Method | Path | Controller |
|--------|------|------------|
| GET | /api/admin/phrases | AdminPhraseController |
| GET | /api/admin/vocab | AdminVocabController |
| POST | /api/admin/reading/passages | AdminReadingController |
| POST | /api/admin/writing/exercises | AdminWritingController |

### Analytics (3)
| Method | Path | Controller |
|--------|------|------------|
| GET | /api/profiles/{userId}/analytics/activity-heatmap | GetActivityHeatmapController |
| GET | /api/profiles/{userId}/analytics/summary | GetAnalyticsSummaryController |
| GET | /api/profiles/{userId}/analytics/progress | GetProgressHistoryController |

### Assessment (4)
| Method | Path | Controller |
|--------|------|------------|
| GET | /api/profiles/{userId}/assessments/level-test/questions | GetLevelTestQuestionsController |
| GET | /api/profiles/{userId}/assessments/history | GetTestHistoryController |
| POST | /api/profiles/{userId}/assessments/level-test | SubmitLevelTestController |
| POST | /api/profiles/{userId}/assessments/mini-test | SubmitMiniTestController |

### Conversation (9)
| Method | Path | Controller |
|--------|------|------------|
| POST | /api/conversations | StartConversationController |
| GET | /api/conversations | ListConversationsController |
| GET | /api/conversations/{id} | GetConversationController |
| POST | /api/conversations/{id}/messages | SendMessageController |
| POST | /api/conversations/{id}/messages/stream | StreamMessageController |
| PUT | /api/conversations/{id}/end | EndConversationController |
| GET | /api/conversations/stats | GetConversationStatsController |
| GET | /api/conversations/suggested-goals | SuggestGoalsController |
| GET | /api/conversations/suggested-topics | SuggestTopicsController |

### Curriculum (4)
| Method | Path | Controller |
|--------|------|------------|
| GET | /api/curriculum/plan | GetCurriculumPlanController |
| GET | /api/curriculum/modules | GetModuleDefinitionsController |
| GET | /api/curriculum/modules/{name} | GetModuleByNameController |
| GET | /api/curriculum/integrators | GetIntegratorsController |

### Daily Challenge (3)
| Method | Path | Controller |
|--------|------|------------|
| GET | /api/challenges/today | GetTodayChallengeController |
| GET | /api/profiles/{userId}/challenges/today | GetUserChallengeProgressController |
| PUT | /api/profiles/{userId}/challenges/today/progress | UpdateChallengeProgressController |

### Error Pattern (1)
| Method | Path | Controller |
|--------|------|------------|
| GET | /api/profiles/{userId}/error-patterns | GetErrorPatternsController |

### Exercise (1)
| Method | Path | Controller |
|--------|------|------------|
| GET | /api/conversations/{id}/exercises | GetConversationExercisesController |

### Gamification (5)
| Method | Path | Controller |
|--------|------|------------|
| GET | /api/achievements | GetAllAchievementsController |
| GET | /api/profiles/{userId}/achievements | GetUserAchievementsController |
| POST | /api/profiles/{userId}/achievements/check | CheckAchievementsController |
| GET | /api/profiles/{userId}/xp-level | GetXpLevelController |
| POST | /api/profiles/{userId}/xp | GrantXpController |

### Learning Path (3)
| Method | Path | Controller |
|--------|------|------------|
| GET | /api/profiles/{profileId}/learning-path | GetLearningPathController |
| GET | /api/profiles/{profileId}/learning-status | GetLearningStatusController |
| POST | /api/profiles/{profileId}/learning-path/generate | GenerateLearningPathController |

### Minigame (6)
| Method | Path | Controller |
|--------|------|------------|
| GET | /api/minigames/fill-gap | GetFillGapDataController |
| GET | /api/minigames/word-match | GetWordMatchDataController |
| GET | /api/minigames/unscramble | GetUnscrambleDataController |
| GET | /api/profiles/{userId}/minigames/scores | GetMiniGameScoresController |
| POST | /api/profiles/{userId}/minigames/scores | SaveMiniGameScoreController |
| POST | /api/profiles/{userId}/minigames/results | SaveGameResultsController |

### Minimal Pairs (3)
| Method | Path | Controller |
|--------|------|------------|
| GET | /api/pronunciation/minimal-pairs | GetMinimalPairsController |
| GET | /api/profiles/{userId}/pronunciation/minimal-pairs/stats | GetMinimalPairStatsController |
| POST | /api/profiles/{userId}/pronunciation/minimal-pairs/results | RecordMinimalPairResultController |

### Module Progress (5)
| Method | Path | Controller |
|--------|------|------------|
| GET | /api/profiles/{userId}/modules | GetAllModuleProgressController |
| GET | /api/profiles/{userId}/modules/{module}/levels/{level} | GetModuleProgressController |
| POST | /api/profiles/{userId}/modules/{module}/levels/{level} | InitModuleProgressController |
| PUT | /api/profiles/{userId}/modules/{module}/levels/{level}/units/{unit} | CompleteUnitController |
| GET | /api/profiles/{userId}/modules/{module}/levels/{level}/level-up | CheckLevelUpController |

### Notification (3)
| Method | Path | Controller |
|--------|------|------------|
| GET | /api/notifications/preferences | GetNotificationPreferencesController |
| PUT | /api/notifications/preferences | UpdateNotificationPreferencesController |
| POST | /api/notifications/subscribe | SubscribePushController |

### Pronunciation (3)
| Method | Path | Controller |
|--------|------|------------|
| GET | /api/profiles/{userId}/pronunciation/errors | GetFrequentErrorsController |
| GET | /api/profiles/{userId}/pronunciation/problematic-sounds | GetProblematicSoundsController |
| POST | /api/profiles/{userId}/pronunciation/errors | RecordPronunciationErrorController |

### Reading (5)
| Method | Path | Controller |
|--------|------|------------|
| GET | /api/reading/passages | GetPassagesByLevelController |
| GET | /api/reading/passages/{id} | GetPassageByIdController |
| GET | /api/reading/passages/{id}/questions | GetPassageQuestionsController |
| POST | /api/reading/passages/{textId}/answers | SubmitPassageAnswersController |
| POST | /api/profiles/{userId}/reading/submit | SubmitReadingAnswersController |

### Session (7)
| Method | Path | Controller |
|--------|------|------------|
| POST | /api/profiles/{userId}/sessions/generate | GenerateSessionController |
| GET | /api/profiles/{userId}/sessions/current | GetCurrentSessionController |
| GET | /api/profiles/{userId}/sessions | GetSessionHistoryController |
| PUT | /api/profiles/{profileId}/sessions/{sessionId}/blocks/{blockIndex}/advance | AdvanceBlockController |
| GET | /api/profiles/{profileId}/sessions/{sessionId}/blocks/{blockIndex}/exercises | GetBlockExercisesController |
| PUT | /api/profiles/{userId}/sessions/{sessionId}/complete | CompleteSessionController |
| POST | /api/profiles/{profileId}/sessions/{sessionId}/exercises/{exerciseIndex}/result | RecordExerciseResultController |

### Spaced Repetition (4)
| Method | Path | Controller |
|--------|------|------------|
| GET | /api/profiles/{userId}/reviews/due | GetDueReviewsController |
| GET | /api/profiles/{userId}/reviews/stats | GetReviewStatsController |
| POST | /api/profiles/{userId}/reviews | AddToReviewQueueController |
| PUT | /api/profiles/{userId}/reviews/{itemId}/complete | CompleteReviewController |

### Tutor Error (4)
| Method | Path | Controller |
|--------|------|------------|
| GET | /api/profiles/{userId}/tutor/errors | GetUserErrorsController |
| GET | /api/profiles/{userId}/tutor/errors/trend | GetErrorTrendController |
| POST | /api/profiles/{userId}/tutor/errors | RecordTutorErrorController |
| POST | /api/profiles/{userId}/tutor/errors/{errorId}/exercise | GenerateErrorExerciseController |

### User Profile (9)
| Method | Path | Controller |
|--------|------|------------|
| POST | /api/profiles | CreateUserProfileController |
| GET | /api/profiles/{id} | GetUserProfileController |
| DELETE | /api/profiles/{id} | DeleteUserProfileController |
| PUT | /api/profiles/{id}/levels | SetAllLevelsController |
| PUT | /api/profiles/{id}/test-completed | MarkTestCompletedController |
| PUT | /api/profiles/{id}/reset-test | ResetTestController |
| PUT | /api/profiles/{id}/modules/{module}/level | UpdateModuleLevelController |
| POST | /api/profiles/{id}/xp | AddXpController |
| POST | /api/profiles/{id}/sessions | RecordSessionController |

### Vocabulary (7)
| Method | Path | Controller |
|--------|------|------------|
| GET | /api/vocab | GetAllVocabController |
| GET | /api/vocab/level/{level} | GetVocabByLevelController |
| GET | /api/vocab/random | GetRandomVocabController |
| GET | /api/vocab/search | SearchVocabController |
| POST | /api/vocab | CreateVocabEntryController |
| GET | /api/profiles/{userId}/vocabulary/unlearned | GetUnlearnedVocabController |
| GET | /api/profiles/{userId}/vocabulary/progress | GetVocabProgressController |

### Vocabulary Context (1)
| Method | Path | Controller |
|--------|------|------------|
| POST | /api/vocabulary/{wordId}/context | GenerateContextController |

### Writing (4)
| Method | Path | Controller |
|--------|------|------------|
| GET | /api/writing/exercises | GetWritingExercisesController |
| POST | /api/writing/submissions | SubmitWritingSubmissionController |
| POST | /api/profiles/{userId}/writing/submit | SubmitWritingController |
| GET | /api/profiles/{userId}/writing/history | GetWritingHistoryController |

**Total: ~12 public + ~101 authenticated = ~113 endpoints**
