# Backend Plan: Article Module — AI News-Style Reader

> Generated: 2026-04-06
> Request: New `article` module — AI generates 500-700 word English news-style article via Claude (topic + level selector B1/B2/C1), alternating AI/user paragraph reading flow, word/phrase marking with Claude contextual translation, marked words auto-saved as ReviewItems with new source_type ARTICLE, after reading completes: Claude generates 5-7 comprehension questions one at a time, 40 word minimum per answer, help gives AI-generated tip/hint stored at generation time, Claude grades grammar/style/content correctness. New backend module named `article` separate from `immerse`.

---

## Decisions Log

| # | Topic | Decision | Alternatives Considered | Why |
|---|-------|----------|------------------------|-----|
| 1 | Base package | `com.faus535.englishtrainer.article` | `com.s2grupo.carmen.englishtrainer.article` (CLAUDE.md mentions this but actual codebase uses `com.faus535`) | Match the real codebase package, not the template placeholder |
| 2 | Migration version series | Start at `V11.0.0` | Continue from V10.x | V10.x series is occupied; V11 is the next clean major series |
| 3 | Marked words migration | `V11.1.0__review_add_article_source_type.sql` includes both the `article_marked_words` table AND the review_items constraint update | Two separate files | They belong to the same phase; atomic migration reduces risk |
| 4 | Questions tables placement | Included in `V11.0.0` (single migration for all article tables) | Split per phase | Simpler schema management; all article tables have a clear dependency on `article_readings`; splitting risks FK issues in test environments with partial migrations |
| 5 | `GetQuestionHintUseCase` | Returns pre-stored `hint_text` from DB — no Claude call at retrieval time | Call Claude on demand | Approved contract; avoids latency and cost on hint retrieval |
| 6 | `GenerateQuestionsUseCase` | Auto-invoked by `CompleteArticleUseCase` — not a public endpoint | Separate POST endpoint | Questions are generated exactly once on completion; no need for external trigger |
| 7 | Ownership check strategy | Use-case-level: fetch entity, assert `entity.userId().equals(userId)`, throw `ArticleAccessDeniedException` | `@RequireProfileOwnership` aspect | Aspect only works on `userId`-keyed routes; article routes are keyed by `articleId` |
| 8 | Duplicate marked word handling | UNIQUE INDEX + catch `DataIntegrityViolationException` in adapter → throw `DuplicateMarkedWordException` | Domain pre-check | DB constraint is the definitive guard; pre-check adds a round-trip and is still race-prone |
| 9 | Re-answering a question | UNIQUE INDEX on `article_question_answers(article_question_id)` + catch in adapter → `QuestionAlreadyAnsweredException` | Domain flag | Same rationale as above |
| 10 | Word count validation | `ArticleQuestionAnswer.create()` throws `AnswerTooShortException` when word count < 40 | Controller-level validation | Domain invariant — must be enforced before any Claude call |
| 11 | `topic` max length in DB | `VARCHAR(100)` | `VARCHAR(200)` (analyst) | Quality analyst specified 100; `@Size(max=100)` in controller matches; more conservative is better |

---

## Analysis

**Existing code relevant to this feature:**

- `com.faus535.englishtrainer.immerse` — full CQRS module (domain, application, infrastructure). This is the primary reference for structure, AI port pattern, and aggregate design.
  - `ImmerseContent.java` — immutable aggregate root extending `AggregateRoot<ImmerseContentId>`; factory methods `create()` / `reconstitute()`
  - `ImmerseAiPort.java` — interface with inner `record` result types; implemented by `AnthropicImmerseAiAdapter.java`
  - `GenerateImmerseContentUseCase.java` — `@UseCase` / `@Service`, constructor injection, `execute()` method
  - `GenerateImmerseContentController.java` — package-private, one action per controller pattern

- `com.faus535.englishtrainer.review.domain.ReviewSourceType` — enum with values `TALK_ERROR`, `IMMERSE_VOCAB`, `PRONUNCIATION`; `ARTICLE` must be added here
- `com.faus535.englishtrainer.review.domain.ReviewItem` — `ReviewItem.create(userId, sourceType, sourceId, frontContent, backContent)` is the factory; `sourceId` will be `ArticleMarkedWordId.value()`

- `com.faus535.englishtrainer.gamification.infrastructure.event.ImmerseAnsweredGamificationListener` — `@TransactionalEventListener` + `@Transactional(propagation = REQUIRES_NEW)` pattern; uses `AuthUserRepository` + `AddXpUseCase`
- `com.faus535.englishtrainer.activity.infrastructure.event.ImmerseSubmittedActivityListener` — same pattern using `RecordActivityUseCase`

- **Flyway migrations**: latest is `V10.9.0`; next available series is `V11.x.x`

---

## Phases

---

### Phase 1: Article Domain + GenerateArticle + GetArticle

**Goal**: Full vertical slice to generate an AI article and retrieve it. Establishes all domain types, persistence, AI port (generateArticle only), and the two main endpoints.

**Files to create**:

- `src/main/java/com/faus535/englishtrainer/article/domain/ArticleReadingId.java`
  — Value Object: `record ArticleReadingId(UUID value)` with `static ArticleReadingId generate()`

- `src/main/java/com/faus535/englishtrainer/article/domain/ArticleLevel.java`
  — Enum `B1, B2, C1` with `value()` and `fromString(String)`

- `src/main/java/com/faus535/englishtrainer/article/domain/ArticleTopic.java`
  — `record ArticleTopic(String value)` with guard: `value` must not be blank and max 100 chars; throw `IllegalArgumentException` in compact constructor

- `src/main/java/com/faus535/englishtrainer/article/domain/ArticleStatus.java`
  — Enum `IN_PROGRESS, COMPLETED` with `value()` and `fromString(String)`

- `src/main/java/com/faus535/englishtrainer/article/domain/ArticleSpeaker.java`
  — Enum `AI, USER` with `fromString(String)`

- `src/main/java/com/faus535/englishtrainer/article/domain/ArticleParagraphId.java`
  — `record ArticleParagraphId(UUID value)` with `static ArticleParagraphId generate()`

- `src/main/java/com/faus535/englishtrainer/article/domain/ArticleParagraph.java`
  — Entity (plain class, not aggregate root): `ArticleParagraphId id`, `ArticleReadingId articleReadingId`, `String content`, `int orderIndex`, `ArticleSpeaker speaker`. Static factory `create(ArticleReadingId, String content, int orderIndex, ArticleSpeaker speaker)`.

- `src/main/java/com/faus535/englishtrainer/article/domain/ArticleReading.java`
  — Aggregate Root extending `AggregateRoot<ArticleReadingId>`. Fields: `ArticleReadingId id`, `UUID userId`, `ArticleTopic topic`, `ArticleLevel level`, `String title`, `ArticleStatus status`, `List<ArticleParagraph> paragraphs` (immutable copy), `Instant createdAt`. Factory methods:
  - `static ArticleReading create(UUID userId, ArticleTopic topic, ArticleLevel level)` — status=IN_PROGRESS, empty paragraphs
  - `static ArticleReading reconstitute(...)` — all fields
  - `ArticleReading withTitleAndParagraphs(String title, List<ArticleParagraph> paragraphs)` — returns new instance with title + paragraphs set
  - `ArticleReading complete()` — returns new instance with status=COMPLETED; throws `ArticleAlreadyCompletedException` if already COMPLETED; registers `ArticleReadingCompletedEvent`

- `src/main/java/com/faus535/englishtrainer/article/domain/ArticleReadingRepository.java`
  — Interface: `void save(ArticleReading)`, `Optional<ArticleReading> findById(ArticleReadingId)`, `List<ArticleReading> findByUserIdOrderByCreatedAtDesc(UUID userId)`

- `src/main/java/com/faus535/englishtrainer/article/domain/ArticleAiPort.java`
  — Interface with inner result records. Phase 1 only needs `generateArticle`:
  ```java
  ArticleGenerateResult generateArticle(String topic, String level) throws ArticleAiException;
  // (other methods added in later phases)
  record ArticleGenerateResult(String title, List<ArticleParagraphData> paragraphs) {}
  record ArticleParagraphData(String content, int orderIndex, String speaker) {}
  ```
  Add remaining methods (`translateWord`, `generateQuestions`, `correctAnswer`) in later phases to keep the port coherent — define all method signatures in Phase 1 but implement adapters incrementally.

- `src/main/java/com/faus535/englishtrainer/article/domain/error/ArticleException.java`
  — Base checked exception: `public class ArticleException extends Exception`

- `src/main/java/com/faus535/englishtrainer/article/domain/error/ArticleNotFoundException.java`
- `src/main/java/com/faus535/englishtrainer/article/domain/error/ArticleAccessDeniedException.java`
- `src/main/java/com/faus535/englishtrainer/article/domain/error/ArticleAiException.java`
- `src/main/java/com/faus535/englishtrainer/article/domain/error/ArticleAlreadyCompletedException.java`
  — All extend `ArticleException`

- `src/main/java/com/faus535/englishtrainer/article/domain/event/ArticleReadingCompletedEvent.java`
  — `record ArticleReadingCompletedEvent(UUID articleReadingId, UUID userId)` — plain record (no Spring annotation needed; Spring's `@TransactionalEventListener` works on any object)

- `src/main/java/com/faus535/englishtrainer/article/application/GenerateArticleUseCase.java`
  — `@UseCase` (`@Service`), package-private constructor. `execute(UUID userId, ArticleTopic topic, ArticleLevel level)`:
  1. Verify user exists via `UserProfileRepository.existsById(userId)` (throw domain error if not found)
  2. `ArticleReading reading = ArticleReading.create(userId, topic, level)`
  3. `ArticleAiPort.ArticleGenerateResult result = aiPort.generateArticle(topic.value(), level.value())`
  4. Map `result.paragraphs()` to `List<ArticleParagraph>` via `ArticleParagraph.create(...)`
  5. `reading = reading.withTitleAndParagraphs(result.title(), paragraphs)`
  6. `repository.save(reading)`
  7. Return `reading`

- `src/main/java/com/faus535/englishtrainer/article/application/GetArticleUseCase.java`
  — `execute(UUID userId, ArticleReadingId id)`: find article, verify ownership, return it.

- `src/main/java/com/faus535/englishtrainer/article/infrastructure/ai/AnthropicArticleAiAdapter.java`
  — `@Component`, package-private. Implements `ArticleAiPort`. Follows same `RestClient` + Claude `tool_use` pattern as `AnthropicImmerseAiAdapter`. Phase 1: implement `generateArticle()`. Claude tool definition: `generate_article` with fields `title:string`, `paragraphs:array[{content,order_index,speaker}]`. Prompt instructs Claude to write a 500-700 word news-style English article at the given level with alternating AI/USER paragraphs (AI writes, USER reads aloud). System prompt frames `topic` as quoted data to prevent prompt injection.

- `src/main/java/com/faus535/englishtrainer/article/infrastructure/controller/ArticleResponse.java`
  — Shared response DTO (package-private record or class):
  ```java
  record ArticleResponse(
      UUID id, String title, String level, String status,
      List<ParagraphResponse> paragraphs, Instant createdAt
  ) {
      record ParagraphResponse(UUID id, String content, int orderIndex, String speaker) {}
  }
  ```

- `src/main/java/com/faus535/englishtrainer/article/infrastructure/controller/GenerateArticleController.java`
  — `@RestController`, package-private. `POST /api/article/generate`. Request record:
  ```java
  record GenerateArticleRequest(
      @NotBlank @Size(max=100) String topic,
      @NotBlank @Pattern(regexp="^(B1|B2|C1)$") String level
  ) {}
  ```
  Extracts `userId` from `authentication.getDetails()` map key `"profileId"`. Returns `201 Created` with `ArticleResponse`.

- `src/main/java/com/faus535/englishtrainer/article/infrastructure/controller/GetArticleController.java`
  — `GET /api/article/{id}`. Returns `200` with `ArticleResponse`. Returns `404` if not found, `403` if wrong user.

- `src/main/java/com/faus535/englishtrainer/article/infrastructure/controller/ArticleControllerAdvice.java`
  — `@RestControllerAdvice`, package-private. Handlers:
  - `ArticleNotFoundException` → `404`
  - `ArticleAccessDeniedException` → `403`
  - `ArticleAiException` → `502`
  - `ArticleAlreadyCompletedException` → `409`

- `src/main/java/com/faus535/englishtrainer/article/infrastructure/persistence/ArticleReadingEntity.java`
  — `@Table("article_readings")`, implements `Persistable<UUID>`. Fields map 1:1 to DB columns. Has `@MappedCollection(idColumn="article_reading_id", keyColumn="order_index") List<ArticleParagraphEntity> paragraphs`.

- `src/main/java/com/faus535/englishtrainer/article/infrastructure/persistence/ArticleParagraphEntity.java`
  — `@Table("article_paragraphs")`, owned by `ArticleReadingEntity`.

- `src/main/java/com/faus535/englishtrainer/article/infrastructure/persistence/JpaArticleReadingRepository.java`
  — Spring Data JDBC `CrudRepository<ArticleReadingEntity, UUID>` with `findByUserId(UUID)`.

- `src/main/java/com/faus535/englishtrainer/article/infrastructure/persistence/JpaArticleReadingRepositoryAdapter.java`
  — `@Component`, implements `ArticleReadingRepository`. Maps between domain and entity.

**Tests to create**:

- `src/test/java/.../article/domain/ArticleReadingTest.java`
  — Unit: `create()` initializes IN_PROGRESS; `complete()` transitions to COMPLETED and registers event; calling `complete()` again throws `ArticleAlreadyCompletedException`

- `src/test/java/.../article/domain/ArticleReadingMother.java`
  — Object Mother: `inProgress(UUID userId)`, `completed(UUID userId)`, `withId(ArticleReadingId, UUID userId)`

- `src/test/java/.../article/application/GenerateArticleUseCaseTest.java`
  — Unit: happy path saves article; AI failure wraps to domain error; user not found throws error

- `src/test/java/.../article/application/GetArticleUseCaseTest.java`
  — Unit: returns article; throws `ArticleNotFoundException`; throws `ArticleAccessDeniedException` for wrong user

**Flyway migration**: `V11.0.0__article_create_tables.sql` (see Database Changes section)

**Acceptance criteria**:
- [x] `POST /api/article/generate` with valid JWT, topic, and level returns `201` with title + paragraphs
- [x] `GET /api/article/{id}` returns the article for the owning user
- [x] `GET /api/article/{id}` returns `403` for a different user's JWT
- [x] `ArticleReadingTest` passes (status transition + event registration)
- [x] `GenerateArticleUseCaseTest` passes (happy + AI failure paths)
- [x] `./gradlew compileJava` and `./gradlew test` pass

---

### Phase 2: MarkWord + Translation + GetMarkedWords + ReviewItem

**Goal**: Users can mark a word/phrase during reading. Claude translates it in context. Marked word is saved and a ReviewItem is created with `source_type = ARTICLE`.

**Files to create**:

- `src/main/java/com/faus535/englishtrainer/article/domain/ArticleMarkedWordId.java`
  — `record ArticleMarkedWordId(UUID value)` with `static generate()`

- `src/main/java/com/faus535/englishtrainer/article/domain/ArticleMarkedWord.java`
  — Standalone entity (not in aggregate). Fields: `ArticleMarkedWordId id`, `ArticleReadingId articleReadingId`, `UUID userId`, `String wordOrPhrase`, `String translation`, `String contextSentence`, `Instant createdAt`. Factory: `static ArticleMarkedWord create(ArticleReadingId, UUID userId, String wordOrPhrase, String translation, String contextSentence)`.

- `src/main/java/com/faus535/englishtrainer/article/domain/ArticleMarkedWordRepository.java`
  — Interface: `void save(ArticleMarkedWord)`, `List<ArticleMarkedWord> findByArticleIdAndUserId(ArticleReadingId, UUID userId)`

- `src/main/java/com/faus535/englishtrainer/article/domain/error/DuplicateMarkedWordException.java`
  — Extends `ArticleException`

**Files to modify**:

- `src/main/java/com/faus535/englishtrainer/review/domain/ReviewSourceType.java`
  — Add `ARTICLE("ARTICLE")` enum value

**Files to create (continued)**:

- `src/main/java/com/faus535/englishtrainer/article/application/MarkWordUseCase.java`
  — `@UseCase`. `execute(UUID userId, ArticleReadingId articleId, String wordOrPhrase, String contextSentence)`:
  1. Fetch article, verify ownership (throw `ArticleNotFoundException` / `ArticleAccessDeniedException`)
  2. `ArticleAiPort.ArticleTranslationResult tr = aiPort.translateWord(wordOrPhrase, contextSentence)`
  3. `ArticleMarkedWord marked = ArticleMarkedWord.create(articleId, userId, wordOrPhrase, tr.translation(), contextSentence)`
  4. `markedWordRepository.save(marked)` — adapter catches `DataIntegrityViolationException` → throws `DuplicateMarkedWordException`
  5. `ReviewItem reviewItem = ReviewItem.create(userId, ReviewSourceType.ARTICLE, marked.id().value(), wordOrPhrase, tr.translation())`
  6. `reviewItemRepository.save(reviewItem)`
  7. Return `marked`

- `src/main/java/com/faus535/englishtrainer/article/application/GetArticleMarkedWordsUseCase.java`
  — `execute(UUID userId, ArticleReadingId articleId)`: verify ownership, return `markedWordRepository.findByArticleIdAndUserId(articleId, userId)`

**AI port addition** in `ArticleAiPort.java`:
  ```java
  ArticleTranslationResult translateWord(String wordOrPhrase, String contextSentence) throws ArticleAiException;
  record ArticleTranslationResult(String translation) {}
  ```

- `src/main/java/com/faus535/englishtrainer/article/infrastructure/ai/AnthropicArticleAiAdapter.java`
  — Add `translateWord()` implementation. Claude tool: `translate_word` with field `translation:string`. Prompt sends `wordOrPhrase` + `contextSentence` for contextual translation.

- `src/main/java/com/faus535/englishtrainer/article/infrastructure/controller/MarkWordController.java`
  — `POST /api/article/{id}/words`. Request:
  ```java
  record MarkWordRequest(
      @NotBlank @Size(max=200) String wordOrPhrase,
      @Size(max=1000) String contextSentence
  ) {}
  ```
  Returns `201` with `MarkedWordResponse { UUID id, String wordOrPhrase, String translation, String contextSentence, Instant createdAt }`.

- `src/main/java/com/faus535/englishtrainer/article/infrastructure/controller/GetMarkedWordsController.java`
  — `GET /api/article/{id}/words`. Returns `200` with `List<MarkedWordResponse>`.

**Add to `ArticleControllerAdvice`**:
  - `DuplicateMarkedWordException` → `409 Conflict`

- `src/main/java/com/faus535/englishtrainer/article/infrastructure/persistence/ArticleMarkedWordEntity.java`
- `src/main/java/com/faus535/englishtrainer/article/infrastructure/persistence/JpaArticleMarkedWordRepository.java`
- `src/main/java/com/faus535/englishtrainer/article/infrastructure/persistence/JpaArticleMarkedWordRepositoryAdapter.java`
  — Adapter catches `DataIntegrityViolationException` on `save()` and rethrows as `DuplicateMarkedWordException`

**Tests**:

- `src/test/java/.../article/domain/ArticleMarkedWordMother.java`
  — `withWord(ArticleReadingId, UUID userId, String word)`

- `src/test/java/.../article/application/MarkWordUseCaseTest.java`
  — Happy path: ReviewItem created with `ARTICLE` source type; ownership violation; duplicate word; AI failure

- `src/test/java/.../article/application/GetArticleMarkedWordsUseCaseTest.java`
  — Returns list; ownership check

**Flyway migration**: `V11.1.0__review_add_article_source_type.sql` (see Database Changes)

**Acceptance criteria**:
- [x] `POST /api/article/{id}/words` returns `201` with translation
- [x] A row exists in `review_items` with `source_type = 'ARTICLE'` after marking a word
- [x] Duplicate word mark returns `409`
- [x] `GET /api/article/{id}/words` returns marked words for the owning user only
- [x] `MarkWordUseCaseTest` passes including ReviewItem side-effect assertion
- [x] `ReviewSourceType.ARTICLE` compiles and existing tests still pass

---

### Phase 3: Questions + SubmitAnswer + GetHint

**Goal**: After article completion, AI-generated questions are persisted with pre-stored hints. Users submit answers (40-word minimum), Claude grades them. Hint retrieval returns stored text — no Claude call.

**Files to create**:

- `src/main/java/com/faus535/englishtrainer/article/domain/ArticleQuestionId.java`
- `src/main/java/com/faus535/englishtrainer/article/domain/ArticleQuestionAnswerId.java`
  — Same pattern as other ID value objects

- `src/main/java/com/faus535/englishtrainer/article/domain/ArticleQuestion.java`
  — Standalone entity. Fields: `ArticleQuestionId id`, `ArticleReadingId articleReadingId`, `String questionText`, `int orderIndex`, `int minWords` (default 40), `String hintText`. Factory: `static ArticleQuestion create(ArticleReadingId, String questionText, int orderIndex, String hintText)`.

- `src/main/java/com/faus535/englishtrainer/article/domain/ArticleQuestionAnswer.java`
  — Standalone entity. Fields: `ArticleQuestionAnswerId id`, `ArticleQuestionId questionId`, `String userAnswer`, `boolean isContentCorrect`, `String grammarFeedback`, `String styleFeedback`, `String correctionSummary`, `Instant createdAt`. Factory:
  ```java
  static ArticleQuestionAnswer create(ArticleQuestionId questionId, String userAnswer,
      boolean isContentCorrect, String grammarFeedback, String styleFeedback, String correctionSummary)
  ```
  Compact constructor validates word count: `if (wordCount(userAnswer) < 40) throw new AnswerTooShortException(...)`. Helper: `static int wordCount(String text)` splits on `\s+`.

- `src/main/java/com/faus535/englishtrainer/article/domain/ArticleQuestionRepository.java`
  — Interface: `void save(ArticleQuestion)`, `List<ArticleQuestion> findByArticleReadingId(ArticleReadingId)`, `Optional<ArticleQuestion> findById(ArticleQuestionId)`

- `src/main/java/com/faus535/englishtrainer/article/domain/ArticleQuestionAnswerRepository.java`
  — Interface: `void save(ArticleQuestionAnswer)`, `Optional<ArticleQuestionAnswer> findByQuestionId(ArticleQuestionId)`

- `src/main/java/com/faus535/englishtrainer/article/domain/error/ArticleQuestionNotFoundException.java`
- `src/main/java/com/faus535/englishtrainer/article/domain/error/AnswerTooShortException.java`
- `src/main/java/com/faus535/englishtrainer/article/domain/error/QuestionAlreadyAnsweredException.java`
  — All extend `ArticleException`

**AI port additions** in `ArticleAiPort.java`:
  ```java
  ArticleQuestionsResult generateQuestions(String articleText, String level) throws ArticleAiException;
  ArticleAnswerCorrectionResult correctAnswer(String question, String userAnswer, String articleText) throws ArticleAiException;
  record ArticleQuestionsResult(List<ArticleQuestionData> questions) {}
  record ArticleQuestionData(String questionText, int orderIndex, String hintText) {}
  record ArticleAnswerCorrectionResult(boolean isContentCorrect, String grammarFeedback, String styleFeedback, String correctionSummary) {}
  ```

- `src/main/java/com/faus535/englishtrainer/article/infrastructure/ai/AnthropicArticleAiAdapter.java`
  — Add `generateQuestions()` and `correctAnswer()`. Claude tools: `generate_questions` (array of `{question_text, order_index, hint_text}`), `correct_answer` (`{is_content_correct, grammar_feedback, style_feedback, correction_summary}`).

- `src/main/java/com/faus535/englishtrainer/article/application/GenerateQuestionsUseCase.java`
  — `@UseCase`. `execute(ArticleReadingId articleId, String articleText, String level)`:
  1. `ArticleQuestionsResult result = aiPort.generateQuestions(articleText, level)`
  2. Map to `List<ArticleQuestion>` via `ArticleQuestion.create(...)`
  3. Save each question
  4. Return list

- `src/main/java/com/faus535/englishtrainer/article/application/SubmitAnswerUseCase.java`
  — `execute(UUID userId, ArticleReadingId articleId, ArticleQuestionId questionId, String answer)`:
  1. Fetch article, verify ownership
  2. Fetch question, verify `question.articleReadingId().equals(articleId)` (throw `ArticleQuestionNotFoundException`)
  3. Check no existing answer: `questionAnswerRepository.findByQuestionId(questionId)` → throw `QuestionAlreadyAnsweredException` if present
  4. Concatenate article text from paragraphs for grading context
  5. `ArticleAnswerCorrectionResult correction = aiPort.correctAnswer(question.questionText(), answer, articleText)` — domain throws `AnswerTooShortException` before this if word count < 40
  6. `ArticleQuestionAnswer answerEntity = ArticleQuestionAnswer.create(questionId, answer, correction.isContentCorrect(), correction.grammarFeedback(), correction.styleFeedback(), correction.correctionSummary())`
  7. `questionAnswerRepository.save(answerEntity)` — adapter catches `DataIntegrityViolationException` → `QuestionAlreadyAnsweredException`
  8. Return `answerEntity`

- `src/main/java/com/faus535/englishtrainer/article/application/GetQuestionHintUseCase.java`
  — `execute(UUID userId, ArticleReadingId articleId, ArticleQuestionId questionId)`:
  1. Fetch article, verify ownership
  2. Fetch question, verify it belongs to article
  3. Return `question.hintText()`

- `src/main/java/com/faus535/englishtrainer/article/infrastructure/controller/GetArticleQuestionsController.java`
  — `GET /api/article/{id}/questions`. Returns `200` with `List<QuestionResponse>`. `QuestionResponse { UUID id, String questionText, int orderIndex, int minWords }` (hint NOT returned here — only via dedicated hint endpoint).

- `src/main/java/com/faus535/englishtrainer/article/infrastructure/controller/SubmitAnswerController.java`
  — `POST /api/article/{id}/questions/{qId}/answer`. Request: `{ @NotBlank @Size(max=5000) String answer }`. Returns `200` with `AnswerResponse { UUID id, boolean isContentCorrect, String grammarFeedback, String styleFeedback, String correctionSummary }`.

- `src/main/java/com/faus535/englishtrainer/article/infrastructure/controller/GetQuestionHintController.java`
  — `GET /api/article/{id}/questions/{qId}/hint`. Returns `200` with `{ String hint }`.

**Add to `ArticleControllerAdvice`**:
  - `ArticleQuestionNotFoundException` → `404`
  - `AnswerTooShortException` → `422 Unprocessable Entity`
  - `QuestionAlreadyAnsweredException` → `409 Conflict`

- `src/main/java/com/faus535/englishtrainer/article/infrastructure/persistence/ArticleQuestionEntity.java`
- `src/main/java/com/faus535/englishtrainer/article/infrastructure/persistence/ArticleQuestionAnswerEntity.java`
- `src/main/java/com/faus535/englishtrainer/article/infrastructure/persistence/JpaArticleQuestionRepository.java`
- `src/main/java/com/faus535/englishtrainer/article/infrastructure/persistence/JpaArticleQuestionRepositoryAdapter.java`
- `src/main/java/com/faus535/englishtrainer/article/infrastructure/persistence/JpaArticleQuestionAnswerRepository.java`
- `src/main/java/com/faus535/englishtrainer/article/infrastructure/persistence/JpaArticleQuestionAnswerRepositoryAdapter.java`

**Tests**:

- `src/test/java/.../article/domain/ArticleQuestionAnswerTest.java`
  — Word count: exactly 40 words passes; 39 words throws `AnswerTooShortException`

- `src/test/java/.../article/domain/ArticleQuestionMother.java`
  — `withHint(ArticleReadingId)`, `ordered(ArticleReadingId, int n)`

- `src/test/java/.../article/domain/ArticleQuestionAnswerMother.java`
  — `valid(ArticleQuestionId)`, `tooShort(ArticleQuestionId)`, `withGrading(ArticleQuestionId)`

- `src/test/java/.../article/application/GenerateQuestionsUseCaseTest.java`
  — Happy path saves questions with hints; AI failure propagates

- `src/test/java/.../article/application/SubmitAnswerUseCaseTest.java`
  — Happy path; < 40 words throws domain exception; question not in article; question already answered

- `src/test/java/.../article/application/GetQuestionHintUseCaseTest.java`
  — Returns pre-stored hintText; question not in article throws `ArticleQuestionNotFoundException`

**Acceptance criteria**:
- [x] `GET /api/article/{id}/questions` returns questions (after article completed in Phase 4)
- [x] `POST /api/article/{id}/questions/{qId}/answer` returns `200` with grading
- [x] `POST` with fewer than 40 words returns `422`
- [x] Second answer submission returns `409`
- [x] `GET /api/article/{id}/questions/{qId}/hint` returns stored hint text (no extra Claude call)
- [x] `ArticleQuestionAnswerTest` 40-word boundary passes
- [x] `SubmitAnswerUseCaseTest` all paths pass

---

### Phase 4: CompleteArticle + Events (Gamification + Activity)

**Goal**: Completing an article transitions its status, auto-generates questions, fires `ArticleReadingCompletedEvent` which awards XP and records activity.

**Files to create**:

- `src/main/java/com/faus535/englishtrainer/article/application/CompleteArticleUseCase.java`
  — `@UseCase`. `@Transactional`. `execute(UUID userId, ArticleReadingId articleId)`:
  1. Fetch article, verify ownership
  2. `ArticleReading completed = article.complete()` — throws `ArticleAlreadyCompletedException` if repeated
  3. `articleReadingRepository.save(completed)`
  4. Concatenate full article text from paragraphs (`paragraphs.stream().map(ArticleParagraph::content).collect(joining("\n\n"))`)
  5. `generateQuestionsUseCase.execute(articleId, fullText, completed.level().value())`
  6. Publish `new ArticleReadingCompletedEvent(articleId.value(), userId)` via `ApplicationEventPublisher.publishEvent(...)` — event is published here so it fires after transaction commits
  7. Return `completed`

- `src/main/java/com/faus535/englishtrainer/article/infrastructure/controller/CompleteArticleController.java`
  — `POST /api/article/{id}/complete`. No request body. Returns `204 No Content`.

- `src/main/java/com/faus535/englishtrainer/gamification/infrastructure/event/ArticleCompletedGamificationListener.java`
  — `@Component`, package-private. Mirrors `ImmerseAnsweredGamificationListener`. XP award for completing an article (e.g., `XP_PER_ARTICLE = 25`). `@TransactionalEventListener` + `@Transactional(propagation = REQUIRES_NEW)`. Handles `ArticleReadingCompletedEvent`.

- `src/main/java/com/faus535/englishtrainer/activity/infrastructure/event/ArticleCompletedActivityListener.java`
  — `@Component`, package-private. Mirrors `ImmerseSubmittedActivityListener`. Calls `RecordActivityUseCase.execute(userProfileId, LocalDate.now())`. Handles `ArticleReadingCompletedEvent`.

**Tests**:

- `src/test/java/.../article/application/CompleteArticleUseCaseTest.java`
  — Happy path: status becomes COMPLETED, questions generated, event published; already-completed throws `ArticleAlreadyCompletedException`; ownership check

- `src/test/java/.../gamification/infrastructure/event/ArticleCompletedGamificationListenerTest.java`
  — XP awarded on valid event; auth user not found is handled gracefully (no exception propagated)

- `src/test/java/.../activity/infrastructure/event/ArticleCompletedActivityListenerTest.java`
  — Activity recorded; auth user not found handled gracefully

**Acceptance criteria**:
- [x] `POST /api/article/{id}/complete` returns `204` and transitions article to COMPLETED
- [x] Questions are generated and persisted after completion
- [x] `GET /api/article/{id}/questions` returns 5-7 questions after completion
- [x] Calling `/complete` twice returns `409`
- [x] XP is awarded in `gamification` after event
- [x] Activity is recorded in `activity` after event
- [x] `CompleteArticleUseCaseTest` all paths pass

---

## API Contract

### `POST /api/article/generate`
- **Auth**: Required (JWT). `userId` from `authentication.getDetails()["profileId"]`
- **Request body**:
  ```json
  { "topic": "Climate change policies in Europe", "level": "B2" }
  ```
- **Response body** (`201 Created`):
  ```json
  {
    "id": "550e8400-e29b-41d4-a716-446655440000",
    "title": "EU's New Climate Targets Spark Debate",
    "level": "B2",
    "status": "IN_PROGRESS",
    "paragraphs": [
      { "id": "...", "content": "European leaders gathered in Brussels...", "orderIndex": 0, "speaker": "AI" },
      { "id": "...", "content": "Critics argue that the timeline...", "orderIndex": 1, "speaker": "USER" }
    ],
    "createdAt": "2026-04-06T10:00:00Z"
  }
  ```
- **Status codes**: `201` success, `400` validation error, `401` unauthenticated, `502` AI failure

---

### `GET /api/article/{id}`
- **Auth**: Required
- **Response body** (`200 OK`): Same as above
- **Status codes**: `200` success, `401`, `403` wrong user, `404` not found

---

### `POST /api/article/{id}/complete`
- **Auth**: Required
- **Request body**: none
- **Response body**: none
- **Status codes**: `204` success, `401`, `403`, `404`, `409` already completed

---

### `POST /api/article/{id}/words`
- **Auth**: Required
- **Request body**:
  ```json
  { "wordOrPhrase": "spark debate", "contextSentence": "The new policies spark debate among economists." }
  ```
- **Response body** (`201 Created`):
  ```json
  {
    "id": "...",
    "wordOrPhrase": "spark debate",
    "translation": "generar debate / provocar polémica",
    "contextSentence": "The new policies spark debate among economists.",
    "createdAt": "2026-04-06T10:05:00Z"
  }
  ```
- **Status codes**: `201` success, `400` validation, `401`, `403`, `404`, `409` duplicate word, `502` AI failure

---

### `GET /api/article/{id}/words`
- **Auth**: Required
- **Response body** (`200 OK`): Array of marked word objects (same shape as above)
- **Status codes**: `200`, `401`, `403`, `404`

---

### `GET /api/article/{id}/questions`
- **Auth**: Required
- **Response body** (`200 OK`):
  ```json
  [
    { "id": "...", "questionText": "What is the main argument against the new policy?", "orderIndex": 0, "minWords": 40 }
  ]
  ```
- **Status codes**: `200`, `401`, `403`, `404`

---

### `POST /api/article/{id}/questions/{qId}/answer`
- **Auth**: Required
- **Request body**: `{ "answer": "The main argument is that the timeline is too aggressive..." }` (40+ words)
- **Response body** (`200 OK`):
  ```json
  {
    "id": "...",
    "isContentCorrect": true,
    "grammarFeedback": "Good use of passive voice. Minor: 'aggressive' could be 'ambitious'.",
    "styleFeedback": "Clear structure. Consider a topic sentence.",
    "correctionSummary": "Well-argued response with minor style improvements possible."
  }
  ```
- **Status codes**: `200` success, `400` validation, `401`, `403`, `404` (article or question), `409` already answered, `422` answer too short, `502` AI failure

---

### `GET /api/article/{id}/questions/{qId}/hint`
- **Auth**: Required
- **Response body** (`200 OK`): `{ "hint": "Consider what economists and industry representatives said in the article." }`
- **Status codes**: `200`, `401`, `403`, `404`

---

## Database Changes

### `V11.0.0__article_create_tables.sql`
```sql
CREATE TABLE article_readings (
    id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id     UUID NOT NULL REFERENCES user_profiles(id) ON DELETE CASCADE,
    topic       VARCHAR(100) NOT NULL,
    level       VARCHAR(5)   NOT NULL CHECK (level IN ('B1','B2','C1')),
    title       VARCHAR(300) NOT NULL DEFAULT '',
    status      VARCHAR(20)  NOT NULL DEFAULT 'IN_PROGRESS'
                    CHECK (status IN ('IN_PROGRESS','COMPLETED')),
    created_at  TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT now(),
    version     BIGINT NOT NULL DEFAULT 0
);
CREATE INDEX idx_article_readings_user_id         ON article_readings(user_id);
CREATE INDEX idx_article_readings_user_created    ON article_readings(user_id, created_at DESC);

CREATE TABLE article_paragraphs (
    id                  UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    article_reading_id  UUID    NOT NULL REFERENCES article_readings(id) ON DELETE CASCADE,
    content             TEXT    NOT NULL,
    order_index         INTEGER NOT NULL,
    speaker             VARCHAR(5) NOT NULL CHECK (speaker IN ('AI','USER'))
);
CREATE INDEX idx_article_paragraphs_reading_id ON article_paragraphs(article_reading_id);
CREATE UNIQUE INDEX idx_article_paragraphs_order ON article_paragraphs(article_reading_id, order_index);

CREATE TABLE article_questions (
    id                  UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    article_reading_id  UUID    NOT NULL REFERENCES article_readings(id) ON DELETE CASCADE,
    question_text       TEXT    NOT NULL,
    order_index         INTEGER NOT NULL,
    min_words           INTEGER NOT NULL DEFAULT 40,
    hint_text           TEXT
);
CREATE INDEX idx_article_questions_reading_id ON article_questions(article_reading_id);
CREATE UNIQUE INDEX idx_article_questions_order ON article_questions(article_reading_id, order_index);

CREATE TABLE article_question_answers (
    id                      UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    article_question_id     UUID    NOT NULL REFERENCES article_questions(id) ON DELETE CASCADE,
    user_answer             TEXT    NOT NULL,
    is_content_correct      BOOLEAN,
    grammar_feedback        TEXT,
    style_feedback          TEXT,
    correction_summary      TEXT,
    created_at              TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT now()
);
CREATE UNIQUE INDEX idx_article_question_answers_unique ON article_question_answers(article_question_id);
```

### `V11.1.0__review_add_article_source_type.sql`
```sql
-- Add article_marked_words table
CREATE TABLE article_marked_words (
    id                  UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    article_reading_id  UUID         NOT NULL REFERENCES article_readings(id) ON DELETE CASCADE,
    user_id             UUID         NOT NULL REFERENCES user_profiles(id)    ON DELETE CASCADE,
    word_or_phrase      VARCHAR(200) NOT NULL,
    translation         TEXT         NOT NULL,
    context_sentence    TEXT,
    created_at          TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT now()
);
CREATE INDEX idx_article_marked_words_reading_id ON article_marked_words(article_reading_id);
CREATE INDEX idx_article_marked_words_user_id    ON article_marked_words(user_id);
CREATE UNIQUE INDEX idx_article_marked_words_unique
    ON article_marked_words(article_reading_id, user_id, word_or_phrase);

-- Extend review_items to allow ARTICLE source type
ALTER TABLE review_items
    DROP CONSTRAINT IF EXISTS review_items_source_type_check;
ALTER TABLE review_items
    ADD CONSTRAINT review_items_source_type_check
    CHECK (source_type IN ('TALK_ERROR','IMMERSE_VOCAB','PRONUNCIATION','ARTICLE'));
```

> **Note on migration ordering**: `V11.0.0` is deployed in Phase 1 (article + paragraphs + questions tables). `V11.1.0` is deployed in Phase 2 (marked words + constraint update). The `article_questions` and `article_question_answers` tables are included in `V11.0.0` to avoid FK dependency issues when running Phase 3 tests against an existing schema.

---

## Testing Strategy

Tests are organized as **unit tests per use case** and **integration tests per risk area**. Each phase above specifies its own tests. Summary:

**Unit tests** (all use in-memory repositories + Object Mothers):
- `ArticleReadingTest` — domain invariants (Phase 1)
- `GenerateArticleUseCaseTest`, `GetArticleUseCaseTest` (Phase 1)
- `ArticleQuestionAnswerTest` — 40-word boundary (Phase 3)
- `MarkWordUseCaseTest` — cross-module ReviewItem side effect with ARTICLE enum (Phase 2, highest priority)
- `SubmitAnswerUseCaseTest` — word count invariant + question membership (Phase 3)
- `GetQuestionHintUseCaseTest` — no Claude call path (Phase 3)
- `CompleteArticleUseCaseTest` — event publication + question generation (Phase 4)
- `ArticleCompletedGamificationListenerTest`, `ArticleCompletedActivityListenerTest` (Phase 4)

**Integration tests** (Testcontainers + real DB):
- `ArticleOwnershipIT` — IDOR: GET article with different user JWT → 403 (Phase 1)
- `GenerateArticleIT` — POST /generate with valid JWT → 201, persisted (Phase 1)
- `MarkWordAndReviewIT` — POST word → `review_items` row with `source_type = 'ARTICLE'` (Phase 2)
- `SubmitAnswerIT` — POST < 40 words → 422; valid → 200 (Phase 3)

**Coverage priorities (ranked)**:
1. `MarkWordUseCaseTest` — ReviewItem cross-module side effect with new enum
2. `SubmitAnswerUseCaseTest` word count — domain invariant only caught by unit test
3. `ArticleOwnershipIT` — IDOR risk, must verify at HTTP level
4. `GenerateArticleUseCaseTest` AI failure path
5. `MarkWordAndReviewIT` — ARTICLE enum + constraint in real DB
6. `SubmitAnswerIT` 40-word minimum — verify 422 before Claude is called
