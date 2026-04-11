# Backend Plan: Implement Real Spaced Repetition System (SM-2 Algorithm)

> Generated: 2026-04-11
> Request: Implement real Spaced Repetition System (SM-2 algorithm) in the review module
> Slug: 2026_04_11-review-spaced-repetition-sm2

## Decisions Log

| # | Decision | Rationale |
|---|----------|-----------|
| D1 | `consecutiveCorrect` renamed to `repetitions` in domain | Aligns with SM-2 standard terminology. The approved contract uses `repetitions`. DB column renamed in V13.0.0. |
| D2 | `nextReviewAt` changed from `Instant` to `LocalDate` in domain | Reviews are date-based, not time-based. DB column type changed from `TIMESTAMPTZ` to `DATE` in V13.0.0. |
| D3 | `SM2Algorithm` extracted as a pure domain service | The logic embedded in `ReviewItem.review()` moves to `SM2Algorithm.calculate()`. Makes the algorithm independently testable. |
| D4 | Migration V13.0.0 renames columns, not adds new ones | `ease_factor`, `interval_days` already exist. RENAME `consecutive_correct` → `repetitions`, ALTER type of `next_review_at` to DATE. |
| D5 | `retentionRate` = rename of existing `accuracyRate` | Semantically equivalent (correct/total over 30 days). `ReviewStats` record updated. |
| D6 | `averageInterval` added to `ReviewStats` + `ReviewItemRepository` | New repo method `averageIntervalByUserId(UUID)`. Query: `AVG(interval_days)`. |
| D7 | `GetReviewQueueUseCase` uses `LocalDate.now()` | Consistent with D2. `findDueByUserId` signature changes. |
| D8 | No new controller created | All 3 endpoints already exist. Only internal logic and response fields change. |
| D9 | `SM2Result` record as output of `SM2Algorithm` | Encapsulates `newEaseFactor`, `newIntervalDays`, `newRepetitions`, `nextReviewAt: LocalDate`. |

## Context

- **Reference module**: review (most semantically similar)
- **Modules affected**: review

### Critical Discovery

The SM-2 algorithm is ALREADY implemented in `ReviewItem.review(int quality)`. The domain already has `easeFactor`, `intervalDays`, `consecutiveCorrect`, `nextReviewAt` (as `Instant`). `SubmitReviewResultUseCase` already accepts quality 0-5.

**What does NOT exist yet** (the genuine gaps):
1. `consecutiveCorrect` must become `repetitions` (SM-2 standard terminology)
2. `nextReviewAt` is `Instant` — contracts specify `LocalDate`
3. No `SM2Algorithm` domain service class (logic in `ReviewItem.review()`)
4. `GetReviewStatsUseCase` missing `retentionRate` (rename) and `averageInterval` (new)
5. Migration V13.0.0 must rename column + change column type

## Public Contracts

### Domain: `SM2Result` (new record)
```java
// com.faus535.englishtrainer.review.domain.SM2Result
public record SM2Result(double newEaseFactor, int newIntervalDays, int newRepetitions, LocalDate nextReviewAt) {}
```

### Domain Service: `SM2Algorithm` (new)
```java
// com.faus535.englishtrainer.review.domain.SM2Algorithm
public final class SM2Algorithm {
    public static SM2Result calculate(double easeFactor, int intervalDays, int repetitions, int quality);
}
```

SM-2 formula:
- `newEF = max(1.3, EF + (0.1 - (5-q)*(0.08 + (5-q)*0.02)))`
- `rep == 0` → interval = 1 day
- `rep == 1` → interval = 6 days
- `rep > 1` → interval = `round(intervalDays * EF)`
- If quality < 3: reset `newRepetitions = 0`, `newIntervalDays = 1`

### Domain: `ReviewItem` field changes
```java
// Before → After
private final int consecutiveCorrect     → private final int repetitions
private final Instant nextReviewAt       → private final LocalDate nextReviewAt
```

### Domain: `ReviewStats` (modified)
```java
public record ReviewStats(int totalItems, int dueToday, int completedToday, int streak,
                           long totalMastered, long weeklyReviewed,
                           double retentionRate, double averageInterval) {}
```
(`accuracyRate` renamed to `retentionRate`, `averageInterval` added)

### Repository: `ReviewItemRepository` (new method)
```java
double averageIntervalByUserId(UUID userId);
// Also updated signatures:
int countDueByUserId(UUID userId, LocalDate today);
List<ReviewItem> findDueByUserId(UUID userId, LocalDate today, int limit);
```

### Migration: V13.0.0
```sql
ALTER TABLE review_items RENAME COLUMN consecutive_correct TO repetitions;
ALTER TABLE review_items ALTER COLUMN next_review_at TYPE DATE USING next_review_at::DATE;
```

### Frontend mapping (for reference — implemented in frontend plan)
- Hard = quality 2, Good = quality 4, Easy = quality 5

## Analysis

### Existing Tests Impacted
- `ReviewItemTest` — rename `consecutiveCorrect()` → `repetitions()`, `Instant` → `LocalDate`
- `ReviewItemMother` — rename field, update `nextReviewAt` type to `LocalDate`
- `GetReviewQueueUseCaseTest` — update to pass `LocalDate.now()`
- `SubmitReviewResultUseCaseTest` — rename `consecutiveCorrect` assertions
- `GetReviewStatsUseCaseTest` — rename `accuracyRate` → `retentionRate`, add `averageInterval` assertions
- `InMemoryReviewItemRepository` — update filter signatures

---

## Phases

### Phase 1: Domain — SM2Algorithm + ReviewItem alignment + Migration [M]

**Goal**: Extract `SM2Algorithm`, rename `consecutiveCorrect` → `repetitions`, change `nextReviewAt` to `LocalDate`, write migration V13.0.0, update persistence entity, update all tests to compile and pass.

**Files to create**:
- `src/main/java/com/faus535/englishtrainer/review/domain/SM2Algorithm.java`
- `src/main/java/com/faus535/englishtrainer/review/domain/SM2Result.java`
- `src/main/resources/db/migration/V13.0.0__review_items_sm2_alignment.sql`
- `src/test/java/com/faus535/englishtrainer/review/domain/SM2AlgorithmTest.java`

**Files to modify**:
- `src/main/java/com/faus535/englishtrainer/review/domain/ReviewItem.java`
- `src/main/java/com/faus535/englishtrainer/review/domain/ReviewStats.java`
- `src/main/java/com/faus535/englishtrainer/review/domain/ReviewItemRepository.java`
- `src/main/java/com/faus535/englishtrainer/review/infrastructure/persistence/ReviewItemEntity.java`
- `src/main/java/com/faus535/englishtrainer/review/infrastructure/persistence/JpaReviewItemRepository.java`
- `src/main/java/com/faus535/englishtrainer/review/infrastructure/persistence/JpaReviewItemRepositoryAdapter.java`
- `src/test/java/com/faus535/englishtrainer/review/infrastructure/InMemoryReviewItemRepository.java`
- `src/test/java/com/faus535/englishtrainer/review/domain/ReviewItemMother.java`
- `src/test/java/com/faus535/englishtrainer/review/domain/ReviewItemTest.java`

**Details**:

**Step 1 — `SM2Result.java`**
```java
package com.faus535.englishtrainer.review.domain;
import java.time.LocalDate;

public record SM2Result(double newEaseFactor, int newIntervalDays, int newRepetitions, LocalDate nextReviewAt) {}
```

**Step 2 — `SM2Algorithm.java`**
```java
package com.faus535.englishtrainer.review.domain;
import java.time.LocalDate;

public final class SM2Algorithm {
    private SM2Algorithm() {}

    public static SM2Result calculate(double easeFactor, int intervalDays, int repetitions, int quality) {
        double newEaseFactor = Math.max(1.3,
                easeFactor + (0.1 - (5 - quality) * (0.08 + (5 - quality) * 0.02)));
        int newIntervalDays;
        int newRepetitions;

        if (quality >= 3) {
            newRepetitions = repetitions + 1;
            if (repetitions == 0) {
                newIntervalDays = 1;
            } else if (repetitions == 1) {
                newIntervalDays = 6;
            } else {
                newIntervalDays = (int) Math.round(intervalDays * newEaseFactor);
            }
        } else {
            newRepetitions = 0;
            newIntervalDays = 1;
        }

        LocalDate nextReviewAt = LocalDate.now().plusDays(newIntervalDays);
        return new SM2Result(newEaseFactor, newIntervalDays, newRepetitions, nextReviewAt);
    }
}
```

**Step 3 — `ReviewItem.java`** changes:
- `private final int consecutiveCorrect` → `private final int repetitions`
- `private final Instant nextReviewAt` → `private final LocalDate nextReviewAt`
- `create()` factory: `nextReviewAt` initialized to `LocalDate.now()`
- `reconstitute()`: updated signature with `LocalDate nextReviewAt`, `int repetitions`
- `review(int quality)` delegates to `SM2Algorithm.calculate()`, returns new `ReviewItem`
- `isDue(LocalDate today)`: `return !nextReviewAt.isAfter(today)`
- Getters: `repetitions()`, `nextReviewAt()` returns `LocalDate`

**Step 4 — `ReviewStats.java`**
```java
public record ReviewStats(int totalItems, int dueToday, int completedToday, int streak,
                           long totalMastered, long weeklyReviewed,
                           double retentionRate, double averageInterval) {}
```

**Step 5 — `ReviewItemRepository.java`** additions:
```java
double averageIntervalByUserId(UUID userId);
int countDueByUserId(UUID userId, LocalDate today);
List<ReviewItem> findDueByUserId(UUID userId, LocalDate today, int limit);
```

**Step 6 — Migration `V13.0.0__review_items_sm2_alignment.sql`**
```sql
ALTER TABLE review_items RENAME COLUMN consecutive_correct TO repetitions;
ALTER TABLE review_items ALTER COLUMN next_review_at TYPE DATE USING next_review_at::DATE;
```

**Step 7 — `ReviewItemEntity.java`**:
- `consecutive_correct` → `repetitions` column + field
- `nextReviewAt` field becomes `LocalDate` (no `@Temporal` needed — Hibernate maps LocalDate to DATE)
- `fromDomainForUpdate()` static factory: sets `isNew = false`

**Step 8 — `JpaReviewItemRepository.java`**:
- `findDueByUserId`: parameter type `LocalDate today`, JPQL: `r.nextReviewAt <= :today`
- Add: `@Query("SELECT COALESCE(AVG(r.intervalDays), 0.0) FROM ReviewItemEntity r WHERE r.userId = :userId") double averageIntervalByUserId(UUID userId);`

**Step 9 — `JpaReviewItemRepositoryAdapter.java`**: pass `LocalDate` to queries, add `averageIntervalByUserId`

**Step 10 — `InMemoryReviewItemRepository.java`**:
- `findDueByUserId(UUID, LocalDate, int)`: filter `!item.nextReviewAt().isAfter(today)`
- `countDueByUserId(UUID, LocalDate)`: same filter
- `averageIntervalByUserId(UUID)`: `mapToInt(ReviewItem::intervalDays).average().orElse(0.0)`

**Step 11 — `ReviewItemMother.java`**:
- All `reconstitute()` calls: `LocalDate` for `nextReviewAt`, rename `consecutiveCorrect` → `repetitions`
- `dueToday()`: `nextReviewAt = LocalDate.now().minusDays(1)`
- `notDue()`: `nextReviewAt = LocalDate.now().plusDays(3)`

**Step 12 — `SM2AlgorithmTest.java`** test methods:
- `shouldReturnInterval1WhenFirstRepetition()` — rep=0, quality=4 → interval=1, rep=1
- `shouldReturnInterval6WhenSecondRepetition()` — rep=1, quality=4 → interval=6, rep=2
- `shouldMultiplyIntervalByEaseFactorWhenThirdRepetition()` — rep=2, ef=2.5, interval=6, quality=4 → interval=15
- `shouldResetRepetitionsWhenQualityBelow3()` — quality=2 → rep=0, interval=1
- `shouldNotDropEaseFactorBelow1Point3()` — ef=1.3, quality=0 → ef stays 1.3
- `shouldIncreaseEaseFactorWhenQuality5()`
- `shouldDecreaseEaseFactorWhenQuality3()`
- `shouldSetNextReviewAtToTodayPlusInterval()`
- `shouldReturnRepetitions0AndInterval1WhenQuality0()`
- `shouldReturnRepetitions0AndInterval1WhenQuality1()`
- `shouldReturnRepetitions0AndInterval1WhenQuality2()`

**Acceptance criteria**:
- [x] `./gradlew compileJava compileTestJava` passes
- [x] `SM2AlgorithmTest` — all 11 test methods green
- [x] `ReviewItemTest` — all tests green with renamed references
- [x] No `Instant` in `ReviewItem` domain class
- [x] No `consecutiveCorrect` references anywhere
- [x] Verify compilation
- [x] Verify tests pass
- [ ] Human review
- [ ] Commit: `feat(review): extract SM2Algorithm domain service and align domain to SM-2 contracts`

---

### Phase 2: Application — Queue + Controller alignment [M]

**Goal**: Update `GetReviewQueueUseCase` to use `LocalDate`, update controllers to expose renamed response fields, fix/extend use case tests.

**Files to modify**:
- `src/main/java/com/faus535/englishtrainer/review/application/GetReviewQueueUseCase.java`
- `src/main/java/com/faus535/englishtrainer/review/infrastructure/controller/GetReviewQueueController.java`
- `src/main/java/com/faus535/englishtrainer/review/infrastructure/controller/SubmitReviewResultController.java`
- `src/test/java/com/faus535/englishtrainer/review/application/GetReviewQueueUseCaseTest.java`
- `src/test/java/com/faus535/englishtrainer/review/application/SubmitReviewResultUseCaseTest.java`

**Details**:

**`GetReviewQueueUseCase.java`**:
```java
@Transactional(readOnly = true)
public List<ReviewItem> execute(UUID userId, int limit) {
    int cappedLimit = Math.min(Math.max(limit, 1), MAX_LIMIT);
    return repository.findDueByUserId(userId, LocalDate.now(), cappedLimit);
}
```

**`GetReviewQueueController.java`** — rename `consecutiveCorrect` → `repetitions` in `ReviewItemResponse` record, change `nextReviewAt` to `item.nextReviewAt().toString()` (ISO-8601 date `YYYY-MM-DD`).

**`SubmitReviewResultController.java`** — rename `consecutiveCorrect` → `repetitions` in `ReviewResultResponse` record.

**`GetReviewQueueUseCaseTest.java`** — update `LocalDate` references, add:
- `shouldSortByMostOverdueFirst()` — item overdue by 5 days appears before item overdue by 1 day

**`SubmitReviewResultUseCaseTest.java`** — `assertEquals(1, updated.repetitions())` (was `consecutiveCorrect()`)

**Acceptance criteria**:
- [ ] `./gradlew test --tests "*.review.*"` all green
- [ ] No `Instant` references in application or controller layer
- [ ] Verify compilation
- [ ] Verify tests pass
- [ ] Human review
- [ ] Commit: `feat(review): align GetReviewQueue and controllers to SM-2 LocalDate contracts`

---

### Phase 3: Application — GetReviewStatsUseCase enrichment [S]

**Goal**: Add `averageInterval` and rename `accuracyRate` → `retentionRate` in `ReviewStats`, update `GetReviewStatsUseCase`, update stats tests.

**Files to modify**:
- `src/main/java/com/faus535/englishtrainer/review/application/GetReviewStatsUseCase.java`
- `src/test/java/com/faus535/englishtrainer/review/application/GetReviewStatsUseCaseTest.java`

**Details**:

In `GetReviewStatsUseCase.execute()`:
- `countDueByUserId(userId, Instant.now())` → `countDueByUserId(userId, LocalDate.now())`
- Rename `accuracyRate` local variable → `retentionRate`
- Add: `double averageInterval = itemRepository.averageIntervalByUserId(userId)`
- Construct `ReviewStats` with new signature

New test methods in `GetReviewStatsUseCaseTest`:
- `shouldReturnAverageIntervalWhenItemsExist()` — items with intervals 1 and 6 → averageInterval = 3.5
- `shouldReturnZeroAverageIntervalWhenNoItems()` — no items → averageInterval = 0.0
- `shouldReturnRetentionRateAsRatioOfCorrectResults()` — 3 correct, 1 incorrect → retentionRate = 0.75
- Update existing tests: `stats.accuracyRate()` → `stats.retentionRate()`

**Acceptance criteria**:
- [ ] `./gradlew test --tests "*.review.*"` all green
- [ ] `ReviewStats` has exactly 8 fields (no `accuracyRate`)
- [ ] No `accuracyRate` reference anywhere in the codebase
- [ ] `./gradlew test` full suite green
- [ ] Verify compilation
- [ ] Verify tests pass
- [ ] Human review
- [ ] Commit: `feat(review): add retentionRate and averageInterval to ReviewStats`

---

### Phase 4: Frontend (see frontend plan)

Frontend: `ReviewPage` quality rating buttons (Hard=2, Good=4, Easy=5), display `repetitions`, show `retentionRate` and `averageInterval` in stats view.

---

### Phase 5: /revisar + fixes [S]

**Goal**: Run `/revisar` to validate architecture and fix any violations.

**Checks**:
- `SM2Algorithm` has no Spring annotations
- All `execute()` methods have `@Transactional` or `@Transactional(readOnly=true)`
- Controllers are package-private
- No `RuntimeException` subclasses in domain
- No `@Autowired` on fields

**Acceptance criteria**:
- [ ] `/revisar` reports no violations
- [ ] `./gradlew test` passes fully
- [ ] Verify compilation
- [ ] Verify tests pass
- [ ] Human review
- [ ] Commit: `fix(review): revisar fixes`

---

## API Contract

### GET `/api/profiles/{userId}/review/queue`
Response (field renamed):
```json
[{
  "id": "uuid",
  "sourceType": "ARTICLE",
  "frontContent": "ephemeral",
  "backContent": "efímero",
  "nextReviewAt": "2026-04-13",
  "intervalDays": 6,
  "repetitions": 2,
  "contextSentence": "...",
  "targetWord": "ephemeral",
  "targetTranslation": "efímero"
}]
```
**Breaking**: `consecutiveCorrect` → `repetitions`; `nextReviewAt` format: ISO-8601 date (not timestamp)

### POST `/api/profiles/{userId}/review/items/{itemId}/result`
Request: `{ "quality": 4 }` (unchanged)
Response: `{ "id": "uuid", "nextReviewAt": "2026-04-17", "intervalDays": 6, "easeFactor": 2.5, "repetitions": 2 }`
**Breaking**: `consecutiveCorrect` → `repetitions`

### GET `/api/profiles/{userId}/review/stats`
Response:
```json
{
  "totalItems": 42, "dueToday": 7, "completedToday": 3, "streak": 1,
  "totalMastered": 12, "weeklyReviewed": 15,
  "retentionRate": 0.87, "averageInterval": 8.3
}
```
**Breaking**: `accuracyRate` → `retentionRate`; new field `averageInterval`

---

## Database Changes

File: `src/main/resources/db/migration/V13.0.0__review_items_sm2_alignment.sql`
```sql
ALTER TABLE review_items RENAME COLUMN consecutive_correct TO repetitions;
ALTER TABLE review_items ALTER COLUMN next_review_at TYPE DATE USING next_review_at::DATE;
```

No new columns needed — `ease_factor` and `interval_days` already exist.

**Risk**: `USING next_review_at::DATE` truncates time component. Run `SELECT id, next_review_at, next_review_at::DATE FROM review_items LIMIT 10` before running in production.

---

## Testing Strategy

| Test Class | Tests | Coverage |
|------------|-------|----------|
| `SM2AlgorithmTest` | 11 | All quality values, formula correctness, boundary EF=1.3 |
| `ReviewItemTest` | ~14 | Domain lifecycle, SM-2 integration, LocalDate |
| `GetReviewQueueUseCaseTest` | 6 | Due filtering, limit, sort order (most overdue first) |
| `SubmitReviewResultUseCaseTest` | 3 | SM-2 apply, persist result, not-found |
| `GetReviewStatsUseCaseTest` | 8 | All stat fields including retentionRate, averageInterval |

## Next Step

Execute Phase 1:
```
/execute-plan .ai/plans/2026_04_11-review-spaced-repetition-sm2-backend.md
```
