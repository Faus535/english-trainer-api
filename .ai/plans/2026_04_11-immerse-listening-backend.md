# Backend Plan: Immerse Listening Mode

> Generated: 2026-04-11
> Request: Add LISTENING_CLOZE exercise type to immerse module for listening comprehension training

## Decisions Log

| # | Topic | Decision | Alternatives Considered | Why |
|---|-------|----------|------------------------|-----|
| 1 | Exercise type extension | Add LISTENING_CLOZE to existing ExerciseType enum | New table | Additive, no breaking change |
| 2 | listenText storage | Add column to immerse_exercises | Separate table | One exercise = one row, simpler |
| 3 | Filtering | Optional `?type=` query param | Separate endpoint | RESTful, backwards compatible |
| 4 | AI generation | Extend existing tool schema | New prompt | Reuse existing AnthropicImmerseAiAdapter infra |

## Context

- Reference module: `article` — async AI generation patterns
- Modules affected: `immerse`

## Public Contracts

### Use Cases

```java
// Extended (existing use case, new filter param)
GetImmerseExercisesUseCase.execute(UUID contentId, ExerciseTypeFilter filter) throws ImmerseContentNotFoundException

// ExerciseTypeFilter enum: ALL, LISTENING_CLOZE, REGULAR
```

### Domain Events

None new.

### Domain Exceptions

None new.

### Database Schema

```sql
-- V11.8.0
ALTER TABLE immerse_exercises ADD COLUMN listen_text TEXT;
ALTER TABLE immerse_exercises ADD COLUMN blank_position INTEGER;
```

### REST Endpoints

```
GET /api/immerse/content/{contentId}/exercises?type=LISTENING_CLOZE|REGULAR|ALL
```

## Analysis

**Existing files relevant to this feature:**
- `immerse/domain/ImmerseExercise.java` — record with `type: ExerciseType`, `question`, `options`, `correctAnswer`
- `immerse/domain/vo/ExerciseType.java` — enum: `VOCABULARY_MATCH`, `FILL_BLANK`, `COMPREHENSION_QUESTION`
- `immerse/infrastructure/ImmerseExerciseEntity.java` — JPA entity
- `immerse/infrastructure/ImmerseExerciseJpaRepository.java` — Spring Data
- `immerse/application/GetImmerseExercisesUseCase.java` — loads by contentId
- `immerse/infrastructure/AnthropicImmerseAiAdapter.java` — tool schema + parse methods
- `immerse/domain/port/ImmerseAiPort.java` — interface with `GeneratedExercise` inner record

**Existing tests impacted:**
- `ImmerseExerciseTest` — add LISTENING_CLOZE field tests
- `ImmerseExerciseMother` — add `listeningCloze()` builder
- `GetImmerseExercisesUseCaseTest` — add filter test cases
- `AnthropicImmerseAiAdapterTest` — extend tool schema tests if they exist

## Phases

### Phase 1: Domain — LISTENING_CLOZE type + DB migration [S]

**Goal**: Domain model accepts new exercise type with `listenText` and `blankPosition` fields. Migration applied.

**Files to create/modify**:
- `src/main/java/com/faus535/englishtrainer/immerse/domain/vo/ExerciseType.java` — add `LISTENING_CLOZE`
- `src/main/java/com/faus535/englishtrainer/immerse/domain/ImmerseExercise.java` — add `listenText` (String, nullable), `blankPosition` (Integer, nullable) fields
- `src/main/java/com/faus535/englishtrainer/immerse/infrastructure/ImmerseExerciseEntity.java` — add column mappings
- `src/main/resources/db/migration/V11.8.0__immerse_exercises_add_listening_fields.sql` — migration
- `src/test/java/com/faus535/englishtrainer/immerse/domain/ImmerseExerciseTest.java` — extend
- `src/test/java/com/faus535/englishtrainer/immerse/domain/ImmerseExerciseMother.java` — add `listeningCloze()`

**Details**:
- `ImmerseExercise` record: add `@Nullable String listenText` and `@Nullable Integer blankPosition` to record components
- For non-listening exercises, both fields are null — backwards compatible
- `ImmerseExerciseEntity`: add `@Column("listen_text") private String listenText` and `@Column("blank_position") private Integer blankPosition`
- Object Mother `ImmerseExerciseMother.listeningCloze()`: returns exercise with type=LISTENING_CLOZE, listenText="She ordered a cup of coffee at the café.", blankPosition=5, correctAnswer="coffee"

**Test method names**:
- `ImmerseExerciseTest.listeningCloze_hasListenTextAndBlankPosition()`
- `ImmerseExerciseTest.regularExercise_hasNullListenText()`

**Acceptance criteria**:
- [x] `ExerciseType.LISTENING_CLOZE` compiles
- [x] Migration `V11.8.0` runs without error
- [x] `ImmerseExercise` record accepts null `listenText` and `blankPosition`
- [x] Verify compilation
- [x] Verify tests pass
- [ ] Human review
- [ ] Commit

---

### Phase 2: AI adapter — generate LISTENING_CLOZE exercises [M]

**Goal**: AI generates one LISTENING_CLOZE exercise per vocabulary item alongside regular exercises.

**Files to create/modify**:
- `src/main/java/com/faus535/englishtrainer/immerse/domain/port/ImmerseAiPort.java` — extend `GeneratedExercise` with `listenText`, `blankPosition`
- `src/main/java/com/faus535/englishtrainer/immerse/infrastructure/AnthropicImmerseAiAdapter.java` — update tool schema + parse + system prompt
- `src/test/java/com/faus535/englishtrainer/immerse/infrastructure/AnthropicImmerseAiAdapterTest.java` — extend if exists

**Details**:
- `GeneratedExercise` (record in `ImmerseAiPort`): add `String listenText` (nullable) and `Integer blankPosition` (nullable)
- Tool schema: add `"listen_text"` (string, optional description: "Full sentence for TTS. Required for LISTENING_CLOZE type only.") and `"blank_position"` (integer, optional) to exercise object schema
- System prompt addition: "For each vocabulary item, also generate one exercise of type LISTENING_CLOZE: provide `listen_text` as a complete natural sentence containing the word, and `blank_position` as the 0-based word index of the target word in the sentence."
- Parse method: when `type == "LISTENING_CLOZE"`, map `listen_text` and `blank_position` from JSON tool call
- For LISTENING_CLOZE: `question` field = "What word do you hear at position X?", `options` = word + 3 distractors, `correctAnswer` = target word

**Test method names**:
- `AnthropicImmerseAiAdapterTest.parse_listeningClozeExercise_mapsListenTextAndBlankPosition()` (if adapter has unit tests)

**Acceptance criteria**:
- [x] AI response JSON with `listen_text` is parsed correctly
- [x] `GeneratedExercise` includes `listenText` when type is LISTENING_CLOZE
- [x] Regular exercises still parse correctly (no regression)
- [x] Verify compilation
- [x] Verify tests pass
- [ ] Human review
- [ ] Commit

---

### Phase 3: Filtering — `?type=` query param on GET exercises [S]

**Goal**: Frontend can request only LISTENING_CLOZE or only REGULAR exercises.

**Files to create/modify**:
- `src/main/java/com/faus535/englishtrainer/immerse/domain/vo/ExerciseTypeFilter.java` — new enum: ALL, LISTENING_CLOZE, REGULAR
- `src/main/java/com/faus535/englishtrainer/immerse/domain/ImmerseExerciseRepository.java` — add `findByContentIdAndTypeFilter(UUID contentId, ExerciseTypeFilter filter)`
- `src/main/java/com/faus535/englishtrainer/immerse/infrastructure/ImmerseExerciseJpaRepository.java` — add query methods
- `src/main/java/com/faus535/englishtrainer/immerse/infrastructure/ImmerseExerciseRepositoryJpa.java` — implement filter
- `src/main/java/com/faus535/englishtrainer/immerse/application/GetImmerseExercisesUseCase.java` — accept `ExerciseTypeFilter` param
- `src/main/java/com/faus535/englishtrainer/immerse/infrastructure/GetImmerseExercisesController.java` — add `?type=ALL` query param
- `src/test/java/com/faus535/englishtrainer/immerse/application/GetImmerseExercisesUseCaseTest.java` — add filter cases

**Details**:
- Default `?type=ALL` (backwards compatible — existing calls without param return all)
- `ExerciseTypeFilter.ALL` → `findByContentId(UUID)` (no type filter in SQL)
- `ExerciseTypeFilter.LISTENING_CLOZE` → `findByContentIdAndType(UUID, "LISTENING_CLOZE")`
- `ExerciseTypeFilter.REGULAR` → `findByContentIdAndTypeNot(UUID, "LISTENING_CLOZE")`
- Controller: `@RequestParam(defaultValue = "ALL") ExerciseTypeFilter type`

**Test method names**:
- `GetImmerseExercisesUseCaseTest.execute_returnsAllExercises_whenFilterIsAll()`
- `GetImmerseExercisesUseCaseTest.execute_returnsOnlyListeningCloze_whenFilterIsListeningCloze()`
- `GetImmerseExercisesUseCaseTest.execute_returnsOnlyRegular_whenFilterIsRegular()`

**Acceptance criteria**:
- [x] `GET /api/immerse/content/{id}/exercises` (no param) returns all exercises
- [x] `GET .../exercises?type=LISTENING_CLOZE` returns only LISTENING_CLOZE
- [x] `GET .../exercises?type=REGULAR` returns no LISTENING_CLOZE
- [x] Verify compilation
- [x] Verify tests pass
- [ ] Human review
- [ ] Commit

---

## API Contract

### `GET /api/immerse/content/{contentId}/exercises?type=ALL`
- **Request body**: none
- **Response body**:
```json
[
  {
    "id": "uuid",
    "type": "LISTENING_CLOZE",
    "question": "What word do you hear?",
    "options": ["coffee", "water", "tea", "juice"],
    "correctAnswer": "coffee",
    "listenText": "She ordered a cup of coffee at the café.",
    "blankPosition": 6
  }
]
```
- **Status codes**: 200, 404
- **Auth**: Bearer JWT required

## Database Changes

```sql
-- V11.8.0__immerse_exercises_add_listening_fields.sql
ALTER TABLE immerse_exercises ADD COLUMN listen_text TEXT;
ALTER TABLE immerse_exercises ADD COLUMN blank_position INTEGER;
```

## Testing Strategy

- Phase 1: Domain record unit tests + Object Mother
- Phase 2: Adapter parse unit tests (mock Anthropic JSON response)
- Phase 3: Use case tests with In-Memory repository, filter scenarios

## Next step

Execute Phase 1: Domain — LISTENING_CLOZE type + DB migration
