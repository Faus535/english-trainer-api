# Backend Plan: Article UX & Code Improvements

> Generated: 2026-04-08
> Request: Improve article module UX (level-based validation, pre-reading, English definitions, progress persistence) and fix code issues (status alignment)

## Decisions Log

| # | Topic | Decision | Alternatives Considered | Why |
|---|-------|----------|------------------------|-----|
| 1 | Min word validation | Pass minWords from ArticleLevel to ArticleQuestionAnswer.create() | Hard-code per level in use case, use question's minWords field | Level already has minWords(); keeping validation in domain entity is cleaner DDD |
| 2 | Pre-reading data | Generate via AI on-demand (not pre-computed) | Store pre-reading at generation time, cache in DB | Pre-reading depends on article content being READY; on-demand avoids extra async complexity |
| 3 | English definition | Add to translateWord() call (single AI call for both) | Separate AI call for definition | Cheaper — one API call instead of two, context is already there |
| 4 | Progress persistence | Store on ArticleReading aggregate directly | Separate progress table, Redis cache | Simple — only 2 int fields, avoids extra table, fits aggregate boundary |
| 5 | ArticleResponse topic field | Add topic to ArticleResponse (frontend expects it) | Keep current response without topic | Frontend ArticleResponse interface has topic field |

## Analysis

### Existing Code (article module)

**Domain:**
- `ArticleReading` (aggregate root): `src/main/java/.../article/domain/ArticleReading.java` — status lifecycle, no progress fields
- `ArticleQuestionAnswer`: `src/main/java/.../article/domain/ArticleQuestionAnswer.java` — hardcoded `MIN_WORDS = 40`
- `ArticleMarkedWord`: `src/main/java/.../article/domain/ArticleMarkedWord.java` — no `englishDefinition` field
- `ArticleLevel`: `src/main/java/.../article/domain/ArticleLevel.java` — has `minWords()` returning B1→20, B2→30, C1→40
- `ArticleAiPort`: `src/main/java/.../article/domain/ArticleAiPort.java` — `translateWord()` returns only `translation`, no pre-reading method

**Application:**
- `SubmitAnswerUseCase`: `src/main/java/.../article/application/SubmitAnswerUseCase.java` — calls `ArticleQuestionAnswer.create()` without passing level
- `MarkWordUseCase`: `src/main/java/.../article/application/MarkWordUseCase.java` — uses `tr.translation()` only

**Infrastructure:**
- `ArticleReadingEntity`: `src/main/java/.../article/infrastructure/persistence/ArticleReadingEntity.java` — JPA entity, no progress columns
- `ArticleMarkedWordEntity`: `src/main/java/.../article/infrastructure/persistence/ArticleMarkedWordEntity.java` — no `englishDefinition` column
- `AnthropicArticleAiAdapter`: `src/main/java/.../article/infrastructure/ai/AnthropicArticleAiAdapter.java` — `TOOL_TRANSLATE_WORD` returns only `translation`
- `MarkWordController`: `src/main/java/.../article/infrastructure/controller/MarkWordController.java` — `MarkedWordResponse` lacks `englishDefinition`
- `ArticleResponse`: `src/main/java/.../article/infrastructure/controller/ArticleResponse.java` — lacks `topic` field
- `ArticleControllerAdvice`: `src/main/java/.../article/infrastructure/controller/ArticleControllerAdvice.java` — handles existing exceptions

**Tests:**
- Object Mothers: `ArticleReadingMother`, `ArticleQuestionMother`, `ArticleQuestionAnswerMother`, `ArticleMarkedWordMother`
- In-memory repos: `InMemoryArticleReadingRepository`, `InMemoryArticleQuestionRepository`, `InMemoryArticleQuestionAnswerRepository`, `InMemoryArticleMarkedWordRepository`
- Stubs: `StubArticleAiPort`, `FailingStubArticleAiPort`

**Migrations:** Latest is `V11.3.0__article_add_xp_earned.sql`

---

## Phases

### Phase 1: Level-Based Answer Validation

**Goal**: Replace hardcoded 40-word minimum with level-specific validation (B1→20, B2→30, C1→40)

**Files to modify**:
- `src/main/java/com/faus535/englishtrainer/article/domain/ArticleQuestionAnswer.java` — accept `minWords` parameter in `create()`
- `src/main/java/com/faus535/englishtrainer/article/application/SubmitAnswerUseCase.java` — pass `article.level().minWords()` to `create()`
- `src/test/java/com/faus535/englishtrainer/article/domain/ArticleQuestionAnswerTest.java` — update tests for level-based validation
- `src/test/java/com/faus535/englishtrainer/article/application/SubmitAnswerUseCaseTest.java` — update test to verify level-based min words

**Details**:

1. **`ArticleQuestionAnswer.java`**:
   - Remove `private static final int MIN_WORDS = 40;`
   - Change `create()` signature to accept `int minWords` parameter:
     ```java
     public static ArticleQuestionAnswer create(ArticleQuestionId questionId, String userAnswer,
             int minWords, boolean isContentCorrect, String grammarFeedback,
             String styleFeedback, String correctionSummary) throws AnswerTooShortException
     ```
   - Use the passed `minWords` in the validation check

2. **`SubmitAnswerUseCase.java`**:
   - After fetching `article`, get `int minWords = article.level().minWords();`
   - Pass `minWords` to `ArticleQuestionAnswer.create()`:
     ```java
     ArticleQuestionAnswer questionAnswer = ArticleQuestionAnswer.create(
             questionId, answer, article.level().minWords(),
             correction.isContentCorrect(), correction.grammarFeedback(),
             correction.styleFeedback(), correction.correctionSummary());
     ```

3. **`ArticleQuestionAnswerTest.java`**:
   - Test B1 level: 19 words throws `AnswerTooShortException`, 20 words succeeds
   - Test B2 level: 29 words throws, 30 succeeds
   - Test C1 level: 39 words throws, 40 succeeds

4. **`SubmitAnswerUseCaseTest.java`**:
   - Update existing test to use an article with specific level
   - Verify that a short answer for B2 (< 30 words) throws `AnswerTooShortException`
   - Verify that a valid answer for B1 (>= 20 words) succeeds

**Acceptance criteria**:
- [x] `ArticleQuestionAnswer.create()` accepts `minWords` parameter
- [x] B1 article allows 20+ word answers, B2 allows 30+, C1 allows 40+
- [x] `AnswerTooShortException` message shows correct minimum for each level
- [x] All existing tests pass (updated for new signature)
- [x] New tests for each level boundary exist
- [x] Compile + tests green

---

### Phase 2: Pre-Reading Endpoint

**Goal**: Backend endpoint for pre-reading data (key vocabulary + predictive question) that the frontend already calls

**Files to create**:
- `src/main/java/com/faus535/englishtrainer/article/domain/PreReadingData.java` — value object
- `src/main/java/com/faus535/englishtrainer/article/domain/KeyWord.java` — value object for key vocabulary
- `src/main/java/com/faus535/englishtrainer/article/application/GetPreReadingUseCase.java` — use case
- `src/main/java/com/faus535/englishtrainer/article/infrastructure/controller/GetPreReadingController.java` — REST endpoint
- `src/test/java/com/faus535/englishtrainer/article/application/GetPreReadingUseCaseTest.java` — unit test

**Files to modify**:
- `src/main/java/com/faus535/englishtrainer/article/domain/ArticleAiPort.java` — add `generatePreReading()` method
- `src/main/java/com/faus535/englishtrainer/article/infrastructure/ai/AnthropicArticleAiAdapter.java` — implement pre-reading AI call
- `src/test/java/com/faus535/englishtrainer/article/infrastructure/ai/StubArticleAiPort.java` — add stub for pre-reading
- `src/test/java/com/faus535/englishtrainer/article/infrastructure/ai/FailingStubArticleAiPort.java` — add failing stub

**Details**:

1. **Domain value objects**:
   ```java
   // KeyWord.java
   public record KeyWord(String word, String translation, String definition) {}
   
   // PreReadingData.java
   public record PreReadingData(List<KeyWord> keyWords, String predictiveQuestion) {}
   ```

2. **`ArticleAiPort.java`** — add method and result record:
   ```java
   PreReadingResult generatePreReading(String articleText, String level) throws ArticleAiException;
   
   record PreReadingResult(List<KeyWordData> keyWords, String predictiveQuestion) {}
   record KeyWordData(String word, String translation, String definition) {}
   ```

3. **`AnthropicArticleAiAdapter.java`**:
   - Add `TOOL_GENERATE_PRE_READING` static constant with schema:
     - `key_words`: array of `{ word, translation, definition }`
     - `predictive_question`: string
   - Implement `generatePreReading()` method with user message:
     ```
     "Analyze this {level} level article and provide: 1) 5-8 key vocabulary words with Spanish translation
     and English definition, 2) One predictive question to engage the reader before reading.
     Article:\n\n{articleText}"
     ```
   - Max tokens: 400

4. **`GetPreReadingUseCase.java`**:
   ```java
   @UseCase
   public class GetPreReadingUseCase {
       private final ArticleReadingRepository articleReadingRepository;
       private final ArticleAiPort aiPort;
       
       public PreReadingData execute(UUID userId, ArticleReadingId articleId)
               throws ArticleNotFoundException, ArticleAccessDeniedException, ArticleAiException {
           ArticleReading article = // find + validate ownership
           String articleText = // join paragraphs
           PreReadingResult result = aiPort.generatePreReading(articleText, article.level().value());
           return new PreReadingData(
               result.keyWords().stream().map(k -> new KeyWord(k.word(), k.translation(), k.definition())).toList(),
               result.predictiveQuestion());
       }
   }
   ```

5. **`GetPreReadingController.java`**:
   ```java
   @GetMapping("/api/article/{id}/pre-reading")
   ResponseEntity<PreReadingResponse> handle(@PathVariable UUID id, Authentication auth) { ... }
   
   record PreReadingResponse(List<KeyWordDto> keyWords, String predictiveQuestion) {}
   record KeyWordDto(String word, String translation, String definition) {}
   ```
   - Auth: JWT required, extract userId from authentication details
   - Maps domain `PreReadingData` to response DTOs

6. **Tests**:
   - `GetPreReadingUseCaseTest`: valid article returns pre-reading data, not found throws, wrong owner throws
   - Stub returns fixed key words and predictive question

**Acceptance criteria**:
- [x] `GET /api/article/{id}/pre-reading` returns key words + predictive question
- [x] Requires authentication, validates article ownership
- [x] Returns 404 for non-existent article, 403 for wrong user
- [x] StubArticleAiPort implements `generatePreReading()`
- [x] Unit test covers happy path + error cases
- [x] Compile + tests green

---

### Phase 3: English Definition for Marked Words

**Goal**: Add English definition alongside Spanish translation when marking words

**Files to modify**:
- `src/main/java/com/faus535/englishtrainer/article/domain/ArticleAiPort.java` — extend `ArticleTranslationResult`
- `src/main/java/com/faus535/englishtrainer/article/domain/ArticleMarkedWord.java` — add `englishDefinition` field
- `src/main/java/com/faus535/englishtrainer/article/application/MarkWordUseCase.java` — pass definition to create
- `src/main/java/com/faus535/englishtrainer/article/infrastructure/ai/AnthropicArticleAiAdapter.java` — update tool schema + extraction
- `src/main/java/com/faus535/englishtrainer/article/infrastructure/persistence/ArticleMarkedWordEntity.java` — add column
- `src/main/java/com/faus535/englishtrainer/article/infrastructure/controller/MarkWordController.java` — update response DTO
- `src/main/resources/db/migration/V11.4.0__article_add_english_definition.sql` — migration
- `src/test/java/com/faus535/englishtrainer/article/application/MarkWordUseCaseTest.java` — update test
- `src/test/java/com/faus535/englishtrainer/article/infrastructure/ai/StubArticleAiPort.java` — update stub
- Test infrastructure: Object Mother, In-Memory repo adapters

**Details**:

1. **Migration `V11.4.0__article_add_english_definition.sql`**:
   ```sql
   ALTER TABLE article_marked_words ADD COLUMN english_definition TEXT;
   ```

2. **`ArticleAiPort.java`** — update result record:
   ```java
   record ArticleTranslationResult(String translation, String englishDefinition) {}
   ```

3. **`AnthropicArticleAiAdapter.java`**:
   - Update `TOOL_TRANSLATE_WORD` schema to add `english_definition` property:
     ```java
     "english_definition", Map.of("type", "string", "description", "Brief English definition of the word")
     ```
   - Add `"english_definition"` to required list
   - Update prompt to: `"Translate the English word/phrase \"%s\" to Spanish and provide a brief English definition. Context sentence: \"%s\"."`
   - Extract both from response:
     ```java
     return new ArticleTranslationResult(
         (String) input.get("translation"),
         (String) input.get("english_definition"));
     ```

4. **`ArticleMarkedWord.java`** — add `englishDefinition` field:
   - Add `private final String englishDefinition;` field
   - Update `create()`: add `englishDefinition` parameter
   - Update `reconstitute()`: add `englishDefinition` parameter
   - Add `public String englishDefinition()` accessor

5. **`MarkWordUseCase.java`** — pass definition:
   ```java
   ArticleMarkedWord marked = ArticleMarkedWord.create(articleId, userId, wordOrPhrase,
           tr.translation(), tr.englishDefinition(), contextSentence);
   ```

6. **`ArticleMarkedWordEntity.java`**:
   - Add `@Column(name = "english_definition", columnDefinition = "TEXT") private String englishDefinition;`
   - Update `fromDomain()` and `toDomain()` to map the new field

7. **`MarkWordController.MarkedWordResponse`** — add field:
   ```java
   record MarkedWordResponse(UUID id, String wordOrPhrase, String translation,
                              String englishDefinition, String contextSentence, Instant createdAt) {
       static MarkedWordResponse from(ArticleMarkedWord word) {
           return new MarkedWordResponse(word.id().value(), word.wordOrPhrase(),
                   word.translation(), word.englishDefinition(), word.contextSentence(), word.createdAt());
       }
   }
   ```
   - Note: `GetMarkedWordsController` reuses this record, so it automatically picks up the change

8. **Tests**:
   - Update `StubArticleAiPort.translateWord()` to return `new ArticleTranslationResult("translated", "brief definition")`
   - Update `ArticleMarkedWordMother` to include `englishDefinition`
   - Update `InMemoryArticleMarkedWordRepository` if needed
   - Update `MarkWordUseCaseTest` to verify `englishDefinition` is set

**Acceptance criteria**:
- [ ] Migration adds `english_definition` column (nullable for backwards compat)
- [ ] `POST /api/article/{id}/words` response includes `englishDefinition` field
- [ ] `GET /api/article/{id}/words` response includes `englishDefinition` field
- [ ] AI prompt asks for English definition alongside translation
- [ ] All existing tests updated and pass
- [ ] Compile + tests green

---

### Phase 4: Progress Persistence

**Goal**: Persist reading progress (paragraph index, question index) so users can resume where they left off

**Files to create**:
- `src/main/java/com/faus535/englishtrainer/article/application/UpdateArticleProgressUseCase.java` — use case
- `src/main/java/com/faus535/englishtrainer/article/infrastructure/controller/UpdateProgressController.java` — PATCH endpoint
- `src/main/java/com/faus535/englishtrainer/article/domain/error/ArticleCannotUpdateProgressException.java` — for COMPLETED articles
- `src/test/java/com/faus535/englishtrainer/article/application/UpdateArticleProgressUseCaseTest.java` — unit test

**Files to modify**:
- `src/main/java/com/faus535/englishtrainer/article/domain/ArticleReading.java` — add progress fields
- `src/main/java/com/faus535/englishtrainer/article/infrastructure/persistence/ArticleReadingEntity.java` — add progress columns
- `src/main/java/com/faus535/englishtrainer/article/infrastructure/controller/ArticleResponse.java` — add progress to response
- `src/main/java/com/faus535/englishtrainer/article/infrastructure/controller/ArticleControllerAdvice.java` — handle new exception
- `src/main/resources/db/migration/V11.5.0__article_add_progress_fields.sql` — migration
- `src/test/java/com/faus535/englishtrainer/article/domain/ArticleReadingTest.java` — test progress update
- Test Mothers and in-memory repos as needed

**Details**:

1. **Migration `V11.5.0__article_add_progress_fields.sql`**:
   ```sql
   ALTER TABLE article_readings ADD COLUMN current_paragraph_index INTEGER NOT NULL DEFAULT 0;
   ALTER TABLE article_readings ADD COLUMN current_question_index INTEGER NOT NULL DEFAULT 0;
   ```

2. **`ArticleReading.java`** — add fields:
   - Add `private final int currentParagraphIndex;` and `private final int currentQuestionIndex;`
   - Update constructors, `create()` (defaults to 0, 0), `reconstitute()`
   - Add method:
     ```java
     public ArticleReading withProgress(int paragraphIndex, int questionIndex) {
         return new ArticleReading(id, userId, topic, level, title, status, paragraphs,
                 xpEarned, createdAt, paragraphIndex, questionIndex);
     }
     ```
   - Update all existing factory methods (`markProcessing`, `markReady`, `markFailed`, `complete`, `withTitleAndParagraphs`) to preserve progress fields
   - Add accessors: `currentParagraphIndex()`, `currentQuestionIndex()`

3. **`ArticleReadingEntity.java`**:
   - Add columns:
     ```java
     @Column(name = "current_paragraph_index", nullable = false)
     private int currentParagraphIndex;
     
     @Column(name = "current_question_index", nullable = false)
     private int currentQuestionIndex;
     ```
   - Update `fromDomain()`, `toDomain()`, `updateFrom()` to map the new fields

4. **`UpdateArticleProgressUseCase.java`**:
   ```java
   @UseCase
   public class UpdateArticleProgressUseCase {
       private final ArticleReadingRepository articleReadingRepository;
       
       public void execute(UUID userId, ArticleReadingId articleId,
                           int paragraphIndex, int questionIndex)
               throws ArticleNotFoundException, ArticleAccessDeniedException {
           ArticleReading article = // find + validate ownership
           if (article.status() == ArticleStatus.COMPLETED) return; // silently ignore
           articleReadingRepository.save(article.withProgress(paragraphIndex, questionIndex));
       }
   }
   ```

5. **`UpdateProgressController.java`**:
   ```java
   @PatchMapping("/api/article/{id}/progress")
   ResponseEntity<Void> handle(@PathVariable UUID id,
                                @Valid @RequestBody UpdateProgressRequest request,
                                Authentication authentication) { ... }
   
   record UpdateProgressRequest(
       @Min(0) int paragraphIndex,
       @Min(0) int questionIndex
   ) {}
   ```
   - Auth: JWT required
   - Returns 204 No Content on success

6. **`ArticleResponse.java`** — add progress fields:
   ```java
   record ArticleResponse(UUID id, String title, String topic, String level, String status,
                            List<ParagraphResponse> paragraphs, int currentParagraphIndex,
                            int currentQuestionIndex, Instant createdAt) {
       static ArticleResponse from(ArticleReading reading) {
           // ... map including reading.topic().value(), reading.currentParagraphIndex(), reading.currentQuestionIndex()
       }
   }
   ```

7. **Tests**:
   - `UpdateArticleProgressUseCaseTest`: valid update saves, wrong owner throws, completed article silently ignored
   - `ArticleReadingTest`: `withProgress()` returns correct values
   - Update `ArticleReadingMother.reconstitute()` calls to include progress defaults

**Acceptance criteria**:
- [ ] Migration adds `current_paragraph_index` and `current_question_index` columns with default 0
- [ ] `PATCH /api/article/{id}/progress` updates progress in DB
- [ ] `GET /api/article/{id}` response includes `currentParagraphIndex` and `currentQuestionIndex`
- [ ] `GET /api/article/{id}` response includes `topic` field
- [ ] Progress update on completed article is silently ignored (not error)
- [ ] Unit tests cover happy path, wrong owner, completed article
- [ ] Compile + tests green

---

## API Contract

### `POST /api/article/{id}/questions/{qId}/answer` (modified)
- **Request body**: `{ "answer": "string (max 5000)" }`
- **Response body**: `{ "id": "uuid", "isContentCorrect": true, "grammarFeedback": "...", "styleFeedback": "...", "correctionSummary": "..." }`
- **Status codes**: 200 OK, 404 Not Found, 403 Forbidden, 409 Conflict (already answered), 422 Unprocessable Entity (too short — now level-based), 502 Bad Gateway (AI error)
- **Auth**: JWT required
- **Change**: 422 error now shows level-specific minimum (e.g., "minimum 20" for B1 instead of always "minimum 40")

### `GET /api/article/{id}/pre-reading` (new)
- **Request body**: none
- **Response body**: `{ "keyWords": [{ "word": "resilience", "translation": "resiliencia", "definition": "the ability to recover quickly" }], "predictiveQuestion": "What do you think this article will discuss about climate change?" }`
- **Status codes**: 200 OK, 404 Not Found, 403 Forbidden, 502 Bad Gateway (AI error)
- **Auth**: JWT required

### `POST /api/article/{id}/words` (modified)
- **Request body**: `{ "wordOrPhrase": "resilience", "contextSentence": "The city showed remarkable resilience." }`
- **Response body**: `{ "id": "uuid", "wordOrPhrase": "resilience", "translation": "resiliencia — capacidad de recuperarse", "englishDefinition": "the ability to recover quickly from difficulties", "contextSentence": "...", "createdAt": "..." }`
- **Status codes**: 201 Created, 404 Not Found, 403 Forbidden, 409 Conflict (duplicate), 502 Bad Gateway
- **Auth**: JWT required
- **Change**: Response now includes `englishDefinition` field

### `GET /api/article/{id}/words` (modified)
- **Change**: Each word in response now includes `englishDefinition` field

### `PATCH /api/article/{id}/progress` (new)
- **Request body**: `{ "paragraphIndex": 3, "questionIndex": 1 }`
- **Response body**: none (204)
- **Status codes**: 204 No Content, 404 Not Found, 403 Forbidden
- **Auth**: JWT required

### `GET /api/article/{id}` (modified)
- **Response body**: `{ "id": "uuid", "title": "...", "topic": "Climate Change", "level": "B2", "status": "READY", "paragraphs": [...], "currentParagraphIndex": 3, "currentQuestionIndex": 0, "createdAt": "..." }`
- **Change**: Response now includes `topic`, `currentParagraphIndex`, `currentQuestionIndex` fields

## Database Changes

### V11.4.0__article_add_english_definition.sql
```sql
ALTER TABLE article_marked_words ADD COLUMN english_definition TEXT;
```

### V11.5.0__article_add_progress_fields.sql
```sql
ALTER TABLE article_readings ADD COLUMN current_paragraph_index INTEGER NOT NULL DEFAULT 0;
ALTER TABLE article_readings ADD COLUMN current_question_index INTEGER NOT NULL DEFAULT 0;
```

## Testing Strategy

- **Per-phase testing**: Each phase includes its own unit tests (no separate testing phase)
- **Domain tests**: Level-based validation boundaries in `ArticleQuestionAnswerTest`, progress in `ArticleReadingTest`
- **Use case tests**: All new/modified use cases with in-memory repos + stub AI port
- **Object Mothers**: Update existing `ArticleReadingMother`, `ArticleMarkedWordMother`, `ArticleQuestionAnswerMother`
- **Stubs**: Update `StubArticleAiPort` for new `generatePreReading()` and updated `translateWord()` signatures
- **Integration tests**: Existing `ArticleLifecycleIT` and `ArticleReadingRepositoryIT` should still pass; optionally extend for progress persistence
- **Verification cycle per phase**: `./gradlew build` must pass before committing
