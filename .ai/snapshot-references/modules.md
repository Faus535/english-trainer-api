# Modules Snapshot

| Module | Domain (Aggregates, VOs, Events, Exceptions) | Use Cases | Controllers & Endpoints |
|--------|----------------------------------------------|-----------|--------------------------|
| activity | ActivityDate, ActivityDateId, StreakCalculator, StreakInfo; ActivityRecordedEvent | GetActivityDatesUseCase, GetActivityCalendarUseCase, GetStreakUseCase, RecordActivityUseCase | RecordActivityController(POST), GetActivityDatesController(GET), GetStreakController(GET) — 3 endpoints |
| admin | (no domain) | (none) | AdminWritingController(POST), AdminVocabController(GET+POST), AdminPhraseController(GET), AdminReadingController(POST) — 4 endpoints |
| analytics | AnalyticsSummary, LevelHistoryEntry, WeaknessReport | GetProgressHistoryUseCase, GetAnalyticsSummaryUseCase, GetActivityHeatmapUseCase | 3 controllers — 3 endpoints |
| assessment | LevelTestResult, MiniTestResult, TestQuestion; LevelTestCompletedEvent | SubmitLevelTestUseCase, SubmitMiniTestUseCase, GetLevelTestQuestionsUseCase, GetMiniTestQuestionsUseCase, GetTestHistoryUseCase | 5 controllers — 5 endpoints |
| auth | AuthUser, RefreshToken, PasswordResetToken; 7 exceptions | RegisterUserUseCase, LoginUserUseCase, GoogleLoginUseCase, GetCurrentUserUseCase, ChangePasswordUseCase, ForgotPasswordUseCase, ResetPasswordUseCase, LogoutUserUseCase | 9 controllers — 9 endpoints |
| conversation | Conversation, ConversationTurn, ConversationGoal, LevelProfiles; 2 events; 5 exceptions | StartConversationUseCase, SendMessageUseCase, StreamMessageUseCase, EndConversationUseCase, GetConversationUseCase, ListConversationsUseCase, GetConversationStatsUseCase, SuggestTopicsUseCase | 9 controllers — 9 endpoints |
| curriculum | ModuleDefinition, CurriculumBlock, CurriculumDay, IntegratorDefinition | GetCurriculumPlanUseCase, GetModuleDefinitionsUseCase, GetIntegratorSessionsUseCase | 4 controllers — 4 endpoints |
| dailychallenge | DailyChallenge, UserChallenge; ChallengeCompletedEvent; 2 exceptions | GetTodayChallengeUseCase, GetUserChallengeProgressUseCase, UpdateChallengeProgressUseCase | 3 controllers — 3 endpoints |
| errorpattern | ErrorPattern, ErrorCategory | RecordErrorPatternUseCase, GetErrorPatternsUseCase | 1 controller — 1 endpoint |
| exercise | ConversationExercise, Exercise, ExerciseType | GenerateExercisesUseCase, GetConversationExercisesUseCase | 1 controller — 1 endpoint |
| gamification | Achievement, UserAchievement, XpLevel; AchievementUnlockedEvent | GetAllAchievementsUseCase, GrantXpUseCase, GetUserAchievementsUseCase, CheckAndUnlockAchievementsUseCase, GetXpLevelUseCase | 5 controllers — 5 endpoints |
| learningpath | LearningPath, LearningUnit, MasteryScore, ContentSelector; 2 events; 3 exceptions | GenerateLearningPathUseCase, GetNextContentUseCase, GetLearningStatusUseCase, AdvanceUnitUseCase, GetLearningPathUseCase, RecordExerciseResultUseCase | 3 controllers — 3 endpoints |
| minigame | MiniGameScore | GetWordMatchDataUseCase, GetFillGapDataUseCase, GetUnscrambleDataUseCase, SaveMiniGameScoreUseCase, SaveGameResultsUseCase | 6 controllers — 6 endpoints |
| minimalpair | MinimalPair, MinimalPairResult | GetMinimalPairsByLevelUseCase, RecordMinimalPairResultUseCase, GetMinimalPairStatsUseCase | 3 controllers — 3 endpoints |
| moduleprogress | ModuleProgress, ModuleLevel, ModuleName; 2 exceptions | GetModuleProgressUseCase, GetAllModuleProgressUseCase, CheckLevelUpUseCase, InitModuleProgressUseCase, CompleteUnitUseCase | 5 controllers — 5 endpoints |
| notification | PushSubscription, NotificationPreferences | SubscribePushUseCase, UpdateNotificationPreferencesUseCase, GetNotificationPreferencesUseCase | 3 controllers — 3 endpoints |
| phrase | Phrase | GetPhrasesByLevelUseCase, GetRandomPhrasesUseCase | 2 controllers — 2 endpoints |
| pronunciation | PronunciationError; PronunciationException | RecordPronunciationErrorUseCase, GetFrequentErrorsUseCase, GetProblematicSoundsUseCase | 3 controllers — 3 endpoints |
| reading | ReadingPassage, ReadingSubmission, ReadingQuestion | GetPassagesByLevelUseCase, SubmitReadingAnswersUseCase, GetPassageByIdUseCase | 5 controllers — 5 endpoints |
| session | Session, BlockProgress, SessionExercise, ExerciseResult, SessionBlock, SessionGenerator; SessionCompletedEvent; 5 exceptions | GenerateSessionUseCase, GetCurrentSessionUseCase, GetSessionHistoryUseCase, CompleteSessionUseCase, AdvanceBlockUseCase, GetBlockExercisesUseCase | 7 controllers — 7 endpoints |
| spacedrepetition | SpacedRepetitionItem; ReviewCompletedEvent; 2 exceptions | GetReviewStatsUseCase, GetDueReviewsUseCase, CompleteReviewUseCase, AddVocabularyToReviewUseCase, AddToReviewQueueUseCase | 4 controllers — 4 endpoints |
| tutorerror | TutorError; TutorErrorException | GetUserErrorsUseCase, GetErrorTrendUseCase, RecordTutorErrorUseCase, GenerateErrorExerciseUseCase | 4 controllers — 4 endpoints |
| user | UserProfile, UserLevel; UserProfileCreatedEvent, XpGrantedEvent; 5 exceptions | GetUserProfileUseCase, UpdateModuleLevelUseCase, MarkTestCompletedUseCase, CreateUserProfileUseCase, DeleteUserProfileUseCase, ResetWeeklyCountersUseCase, RecordSessionUseCase, AddXpUseCase, ResetTestUseCase, SetAllLevelsUseCase | 9 controllers — 9 endpoints |
| vocabulary | VocabEntry, VocabMastery; WordLearnedEvent, WordMasteredEvent | GetVocabByLevelUseCase, GetRandomVocabUseCase, SearchVocabUseCase, CreateVocabEntryUseCase, GetAllVocabUseCase, GetVocabByLevelAndBlockUseCase, GetUnlearnedVocabUseCase, GetVocabProgressUseCase | 7 controllers — 7 endpoints |
| vocabularycontext | VocabularyContext | GenerateContextSentencesUseCase | 1 controller — 1 endpoint |
| writing | WritingExercise, WritingSubmission, WritingFeedback | GetWritingExercisesUseCase, SubmitWritingUseCase, GetWritingHistoryUseCase | 4 controllers — 4 endpoints |
