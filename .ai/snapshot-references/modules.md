# Modules Snapshot

| Module | Domain (Aggregates, VOs, Events, Exceptions) | Use Cases | Controllers & Endpoints |
|--------|----------------------------------------------|-----------|--------------------------|
| activity | ActivityDate, ActivityDateId, StreakInfo; ActivityRecordedEvent | GetActivityDatesUseCase, GetActivityCalendarUseCase, GetStreakUseCase, RecordActivityUseCase | 3 endpoints — GET+POST /api/profiles/{userId}/activity, streak |
| admin | (no domain) | (none) | 4 controllers — GET+POST /api/admin/* (5 endpoints) |
| analytics | AnalyticsSummary, LevelHistoryEntry, WeaknessReport | GetProgressHistoryUseCase, GetAnalyticsSummaryUseCase, GetActivityHeatmapUseCase | 3 endpoints — GET /api/profiles/{userId}/analytics/* |
| assessment | LevelTestResult, MiniTestResult, TestQuestion; LevelTestCompletedEvent | GetTestHistoryUseCase, SubmitLevelTestUseCase, SubmitMiniTestUseCase, GetLevelTestQuestionsUseCase, GetMiniTestQuestionsUseCase | 5 endpoints — GET+POST /api/profiles/{userId}/assessments/* |
| auth | AuthUser, AuthUserId; 7 exceptions | RegisterUserUseCase, GetCurrentUserUseCase, GoogleLoginUseCase, LoginUserUseCase, ChangePasswordUseCase, ForgotPasswordUseCase, ResetPasswordUseCase, LogoutUserUseCase | 9 endpoints — /api/auth/* |
| conversation | Conversation, 11 VOs; ConversationStartedEvent, ConversationCompletedEvent; 5 exceptions | 8 use cases (Start, Send, Stream, End, Get, List, Stats, SuggestTopics) | 9 endpoints — /api/conversations/* |
| curriculum | ModuleDefinition, CurriculumPlan (JSON-based) | GetCurriculumPlanUseCase, GetModuleDefinitionsUseCase, GetIntegratorSessionsUseCase | 4 endpoints — GET /api/curriculum/* |
| dailychallenge | DailyChallenge, UserChallenge; ChallengeCompletedEvent | GetTodayChallengeUseCase, GetUserChallengeProgressUseCase, UpdateChallengeProgressUseCase | 3 endpoints — /api/challenges/* |
| errorpattern | ErrorPattern, ErrorCategory | RecordErrorPatternUseCase, GetErrorPatternsUseCase | 1 endpoint — GET /api/profiles/{userId}/error-patterns |
| exercise | ConversationExercise, ExerciseType | GenerateExercisesUseCase, GetConversationExercisesUseCase | 1 endpoint — GET /api/conversations/{id}/exercises |
| gamification | Achievement, UserAchievement, XpLevel; AchievementUnlockedEvent | 5 use cases (GetAll, GetUser, Check, Grant, GetXpLevel) | 5 endpoints — /api/achievements + /api/profiles/{userId}/* |
| learningpath | LearningPath, LearningUnit, MasteryScore; UnitMasteredEvent, LevelCompletedEvent | 6 use cases (Generate, GetPath, GetStatus, GetNextContent, Advance, RecordResult) | 3 endpoints — /api/profiles/{profileId}/learning-path/* |
| minigame | MiniGameScore, MiniGameScoreId | GetFillGapDataUseCase, GetWordMatchDataUseCase, GetUnscrambleDataUseCase, SaveMiniGameScoreUseCase, SaveGameResultsUseCase | 6 endpoints — /api/minigames/* + /api/profiles/{userId}/minigames/* |
| minimalpair | MinimalPair, MinimalPairResult | 3 use cases (GetByLevel, Record, GetStats) | 3 endpoints — /api/pronunciation/minimal-pairs/* |
| moduleprogress | ModuleProgress, ModuleName, ModuleLevel | 5 use cases (Get, GetAll, CheckLevelUp, Init, CompleteUnit) | 5 endpoints — /api/profiles/{userId}/modules/* |
| notification | PushSubscription, NotificationPreferences | SubscribePushUseCase, UpdateNotificationPreferencesUseCase, GetNotificationPreferencesUseCase | 3 endpoints — /api/notifications/* |
| phrase | Phrase, PhraseId | GetPhrasesByLevelUseCase, GetRandomPhrasesUseCase | 2 endpoints — GET /api/phrases* |
| pronunciation | PronunciationError | 3 use cases (Record, GetFrequent, GetProblematic) | 3 endpoints — /api/profiles/{userId}/pronunciation/* |
| reading | ReadingPassage, ReadingSubmission, ReadingQuestion | GetPassagesByLevelUseCase, SubmitReadingAnswersUseCase, GetPassageByIdUseCase | 5 endpoints — /api/reading/* |
| session | Session, SessionBlock, SessionExercise, BlockProgress, ExerciseResult; SessionCompletedEvent; 5 exceptions | 6 use cases (Generate, GetCurrent, Complete, GetHistory, AdvanceBlock, GetBlockExercises) + RecordExerciseResult | 7 endpoints — /api/profiles/{userId}/sessions/* |
| spacedrepetition | SpacedRepetitionItem; ReviewCompletedEvent | 5 use cases (GetDue, GetStats, Complete, AddVocab, AddToQueue) | 4 endpoints — /api/profiles/{userId}/reviews/* |
| tutorerror | TutorError | 4 use cases (GetErrors, GetTrend, Record, GenerateExercise) | 4 endpoints — /api/profiles/{userId}/tutor/errors/* |
| user | UserProfile, UserLevel; UserProfileCreatedEvent, XpGrantedEvent; 5 exceptions | 10 use cases (Get, Create, Delete, UpdateLevel, MarkTest, ResetTest, SetAllLevels, RecordSession, AddXp, ResetWeekly) | 9 endpoints — /api/profiles/* |
| vocabulary | VocabEntry, VocabMastery, VocabLevel; WordLearnedEvent, WordMasteredEvent | 8 use cases (GetByLevel, GetRandom, Search, Create, GetAll, GetByBlock, GetUnlearned, GetProgress) | 7 endpoints — /api/vocab/* + /api/profiles/{userId}/vocabulary/* |
| vocabularycontext | VocabularyContext | GenerateContextSentencesUseCase | 1 endpoint — POST /api/vocabulary/{wordId}/context |
| writing | WritingExercise, WritingSubmission, WritingFeedback | GetWritingExercisesUseCase, SubmitWritingUseCase, GetWritingHistoryUseCase | 4 endpoints — /api/writing/* + /api/profiles/{userId}/writing/* |

**Totals**: 26 modules, ~117 use cases, ~131 controllers, ~114 endpoints
