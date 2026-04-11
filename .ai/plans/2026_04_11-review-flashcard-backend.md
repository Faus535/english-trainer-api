# Backend Plan: Review Flashcard Enrichment

> Generated: 2026-04-11
> Request: Enrich review items with context sentences and translations for better spaced repetition

## Decisions Log

| # | Topic | Decision | Alternatives Considered | Why |
|---|-------|----------|------------------------|-----|
| 1 | Context storage | 4 nullable columns on review_items | Separate table | Simple, no JOIN needed at review time |
| 2 | TalkCorrection context | Add `originalUserMessage` field to TalkCorrection | Query messages table | Correction already has all data, just needs pairing |
| 3 | Stats enrichment | Add to existing ReviewStats VO | New endpoint | Backwards compatible, same /stats endpoint |
| 4 | New stat queries | JPQL query methods in repo interface | Native SQL | Consistent with existing Spring Data JPA patterns |

## Context

- Reference module: `article` — event-driven enrichment patterns
- Modules affected: `review`, `talk`

## Public Contracts

### Use Cases

```java
// Extended response (existing use case, no signature change)
GetReviewQueueUseCase.execute(UUID userId) → List<ReviewItem>  // ReviewItem now has context fields

// Extended (existing)
GetReviewStatsUseCase.execute(UUID userId) → ReviewStats  // ReviewStats now has totalMastered + accuracyRate
```

### Domain Events

None new — enrichment happens at creation time via existing events.

### Domain Exceptions

None new.

### Database Schema

```sql
-- V11.9.0
ALTER TABLE review_items ADD COLUMN context_sentence TEXT;
ALTER TABLE review_items ADD COLUMN context_translation TEXT;
ALTER TABLE review_items ADD COLUMN target_word TEXT;
ALTER TABLE review_items ADD COLUMN target_translation TEXT;
```

### REST Endpoints

No new endpoints — existing responses enriched:
- `GET /api/review/queue` response items now include `contextSentence`, `contextTranslation`, `targetWord`, `targetTranslation`
- `GET /api/review/stats` response now includes `totalMastered`, `weeklyReviewed`, `accuracyRate`

## Analysis

**Existing files relevant to this feature:**
- `review/domain/ReviewItem.java` — aggregate with `userId`, `sourceType`, `word`, `translation`, `quality`, `interval`, SM-2 fields
- `review/domain/ReviewStats.java` — VO with `totalItems`, `dueToday`, `averageQuality`
- `review/domain/ReviewItemRepository.java` — interface with `findDueByUserId(UUID, Instant)`
- `review/infrastructure/ReviewItemEntity.java` — JPA entity
- `review/application/CreateReviewItemFromArticleWordUseCase.java` — handles `ArticleWordMarkedEvent`
- `review/application/CreateReviewItemFromImmerseUseCase.java` — handles `ImmerseSubmittedEvent`
- `review/application/CreateReviewItemFromTalkUseCase.java` — handles `TalkConversationCompletedEvent`
- `talk/domain/TalkConversation.java` — `end()` method creates `TalkConversationCompletedEvent`
- `talk/domain/TalkCorrection.java` — record with correction data

**Existing tests impacted:**
- `ReviewItemTest` — add context field tests
- `ReviewItemMother` — add `withContext()` builders
- `CreateReviewItemFromArticleWordUseCaseTest` — extend with context
- `CreateReviewItemFromImmerseUseCaseTest` — extend with context
- `CreateReviewItemFromTalkUseCaseTest` — extend with context
- `TalkConversationTest` — `end()` must pair corrections with user messages
- `ReviewStatsMother` — extend with new stat fields

## Phases

### Phase 1: ReviewItem context fields + migration [M]

**Goal**: ReviewItem domain model carries context sentence, translation, target word, and translation. Migration applied.

**Files to create/modify**:
- `src/main/java/com/faus535/englishtrainer/review/domain/ReviewItem.java` — add 4 nullable context fields
- `src/main/java/com/faus535/englishtrainer/review/infrastructure/ReviewItemEntity.java` — add column mappings
- `src/main/java/com/faus535/englishtrainer/review/infrastructure/ReviewItemResponse.java` (or DTO) — add context fields to response
- `src/main/resources/db/migration/V11.9.0__review_items_add_context_fields.sql` — migration
- `src/test/java/com/faus535/englishtrainer/review/domain/ReviewItemTest.java` — extend
- `src/test/java/com/faus535/englishtrainer/review/domain/ReviewItemMother.java` — add builders

**Details**:
- `ReviewItem` record: add `@Nullable String contextSentence`, `@Nullable String contextTranslation`, `@Nullable String targetWord`, `@Nullable String targetTranslation` to record components
- All nullable — existing items without context still work
- `ReviewItemEntity`: add 4 `@Column` fields mapping `context_sentence`, `context_translation`, `target_word`, `target_translation`
- `GetReviewQueueController` response DTO: include all 4 fields (null if not set)
- `ReviewItemMother.withContext(String sentence, String translation, String word, String wordTranslation)` builder

**Test method names**:
- `ReviewItemTest.reviewItem_withContext_storesAllFields()`
- `ReviewItemTest.reviewItem_withoutContext_hasNullFields()`

**Acceptance criteria**:
- [x] Migration `V11.9.0` runs without error
- [x] `ReviewItem` record accepts null context fields
- [x] `GET /api/review/queue` response includes `contextSentence` (null for old items)
- [x] Verify compilation
- [x] Verify tests pass
- [ ] Human review
- [ ] Commit

---

### Phase 2: Enrich review item creation from article, immerse, talk [M]

**Goal**: New review items created from events carry context sentence and target word/translation.

**Files to create/modify**:
- `src/main/java/com/faus535/englishtrainer/review/application/CreateReviewItemFromArticleWordUseCase.java` — use event context
- `src/main/java/com/faus535/englishtrainer/review/application/CreateReviewItemFromImmerseUseCase.java` — use event question
- `src/main/java/com/faus535/englishtrainer/review/application/CreateReviewItemFromTalkUseCase.java` — use correction + user message
- `src/main/java/com/faus535/englishtrainer/talk/domain/TalkCorrection.java` — add `originalUserMessage` field
- `src/main/java/com/faus535/englishtrainer/talk/domain/TalkConversation.java` — update `end()` to pair corrections with messages
- `src/test/java/com/faus535/englishtrainer/review/application/CreateReviewItemFromArticleWordUseCaseTest.java` — extend
- `src/test/java/com/faus535/englishtrainer/review/application/CreateReviewItemFromImmerseUseCaseTest.java` — extend
- `src/test/java/com/faus535/englishtrainer/review/application/CreateReviewItemFromTalkUseCaseTest.java` — extend
- `src/test/java/com/faus535/englishtrainer/talk/domain/TalkConversationTest.java` — add pairing test

**Details**:

**Article enrichment**: `ArticleWordMarkedEvent` should carry `contextSentence` (the sentence containing the marked word) and `englishDefinition`. Use these in `CreateReviewItemFromArticleWordUseCase.execute()` — map to `contextSentence` and `targetWord`/`targetTranslation`.

**Immerse enrichment**: `ImmerseSubmittedEvent` carries exercise `question` — use as `contextSentence`. Correct answer → `targetWord`. No translation available from immerse — leave `contextTranslation`/`targetTranslation` null.

**Talk enrichment**: `TalkConversationCompletedEvent` corrections need pairing:
- `TalkCorrection` record: add `String originalUserMessage` field
- `TalkConversation.end()`: iterate corrections, for each find the preceding user message by index — `messages.get(correctionIndex - 1).content()` where `correctionIndex` is the message index that triggered the correction
- `CreateReviewItemFromTalkUseCase`: map `correction.originalText()` → `targetWord`, `correction.correctedText()` → `targetTranslation`, `correction.originalUserMessage()` → `contextSentence`

**Test method names**:
- `CreateReviewItemFromArticleWordUseCaseTest.execute_setsContextSentenceFromEvent()`
- `CreateReviewItemFromImmerseUseCaseTest.execute_setsContextSentenceFromQuestion()`
- `CreateReviewItemFromTalkUseCaseTest.execute_setsContextFromCorrectionAndUserMessage()`
- `TalkConversationTest.end_pairsCorrectionsWithPrecedingUserMessages()`

**Acceptance criteria**:
- [x] Marking a word in article creates review item with context sentence
- [x] Completing immerse creates review items with context from exercise
- [x] Completing talk creates review items with `originalUserMessage` as context
- [x] TalkCorrection records include `originalUserMessage`
- [x] Verify compilation
- [x] Verify tests pass
- [ ] Human review
- [ ] Commit

---

### Phase 3: ReviewStats enrichment [S]

**Goal**: Review stats include total mastered count, weekly review count, and accuracy rate.

**Files to create/modify**:
- `src/main/java/com/faus535/englishtrainer/review/domain/ReviewStats.java` — add `totalMastered`, `weeklyReviewed`, `accuracyRate` fields
- `src/main/java/com/faus535/englishtrainer/review/domain/ReviewItemRepository.java` — add `countMasteredByUserId()`, `countCorrectByUserIdSince()`
- `src/main/java/com/faus535/englishtrainer/review/infrastructure/ReviewItemJpaRepository.java` — add JPQL methods
- `src/main/java/com/faus535/englishtrainer/review/infrastructure/ReviewItemRepositoryJpa.java` — implement
- `src/main/java/com/faus535/englishtrainer/review/application/GetReviewStatsUseCase.java` — update to use new repo methods
- `src/test/java/com/faus535/englishtrainer/review/application/GetReviewStatsUseCaseTest.java` — extend
- `src/test/java/com/faus535/englishtrainer/review/domain/ReviewStatsMother.java` — extend

**Details**:
- `ReviewStats` record: add `long totalMastered`, `long weeklyReviewed`, `double accuracyRate` (0.0–1.0)
- "Mastered" = items where `quality >= 4` (SM-2 well-recalled) OR `interval >= 21` (3+ weeks)
- "Weekly reviewed" = items reviewed in last 7 days (use `reviewedAt` or `lastReviewedAt` field)
- "Accuracy rate" = items with quality >= 3 / total reviewed in last 30 days (0.0 if no reviews)
- JPQL: `@Query("SELECT COUNT(r) FROM ReviewItemEntity r WHERE r.userId = :userId AND r.quality >= 4")`
- JPQL: `@Query("SELECT COUNT(r) FROM ReviewItemEntity r WHERE r.userId = :userId AND r.reviewedAt >= :since")`

**Test method names**:
- `GetReviewStatsUseCaseTest.execute_returnsMasteredCount()`
- `GetReviewStatsUseCaseTest.execute_returnsWeeklyReviewedCount()`
- `GetReviewStatsUseCaseTest.execute_returnsAccuracyRate()`

**Acceptance criteria**:
- [ ] `GET /api/review/stats` includes `totalMastered`, `weeklyReviewed`, `accuracyRate`
- [ ] `accuracyRate` is between 0.0 and 1.0
- [ ] Stats are user-scoped (no cross-user data)
- [ ] Verify compilation
- [ ] Verify tests pass
- [ ] Human review
- [ ] Commit

---

## API Contract

### `GET /api/review/queue`
- **Response body**:
```json
[
  {
    "id": "uuid",
    "word": "ephemeral",
    "translation": "efímero",
    "contextSentence": "The ephemeral beauty of cherry blossoms delights tourists each spring.",
    "contextTranslation": "La belleza efímera de los cerezos deleita a los turistas cada primavera.",
    "targetWord": "ephemeral",
    "targetTranslation": "efímero",
    "quality": 3,
    "interval": 7
  }
]
```
- **Status codes**: 200
- **Auth**: Bearer JWT required

### `GET /api/review/stats`
- **Response body**:
```json
{
  "totalItems": 45,
  "dueToday": 8,
  "averageQuality": 3.2,
  "totalMastered": 12,
  "weeklyReviewed": 23,
  "accuracyRate": 0.78
}
```
- **Status codes**: 200
- **Auth**: Bearer JWT required

## Database Changes

```sql
-- V11.9.0__review_items_add_context_fields.sql
ALTER TABLE review_items ADD COLUMN context_sentence TEXT;
ALTER TABLE review_items ADD COLUMN context_translation TEXT;
ALTER TABLE review_items ADD COLUMN target_word TEXT;
ALTER TABLE review_items ADD COLUMN target_translation TEXT;
```

## Testing Strategy

- Phase 1: Domain record tests, Object Mother builders
- Phase 2: Use case tests with enriched event mocks, TalkConversation unit test for message pairing
- Phase 3: Stats use case tests with In-Memory repository returning known fixture data

## Next step

Execute Phase 1: ReviewItem context fields + migration
