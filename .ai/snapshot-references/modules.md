# Modules Snapshot

| Module | Domain (Aggregates, VOs, Events) | Use Cases | Controllers & Endpoints |
|--------|----------------------------------|-----------|--------------------------|
| activity | ActivityDate, ActivityDateId, ActivityRecordedEvent | GetActivityCalendar, GetActivityDates, GetStreak, RecordActivity | GetActivityDates(GET), GetStreak(GET), RecordActivity(POST) |
| admin | (none) | (none) | AdminPhrase(CRUD), AdminReading(CRUD), AdminVocab(CRUD), AdminWriting(CRUD) — 5 endpoints |
| analytics | LevelHistoryRepo | GetActivityHeatmap, GetAnalyticsSummary, GetProgressHistory | 3 GET endpoints |
| assessment | LevelTestResult, MiniTestResult, TestQuestion, LevelTestCompletedEvent | GetLevelTestQuestions, GetMiniTestQuestions, GetTestHistory, SubmitLevelTest, SubmitMiniTest | 5 endpoints |
| auth | AuthUser, RefreshToken, PasswordResetToken, 7 exceptions | ChangePassword, ForgotPassword, GetCurrentUser, GoogleLogin, LoginUser, LogoutUser, RegisterUser, ResetPassword | 9 endpoints (public) |
| conversation | Conversation, ConversationTurn, 10 VOs, 2 events, 5 exceptions | EndConversation, GetConversationStats, GetConversation, ListConversations, SendMessage, StartConversation, StreamMessage, SuggestTopics | 9 endpoints |
| curriculum | CurriculumDay/Week/Block, UnitDefinition, ModuleDefinition | GetCurriculumPlan, GetIntegratorSessions, GetModuleDefinitions | 4 endpoints |
| dailychallenge | DailyChallenge, UserChallenge, ChallengeCompletedEvent | GetTodayChallenge, GetUserChallengeProgress, UpdateChallengeProgress | 3 endpoints |
| errorpattern | ErrorPattern, ErrorPatternId, ErrorCategory | GetErrorPatterns, RecordErrorPattern | 1 endpoint |
| exercise | Exercise, ConversationExercise | GenerateExercises, GetConversationExercises | 1 endpoint |
| gamification | Achievement, UserAchievement, AchievementUnlockedEvent | CheckAndUnlockAchievements, GetAllAchievements, GetUserAchievements, GetXpLevel, GrantXp | 5 endpoints |
| learningpath | LearningPath, LearningUnit, 6 VOs, 2 events, 3 exceptions | AdvanceUnit, GenerateLearningPath, GetLearningPath, GetLearningStatus, GetNextContent, RecordExerciseResult | 3 endpoints |
| minigame | MiniGameScore | GetFillGapData, GetUnscrambleData, GetWordMatchData, SaveGameResults, SaveMiniGameScore | 6 endpoints |
| minimalpair | MinimalPair, MinimalPairResult | GetMinimalPairStats, GetMinimalPairsByLevel, RecordMinimalPairResult | 3 endpoints |
| moduleprogress | ModuleProgress, 3 VOs, 2 exceptions | CheckLevelUp, CompleteUnit, GetAllModuleProgress, GetModuleProgress, InitModuleProgress | 5 endpoints |
| notification | PushSubscription, NotificationPreferences | GetNotificationPreferences, SubscribePush, UpdateNotificationPreferences | 3 endpoints |
| phonetics | Phoneme, PhonemePracticePhrase, UserPhonemeProgress, PhonemeDailyAssignment, PhonemeCompletedEvent, 5 exceptions | CompletePhoneme, GetAllPhonemes, GetPhonemeById, GetPhrasesByPhoneme, GetTodayPhoneme, GetUserPhonemeProgress, RecordPhraseAttempt | 8 endpoints |
| phrase | Phrase, PhraseId | GetPhrasesByLevel, GetRandomPhrases | 2 endpoints |
| pronunciation | PronunciationError | GetFrequentErrors, GetProblematicSounds, RecordPronunciationError | 3 endpoints |
| reading | ReadingPassage, ReadingSubmission | GetPassageById, GetPassagesByLevel, SubmitReadingAnswers | 5 endpoints |
| session | Session, SessionBlock, ExerciseResult, SessionExercise, SessionCompletedEvent, 5 exceptions | AdvanceBlock, CompleteSession, GenerateSession, GetBlockExercises, GetCurrentSession, GetSessionHistory | 7 endpoints |
| spacedrepetition | SpacedRepetitionItem, ReviewCompletedEvent, 2 exceptions | AddToReviewQueue, AddVocabularyToReview, CompleteReview, GetDueReviews, GetReviewStats | 4 endpoints |
| tutorerror | TutorError | GenerateErrorExercise, GetErrorTrend, GetUserErrors, RecordTutorError | 4 endpoints |
| user | UserProfile, UserLevel, 2 events, 5 exceptions | AddXp, CreateUserProfile, DeleteUserProfile, GetUserProfile, MarkTestCompleted, RecordSession, ResetTest, ResetWeeklyCounters, SetAllLevels, UpdateModuleLevel | 9 endpoints |
| vocabulary | VocabEntry, VocabMastery, 5 VOs, 2 events | CreateVocabEntry, GetAllVocab, GetRandomVocab, GetUnlearnedVocab, GetVocabByLevelAndBlock, GetVocabByLevel, GetVocabProgress, SearchVocab | 7 endpoints |
| vocabularycontext | VocabularyContext | GenerateContextSentences | 1 endpoint |
| writing | WritingExercise, WritingSubmission, WritingFeedback | GetWritingExercises, GetWritingHistory, SubmitWriting | 4 endpoints |

**Totals**: 27 modules, ~117 use cases, ~120 controllers, ~120 endpoints
