# Backend Plan: Async Immerse Content Generation + Security Fixes

> Generated: 2026-04-06
> Request: Make immerse content generation async (return PENDING immediately, generate AI content in background, frontend polls GET /content/{id}). Fix IDOR on GET /content/{id}. Add input validation.

## Decisions Log

| # | Topic | Decision | Alternatives Considered | Why |
|---|-------|----------|------------------------|-----|
| 1 | Placeholder for NOT NULL `title` and `raw_text` | Store contentType name as `title`, empty string `""` as `raw_text` for PENDING rows | Add new `requested_level`/`requested_topic` columns, make title nullable | Avoids migration, keeps schema stable. The async service overwrites both on PROCESSED. Empty string satisfies NOT NULL constraint. |
| 2 | Where to store level/topic for async pickup | Pass `level` and `topic` directly to the async method as parameters | Store in new DB columns `requested_level`/`requested_topic` | Simpler, no migration needed. The async method receives them as arguments in the same JVM. If the JVM crashes, PENDING is stuck regardless. |
| 3 | `markProcessed()` signature | Extend to include `title` and `rawText` parameters | Keep current signature, set title/rawText at generate() time | The async service produces the real title and rawText from AI; PENDING rows have placeholders. markProcessed() must overwrite them. |
| 4 | `@EnableAsync` placement | New `AsyncConfig` class in `shared/infrastructure/config/` | On the main Application class | Keeps config modular and reusable for future async needs. |
| 5 | IDOR fix scope | Fix GET /content/{id} and GET /content/{id}/exercises | Only GET /content/{id} | Both endpoints expose content by UUID without ownership check; both must be fixed together. |
| 6 | Transaction splitting | AI call happens outside `@Transactional` in the async service | Wrap everything in one transaction | Holding a DB connection for 30-60s during AI call is unacceptable. Save PENDING in TX1, AI call outside TX, update in TX2. |
| 7 | Error handling in `@Async` | Catch `Throwable` (not just `ImmerseAiException`) in async method | Only catch checked exceptions | Prevents PENDING-forever on unexpected RuntimeExceptions. |
| 8 | Migration for nullable title/rawText | Use `ALTER COLUMN ... DROP NOT NULL` migration | Placeholder approach | DECISION REVERSED: Actually keep NOT NULL with placeholders (Decision 1). No migration for nullable columns. |

## Analysis

### Existing code relevant to this feature

- **`ImmerseContent.java`** (domain aggregate): Has `generate()` factory that creates PROCESSED status immediately. Has `markProcessed()` and `markFailed()` methods. Title and rawText are final fields set at construction.
- **`GenerateImmerseContentUseCase.java`**: Synchronously calls `aiPort.generateContent()`, creates PROCESSED content, saves exercises, returns result. All in one `@Transactional`.
- **`GenerateImmerseContentController.java`**: POST /api/immerse/generate. Calls use case, returns 201 with full content. Throws `ImmerseAiException`.
- **`GetImmerseContentController.java`**: GET /api/immerse/content/{id}. No Authentication parameter -- **no ownership check (IDOR)**.
- **`GetImmerseExercisesController.java`**: GET /api/immerse/content/{id}/exercises. Also **no ownership check (IDOR)**.
- **`GetImmerseContentUseCase.java`** / **`GetImmerseExercisesUseCase.java`**: No userId parameter. Fetch by ID only.
- **`ImmerseContentEntity.java`**: `updateFrom()` does NOT update `title` or `rawText` -- only processedText, cefrLevel, vocabulary, contentType, status.
- **`ImmerseContentResponse.java`**: Already exposes `status` field.
- **DB schema**: `title VARCHAR(500) NOT NULL`, `raw_text TEXT NOT NULL`. Status defaults to PENDING.
- **`ImmerseControllerAdvice.java`**: Handles `ImmerseAiException` with 503 response.
- **Tests**: `GenerateImmerseContentUseCaseTest` asserts PROCESSED status. `StubImmerseAiPort` returns canned data.

## Phases

### Phase 1: Domain changes -- async-ready aggregate + extended markProcessed

**Goal**: Make `ImmerseContent` support the PENDING-then-PROCESSED lifecycle for generated content. Extend `markProcessed()` to accept title and rawText.

**Files to modify**:
- `src/main/java/com/faus535/englishtrainer/immerse/domain/ImmerseContent.java` -- change `generate()` factory and `markProcessed()` signature

**Details**:
1. Change `generate(UUID userId, ContentType contentType, String title, String rawText, String processedText, String cefrLevel, List<VocabularyItem> vocabulary)` to `generate(UUID userId, ContentType contentType, String level, String topic)`:
   - Set `title` to `contentType.value() + " content"` (placeholder)
   - Set `rawText` to `""` (satisfies NOT NULL)
   - Set `processedText`, `cefrLevel`, `extractedVocabulary` to `null`/`List.of()`
   - Set `status` to `PENDING`
   - Store no sourceUrl (null)
2. Change `markProcessed(String processedText, String detectedLevel, List<VocabularyItem> vocabulary)` to `markProcessed(String title, String rawText, String processedText, String detectedLevel, List<VocabularyItem> vocabulary)`:
   - New constructor call passes the new `title` and `rawText` values
3. Update `reconstitute()` -- no change needed, already generic

**Acceptance criteria**:
- [x] `ImmerseContent.generate(userId, TEXT, "b1", "city life")` returns content with status PENDING, placeholder title, null rawText
- [x] `markProcessed(title, rawText, processed, level, vocab)` returns content with status PROCESSED and the real title/rawText
- [x] Existing `submit()` factory unchanged
- [x] `markFailed()` unchanged

---

### Phase 2: Entity mapping -- updateFrom includes title and rawText

**Goal**: Ensure persistence layer correctly maps title and rawText when updating from PENDING to PROCESSED.

**Files to modify**:
- `src/main/java/com/faus535/englishtrainer/immerse/infrastructure/persistence/ImmerseContentEntity.java` -- extend `updateFrom()`

**Details**:
1. In `updateFrom(ImmerseContent aggregate)`, add:
   ```java
   this.title = aggregate.title();
   this.rawText = aggregate.rawText();
   ```

**Acceptance criteria**:
- [x] `updateFrom()` persists title and rawText changes (not just status/processedText)

---

### Phase 3: Async infrastructure -- AsyncConfig + ProcessImmerseContentAsyncService

**Goal**: Create the async processing service that generates AI content in the background.

**Files to create**:
- `src/main/java/com/faus535/englishtrainer/shared/infrastructure/config/AsyncConfig.java` -- @EnableAsync + ThreadPoolTaskExecutor bean
- `src/main/java/com/faus535/englishtrainer/immerse/application/ProcessImmerseContentAsyncService.java` -- @Async background processor

**Details**:

1. **AsyncConfig.java**:
   ```java
   @Configuration
   @EnableAsync
   public class AsyncConfig {
       @Bean("immerseAsyncExecutor")
       public Executor immerseAsyncExecutor() {
           ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
           executor.setCorePoolSize(2);
           executor.setMaxPoolSize(5);
           executor.setQueueCapacity(100);
           executor.setThreadNamePrefix("immerse-async-");
           executor.initialize();
           return executor;
       }
   }
   ```

2. **ProcessImmerseContentAsyncService.java**:
   - `@Service` class with `@Async("immerseAsyncExecutor")` on its `process()` method
   - Constructor dependencies: `ImmerseContentRepository`, `ImmerseExerciseRepository`, `ImmerseAiPort`
   - `public void process(UUID contentId, ContentType contentType, String level, String topic)`:
     a. Fetch content by ID. If not found, log warning and return.
     b. Call `aiPort.generateContent(contentType, level, topic)` -- **outside any @Transactional** to avoid holding DB connection during AI call
     c. Call `content.markProcessed(result.title(), result.rawText(), result.processedText(), result.detectedLevel(), result.vocabulary())`
     d. Save content (`@Transactional` for the save+exercises block)
     e. Create and save exercises from `result.exercises()`
     f. **Catch `Throwable`**: call `content.markFailed()`, save, log error
   - IMPORTANT: The `process()` method should call a separate `@Transactional` private helper (or split into two methods) to keep the AI call outside the transaction. Alternatively, use `TransactionTemplate` or call a transactional helper bean.
   - Since `@Transactional` on private methods does not work with Spring proxies, use one of:
     - Option A: Inject a `TransactionTemplate` and wrap save operations
     - Option B: Create a separate `@Service` helper with `@Transactional` methods
     - **Preferred**: Option A (TransactionTemplate) for simplicity

**Acceptance criteria**:
- [ ] `AsyncConfig` defines `immerseAsyncExecutor` bean with bounded thread pool
- [ ] `ProcessImmerseContentAsyncService.process()` is `@Async("immerseAsyncExecutor")`
- [ ] AI call happens outside any transaction
- [ ] On success: content is PROCESSED with title, rawText, exercises saved
- [ ] On failure (any Throwable): content is FAILED, error is logged
- [ ] On content not found: logs warning, returns without error

---

### Phase 4: Rewire GenerateImmerseContentUseCase -- return PENDING, trigger async

**Goal**: Make the generate endpoint return immediately with PENDING status and kick off background processing.

**Files to modify**:
- `src/main/java/com/faus535/englishtrainer/immerse/application/GenerateImmerseContentUseCase.java` -- remove sync AI call, add async trigger
- `src/main/java/com/faus535/englishtrainer/immerse/infrastructure/controller/GenerateImmerseContentController.java` -- remove `ImmerseAiException` from throws, add input validation

**Details**:

1. **GenerateImmerseContentUseCase.java**:
   - Remove `ImmerseAiPort` and `ImmerseExerciseRepository` dependencies
   - Add `ProcessImmerseContentAsyncService` dependency
   - Change `execute()`:
     a. Validate user profile exists (keep)
     b. `ImmerseContent content = ImmerseContent.generate(userId, contentType, level, topic)`
     c. `ImmerseContent saved = contentRepository.save(content)`
     d. `asyncService.process(saved.id().value(), contentType, level, topic)`
     e. Return `saved` (PENDING)
   - Remove `throws ImmerseAiException` from signature (keep `throws UserProfileNotFoundException`)

2. **GenerateImmerseContentController.java**:
   - Remove `throws ImmerseAiException` from `handle()` method
   - Add validation to `GenerateContentRequest`:
     ```java
     @Pattern(regexp = "(?i)a1|a2|b1|b2|c1|c2", message = "Level must be a valid CEFR level")
     String level,
     @Size(max = 200, message = "Topic must be at most 200 characters")
     String topic
     ```

**Acceptance criteria**:
- [ ] POST /api/immerse/generate returns 201 with status "PENDING"
- [ ] Response is immediate (no AI call on HTTP thread)
- [ ] Background processing is triggered
- [ ] `level` validated against CEFR values (a1-c2), null allowed
- [ ] `topic` validated with max 200 chars, null allowed
- [ ] Invalid level returns 400
- [ ] `ImmerseAiException` no longer thrown by controller

---

### Phase 5: Security -- IDOR fix on GET endpoints

**Goal**: Fix IDOR vulnerability on GET /content/{id} and GET /content/{id}/exercises by adding ownership checks.

**Files to modify**:
- `src/main/java/com/faus535/englishtrainer/immerse/application/GetImmerseContentUseCase.java` -- add userId parameter
- `src/main/java/com/faus535/englishtrainer/immerse/infrastructure/controller/GetImmerseContentController.java` -- extract userId from JWT, pass to use case
- `src/main/java/com/faus535/englishtrainer/immerse/application/GetImmerseExercisesUseCase.java` -- add userId parameter
- `src/main/java/com/faus535/englishtrainer/immerse/infrastructure/controller/GetImmerseExercisesController.java` -- extract userId from JWT, pass to use case

**Files to create**:
- `src/main/java/com/faus535/englishtrainer/immerse/domain/error/ImmerseContentAccessDeniedException.java` -- new checked exception

**Details**:

1. **ImmerseContentAccessDeniedException.java**:
   ```java
   public class ImmerseContentAccessDeniedException extends ImmerseException {
       public ImmerseContentAccessDeniedException(ImmerseContentId id) {
           super("Access denied to immerse content: " + id.value());
       }
   }
   ```

2. **GetImmerseContentUseCase.java**:
   - Change `execute(UUID contentId)` to `execute(UUID contentId, UUID userId)`
   - After fetching content, check `if (!content.userId().equals(userId))` throw `ImmerseContentAccessDeniedException`

3. **GetImmerseContentController.java**:
   - Add `Authentication authentication` parameter
   - Extract `userId` from JWT details (same pattern as GenerateImmerseContentController)
   - Pass to `useCase.execute(id, userId)`

4. **GetImmerseExercisesUseCase.java**:
   - Change `execute(UUID contentId)` to `execute(UUID contentId, UUID userId)`
   - After fetching content, check ownership before checking PROCESSED status

5. **GetImmerseExercisesController.java**:
   - Add `Authentication authentication` parameter
   - Extract `userId` from JWT details
   - Pass to `useCase.execute(id, userId)`

6. **ImmerseControllerAdvice.java**:
   - Add handler for `ImmerseContentAccessDeniedException` returning 403 FORBIDDEN

**Acceptance criteria**:
- [ ] GET /content/{id} with wrong user returns 403
- [ ] GET /content/{id}/exercises with wrong user returns 403
- [ ] GET /content/{id} with correct user returns content as before
- [ ] GET /content/{id}/exercises with correct user returns exercises as before
- [ ] Both controllers inject Authentication from SecurityContext

---

### Phase 6: Tests -- update existing + add new

**Goal**: Update all affected tests and add new tests for async service and security.

**Files to modify**:
- `src/test/java/com/faus535/englishtrainer/immerse/domain/ImmerseContentTest.java` -- add test for new generate() factory
- `src/test/java/com/faus535/englishtrainer/immerse/domain/ImmerseContentMother.java` -- add `pendingGenerated()` factory, update `generated()` to use old reconstitute pattern
- `src/test/java/com/faus535/englishtrainer/immerse/application/GenerateImmerseContentUseCaseTest.java` -- assert PENDING, no exercises saved
- `src/test/java/com/faus535/englishtrainer/immerse/application/GetImmerseContentUseCaseTest.java` -- add ownership check tests
- `src/test/java/com/faus535/englishtrainer/immerse/application/GetImmerseExercisesUseCaseTest.java` -- add ownership check tests

**Files to create**:
- `src/test/java/com/faus535/englishtrainer/immerse/application/ProcessImmerseContentAsyncServiceTest.java` -- unit tests for async service
- `src/test/java/com/faus535/englishtrainer/immerse/infrastructure/FailingStubImmerseAiPort.java` -- stub that always throws

**Details**:

1. **ImmerseContentTest.java** -- add:
   - `generateCreatesPendingContent()`: verify `generate(userId, TEXT, "b1", "topic")` returns PENDING with placeholder title
   - `markProcessedUpdatesAllFields()`: verify markProcessed sets title, rawText, processedText, level, vocab
   - Update `generateCreatesProcessedContentWithContentType()` to match new PENDING behavior

2. **ImmerseContentMother.java** -- add:
   - `pendingGenerated()`: uses new `generate()` factory, returns PENDING content
   - `pendingGeneratedWithId(ImmerseContentId id, UUID userId)`: for async service tests
   - Keep `generated()` using `reconstitute()` with PROCESSED status for backward compatibility

3. **GenerateImmerseContentUseCaseTest.java**:
   - Update constructor: remove exerciseRepository and aiPort, add mock/stub async service
   - Since `ProcessImmerseContentAsyncService` is a concrete `@Service`, create a test subclass or use a spy pattern
   - **Preferred**: Create a `SpyProcessImmerseContentAsyncService` extending the real class, overriding `process()` to record calls
   - Assert: result status is PENDING, content saved in repository, async process triggered, no exercises saved synchronously

4. **ProcessImmerseContentAsyncServiceTest.java**:
   - `processesPendingContentSuccessfully()`: content goes from PENDING to PROCESSED, exercises saved
   - `marksContentFailedWhenAiThrows()`: uses FailingStubImmerseAiPort, content goes to FAILED
   - `doesNothingWhenContentNotFound()`: no exception thrown, no state change

5. **FailingStubImmerseAiPort.java**:
   ```java
   public class FailingStubImmerseAiPort implements ImmerseAiPort {
       public ImmerseProcessResult processContent(...) throws ImmerseAiException {
           throw new ImmerseAiException("AI unavailable");
       }
       public ImmerseGenerateResult generateContent(...) throws ImmerseAiException {
           throw new ImmerseAiException("AI unavailable");
       }
   }
   ```

6. **GetImmerseContentUseCaseTest.java** -- add:
   - `throwsAccessDeniedForWrongUser()`: content belongs to user A, user B tries to access
   - Update existing tests to pass userId parameter

7. **GetImmerseExercisesUseCaseTest.java** -- add:
   - `throwsAccessDeniedForWrongUser()`
   - Update existing tests to pass userId parameter

**Acceptance criteria**:
- [ ] All existing tests updated to reflect new signatures and PENDING behavior
- [ ] `ProcessImmerseContentAsyncServiceTest` covers success, AI failure, and content-not-found
- [ ] Ownership check tests verify 403 behavior for wrong user
- [ ] `./gradlew test` passes with all tests green

---

### Phase 7: Validate with /revisar

**Goal**: Run the review skill to validate architecture, naming conventions, and code quality.

**Files to modify**: None (validation only)

**Details**:
1. Run `/revisar` to validate all changes
2. Fix any issues found
3. Verify `./gradlew build` compiles cleanly
4. Verify `./gradlew test` passes all tests

**Acceptance criteria**:
- [ ] `/revisar` reports no critical issues
- [ ] `./gradlew build` succeeds
- [ ] `./gradlew test` passes all tests
- [ ] No regressions in existing functionality

## API Contract

### `POST /api/immerse/generate`
- **Auth**: Required (JWT)
- **Request body**:
  ```json
  {
    "contentType": "TEXT",
    "level": "b1",
    "topic": "city life"
  }
  ```
  - `contentType`: required, one of `TEXT`, `AUDIO`, `VIDEO`
  - `level`: optional, CEFR level (`a1`-`c2`, case-insensitive)
  - `topic`: optional, max 200 characters
- **Response body** (201 Created):
  ```json
  {
    "id": "uuid",
    "title": "TEXT content",
    "sourceUrl": null,
    "processedText": null,
    "cefrLevel": null,
    "extractedVocabulary": [],
    "contentType": "TEXT",
    "status": "PENDING",
    "createdAt": "2026-04-06T12:00:00Z"
  }
  ```
- **Status codes**: 201 Created, 400 Bad Request (validation), 401 Unauthorized, 404 Profile Not Found
- **Breaking change**: Previously returned PROCESSED with full content. Now returns PENDING immediately.

### `GET /api/immerse/content/{id}`
- **Auth**: Required (JWT) -- ownership enforced
- **Response body** (200 OK):
  ```json
  {
    "id": "uuid",
    "title": "A Day in the City",
    "sourceUrl": null,
    "processedText": "The city wakes up...",
    "cefrLevel": "b1",
    "extractedVocabulary": [...],
    "contentType": "TEXT",
    "status": "PROCESSED",
    "createdAt": "2026-04-06T12:00:00Z"
  }
  ```
- **Status codes**: 200 OK, 401 Unauthorized, 403 Forbidden (wrong user), 404 Not Found
- **Polling**: Frontend polls this endpoint until `status` is `PROCESSED` or `FAILED`

### `GET /api/immerse/content/{id}/exercises`
- **Auth**: Required (JWT) -- ownership enforced
- **Status codes**: 200 OK, 401 Unauthorized, 403 Forbidden (wrong user), 404 Not Found, 422 Not Processed

## Database Changes

No new Flyway migrations required. The existing schema supports this change:
- `title VARCHAR(500) NOT NULL` -- placeholder value (`"TEXT content"`) used for PENDING rows, overwritten on PROCESSED
- `raw_text TEXT NOT NULL` -- empty string `""` used for PENDING rows, overwritten on PROCESSED
- `status` already supports `PENDING`, `PROCESSED`, `FAILED`
- Level and topic are passed as method parameters to the async service (same JVM), not persisted

## Testing Strategy

| Layer | What | How |
|-------|------|-----|
| Domain | `ImmerseContent` generate/markProcessed lifecycle | `ImmerseContentTest` with new factory assertions |
| Application | `GenerateImmerseContentUseCase` returns PENDING | Unit test with spy async service |
| Application | `ProcessImmerseContentAsyncService` success/failure | Unit test with StubImmerseAiPort / FailingStubImmerseAiPort |
| Application | Ownership checks on GET use cases | Unit test with InMemoryRepository |
| Infrastructure | Entity mapping updates | Covered by integration via use case tests |

**Key testing decisions**:
- `@Async` has no effect in unit tests (methods run synchronously) -- this is an advantage for testing `ProcessImmerseContentAsyncService` in isolation
- No integration/Testcontainers tests needed for this phase (existing integration tests cover persistence layer)
- Spy pattern for async service in `GenerateImmerseContentUseCaseTest` to verify `process()` was called
