# Modules Snapshot

| Module | Domain (Aggregates, VOs, Events, Exceptions) | Use Cases | Controllers & Endpoints |
|--------|----------------------------------------------|-----------|--------------------------|
| activity | ActivityDate; ActivityDateId; ActivityRecordedEvent | GetActivityCalendar, GetActivityDates, GetStreak, RecordActivity | 3 — GET+POST /profiles/{userId}/activity, GET /profiles/{userId}/streak |
| admin | (none) | (none) | 5 — GET+POST /admin/vocab, GET /admin/phrases, POST /admin/reading, POST /admin/writing |
| analytics | LevelHistoryEntry, AnalyticsSummary, WeaknessReport | GetActivityHeatmap, GetAnalyticsSummary, GetProgressHistory | 3 — GET /profiles/{userId}/analytics/* |
| assessment | LevelTestResult, MiniTestResult, TestQuestion; 3 IDs; LevelTestCompletedEvent | GetLevelTestQuestions, GetMiniTestQuestions, GetTestHistory, SubmitLevelTest, SubmitMiniTest | 5 — GET+POST /profiles/{userId}/assessments/*, GET /assessments/mini-test |
| auth | AuthUser, RefreshToken, PasswordResetToken; AuthUserId; 6 exceptions | ChangePassword, ForgotPassword, GetCurrentUser, GoogleLogin, LoginUser, LogoutUser, RegisterUser, ResetPassword | 9 — POST /auth/login+register+google+refresh+logout+forgot+reset, GET /auth/me, PUT /auth/change-password |
| conversation | Conversation, ConversationTurn; 2 IDs; 2 events; 4 exceptions | EndConversation, GetConversationStats, GetConversation, ListConversations, SendMessage, StartConversation, StreamMessage, SuggestTopics | 9 — CRUD+stream /conversations/* |
| curriculum | CurriculumDay, CurriculumWeek, CurriculumBlock, UnitDefinition, ModuleDefinition | GetCurriculumPlan, GetIntegratorSessions, GetModuleDefinitions | 4 — GET /curriculum/* |
| dailychallenge | DailyChallenge, UserChallenge; 2 IDs; ChallengeCompletedEvent | GetTodayChallenge, GetUserChallengeProgress, UpdateChallengeProgress | 3 — GET+PUT /challenges/*, /profiles/{userId}/challenges/* |
| errorpattern | ErrorPattern; ErrorPatternId | GetErrorPatterns, RecordErrorPattern | 1 — GET /profiles/{userId}/error-patterns |
| exercise | Exercise, ConversationExercise; ConversationExerciseId | GenerateExercises, GetConversationExercises | 1 — GET /conversations/{id}/exercises |
| gamification | Achievement, UserAchievement; 2 IDs; AchievementUnlockedEvent | CheckAndUnlockAchievements, GetAllAchievements, GetUserAchievements, GetXpLevel, GrantXp | 5 — GET+POST /profiles/{userId}/achievements+xp |
| learningpath | LearningPath, LearningUnit; 2 IDs; 2 events; 3 exceptions | AdvanceUnit, GenerateLearningPath, GetLearningPath, GetLearningStatus, GetNextContent, RecordExerciseResult | 3 — POST+GET /profiles/{profileId}/learning-path+learning-status |
| minigame | MiniGameScore; MiniGameScoreId | GetFillGapData, GetUnscrambleData, GetWordMatchData, SaveGameResults, SaveMiniGameScore | 6 — GET /minigames/*, GET+POST /profiles/{userId}/minigames/* |
| minimalpair | MinimalPair, MinimalPairResult; 2 IDs | GetMinimalPairStats, GetMinimalPairsByLevel, RecordMinimalPairResult | 3 — GET /pronunciation/minimal-pairs, GET+POST /profiles/{userId}/pronunciation/minimal-pairs/* |
| moduleprogress | ModuleProgress; ModuleProgressId; 2 exceptions | CheckLevelUp, CompleteUnit, GetAllModuleProgress, GetModuleProgress, InitModuleProgress | 5 — GET+POST+PUT /profiles/{userId}/modules/* |
| notification | PushSubscription, NotificationPreferences | GetNotificationPreferences, SubscribePush, UpdateNotificationPreferences | 3 — GET+POST+PUT /notifications/* |
| phonetics | Phoneme, PhonemePracticePhrase, UserPhonemeProgress, PhonemeDailyAssignment; 4 IDs; PhonemeCompletedEvent; 6 exceptions | CompletePhoneme, GetAllPhonemes, GetPhonemeById, GetPhrasesByPhoneme, GetTodayPhoneme, RecordPhraseAttempt | 6 — GET /phonetics/phonemes/*, GET+POST+PUT /profiles/{userId}/phonetics/* |
| phrase | Phrase; PhraseId | GetPhrasesByLevel, GetRandomPhrases | 2 — GET /phrases, GET /phrases/random |
| pronunciation | PronunciationError; PronunciationErrorId; PronunciationException | GetFrequentErrors, GetProblematicSounds, RecordPronunciationError | 3 — GET+POST /profiles/{userId}/pronunciation/* |
| reading | ReadingPassage, ReadingSubmission; ReadingPassageId | GetPassageById, GetPassagesByLevel, SubmitReadingAnswers | 5 — GET+POST /reading/passages/*, POST /profiles/{userId}/reading/submit |
| session | Session, SessionExercise; SessionId; SessionCompletedEvent; 5 exceptions | AdvanceBlock, CompleteSession, GenerateSession, GetBlockExercises, GetCurrentSession, GetSessionHistory | 7 — POST+GET+PUT /profiles/{userId}/sessions/* |
| spacedrepetition | SpacedRepetitionItem; SpacedRepetitionItemId; ReviewCompletedEvent; 2 exceptions | AddToReviewQueue, AddVocabularyToReview, CompleteReview, GetDueReviews, GetReviewStats | 4 — POST+GET+PUT /profiles/{userId}/reviews/* |
| tutorerror | TutorError; TutorErrorId; TutorErrorException | GenerateErrorExercise, GetErrorTrend, GetUserErrors, RecordTutorError | 4 — GET+POST /profiles/{userId}/tutor/errors/* |
| user | UserProfile; UserProfileId; 2 events; 5 exceptions | AddXp, CreateUserProfile, DeleteUserProfile, GetUserProfile, MarkTestCompleted, RecordSession, ResetTest, ResetWeeklyCounters, SetAllLevels, UpdateModuleLevel | 9 — POST+GET+DELETE+PUT /profiles/* |
| vocabulary | VocabEntry, VocabMastery; 2 IDs; WordLearnedEvent, WordMasteredEvent | CreateVocabEntry, GetAllVocab, GetRandomVocab, GetUnlearnedVocab, GetVocabByLevelAndBlock, GetVocabByLevel, GetVocabProgress, SearchVocab | 7 — GET+POST /vocab/*, GET /profiles/{userId}/vocabulary/* |
| vocabularycontext | VocabularyContext; VocabularyContextId | GenerateContextSentences | 1 — POST /vocabulary/{wordId}/context |
| writing | WritingExercise, WritingSubmission; WritingExerciseId | GetWritingExercises, GetWritingHistory, SubmitWriting | 4 — GET+POST /writing/*, POST /profiles/{userId}/writing/submit |
