# Backend Plan: Pronunciation Lab

> Generated: 2026-04-13
> Request: Pronunciation Lab — (A) text analysis with IPA/tips/minimal pairs; (B) speech feedback comparing target vs recognized; (C) phrase drills with scoring; (D) mini-conversation with per-utterance pronunciation score

## Decisions Log

| # | Topic | Decision | Alternatives Considered | Why |
|---|-------|----------|------------------------|-----|
| 1 | Module location | New `pronunciation` module (not extending `talk`) | Extend talk module | Pronunciation is a distinct bounded context: stateless analysis + catalog-based drills + conversation sessions. Talk couples conversation to scenarios; pronunciation is input-focused. Separate module avoids coupling. |
| 2 | Phases 1 & 2 — no persistence | Stateless endpoints (no DB tables) | Persist analysis history | Analysis and feedback are compute-on-demand operations. Caching adds complexity without clear user value at this stage. |
| 3 | Single `PronunciationAiPort` | One port interface covers all 5 AI operations | One port per use case | All operations share the same Anthropic adapter; a single port keeps the module cohesive and the adapter testable via one stub. |
| 4 | Flyway versions | V14.0.0 (drills), V14.1.0 (mini-conversations) | V13.3.0 | 14.x reserves a new major for the pronunciation module; avoids collision risk with in-flight article/talk migrations at 13.x. |
| 5 | Drill catalog seeded in migration | Seed phrases via INSERT in V14.0.0 | Admin endpoint to create drills | Drills are curated content, not user-generated. Seeding ensures the feature works immediately after deploy. |
| 6 | MAX_TURNS = 5 | 5 turns per mini-conversation | 3 or 10 | Enough repetition to get useful feedback without fatiguing the user in a short session. |
| 7 | PronunciationControllerAdvice scope | `@RestControllerAdvice(basePackages = "com.faus535.englishtrainer.pronunciation")` | Global ControllerAdvice | Matches talk module pattern: each module owns its exception handling. |

## Context

- **Reference module**: `talk` — AI-powered, event-driven, JPA persistence, same structural patterns
- **Base package**: `com.faus535.englishtrainer`
- **Latest Flyway migration**: `V13.2.0__article_marked_words_add_enrichment_fields.sql`
- **No existing `pronunciation` module** — created from scratch

## Analysis

### Existing patterns confirmed (from `talk` module)

- Aggregate: `final class extends AggregateRoot<ID>`, all fields `final`, factory methods `start()` / `reconstitute()`
- Value Object IDs: `record` with `UUID value()`, compact constructor null-check, static `generate()`
- Domain port: `public interface {Module}AiPort` in `domain/` with nested result records
- Exceptions: Abstract `{Module}Exception extends Exception`, concrete finals extend it, in `domain/error/`
- Domain events: `@DomainEvent record` in `domain/event/`, past-tense naming
- Use Cases: `@UseCase` (= `@Service`), constructor injection, `@Transactional`, single `execute()`
- JPA entity: `@Entity @Table`, `Persistable<UUID>`, `@Version`, `@Transient boolean isNew`, `fromAggregate()` + `toAggregate()` + `updateFrom()`
- Controller: package-private, `@RestController`, single `handle()`, inner request record with `@Valid`
- Event listeners: `@TransactionalEventListener` + `@Transactional(propagation = REQUIRES_NEW)` in `activity/infrastructure/event/` and `gamification/infrastructure/event/`
- Test doubles: `InMemory{X}Repository`, `Stub{X}AiPort`, Object Mothers with `create()` / `withXxx()`
- Test naming: `should{Result}When{Condition}()`

**Existing tests impacted**: None. No pronunciation module exists; event listeners will be new files in `activity` and `gamification` modules.

## Public Contracts

### Use Cases

```java
// Phase 1
AnalyzePronunciationUseCase.execute(String text, String level): PronunciationAnalysisDto
    throws PronunciationAiException

// Phase 2
EvaluatePronunciationFeedbackUseCase.execute(String targetText, String recognizedText,
    List<WordConfidenceDto> wordConfidences): PronunciationFeedbackDto
    throws PronunciationAiException

// Phase 3
ListDrillsUseCase.execute(String level, String focus): List<PronunciationDrillDto>
SubmitDrillAttemptUseCase.execute(UUID drillId, UUID userId, String recognizedText,
    double confidence): DrillAttemptResultDto
    throws PronunciationDrillNotFoundException, PronunciationAiException

// Phase 4
StartMiniConversationUseCase.execute(UUID userId, String focus, String level): PronunciationMiniConversationDto
    throws PronunciationAiException
EvaluateMiniConversationTurnUseCase.execute(UUID conversationId, String recognizedText,
    List<WordConfidenceDto> wordConfidences): MiniConversationTurnResultDto
    throws PronunciationMiniConversationNotFoundException, PronunciationAiException
```

### Domain Events

```java
@DomainEvent
record PronunciationDrillCompletedEvent(
    PronunciationDrillId drillId, UUID userId, int score, int perfectStreak)

@DomainEvent
record PronunciationMiniConversationCompletedEvent(
    PronunciationMiniConversationId conversationId, UUID userId, int finalScore)
```

### Domain Exceptions

```java
// Base
abstract PronunciationException extends Exception

// Concrete
final PronunciationAiException extends PronunciationException    // AI call failed
final PronunciationDrillNotFoundException extends PronunciationException  // Phase 3
final PronunciationMiniConversationNotFoundException extends PronunciationException  // Phase 4
```

### Database Schema

| Table | Migration | Purpose |
|-------|-----------|---------|
| `pronunciation_drills` | V14.0.0 | Drill catalog (seeded) |
| `pronunciation_drill_attempts` | V14.0.0 | Per-user attempt tracking (streak) |
| `pronunciation_mini_conversations` | V14.1.0 | Mini-conversation sessions |
| `pronunciation_mini_conversation_turns` | V14.1.0 | Per-turn evaluations |

### REST Endpoints

| Method | Path | Auth | Phase |
|--------|------|------|-------|
| POST | `/api/pronunciation/analyze` | JWT | 1 |
| POST | `/api/pronunciation/feedback` | JWT | 2 |
| GET | `/api/pronunciation/drills` | JWT | 3 |
| POST | `/api/pronunciation/drills/{id}/submit` | JWT | 3 |
| POST | `/api/pronunciation/mini-conversation` | JWT | 4 |
| POST | `/api/pronunciation/mini-conversation/{id}/evaluate` | JWT | 4 |

---

## Phase 1 [M] — PronunciationAnalysis: domain port + AI adapter + analyze endpoint

**Goal**: User submits a word/phrase + CEFR level and receives IPA, syllables, stress pattern, tips for Spanish speakers, common mistakes, minimal pairs, and example sentences — all AI-generated. No database needed.

**Files to create**:

- `src/main/java/com/faus535/englishtrainer/pronunciation/domain/PronunciationAiPort.java`
- `src/main/java/com/faus535/englishtrainer/pronunciation/domain/error/PronunciationException.java`
- `src/main/java/com/faus535/englishtrainer/pronunciation/domain/error/PronunciationAiException.java`
- `src/main/java/com/faus535/englishtrainer/pronunciation/application/AnalyzePronunciationUseCase.java`
- `src/main/java/com/faus535/englishtrainer/pronunciation/application/PronunciationAnalysisDto.java`
- `src/main/java/com/faus535/englishtrainer/pronunciation/infrastructure/ai/AnthropicPronunciationAiAdapter.java`
- `src/main/java/com/faus535/englishtrainer/pronunciation/infrastructure/ai/PronunciationPromptBuilder.java`
- `src/main/java/com/faus535/englishtrainer/pronunciation/infrastructure/controller/AnalyzePronunciationController.java`
- `src/main/java/com/faus535/englishtrainer/pronunciation/infrastructure/controller/PronunciationControllerAdvice.java`
- `src/test/java/com/faus535/englishtrainer/pronunciation/infrastructure/StubPronunciationAiPort.java`
- `src/test/java/com/faus535/englishtrainer/pronunciation/application/AnalyzePronunciationUseCaseTest.java`

**Details**:

`PronunciationAiPort.java` — single interface, all 5 operations defined upfront (other methods stubbed until their phase):
```java
public interface PronunciationAiPort {
    PronunciationAnalysisResult analyze(String text, String level) throws PronunciationAiException;
    PronunciationFeedbackResult feedback(String targetText, String recognizedText,
        List<WordConfidence> wordConfidences) throws PronunciationAiException;
    DrillFeedbackResult drillFeedback(String drillPhrase, String recognizedText,
        double confidence) throws PronunciationAiException;
    MiniConversationResult startMiniConversation(String focus, String level) throws PronunciationAiException;
    MiniConversationTurnResult evaluateMiniConversationTurn(String targetPhrase, String recognizedText,
        List<WordConfidence> wordConfidences, String focus, String level) throws PronunciationAiException;

    record PronunciationAnalysisResult(String ipa, String syllables, String stressPattern,
        List<String> tips, List<String> commonMistakes, List<String> minimalPairs, List<String> exampleSentences) {}
    record WordConfidence(String word, double confidence) {}
    record WordFeedback(String word, String recognized, String tip, int score) {}
    record PronunciationFeedbackResult(int score, List<WordFeedback> wordFeedback, String overallTip) {}
    record DrillFeedbackResult(int score, String feedback) {}
    record MiniConversationResult(String prompt, String targetPhrase) {}
    record MiniConversationTurnResult(int score, List<WordFeedback> wordFeedback,
        String nextPrompt, String nextTargetPhrase, boolean isComplete) {}
}
```

`PronunciationAnalysisDto.java`:
```java
public record PronunciationAnalysisDto(String text, String ipa, String syllables,
    String stressPattern, List<String> tips, List<String> commonMistakes,
    List<String> minimalPairs, List<String> exampleSentences) {}
```

`AnalyzePronunciationUseCase.java`:
```java
@UseCase
public class AnalyzePronunciationUseCase {
    private final PronunciationAiPort pronunciationAiPort;
    public AnalyzePronunciationUseCase(PronunciationAiPort pronunciationAiPort) {
        this.pronunciationAiPort = pronunciationAiPort;
    }
    public PronunciationAnalysisDto execute(String text, String level) throws PronunciationAiException {
        var result = pronunciationAiPort.analyze(text, level);
        return new PronunciationAnalysisDto(text, result.ipa(), result.syllables(),
            result.stressPattern(), result.tips(), result.commonMistakes(),
            result.minimalPairs(), result.exampleSentences());
    }
}
```

`AnthropicPronunciationAiAdapter.java` — implements `PronunciationAiPort`:
- Injects `@Qualifier("anthropicRestClient") RestClient restClient` and `@Value("${anthropic.model}") String model`
- `analyze()`: uses Claude tool-use with `TOOL_ANALYZE_PRONUNCIATION` schema (fields: `ipa`, `syllables`, `stressPattern`, `tips[]`, `commonMistakes[]`, `minimalPairs[]`, `exampleSentences[]`). System prompt from `PronunciationPromptBuilder.buildAnalyzeSystemPrompt(level)` focusing on Spanish speaker interference patterns. Parses `tool_use` block from response content.
- Other 4 methods: throw `PronunciationAiException("Not implemented in this phase")` — filled in subsequent phases.

`PronunciationPromptBuilder.java` — static helper with `buildAnalyzeSystemPrompt(String level)`.

`AnalyzePronunciationController.java` (package-private):
```java
@RestController
class AnalyzePronunciationController {
    record AnalyzeRequest(@NotBlank String text,
        @NotBlank @Pattern(regexp = "(?i)(a1|a2|b1|b2|c1|c2)") String level) {}
    record AnalyzeResponse(String text, String ipa, String syllables, String stressPattern,
        List<String> tips, List<String> commonMistakes, List<String> minimalPairs,
        List<String> exampleSentences) {
        static AnalyzeResponse fromDto(PronunciationAnalysisDto dto) { ... }
    }
    @PostMapping("/api/pronunciation/analyze")
    ResponseEntity<AnalyzeResponse> handle(@Valid @RequestBody AnalyzeRequest request)
        throws PronunciationAiException { ... }
}
```

`PronunciationControllerAdvice.java`:
```java
@RestControllerAdvice(basePackages = "com.faus535.englishtrainer.pronunciation")
class PronunciationControllerAdvice {
    record ApiError(String code, String message) {}
    @ExceptionHandler(PronunciationAiException.class)
    ResponseEntity<ApiError> handleAiError(PronunciationAiException ex) {
        // return 503 SERVICE_UNAVAILABLE, code "ai_unavailable"
    }
}
```

`StubPronunciationAiPort.java` — implements all 5 methods returning configurable fixed values via setter methods: `setAnalysisToReturn()`, `setFeedbackToReturn()`, `setDrillFeedbackToReturn()`, `setMiniConversationToReturn()`, `setMiniConversationTurnToReturn()`.

`AnalyzePronunciationUseCaseTest.java`:
```java
@Test void shouldReturnAnalysisWhenTextIsValid() throws Exception { ... }
@Test void shouldPropagateAiExceptionWhenAiFails() { ... }
@Test void shouldPreserveInputTextInResult() throws Exception { ... }
```

**Acceptance criteria**:
- [x] `./gradlew compileJava` passes
- [x] `AnalyzePronunciationUseCaseTest` passes
- [ ] `POST /api/pronunciation/analyze` with `{"text":"thought","level":"b1"}` returns 200 with `ipa`, `tips`, `minimalPairs` fields
- [ ] `POST /api/pronunciation/analyze` without JWT returns 401
- [ ] AI exception returns 503
- [ ] Human review: package structure mirrors `talk`, no public constructors, no field injection
- [ ] Commit: `feat(pronunciation): Phase 1 — analyze endpoint with AI-powered IPA and tips`

---

## Phase 2 [S] — PronunciationFeedback: speech comparison endpoint

**Goal**: Stateless endpoint receives target text, recognized text, and word confidences; returns overall score, per-word feedback, and tip.

**Files to create**:

- `src/main/java/com/faus535/englishtrainer/pronunciation/application/EvaluatePronunciationFeedbackUseCase.java`
- `src/main/java/com/faus535/englishtrainer/pronunciation/application/PronunciationFeedbackDto.java`
- `src/main/java/com/faus535/englishtrainer/pronunciation/application/WordFeedbackDto.java`
- `src/main/java/com/faus535/englishtrainer/pronunciation/application/WordConfidenceDto.java`
- `src/main/java/com/faus535/englishtrainer/pronunciation/infrastructure/controller/EvaluatePronunciationFeedbackController.java`
- `src/main/java/com/faus535/englishtrainer/pronunciation/infrastructure/ai/AnthropicPronunciationAiAdapter.java` — implement `feedback()` method

**Files to modify**:
- `src/test/java/com/faus535/englishtrainer/pronunciation/application/EvaluatePronunciationFeedbackUseCaseTest.java` — new test class

**Details**:

```java
public record WordConfidenceDto(String word, double confidence) {}
public record WordFeedbackDto(String word, String recognized, String tip, int score) {}
public record PronunciationFeedbackDto(int score, List<WordFeedbackDto> wordFeedback, String overallTip) {}
```

`EvaluatePronunciationFeedbackUseCase.execute()` maps `WordConfidenceDto` → `PronunciationAiPort.WordConfidence`, calls `pronunciationAiPort.feedback()`, maps result back to `PronunciationFeedbackDto`.

`AnthropicPronunciationAiAdapter.feedback()` — tool schema: `overallScore` (int 0–100), `wordFeedback` (array: `word`, `recognized`, `tip`, `score`), `overallTip` (string). Serializes target text, recognized text, and confidences into user message.

`EvaluatePronunciationFeedbackController.java` (package-private):
```java
record WordConfidenceRequest(String word, double confidence) {}
record FeedbackRequest(@NotBlank String targetText, @NotBlank String recognizedText,
    @NotNull List<WordConfidenceRequest> wordConfidences) {}
@PostMapping("/api/pronunciation/feedback")
ResponseEntity<FeedbackResponse> handle(@Valid @RequestBody FeedbackRequest request)
    throws PronunciationAiException { ... }
```

`EvaluatePronunciationFeedbackUseCaseTest.java`:
```java
@Test void shouldReturnScoreAndWordFeedbackWhenRecognizedTextDiffersFromTarget() throws Exception { ... }
@Test void shouldReturnHighScoreWhenRecognizedMatchesTarget() throws Exception { ... }
@Test void shouldPropagateAiExceptionWhenAiFails() { ... }
```

**Acceptance criteria**:
- [x] `./gradlew compileJava` passes
- [x] `EvaluatePronunciationFeedbackUseCaseTest` passes
- [ ] `POST /api/pronunciation/feedback` with valid body returns 200 with `score`, `wordFeedback`, `overallTip`
- [ ] `POST /api/pronunciation/feedback` without JWT returns 401
- [ ] Human review: no persistence, maps cleanly through layers
- [ ] Commit: `feat(pronunciation): Phase 2 — speech feedback endpoint`

---

## Phase 3 [M] — PronunciationDrill: catalog + scoring + events

**Goal**: Introduce `PronunciationDrill` aggregate with a seeded catalog. Users can list drills by level/focus and submit attempts to receive AI-generated feedback + score. Fires `PronunciationDrillCompletedEvent` on completion.

**Files to create**:

**Domain**:
- `src/main/java/com/faus535/englishtrainer/pronunciation/domain/PronunciationDrill.java`
- `src/main/java/com/faus535/englishtrainer/pronunciation/domain/PronunciationDrillId.java`
- `src/main/java/com/faus535/englishtrainer/pronunciation/domain/DrillAttempt.java`
- `src/main/java/com/faus535/englishtrainer/pronunciation/domain/DrillDifficulty.java`
- `src/main/java/com/faus535/englishtrainer/pronunciation/domain/PronunciationDrillRepository.java`
- `src/main/java/com/faus535/englishtrainer/pronunciation/domain/error/PronunciationDrillNotFoundException.java`
- `src/main/java/com/faus535/englishtrainer/pronunciation/domain/event/PronunciationDrillCompletedEvent.java`

**Application**:
- `src/main/java/com/faus535/englishtrainer/pronunciation/application/ListDrillsUseCase.java`
- `src/main/java/com/faus535/englishtrainer/pronunciation/application/SubmitDrillAttemptUseCase.java`
- `src/main/java/com/faus535/englishtrainer/pronunciation/application/PronunciationDrillDto.java`
- `src/main/java/com/faus535/englishtrainer/pronunciation/application/DrillAttemptResultDto.java`

**Infrastructure — persistence**:
- `src/main/java/com/faus535/englishtrainer/pronunciation/infrastructure/persistence/PronunciationDrillEntity.java`
- `src/main/java/com/faus535/englishtrainer/pronunciation/infrastructure/persistence/DrillAttemptEntity.java`
- `src/main/java/com/faus535/englishtrainer/pronunciation/infrastructure/persistence/JpaPronunciationDrillRepository.java`
- `src/main/java/com/faus535/englishtrainer/pronunciation/infrastructure/persistence/JpaPronunciationDrillRepositoryAdapter.java`

**Infrastructure — AI adapter** (update):
- `src/main/java/com/faus535/englishtrainer/pronunciation/infrastructure/ai/AnthropicPronunciationAiAdapter.java` — implement `drillFeedback()` method

**Infrastructure — controllers**:
- `src/main/java/com/faus535/englishtrainer/pronunciation/infrastructure/controller/ListDrillsController.java`
- `src/main/java/com/faus535/englishtrainer/pronunciation/infrastructure/controller/SubmitDrillAttemptController.java`

**Infrastructure — listeners**:
- `src/main/java/com/faus535/englishtrainer/activity/infrastructure/event/PronunciationDrillCompletedActivityListener.java`
- `src/main/java/com/faus535/englishtrainer/gamification/infrastructure/event/PronunciationDrillCompletedGamificationListener.java`

**Database**:
- `src/main/resources/db/migration/V14.0.0__pronunciation_create_drills.sql`

**Tests**:
- `src/test/java/com/faus535/englishtrainer/pronunciation/domain/PronunciationDrillMother.java`
- `src/test/java/com/faus535/englishtrainer/pronunciation/infrastructure/InMemoryPronunciationDrillRepository.java`
- `src/test/java/com/faus535/englishtrainer/pronunciation/application/ListDrillsUseCaseTest.java`
- `src/test/java/com/faus535/englishtrainer/pronunciation/application/SubmitDrillAttemptUseCaseTest.java`

**Details**:

`PronunciationDrill` aggregate — immutable, fields: `id (PronunciationDrillId)`, `phrase (String)`, `focus (String)`, `difficulty (DrillDifficulty)`, `cefrLevel (String)`, `attempts (List<DrillAttempt>)`. Methods: `reconstitute(...)`, `addAttempt(UUID userId, int score, String recognizedText)` returns new instance. `perfectStreakFor(UUID userId)` computes consecutive attempts with `score >= 90`.

`DrillAttempt` — record: `id (UUID)`, `userId (UUID)`, `score (int)`, `recognizedText (String)`, `attemptedAt (Instant)`.

`SubmitDrillAttemptUseCase.execute()`:
1. Load drill from repo (throw `PronunciationDrillNotFoundException` if not found)
2. Call `pronunciationAiPort.drillFeedback(drill.phrase(), recognizedText, confidence)`
3. `drill.addAttempt(userId, result.score(), recognizedText)` → new drill instance
4. Compute `perfectStreak`
5. Save updated drill
6. Publish `PronunciationDrillCompletedEvent` when `score >= 70`
7. Return `DrillAttemptResultDto(score, feedback, perfectStreak)`

`V14.0.0__pronunciation_create_drills.sql`:
```sql
CREATE TABLE IF NOT EXISTS pronunciation_drills (
    id UUID PRIMARY KEY, version BIGINT NOT NULL DEFAULT 0,
    phrase TEXT NOT NULL, focus VARCHAR(100) NOT NULL,
    difficulty VARCHAR(10) NOT NULL, cefr_level VARCHAR(5) NOT NULL
);
CREATE TABLE IF NOT EXISTS pronunciation_drill_attempts (
    id UUID PRIMARY KEY, drill_id UUID NOT NULL REFERENCES pronunciation_drills(id),
    user_id UUID NOT NULL, score INTEGER NOT NULL,
    recognized_text TEXT, attempted_at TIMESTAMP WITH TIME ZONE NOT NULL
);
CREATE INDEX IF NOT EXISTS idx_drill_attempts_drill_user ON pronunciation_drill_attempts(drill_id, user_id);
-- Seed initial drills
INSERT INTO pronunciation_drills (id, version, phrase, focus, difficulty, cefr_level) VALUES
  ('a0000000-0000-0000-0000-000000000001', 0, 'I thought about the theory', 'th-sound', 'MEDIUM', 'b1'),
  ('a0000000-0000-0000-0000-000000000002', 0, 'Three thin threads', 'th-sound', 'HARD', 'b2'),
  ('a0000000-0000-0000-0000-000000000003', 0, 'She sells seashells', 'sh-vs-s', 'MEDIUM', 'a2'),
  ('a0000000-0000-0000-0000-000000000004', 0, 'The weather is rather cold today', 'th-sound', 'EASY', 'b1'),
  ('a0000000-0000-0000-0000-000000000005', 0, 'Thirty-three thousand', 'th-sound', 'HARD', 'b2')
ON CONFLICT DO NOTHING;
```

`PronunciationDrillMother.java`:
```java
public final class PronunciationDrillMother {
    public static PronunciationDrill withThFocus() { ... }           // b1, th-sound, MEDIUM, no attempts
    public static PronunciationDrill withAttempts(int count) { ... }
    public static PronunciationDrill withUserPerfectStreak(UUID userId, int streak) { ... }
}
```

`ListDrillsUseCaseTest.java`:
```java
@Test void shouldReturnDrillsFilteredByLevel() { ... }
@Test void shouldReturnDrillsFilteredByLevelAndFocus() { ... }
@Test void shouldReturnEmptyListWhenNoDrillsMatch() { ... }
```

`SubmitDrillAttemptUseCaseTest.java`:
```java
@Test void shouldReturnScoreAndFeedbackWhenDrillExists() throws Exception { ... }
@Test void shouldThrowNotFoundWhenDrillDoesNotExist() { ... }
@Test void shouldFireDrillCompletedEventWhenScoreAboveThreshold() throws Exception { ... }
@Test void shouldNotFireEventWhenScoreBelowThreshold() throws Exception { ... }
@Test void shouldCalculatePerfectStreakCorrectly() throws Exception { ... }
```

`PronunciationControllerAdvice` gets `@ExceptionHandler(PronunciationDrillNotFoundException.class)` returning 404.

**Acceptance criteria**:
- [x] `./gradlew compileJava` passes
- [x] `ListDrillsUseCaseTest` and `SubmitDrillAttemptUseCaseTest` pass
- [ ] `GET /api/pronunciation/drills?level=b1` returns seeded drills
- [ ] `GET /api/pronunciation/drills?level=b1&focus=th-sound` returns filtered drills
- [ ] `POST /api/pronunciation/drills/{id}/submit` returns `score`, `feedback`, `perfectStreak`
- [ ] `POST /api/pronunciation/drills/{nonExistentId}/submit` returns 404
- [ ] `V14.0.0` migration runs cleanly, seeded rows present
- [ ] Human review: aggregate is immutable, event fired only when score >= 70, listeners use REQUIRES_NEW
- [ ] Commit: `feat(pronunciation): Phase 3 — drill catalog with scoring and events`

---

## Phase 4 [M] — PronunciationMiniConversation: multi-turn pronunciation coaching

**Goal**: Multi-turn conversational pronunciation practice. Each turn evaluates the user's spoken utterance vs a target phrase. Completes after MAX_TURNS = 5. Fires `PronunciationMiniConversationCompletedEvent`.

**Files to create**:

**Domain**:
- `src/main/java/com/faus535/englishtrainer/pronunciation/domain/PronunciationMiniConversation.java`
- `src/main/java/com/faus535/englishtrainer/pronunciation/domain/PronunciationMiniConversationId.java`
- `src/main/java/com/faus535/englishtrainer/pronunciation/domain/MiniConversationTurn.java`
- `src/main/java/com/faus535/englishtrainer/pronunciation/domain/MiniConversationStatus.java`
- `src/main/java/com/faus535/englishtrainer/pronunciation/domain/PronunciationMiniConversationRepository.java`
- `src/main/java/com/faus535/englishtrainer/pronunciation/domain/error/PronunciationMiniConversationNotFoundException.java`
- `src/main/java/com/faus535/englishtrainer/pronunciation/domain/event/PronunciationMiniConversationCompletedEvent.java`

**Application**:
- `src/main/java/com/faus535/englishtrainer/pronunciation/application/StartMiniConversationUseCase.java`
- `src/main/java/com/faus535/englishtrainer/pronunciation/application/EvaluateMiniConversationTurnUseCase.java`
- `src/main/java/com/faus535/englishtrainer/pronunciation/application/PronunciationMiniConversationDto.java`
- `src/main/java/com/faus535/englishtrainer/pronunciation/application/MiniConversationTurnResultDto.java`

**Infrastructure — persistence**:
- `src/main/java/com/faus535/englishtrainer/pronunciation/infrastructure/persistence/PronunciationMiniConversationEntity.java`
- `src/main/java/com/faus535/englishtrainer/pronunciation/infrastructure/persistence/MiniConversationTurnEntity.java`
- `src/main/java/com/faus535/englishtrainer/pronunciation/infrastructure/persistence/JpaPronunciationMiniConversationRepository.java`
- `src/main/java/com/faus535/englishtrainer/pronunciation/infrastructure/persistence/JpaPronunciationMiniConversationRepositoryAdapter.java`

**Infrastructure — AI adapter** (update):
- `AnthropicPronunciationAiAdapter.java` — implement `startMiniConversation()` and `evaluateMiniConversationTurn()`

**Infrastructure — controllers**:
- `src/main/java/com/faus535/englishtrainer/pronunciation/infrastructure/controller/StartMiniConversationController.java`
- `src/main/java/com/faus535/englishtrainer/pronunciation/infrastructure/controller/EvaluateMiniConversationTurnController.java`

**Infrastructure — listeners**:
- `src/main/java/com/faus535/englishtrainer/activity/infrastructure/event/PronunciationMiniConversationCompletedActivityListener.java`
- `src/main/java/com/faus535/englishtrainer/gamification/infrastructure/event/PronunciationMiniConversationCompletedGamificationListener.java`

**Database**:
- `src/main/resources/db/migration/V14.1.0__pronunciation_create_mini_conversations.sql`

**Tests**:
- `src/test/java/com/faus535/englishtrainer/pronunciation/domain/PronunciationMiniConversationMother.java`
- `src/test/java/com/faus535/englishtrainer/pronunciation/infrastructure/InMemoryPronunciationMiniConversationRepository.java`
- `src/test/java/com/faus535/englishtrainer/pronunciation/application/StartMiniConversationUseCaseTest.java`
- `src/test/java/com/faus535/englishtrainer/pronunciation/application/EvaluateMiniConversationTurnUseCaseTest.java`

**Details**:

`PronunciationMiniConversation` aggregate (immutable, MAX_TURNS = 5):
- Fields: `id`, `userId`, `focus`, `level`, `status (MiniConversationStatus)`, `currentPrompt`, `currentTargetPhrase`, `turns (List<MiniConversationTurn>)`, `startedAt`, `completedAt`
- `start(userId, focus, level, initialPrompt, initialTargetPhrase)` factory
- `evaluateTurn(recognizedText, score, nextPrompt, nextTargetPhrase)` → returns new instance; registers `PronunciationMiniConversationCompletedEvent` when `turns.size() >= MAX_TURNS`
- `isComplete()` returns `status == COMPLETED`
- `averageScore()` computed from all turns

`MiniConversationTurn` record: `id (UUID)`, `turnNumber (int)`, `targetPhrase`, `recognizedText`, `score`, `evaluatedAt (Instant)`.

`StartMiniConversationUseCase.execute(userId, focus, level)`:
1. `pronunciationAiPort.startMiniConversation(focus, level)` → `MiniConversationResult`
2. `PronunciationMiniConversation.start(...)` with AI result
3. `repository.save()`
4. Return `PronunciationMiniConversationDto(id, prompt, targetPhrase)`

`EvaluateMiniConversationTurnUseCase.execute(conversationId, recognizedText, wordConfidences)`:
1. Load from repo (throw `PronunciationMiniConversationNotFoundException` if not found)
2. `pronunciationAiPort.evaluateMiniConversationTurn(targetPhrase, recognizedText, confidences, focus, level)`
3. `conversation.evaluateTurn(...)` → updated conversation
4. `repository.save(updated)`
5. `saved.pullDomainEvents().forEach(eventPublisher::publishEvent)`
6. Return `MiniConversationTurnResultDto(score, wordFeedback, nextPrompt, nextTargetPhrase, isComplete)`

`V14.1.0__pronunciation_create_mini_conversations.sql`:
```sql
CREATE TABLE IF NOT EXISTS pronunciation_mini_conversations (
    id UUID PRIMARY KEY, version BIGINT NOT NULL DEFAULT 0,
    user_id UUID NOT NULL, focus VARCHAR(100) NOT NULL, level VARCHAR(5) NOT NULL,
    status VARCHAR(20) NOT NULL, current_prompt TEXT, current_target_phrase TEXT,
    started_at TIMESTAMP WITH TIME ZONE NOT NULL, completed_at TIMESTAMP WITH TIME ZONE
);
CREATE TABLE IF NOT EXISTS pronunciation_mini_conversation_turns (
    id UUID PRIMARY KEY, conversation_id UUID NOT NULL REFERENCES pronunciation_mini_conversations(id),
    turn_number INTEGER NOT NULL, target_phrase TEXT NOT NULL,
    recognized_text TEXT, score INTEGER NOT NULL,
    evaluated_at TIMESTAMP WITH TIME ZONE NOT NULL
);
CREATE INDEX IF NOT EXISTS idx_mini_conv_turns_conversation ON pronunciation_mini_conversation_turns(conversation_id);
```

`PronunciationMiniConversationMother.java`:
```java
public static PronunciationMiniConversation active() { ... }         // 0 turns
public static PronunciationMiniConversation withTurns(int count) { ... }
public static PronunciationMiniConversation nearCompletion() { ... } // MAX_TURNS - 1 turns
public static PronunciationMiniConversation completed() { ... }
```

`StartMiniConversationUseCaseTest.java`:
```java
@Test void shouldPersistConversationWithInitialPromptWhenFocusAndLevelAreValid() throws Exception { ... }
@Test void shouldReturnDtoWithIdAndPromptAndTargetPhrase() throws Exception { ... }
@Test void shouldPropagateAiExceptionWhenAiFails() { ... }
```

`EvaluateMiniConversationTurnUseCaseTest.java`:
```java
@Test void shouldReturnScoreAndNextPromptWhenConversationIsActive() throws Exception { ... }
@Test void shouldReturnIsCompleteTrueWhenMaxTurnsReached() throws Exception { ... }
@Test void shouldFireCompletedEventWhenConversationCompletes() throws Exception { ... }
@Test void shouldNotFireEventOnIntermediateTurn() throws Exception { ... }
@Test void shouldThrowNotFoundWhenConversationDoesNotExist() { ... }
@Test void shouldPropagateAiExceptionWhenAiFails() throws Exception { ... }
```

`PronunciationControllerAdvice` gets `@ExceptionHandler(PronunciationMiniConversationNotFoundException.class)` returning 404.

**Acceptance criteria**:
- [ ] `./gradlew compileJava` passes
- [ ] `StartMiniConversationUseCaseTest` and `EvaluateMiniConversationTurnUseCaseTest` pass
- [ ] `POST /api/pronunciation/mini-conversation` returns 201 with `id`, `prompt`, `targetPhrase`
- [ ] `POST /api/pronunciation/mini-conversation/{id}/evaluate` returns `score`, `wordFeedback`, `nextPrompt`, `nextTargetPhrase`, `isComplete`
- [ ] After MAX_TURNS evaluations, `isComplete` is `true`
- [ ] `PronunciationMiniConversationCompletedEvent` is published on completion
- [ ] `V14.1.0` migration runs cleanly
- [ ] Human review: aggregate completes cleanly, events before `pullDomainEvents()`
- [ ] Commit: `feat(pronunciation): Phase 4 — mini-conversation with per-utterance scoring`

---

## Phase 5 [S] — Architecture review (`/revisar`)

**Goal**: Validate the full `pronunciation` module for naming conventions, package structure, DDD correctness, security, and test coverage.

**Checklist**:
- [ ] Run `/revisar` on the entire `pronunciation` module
- [ ] All controllers are package-private (not `public class`)
- [ ] No `public` constructors in aggregates (`PronunciationDrill`, `PronunciationMiniConversation`)
- [ ] No field injection (`@Autowired`) anywhere in `pronunciation`
- [ ] All domain exceptions extend `PronunciationException extends Exception` (not RuntimeException)
- [ ] `PronunciationControllerAdvice` handles all 3 exceptions: `PronunciationAiException` (503), `PronunciationDrillNotFoundException` (404), `PronunciationMiniConversationNotFoundException` (404)
- [ ] `AnthropicPronunciationAiAdapter` is package-private
- [ ] All repository adapters are package-private
- [ ] All JPA repository interfaces are package-private
- [ ] `@DomainEvent` on both events
- [ ] `pullDomainEvents()` called in both use cases that fire events
- [ ] `@Transactional(propagation = REQUIRES_NEW)` on all 4 event listeners
- [ ] No cross-module domain imports (only events cross boundaries)
- [ ] Commit: `chore(pronunciation): revisar — architecture validation`

---

## API Contract

### `POST /api/pronunciation/analyze`
- **Auth**: JWT required
- **Request**: `{ "text": "thought", "level": "b1" }`
- **Response 200**: `{ "text": "thought", "ipa": "/θɔːt/", "syllables": "thought (1 syllable)", "stressPattern": "single syllable", "tips": ["..."], "commonMistakes": ["..."], "minimalPairs": ["thought/taught"], "exampleSentences": ["..."] }`
- **Status codes**: 200, 400 (validation), 401, 503 (AI unavailable)

### `POST /api/pronunciation/feedback`
- **Auth**: JWT required
- **Request**: `{ "targetText": "I thought about it", "recognizedText": "I tot about it", "wordConfidences": [{"word":"thought","confidence":0.41}] }`
- **Response 200**: `{ "score": 72, "wordFeedback": [{"word":"thought","recognized":"tot","tip":"...","score":40}], "overallTip": "..." }`
- **Status codes**: 200, 400, 401, 503

### `GET /api/pronunciation/drills`
- **Auth**: JWT required
- **Query params**: `level` (required, a1–c2), `focus` (optional)
- **Response 200**: `[{ "id": "uuid", "phrase": "...", "focus": "th-sound", "difficulty": "MEDIUM", "cefrLevel": "b1" }]`
- **Status codes**: 200, 400, 401

### `POST /api/pronunciation/drills/{id}/submit`
- **Auth**: JWT required
- **Request**: `{ "recognizedText": "...", "confidence": 0.8 }`
- **Response 200**: `{ "score": 85, "feedback": "...", "perfectStreak": 2 }`
- **Status codes**: 200, 401, 404 (drill not found), 503

### `POST /api/pronunciation/mini-conversation`
- **Auth**: JWT required
- **Request**: `{ "focus": "th-sound", "level": "b1" }`
- **Response 201**: `{ "id": "uuid", "prompt": "Read this sentence aloud: ...", "targetPhrase": "..." }`
- **Status codes**: 201, 400, 401, 503

### `POST /api/pronunciation/mini-conversation/{id}/evaluate`
- **Auth**: JWT required
- **Request**: `{ "recognizedText": "...", "wordConfidences": [{"word":"...","confidence":0.8}] }`
- **Response 200**: `{ "score": 85, "wordFeedback": [...], "nextPrompt": "...", "nextTargetPhrase": "...", "isComplete": false }`
- **Status codes**: 200, 401, 404, 503

## Database Changes

| Migration | Table(s) | Description |
|-----------|----------|-------------|
| V14.0.0 | `pronunciation_drills`, `pronunciation_drill_attempts` | Drill catalog + attempts, seeded with 5 initial drills |
| V14.1.0 | `pronunciation_mini_conversations`, `pronunciation_mini_conversation_turns` | Mini-conversation sessions and per-turn evaluations |

## Testing Strategy

- **Unit tests only** (no Testcontainers needed until integration tests are requested)
- All use cases tested with `StubPronunciationAiPort` and `InMemory*Repository`
- Object Mothers for all aggregates
- Test naming: `should{Result}When{Condition}()`
- Event publishing verified via `ApplicationEventPublisher` spy in use case tests

## Next step

Execute Phase 1: pronunciation module — domain port + AI adapter + analyze endpoint
