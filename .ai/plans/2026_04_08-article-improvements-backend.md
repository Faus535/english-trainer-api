# Backend Plan: Article Module Improvements

> Generated: 2026-04-08
> Request: Article module improvements — async generation, history/delete endpoints, event decoupling, level-based scaffolding, integration tests + pedagogical improvements (pre-reading, English definitions, proportional XP)

## Decisions Log

| # | Topic | Decision | Alternatives Considered | Why |
|---|-------|----------|------------------------|-----|
| 1 | Async Strategy | ProcessArticleContentAsyncService with @Async + TransactionTemplate (immerse pattern) | Thread pool, reactive streams | Aligns with existing immerse module, reduces coupling, proven pattern in codebase |
| 2 | Event Decoupling | ArticleWordMarkedEvent + listener pattern instead of direct ReviewItemRepository | Keep direct injection, event sourcing | Reduces article→review coupling, enables future analytics/history features, matches review pattern |
| 3 | Status Enum | Extend ArticleStatus: PENDING, PROCESSING, READY, FAILED (new) + IN_PROGRESS, COMPLETED (legacy) | Migrate existing to new, use separate field | Backward compatible, clear lifecycle, matches immerse model |
| 4 | Cascading Questions | Questions generated async via ArticleReadingCompletedEvent listener after completion | Synchronous in CompleteArticle, background batch | Completes articles faster for UX, level-adapted generation decoupled, handles generation failures gracefully |
| 5 | XP Calculation | Base 25 + 5 per correct answer + 2 per marked word (proportional) | Fixed per article, cumulative by level | Rewards engagement, encourages both reading and interaction, scales with content difficulty |
| 6 | Level Scaffolding | B1: 20 min words + guided questions, B2: 30 min words + mixed, C1: 40 min words + analysis | Single threshold, percentage-based | Validates engagement, enforces learning progression, aligns with CEFR standards |
| 7 | Testing | Testcontainers IT + Unit tests for critical paths | Mocks only | Real database contracts verified, N+1 queries caught, constraints enforced |
| 8 | Phase Order | History → Delete → Decouple Events → Async → CompleteArticle refactor → Level Scaffolding → Tests | Reverse order, all at once | Dependencies: History/Delete need refactored CompleteArticle; Events needed for async; Async before Questions; Level scaffolding depends on async questions |

## Analysis

### Current Architecture

**Module structure:** `com.faus535.englishtrainer.article` follows package-per-aggregate (domain/application/infrastructure).

**Aggregate Root — ArticleReading:**
- Immutable, UUID-based (ArticleReadingId)
- Current status enum: `IN_PROGRESS`, `COMPLETED`
- Events: Only `ArticleReadingCompletedEvent` (registered but underutilized)
- Methods: `create()`, `reconstitute()`, `withTitleAndParagraphs()`, `complete()`

**Application layer (8 use cases):**
- GenerateArticleUseCase, GetArticleUseCase, MarkWordUseCase, SubmitAnswerUseCase, CompleteArticleUseCase, GetHintUseCase, GetMarkedWordsUseCase, ReviewItemUseCase

**Persistence:**
- JPA entities: ArticleReadingEntity, ArticleParagraphEntity, ArticleMarkedWordEntity, ArticleQuestionEntity, ArticleQuestionAnswerEntity
- 3 controllers: GenerateArticle, GetArticle, (MarkWord, SubmitAnswer, etc. in unified controller or split)
- AI adapter: AnthropicArticleAiAdapter

**Critical issue:** MarkWordUseCase directly injects ReviewItemRepository → breaks article/review boundary, prevents async article generation.

**Reference patterns in codebase:**
- **Async:** ProcessImmerseContentAsyncService uses @Async, TransactionTemplate, try-catch with markFailed()
- **Events:** ImmerseAnsweredReviewListener listens to domain events, creates review items transactionally
- **Extended status:** ImmerseContentStatus enum (PENDING, PROCESSED, FAILED) for async workflows

### Existing Database Schema

**Current tables:**
- `article_readings` (id, user_id, title, topic, level, status, created_at)
- `article_paragraphs` (id, article_reading_id, content, paragraph_order)
- `article_marked_words` (id, article_reading_id, word_or_phrase, translation, context_sentence)
- `article_questions` (id, article_reading_id, question_text, question_type, correct_answer, min_words)
- `article_question_answers` (id, question_id, user_answer, is_correct, submitted_at)

**Constraints:** UNIQUE on (user_id, status, created_at) composite; FK cascades on article_reading_id.

## Phases

### Phase 1: History, Delete, Enriched Questions (Endpoints Foundation)

**Goal:** Add three new read/write endpoints to expose article history, deletion, and enriched question data. Prepares foundation for async refactoring.

**Scope:** No architectural changes yet; new use cases + controllers + DTOs.

**Files to create:**
1. `article/application/GetArticleHistoryUseCase.java` — Query all articles for user, order DESC by createdAt
2. `article/application/DeleteArticleUseCase.java` — Delete only IN_PROGRESS articles, verify ownership
3. `article/domain/ArticleReadingSummary.java` (VO/record) — id, title, level, topic, status, createdAt, wordCount, questionsAnswered
4. `article/domain/QuestionWithAnswer.java` (VO/record) — questionId, questionText, answered, answer (or null)
5. `article/infrastructure/controller/GetArticleHistoryController.java` — GET /api/article/history
6. `article/infrastructure/controller/DeleteArticleController.java` — DELETE /api/article/{id}
7. `article/infrastructure/controller/GetArticleQuestionsWithAnswersController.java` — GET /api/article/{id}/questions

**Details:**

**GetArticleHistoryUseCase (new):**
```
execute(UUID userId) throws ArticleException
  - Query ArticleReadingRepository.findAllByUserIdOrderByCreatedAtDesc(userId)
  - For each ArticleReading:
    - Count ArticleMarkedWord records
    - Count ArticleQuestionAnswer records where is_correct = true
    - Build ArticleReadingSummary VO
  - Return List<ArticleReadingSummary>
```

**DeleteArticleUseCase (new):**
```
execute(UUID userId, ArticleReadingId articleReadingId) throws ArticleException, UserNotAuthorizedForArticleException
  - Load ArticleReading by ID
  - Verify ownership: article.userId == userId
  - Verify status: article.status == IN_PROGRESS only (cannot delete COMPLETED)
  - Delete cascade via ArticleReadingRepository.delete(article)
  - Commit transaction
```

**GetArticleQuestionsWithAnswersUseCase (new):**
```
execute(UUID userId, ArticleReadingId articleReadingId) throws ArticleException, UserNotAuthorizedForArticleException
  - Load ArticleReading by ID
  - Verify ownership
  - Query ArticleQuestionRepository.findAllByArticleReadingId(id) ordered by question_order (if applicable)
  - For each question:
    - Query ArticleQuestionAnswerRepository.findByQuestionId(id) — get most recent/only answer
    - Build QuestionWithAnswer VO (questionId, questionText, answered: answer != null, answer: answer?.userAnswer)
  - Return List<QuestionWithAnswer>
```

**Controllers (follow api-design skill):**
- `GetArticleHistoryController`: GET /api/article/history → 200 List<ArticleReadingSummary>
- `DeleteArticleController`: DELETE /api/article/{id} → 204 No Content (or 400 if COMPLETED)
- `GetArticleQuestionsWithAnswersController`: GET /api/article/{id}/questions → 200 List<QuestionWithAnswer>

**DTOs/VOs:**
- `ArticleReadingSummary` record: { articleReadingId: UUID, title: String, level: String, topic: String, status: String, createdAt: LocalDateTime, wordCount: Int, questionsAnswered: Int }
- `QuestionWithAnswer` record: { questionId: UUID, questionText: String, answered: Boolean, answer: String }

**Security:**
- All endpoints require JWT (ROLE_USER)
- Ownership verified before returning data or deleting
- Input validation: articleReadingId format

**Testing:**
- Unit: GetArticleHistoryUseCaseTest (happy path, empty list, DESC order)
- Unit: DeleteArticleUseCaseTest (happy path, not found, different user, COMPLETED status error)
- Unit: GetArticleQuestionsWithAnswersUseCaseTest (happy path, no answers, ownership check)

**Acceptance criteria:**
- [x] GET /api/article/history returns user's articles DESC by createdAt with accurate word/question counts
- [x] DELETE /api/article/{id} removes only IN_PROGRESS articles, returns 204
- [x] DELETE returns 400 if article COMPLETED, 404 if not found, 403 if different user
- [x] GET /api/article/{id}/questions returns list with answered flag and user answer (null if not answered)
- [x] All endpoints require JWT authentication
- [x] Unit tests pass for all three use cases

---

### Phase 2: Event Decoupling (MarkWord → Review Bridge)

**Goal:** Decouple MarkWord use case from ReviewItemRepository by publishing domain event. Enables independent article/review scaling and prepares for article async generation.

**Scope:** Refactor MarkWordUseCase, add event, add listener in review module.

**Files to create/modify:**
1. `article/domain/event/ArticleWordMarkedEvent.java` (new) — Domain event, @DomainEvent
2. `article/application/MarkWordUseCase.java` (modify) — Remove ReviewItemRepository, publish event instead
3. `review/application/CreateReviewItemFromArticleUseCase.java` (new) — Listen to event, create review item
4. `review/infrastructure/event/ArticleWordMarkedReviewListener.java` (new) — @TransactionalEventListener

**Details:**

**ArticleWordMarkedEvent (new domain event):**
```java
@DomainEvent
public class ArticleWordMarkedEvent {
    public ArticleWordMarkedEvent(
        ArticleReadingId articleReadingId,
        UUID userId,
        ArticleMarkedWordId markedWordId,
        String wordOrPhrase,
        String translation,
        String contextSentence
    ) { ... }
    
    public ArticleReadingId articleReadingId() { ... }
    public UUID userId() { ... }
    public ArticleMarkedWordId markedWordId() { ... }
    public String wordOrPhrase() { ... }
    public String translation() { ... }
    public String contextSentence() { ... }
}
```

**MarkWordUseCase (refactored):**
```
Before:
  - Inject ReviewItemRepository
  - Create ArticleMarkedWord
  - Create ReviewItem via ReviewItemRepository

After:
  - Inject ArticleReadingRepository, DomainEventPublisher (Spring's ApplicationEventPublisher)
  - Create ArticleMarkedWord
  - Save to article (via aggregate method or direct repository)
  - Publish ArticleWordMarkedEvent
  - Return (do NOT create ReviewItem)
```

**CreateReviewItemFromArticleUseCase (new, in review module):**
```
execute(ArticleWordMarkedEvent event) throws ReviewItemException
  - Create ReviewItem with sourceType = ARTICLE, sourceId = event.markedWordId()
  - Save via ReviewItemRepository
```

**ArticleWordMarkedReviewListener (new, in review/infrastructure/event):**
```java
@Component
public class ArticleWordMarkedReviewListener {
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void on(ArticleWordMarkedEvent event) {
        createReviewItemFromArticleUseCase.execute(event);
    }
}
```

**Security:**
- Event payload includes userId — listener can validate authorization if needed
- ReviewItem inherits article review rules

**Testing:**
- Unit: MarkWordUseCaseTest — verify event published, ReviewItem NOT created directly
- Unit: CreateReviewItemFromArticleUseCaseTest — happy path, persistence
- Integration: ArticleWordMarkedReviewListenerIT (Testcontainers) — event published → review item created

**Acceptance criteria:**
- [x] MarkWordUseCase no longer injects ReviewItemRepository
- [x] MarkWordUseCase publishes ArticleWordMarkedEvent
- [x] ArticleWordMarkedReviewListener listens and creates ReviewItem transactionally
- [x] Unit + integration tests pass
- [x] No functional change to user (still creates review items)

---

### Phase 3: Async Article Generation (Status Enum + ProcessService)

**Goal:** Make article generation asynchronous. User gets immediate 202 PENDING response; questions generated in background via listener. Mirrors immerse pattern.

**Scope:** Extend ArticleStatus, add async service, modify GenerateArticleUseCase, add migration.

**Files to create/modify:**
1. `article/domain/ArticleStatus.java` (modify) — Add PENDING, PROCESSING, READY, FAILED (keep legacy IN_PROGRESS, COMPLETED for backward compat)
2. `article/domain/ArticleReading.java` (modify) — Add markProcessing(), markReady(), markFailed() methods
3. `article/application/GenerateArticleUseCase.java` (modify) — Return 202 PENDING instead of 200 with full article
4. `article/application/ProcessArticleContentAsyncService.java` (new) — @Async service, generates paragraphs via AI, calls markReady()
5. Database migration `V11.2.0__article_update_status_enum.sql`

**Details:**

**ArticleStatus (modified enum):**
```java
public enum ArticleStatus {
    PENDING,       // New: waiting for background processing
    PROCESSING,    // New: AI generation in progress
    READY,         // New: generation complete, questions may be pending
    FAILED,        // New: generation failed, user can retry
    IN_PROGRESS,   // Legacy: user is reading/answering (for backward compat)
    COMPLETED      // Legacy: user finished
}
```

**ArticleReading domain methods (new):**
```java
public void markProcessing() {
    this.status = ArticleStatus.PROCESSING;
}

public void markReady() {
    this.status = ArticleStatus.READY;
}

public void markFailed(String failureReason) {
    this.status = ArticleStatus.FAILED;
    this.failureReason = failureReason; // Add field to entity
}
```

**GenerateArticleUseCase (modified):**
```
execute(UUID userId, String topic, ArticleLevel level) throws ArticleException
  Before:
    - Synchronously generate paragraphs via AI
    - Save ArticleReading with status = IN_PROGRESS
    - Return full article (200 OK)
  
  After:
    - Create ArticleReading with status = PENDING, empty paragraphs
    - Save to repository
    - Publish ArticleGenerateRequestedEvent (new) OR call processArticleContentAsyncService.process(articleReadingId) directly
    - Return ArticleReadingSummary with status=PENDING (202 Accepted)
```

**ProcessArticleContentAsyncService (new async service):**
```java
@Service
@Async // Spring's async annotation
public class ProcessArticleContentAsyncService {
    
    public void processArticle(ArticleReadingId articleReadingId, UUID userId) {
        TransactionTemplate transactionTemplate = new TransactionTemplate(transactionManager);
        try {
            transactionTemplate.execute(status -> {
                ArticleReading article = articleReadingRepository.findById(articleReadingId);
                article.markProcessing();
                articleReadingRepository.save(article);
                return null;
            });
            
            // Call AI to generate paragraphs (synchronously here, async to caller)
            List<String> paragraphs = anthropicArticleAiAdapter.generateArticle(
                article.topic(), 
                article.level()
            );
            
            transactionTemplate.execute(status -> {
                ArticleReading article = articleReadingRepository.findById(articleReadingId);
                article = article.withTitleAndParagraphs(title, paragraphs);
                article.markReady();
                articleReadingRepository.save(article);
                // Publish ArticleReadyEvent to trigger question generation
                domainEventPublisher.publishEvent(new ArticleReadyEvent(articleReadingId, userId));
                return null;
            });
        } catch (Exception e) {
            transactionTemplate.execute(status -> {
                ArticleReading article = articleReadingRepository.findById(articleReadingId);
                article.markFailed(e.getMessage());
                articleReadingRepository.save(article);
                return null;
            });
            log.error("Failed to process article {}", articleReadingId, e);
        }
    }
}
```

**GenerateArticleController (modified response):**
```
Before: 200 OK { article: {...} }
After:  202 Accepted { id: UUID, status: "PENDING" }
```

**Database migration V11.2.0:**
```sql
-- Add new status values and failure reason column
ALTER TABLE article_readings DROP CONSTRAINT IF EXISTS article_readings_status_check;
ALTER TABLE article_readings ADD COLUMN IF NOT EXISTS failure_reason VARCHAR(500);
ALTER TABLE article_readings ADD CONSTRAINT article_readings_status_check 
  CHECK (status IN ('PENDING', 'PROCESSING', 'READY', 'FAILED', 'IN_PROGRESS', 'COMPLETED'));

-- Performance index for history queries
CREATE INDEX IF NOT EXISTS idx_article_readings_user_status_created 
  ON article_readings(user_id, status, created_at DESC);
```

**Security:**
- Async service validates userId matches before processing
- Error messages in failure_reason field (sanitized, no AI secrets)

**Testing:**
- Unit: GenerateArticleUseCaseTest — verify returns 202 PENDING
- Unit: ProcessArticleContentAsyncServiceTest — mock AI, verify markReady(), markFailed() called
- Integration: ProcessArticleContentAsyncServiceIT (Testcontainers) — verify database transitions through PENDING → PROCESSING → READY

**Acceptance criteria:**
- [ ] POST /api/article/generate returns 202 with status=PENDING
- [ ] Article status transitions PENDING → PROCESSING → READY asynchronously
- [ ] On AI failure, status = FAILED, failure_reason populated
- [ ] Database migration applied, new columns + index created
- [ ] Backward compatibility: legacy IN_PROGRESS/COMPLETED still supported
- [ ] Unit + integration tests pass

---

### Phase 4: Async Questions + Level Scaffolding (CompleteArticle Refactor)

**Goal:** Split CompleteArticleUseCase into pure state transition + event listener for async question generation with level-based scaffolding. Enables complete articles fast, questions generated in background.

**Scope:** Refactor CompleteArticleUseCase, add question generation listener with level logic, extend AI adapter.

**Files to create/modify:**
1. `article/application/CompleteArticleUseCase.java` (modify) — Only state transition + XP calc, publish event
2. `article/domain/event/ArticleReadyEvent.java` or reuse `ArticleReadingCompletedEvent` (review/extend)
3. `article/application/GenerateArticleQuestionsAsyncService.java` (new) — Listen to event, call AI with level
4. `article/infrastructure/event/ArticleReadyQuestionGenerationListener.java` (new) — @TransactionalEventListener
5. `article/domain/ArticleLevel.java` (modify if needed) — B1/B2/C1 with min_words, question_style
6. `article/infrastructure/adapter/ArticleAiPort.java` or `AnthropicArticleAiAdapter.java` (extend) — generateQuestionsAdapted(level, paragraphs)

**Details:**

**ArticleLevel VO (extended, if using record):**
```java
public record ArticleLevel(String code) {
    public int minWords() {
        return switch(code) {
            case "B1" -> 20;
            case "B2" -> 30;
            case "C1" -> 40;
            default -> 20;
        };
    }
    
    public String questionStyle() {
        return switch(code) {
            case "B1" -> "GUIDED";    // Multiple choice, fill-the-blank
            case "B2" -> "MIXED";     // Mix of guided + open
            case "C1" -> "ANALYSIS";  // Short answer, opinion-based
            default -> "GUIDED";
        };
    }
}
```

**CompleteArticleUseCase (refactored):**
```
Before:
  - Generate questions synchronously
  - Mark article as COMPLETED
  - Calculate XP
  - Return article with questions
  - UpdateXP use case

After:
  - Verify status = IN_PROGRESS or READY (after async gen)
  - Calculate XP: base 25 + (5 * correct_answers) + (2 * marked_words)
  - Mark article as COMPLETED
  - Persist XP to article_readings.xp_earned
  - Publish ArticleCompletedEvent (includes userID, XP)
  - Return minimal response (202 or 200)
  - Question generation triggered via listener listening to ArticleReadyEvent (from Phase 3)
```

**GenerateArticleQuestionsAsyncService (new):**
```java
@Service
@Async
public class GenerateArticleQuestionsAsyncService {
    
    public void generateQuestions(ArticleReadingId articleReadingId, UUID userId, ArticleLevel level) {
        TransactionTemplate transactionTemplate = new TransactionTemplate(transactionManager);
        try {
            ArticleReading article = transactionTemplate.execute(status -> {
                return articleReadingRepository.findById(articleReadingId);
            });
            
            List<ArticleParagraph> paragraphs = article.paragraphs();
            String paragraphsText = paragraphs.stream()
                .map(ArticleParagraph::content)
                .collect(Collectors.joining("\n\n"));
            
            // Call AI with level-adapted prompt
            List<GeneratedQuestion> questions = anthropicArticleAiAdapter.generateQuestionsAdapted(
                level,
                paragraphsText,
                level.minWords()
            );
            
            transactionTemplate.execute(status -> {
                for (GeneratedQuestion q : questions) {
                    ArticleQuestion question = ArticleQuestion.create(
                        articleReadingId,
                        q.text(),
                        q.type(),
                        q.correctAnswer(),
                        level.minWords()
                    );
                    articleQuestionRepository.save(question);
                }
                return null;
            });
        } catch (Exception e) {
            log.error("Failed to generate questions for article {}", articleReadingId, e);
            // Do NOT fail the article; questions are optional enhancement
        }
    }
}
```

**ArticleReadyQuestionGenerationListener (new):**
```java
@Component
public class ArticleReadyQuestionGenerationListener {
    
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void on(ArticleReadyEvent event) {
        // Retrieve article to get level
        ArticleReading article = articleReadingRepository.findById(event.articleReadingId());
        generateArticleQuestionsAsyncService.generateQuestions(
            event.articleReadingId(),
            event.userId(),
            article.level()
        );
    }
}
```

**CompleteArticleController (response change):**
```
Before: 200 OK { article: {...}, questionsCount: N }
After:  202 Accepted { id: UUID, status: "COMPLETED", xpEarned: 25 }
        (questions will be generated asynchronously)
```

**XP Calculation Logic (in CompleteArticleUseCase):**
```java
int xpEarned = 25; // Base XP
xpEarned += correctAnswersCount * 5;  // Per correct answer
xpEarned += markedWordsCount * 2;     // Per marked word (engagement bonus)
article.markCompleted(xpEarned);
userXpService.addXp(userId, xpEarned); // Update user XP aggregate
```

**AnthropicArticleAiAdapter extension:**
```java
// New method signature
public List<GeneratedQuestion> generateQuestionsAdapted(
    ArticleLevel level,
    String paragraphsText,
    int minWords
) throws Exception {
    // Build level-specific prompt
    String prompt = buildPrompt(level, paragraphsText, minWords);
    // Call Claude API
    return parseQuestions(response);
}

private String buildPrompt(ArticleLevel level, String text, int minWords) {
    return switch(level.code()) {
        case "B1" -> "Generate 3 multiple choice or fill-the-blank questions...";
        case "B2" -> "Generate 4 mixed difficulty questions...";
        case "C1" -> "Generate 3-4 short answer or opinion-based questions...";
        default -> "";
    };
}
```

**Database migration (extend V11.2.0 or new V11.3.0):**
```sql
ALTER TABLE article_readings 
  ADD COLUMN IF NOT EXISTS xp_earned INTEGER NOT NULL DEFAULT 0;

-- Index for XP leaderboard queries (if future feature)
CREATE INDEX IF NOT EXISTS idx_article_readings_user_xp_earned 
  ON article_readings(user_id, xp_earned DESC);
```

**Security:**
- Ownership check in CompleteArticleUseCase before XP assignment
- XP integer overflow protected: max ~725 per article (25 + 5*80 + 2*150), fits INT

**Testing:**
- Unit: CompleteArticleUseCaseTest — XP calc (base, correct answers, marked words), state transition
- Unit: GenerateArticleQuestionsAsyncServiceTest — level-adapted prompt, persistence
- Integration: ArticleReadyQuestionGenerationListenerIT — event triggers async generation
- Edge case: Concurrent SubmitAnswer on same question → UNIQUE constraint enforced by DB

**Acceptance criteria:**
- [ ] CompleteArticleUseCase returns 202, does NOT wait for questions
- [ ] XP earned calculated: 25 + (5 * correct) + (2 * marked), persisted to DB
- [ ] ArticleReadyEvent triggers async question generation with level scaffolding
- [ ] B1 questions: guided (MC, fill-blank), min_words=20
- [ ] B2 questions: mixed, min_words=30
- [ ] C1 questions: analysis/opinion, min_words=40
- [ ] Questions generated asynchronously, no blocking
- [ ] Unit + integration tests pass

---

### Phase 5: Integration Tests with Testcontainers

**Goal:** Validate all persistence contracts, constraints, N+1 prevention, and end-to-end workflows with real PostgreSQL database.

**Scope:** Integration test suite covering repositories, aggregates, and full use case flows.

**Files to create:**
1. `article/infrastructure/persistence/ArticleReadingRepositoryIT.java` — save, find, update, cascade delete, history query
2. `article/infrastructure/persistence/ArticleParagraphRepositoryIT.java` — order uniqueness, cascade
3. `article/infrastructure/persistence/ArticleQuestionRepositoryIT.java` — min_words persistence, order
4. `article/infrastructure/persistence/ArticleQuestionAnswerRepositoryIT.java` — unique constraint validation
5. `article/infrastructure/persistence/ArticleMarkedWordRepositoryIT.java` — unique constraint, cascade
6. `article/application/GenerateArticleUseCaseIT.java` — 202 PENDING response
7. `article/application/ProcessArticleContentAsyncServiceIT.java` — async status transitions
8. `article/application/CompleteArticleUseCaseIT.java` — XP calculation, state transition
9. `article/application/GenerateArticleQuestionsAsyncServiceIT.java` — level-adapted generation
10. `article/infrastructure/event/ArticleWordMarkedReviewListenerIT.java` — event → review creation

**Details:**

**Test base class (Testcontainers):**
```java
@SpringBootTest
@Testcontainers
public abstract class ArticleIntegrationTestBase {
    
    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>(DockerImageName.parse("postgres:15"))
        .withDatabaseName("test_article")
        .withUsername("test")
        .withPassword("test");
    
    static {
        postgres.setPortBindings(List.of("5432:5432"));
    }
    
    @DynamicPropertySource
    static void overrideProps(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }
}
```

**ArticleReadingRepositoryIT:**
```java
public class ArticleReadingRepositoryIT extends ArticleIntegrationTestBase {
    
    @Test
    void save_and_find_by_id() {
        // Arrange
        ArticleReading article = ArticleReadingMother.anArticle()
            .withStatus(ArticleStatus.PENDING)
            .build();
        
        // Act
        articleReadingRepository.save(article);
        ArticleReading found = articleReadingRepository.findById(article.id()).orElseThrow();
        
        // Assert
        assertEquals(article.title(), found.title());
        assertEquals(ArticleStatus.PENDING, found.status());
    }
    
    @Test
    void findAllByUserIdOrderByCreatedAtDesc_returns_correct_order() {
        // Arrange - create 3 articles for user
        UUID userId = UUID.randomUUID();
        articleReadingRepository.saveAll(List.of(
            ArticleReadingMother.anArticle().withUserId(userId).build(),
            ArticleReadingMother.anArticle().withUserId(userId).build(),
            ArticleReadingMother.anArticle().withUserId(userId).build()
        ));
        
        // Act
        List<ArticleReading> articles = articleReadingRepository.findAllByUserIdOrderByCreatedAtDesc(userId);
        
        // Assert
        assertEquals(3, articles.size());
        for (int i = 0; i < articles.size() - 1; i++) {
            assertTrue(articles.get(i).createdAt().isAfter(articles.get(i+1).createdAt()),
                "Articles should be ordered DESC by createdAt");
        }
    }
    
    @Test
    void cascade_delete_paragraphs_when_article_deleted() {
        // Arrange
        ArticleReading article = ArticleReadingMother.anArticle().build();
        articleReadingRepository.save(article);
        ArticleParagraph para = ArticleParagraphMother.aParagraph()
            .withArticleReadingId(article.id())
            .build();
        articleParagraphRepository.save(para);
        
        // Act
        articleReadingRepository.delete(article);
        
        // Assert
        assertTrue(articleParagraphRepository.findById(para.id()).isEmpty());
    }
}
```

**CompleteArticleUseCaseIT:**
```java
public class CompleteArticleUseCaseIT extends ArticleIntegrationTestBase {
    
    @Test
    void complete_article_calculates_xp_correctly() {
        // Arrange
        UUID userId = UUID.randomUUID();
        ArticleReading article = ArticleReadingMother.anArticle()
            .withUserId(userId)
            .withStatus(ArticleStatus.READY) // Pre-populated by GenerateArticleUseCase
            .build();
        articleReadingRepository.save(article);
        
        // Create 3 questions
        List<ArticleQuestion> questions = List.of(
            ArticleQuestionMother.aQuestion().withArticleReadingId(article.id()).build(),
            ArticleQuestionMother.aQuestion().withArticleReadingId(article.id()).build(),
            ArticleQuestionMother.aQuestion().withArticleReadingId(article.id()).build()
        );
        articleQuestionRepository.saveAll(questions);
        
        // Answer 2 correctly
        articleQuestionAnswerRepository.save(ArticleQuestionAnswerMother.anAnswer()
            .withQuestionId(questions.get(0).id())
            .withIsCorrect(true)
            .build());
        articleQuestionAnswerRepository.save(ArticleQuestionAnswerMother.anAnswer()
            .withQuestionId(questions.get(1).id())
            .withIsCorrect(true)
            .build());
        
        // Mark 3 words
        for (int i = 0; i < 3; i++) {
            articleMarkedWordRepository.save(ArticleMarkedWordMother.aMarkedWord()
                .withArticleReadingId(article.id())
                .build());
        }
        
        // Act
        completeArticleUseCase.execute(userId, article.id());
        
        // Assert: XP = 25 (base) + 5*2 (2 correct) + 2*3 (3 marked) = 41
        ArticleReading completed = articleReadingRepository.findById(article.id()).orElseThrow();
        assertEquals(ArticleStatus.COMPLETED, completed.status());
        assertEquals(41, completed.xpEarned());
    }
    
    @Test
    void complete_article_publishes_event() {
        // Arrange
        ArticleReading article = ArticleReadingMother.anArticle()
            .withStatus(ArticleStatus.READY)
            .build();
        articleReadingRepository.save(article);
        
        // Spy on event publisher
        ArgumentCaptor<DomainEvent> eventCaptor = ArgumentCaptor.forClass(DomainEvent.class);
        
        // Act
        completeArticleUseCase.execute(article.userId(), article.id());
        
        // Assert
        verify(domainEventPublisher).publishEvent(eventCaptor.capture());
        assertTrue(eventCaptor.getValue() instanceof ArticleCompletedEvent);
    }
}
```

**ArticleWordMarkedReviewListenerIT:**
```java
public class ArticleWordMarkedReviewListenerIT extends ArticleIntegrationTestBase {
    
    @Test
    void article_word_marked_event_creates_review_item() throws Exception {
        // Arrange
        UUID userId = UUID.randomUUID();
        ArticleReadingId articleReadingId = ArticleReadingId.create();
        ArticleMarkedWordId markedWordId = ArticleMarkedWordId.create();
        
        ArticleWordMarkedEvent event = new ArticleWordMarkedEvent(
            articleReadingId,
            userId,
            markedWordId,
            "word",
            "palabra",
            "context"
        );
        
        // Act - publish event
        applicationEventPublisher.publishEvent(event);
        
        // Assert - wait for async listener, then verify review item created
        Thread.sleep(1000); // Simple wait (better: use Awaitility library)
        List<ReviewItem> reviewItems = reviewItemRepository.findBySourceIdAndSourceType(
            markedWordId.value().toString(),
            "ARTICLE"
        );
        assertEquals(1, reviewItems.size());
    }
}
```

**Testing strategy summary:**
- All tests use Testcontainers with real PostgreSQL (not H2 or mocks)
- Object Mothers for test data (ArticleReadingMother, ArticleQuestionMother, etc.)
- Constructor injection verified in repositories
- N+1 query checks via Hibernates statistics API if using JPA (or query logging if JDBC)
- Unique constraints, foreign keys, cascades validated
- Async services tested with TransactionTemplate and eventual consistency waits

**Acceptance criteria:**
- [ ] All 10 integration test classes created with >90% use case coverage
- [ ] All tests pass on clean PostgreSQL container
- [ ] Constraint violations caught (UNIQUE, FK, CHECK)
- [ ] Cascade deletes verified
- [ ] XP calculation validated end-to-end
- [ ] Async event listeners verified
- [ ] N+1 query risks identified and mitigated

---

## API Contract

### `POST /api/article/generate`

**Request body:**
```json
{
  "topic": "Climate Change",
  "level": "B2"
}
```

**Response body (202 Accepted):**
```json
{
  "id": "f47ac10b-58cc-4372-a567-0e02b2c3d479",
  "status": "PENDING",
  "createdAt": "2026-04-08T10:30:00Z"
}
```

**Status codes:**
- `202 Accepted` — Article generation requested, processing in background
- `400 Bad Request` — Invalid topic or level
- `401 Unauthorized` — Missing/invalid JWT
- `500 Internal Server Error` — AI service unavailable

**Auth:** Required (JWT, ROLE_USER)

---

### `GET /api/article/history`

**Request body:** None

**Response body (200 OK):**
```json
[
  {
    "id": "f47ac10b-58cc-4372-a567-0e02b2c3d479",
    "title": "Climate Change and Its Effects",
    "level": "B2",
    "topic": "Climate Change",
    "status": "COMPLETED",
    "createdAt": "2026-04-08T10:30:00Z",
    "wordCount": 245,
    "questionsAnswered": 2
  },
  {
    "id": "e38bd2c0-49dd-4463-b678-1f13c4e2c580",
    "title": "Article Title",
    "level": "B1",
    "topic": "Technology",
    "status": "IN_PROGRESS",
    "createdAt": "2026-04-07T15:20:00Z",
    "wordCount": 180,
    "questionsAnswered": 0
  }
]
```

**Status codes:**
- `200 OK` — List returned (may be empty)
- `401 Unauthorized` — Missing/invalid JWT
- `500 Internal Server Error` — Database error

**Auth:** Required (JWT, ROLE_USER)

---

### `DELETE /api/article/{id}`

**Request body:** None

**Response body:** None (empty)

**Status codes:**
- `204 No Content` — Article deleted successfully
- `400 Bad Request` — Cannot delete COMPLETED article
- `401 Unauthorized` — Missing/invalid JWT
- `403 Forbidden` — Article belongs to another user
- `404 Not Found` — Article not found
- `500 Internal Server Error` — Database error

**Auth:** Required (JWT, ROLE_USER)

---

### `GET /api/article/{id}/questions`

**Request body:** None

**Response body (200 OK):**
```json
[
  {
    "id": "c5f7a8e1-2b6c-4d91-8e3a-9f4c0b5d2e8a",
    "text": "What is the main cause of climate change?",
    "answered": true,
    "answer": "Human activities such as burning fossil fuels"
  },
  {
    "id": "d6g8b9f2-3c7d-5e02-9f4b-0g5d1c6e3f9b",
    "text": "How can individuals contribute to reducing emissions?",
    "answered": false,
    "answer": null
  }
]
```

**Status codes:**
- `200 OK` — Questions list returned (may be empty if not yet generated)
- `401 Unauthorized` — Missing/invalid JWT
- `403 Forbidden` — Article belongs to another user
- `404 Not Found` — Article not found
- `500 Internal Server Error` — Database error

**Auth:** Required (JWT, ROLE_USER)

---

### `POST /api/article/{id}/complete`

**Request body:** None

**Response body (202 Accepted):**
```json
{
  "id": "f47ac10b-58cc-4372-a567-0e02b2c3d479",
  "status": "COMPLETED",
  "xpEarned": 41
}
```

**Status codes:**
- `202 Accepted` — Article marked as completed, XP awarded
- `400 Bad Request` — Article not in READY or IN_PROGRESS status
- `401 Unauthorized` — Missing/invalid JWT
- `403 Forbidden` — Article belongs to another user
- `404 Not Found` — Article not found
- `500 Internal Server Error` — Database error

**Auth:** Required (JWT, ROLE_USER)

---

## Database Changes

### Migration V11.2.0: Update status enum and add performance indexes

```sql
-- Update status enum to include new async states
ALTER TABLE article_readings DROP CONSTRAINT IF EXISTS article_readings_status_check;
ALTER TABLE article_readings ADD COLUMN IF NOT EXISTS failure_reason VARCHAR(500);
ALTER TABLE article_readings ADD CONSTRAINT article_readings_status_check 
  CHECK (status IN ('PENDING', 'PROCESSING', 'READY', 'FAILED', 'IN_PROGRESS', 'COMPLETED'));

-- Performance indexes for common queries
CREATE INDEX IF NOT EXISTS idx_article_readings_user_status_created 
  ON article_readings(user_id, status, created_at DESC);

CREATE INDEX IF NOT EXISTS idx_article_readings_user_xp_earned 
  ON article_readings(user_id, xp_earned DESC);

-- Add XP tracking column
ALTER TABLE article_readings ADD COLUMN IF NOT EXISTS xp_earned INTEGER NOT NULL DEFAULT 0;
```

**Rationale:**
- New status values (PENDING, PROCESSING, READY, FAILED) support async workflow
- Backward compatibility maintained (legacy IN_PROGRESS, COMPLETED still valid)
- `failure_reason` stores error context for debugging failed generations
- `idx_article_readings_user_status_created` enables efficient history query filtering + ordering
- `idx_article_readings_user_xp_earned` enables future XP leaderboards
- `xp_earned` INT column (no overflow risk: max ~725 per article)

**No breaking changes:** All existing rows remain valid; migration is additive.

---

## Testing Strategy

### Unit Tests (by phase)

**Phase 1:** 3 use cases (GetArticleHistory, DeleteArticle, GetArticleQuestionsWithAnswers)
- Happy path, empty list, not found, ownership check, status validation

**Phase 2:** Event publishing (MarkWordUseCase), event listener
- Verify event published, listener called, review item created, no direct repository injection

**Phase 3:** Async status transitions (GenerateArticle, ProcessArticleContentAsyncService)
- PENDING state returned immediately, PROCESSING→READY→FAILED transitions, failure handling

**Phase 4:** XP calculation, level-based question generation
- Base + correct answers + marked words, B1/B2/C1 scaffolding

**Phase 5:** Integration tests with Testcontainers
- Repository contracts, constraints, cascade, end-to-end flows

### Integration Tests (Testcontainers)

**Scope:** All repositories, constraint validation, cascade deletes, async event listeners

**Database:** PostgreSQL 15 container (not in-memory H2)

**Fixture approach:** Object Mothers for test data creation

**Async testing:** Awaitility library for event listener verification

**Coverage:**
- CRITICAL: SubmitAnswer + ownership checks (prevents data leaks)
- CRITICAL: CompleteArticle XP calculation (prevents XP fraud)
- HIGH: MarkWord → Review event decoupling
- HIGH: GenerateArticle async status transitions
- MEDIUM: DeleteArticle cascade
- MEDIUM: Level scaffolding prompt generation

### Test Counts

- Phase 1: ~9 unit tests (3 per use case)
- Phase 2: ~4 unit + 1 integration test
- Phase 3: ~6 unit + 2 integration tests
- Phase 4: ~8 unit + 3 integration tests
- Phase 5: ~15 integration tests
- **Total: ~48 test cases**

---

## Cross-Cutting Concerns

### Security (all phases)

- **JWT validation:** All endpoints require ROLE_USER
- **Ownership checks:** GetArticle, DeleteArticle, CompleteArticle verify `userId == currentUser`
- **Input validation:** Jakarta Validation annotations (topic length, level enum, userAnswer word count)
- **Error messages:** Sanitized (no AI secrets, no database paths in 500 errors)

### Logging (all phases)

- **Async service:** Log start/finish/error of ProcessArticleContentAsyncService
- **Event listeners:** Log event received, processing, errors
- **Repository:** Log N+1 query risks via Hibernate statistics
- **Use cases:** Log execution, parameters (no sensitive data), outcomes

### Error Handling (all phases)

- **Checked exceptions:** ArticleException, UserNotAuthorizedForArticleException
- **ControllerAdvice:** Return consistent error JSON (error code, message, timestamp)
- **Async failures:** Do NOT propagate to user; log and set status = FAILED
- **Constraint violations:** Catch unique constraint errors, return 400 Bad Request

### Performance (all phases)

- **N+1 prevention:** History query uses `findAllByUserIdOrderByCreatedAtDesc` (JPA projection or JDBC custom query)
- **Async:** Questions generated in background, do NOT block article completion
- **Indexes:** Created in V11.2.0 for history + XP queries
- **Batch:** Multiple marked words/answers saved together where possible

