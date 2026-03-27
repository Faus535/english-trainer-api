# Backend Plan: Fix session warmup block advance + minigames don't start

> Generated: 2026-03-27
> Request: Fix two bugs: 1) Session warmup block advance error (block exercises not visible in response). 2) Minigames don't start (response format mismatch with frontend).

## Decisions Log

| # | Decision | Rationale |
|---|----------|-----------|
| 1 | Wrap all 3 minigame responses in objects matching frontend interfaces | Frontend expects `{pairs, level}`, `{words, level}`, `{sentences, level}` -- not raw arrays |
| 2 | Fill-gap: transform sentences to blank last word, generate distractors, return `correct` as index | Frontend expects `correct: number` (index into options array), not a string word |
| 3 | Add `exercises` list to `SessionBlockResponse` | Frontend needs block-level exercises to render block content and track progress |
| 4 | Extract `SessionResponseMapper` to eliminate duplicated `toResponse()` across 3 controllers | DRY principle; same 30-line method duplicated in Generate, GetCurrent, Complete controllers |
| 5 | Use `IntStream.range` instead of `indexOf` for block mapping | `indexOf` uses object identity/equals on records which is fragile with duplicate block types |
| 6 | Include `vocabEntryId` in word-match and unscramble responses | Frontend interfaces include optional `vocabEntryId` field |
| 7 | No database changes | All fixes are response format transformations in controller/use-case layer |

## Analysis

### Bug 1: Session warmup block advance error

**Root cause**: `SessionBlockResponse` lacks an `exercises` field. The frontend receives blocks without their exercises, making it impossible to render block content or determine completion state from the block alone. Additionally, the `toResponse()` method uses `session.blocks().indexOf(b)` which is fragile -- if two blocks have identical fields, `indexOf` returns the first match, causing incorrect exercise mapping.

**Affected files**:
- `SessionBlockResponse` -- missing `exercises` list
- `GenerateSessionController.toResponse()` -- duplicated, uses fragile `indexOf`
- `GetCurrentSessionController.toResponse()` -- duplicated, uses fragile `indexOf`
- `CompleteSessionController.toResponse()` -- duplicated, uses fragile `indexOf`

### Bug 2: Minigames don't start

**Root cause**: All 3 minigame endpoints return raw `List<T>` responses. The frontend expects wrapped objects:

| Minigame | Backend returns | Frontend expects |
|----------|----------------|-----------------|
| fill-gap | `[{en, es}]` | `{sentences: [{sentence, blank, options, correct}], level}` |
| word-match | `[{en, es}]` | `{pairs: [{en, es, vocabEntryId?}], level}` |
| unscramble | `[{en, es}]` | `{words: [{word, vocabEntryId?}], level}` |

Fill-gap additionally needs sentence transformation: blank the last word, generate 3 distractor options, and provide `correct` as the index of the real word in the options array.

## Phases

### Phase 1: Fix minigame response format

**Status**: done

**Goal**: Wrap minigame responses in objects matching frontend interfaces. Transform fill-gap sentences.

**Files to modify**:

#### 1.1 Modify `GetWordMatchDataUseCase` -- add `vocabEntryId`

**File**: `src/main/java/com/faus535/englishtrainer/minigame/application/GetWordMatchDataUseCase.java`

Change the `WordMatchPair` record and mapping:

```java
public record WordMatchPair(String en, String es, String vocabEntryId) {}

@Transactional(readOnly = true)
public List<WordMatchPair> execute(VocabLevel level) {
    List<VocabEntry> entries = vocabRepository.findRandom(10, level);
    return entries.stream()
            .map(entry -> new WordMatchPair(
                    entry.en(), entry.es(),
                    entry.id().value().toString()))
            .toList();
}
```

#### 1.2 Modify `GetUnscrambleDataUseCase` -- add `vocabEntryId`

**File**: `src/main/java/com/faus535/englishtrainer/minigame/application/GetUnscrambleDataUseCase.java`

Change the `UnscrambleItem` record and mapping:

```java
public record UnscrambleItem(String en, String es, String vocabEntryId) {}

@Transactional(readOnly = true)
public List<UnscrambleItem> execute(VocabLevel level) {
    List<VocabEntry> entries = vocabRepository.findRandom(8, level);
    return entries.stream()
            .map(entry -> new UnscrambleItem(
                    entry.en(), entry.es(),
                    entry.id().value().toString()))
            .toList();
}
```

#### 1.3 Modify `GetWordMatchDataController` -- wrap in object with `level`

**File**: `src/main/java/com/faus535/englishtrainer/minigame/infrastructure/controller/GetWordMatchDataController.java`

Replace the flat list response with a wrapped object:

```java
record WordMatchPairResponse(String en, String es, String vocabEntryId) {}
record WordMatchDataResponse(List<WordMatchPairResponse> pairs, String level) {}

@GetMapping("/api/minigames/word-match")
ResponseEntity<WordMatchDataResponse> handle(@RequestParam String level) {
    VocabLevel vocabLevel = new VocabLevel(level);
    List<GetWordMatchDataUseCase.WordMatchPair> pairs = useCase.execute(vocabLevel);
    List<WordMatchPairResponse> pairResponses = pairs.stream()
            .map(pair -> new WordMatchPairResponse(pair.en(), pair.es(), pair.vocabEntryId()))
            .toList();
    return ResponseEntity.ok(new WordMatchDataResponse(pairResponses, level));
}
```

#### 1.4 Modify `GetUnscrambleDataController` -- wrap in object with `level`

**File**: `src/main/java/com/faus535/englishtrainer/minigame/infrastructure/controller/GetUnscrambleDataController.java`

Replace the flat list response with a wrapped object:

```java
record UnscrambleWordResponse(String word, String vocabEntryId) {}
record UnscrambleDataResponse(List<UnscrambleWordResponse> words, String level) {}

@GetMapping("/api/minigames/unscramble")
ResponseEntity<UnscrambleDataResponse> handle(@RequestParam String level) {
    VocabLevel vocabLevel = new VocabLevel(level);
    List<GetUnscrambleDataUseCase.UnscrambleItem> items = useCase.execute(vocabLevel);
    List<UnscrambleWordResponse> wordResponses = items.stream()
            .map(item -> new UnscrambleWordResponse(item.en(), item.vocabEntryId()))
            .toList();
    return ResponseEntity.ok(new UnscrambleDataResponse(wordResponses, level));
}
```

#### 1.5 Modify `GetFillGapDataController` -- transform sentences, wrap in object

**File**: `src/main/java/com/faus535/englishtrainer/minigame/infrastructure/controller/GetFillGapDataController.java`

Transform each phrase: blank the last word, generate distractor options from other phrases, return `correct` as the index of the real word in the shuffled options array:

```java
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

record FillGapSentenceResponse(String sentence, String blank, List<String> options, int correct) {}
record FillGapDataResponse(List<FillGapSentenceResponse> sentences, String level) {}

@GetMapping("/api/minigames/fill-gap")
ResponseEntity<FillGapDataResponse> handle(@RequestParam String level) {
    VocabLevel vocabLevel = new VocabLevel(level);
    List<GetFillGapDataUseCase.FillGapItem> items = useCase.execute(vocabLevel);

    List<FillGapSentenceResponse> sentences = new ArrayList<>();
    Random random = new Random();

    for (int i = 0; i < items.size(); i++) {
        GetFillGapDataUseCase.FillGapItem item = items.get(i);
        String[] words = item.en().split("\\s+");
        if (words.length < 2) continue;

        String lastWord = words[words.length - 1].replaceAll("[^a-zA-Z']", "");
        String sentenceWithBlank = String.join(" ", java.util.Arrays.copyOf(words, words.length - 1)) + " ___";

        // Collect distractors from other items' last words
        List<String> distractors = items.stream()
                .filter(other -> !other.equals(item))
                .map(other -> {
                    String[] otherWords = other.en().split("\\s+");
                    return otherWords[otherWords.length - 1].replaceAll("[^a-zA-Z']", "");
                })
                .filter(w -> !w.equalsIgnoreCase(lastWord))
                .distinct()
                .limit(3)
                .collect(Collectors.toList());

        // Build options list and shuffle
        List<String> options = new ArrayList<>(distractors);
        options.add(lastWord);
        Collections.shuffle(options, random);

        int correctIndex = options.indexOf(lastWord);

        sentences.add(new FillGapSentenceResponse(sentenceWithBlank, lastWord, options, correctIndex));
    }

    return ResponseEntity.ok(new FillGapDataResponse(sentences, level));
}
```

#### 1.6 Tests for minigame controllers

**File to create**: `src/test/java/com/faus535/englishtrainer/minigame/application/GetFillGapDataUseCaseTest.java`

Test that the use case returns items correctly from the repository.

**File to create**: `src/test/java/com/faus535/englishtrainer/minigame/application/GetWordMatchDataUseCaseTest.java`

Test that `vocabEntryId` is included in the response.

**File to create**: `src/test/java/com/faus535/englishtrainer/minigame/application/GetUnscrambleDataUseCaseTest.java`

Test that `vocabEntryId` is included in the response.

Test cases per use case:
- `should_return_items_with_vocabEntryId` (word-match, unscramble)
- `should_return_items_from_phrases` (fill-gap)
- `should_return_empty_list_when_no_data`

**Acceptance criteria**:
- `GET /api/minigames/word-match?level=A1` returns `{"pairs": [{en, es, vocabEntryId}], "level": "A1"}`
- `GET /api/minigames/unscramble?level=A1` returns `{"words": [{word, vocabEntryId}], "level": "A1"}`
- `GET /api/minigames/fill-gap?level=A1` returns `{"sentences": [{sentence, blank, options, correct}], "level": "A1"}`
- `correct` field is a number (index into `options` array)
- All existing tests still pass

**Verification**:
```bash
./gradlew compileJava compileTestJava
./gradlew test --tests "*GetWordMatchData*"
./gradlew test --tests "*GetUnscrambleData*"
./gradlew test --tests "*GetFillGapData*"
```

**Commit message**:
```
Fix minigame response format to match frontend interfaces
```

---

### Phase 2: Fix session exercise tracking (SessionBlockResponse enrichment)

**Status**: done

**Goal**: Add exercises list to `SessionBlockResponse`, extract `SessionResponseMapper`, fix fragile `indexOf` usage.

**Files to create/modify**:

#### 2.1 Modify `SessionBlockResponse` -- add exercises list

**File**: `src/main/java/com/faus535/englishtrainer/session/infrastructure/controller/SessionBlockResponse.java`

Add the `exercises` field so the frontend can see which exercises belong to each block:

```java
record SessionBlockResponse(
        String blockType,
        String moduleName,
        int durationMinutes,
        int exerciseCount,
        int completedExercises,
        boolean blockCompleted,
        List<SessionExerciseResponse> exercises
) {}
```

#### 2.2 Create `SessionResponseMapper` -- extract duplicated logic

**File to create**: `src/main/java/com/faus535/englishtrainer/session/infrastructure/controller/SessionResponseMapper.java`

Extract the `toResponse()` method shared across 3 controllers. Fix the `indexOf` bug by using `IntStream.range`:

```java
package com.faus535.englishtrainer.session.infrastructure.controller;

import com.faus535.englishtrainer.session.domain.BlockProgress;
import com.faus535.englishtrainer.session.domain.Session;
import com.faus535.englishtrainer.session.domain.SessionExercise;

import java.util.List;
import java.util.stream.IntStream;

final class SessionResponseMapper {

    private SessionResponseMapper() {}

    static SessionResponse toResponse(Session session) {
        List<SessionBlockResponse> blockResponses = IntStream.range(0, session.blocks().size())
                .mapToObj(blockIdx -> {
                    var block = session.blocks().get(blockIdx);
                    BlockProgress progress = session.getBlockProgress(blockIdx);
                    List<SessionExercise> blockExercises = session.getExercisesForBlock(blockIdx);

                    List<SessionExerciseResponse> exerciseResponses = blockExercises.stream()
                            .map(ex -> new SessionExerciseResponse(
                                    ex.exerciseIndex(), ex.blockIndex(), ex.exerciseType(),
                                    ex.targetCount(), ex.isCompleted(),
                                    ex.result() != null ? ex.result().correctCount() : null,
                                    ex.result() != null ? ex.result().totalCount() : null))
                            .toList();

                    return new SessionBlockResponse(
                            block.blockType(), block.moduleName(), block.durationMinutes(),
                            block.exerciseCount(), progress.completedExercises(),
                            progress.isCompleted(), exerciseResponses);
                })
                .toList();

        List<SessionExerciseResponse> allExercises = session.exercises().stream()
                .map(ex -> new SessionExerciseResponse(
                        ex.exerciseIndex(), ex.blockIndex(), ex.exerciseType(),
                        ex.targetCount(), ex.isCompleted(),
                        ex.result() != null ? ex.result().correctCount() : null,
                        ex.result() != null ? ex.result().totalCount() : null))
                .toList();

        return new SessionResponse(
                session.id().value().toString(),
                session.userId().value().toString(),
                session.mode().value(),
                session.sessionType().value(),
                session.listeningModule(),
                session.secondaryModule(),
                session.integratorTheme(),
                blockResponses,
                allExercises,
                session.completed(),
                session.startedAt().toString(),
                session.completedAt() != null ? session.completedAt().toString() : null,
                session.durationMinutes()
        );
    }
}
```

#### 2.3 Modify `GenerateSessionController` -- use mapper

**File**: `src/main/java/com/faus535/englishtrainer/session/infrastructure/controller/GenerateSessionController.java`

Replace the private `toResponse()` method with `SessionResponseMapper.toResponse()`:

```java
// In handle():
return ResponseEntity.status(HttpStatus.CREATED).body(SessionResponseMapper.toResponse(session));

// Delete the private toResponse() method entirely
```

#### 2.4 Modify `GetCurrentSessionController` -- use mapper

**File**: `src/main/java/com/faus535/englishtrainer/session/infrastructure/controller/GetCurrentSessionController.java`

Replace the private `toResponse()` method with `SessionResponseMapper.toResponse()`:

```java
// In handle():
return session
    .map(s -> ResponseEntity.ok(SessionResponseMapper.toResponse(s)))
    .orElseGet(() -> ResponseEntity.notFound().build());

// Delete the private toResponse() method entirely
```

#### 2.5 Modify `CompleteSessionController` -- use mapper

**File**: `src/main/java/com/faus535/englishtrainer/session/infrastructure/controller/CompleteSessionController.java`

Replace the private `toResponse()` method with `SessionResponseMapper.toResponse()`:

```java
// In handle():
return ResponseEntity.ok(SessionResponseMapper.toResponse(session));

// Delete the private toResponse() method entirely
```

#### 2.6 Modify `GetBlockExercisesController` -- use `SessionExerciseResponse` mapping consistently

**File**: `src/main/java/com/faus535/englishtrainer/session/infrastructure/controller/GetBlockExercisesController.java`

No changes needed -- already maps correctly. Verify it compiles with updated `SessionBlockResponse`.

#### 2.7 Tests for `SessionResponseMapper`

**File to create**: `src/test/java/com/faus535/englishtrainer/session/infrastructure/controller/SessionResponseMapperTest.java`

Test cases:
- `should_map_session_with_blocks_and_exercises` -- verify exercises appear inside each block's `exercises` list
- `should_map_block_exercises_by_blockIndex` -- verify exercises are correctly grouped per block (not all lumped into block 0)
- `should_handle_session_with_no_exercises` -- verify empty exercises list works
- `should_use_index_based_mapping_not_indexOf` -- create session with 2 blocks of same type, verify both get correct progress

```java
package com.faus535.englishtrainer.session.infrastructure.controller;

import com.faus535.englishtrainer.session.domain.*;
import com.faus535.englishtrainer.user.domain.UserProfileId;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

final class SessionResponseMapperTest {

    @Test
    void should_map_block_exercises_correctly() {
        Session session = Session.reconstitute(
                SessionId.generate(), UserProfileId.generate(),
                new SessionMode("daily"), new SessionType("standard"),
                "listening", null, null,
                List.of(
                        new SessionBlock("warmup", "vocabulary", 5, 2, List.of()),
                        new SessionBlock("core", "listening", 10, 1, List.of())
                ),
                List.of(
                        new SessionExercise(0, 0, "flashcard", List.of(), 5, null),
                        new SessionExercise(1, 0, "multiple_choice", List.of(), 3, null),
                        new SessionExercise(2, 1, "listening", List.of(), 4, null)
                ),
                false, java.time.Instant.now(), null, null
        );

        SessionResponse response = SessionResponseMapper.toResponse(session);

        assertEquals(2, response.blocks().size());
        assertEquals(2, response.blocks().get(0).exercises().size());
        assertEquals(1, response.blocks().get(1).exercises().size());
        assertEquals("flashcard", response.blocks().get(0).exercises().get(0).exerciseType());
        assertEquals("listening", response.blocks().get(1).exercises().get(0).exerciseType());
    }

    @Test
    void should_handle_duplicate_block_types() {
        // Two blocks with identical blockType -- indexOf would return 0 for both
        Session session = Session.reconstitute(
                SessionId.generate(), UserProfileId.generate(),
                new SessionMode("daily"), new SessionType("standard"),
                "listening", null, null,
                List.of(
                        new SessionBlock("warmup", "vocabulary", 5, 1, List.of()),
                        new SessionBlock("warmup", "vocabulary", 5, 1, List.of())
                ),
                List.of(
                        new SessionExercise(0, 0, "flashcard", List.of(), 5,
                                new ExerciseResult(5, 5)),
                        new SessionExercise(1, 1, "flashcard", List.of(), 3, null)
                ),
                false, java.time.Instant.now(), null, null
        );

        SessionResponse response = SessionResponseMapper.toResponse(session);

        // Block 0 should show completed, block 1 should not
        assertTrue(response.blocks().get(0).blockCompleted());
        assertFalse(response.blocks().get(1).blockCompleted());
        assertEquals(1, response.blocks().get(0).completedExercises());
        assertEquals(0, response.blocks().get(1).completedExercises());
    }
}
```

**Acceptance criteria**:
- `SessionBlockResponse` includes `exercises` list of `SessionExerciseResponse`
- All 3 session controllers use `SessionResponseMapper.toResponse()`
- No duplicated `toResponse()` methods remain in controllers
- `IntStream.range` used instead of `indexOf` for block iteration
- Block exercises are correctly grouped by `blockIndex`
- All existing session tests pass
- New mapper tests pass

**Verification**:
```bash
./gradlew compileJava compileTestJava
./gradlew test --tests "*Session*"
./gradlew test --tests "*SessionResponseMapper*"
```

**Commit message**:
```
Enrich SessionBlockResponse with exercises, extract SessionResponseMapper
```

---

### Phase 3: Verification

**Status**: done

**Goal**: Run full test suite and `/revisar` to validate architecture and code quality.

**Verification**:
```bash
./gradlew test
```

Then run `/revisar` to validate:
- No architecture violations
- No duplicated code
- Naming conventions followed
- All controllers are package-private
- All new records follow project conventions

**Acceptance criteria**:
- Full test suite passes with 0 failures
- `/revisar` reports no critical issues
- Application compiles and starts successfully

---

## API Contract

### Minigame Endpoints (modified response format)

```
GET /api/minigames/word-match?level=A1
Response 200:
{
  "pairs": [
    {"en": "hello", "es": "hola", "vocabEntryId": "uuid-string"},
    {"en": "goodbye", "es": "adios", "vocabEntryId": "uuid-string"}
  ],
  "level": "A1"
}

GET /api/minigames/unscramble?level=A1
Response 200:
{
  "words": [
    {"word": "hello", "vocabEntryId": "uuid-string"},
    {"word": "goodbye", "vocabEntryId": "uuid-string"}
  ],
  "level": "A1"
}

GET /api/minigames/fill-gap?level=A1
Response 200:
{
  "sentences": [
    {
      "sentence": "I want to ___",
      "blank": "go",
      "options": ["run", "go", "eat", "see"],
      "correct": 1
    }
  ],
  "level": "A1"
}
```

### Session Endpoints (enriched response format)

```
POST /api/profiles/{userId}/sessions/generate
GET  /api/profiles/{userId}/sessions/current
PUT  /api/profiles/{userId}/sessions/{sessionId}/complete

Response 200/201 -- SessionResponse (updated):
{
  "id": "uuid",
  "userId": "uuid",
  "mode": "daily",
  "sessionType": "standard",
  "listeningModule": "listening",
  "secondaryModule": null,
  "integratorTheme": null,
  "blocks": [
    {
      "blockType": "warmup",
      "moduleName": "vocabulary",
      "durationMinutes": 5,
      "exerciseCount": 2,
      "completedExercises": 0,
      "blockCompleted": false,
      "exercises": [
        {
          "exerciseIndex": 0,
          "blockIndex": 0,
          "exerciseType": "flashcard",
          "targetCount": 5,
          "completed": false,
          "correctCount": null,
          "totalCount": null
        }
      ]
    }
  ],
  "exercises": [ ... ],
  "completed": false,
  "startedAt": "2026-03-27T10:00:00Z",
  "completedAt": null,
  "durationMinutes": null
}
```

## Database Changes

None. All changes are in the controller/application layer (response format transformations).

## Testing Strategy

| Layer | What to test | Approach |
|-------|-------------|----------|
| Use Case | `GetWordMatchDataUseCase` returns items with `vocabEntryId` | Unit test with `InMemoryVocabRepository` |
| Use Case | `GetUnscrambleDataUseCase` returns items with `vocabEntryId` | Unit test with `InMemoryVocabRepository` |
| Use Case | `GetFillGapDataUseCase` returns phrase data | Unit test with `InMemoryPhraseRepository` |
| Controller mapper | `SessionResponseMapper.toResponse()` groups exercises by block | Unit test with `SessionMother` |
| Controller mapper | Duplicate block types don't cause indexOf collision | Unit test with manual Session construction |
| Integration | All existing tests continue to pass | `./gradlew test` |
