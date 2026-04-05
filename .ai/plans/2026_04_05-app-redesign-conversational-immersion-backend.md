# Backend Plan: App Redesign - Talk, Immerse, Review

> Generated: 2026-04-05
> Request: Complete app redesign: strip 27 modules down to 3 core experiences (Talk, Immerse, Review). Talk = AI conversations with real-time correction. Immerse = learn from real content with deep exercises. Review = spaced repetition of personal errors. Support listening-first learning for B1-B2 user.

## Decisions Log

| # | Topic | Decision | Alternatives Considered | Why |
|---|-------|----------|------------------------|-----|
| 1 | Talk module naming | Name aggregate `TalkConversation` (not `TalkSession`) | `TalkSession` (Architect), `Conversation` (existing) | `TalkConversation` distinguishes from existing `Conversation` while making the domain clear; `TalkSession` is ambiguous with HTTP sessions |
| 2 | Review module approach | Create new `review` module with proper SM-2 algorithm (ease_factor + quality rating) instead of thin facade over spacedrepetition | Thin facade over existing spacedrepetition (Developer), completely new tables (Data/Security) | Existing `spacedrepetition` uses a simple interval array, not true SM-2. The new review module needs front/back content for flashcard UX and proper ease_factor math. Keep old module alive temporarily to avoid breaking cross-module listeners |
| 3 | Talk scenario naming | Use `title` field (Data/Security agent) not `name` (Developer) | `name` vs `title` | `title` is more descriptive for user-facing content and consistent with `immerse_content.title` |
| 4 | Immerse exercises storage | Separate `immerse_exercises` table (normalized) instead of JSONB `exercises_json` column | JSONB column (Developer), separate table (Data/Security) | Separate table enables individual exercise submissions with proper FK constraints, better for querying and the answer-tracking use case |
| 5 | Talk pronunciation feedback | Skip `talk_pronunciation_feedback` table in Phase 1; speech endpoint is a stub | Full pronunciation table (Data/Security) | Audio analysis requires a real speech-to-text service not yet integrated. The speech endpoint returns a stub response. Add the table when the service is ready |
| 6 | Review ENUM type | Use VARCHAR with CHECK constraint instead of PostgreSQL ENUM | `CREATE TYPE review_source_type AS ENUM` (Data/Security) | Spring Data JDBC handles VARCHAR better than custom PG enums; avoids migration headaches when adding new source types |
| 7 | ConversationLevel reuse | Reuse existing `ConversationLevel` value object from conversation module by copying it into talk module (not importing across modules) | Import from conversation module, create from scratch | Modules must be independent. Copy the VO to maintain isolation while reusing the proven validation logic |
| 8 | Module archival timing | Archive old modules in Phase 6 (last phase), not Phase 1 | Archive first (risky), archive last (safe) | Avoids compilation breaks from cross-module listeners (vocabulary.WordLearnedListener -> spacedrepetition). New modules must be fully operational before removing old ones |
| 9 | User ID type in new modules | Use `UUID userId` directly (not `UserProfileId` wrapper) | Use `UserProfileId` from user.domain | The existing conversation module uses raw `UUID userId`. New modules follow the same pattern for consistency. The user module boundary is at the DB FK level |
| 10 | Flyway version numbers | Start at V10.0.0 (next major after V9.7.0) | V10.0.0 (all agents agreed) | Clear major version bump signals the redesign. No conflicts with existing V9.x migrations |

## Analysis

### Existing code to reuse

**Conversation module** (`com.faus535.englishtrainer.conversation`):
- `AnthropicAiTutorAdapter.java` -- RestClient + prompt caching + <<F>> feedback parsing pattern. Will be adapted for Talk AI adapter.
- `AnthropicAiTutorStreamAdapter.java` -- WebClient + Flux SSE streaming. Will be adapted for Talk streaming.
- `SystemPromptBuilder.java` -- Prompt construction. Will be adapted as `TalkSystemPromptBuilder`.
- `AiTutorPort.java` / `AiTutorStreamPort.java` -- Interface patterns for port/adapter.
- `ConversationLevel.java` -- CEFR level validation VO. Copy to talk module.
- `ConversationEvaluation.java` -- Evaluation VO structure. Adapt for `TalkEvaluation`.
- `TutorFeedback.java` -- Correction VO. Adapt for `TalkCorrection`.
- `Conversation.java` -- Immutable aggregate pattern (start/reconstitute/addTurn/end). Blueprint for `TalkConversation`.

**Exercise module** (`com.faus535.englishtrainer.exercise`):
- `AnthropicExerciseGeneratorAdapter.java` -- Claude tool-use pattern for exercise generation. Blueprint for `AnthropicImmerseAiAdapter`.
- `ExerciseGeneratorPort.java` -- Port interface pattern.
- `ExerciseType.java` -- Exercise type enum. Will be expanded for Immerse.

**Spaced Repetition module** (`com.faus535.englishtrainer.spacedrepetition`):
- `SpacedRepetitionItem.java` -- SM-2-lite algorithm in `completeReview()`. The new Review module improves this with proper ease_factor.
- `SpacedRepetitionRepository.java` / `JpaSpacedRepetitionRepository.java` -- Repository pattern reference.

**Shared module** (`com.faus535.englishtrainer.shared`):
- `AggregateRoot.java` -- Base class with domain event support.
- `SecurityConfig.java` -- Will be modified to permit new routes (already permits `/api/**` for authenticated users).
- `GlobalControllerAdvice.java` -- Will be extended for new module exceptions.
- `RateLimitingFilter.java` -- Apply to AI-heavy endpoints.
- `ProfileOwnershipAspect.java` + `@RequireProfileOwnership` -- Reuse for ownership checks.
- `UseCase.java` -- `@UseCase` annotation.
- `PageResponse.java` -- Pagination response wrapper.

### Cross-module dependencies to manage

- `vocabulary.infrastructure.event.WordLearnedListener` imports `spacedrepetition.application.AddVocabularyToReviewUseCase` -- will break if spacedrepetition is archived before vocabulary.
- `vocabulary.infrastructure.event.SrsGraduationListener` -- same pattern, imports from spacedrepetition.
- `conversation.infrastructure.event.ConversationCompletedEventListener` -- references exercise/gamification modules.
- `learningpath.application` imports `spacedrepetition.domain` -- same issue.

These dependencies are resolved by deferring module archival to Phase 6.

### Database state

Last migration: `V9.7.0__phonetics_spanish_content_and_phrases.sql`. Next available: `V10.0.0`.
User profiles table: `user_profiles` with `id UUID` primary key.
Existing conversation tables: `conversations`, `conversation_turns` (from V3.0.0, V4.2.0, V6.0.0).

## Phases

### Phase 1: Talk Backend Core

**Goal**: Deliver the Talk module end-to-end: scenario catalog, AI conversation with real-time corrections, summary/evaluation, and stats. This is the highest-value vertical slice -- the core "Talk" experience.

**Files to create**:

*Domain* (`src/main/java/com/faus535/englishtrainer/talk/domain/`):
- `TalkConversation.java` -- Aggregate Root extending `AggregateRoot<TalkConversationId>`. Fields: id, userId (UUID), scenarioId (UUID, nullable), level (TalkLevel), status (TalkStatus), summary (String), evaluation (TalkEvaluation), startedAt, endedAt, turns (List<TalkMessage>). Methods: `start()`, `reconstitute()`, `addMessage()`, `end()`, `recentMessages(int)`. Publishes `TalkConversationCompletedEvent` on `end()`.
- `TalkConversationId.java` -- `record TalkConversationId(UUID value)` with `generate()` and validation.
- `TalkMessage.java` -- `record TalkMessage(TalkMessageId id, String role, String content, TalkCorrection correction, Instant createdAt)`. Role is "user" or "assistant".
- `TalkMessageId.java` -- `record TalkMessageId(UUID value)` with `generate()`.
- `TalkCorrection.java` -- `record TalkCorrection(List<String> grammarFixes, List<String> vocabularySuggestions, List<String> pronunciationTips, String encouragement)` with `empty()` factory.
- `TalkEvaluation.java` -- `record TalkEvaluation(int grammarAccuracy, int vocabularyRange, int fluency, int taskCompletion, int overallScore, String levelDemonstrated, List<String> strengths, List<String> areasToImprove)` with `empty()` factory. Reuses structure from `ConversationEvaluation`.
- `TalkLevel.java` -- `record TalkLevel(String value)` with CEFR validation (copy from `ConversationLevel`).
- `TalkStatus.java` -- `enum TalkStatus { ACTIVE, COMPLETED }`
- `TalkStats.java` -- `record TalkStats(int totalConversations, int completedConversations, int totalMessages, double averageScore)`
- `TalkScenario.java` -- Aggregate Root. Fields: id (TalkScenarioId), title, description, contextPrompt, category, cefrLevel (TalkLevel), difficultyOrder, createdAt. Methods: `create()`, `reconstitute()`.
- `TalkScenarioId.java` -- `record TalkScenarioId(UUID value)` with `generate()`.
- `TalkAiPort.java` -- Interface: `chat(TalkLevel, TalkScenario, List<TalkMessage>, Float confidence): TalkAiResponse`, `summarize(TalkLevel, List<TalkMessage>): String`, `evaluate(TalkLevel, List<TalkMessage>): TalkEvaluation`. Inner record `TalkAiResponse(String content, TalkCorrection correction)`.
- `TalkConversationRepository.java` -- Interface: `save(TalkConversation)`, `findById(TalkConversationId)`, `findByUserId(UUID)`, `countActiveByUserId(UUID)`.
- `TalkScenarioRepository.java` -- Interface: `findAll()`, `findByLevel(TalkLevel)`, `findById(TalkScenarioId)`.
- `event/TalkConversationCompletedEvent.java` -- `record(TalkConversationId conversationId, UUID userId, List<TalkCorrection> corrections, int turnCount)`.
- `error/TalkException.java` -- `abstract class TalkException extends Exception` (checked).
- `error/TalkConversationNotFoundException.java` -- extends `TalkException`.
- `error/TalkConversationAlreadyEndedException.java` -- extends `TalkException`.
- `error/TalkAiException.java` -- extends `TalkException`.
- `error/TalkMaxConversationsExceededException.java` -- extends `TalkException`.

*Application* (`src/main/java/com/faus535/englishtrainer/talk/application/`):
- `ListTalkScenariosUseCase.java` -- `@UseCase`. Method: `execute(String level): List<TalkScenario>`. Queries `TalkScenarioRepository.findByLevel()`.
- `StartTalkConversationUseCase.java` -- `@UseCase`. Method: `execute(UUID userId, UUID scenarioId, String level): TalkConversation`. Validates max 1 active conversation per user. Creates `TalkConversation.start()`, calls `talkAiPort.chat()` for AI greeting, adds greeting as assistant turn, saves.
- `SendTalkMessageUseCase.java` -- `@UseCase`. Method: `execute(UUID conversationId, String userContent, Float confidence): TalkAiResponse`. Loads conversation, adds user turn, calls `talkAiPort.chat()`, adds assistant turn with correction, saves. Returns AI response with correction.
- `EndTalkConversationUseCase.java` -- `@UseCase`. Method: `execute(UUID conversationId): TalkConversationSummary`. Calls `talkAiPort.summarize()` and `talkAiPort.evaluate()`, then `conversation.end()`. Returns summary record. Inner `record TalkConversationSummary(String summary, TalkEvaluation evaluation, int turnCount, int errorCount)`.
- `GetTalkConversationSummaryUseCase.java` -- `@UseCase`. Method: `execute(UUID conversationId): TalkConversationSummary`. Loads completed conversation, returns stored summary + evaluation.
- `GetTalkStatsUseCase.java` -- `@UseCase`. Method: `execute(UUID userId): TalkStats`. Queries repository for aggregated stats.

*Infrastructure - AI* (`src/main/java/com/faus535/englishtrainer/talk/infrastructure/ai/`):
- `AnthropicTalkAiAdapter.java` -- Implements `TalkAiPort`. Adapted from `AnthropicAiTutorAdapter`. Preserves: RestClient setup, `anthropic-beta: prompt-caching-2024-07-31` header, `cache_control: {type: ephemeral}` in system blocks, <<F>> feedback parsing, greeting cache, token-based context windowing. Changes: uses `TalkMessage`/`TalkCorrection`/`TalkEvaluation` types, takes `TalkScenario.contextPrompt` for system prompt context.
- `TalkSystemPromptBuilder.java` -- Adapted from `SystemPromptBuilder`. Adds scenario context injection. Same feedback-frequency-by-level logic.

*Infrastructure - Persistence* (`src/main/java/com/faus535/englishtrainer/talk/infrastructure/persistence/`):
- `TalkConversationEntity.java` -- `@Table("talk_conversations")`, `Persistable<UUID>`. Maps to/from `TalkConversation`. Stores evaluation as JSON string, messages as `MappedCollection`.
- `TalkMessageEntity.java` -- `@Table("talk_messages")`. Stores correction as JSON string.
- `JpaTalkConversationRepository.java` -- Spring Data JDBC `CrudRepository<TalkConversationEntity, UUID>` with custom queries: `findByUserId`, `countByUserIdAndStatus`.
- `JpaTalkConversationRepositoryAdapter.java` -- Implements `TalkConversationRepository`. Maps between entities and domain objects.
- `TalkScenarioEntity.java` -- `@Table("talk_scenarios")`, `Persistable<UUID>`.
- `JpaTalkScenarioRepository.java` -- `CrudRepository<TalkScenarioEntity, UUID>` with `findByCefrLevel`.
- `JpaTalkScenarioRepositoryAdapter.java` -- Implements `TalkScenarioRepository`.

*Infrastructure - Controllers* (`src/main/java/com/faus535/englishtrainer/talk/infrastructure/controller/`):
- `ListTalkScenariosController.java` -- `@RestController`, `@GetMapping("/api/talk/scenarios")`. Query param: `level` (optional). Auth: JWT required. Returns `List<TalkScenarioResponse>`.
- `StartTalkConversationController.java` -- `@RestController`, `@PostMapping("/api/talk/conversations")`. Request body: `{scenarioId, level}`. Auth: JWT required, userId from token. Returns 201 + `TalkConversationResponse`.
- `SendTalkMessageController.java` -- `@RestController`, `@PostMapping("/api/talk/conversations/{id}/messages")`. Request body: `{content, confidence?}`. Auth: JWT + ownership check. Returns 200 + `TalkMessageResponse`.
- `EndTalkConversationController.java` -- `@RestController`, `@PostMapping("/api/talk/conversations/{id}/end")`. Auth: JWT + ownership check. Returns 200 + `TalkConversationSummaryResponse`.
- `GetTalkConversationSummaryController.java` -- `@RestController`, `@GetMapping("/api/talk/conversations/{id}/summary")`. Auth: JWT + ownership check. Returns 200 + `TalkConversationSummaryResponse`.
- `GetTalkStatsController.java` -- `@RestController`, `@GetMapping("/api/profiles/{userId}/talk/stats")`. Auth: JWT + `@RequireProfileOwnership`. Returns 200 + `TalkStatsResponse`.
- `SubmitTalkSpeechController.java` -- `@RestController`, `@PostMapping("/api/talk/conversations/{id}/speech")`. **Stub endpoint**: returns 501 Not Implemented with message "Speech analysis coming soon". Placeholder for future audio integration.
- `TalkControllerAdvice.java` -- Handles `TalkConversationNotFoundException` (404), `TalkConversationAlreadyEndedException` (409), `TalkMaxConversationsExceededException` (409), `TalkAiException` (503).

*Flyway migrations* (`src/main/resources/db/migration/`):
- `V10.0.0__talk_create_scenarios.sql`
- `V10.0.1__talk_seed_scenarios.sql`
- `V10.1.0__talk_create_conversations.sql`

*Tests* (`src/test/java/com/faus535/englishtrainer/talk/`):
- `domain/TalkConversationTest.java` -- Tests: start sets ACTIVE status, addMessage adds turn, addMessage throws on COMPLETED, end sets COMPLETED + registers event, recentMessages limits correctly, max 30 turns guard.
- `domain/TalkConversationMother.java` -- Factory: `active()`, `withMessages(int)`, `completed()`, `withCorrections(int)`.
- `domain/TalkScenarioMother.java` -- Factory: `coffeeShop()`, `jobInterview()`, `withLevel(String)`.
- `domain/TalkMessageMother.java` -- Factory: `userMessage()`, `assistantWithCorrection()`, `assistantWithoutCorrection()`.
- `application/StartTalkConversationUseCaseTest.java` -- Tests: starts conversation with scenario, throws when active conversation exists, greeting message is added as first turn.
- `application/SendTalkMessageUseCaseTest.java` -- Tests: adds user turn and AI response, includes correction when present, throws on completed conversation.
- `application/EndTalkConversationUseCaseTest.java` -- Tests: generates summary and evaluation, throws on already completed, publishes TalkConversationCompletedEvent.
- `application/ListTalkScenariosUseCaseTest.java` -- Tests: returns scenarios for level, returns all when no level.
- `infrastructure/InMemoryTalkConversationRepository.java` -- In-memory implementation.
- `infrastructure/InMemoryTalkScenarioRepository.java` -- In-memory implementation.
- `infrastructure/StubTalkAiPort.java` -- Stub returning canned responses with/without corrections.

**Details**:

1. Create Flyway migrations first (see Database Changes section for exact SQL).
2. Create domain layer: value objects (TalkConversationId, TalkMessageId, TalkScenarioId, TalkLevel, TalkStatus, TalkCorrection, TalkEvaluation, TalkStats, TalkMessage), then aggregate roots (TalkConversation, TalkScenario), then ports (TalkAiPort), then repositories, then events, then errors.
3. Create application layer: use cases with `@UseCase` annotation, constructor injection, checked exceptions.
4. Create infrastructure AI adapter: copy `AnthropicAiTutorAdapter` structure, adapt to use `TalkMessage`/`TalkCorrection` types. Preserve prompt caching headers. Preserve <<F>> feedback parsing. Adapt `SystemPromptBuilder` to inject scenario context.
5. Create persistence layer: entities with `Persistable<UUID>`, store TalkEvaluation and TalkCorrection as JSON strings in TEXT columns. Use `@MappedCollection` for talk_messages within TalkConversationEntity.
6. Create controllers: one per action, package-private, Jakarta Validation on request bodies (`@NotBlank`, `@NotNull`). All require JWT. Ownership checks via `@RequireProfileOwnership` on profile-scoped endpoints, manual check (load conversation, compare userId) on conversation-scoped endpoints.
7. Create TalkControllerAdvice for module-specific exception handling.
8. Create test doubles (InMemory repos, StubTalkAiPort, Object Mothers), then unit tests for domain and use cases.
9. Verify compilation: `./gradlew compileJava compileTestJava`.
10. Run tests: `./gradlew test`.

**Security considerations**:
- All endpoints require JWT authentication (already enforced by SecurityConfig's `.requestMatchers("/api/**").authenticated()`).
- `SendTalkMessageController` and `EndTalkConversationController` must verify `conversation.userId == token.profileId` before proceeding.
- `TalkMessage.role` is always set server-side ("user" for client input, "assistant" for AI response). Never accept role from request body.
- Do not log `TalkMessage.content` at INFO level (PII). Use DEBUG only.
- AI request/response bodies must not be logged at INFO level.

**Acceptance criteria**:
- [ ] `GET /api/talk/scenarios` returns seeded scenarios filtered by level
- [ ] `POST /api/talk/conversations` creates conversation and returns AI greeting
- [ ] `POST /api/talk/conversations/{id}/messages` returns AI response with corrections
- [ ] `POST /api/talk/conversations/{id}/end` returns summary with evaluation scores
- [ ] `GET /api/talk/conversations/{id}/summary` returns stored summary for completed conversation
- [ ] `GET /api/profiles/{userId}/talk/stats` returns aggregated talk statistics
- [ ] `POST /api/talk/conversations/{id}/speech` returns 501 stub
- [ ] Cannot send message to completed conversation (409)
- [ ] Cannot start second active conversation (409)
- [ ] Prompt caching header `anthropic-beta: prompt-caching-2024-07-31` is present in AI calls
- [ ] All unit tests pass
- [ ] `./gradlew compileJava compileTestJava test` succeeds

---

### Phase 2: Talk Frontend

**Goal**: Frontend implementation of the Talk experience. (Defined by frontend plan -- not in scope for this backend plan.)

**Acceptance criteria**:
- [ ] Frontend plan delivered separately

---

### Phase 3: Immerse Backend Core

**Goal**: Deliver the Immerse module end-to-end: content submission and AI processing, exercise generation, answer submission, vocabulary extraction, and history.

**Files to create**:

*Domain* (`src/main/java/com/faus535/englishtrainer/immerse/domain/`):
- `ImmerseContent.java` -- Aggregate Root extending `AggregateRoot<ImmerseContentId>`. Fields: id, userId (UUID), sourceUrl (String, nullable), title, rawText, processedText, cefrLevel (String), extractedVocabulary (List<VocabularyItem>), status (ImmerseContentStatus), createdAt. Methods: `submit()`, `reconstitute()`, `markProcessed()`, `markFailed()`.
- `ImmerseContentId.java` -- record with UUID value.
- `ImmerseContentStatus.java` -- `enum { PENDING, PROCESSED, FAILED }`
- `VocabularyItem.java` -- `record VocabularyItem(String word, String definition, String exampleSentence, String cefrLevel)`
- `ImmerseExercise.java` -- `record ImmerseExercise(ImmerseExerciseId id, ImmerseContentId contentId, ExerciseType exerciseType, String question, String correctAnswer, List<String> options, int orderIndex)`
- `ImmerseExerciseId.java` -- record with UUID value.
- `ExerciseType.java` -- `enum { FILL_THE_GAP, MULTIPLE_CHOICE, TRUE_FALSE, SENTENCE_REORDER, WORD_DEFINITION }`
- `ImmerseSubmission.java` -- Aggregate Root. Fields: id (ImmerseSubmissionId), exerciseId, userId, userAnswer, isCorrect, feedback (String), submittedAt. Publishes `ImmerseExerciseAnsweredEvent` when incorrect (feeds Review queue).
- `ImmerseSubmissionId.java` -- record with UUID value.
- `ImmerseAiPort.java` -- Interface: `processContent(String rawText, String level): ImmerseProcessResult`. Inner `record ImmerseProcessResult(String processedText, String detectedLevel, List<VocabularyItem> vocabulary, List<GeneratedExercise> exercises)`. Inner `record GeneratedExercise(String type, String question, String correctAnswer, List<String> options)`.
- `ImmerseContentRepository.java` -- Interface: `save(ImmerseContent)`, `findById(ImmerseContentId)`, `findByUserId(UUID, int page, int size)`.
- `ImmerseExerciseRepository.java` -- Interface: `saveAll(List<ImmerseExercise>)`, `findByContentId(ImmerseContentId)`, `findById(ImmerseExerciseId)`.
- `ImmerseSubmissionRepository.java` -- Interface: `save(ImmerseSubmission)`, `findByExerciseId(ImmerseExerciseId)`, `findByUserIdAndContentId(UUID, ImmerseContentId)`.
- `event/ImmerseExerciseAnsweredEvent.java` -- `record(UUID userId, ImmerseExerciseId exerciseId, String question, String correctAnswer, String userAnswer)`. Consumed by Review module to create review items from errors.
- `error/ImmerseException.java` -- abstract checked exception.
- `error/ImmerseContentNotFoundException.java`, `error/ImmerseExerciseNotFoundException.java`, `error/ImmerseAiException.java`, `error/ImmerseContentNotProcessedException.java`.

*Application* (`src/main/java/com/faus535/englishtrainer/immerse/application/`):
- `SubmitImmerseContentUseCase.java` -- `@UseCase`. Method: `execute(UUID userId, String sourceUrl, String title, String rawText, String level): ImmerseContent`. Validates input (rawText <= 10000 chars), creates PENDING content, calls `immerseAiPort.processContent()`, updates to PROCESSED with vocabulary + exercises, saves exercises. Returns processed content.
- `GetImmerseContentUseCase.java` -- `@UseCase`. Method: `execute(UUID contentId): ImmerseContent`.
- `GetImmerseExercisesUseCase.java` -- `@UseCase`. Method: `execute(UUID contentId): List<ImmerseExercise>`.
- `SubmitExerciseAnswerUseCase.java` -- `@UseCase`. Method: `execute(UUID userId, UUID exerciseId, String userAnswer): ImmerseSubmission`. Loads exercise, compares answer, creates ImmerseSubmission, publishes `ImmerseExerciseAnsweredEvent` if incorrect.
- `GetImmerseVocabularyUseCase.java` -- `@UseCase`. Method: `execute(UUID contentId): List<VocabularyItem>`. Loads content, returns extracted vocabulary.
- `GetImmerseHistoryUseCase.java` -- `@UseCase`. Method: `execute(UUID userId, int page, int size): PageResponse<ImmerseContent>`.

*Infrastructure - AI* (`src/main/java/com/faus535/englishtrainer/immerse/infrastructure/ai/`):
- `AnthropicImmerseAiAdapter.java` -- Implements `ImmerseAiPort`. Uses Claude tool-use pattern (adapted from `AnthropicExerciseGeneratorAdapter`). Defines `process_content` tool with schema for processedText, detectedLevel, vocabulary array, exercises array. Validates JSONB structure before returning.

*Infrastructure - Persistence* (`src/main/java/com/faus535/englishtrainer/immerse/infrastructure/persistence/`):
- `ImmerseContentEntity.java` -- `@Table("immerse_content")`, `Persistable<UUID>`. Stores `extractedVocabulary` as JSON TEXT.
- `ImmerseExerciseEntity.java` -- `@Table("immerse_exercises")`. Stores `options` as JSON TEXT.
- `ImmerseSubmissionEntity.java` -- `@Table("immerse_submissions")`.
- `JpaImmerseContentRepository.java` -- Spring Data JDBC CrudRepository with `findByUserId` (paginated).
- `JpaImmerseContentRepositoryAdapter.java`
- `JpaImmerseExerciseRepository.java` -- CrudRepository with `findByContentId`.
- `JpaImmerseExerciseRepositoryAdapter.java`
- `JpaImmerseSubmissionRepository.java` -- CrudRepository with `findByExerciseId`, `findByUserIdAndContentId`.
- `JpaImmerseSubmissionRepositoryAdapter.java`

*Infrastructure - Controllers* (`src/main/java/com/faus535/englishtrainer/immerse/infrastructure/controller/`):
- `SubmitImmerseContentController.java` -- `@PostMapping("/api/immerse/content")`. Request: `{sourceUrl?, title, rawText, level?}`. Auth: JWT, userId from token. Returns 201 + processed content.
- `GetImmerseContentController.java` -- `@GetMapping("/api/immerse/content/{id}")`. Auth: JWT + ownership. Returns 200.
- `GetImmerseExercisesController.java` -- `@GetMapping("/api/immerse/content/{id}/exercises")`. Auth: JWT + ownership. Returns 200.
- `SubmitExerciseAnswerController.java` -- `@PostMapping("/api/immerse/content/{contentId}/exercises/{exerciseId}/submit")`. Request: `{answer}`. Auth: JWT + ownership chain (content.userId == token). Returns 200 + submission result.
- `GetImmerseVocabularyController.java` -- `@GetMapping("/api/immerse/content/{id}/vocabulary")`. Auth: JWT + ownership. Returns 200.
- `GetImmerseHistoryController.java` -- `@GetMapping("/api/profiles/{userId}/immerse/history")`. Auth: JWT + `@RequireProfileOwnership`. Query params: `page`, `size`. Returns 200 + paginated history.
- `ImmerseControllerAdvice.java` -- Handles module exceptions: 404, 422 (not processed), 503 (AI error).

*Flyway migrations*:
- `V10.2.0__immerse_create_tables.sql`

*Tests* (`src/test/java/com/faus535/englishtrainer/immerse/`):
- `domain/ImmerseContentTest.java` -- Tests: submit creates PENDING, markProcessed sets status and vocabulary, markFailed sets FAILED status.
- `domain/ImmerseContentMother.java` -- Factory: `pending()`, `processed()`, `withExercises()`, `withVocabulary()`.
- `domain/ImmerseExerciseMother.java` -- Factory: `multipleChoice()`, `fillTheGap()`, `trueFalse()`.
- `application/SubmitImmerseContentUseCaseTest.java` -- Tests: processes content via AI, rejects text over 10000 chars, handles AI failure gracefully.
- `application/SubmitExerciseAnswerUseCaseTest.java` -- Tests: correct answer marks isCorrect=true, incorrect answer publishes event, exercise not found throws.
- `infrastructure/InMemoryImmerseContentRepository.java`
- `infrastructure/InMemoryImmerseExerciseRepository.java`
- `infrastructure/InMemoryImmerseSubmissionRepository.java`
- `infrastructure/StubImmerseAiPort.java`

**Details**:

1. Create Flyway migration (see Database Changes section).
2. Build domain layer: VOs, aggregates, ports, repositories, events, errors.
3. Build application layer: use cases.
4. Build AI adapter: Adapted from `AnthropicExerciseGeneratorAdapter` pattern. Uses Claude `tool_choice: {type: "tool", name: "process_content"}` to get structured JSON. Validates response structure before persisting (guard against JSONB injection from AI output).
5. Build persistence layer: entities, Spring Data JDBC repositories, adapter classes.
6. Build controllers: one per action. Ownership check on content-scoped endpoints: load content, verify `content.userId == token.profileId`. For exercise submission, verify ownership via the content parent.
7. Build tests with Object Mothers, in-memory repos, stub AI port.
8. Verify compilation and run tests.

**Security considerations**:
- `sourceUrl` SSRF risk: validate that URL starts with `https://`, reject private IP ranges. For Phase 3, sourceUrl is informational only (user pastes raw text). URL fetching deferred to a future phase.
- JSONB validation: after AI returns processed content, validate that vocabulary items have required fields before persisting.
- Content size limit: reject rawText > 10000 characters at controller level with `@Size` validation.
- Two-level ownership chain for exercise submissions: content -> exercise -> submission all must belong to same user.

**Acceptance criteria**:
- [ ] `POST /api/immerse/content` processes text and returns content with vocabulary + exercises
- [ ] `GET /api/immerse/content/{id}` returns processed content
- [ ] `GET /api/immerse/content/{id}/exercises` returns generated exercises
- [ ] `POST /api/immerse/content/{contentId}/exercises/{exerciseId}/submit` evaluates answer correctly
- [ ] `GET /api/immerse/content/{id}/vocabulary` returns extracted vocabulary
- [ ] `GET /api/profiles/{userId}/immerse/history` returns paginated history
- [ ] Incorrect exercise answers publish `ImmerseExerciseAnsweredEvent`
- [ ] Content over 10000 chars rejected with 400
- [ ] All unit tests pass
- [ ] `./gradlew compileJava compileTestJava test` succeeds

---

### Phase 4: Immerse Frontend

**Goal**: Frontend implementation of the Immerse experience. (Defined by frontend plan -- not in scope for this backend plan.)

**Acceptance criteria**:
- [ ] Frontend plan delivered separately

---

### Phase 5: Review Backend + Frontend

**Goal**: Deliver the Review module: unified spaced-repetition queue fed by errors from Talk and Immerse, with proper SM-2 algorithm, plus event listeners that auto-create review items.

**Files to create**:

*Domain* (`src/main/java/com/faus535/englishtrainer/review/domain/`):
- `ReviewItem.java` -- Aggregate Root. Fields: id (ReviewItemId), userId (UUID), sourceType (ReviewSourceType), sourceId (UUID), frontContent (String), backContent (String), nextReviewAt (Instant), intervalDays (int, default 1), easeFactor (double, default 2.5), consecutiveCorrect (int, default 0), createdAt. Methods: `create()`, `reconstitute()`, `review(int quality): ReviewItem` (SM-2 algorithm). Publishes `ReviewCompletedEvent`.
- `ReviewItemId.java` -- record with UUID value.
- `ReviewSourceType.java` -- `enum { TALK_ERROR, IMMERSE_VOCAB, PRONUNCIATION }`
- `ReviewResult.java` -- `record ReviewResult(ReviewResultId id, ReviewItemId itemId, int quality, Instant reviewedAt)`. Quality 0-5 per SM-2.
- `ReviewResultId.java` -- record with UUID value.
- `ReviewStats.java` -- `record ReviewStats(int totalItems, int dueToday, int completedToday, int streak)`
- `ReviewItemRepository.java` -- Interface: `save(ReviewItem)`, `findById(ReviewItemId)`, `findDueByUserId(UUID, Instant now, int limit)`, `findByUserIdSourceTypeAndSourceId(UUID, ReviewSourceType, UUID)`, `countByUserId(UUID)`, `countDueByUserId(UUID, Instant now)`.
- `ReviewResultRepository.java` -- Interface: `save(ReviewResult)`, `countByUserIdAndReviewedAtAfter(UUID, Instant)`.
- `event/ReviewCompletedEvent.java` -- `record(ReviewItemId itemId, UUID userId, boolean graduated)`.
- `error/ReviewException.java` -- abstract checked exception.
- `error/ReviewItemNotFoundException.java`.

SM-2 algorithm in `ReviewItem.review(int quality)`:
```
if quality >= 3:
  if consecutiveCorrect == 0: intervalDays = 1
  else if consecutiveCorrect == 1: intervalDays = 6
  else: intervalDays = round(intervalDays * easeFactor)
  consecutiveCorrect++
else:
  consecutiveCorrect = 0
  intervalDays = 1
easeFactor = max(1.3, easeFactor + (0.1 - (5 - quality) * (0.08 + (5 - quality) * 0.02)))
nextReviewAt = now + intervalDays days
```

*Application* (`src/main/java/com/faus535/englishtrainer/review/application/`):
- `GetReviewQueueUseCase.java` -- `@UseCase`. Method: `execute(UUID userId, int limit): List<ReviewItem>`. Returns items where `nextReviewAt <= now`, sorted by most overdue first.
- `SubmitReviewResultUseCase.java` -- `@UseCase`. Method: `execute(UUID userId, UUID itemId, int quality): ReviewItem`. Loads item, calls `item.review(quality)`, saves ReviewResult, saves updated item.
- `GetReviewStatsUseCase.java` -- `@UseCase`. Method: `execute(UUID userId): ReviewStats`.
- `CreateReviewItemFromTalkUseCase.java` -- `@UseCase`. Method: `execute(UUID userId, TalkConversationCompletedEvent event)`. Extracts corrections from event, creates ReviewItem per error with sourceType=TALK_ERROR.
- `CreateReviewItemFromImmerseUseCase.java` -- `@UseCase`. Method: `execute(UUID userId, ImmerseExerciseAnsweredEvent event)`. Creates ReviewItem with sourceType=IMMERSE_VOCAB, front=question, back=correctAnswer.

*Infrastructure - Event Listeners* (`src/main/java/com/faus535/englishtrainer/review/infrastructure/event/`):
- `TalkCompletedReviewListener.java` -- `@Component` with `@TransactionalEventListener`. Listens for `TalkConversationCompletedEvent`, delegates to `CreateReviewItemFromTalkUseCase`. Uses `@Transactional(propagation = Propagation.REQUIRES_NEW)`.
- `ImmerseAnsweredReviewListener.java` -- `@Component` with `@TransactionalEventListener`. Listens for `ImmerseExerciseAnsweredEvent`, delegates to `CreateReviewItemFromImmerseUseCase`. Uses `@Transactional(propagation = Propagation.REQUIRES_NEW)`.

*Infrastructure - Persistence* (`src/main/java/com/faus535/englishtrainer/review/infrastructure/persistence/`):
- `ReviewItemEntity.java` -- `@Table("review_items")`, `Persistable<UUID>`.
- `ReviewResultEntity.java` -- `@Table("review_results")`.
- `JpaReviewItemRepository.java` -- CrudRepository with `findByUserIdAndNextReviewAtBeforeOrderByNextReviewAt`, `findByUserIdAndSourceTypeAndSourceId`.
- `JpaReviewItemRepositoryAdapter.java`
- `JpaReviewResultRepository.java` -- CrudRepository with `countByUserIdAndReviewedAtAfter`.
- `JpaReviewResultRepositoryAdapter.java`

*Infrastructure - Controllers* (`src/main/java/com/faus535/englishtrainer/review/infrastructure/controller/`):
- `GetReviewQueueController.java` -- `@GetMapping("/api/profiles/{userId}/review/queue")`. Query param: `limit` (default 10). Auth: JWT + `@RequireProfileOwnership`. Returns 200 + list of review items.
- `SubmitReviewResultController.java` -- `@PostMapping("/api/profiles/{userId}/review/items/{itemId}/result")`. Request: `{quality}` (0-5). Auth: JWT + `@RequireProfileOwnership`. Returns 200 + updated item.
- `GetReviewStatsController.java` -- `@GetMapping("/api/profiles/{userId}/review/stats")`. Auth: JWT + `@RequireProfileOwnership`. Returns 200.
- `ReviewControllerAdvice.java` -- Handles `ReviewItemNotFoundException` (404).

*Flyway migration*:
- `V10.3.0__review_create_tables.sql`

*Tests* (`src/test/java/com/faus535/englishtrainer/review/`):
- `domain/ReviewItemTest.java` -- Tests: SM-2 correct response increases interval, SM-2 incorrect resets to 1, ease factor never below 1.3, quality 3 is threshold, consecutive correct tracking, graduated after multiple reviews.
- `domain/ReviewItemMother.java` -- Factory: `dueToday()`, `notDue()`, `fromTalkError()`, `fromImmerseVocab()`, `withEaseFactor(double)`.
- `application/GetReviewQueueUseCaseTest.java` -- Tests: returns due items sorted, excludes not-due items, respects limit.
- `application/SubmitReviewResultUseCaseTest.java` -- Tests: updates item schedule, saves result record.
- `application/CreateReviewItemFromTalkUseCaseTest.java` -- Tests: creates items from corrections, skips duplicates.
- `application/CreateReviewItemFromImmerseUseCaseTest.java` -- Tests: creates item from incorrect answer.
- `infrastructure/InMemoryReviewItemRepository.java`
- `infrastructure/InMemoryReviewResultRepository.java`
- `infrastructure/event/TalkCompletedReviewListenerTest.java` -- Tests: event creates review items.
- `infrastructure/event/ImmerseAnsweredReviewListenerTest.java` -- Tests: event creates review item.

**Details**:

1. Create Flyway migration.
2. Build domain layer with SM-2 algorithm implemented in `ReviewItem.review()`.
3. Build application layer: queue, submit, stats, and two event-driven creation use cases.
4. Build event listeners using `@TransactionalEventListener` + `Propagation.REQUIRES_NEW` (existing pattern in codebase).
5. Build persistence + controllers.
6. Build tests. SM-2 algorithm tests are critical -- verify interval calculations with known inputs.
7. Verify compilation and run ALL tests (including Talk and Immerse tests to ensure no regressions).

**Security considerations**:
- Review items always filtered by `userId` from JWT token. No IDOR risk.
- `quality` parameter validated: `@Min(0) @Max(5)`.
- Review queue limit capped server-side (max 50) to prevent abuse.

**Acceptance criteria**:
- [ ] `GET /api/profiles/{userId}/review/queue` returns due items sorted by overdueness
- [ ] `POST /api/profiles/{userId}/review/items/{id}/result` updates SM-2 schedule
- [ ] `GET /api/profiles/{userId}/review/stats` returns correct counts
- [ ] Completing a Talk conversation auto-creates review items from corrections
- [ ] Answering an Immerse exercise incorrectly auto-creates a review item
- [ ] SM-2 algorithm: quality >= 3 increases interval, quality < 3 resets to 1
- [ ] Ease factor never drops below 1.3
- [ ] Duplicate review items (same source) are not created
- [ ] Empty review queue returns empty list (not error)
- [ ] All unit tests pass
- [ ] `./gradlew compileJava compileTestJava test` succeeds

---

### Phase 6: Home, Cleanup, and Validation

**Goal**: Deliver the Home aggregation endpoint, archive obsolete modules (keeping Flyway migrations), update gamification/activity listeners for new events, and run `/revisar` for full architecture validation.

**Files to create**:

*Home module* (`src/main/java/com/faus535/englishtrainer/home/`):
- `application/GetHomeUseCase.java` -- `@UseCase`. Method: `execute(UUID userId): HomeData`. Queries TalkConversationRepository (active count, last conversation), ReviewItemRepository (due count), ImmerseContentRepository (recent count). Returns `HomeData` record.
- `application/HomeData.java` -- `record HomeData(String suggestedAction, TalkSummary talk, ImmerseSummary immerse, ReviewSummary review)`. Inner records for each section's summary stats.
- `infrastructure/controller/GetHomeController.java` -- `@GetMapping("/api/profiles/{userId}/home")`. Auth: JWT + `@RequireProfileOwnership`. Returns 200.

**Files to modify**:

*Gamification event listeners* (`src/main/java/com/faus535/englishtrainer/gamification/`):
- Add `TalkCompletedGamificationListener.java` -- Listens for `TalkConversationCompletedEvent`, grants XP.
- Add `ImmerseAnsweredGamificationListener.java` -- Listens for `ImmerseExerciseAnsweredEvent`, grants XP for correct answers.
- Add `ReviewCompletedGamificationListener.java` -- Listens for `ReviewCompletedEvent`, grants XP.

*Activity event listeners* (`src/main/java/com/faus535/englishtrainer/activity/`):
- Add `TalkCompletedActivityListener.java` -- Records activity for completed talk conversations.
- Add `ImmerseSubmittedActivityListener.java` -- Records activity for content submission.
- Add `ReviewCompletedActivityListener.java` -- Records activity for review sessions.

**Modules to archive** (delete source code, keep Flyway migrations intact):
- `admin` -- functionality not needed in new design
- `minigame` -- replaced by Talk/Immerse
- `dailychallenge` -- replaced by Review queue
- `curriculum` -- replaced by scenario-based Talk
- `moduleprogress` -- replaced by per-module stats
- `session` -- replaced by Talk conversations
- `assessment` -- deferred; can be re-added later
- `learningpath` -- replaced by home suggested action
- `phonetics` -- pronunciation feedback integrated into Talk
- `minimalpair` -- absorbed into Talk pronunciation
- `phrase` -- absorbed into Immerse content
- `vocabulary` -- absorbed into Immerse vocabulary extraction
- `reading` -- absorbed into Immerse
- `writing` -- absorbed into Immerse

**Modules to archive carefully** (have cross-module dependencies):
- `conversation` -- predecessor to Talk. Archive AFTER verifying no remaining imports.
- `exercise` -- predecessor to Immerse exercises. Archive AFTER verifying no remaining imports.
- `spacedrepetition` -- predecessor to Review. Archive AFTER verifying vocabulary.WordLearnedListener and learningpath imports are gone (they are archived too).
- `tutorerror` -- merged into Review.
- `errorpattern` -- merged into Review.
- `pronunciation` -- merged into Talk.

**Archive procedure per module**:
1. `git rm -r src/main/java/com/faus535/englishtrainer/<module>/`
2. `git rm -r src/test/java/com/faus535/englishtrainer/<module>/` (if tests exist)
3. Do NOT delete `src/main/resources/db/migration/V*__<module>_*` files -- Flyway needs them for checksum validation.
4. After all deletions: `./gradlew compileJava` to verify no compilation breaks.

**Details**:

1. Create Home module (use case + controller).
2. Create new event listeners in gamification and activity modules.
3. Archive modules in dependency order:
   - First wave (no dependents): admin, minigame, dailychallenge, curriculum, moduleprogress, session, assessment, phrase, reading, writing, phonetics, minimalpair, pronunciation, tutorerror, errorpattern
   - Second wave (after first wave removes their dependents): vocabulary (depends on spacedrepetition, but WordLearnedListener is gone now)
   - Third wave: learningpath, spacedrepetition (learningpath's dependency on spacedrepetition gone)
   - Final wave: conversation, exercise (predecessors fully replaced)
4. After each wave, run `./gradlew compileJava` to catch breaks.
5. Run full test suite: `./gradlew test`.
6. Run `/revisar` for architecture validation.

**Acceptance criteria**:
- [ ] `GET /api/profiles/{userId}/home` returns suggested action and progress summaries
- [ ] Gamification XP is granted for Talk, Immerse, and Review events
- [ ] Activity is recorded for Talk, Immerse, and Review events
- [ ] All archived module source code is removed
- [ ] All Flyway migration files are preserved
- [ ] `./gradlew compileJava compileTestJava` succeeds with zero compilation errors
- [ ] `./gradlew test` passes all remaining tests
- [ ] `/revisar` validates architecture with no critical issues

---

## API Contract

### `GET /api/talk/scenarios`
- **Query params**: `level` (optional, CEFR: a1-c2)
- **Response body**: `[{"id": "uuid", "title": "Coffee Shop Chat", "description": "...", "category": "daily_life", "cefrLevel": "a2", "difficultyOrder": 1}]`
- **Status codes**: 200 OK, 401 Unauthorized
- **Auth**: JWT required

### `POST /api/talk/conversations`
- **Request body**: `{"scenarioId": "uuid", "level": "b1"}`
- **Response body**: `{"id": "uuid", "scenarioId": "uuid", "level": "b1", "status": "ACTIVE", "greeting": {"content": "Hi! Welcome to...", "correction": null}, "startedAt": "2026-04-05T10:00:00Z"}`
- **Status codes**: 201 Created, 409 Conflict (active conversation exists), 401 Unauthorized
- **Auth**: JWT required, userId from token

### `POST /api/talk/conversations/{id}/messages`
- **Request body**: `{"content": "I want to order a coffee please", "confidence": 0.85}`
- **Response body**: `{"message": {"id": "uuid", "role": "assistant", "content": "Great choice! What kind of coffee...", "correction": {"grammarFixes": ["'I want to order' -> 'I'd like to order' (more polite)"], "vocabularySuggestions": ["latte", "espresso"], "pronunciationTips": [], "encouragement": "Good job using complete sentences!"}, "createdAt": "..."}}`
- **Status codes**: 200 OK, 404 Not Found, 409 Conflict (conversation ended), 503 Service Unavailable (AI error)
- **Auth**: JWT required, ownership verified

### `POST /api/talk/conversations/{id}/end`
- **Request body**: none
- **Response body**: `{"summary": "You practiced ordering coffee...", "evaluation": {"grammarAccuracy": 75, "vocabularyRange": 60, "fluency": 80, "taskCompletion": 90, "overallScore": 76, "levelDemonstrated": "b1", "strengths": ["Good sentence structure"], "areasToImprove": ["Use polite forms"]}, "turnCount": 12, "errorCount": 3}`
- **Status codes**: 200 OK, 404 Not Found, 409 Conflict (already ended), 503 AI error
- **Auth**: JWT required, ownership verified

### `POST /api/talk/conversations/{id}/speech`
- **Request body**: `multipart/form-data` with audio file (stub)
- **Response body**: `{"message": "Speech analysis coming soon"}`
- **Status codes**: 501 Not Implemented
- **Auth**: JWT required

### `GET /api/talk/conversations/{id}/summary`
- **Response body**: same as POST .../end response
- **Status codes**: 200 OK, 404 Not Found, 422 Unprocessable (conversation not completed)
- **Auth**: JWT required, ownership verified

### `GET /api/profiles/{userId}/talk/stats`
- **Response body**: `{"totalConversations": 15, "completedConversations": 12, "totalMessages": 180, "averageScore": 72.5}`
- **Status codes**: 200 OK, 401, 403
- **Auth**: JWT + @RequireProfileOwnership

### `POST /api/immerse/content`
- **Request body**: `{"sourceUrl": "https://example.com/article", "title": "Climate Change Article", "rawText": "The effects of climate change...", "level": "b1"}`
- **Response body**: `{"id": "uuid", "title": "...", "processedText": "...", "cefrLevel": "b1", "extractedVocabulary": [{"word": "drought", "definition": "...", "exampleSentence": "...", "cefrLevel": "b2"}], "exerciseCount": 5, "status": "PROCESSED", "createdAt": "..."}`
- **Status codes**: 201 Created, 400 Bad Request (text too long), 503 AI error
- **Auth**: JWT required, userId from token

### `GET /api/immerse/content/{id}`
- **Response body**: full content object
- **Status codes**: 200 OK, 404 Not Found
- **Auth**: JWT + ownership

### `GET /api/immerse/content/{id}/exercises`
- **Response body**: `[{"id": "uuid", "exerciseType": "MULTIPLE_CHOICE", "question": "What does 'drought' mean?", "options": ["A long period without rain", "A type of storm", "A flood"], "orderIndex": 0}]`
- **Status codes**: 200 OK, 404 Not Found, 422 (content not processed)
- **Auth**: JWT + ownership

### `POST /api/immerse/content/{contentId}/exercises/{exerciseId}/submit`
- **Request body**: `{"answer": "A long period without rain"}`
- **Response body**: `{"id": "uuid", "isCorrect": true, "correctAnswer": "A long period without rain", "feedback": "Correct! A drought is..."}`
- **Status codes**: 200 OK, 404 Not Found
- **Auth**: JWT + ownership chain

### `GET /api/immerse/content/{id}/vocabulary`
- **Response body**: `[{"word": "drought", "definition": "...", "exampleSentence": "...", "cefrLevel": "b2"}]`
- **Status codes**: 200 OK, 404 Not Found
- **Auth**: JWT + ownership

### `GET /api/profiles/{userId}/immerse/history`
- **Query params**: `page` (default 0), `size` (default 10)
- **Response body**: `{"content": [...], "page": 0, "size": 10, "totalElements": 25, "totalPages": 3}`
- **Status codes**: 200 OK, 401, 403
- **Auth**: JWT + @RequireProfileOwnership

### `GET /api/profiles/{userId}/review/queue`
- **Query params**: `limit` (default 10, max 50)
- **Response body**: `[{"id": "uuid", "sourceType": "TALK_ERROR", "frontContent": "I want to order a coffee", "backContent": "I'd like to order a coffee (use polite form)", "nextReviewAt": "...", "intervalDays": 1, "consecutiveCorrect": 0}]`
- **Status codes**: 200 OK, 401, 403
- **Auth**: JWT + @RequireProfileOwnership

### `POST /api/profiles/{userId}/review/items/{itemId}/result`
- **Request body**: `{"quality": 4}`
- **Response body**: `{"id": "uuid", "nextReviewAt": "2026-04-11T00:00:00Z", "intervalDays": 6, "easeFactor": 2.5, "consecutiveCorrect": 2}`
- **Status codes**: 200 OK, 404 Not Found, 400 (quality not 0-5)
- **Auth**: JWT + @RequireProfileOwnership

### `GET /api/profiles/{userId}/review/stats`
- **Response body**: `{"totalItems": 45, "dueToday": 8, "completedToday": 3, "streak": 5}`
- **Status codes**: 200 OK, 401, 403
- **Auth**: JWT + @RequireProfileOwnership

### `GET /api/profiles/{userId}/home`
- **Response body**: `{"suggestedAction": "REVIEW", "talk": {"activeConversation": null, "totalCompleted": 12, "lastScore": 76}, "immerse": {"recentContentCount": 3, "lastContentTitle": "Climate Change"}, "review": {"dueToday": 8, "streak": 5}}`
- **Status codes**: 200 OK, 401, 403
- **Auth**: JWT + @RequireProfileOwnership

## Database Changes

### V10.0.0__talk_create_scenarios.sql

```sql
CREATE TABLE talk_scenarios (
    id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    title       VARCHAR(200) NOT NULL,
    description TEXT,
    context_prompt TEXT NOT NULL,
    category    VARCHAR(50) NOT NULL,
    cefr_level  VARCHAR(5) NOT NULL CHECK (cefr_level IN ('a1','a2','b1','b2','c1','c2')),
    difficulty_order INTEGER NOT NULL DEFAULT 0,
    created_at  TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT now()
);

CREATE INDEX idx_talk_scenarios_cefr_level ON talk_scenarios(cefr_level);
```

### V10.0.1__talk_seed_scenarios.sql

```sql
INSERT INTO talk_scenarios (id, title, description, context_prompt, category, cefr_level, difficulty_order) VALUES
(gen_random_uuid(), 'Coffee Shop Order', 'Practice ordering drinks and snacks at a coffee shop.', 'You are a friendly barista at a cozy coffee shop. The student is a customer. Help them practice ordering drinks, asking about options, and making small talk. Use natural, casual English appropriate for the setting.', 'daily_life', 'a2', 1),
(gen_random_uuid(), 'Job Interview', 'Practice answering common job interview questions.', 'You are a hiring manager conducting a job interview for a marketing position. Ask the student common interview questions, follow up on their answers, and give them a realistic interview experience. Be professional but encouraging.', 'professional', 'b1', 2),
(gen_random_uuid(), 'Debate Club', 'Discuss and debate current topics with structured arguments.', 'You are a debate partner. Present a position on the given topic and engage in structured debate. Challenge the student to use complex arguments, counterpoints, and formal language. Expect and encourage sophisticated vocabulary and grammar.', 'academic', 'b2', 3),
(gen_random_uuid(), 'Hotel Check-In', 'Practice checking into a hotel and making requests.', 'You are a hotel receptionist. Help the student check in, explain room options, and handle special requests. Use clear, service-oriented English.', 'travel', 'a2', 4),
(gen_random_uuid(), 'Doctor Appointment', 'Practice describing symptoms and understanding medical advice.', 'You are a general practitioner doctor. The student is a patient describing their symptoms. Ask follow-up questions, explain a diagnosis in simple terms, and give advice. Use clear medical English appropriate for patient communication.', 'health', 'b1', 5),
(gen_random_uuid(), 'News Discussion', 'Analyze and discuss a recent news article.', 'You are a discussion partner for current events. Present a summary of a recent news topic, ask the student for their opinion, and explore different perspectives. Encourage use of opinion expressions, conditionals, and complex sentence structures.', 'media', 'b2', 6);
```

### V10.1.0__talk_create_conversations.sql

```sql
CREATE TABLE talk_conversations (
    id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id     UUID NOT NULL REFERENCES user_profiles(id) ON DELETE CASCADE,
    scenario_id UUID REFERENCES talk_scenarios(id) ON DELETE SET NULL,
    level       VARCHAR(5) NOT NULL CHECK (level IN ('a1','a2','b1','b2','c1','c2')),
    status      VARCHAR(20) NOT NULL DEFAULT 'ACTIVE' CHECK (status IN ('ACTIVE','COMPLETED')),
    summary     TEXT,
    evaluation_json TEXT,
    started_at  TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT now(),
    ended_at    TIMESTAMP WITH TIME ZONE,
    version     BIGINT NOT NULL DEFAULT 0
);

CREATE INDEX idx_talk_conversations_user_id ON talk_conversations(user_id);
CREATE INDEX idx_talk_conversations_user_status ON talk_conversations(user_id, status);

CREATE TABLE talk_messages (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    conversation_id UUID NOT NULL REFERENCES talk_conversations(id) ON DELETE CASCADE,
    role            VARCHAR(10) NOT NULL CHECK (role IN ('user','assistant')),
    content         TEXT NOT NULL,
    correction_json TEXT,
    created_at      TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT now()
);

CREATE INDEX idx_talk_messages_conversation_id ON talk_messages(conversation_id);
CREATE INDEX idx_talk_messages_conversation_created ON talk_messages(conversation_id, created_at);
```

### V10.2.0__immerse_create_tables.sql

```sql
CREATE TABLE immerse_content (
    id                   UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id              UUID NOT NULL REFERENCES user_profiles(id) ON DELETE CASCADE,
    source_url           TEXT,
    title                VARCHAR(500) NOT NULL,
    raw_text             TEXT NOT NULL,
    processed_text       TEXT,
    cefr_level           VARCHAR(5) CHECK (cefr_level IN ('a1','a2','b1','b2','c1','c2')),
    extracted_vocabulary TEXT,
    status               VARCHAR(20) NOT NULL DEFAULT 'PENDING' CHECK (status IN ('PENDING','PROCESSED','FAILED')),
    created_at           TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT now(),
    version              BIGINT NOT NULL DEFAULT 0
);

CREATE INDEX idx_immerse_content_user_id ON immerse_content(user_id);
CREATE INDEX idx_immerse_content_user_created ON immerse_content(user_id, created_at DESC);

CREATE TABLE immerse_exercises (
    id            UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    content_id    UUID NOT NULL REFERENCES immerse_content(id) ON DELETE CASCADE,
    exercise_type VARCHAR(50) NOT NULL,
    question      TEXT NOT NULL,
    correct_answer TEXT NOT NULL,
    options       TEXT,
    order_index   INTEGER NOT NULL DEFAULT 0
);

CREATE INDEX idx_immerse_exercises_content_id ON immerse_exercises(content_id);

CREATE TABLE immerse_submissions (
    id           UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    exercise_id  UUID NOT NULL REFERENCES immerse_exercises(id) ON DELETE CASCADE,
    user_id      UUID NOT NULL REFERENCES user_profiles(id) ON DELETE CASCADE,
    user_answer  TEXT NOT NULL,
    is_correct   BOOLEAN NOT NULL,
    feedback     TEXT,
    submitted_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT now()
);

CREATE INDEX idx_immerse_submissions_exercise ON immerse_submissions(exercise_id);
CREATE INDEX idx_immerse_submissions_user ON immerse_submissions(user_id);
```

### V10.3.0__review_create_tables.sql

```sql
CREATE TABLE review_items (
    id                  UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id             UUID NOT NULL REFERENCES user_profiles(id) ON DELETE CASCADE,
    source_type         VARCHAR(30) NOT NULL CHECK (source_type IN ('TALK_ERROR','IMMERSE_VOCAB','PRONUNCIATION')),
    source_id           UUID NOT NULL,
    front_content       TEXT NOT NULL,
    back_content        TEXT NOT NULL,
    next_review_at      TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT now(),
    interval_days       INTEGER NOT NULL DEFAULT 1 CHECK (interval_days >= 1),
    ease_factor         NUMERIC(4,2) NOT NULL DEFAULT 2.50 CHECK (ease_factor >= 1.30),
    consecutive_correct INTEGER NOT NULL DEFAULT 0 CHECK (consecutive_correct >= 0),
    created_at          TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT now(),
    version             BIGINT NOT NULL DEFAULT 0,
    UNIQUE(user_id, source_type, source_id)
);

CREATE INDEX idx_review_items_user_due ON review_items(user_id, next_review_at);

CREATE TABLE review_results (
    id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    item_id     UUID NOT NULL REFERENCES review_items(id) ON DELETE CASCADE,
    quality     INTEGER NOT NULL CHECK (quality BETWEEN 0 AND 5),
    reviewed_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT now()
);

CREATE INDEX idx_review_results_item ON review_results(item_id);
CREATE INDEX idx_review_results_user_date ON review_results(
    (SELECT user_id FROM review_items WHERE review_items.id = item_id),
    reviewed_at
);
```

**Note**: The last index on `review_results` uses a subquery which may not work in all PostgreSQL versions. Replace with a simpler approach if needed -- add `user_id` column to `review_results` denormalized, or query via JOIN.

Corrected alternative for the last index:

```sql
-- Drop the subquery index and instead add a denormalized user_id for efficient stats queries
ALTER TABLE review_results ADD COLUMN user_id UUID REFERENCES user_profiles(id) ON DELETE CASCADE;
CREATE INDEX idx_review_results_user_date ON review_results(user_id, reviewed_at);
```

## Testing Strategy

**Approach**: Unit tests with in-memory doubles for all use cases. Integration tests deferred to when Testcontainers infrastructure is set up. Domain aggregate tests for invariant validation.

**Test doubles per module**:

| Module | InMemory Repos | Stubs | Object Mothers |
|--------|---------------|-------|----------------|
| Talk | InMemoryTalkConversationRepository, InMemoryTalkScenarioRepository | StubTalkAiPort | TalkConversationMother, TalkScenarioMother, TalkMessageMother |
| Immerse | InMemoryImmerseContentRepository, InMemoryImmerseExerciseRepository, InMemoryImmerseSubmissionRepository | StubImmerseAiPort | ImmerseContentMother, ImmerseExerciseMother |
| Review | InMemoryReviewItemRepository, InMemoryReviewResultRepository | (none) | ReviewItemMother |

**Critical test paths** (ranked by risk):
1. SM-2 algorithm correctness (ReviewItem.review) -- math bugs cause wrong scheduling
2. Talk AI feedback parsing (<<F>> pattern) -- broken parsing loses all corrections
3. Talk conversation lifecycle (start -> message -> end -> event) -- cross-module event chain
4. Immerse exercise answer evaluation + event publishing -- feeds Review queue
5. Review event listeners creating items from Talk/Immerse events -- integration point
6. Ownership checks on all endpoints -- security critical

**Coverage target**: 100% of use cases, 100% of domain aggregate methods, all error paths.
