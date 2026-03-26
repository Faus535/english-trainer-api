# Backend Plan: Learning Journey Redesign v3 (Final)

> Generated: 2026-03-26
> Request: Cohesive learning journey with structured progression, smart content selection, per-word mastery tracking, backend-driven sessions, and dashboard status endpoint
> Previous plans: plans/learning-journey-redesign-backend.md (v1), plans/learning-journey-redesign-2-backend.md (v2)
> Source: 10-agent analysis with backend, frontend, and cross-cutting synthesizer outputs

## Decisions Log

| # | Topic | Decision | Alternatives Considered | Why |
|---|-------|----------|------------------------|-----|
| D1 | Security fix priority | Phase 0: fix SaveMiniGameScoreController immediately | Bundled with Phase 7 (v2 plan) | CRITICAL security bug -- any authenticated user can save scores for any profile. Must not wait for feature development |
| D2 | Phase ordering | Persistence bundled with domain model (not separate phase) | Persistence last (v1) | Cannot integration-test use cases without persistence; blocked 6 phases of untestable code in v1 |
| D3 | LearningUnit aggregate boundary | LearningUnit as separate aggregate root from LearningPath | Nested entity inside LearningPath | Writing one unit's mastery avoids loading/saving entire path tree; no @OneToMany anywhere in codebase |
| D4 | Domain services | Pure domain services receiving pre-loaded data as parameters | Domain services with repository injection | Skill rules: "No external dependencies, receives already-loaded aggregates as parameters" |
| D5 | Session content binding | Backend-driven sessions (POST /sessions/generate returns UUID + content) | Keep client-side generation | Session IDs are client-generated `session-${Date.now()}` strings -- breaks exercise result endpoints. Backend UUID fixes this |
| D6 | VocabMastery.source | Value Object record `MasterySource(context, detail)` | Raw String | Domain-design skill: use Value Objects for domain concepts |
| D7 | Fill-gap game results | vocabEntryId nullable in answeredItems | Force vocabEntryId on all games | Fill-gap is grammar-based, not vocabulary |
| D8 | ReviewCompletedEvent enrichment | Add graduated + itemType fields (2-line change; fields already exist on SpacedRepetitionItem) | Listener reloads SpacedRepetitionItem from repo | Reloading adds latency and repo dependency; enriched event is cleaner |
| D9 | ModuleProgress deprecation | Soft deprecation with runtime migration, deferred to Phase 11 | Immediate hard removal | Two systems cause confusion but hard removal breaks existing features |
| D10 | Migration versioning | V9.0.0, V9.1.0, V9.2.0 (major increments) | V9.0.1 (patch) | Consistent with existing codebase pattern (V8.0.0 through V8.9.0) |
| D11 | Base package | `com.faus535.englishtrainer` | `com.s2grupo.carmen.englishtrainer` | Verified against codebase -- all existing code uses `com.faus535.englishtrainer` |
| D12 | V8.5.0 content | A1 blocks 6-10 (not A2) | A2 content | Verified: V8.5.0 = A1 blocks 6-10, V8.6.0 + V8.7.0 = A2 blocks |
| D13 | CurriculumProvider usage | Wired into GenerateLearningPathUseCase (Phase 2) | Left orphaned | CurriculumProvider + ModuleDefinition exist but are never called -- this fixes the orphan |
| D14 | LevelTestCompletedEvent | New domain event for decoupled assessment-to-learningpath communication | Direct use case call from SubmitLevelTestUseCase | Event-driven decoupling follows existing codebase pattern with @DomainEvent |
| D15 | Validation constraints | @Size(max=200) on answeredItems, @Max(10000) on score | No constraints | Prevents abuse -- 200 items is generous for any single game round |
| D16 | Ownership AOP | Create @RequireProfileOwnership aspect in Phase 0, retrofit 15 controllers later | Manual ProfileOwnershipChecker.check() everywhere | AOP reduces boilerplate; Phase 0 creates the annotation, all new controllers use it |
| D17 | Content binding validation | Application-layer validation in LearningPathGenerator that content IDs reference real entities | No validation | Prevents orphaned unit contents pointing to deleted vocab entries |
| D18 | Idempotent exercise results | UNIQUE constraint on (session_id, exercise_index) | No constraint | Prevents duplicate result submissions from network retries |

## Analysis

### What exists and works:
- **Session module**: `SessionGenerator.java` creates blocks with `(blockType, moduleName, durationMinutes)` -- no content IDs. `Session.java` stores blocks as JSON blob (`blocks_data` TEXT column). Client generates sessions with `session-${Date.now()}` IDs (not UUIDs).
- **Vocabulary blocks**: `V8.3.0` seeds blocks 1-5 for A1 (~20 words each). `V8.5.0` seeds blocks 6-10 for A1. `V8.6.0` + `V8.7.0` seed A2 blocks 1-10. `VocabRepository.findByLevelAndBlock(VocabLevel, int)` already exists.
- **Spaced Repetition**: Full SRS with `SpacedRepetitionItem.createForVocabulary(userId, word, level)`. `ReviewCompletedEvent` only has `(itemId, userId)` -- missing `graduated` flag and `itemType`. Fields `graduated` and `itemType` already exist on `SpacedRepetitionItem` but are not passed to the event.
- **Curriculum**: `CurriculumProvider` + `ModuleDefinition` + `UnitDefinition` loaded from JSON at startup. Never called from sessions or dashboard.
- **Module Progress**: `ModuleProgress.java` tracks per-module unit progression. Will be soft-deprecated by LearningPath.
- **Content selection**: `JpaVocabRepositoryAdapter.findRandom()` loads ALL entries then `Collections.shuffle()`. Performance risk and ignores blocks/SRS.
- **MiniGame**: `SaveMiniGameScoreUseCase` stores aggregate score only. Existing endpoints `GET /api/minigames/word-match`, `/fill-gap`, `/unscramble` exist but frontend ignores them. **`SaveMiniGameScoreController` is missing ownership check (security bug)**.
- **Auth**: `ProfileOwnershipChecker.check(authentication, userId)` validates profile access. JWT with `profileId` claim. Used in 9 controllers, missing from ~15 others.

### Root causes:
1. `SessionGenerator` creates time-labeled blocks without content binding
2. `findRandom()` ignores vocabulary blocks, user progress, and SRS schedule
3. No "what to study next" endpoint
4. Session IDs are client-generated strings, not backend UUIDs
5. Game endpoints exist but are unused by frontend
6. ModuleProgress and CurriculumProvider are disconnected from sessions
7. `SaveMiniGameScoreController` has no ownership check

## Phases

### Phase 0: Security Fix -- SaveMiniGameScoreController Ownership Check

**Goal**: Fix the CRITICAL security bug where any authenticated user can save scores for any profile. Also create the `@RequireProfileOwnership` AOP annotation for all future controllers.

**Files to modify**:
- `src/main/java/com/faus535/englishtrainer/minigame/infrastructure/controller/SaveMiniGameScoreController.java` -- Add `ProfileOwnershipChecker.check(authentication, userId)` before calling use case

**Files to create**:
- `src/main/java/com/faus535/englishtrainer/shared/infrastructure/security/RequireProfileOwnership.java` -- Custom annotation `@RequireProfileOwnership`
- `src/main/java/com/faus535/englishtrainer/shared/infrastructure/security/ProfileOwnershipAspect.java` -- AOP aspect that intercepts `@RequireProfileOwnership` and calls `ProfileOwnershipChecker.check()`

**Details**:

Immediate fix for `SaveMiniGameScoreController`:
```java
@PostMapping("/api/profiles/{userId}/minigames/scores")
ResponseEntity<SaveScoreResponse> handle(@PathVariable UUID userId,
                                          @Valid @RequestBody SaveScoreRequest request,
                                          Authentication authentication) {
    ProfileOwnershipChecker.check(authentication, new UserProfileId(userId));
    // ... existing logic
}
```

AOP annotation for future use:
```java
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RequireProfileOwnership {
    String pathVariable() default "userId";
}
```

AOP aspect:
```java
@Aspect
@Component
public class ProfileOwnershipAspect {
    @Around("@annotation(requireProfileOwnership)")
    public Object checkOwnership(ProceedingJoinPoint joinPoint,
                                  RequireProfileOwnership requireProfileOwnership) throws Throwable {
        // Extract userId from path variable, authentication from SecurityContextHolder
        // Call ProfileOwnershipChecker.check()
        return joinPoint.proceed();
    }
}
```

**Testing**:
- [ ] Unit test: aspect correctly extracts userId and calls checker
- [ ] MockMvc test: SaveMiniGameScoreController returns 403 when userId does not match JWT profileId
- [ ] MockMvc test: SaveMiniGameScoreController returns 200 when userId matches JWT profileId

**Acceptance criteria**:
- [ ] `SaveMiniGameScoreController` calls `ProfileOwnershipChecker.check()` before use case
- [ ] `@RequireProfileOwnership` annotation created and working via AOP
- [ ] Existing tests still pass
- [ ] No other controllers changed yet (retrofit in Phase 11)

---

### Phase 1: Learning Path Domain Model + Persistence

**Goal**: Create the domain model AND persistence layer for LearningPath and LearningUnit as separate aggregates, enabling integration testing from the start.

**Files to create (domain)**:
- `src/main/java/com/faus535/englishtrainer/learningpath/domain/LearningPath.java` -- Aggregate Root: user's learning path with current position
- `src/main/java/com/faus535/englishtrainer/learningpath/domain/LearningPathId.java` -- Value Object (UUID)
- `src/main/java/com/faus535/englishtrainer/learningpath/domain/LearningUnit.java` -- Separate Aggregate Root: a unit within the path
- `src/main/java/com/faus535/englishtrainer/learningpath/domain/LearningUnitId.java` -- Value Object (UUID)
- `src/main/java/com/faus535/englishtrainer/learningpath/domain/UnitContent.java` -- Record Value Object: `(ContentType contentType, UUID contentId, boolean practiced, Instant lastPracticedAt)`
- `src/main/java/com/faus535/englishtrainer/learningpath/domain/UnitStatus.java` -- Enum: NOT_STARTED, IN_PROGRESS, NEEDS_REVIEW, MASTERED
- `src/main/java/com/faus535/englishtrainer/learningpath/domain/MasteryScore.java` -- Value Object: int 0-100 with CHECK constraint
- `src/main/java/com/faus535/englishtrainer/learningpath/domain/ContentType.java` -- Enum: VOCAB, PHRASE, READING, WRITING, GRAMMAR
- `src/main/java/com/faus535/englishtrainer/learningpath/domain/LearningPathRepository.java` -- Repository interface
- `src/main/java/com/faus535/englishtrainer/learningpath/domain/LearningUnitRepository.java` -- Repository interface (separate aggregate)
- `src/main/java/com/faus535/englishtrainer/learningpath/domain/error/LearningPathException.java` -- Base checked exception extends Exception
- `src/main/java/com/faus535/englishtrainer/learningpath/domain/error/LearningPathNotFoundException.java` -- Extends LearningPathException
- `src/main/java/com/faus535/englishtrainer/learningpath/domain/error/LearningUnitNotFoundException.java` -- Extends LearningPathException
- `src/main/java/com/faus535/englishtrainer/learningpath/domain/event/UnitMasteredEvent.java` -- Domain event published when unit reaches MASTERED
- `src/main/java/com/faus535/englishtrainer/learningpath/domain/event/LevelCompletedEvent.java` -- Domain event published when all units at a level are mastered

**Files to create (persistence)**:
- `src/main/resources/db/migration/V9.0.0__create_learning_paths.sql` -- Migration
- `src/main/java/com/faus535/englishtrainer/learningpath/infrastructure/persistence/JpaLearningPathEntity.java` -- Persistable<UUID> with @Version
- `src/main/java/com/faus535/englishtrainer/learningpath/infrastructure/persistence/JpaLearningUnitEntity.java` -- Persistable<UUID> with @Version
- `src/main/java/com/faus535/englishtrainer/learningpath/infrastructure/persistence/SpringDataLearningPathRepository.java` -- JpaRepository
- `src/main/java/com/faus535/englishtrainer/learningpath/infrastructure/persistence/SpringDataLearningUnitRepository.java` -- JpaRepository
- `src/main/java/com/faus535/englishtrainer/learningpath/infrastructure/persistence/JpaLearningPathRepositoryAdapter.java` -- Adapter mapping domain <-> entity
- `src/main/java/com/faus535/englishtrainer/learningpath/infrastructure/persistence/JpaLearningUnitRepositoryAdapter.java` -- Adapter mapping domain <-> entity, JSON serialization of contents_data

**Files to create (testing)**:
- `src/test/java/com/faus535/englishtrainer/learningpath/domain/InMemoryLearningPathRepository.java` -- HashMap-based in-memory repo
- `src/test/java/com/faus535/englishtrainer/learningpath/domain/InMemoryLearningUnitRepository.java` -- HashMap-based in-memory repo
- `src/test/java/com/faus535/englishtrainer/learningpath/domain/LearningPathMother.java` -- Object Mother with `create()` defaults
- `src/test/java/com/faus535/englishtrainer/learningpath/domain/LearningUnitMother.java` -- Object Mother with `create()` defaults
- `src/test/java/com/faus535/englishtrainer/learningpath/domain/LearningPathTest.java` -- Domain logic tests
- `src/test/java/com/faus535/englishtrainer/learningpath/domain/LearningUnitTest.java` -- Status transition tests
- `src/test/java/com/faus535/englishtrainer/learningpath/domain/MasteryScoreTest.java` -- Value object range tests
- `src/test/java/com/faus535/englishtrainer/learningpath/infrastructure/persistence/JpaLearningPathRepositoryAdapterTest.java` -- Testcontainers integration test

**Details**:

`LearningPath` aggregate (lightweight, references unit IDs only):
```java
public final class LearningPath extends AggregateRoot<LearningPathId> {
    private final LearningPathId id;
    private final UserProfileId userId;
    private final String currentLevel;  // CEFR level
    private int currentUnitIndex;
    private final List<LearningUnitId> unitIds;
    private final Instant createdAt;
    private Instant updatedAt;

    public static LearningPath create(UserProfileId userId, String currentLevel, List<LearningUnitId> unitIds) { ... }
    public static LearningPath reconstitute(...) { ... }
    public LearningUnitId getCurrentUnitId() { ... }
    public void advanceToNextUnit() { ... }  // increments currentUnitIndex, sets updatedAt
    public boolean isCompleted() { return currentUnitIndex >= unitIds.size(); }
}
```

`LearningUnit` aggregate (separate, updated independently):
```java
public final class LearningUnit extends AggregateRoot<LearningUnitId> {
    private final LearningUnitId id;
    private final LearningPathId learningPathId;
    private final int unitIndex;
    private final String unitName;
    private final String targetLevel;
    private UnitStatus status;
    private MasteryScore masteryScore;
    private List<UnitContent> contents;  // stored as JSON in contents_data TEXT column
    private Instant completedAt;

    public static LearningUnit create(...) { ... }
    public static LearningUnit reconstitute(...) { ... }
    public void startUnit() { ... }  // NOT_STARTED -> IN_PROGRESS
    public void updateMastery(MasteryScore newScore) { ... }  // recalculates status
    public void markContentPracticed(UUID contentId) { ... }
    public List<UnitContent> getUnpracticedContents() { ... }
    public List<UnitContent> getUnpracticedContentsByType(ContentType type) { ... }
}
```

`MasteryScore` value object:
```java
public record MasteryScore(int value) {
    public MasteryScore {
        if (value < 0 || value > 100) throw new IllegalArgumentException("Mastery score must be 0-100");
    }
    public boolean isMastered() { return value >= 70; }
    public boolean needsReview() { return value < 50; }
}
```

Migration V9.0.0:
```sql
CREATE TABLE learning_paths (
    id UUID PRIMARY KEY,
    user_profile_id UUID NOT NULL REFERENCES user_profiles(id) ON DELETE CASCADE,
    current_level VARCHAR(10) NOT NULL,
    current_unit_index INT NOT NULL DEFAULT 0,
    version BIGINT NOT NULL DEFAULT 0,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(user_profile_id)
);

CREATE TABLE learning_units (
    id UUID PRIMARY KEY,
    learning_path_id UUID NOT NULL REFERENCES learning_paths(id) ON DELETE CASCADE,
    unit_index INT NOT NULL,
    unit_name VARCHAR(100) NOT NULL,
    target_level VARCHAR(10) NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'NOT_STARTED',
    mastery_score INT NOT NULL DEFAULT 0 CHECK (mastery_score >= 0 AND mastery_score <= 100),
    contents_data TEXT,
    version BIGINT NOT NULL DEFAULT 0,
    completed_at TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(learning_path_id, unit_index)
);

CREATE INDEX idx_learning_paths_user ON learning_paths(user_profile_id);
CREATE INDEX idx_learning_units_path ON learning_units(learning_path_id);
CREATE INDEX idx_learning_units_path_status ON learning_units(learning_path_id, status);
```

Note: `contents_data` stored as JSON TEXT column following the `sessions.blocks_data` pattern. JSON serialization/deserialization in the repository adapter, not the entity.

**Security**: No endpoints in this phase -- domain + persistence only.

**Testing**:
- [ ] Unit tests for LearningPath: create, reconstitute, advanceToNextUnit, isCompleted
- [ ] Unit tests for LearningUnit: status transitions (NOT_STARTED -> IN_PROGRESS -> MASTERED/NEEDS_REVIEW)
- [ ] Unit tests for MasteryScore: 0-100 range enforcement, isMastered(), needsReview()
- [ ] Integration test (Testcontainers): save + load LearningPath, save + load LearningUnit with JSON contents
- [ ] GAP-4 test: concurrent unit mastery updates with optimistic locking (@Version)

**Acceptance criteria**:
- [ ] LearningPath aggregate with immutable fields and create()/reconstitute() factory methods
- [ ] LearningUnit as separate aggregate with status transitions
- [ ] MasteryScore value object enforces 0-100 range
- [ ] All entities implement Persistable<UUID> with @Version
- [ ] `version BIGINT` on all tables, `VARCHAR(10)` for levels, `CURRENT_TIMESTAMP` defaults
- [ ] CHECK constraint on mastery_score (0-100)
- [ ] In-memory repositories + Object Mothers for testing
- [ ] Migration V9.0.0 runs cleanly on PostgreSQL (Testcontainers)
- [ ] Checked exceptions: LearningPathException extends Exception

---

### Phase 2: Learning Path Generation Use Case

**Goal**: Auto-generate a personalized learning path when a user completes their level test or requests on-demand generation. Wire CurriculumProvider into the generation flow.

**Files to create**:
- `src/main/java/com/faus535/englishtrainer/learningpath/application/GenerateLearningPathUseCase.java` -- @UseCase, orchestrates path creation
- `src/main/java/com/faus535/englishtrainer/learningpath/domain/LearningPathGenerator.java` -- Pure domain service, receives pre-loaded data as parameters
- `src/main/java/com/faus535/englishtrainer/learningpath/infrastructure/controller/GenerateLearningPathController.java` -- POST /api/profiles/{profileId}/learning-path/generate
- `src/main/java/com/faus535/englishtrainer/learningpath/infrastructure/controller/LearningPathControllerAdvice.java` -- Error handling for all learningpath controllers
- `src/main/java/com/faus535/englishtrainer/assessment/domain/event/LevelTestCompletedEvent.java` -- Domain event for decoupled assessment-to-learningpath communication
- `src/main/java/com/faus535/englishtrainer/learningpath/infrastructure/event/LevelTestCompletedListener.java` -- @EventListener that triggers path generation

**Files to modify**:
- `src/main/java/com/faus535/englishtrainer/assessment/application/SubmitLevelTestUseCase.java` -- Publish LevelTestCompletedEvent after setting levels (instead of direct use case call)

**Details**:

`LearningPathGenerator` (pure domain service):
```java
public class LearningPathGenerator {
    public record GenerationResult(LearningPath path, List<LearningUnit> units) {}

    public GenerationResult generate(
        UserProfileId userId,
        String currentLevel,
        List<VocabEntry> vocabEntries,         // pre-loaded by use case
        List<Phrase> phrases,                   // pre-loaded by use case
        List<UnitDefinition> curriculumUnits,   // from CurriculumProvider
        Set<UUID> alreadyPracticedIds           // from SRS history
    ) {
        // 1. Group vocab by block number (blocks 1-10 for A1/A2)
        // 2. Map each block to a LearningUnit with ~20 vocab + ~5 phrases
        // 3. For levels without blocks, group by category
        // 4. Mark already-practiced content IDs
        // 5. Validate all contentIds reference real entries (GAP-1 content binding)
        // 6. Return GenerationResult
    }
}
```

`GenerateLearningPathUseCase.execute(UserProfileId userId)`:
1. Load UserProfile -> get current levels
2. Load vocab entries by level via `vocabRepository.findByLevel()`
3. Load phrases by level via `phraseRepository.findByLevel()`
4. Load curriculum units via `curriculumProvider.getModules()` -- wires the orphaned CurriculumProvider
5. Load SRS history -> get already-practiced IDs
6. Call `LearningPathGenerator.generate(...)` with all pre-loaded data
7. If existing path exists -> delete old one (UNIQUE constraint on user_profile_id ensures one active path)
8. Save new LearningPath + all LearningUnits
9. Return summary

`LevelTestCompletedEvent` for decoupled communication:
```java
public record LevelTestCompletedEvent(UserProfileId userId, String level) {}
```

`LevelTestCompletedListener`:
```java
@Component
class LevelTestCompletedListener {
    private final GenerateLearningPathUseCase generateLearningPathUseCase;

    @EventListener
    void on(LevelTestCompletedEvent event) throws LearningPathException {
        generateLearningPathUseCase.execute(event.userId());
    }
}
```

Controller (package-private, inner record DTOs):
```java
@RestController
class GenerateLearningPathController {
    record GenerateResponse(UUID learningPathId, int unitsGenerated, String currentLevel) {}

    @PostMapping("/api/profiles/{profileId}/learning-path/generate")
    @RequireProfileOwnership(pathVariable = "profileId")
    ResponseEntity<GenerateResponse> handle(@PathVariable UUID profileId,
                                             Authentication authentication)
            throws LearningPathException { ... }
}
```

**Security**:
- `@RequireProfileOwnership` on controller method (from Phase 0 AOP)
- ControllerAdvice maps LearningPathException -> 404, other exceptions -> 400/500

**Testing**:
- [ ] Unit test: LearningPathGenerator produces correct units from vocab blocks (A1 blocks 1-10)
- [ ] Unit test: already-practiced content marked correctly
- [ ] Unit test: levels without blocks fall back to category grouping
- [ ] GAP-1 test: content binding verification -- generator rejects unknown content IDs
- [ ] Use case test (in-memory repos): full generation flow
- [ ] MockMvc test: POST /generate returns 201, ownership check enforced

**Acceptance criteria**:
- [ ] Learning path generated with correct units per level
- [ ] A1 vocabulary blocks (V8.3.0 + V8.5.0) used for unit structure
- [ ] A2 vocabulary blocks (V8.6.0 + V8.7.0) used when level is A2
- [ ] CurriculumProvider wired into generation (no longer orphaned)
- [ ] LevelTestCompletedEvent published by SubmitLevelTestUseCase
- [ ] Path regenerates automatically on level test completion
- [ ] On-demand generation endpoint for existing users
- [ ] Already-reviewed items marked as practiced in unit contents
- [ ] LearningPathGenerator is a pure domain service (no DI, no repositories)
- [ ] `@RequireProfileOwnership` on controller

---

### Phase 3: Smart Content Selection (Replace Random)

**Goal**: Replace `findRandom()` with intelligent content selection following the learning path and integrating spaced repetition. 70% new content from current unit + 30% SRS review items.

**Files to create**:
- `src/main/java/com/faus535/englishtrainer/learningpath/application/GetNextContentUseCase.java` -- @UseCase, returns content for current unit + SRS due items
- `src/main/java/com/faus535/englishtrainer/learningpath/domain/ContentSelector.java` -- Pure domain service: 70/30 new/review mix

**Files to modify**:
- `src/main/java/com/faus535/englishtrainer/vocabulary/domain/VocabRepository.java` -- Add `findByIds(List<UUID>)`, `findByLevelExcludingIds(VocabLevel, Set<UUID>, int)`
- `src/main/java/com/faus535/englishtrainer/vocabulary/infrastructure/persistence/JpaVocabRepositoryAdapter.java` -- Implement new queries

**Details**:

`ContentSelector` (pure domain service):
```java
public class ContentSelector {
    public record ContentSelection(List<UUID> newContentIds, List<UUID> reviewContentIds) {
        public List<UUID> allIds() {
            var all = new ArrayList<>(newContentIds);
            all.addAll(reviewContentIds);
            return all;
        }
    }

    public ContentSelection select(
        List<UnitContent> unpracticedContents,    // from current unit
        List<SpacedRepetitionItem> dueItems,       // SRS due today
        Set<UUID> recentlyPracticedIds,            // practiced in last 24h
        int requestedCount
    ) {
        // 1. Filter out recently practiced (last 24h)
        // 2. Calculate split: 70% new, 30% review (adjust if insufficient new/review)
        // 3. Select new items from unpracticed contents
        // 4. Select review items from SRS due list
        // 5. If no new items left in unit -> 100% review
        // 6. Return ContentSelection
    }
}
```

`GetNextContentUseCase.execute(UserProfileId userId, ContentType type, int count)`:
1. Load LearningPath -> get current unit ID
2. Load LearningUnit -> get unpracticed contents of requested type
3. Call `GetDueReviewsUseCase.execute(userId)` -> SRS items due today
4. Get recently practiced IDs (last 24h) from activity/SRS history
5. Call `ContentSelector.select(...)` with all pre-loaded data
6. Return ordered list of content IDs

Note: `findByLevelAndBlock()` already exists -- no need to add it. Only add `findByIds()` and `findByLevelExcludingIds()`.

**Security**: No new endpoints in this phase (internal use case).

**Testing**:
- [ ] Unit test: ContentSelector produces 70/30 mix when sufficient content
- [ ] Unit test: ContentSelector adjusts ratio when new content exhausted
- [ ] Unit test: recently-practiced items excluded
- [ ] Unit test: empty unit contents returns 100% review
- [ ] Use case test (in-memory repos): full content selection flow

**Acceptance criteria**:
- [ ] Content selection follows learning path unit order
- [ ] SRS due items mixed into every session (30% target)
- [ ] Items practiced in last 24h excluded
- [ ] `findRandom()` only used as fallback when no learning path exists
- [ ] ContentSelector is a pure domain service (no DI)

---

### Phase 4: Backend-Driven Session Generation

**Goal**: Move session generation to backend. Sessions have UUID IDs, real content, and exercise counts scaled to block duration. Replace client-side `session-${Date.now()}` IDs.

**Files to create**:
- `src/main/java/com/faus535/englishtrainer/session/domain/SessionExercise.java` -- Value Object: `(int exerciseIndex, String exerciseType, List<UUID> contentIds, int targetCount, ExerciseResult result)`
- `src/main/java/com/faus535/englishtrainer/session/domain/ExerciseResult.java` -- Value Object: `(int correctCount, int totalCount, long averageResponseTimeMs, Instant completedAt)`
- `src/main/resources/db/migration/V9.1.0__add_session_exercises.sql` -- Add exercises_data TEXT column + index

**Files to modify**:
- `src/main/java/com/faus535/englishtrainer/session/domain/Session.java` -- Add `List<SessionExercise>` serialized as JSON, `recordExerciseResult(int exerciseIndex, ExerciseResult)`
- `src/main/java/com/faus535/englishtrainer/session/domain/SessionBlock.java` -- Add `exerciseCount` (int), `contentIds` (List<UUID>)
- `src/main/java/com/faus535/englishtrainer/session/domain/SessionGenerator.java` -- Accept content from GetNextContentUseCase, calculate exercise counts per block
- `src/main/java/com/faus535/englishtrainer/session/application/GenerateSessionUseCase.java` -- Wire content selection into session creation
- `src/main/java/com/faus535/englishtrainer/session/infrastructure/persistence/JpaSessionEntity.java` -- Add `exercisesData` TEXT column mapping

**Details**:

Exercise count scaling by block type:
```java
// Items per minute by block type
private static final Map<String, Double> ITEMS_PER_MINUTE = Map.of(
    "listening", 1.5,
    "vocabulary", 2.0,
    "grammar", 2.5,
    "pronunciation", 1.0,
    "phrases", 2.0
);

static int getExerciseCount(String blockType, int durationMinutes) {
    double rate = ITEMS_PER_MINUTE.getOrDefault(blockType, 2.0);
    return Math.max(3, (int) Math.round(durationMinutes * rate));
}
```

`GenerateSessionUseCase.execute()` updated flow:
1. Load user profile -> get levels
2. For each block type, call `GetNextContentUseCase.execute(userId, contentType, exerciseCount)` to get content IDs
3. Calculate exercise count per block based on duration
4. Create Session with populated blocks + exercises (UUID ID generated by backend)
5. Save session with exercises_data JSON
6. Return Session with UUID (frontend uses this for all result POSTs)

Migration V9.1.0:
```sql
ALTER TABLE sessions ADD COLUMN exercises_data TEXT;
CREATE INDEX idx_sessions_user_created ON sessions(user_profile_id, created_at DESC);
```

**Security**: Existing `GenerateSessionUseCase` already has ownership checks.

**Testing**:
- [ ] Unit test: exercise count scales correctly with block duration
- [ ] Unit test: Session.recordExerciseResult validates exerciseIndex bounds
- [ ] Integration test: session saved with exercises_data JSON, reloaded correctly
- [ ] GAP-6 test: N+1 query prevention when loading session with exercises

**Acceptance criteria**:
- [ ] Sessions contain specific content IDs per block
- [ ] Exercise count scales with block duration (e.g., full/21min -> ~37 items total)
- [ ] Session has UUID ID generated by backend
- [ ] Exercises stored as JSON in `exercises_data` TEXT column
- [ ] Migration V9.1.0 adds column to existing sessions table without data loss
- [ ] Backward compatible: sessions without exercises_data still loadable

---

### Phase 5: Mastery Tracking & Exercise Results

**Goal**: Record exercise results, update unit mastery scores, and advance units when mastery threshold (70%) is reached.

**Files to create**:
- `src/main/java/com/faus535/englishtrainer/learningpath/application/RecordExerciseResultUseCase.java` -- @UseCase, updates mastery when exercises completed
- `src/main/java/com/faus535/englishtrainer/learningpath/application/AdvanceUnitUseCase.java` -- @UseCase, checks mastery and advances to next unit
- `src/main/java/com/faus535/englishtrainer/learningpath/domain/MasteryCalculator.java` -- Pure domain service
- `src/main/java/com/faus535/englishtrainer/session/infrastructure/controller/RecordExerciseResultController.java` -- POST endpoint
- `src/main/java/com/faus535/englishtrainer/session/infrastructure/controller/SessionControllerAdvice.java` -- Error handling (if not already exists)

**Details**:

`MasteryCalculator` (pure domain service):
```java
public class MasteryCalculator {
    // Weights by exercise type
    private static final Map<String, Double> WEIGHTS = Map.of(
        "VOCAB_QUIZ", 0.40,
        "LISTENING", 0.25,
        "PRONUNCIATION", 0.20,
        "GRAMMAR", 0.15
    );

    public MasteryScore calculate(List<ExerciseResult> results) {
        // 1. Group results by exercise type
        // 2. For each type: weighted average with exponential decay (recent attempts weighted more)
        // 3. Combine weighted scores
        // 4. Return MasteryScore(0-100)
    }
}
```

`RecordExerciseResultUseCase.execute(UserProfileId userId, UUID sessionId, int exerciseIndex, ExerciseResult result)`:
1. Load Session -> validate it belongs to user (ownership check at controller level)
2. Record ExerciseResult on SessionExercise at given index
3. Load LearningUnit for current unit from LearningPath
4. Mark practiced content IDs in unit
5. Call `MasteryCalculator.calculate(...)` with all exercise results for this unit
6. Update unit masteryScore and status
7. If mastery >= 70% AND all content practiced -> call `AdvanceUnitUseCase`
8. Save updated Session and LearningUnit
9. Return `{ unitMasteryScore, unitStatus, xpEarned }`

`AdvanceUnitUseCase`:
1. Mark current unit MASTERED, set completedAt
2. Schedule all unit content into SRS via `AddVocabularyToReviewUseCase`
3. Load LearningPath, call `advanceToNextUnit()`
4. Set next unit status to IN_PROGRESS
5. If all units at level mastered -> publish `LevelCompletedEvent`
6. Save LearningPath + both LearningUnits

Idempotency: `RecordExerciseResultController` checks if result already recorded for (sessionId, exerciseIndex) before processing. Returns existing result if duplicate.

**Security**:
- `@RequireProfileOwnership` on RecordExerciseResultController
- Session ownership validated (session.userId must match path profileId)

**Testing**:
- [ ] Unit test: MasteryCalculator weighted calculation correct
- [ ] Unit test: exponential decay weights recent results more
- [ ] Unit test: mastery < 50% -> NEEDS_REVIEW, 50-69% -> IN_PROGRESS, >= 70% -> MASTERED
- [ ] Unit test: AdvanceUnitUseCase publishes LevelCompletedEvent when all units mastered
- [ ] GAP-3 test: level completion workflow end-to-end
- [ ] GAP-4 test: concurrent unit mastery updates handled by optimistic locking
- [ ] MockMvc test: POST exercise result returns 200 with mastery update

**Acceptance criteria**:
- [ ] Exercise results recorded and persist on Session
- [ ] Mastery score calculated from weighted exercise performance
- [ ] Units require >= 70% mastery to advance
- [ ] Weak units (NEEDS_REVIEW) identified for extra content in future sessions
- [ ] Completed unit content auto-scheduled into SRS
- [ ] Duplicate result submissions handled idempotently
- [ ] `@RequireProfileOwnership` on controller

---

### Phase 6: Vocabulary Mastery Domain + Persistence

**Goal**: Track per-word mastery across games and sessions. Words progress: LEARNING -> LEARNED -> MASTERED.

**Files to create (domain)**:
- `src/main/java/com/faus535/englishtrainer/vocabulary/domain/VocabMastery.java` -- Aggregate Root
- `src/main/java/com/faus535/englishtrainer/vocabulary/domain/VocabMasteryId.java` -- Value Object (UUID)
- `src/main/java/com/faus535/englishtrainer/vocabulary/domain/VocabMasteryStatus.java` -- Enum: LEARNING, LEARNED, MASTERED
- `src/main/java/com/faus535/englishtrainer/vocabulary/domain/MasterySource.java` -- Value Object record: `(String context, String detail)` e.g. ("game", "word-match")
- `src/main/java/com/faus535/englishtrainer/vocabulary/domain/VocabMasteryRepository.java` -- Repository interface
- `src/main/java/com/faus535/englishtrainer/vocabulary/domain/event/WordLearnedEvent.java` -- Domain event when LEARNING -> LEARNED
- `src/main/java/com/faus535/englishtrainer/vocabulary/domain/event/WordMasteredEvent.java` -- Domain event when LEARNED -> MASTERED

**Files to create (persistence)**:
- `src/main/resources/db/migration/V9.2.0__create_vocab_mastery.sql` -- Migration
- `src/main/java/com/faus535/englishtrainer/vocabulary/infrastructure/persistence/JpaVocabMasteryEntity.java` -- Persistable<UUID> with @Version
- `src/main/java/com/faus535/englishtrainer/vocabulary/infrastructure/persistence/SpringDataVocabMasteryRepository.java` -- JpaRepository
- `src/main/java/com/faus535/englishtrainer/vocabulary/infrastructure/persistence/JpaVocabMasteryRepositoryAdapter.java` -- Adapter

**Files to create (testing)**:
- `src/test/java/com/faus535/englishtrainer/vocabulary/domain/InMemoryVocabMasteryRepository.java` -- HashMap-based
- `src/test/java/com/faus535/englishtrainer/vocabulary/domain/VocabMasteryMother.java` -- Object Mother
- `src/test/java/com/faus535/englishtrainer/vocabulary/domain/VocabMasteryTest.java` -- Status transition tests

**Details**:

`VocabMastery` aggregate:
```java
public final class VocabMastery extends AggregateRoot<VocabMasteryId> {
    private final VocabMasteryId id;
    private final UserProfileId userId;
    private final VocabEntryId vocabEntryId;  // nullable for grammar items
    private final String word;
    private int correctCount;
    private int incorrectCount;
    private int totalAttempts;
    private VocabMasteryStatus status;
    private final MasterySource source;
    private Instant lastPracticedAt;
    private Instant learnedAt;  // nullable

    public static VocabMastery create(UserProfileId userId, VocabEntryId vocabEntryId, String word, MasterySource source) { ... }
    public static VocabMastery reconstitute(...) { ... }

    public void recordCorrectAnswer() {
        correctCount++;
        totalAttempts++;
        lastPracticedAt = Instant.now();
        recalculateStatus();
    }

    public void recordIncorrectAnswer() {
        incorrectCount++;
        totalAttempts++;
        lastPracticedAt = Instant.now();
        recalculateStatus();
    }

    public int getAccuracy() {
        return totalAttempts == 0 ? 0 : (int) ((correctCount * 100.0) / totalAttempts);
    }

    public void graduate() {
        status = VocabMasteryStatus.MASTERED;
        registerEvent(new WordMasteredEvent(id, userId, word));
    }

    private void recalculateStatus() {
        if (status == VocabMasteryStatus.MASTERED) return;  // mastered is terminal except via SRS
        if (correctCount >= 3 && getAccuracy() >= 70) {
            if (status != VocabMasteryStatus.LEARNED) {
                status = VocabMasteryStatus.LEARNED;
                learnedAt = Instant.now();
                registerEvent(new WordLearnedEvent(id, userId, vocabEntryId, word));
            }
        } else if (status == VocabMasteryStatus.LEARNED && getAccuracy() < 60) {
            status = VocabMasteryStatus.LEARNING;  // regression
            learnedAt = null;
        }
    }
}
```

Migration V9.2.0:
```sql
CREATE TABLE vocab_mastery (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL REFERENCES user_profiles(id) ON DELETE CASCADE,
    vocab_entry_id UUID REFERENCES vocab_entries(id) ON DELETE CASCADE,
    word VARCHAR(100) NOT NULL,
    correct_count INT NOT NULL DEFAULT 0 CHECK (correct_count >= 0),
    incorrect_count INT NOT NULL DEFAULT 0 CHECK (incorrect_count >= 0),
    total_attempts INT NOT NULL DEFAULT 0 CHECK (total_attempts >= 0),
    status VARCHAR(20) NOT NULL DEFAULT 'LEARNING',
    source_context VARCHAR(50) NOT NULL,
    source_detail VARCHAR(50),
    last_practiced_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    learned_at TIMESTAMP,
    version BIGINT NOT NULL DEFAULT 0,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(user_id, vocab_entry_id)
);

CREATE INDEX idx_vocab_mastery_user ON vocab_mastery(user_id);
CREATE INDEX idx_vocab_mastery_user_status ON vocab_mastery(user_id, status);
```

Note: `vocab_entry_id` is nullable to support grammar-only game items (fill-gap). UNIQUE constraint on `(user_id, vocab_entry_id)` only applies when `vocab_entry_id IS NOT NULL`.

**Security**: No endpoints in this phase -- domain + persistence only.

**Testing**:
- [ ] Unit test: LEARNING -> LEARNED at correctCount >= 3 AND accuracy >= 70%
- [ ] Unit test: LEARNED -> LEARNING regression at accuracy < 60%
- [ ] Unit test: MASTERED is terminal (recordCorrectAnswer/recordIncorrectAnswer don't change status)
- [ ] Unit test: WordLearnedEvent published on LEARNING -> LEARNED transition
- [ ] Unit test: graduate() sets MASTERED and publishes WordMasteredEvent
- [ ] Unit test: MasterySource value object creation
- [ ] Integration test (Testcontainers): save + load, UNIQUE constraint test

**Acceptance criteria**:
- [ ] VocabMastery aggregate with checked exceptions
- [ ] Status transitions with events: LEARNING -> LEARNED (WordLearnedEvent) -> MASTERED (WordMasteredEvent)
- [ ] Incorrect answers regress LEARNED -> LEARNING at < 60% accuracy
- [ ] MasterySource is a Value Object record, not raw String
- [ ] Entity implements Persistable<UUID> with @Version
- [ ] `version BIGINT`, `updated_at`, `CURRENT_TIMESTAMP` in migration
- [ ] CHECK constraints on correct_count, incorrect_count, total_attempts (>= 0)
- [ ] vocab_entry_id nullable for grammar items
- [ ] In-memory repository + Object Mother for testing

---

### Phase 7: Game Results Use Case

**Goal**: New endpoint to save game results with per-word correctness data. Correctly-answered words feed into VocabMastery and eventually SRS.

**Files to create**:
- `src/main/java/com/faus535/englishtrainer/minigame/application/SaveGameResultsUseCase.java` -- @UseCase, processes game results with per-word data
- `src/main/java/com/faus535/englishtrainer/minigame/infrastructure/controller/SaveGameResultsController.java` -- POST /api/profiles/{userId}/minigames/results
- `src/main/java/com/faus535/englishtrainer/minigame/infrastructure/controller/MiniGameControllerAdvice.java` -- Error handling

**Details**:

`SaveGameResultsUseCase.execute(UserProfileId userId, String gameType, int score, List<AnsweredItem> answeredItems)`:
1. Validate: `answeredItems.size() <= 200`, `score <= 10000` (D15)
2. Save game score (reuse existing `SaveMiniGameScoreUseCase` logic for XP calculation)
3. For each answeredItem where `vocabEntryId` is not null:
   a. Find VocabMastery by userId + vocabEntryId, or create new one
   b. Call `recordCorrectAnswer()` or `recordIncorrectAnswer()`
   c. Save updated VocabMastery
4. Collect newly-learned words (those where WordLearnedEvent was published)
5. Domain events (WordLearnedEvent) handled by listener -> auto-schedules SRS
6. Return `SaveGameResultsResponse(score, xpEarned, wordsLearned, wordsAddedToReview, totalWordsEncountered)`

Controller (package-private, inner record DTOs):
```java
@RestController
class SaveGameResultsController {
    record AnsweredItemDto(
        UUID vocabEntryId,         // nullable for grammar games
        String word,
        @NotBlank String level,
        boolean correct
    ) {}

    record SaveGameResultsRequest(
        @NotBlank String gameType,
        @NotNull @Min(0) @Max(10000) Integer score,
        @NotNull @Size(max = 200) @Valid List<AnsweredItemDto> answeredItems
    ) {}

    record SaveGameResultsResponse(int score, int xpEarned, List<String> wordsLearned,
                                    int wordsAddedToReview, int totalWordsEncountered) {}

    @PostMapping("/api/profiles/{userId}/minigames/results")
    @RequireProfileOwnership
    ResponseEntity<SaveGameResultsResponse> handle(@PathVariable UUID userId,
                                                    @Valid @RequestBody SaveGameResultsRequest request,
                                                    Authentication authentication) throws Exception { ... }
}
```

**Security**:
- `@RequireProfileOwnership` on controller
- `@Max(10000)` on score, `@Size(max=200)` on answeredItems (D15)
- `vocabEntryId` nullable (D7) -- fill-gap grammar game has no vocab IDs

**Testing**:
- [ ] Unit test: per-word mastery created/updated correctly
- [ ] Unit test: words reaching LEARNED trigger event (SRS scheduling via listener)
- [ ] Unit test: null vocabEntryId items skipped for mastery tracking
- [ ] Unit test: backward compatible -- empty answeredItems behaves like old endpoint
- [ ] MockMvc test: validation constraints enforced (@Max, @Size)
- [ ] MockMvc test: ownership check enforced

**Acceptance criteria**:
- [ ] Game results saved with per-word correctness
- [ ] VocabMastery created/updated for answered words with vocabEntryId
- [ ] Words reaching LEARNED auto-added to SRS via WordLearnedEvent listener
- [ ] `vocabEntryId` nullable for grammar games (fill-gap)
- [ ] `@RequireProfileOwnership` on controller
- [ ] `@Max(10000)` on score, `@Size(max=200)` on answeredItems
- [ ] Backward compatible: empty answeredItems behaves like old endpoint
- [ ] XP calculated from score (existing logic preserved)

---

### Phase 8: Exclude Mastered Words + Unlearned Vocab Endpoint

**Goal**: Words with LEARNED/MASTERED status don't appear as "new" in games or sessions. New endpoints return unlearned words and vocabulary progress.

**Files to create**:
- `src/main/java/com/faus535/englishtrainer/vocabulary/application/GetUnlearnedVocabUseCase.java` -- @UseCase, returns vocab user hasn't mastered
- `src/main/java/com/faus535/englishtrainer/vocabulary/infrastructure/controller/GetUnlearnedVocabController.java` -- GET /api/profiles/{userId}/vocabulary/unlearned
- `src/main/java/com/faus535/englishtrainer/vocabulary/application/GetVocabProgressUseCase.java` -- @UseCase, returns paginated vocabulary progress
- `src/main/java/com/faus535/englishtrainer/vocabulary/infrastructure/controller/GetVocabProgressController.java` -- GET /api/profiles/{userId}/vocabulary/progress
- `src/main/java/com/faus535/englishtrainer/vocabulary/infrastructure/controller/VocabularyControllerAdvice.java` -- Error handling

**Files to modify**:
- `src/main/java/com/faus535/englishtrainer/vocabulary/domain/VocabRepository.java` -- Add `findByLevelExcludingIds(VocabLevel, Set<UUID>, int)`
- `src/main/java/com/faus535/englishtrainer/vocabulary/infrastructure/persistence/JpaVocabRepositoryAdapter.java` -- Implement exclusion query (NOT IN with parameter list, or JOIN approach for large sets)
- `src/main/java/com/faus535/englishtrainer/learningpath/domain/ContentSelector.java` (from Phase 3) -- Filter out LEARNED/MASTERED from "new content" pool

**Details**:

`GetUnlearnedVocabUseCase.execute(UserProfileId userId, VocabLevel level, int count)`:
1. Load all VocabMastery IDs with status LEARNED or MASTERED for this user
2. Call `vocabRepository.findByLevelExcludingIds(level, learnedIds, count)`
3. Count remaining unlearned words
4. Return items + remainingCount

Performance note: for the exclusion query, use a subquery approach rather than loading all mastered IDs into application memory:
```sql
SELECT ve.* FROM vocab_entries ve
WHERE ve.level = :level
  AND ve.id NOT IN (SELECT vm.vocab_entry_id FROM vocab_mastery vm WHERE vm.user_id = :userId AND vm.status IN ('LEARNED', 'MASTERED'))
ORDER BY ve.block_number, ve.id
LIMIT :count
```

`GetVocabProgressUseCase.execute(UserProfileId userId, VocabLevel level, int page, int size)`:
1. Load VocabMastery entries for user, optionally filtered by level
2. Group by status: learning, learned, mastered
3. Calculate stats: totalEncountered, totalLearned, totalMastered, avgAccuracy
4. For LEARNED words: join with SRS to get next review date
5. Return paginated response

**Security**:
- `@RequireProfileOwnership` on both controllers

**Testing**:
- [ ] Unit test: unlearned excludes LEARNED and MASTERED words
- [ ] Unit test: remainingCount accurate
- [ ] Unit test: vocab progress groups correctly by status
- [ ] Integration test: exclusion query performs correctly with large dataset
- [ ] MockMvc test: pagination and level filter work
- [ ] MockMvc test: ownership check enforced

**Acceptance criteria**:
- [ ] Games receive only unlearned words for the user
- [ ] MASTERED words never appear outside SRS review
- [ ] Vocab progress endpoint returns grouped data with stats
- [ ] Pagination with filter by level (default 50 items per page)
- [ ] If all vocab at level learned, endpoint returns empty items + `remainingCount: 0`
- [ ] `@RequireProfileOwnership` on both controllers

---

### Phase 9: Dashboard Learning Status Endpoint

**Goal**: Single endpoint giving the frontend everything for a learning-journey-focused dashboard. Also expose the raw learning path.

**Files to create**:
- `src/main/java/com/faus535/englishtrainer/learningpath/application/GetLearningStatusUseCase.java` -- @UseCase, aggregates data from multiple modules
- `src/main/java/com/faus535/englishtrainer/learningpath/infrastructure/controller/GetLearningStatusController.java` -- GET /api/profiles/{profileId}/learning-status
- `src/main/java/com/faus535/englishtrainer/learningpath/application/GetLearningPathUseCase.java` -- @UseCase, returns path with all units
- `src/main/java/com/faus535/englishtrainer/learningpath/infrastructure/controller/GetLearningPathController.java` -- GET /api/profiles/{profileId}/learning-path

**Details**:

`GetLearningStatusUseCase.execute(UserProfileId userId)`:
Cross-module dependencies (acceptable at application layer):
- `LearningPathRepository` -> overall progress, current unit index
- `LearningUnitRepository` -> current unit details, weak areas (NEEDS_REVIEW units)
- `SpacedRepetitionRepository` -> reviewsDue count
- `ActivityRepository` -> streak calculation
- `ContentSelector` -> todaysPlan (estimate new + review items)

Returns 404 (via LearningPathNotFoundException) if no learning path exists. Frontend catches 404 and shows "Take your level test" CTA.

Batched unit loading to prevent N+1:
```java
// Load all units for the path in one query
List<LearningUnit> units = learningUnitRepository.findByLearningPathId(path.id());
// Use in-memory for weak areas, current unit, etc.
```

`GetLearningPathUseCase.execute(UserProfileId userId)`:
1. Load LearningPath by userId
2. Load all LearningUnits for the path in a single query (N+1 prevention)
3. Map to response with unit summaries

**Security**:
- `@RequireProfileOwnership` on both controllers

**Testing**:
- [ ] Unit test: todaysPlan.estimatedMinutes based on actual content count
- [ ] Unit test: weakAreas surfaces NEEDS_REVIEW units sorted by masteryScore ascending
- [ ] Unit test: 404 when no learning path exists
- [ ] GAP-6 test: N+1 prevention -- single query loads all units
- [ ] MockMvc test: both endpoints return correct shapes
- [ ] MockMvc test: 404 for missing learning path
- [ ] MockMvc test: ownership check enforced

**Acceptance criteria**:
- [ ] Single call returns complete learning status
- [ ] `todaysPlan.estimatedMinutes` based on actual content count (not hardcoded)
- [ ] `weakAreas` surfaces NEEDS_REVIEW units
- [ ] 404 when no learning path (frontend shows "Take test" CTA)
- [ ] Learning path endpoint returns all units in single response
- [ ] N+1 prevented: single query for all units
- [ ] `@RequireProfileOwnership` on both controllers

---

### Phase 10: SRS Enrichment + Graduation Listener

**Goal**: Enrich `ReviewCompletedEvent` with graduation data (2-line change). Create listeners that close the mastery lifecycle loop: Game -> LEARNING -> LEARNED -> SRS -> MASTERED.

**Files to modify**:
- `src/main/java/com/faus535/englishtrainer/spacedrepetition/domain/event/ReviewCompletedEvent.java` -- Add `graduated` (boolean), `itemType` (String), `unitReference` (String) fields
- `src/main/java/com/faus535/englishtrainer/spacedrepetition/domain/SpacedRepetitionItem.java` -- Pass graduated + itemType when registering event in `completeReview()` (2-line change: fields already exist on the entity)

**Files to create**:
- `src/main/java/com/faus535/englishtrainer/vocabulary/infrastructure/event/SrsGraduationListener.java` -- @EventListener for ReviewCompletedEvent
- `src/main/java/com/faus535/englishtrainer/vocabulary/infrastructure/event/WordLearnedListener.java` -- @EventListener for WordLearnedEvent -> auto-adds to SRS

**Details**:

Enriched `ReviewCompletedEvent`:
```java
public record ReviewCompletedEvent(
    SpacedRepetitionItemId itemId,
    UserProfileId userId,
    String itemType,       // "module-unit" or "vocabulary-word" (default: "module-unit" for backward compat)
    String unitReference,  // e.g. "vocab-apple" (default: null for backward compat)
    boolean graduated      // default: false for backward compat
) {
    // Backward-compatible constructor for existing callers
    public ReviewCompletedEvent(SpacedRepetitionItemId itemId, UserProfileId userId) {
        this(itemId, userId, "module-unit", null, false);
    }
}
```

`SrsGraduationListener`:
```java
@Component
class SrsGraduationListener {
    private final VocabMasteryRepository vocabMasteryRepository;

    @EventListener
    void on(ReviewCompletedEvent event) {
        if (!event.graduated() || !"vocabulary-word".equals(event.itemType())) return;
        // Parse word from unitReference: "vocab-{word}" -> word
        String word = event.unitReference().substring("vocab-".length());
        vocabMasteryRepository.findByUserIdAndWord(event.userId(), word)
            .ifPresent(mastery -> {
                mastery.graduate();  // -> MASTERED + publishes WordMasteredEvent
                vocabMasteryRepository.save(mastery);
            });
    }
}
```

`WordLearnedListener`:
```java
@Component
class WordLearnedListener {
    private final AddVocabularyToReviewUseCase addToReview;

    @EventListener
    void on(WordLearnedEvent event) throws Exception {
        addToReview.execute(event.userId(), event.word(), /* level from vocabEntry lookup */);
    }
}
```

This closes the lifecycle: Game -> VocabMastery(LEARNING) -> threshold -> VocabMastery(LEARNED) -> SRS schedule -> 5 reviews -> VocabMastery(MASTERED) -> excluded from games.

**Security**: No endpoints -- internal event listeners only.

**Testing**:
- [ ] Unit test: ReviewCompletedEvent backward-compatible constructor works
- [ ] Unit test: SrsGraduationListener only triggers for vocabulary-word + graduated
- [ ] Unit test: WordLearnedListener schedules SRS review
- [ ] GAP-2 test: SRS enrichment flow end-to-end (completeReview -> graduated event -> mastery update)
- [ ] GAP-7 test: domain event enrichment does not break existing module-unit reviews
- [ ] Integration test: full word lifecycle (game -> learned -> SRS x5 -> mastered)

**Acceptance criteria**:
- [ ] ReviewCompletedEvent enriched without breaking existing callers (backward-compatible constructor)
- [ ] SRS graduation triggers MASTERED on VocabMastery
- [ ] Only vocabulary-word items trigger graduation listener (not module-unit)
- [ ] WordLearnedEvent auto-schedules SRS review
- [ ] WordMasteredEvent published for gamification hooks
- [ ] Integration test: full word lifecycle passes

---

### Phase 11: ModuleProgress Soft Deprecation + Controller Security Retrofit

**Goal**: Mark ModuleProgress as deprecated, map existing data during path generation, and retrofit `@RequireProfileOwnership` on 15 existing controllers missing ownership checks.

**Files to modify**:
- `src/main/java/com/faus535/englishtrainer/moduleprogress/domain/ModuleProgress.java` -- Add @Deprecated annotation
- `src/main/java/com/faus535/englishtrainer/learningpath/application/GenerateLearningPathUseCase.java` -- During generation, check ModuleProgress for existing scores and pre-set unit mastery
- 15 existing controllers without ownership checks (see security audit below)

**Details**:

ModuleProgress soft deprecation:
- Add `@Deprecated(since = "v9", forRemoval = true)` on ModuleProgress
- During `GenerateLearningPathUseCase.execute()`: load ModuleProgress data if exists, map completed modules to MASTERED units
- LearningPath becomes the primary progression system; ModuleProgress kept as fallback

Security retrofit -- add `@RequireProfileOwnership` to these controllers (based on codebase audit showing `ProfileOwnershipChecker` not called):
1. All controllers in `minigame/infrastructure/controller/` (except SaveMiniGameScoreController, fixed in Phase 0)
2. All controllers in `spacedrepetition/infrastructure/controller/`
3. All controllers in `session/infrastructure/controller/`
4. All controllers in `moduleprogress/infrastructure/controller/`
5. All controllers in `gamification/infrastructure/controller/`
6. All controllers in `conversation/infrastructure/controller/`
7. All controllers in `dailychallenge/infrastructure/controller/`
8. All controllers in `notification/infrastructure/controller/`

**Testing**:
- [ ] Unit test: GenerateLearningPathUseCase maps ModuleProgress scores to unit mastery
- [ ] GAP-8 test: @RequireProfileOwnership AOP aspect works on retrofitted controllers
- [ ] Regression test: all existing controller tests still pass with annotation added

**Acceptance criteria**:
- [ ] ModuleProgress marked @Deprecated
- [ ] Existing ModuleProgress data mapped to LearningUnit scores during path generation
- [ ] No existing functionality broken
- [ ] All 15+ controllers retrofitted with @RequireProfileOwnership
- [ ] Each retrofitted controller returns 403 for non-owner access

---

## API Contract

All endpoints require Bearer JWT authentication. All endpoints with `{userId}` or `{profileId}` path parameters enforce ownership via `@RequireProfileOwnership`.

### `POST /api/profiles/{profileId}/learning-path/generate`
- **Request body**: none (uses user's current levels from profile)
- **Response body**:
```json
{
  "learningPathId": "uuid",
  "unitsGenerated": 10,
  "currentLevel": "A1"
}
```
- **Status codes**: 201 Created, 404 Profile not found, 403 Forbidden (not owner)
- **Auth**: Required (Bearer JWT + ownership check)

### `GET /api/profiles/{profileId}/learning-status`
- **Response body**:
```json
{
  "currentUnit": {
    "name": "Food & Drink",
    "unitIndex": 3,
    "masteryScore": 45,
    "status": "IN_PROGRESS",
    "contentProgress": { "practiced": 12, "total": 27 }
  },
  "nextUnit": { "name": "Daily Routines", "unitIndex": 4 },
  "overallProgress": { "unitsCompleted": 2, "totalUnits": 10, "percentComplete": 20 },
  "todaysPlan": {
    "newItemsCount": 15,
    "reviewItemsCount": 8,
    "estimatedMinutes": 18,
    "suggestedSessionMode": "full"
  },
  "reviewsDue": 8,
  "streak": 5,
  "weakAreas": [
    { "module": "pronunciation", "unitName": "Greetings", "masteryScore": 42 }
  ],
  "recentMilestones": [
    { "type": "UNIT_MASTERED", "description": "Unit 2: My House completed!", "date": "2026-03-25" }
  ]
}
```
- **Status codes**: 200 OK, 404 Learning path not found (user hasn't taken test), 403 Forbidden
- **Auth**: Required (Bearer JWT + ownership check)
- **UX note**: 404 = valid business case (no path yet), frontend shows "Take your level test" CTA

### `GET /api/profiles/{profileId}/learning-path`
- **Response body**:
```json
{
  "currentLevel": "A1",
  "currentUnitIndex": 3,
  "units": [
    { "unitIndex": 0, "name": "Me & My World", "status": "MASTERED", "masteryScore": 92 },
    { "unitIndex": 1, "name": "My House", "status": "MASTERED", "masteryScore": 78 },
    { "unitIndex": 2, "name": "Food & Drink", "status": "NEEDS_REVIEW", "masteryScore": 55 },
    { "unitIndex": 3, "name": "Daily Routines", "status": "IN_PROGRESS", "masteryScore": 30 },
    { "unitIndex": 4, "name": "Family & Friends", "status": "NOT_STARTED", "masteryScore": 0 }
  ]
}
```
- **Status codes**: 200 OK, 404 Not found, 403 Forbidden
- **Auth**: Required (Bearer JWT + ownership check)

### `POST /api/profiles/{profileId}/sessions/{sessionId}/exercises/{exerciseIndex}/result`
- **Request body**:
```json
{
  "correctCount": 8,
  "totalCount": 10,
  "averageResponseTimeMs": 3200,
  "exerciseType": "VOCAB_QUIZ"
}
```
- **Response body**:
```json
{
  "unitMasteryScore": 65,
  "unitStatus": "IN_PROGRESS",
  "xpEarned": 15
}
```
- **Status codes**: 200 OK, 404 Session/exercise not found, 403 Forbidden
- **Auth**: Required (Bearer JWT + ownership check)
- **Idempotency**: Duplicate submissions for same (sessionId, exerciseIndex) return existing result

### `POST /api/profiles/{userId}/minigames/results`
- **Request body**:
```json
{
  "gameType": "word-match",
  "score": 85,
  "answeredItems": [
    { "vocabEntryId": "uuid-1", "word": "apple", "level": "A1", "correct": true },
    { "vocabEntryId": "uuid-2", "word": "breakfast", "level": "A1", "correct": true },
    { "vocabEntryId": null, "word": null, "level": "A1", "correct": true }
  ]
}
```
- **Response body**:
```json
{
  "score": 85,
  "xpEarned": 20,
  "wordsLearned": ["apple", "breakfast"],
  "wordsAddedToReview": 2,
  "totalWordsEncountered": 3
}
```
- **Status codes**: 200 OK, 404 Profile not found, 400 Validation error, 403 Forbidden
- **Auth**: Required (Bearer JWT + ownership check)
- **Validation**: `@Max(10000)` on score, `@Size(max=200)` on answeredItems
- **Note**: `vocabEntryId` nullable for grammar games (fill-gap)

### `GET /api/profiles/{userId}/vocabulary/unlearned?level=A1&count=20`
- **Response body**:
```json
{
  "items": [
    { "id": "uuid", "english": "kitchen", "spanish": "cocina", "ipa": "/kitSIn/", "type": "noun", "level": "A1", "category": "home" }
  ],
  "remainingCount": 35
}
```
- **Status codes**: 200 OK, 403 Forbidden
- **Auth**: Required (Bearer JWT + ownership check)
- **Note**: Returns only words this user has NOT yet learned (excludes LEARNED/MASTERED)

### `GET /api/profiles/{userId}/vocabulary/progress?level=A1&page=0&size=50`
- **Response body**:
```json
{
  "stats": {
    "totalEncountered": 45,
    "totalLearned": 28,
    "totalMastered": 12,
    "averageAccuracy": 78.5
  },
  "learning": [
    { "word": "schedule", "vocabEntryId": "uuid", "correctCount": 1, "totalAttempts": 3, "accuracy": 33, "lastPracticedAt": "2026-03-26T10:00:00Z" }
  ],
  "learned": [
    { "word": "apple", "vocabEntryId": "uuid", "correctCount": 5, "totalAttempts": 6, "accuracy": 83, "learnedAt": "2026-03-25", "nextReviewDate": "2026-03-28" }
  ],
  "mastered": [
    { "word": "hello", "vocabEntryId": "uuid", "correctCount": 8, "totalAttempts": 9, "accuracy": 89, "learnedAt": "2026-03-10" }
  ]
}
```
- **Status codes**: 200 OK, 403 Forbidden
- **Auth**: Required (Bearer JWT + ownership check)

## Database Changes

| Migration | Tables | Key Changes |
|-----------|--------|-------------|
| V9.0.0 | learning_paths, learning_units | `version BIGINT` for optimistic locking, `VARCHAR(10)` for levels, `CURRENT_TIMESTAMP` defaults, contents as JSON TEXT, CHECK on mastery_score 0-100 |
| V9.1.0 | sessions (ALTER) | Add `exercises_data TEXT` column + `idx_sessions_user_created` index |
| V9.2.0 | vocab_mastery | `version BIGINT`, `updated_at`, `source_context`/`source_detail` (Value Object columns), CHECK on counts >= 0, nullable vocab_entry_id |

All migrations use `CURRENT_TIMESTAMP` (not `NOW()`) for consistency with existing migrations (V8.x series).

## Testing Strategy

### Unit Tests (Pure domain, no Spring context)
- MasteryCalculator: weighted calculation, exponential decay, threshold boundaries
- ContentSelector: 70/30 mix, ratio adjustment, recently-practiced exclusion
- LearningPathGenerator: vocab block mapping, category fallback, content binding validation
- LearningPath: create, reconstitute, advanceToNextUnit, isCompleted
- LearningUnit: all status transitions, markContentPracticed, mastery updates
- MasteryScore: range enforcement, isMastered(), needsReview()
- VocabMastery: all status transitions, regression, graduation, accuracy calculation

### Integration Tests (Testcontainers)
- Repository adapters for all new entities (learning_paths, learning_units, vocab_mastery)
- Learning path generation with real A1/A2 vocab block data
- Full word lifecycle: game -> VocabMastery(LEARNING) -> LEARNED -> SRS -> MASTERED
- GAP-5: migration safety -- V9.0.0, V9.1.0, V9.2.0 run on existing database with data
- GAP-6: N+1 query prevention -- verify single query loads all units for a path

### Use Case Tests (In-memory repositories, no Spring context)
- GenerateLearningPathUseCase: path creation, regeneration, ModuleProgress migration
- GetNextContentUseCase: content selection with SRS integration
- RecordExerciseResultUseCase: mastery update, unit advancement, SRS scheduling
- SaveGameResultsUseCase: per-word tracking, XP calculation, backward compatibility
- GetUnlearnedVocabUseCase: exclusion logic, remainingCount
- GetVocabProgressUseCase: grouping, stats, pagination

### Controller Tests (MockMvc)
- All 7 new endpoints + 1 fixed endpoint with auth + ownership checks
- 404 for missing learning path, 400 for validation errors, 403 for non-owner
- GAP-8: @RequireProfileOwnership AOP aspect enforced on all annotated controllers

### Security Tests
- Phase 0 fix: SaveMiniGameScoreController returns 403 for non-owner
- All new controllers: @RequireProfileOwnership enforced
- Phase 11 retrofit: 15 existing controllers return 403 for non-owner

### Gap Tests (from cross-cutting analysis)
- GAP-1: Content binding verification -- LearningPathGenerator rejects unknown content IDs
- GAP-2: SRS enrichment flow -- ReviewCompletedEvent with graduated flag triggers mastery update
- GAP-3: Level completion workflow -- all units mastered -> LevelCompletedEvent
- GAP-4: Concurrent unit mastery -- optimistic locking prevents lost updates
- GAP-5: Migration safety -- all 3 migrations on existing data
- GAP-6: N+1 prevention -- batched unit loading
- GAP-7: Domain event enrichment backward compatibility
- GAP-8: Ownership AOP aspect test

## Risk Register

| # | Risk | Severity | Mitigation | Phase |
|---|------|----------|------------|-------|
| R1 | 15 existing controllers without ownership checks | CRITICAL | Phase 0 creates AOP aspect, Phase 11 retrofits all controllers | 0, 11 |
| R2 | Session ID mismatch (frontend string vs backend UUID) | HIGH | Phase 4 moves to backend-generated UUID. Frontend must consume backend ID. | 4 |
| R3 | ModuleProgress deprecation creates dual progression system | MEDIUM | Soft deprecation: LearningPath primary, ModuleProgress fallback. Runtime migration during path generation. | 11 |
| R4 | findRandom() performance (loads ALL entries then shuffles) | MEDIUM | Phase 3 replaces with ContentSelector. findRandom() kept as fallback only for users without learning path. | 3 |
| R5 | CurriculumProvider orphaned | LOW | Phase 2 wires it into GenerateLearningPathUseCase | 2 |
| R6 | ReviewCompletedEvent too lean | MEDIUM | Phase 10 enriches with 2-line change. Backward-compatible constructor. | 10 |
| R7 | Frontend games 100% client-side | MEDIUM | Phase 7 provides per-word result endpoint. Frontend Phase 3 wires to backend data. | 7 |
| R8 | N+1 query risk on learning path + units | MEDIUM | Phase 9: batch load all units in single query. Index on learning_path_id. | 9 |
| R9 | Path generation timeout > 3s | LOW | LearningPathGenerator is pure in-memory computation on pre-loaded data. Timeout budget monitored. | 2 |
