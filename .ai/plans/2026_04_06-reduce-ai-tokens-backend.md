# Backend Plan: Reduce AI Token Consumption (Immerse + Talk)

> Generated: 2026-04-06
> Request: Reduce AI token consumption across modules, focusing on immerse (level-aware content sizing, remove rawText/processedText duplication, compact prompts) and minor talk optimizations

## Decisions Log

| # | Topic | Decision | Alternatives Considered | Why |
|---|-------|----------|------------------------|-----|
| 1 | VocabularyItem.exampleSentence rename | Keep `exampleSentence` in Java record, only shorten description text in AI tool schema | A) Rename Java field + @JsonProperty, B) Data migration | Existing JSON in DB uses `exampleSentence` key. Jackson would silently null out the field on deserialization if renamed. Option C is zero-risk. |
| 2 | rawText in domain aggregate | Keep rawText field in ImmerseContent aggregate and entity. Only remove from ImmerseGenerateResult and the generate tool schema. | Remove rawText entirely from aggregate | Submit path (`SubmitImmerseContentUseCase`) stores user-provided rawText. The field is still needed for that flow. Generate path will pass null for rawText. |
| 3 | Migration version | Use V9.7.0 (next after V9.6.0, the actual latest migration) | V10.11.0 (specialist suggested) | Specialists assumed wrong latest version. Actual latest is V9.6.0. |
| 4 | processContent max_tokens | Use `ImmerseContentSizing.processingMaxTokens()` as a dedicated field (60% of generateMaxTokens) | A) sizing.maxTokens()/2, B) same as generate | processContent transforms existing text (less generation needed) but still needs room for vocabulary + exercises. 60% is a balanced ratio. |
| 5 | ImmerseContentResponse fallback | Add null-safe fallback: return processedText if available, else rawText | No change needed | Generate path now stores null rawText, so processedText is the primary field. Old submit-path content still has rawText as backup. |
| 6 | Entity raw_text nullable | Make `raw_text` column nullable via migration, update entity annotation | Drop column entirely | Column still used by submit path. Only generate path will insert null. |

## Analysis

### Existing Code (Immerse Module)

- **Domain**: `ImmerseContent.java` aggregate with rawText + processedText fields. `VocabularyItem` record with exampleSentence. `ImmerseAiPort` interface with `ImmerseGenerateResult(title, rawText, processedText, ...)` and `ImmerseProcessResult`.
- **Infrastructure AI**: `AnthropicImmerseAiAdapter` with hardcoded `max_tokens: 4000` for generate, `max_tokens: 2000` for process. Hardcoded word range "300-600 words", "5-6 exercises", "6-10 vocabulary items" in user message. Verbose system prompts (~80 words each).
- **Use Cases**: `GenerateImmerseContentUseCase` calls `aiPort.generateContent()` then `ImmerseContent.generate()` passing rawText. `SubmitImmerseContentUseCase` calls `aiPort.processContent(rawText, level)` then `content.markProcessed()`.
- **Entity**: `ImmerseContentEntity` maps rawText to `raw_text NOT NULL` column.
- **Response**: `ImmerseContentResponse` only exposes processedText (not rawText) to API.
- **Tests**: `StubImmerseAiPort`, `ImmerseContentMother`, `GenerateImmerseContentUseCaseTest` all reference rawText in ImmerseGenerateResult.

### Existing Code (Talk Module)

- **Infrastructure AI**: `AnthropicTalkAiAdapter` with `evaluate()` using `max_tokens: 500` and verbose tool descriptions. `summarize()` using `max_tokens: 200`.
- **Domain**: `LevelProfiles.forLevel()` pattern already exists -- will mirror for ImmerseContentSizing.

### Token Savings Estimate

| Call | Before | After (B1) | After (A1) | After (C2) |
|------|--------|-------------|------------|------------|
| generateContent() | 4000 | 2000 | 800 | 3500 |
| processContent() | 2000 | 1200 | 480 | 2100 |
| evaluate() | 500 | 350 | 350 | 350 |
| **Total per session** | **6500** | **3550** | **1630** | **5950** |

## Phases

### Phase 1: Level-Aware Content Sizing for Immerse

**Goal**: Replace all hardcoded token limits and content sizing in the immerse generate/process flows with a level-aware value object.

**Files to create**:
- `src/main/java/com/faus535/englishtrainer/immerse/domain/ImmerseContentSizing.java` -- Value object record with static factory `forLevel(String)`
- `src/test/java/com/faus535/englishtrainer/immerse/domain/ImmerseContentSizingTest.java` -- Unit tests for sizing logic

**Files to modify**:
- `src/main/java/com/faus535/englishtrainer/immerse/infrastructure/ai/AnthropicImmerseAiAdapter.java` -- Use ImmerseContentSizing in generateContent() and processContent()

**Details**:

1. Create `ImmerseContentSizing` as a Java record:
```java
package com.faus535.englishtrainer.immerse.domain;

public record ImmerseContentSizing(
    int minWords, int maxWords, int exerciseCount,
    int vocabCount, int generateMaxTokens, int processingMaxTokens
) {
    private static final java.util.Map<String, ImmerseContentSizing> SIZINGS = java.util.Map.of(
        "a1", new ImmerseContentSizing(80, 120, 3, 4, 800, 480),
        "a2", new ImmerseContentSizing(100, 180, 4, 5, 1200, 720),
        "b1", new ImmerseContentSizing(200, 350, 5, 6, 2000, 1200),
        "b2", new ImmerseContentSizing(300, 500, 5, 8, 2500, 1500),
        "c1", new ImmerseContentSizing(400, 600, 6, 10, 3000, 1800),
        "c2", new ImmerseContentSizing(500, 700, 6, 10, 3500, 2100)
    );

    public static ImmerseContentSizing forLevel(String level) {
        if (level == null) return SIZINGS.get("b1");
        ImmerseContentSizing sizing = SIZINGS.get(level.toLowerCase());
        return sizing != null ? sizing : SIZINGS.get("b1");
    }
}
```

2. Modify `AnthropicImmerseAiAdapter.generateContent()`:
   - Resolve sizing with `ImmerseContentSizing.forLevel(level)`
   - Replace hardcoded `"max_tokens", 4000` with `sizing.generateMaxTokens()`
   - Update `buildGenerateUserMessage()` to use `sizing.minWords()`, `sizing.maxWords()`, `sizing.exerciseCount()`, `sizing.vocabCount()`

3. Modify `AnthropicImmerseAiAdapter.processContent()`:
   - Resolve sizing with `ImmerseContentSizing.forLevel(level)`
   - Replace hardcoded `"max_tokens", 2000` with `sizing.processingMaxTokens()`

4. Write `ImmerseContentSizingTest`:
   - `forLevel_a1_returnsSmallestSizing()` -- verify maxTokens=800, minWords=80
   - `forLevel_b1_returnsMediumSizing()` -- verify maxTokens=2000
   - `forLevel_c2_returnsLargestSizing()` -- verify maxTokens=3500
   - `forLevel_null_defaultsToB1()` -- verify returns b1 sizing
   - `forLevel_unknownLevel_defaultsToB1()` -- verify returns b1 sizing

**Acceptance criteria**:
- [ ] `ImmerseContentSizing.forLevel("a1").generateMaxTokens()` returns 800
- [ ] `ImmerseContentSizing.forLevel(null)` returns b1 default
- [ ] `AnthropicImmerseAiAdapter.generateContent()` uses level-aware max_tokens
- [ ] `AnthropicImmerseAiAdapter.processContent()` uses level-aware max_tokens
- [ ] User message includes level-specific word range and exercise/vocab counts
- [ ] All `ImmerseContentSizingTest` tests pass
- [ ] Project compiles with `./gradlew compileJava`

---

### Phase 2: Remove rawText Duplication from Generate Path

**Goal**: Stop generating duplicate rawText in the generate flow. AI returns a single `text` field instead of rawText + processedText. Database migration makes raw_text nullable.

**Files to create**:
- `src/main/resources/db/migration/V9.7.0__immerse_nullable_raw_text.sql` -- Make raw_text nullable

**Files to modify**:
- `src/main/java/com/faus535/englishtrainer/immerse/domain/ImmerseAiPort.java` -- Remove rawText from ImmerseGenerateResult
- `src/main/java/com/faus535/englishtrainer/immerse/domain/ImmerseContent.java` -- Update `generate()` factory to accept null rawText
- `src/main/java/com/faus535/englishtrainer/immerse/infrastructure/persistence/ImmerseContentEntity.java` -- Make rawText column nullable in annotation
- `src/main/java/com/faus535/englishtrainer/immerse/infrastructure/ai/AnthropicImmerseAiAdapter.java` -- Remove rawText from tool schema and parsing
- `src/main/java/com/faus535/englishtrainer/immerse/application/GenerateImmerseContentUseCase.java` -- Update result mapping (no rawText)
- `src/main/java/com/faus535/englishtrainer/immerse/infrastructure/controller/ImmerseContentResponse.java` -- Add null-safe fallback for processedText
- `src/test/java/com/faus535/englishtrainer/immerse/infrastructure/StubImmerseAiPort.java` -- Remove rawText from ImmerseGenerateResult constructor
- `src/test/java/com/faus535/englishtrainer/immerse/domain/ImmerseContentMother.java` -- Update `generated()` and `generatedAudio()` to pass null rawText
- `src/test/java/com/faus535/englishtrainer/immerse/application/GenerateImmerseContentUseCaseTest.java` -- Update assertions (rawText is null for generated content)

**Details**:

1. **DB Migration** `V9.7.0__immerse_nullable_raw_text.sql`:
```sql
ALTER TABLE immerse_content ALTER COLUMN raw_text DROP NOT NULL;
```

2. **ImmerseAiPort.java** -- Remove rawText from `ImmerseGenerateResult`:
```java
record ImmerseGenerateResult(
    String title,
    String processedText,
    String detectedLevel,
    List<VocabularyItem> vocabulary,
    List<GeneratedExercise> exercises
) {}
```

3. **ImmerseContent.java** -- Update `generate()` to not require rawText:
```java
public static ImmerseContent generate(UUID userId, ContentType contentType, String title,
                                       String processedText, String cefrLevel,
                                       List<VocabularyItem> vocabulary) {
    return new ImmerseContent(
            ImmerseContentId.generate(), userId, null, title, null,
            processedText, cefrLevel, vocabulary, contentType,
            ImmerseContentStatus.PROCESSED, Instant.now());
}
```
Keep the old constructor, `submit()`, `reconstitute()`, and `markProcessed()` unchanged since they still use rawText.

4. **ImmerseContentEntity.java** -- Change `rawText` annotation from `nullable = false` to no nullable constraint (or `nullable = true`):
```java
@Column(name = "raw_text", columnDefinition = "TEXT")
private String rawText;
```

5. **AnthropicImmerseAiAdapter.java**:
   - In `buildGenerateContentTool()`: Remove the `rawText` entry from properties map. Remove `rawText` from required list. Rename `processedText` to `text` in tool schema.
   - In `parseGenerateResult()`: Read `text` instead of `rawText`/`processedText`. Return `new ImmerseGenerateResult(title, text, detectedLevel, vocabulary, exercises)`.

6. **GenerateImmerseContentUseCase.java** -- Update `ImmerseContent.generate()` call to new signature (no rawText):
```java
ImmerseContent content = ImmerseContent.generate(
    userId, contentType, result.title(),
    result.processedText(), result.detectedLevel(), result.vocabulary());
```

7. **ImmerseContentResponse.java** -- Add null-safe fallback:
```java
static ImmerseContentResponse from(ImmerseContent content) {
    String text = content.processedText() != null ? content.processedText() : content.rawText();
    return new ImmerseContentResponse(
            content.id().value().toString(), content.title(), content.sourceUrl(),
            text, content.cefrLevel(), content.extractedVocabulary(),
            content.contentType() != null ? content.contentType().value() : null,
            content.status().value(), content.createdAt());
}
```

8. **StubImmerseAiPort.java** -- Update `generateContent()` return to new ImmerseGenerateResult constructor (no rawText field).

9. **ImmerseContentMother.java** -- Update `generated()` and `generatedAudio()` to call the new `ImmerseContent.generate()` signature (no rawText).

10. **GenerateImmerseContentUseCaseTest.java** -- Change `assertNotNull(result.rawText())` in `worksWithoutTopicOrLevel()` to `assertNull(result.rawText())`.

**Acceptance criteria**:
- [ ] `ImmerseGenerateResult` has no rawText field
- [ ] `ImmerseContent.generate()` sets rawText to null
- [ ] Entity annotation allows null raw_text
- [ ] Migration V9.7.0 runs successfully
- [ ] Generate tool schema has single `text` field instead of rawText + processedText
- [ ] `ImmerseContentResponse.from()` returns processedText (or rawText fallback for old submit content)
- [ ] All existing tests compile and pass (StubImmerseAiPort, ImmerseContentMother updated)
- [ ] Project compiles with `./gradlew compileJava`

---

### Phase 3: Compact Immerse Prompts and Shorter Vocab Schema

**Goal**: Reduce token consumption in immerse system prompts and tool schema descriptions.

**Files to modify**:
- `src/main/java/com/faus535/englishtrainer/immerse/infrastructure/ai/AnthropicImmerseAiAdapter.java` -- Compact system prompts and tool descriptions

**Details**:

1. **Compact `buildGenerateSystemPrompt()`** -- Shorten each content type prompt from ~80 words to ~30 words. Example for TEXT:
```java
case TEXT -> "You create English learning content. Generate an engaging article/story at the requested CEFR level with varied vocabulary. Extract key vocabulary and generate exercises.";
```
Similar compression for AUDIO and VIDEO.

2. **Compact tool schema descriptions** -- Shorten verbose descriptions:
   - `"An engaging title for the content"` -> `"Content title"`
   - `"The content with annotations and highlights"` -> `"Content text"`
   - `"Detected CEFR level: a1,a2,b1,b2,c1,c2"` -> `"CEFR level"`
   - `"A long description of exercise type"` -> `"MULTIPLE_CHOICE|FILL_THE_GAP|TRUE_FALSE|WORD_DEFINITION"`
   - Shorten `exampleSentence` description in tool schema to `"Example"` (keep Java field name unchanged per Decision #1)

3. **Compact `buildProcessContentTool()`** descriptions similarly:
   - `"The original text with annotations"` -> `"Annotated text"`
   - `"Detected CEFR level: a1,a2,b1,b2,c1,c2"` -> `"CEFR level"`
   - `"Process text for English language learning"` -> `"Process English text for learners"`

4. **Compact processContent system prompt**:
   - From: `"You process English text for language learners. Extract vocabulary, annotate difficulty, and generate exercises."`
   - To: `"Process English text for learners. Extract vocabulary, annotate difficulty, generate exercises."`

**Acceptance criteria**:
- [ ] System prompts are under 40 words each
- [ ] Tool schema descriptions are concise (under 5 words each where possible)
- [ ] No functional change to AI output structure
- [ ] All existing tests pass
- [ ] Project compiles with `./gradlew compileJava`

---

### Phase 4: Talk Module Minor Optimizations

**Goal**: Reduce token consumption in the talk evaluate() and summarize() calls.

**Files to modify**:
- `src/main/java/com/faus535/englishtrainer/talk/infrastructure/ai/AnthropicTalkAiAdapter.java` -- Compact evaluate tool schema, reduce evaluate max_tokens, compress summarize context

**Details**:

1. **Compact `buildEvaluationTool()` descriptions**:
   - `"Grammar accuracy 0-100"` -> `"0-100"`
   - `"Vocabulary range 0-100"` -> `"0-100"`
   - `"Fluency score 0-100"` -> `"0-100"`
   - `"Task/topic completion 0-100"` -> `"0-100"`
   - `"Overall score 0-100"` -> `"0-100"`
   - `"CEFR level: a1,a2,b1,b2,c1,c2"` -> `"CEFR level"`
   - `"Evaluate a student's English conversation performance"` -> `"Evaluate English conversation"`

2. **Reduce evaluate max_tokens** from 500 to 350.

3. **Compact evaluate system prompt**:
   - From: `"You are an English tutor evaluator. Evaluate the student's performance."`
   - To: `"Evaluate the student's English conversation."`

4. **Compress summarize() conversation format** -- Already compact (`msg.role().charAt(0) + ":" + content`). No further optimization needed here.

**Acceptance criteria**:
- [ ] Evaluate tool descriptions are compact (single property type hints)
- [ ] Evaluate max_tokens is 350
- [ ] Evaluate system prompt is under 10 words
- [ ] All existing talk tests pass
- [ ] Project compiles with `./gradlew compileJava`

---

### Phase 5: Validation

**Goal**: Verify all changes compile, tests pass, and architecture conventions are followed.

**Files to modify**: None

**Details**:
1. Run `./gradlew compileJava` to verify compilation
2. Run `./gradlew test` to verify all tests pass
3. Run `/revisar` to validate architecture and naming conventions
4. Fix any issues found

**Acceptance criteria**:
- [ ] `./gradlew compileJava` succeeds
- [ ] `./gradlew test` passes all tests
- [ ] `/revisar` reports no critical issues
- [ ] No regressions in existing functionality

## API Contract

No API endpoint changes. The response shape of `ImmerseContentResponse` remains identical. The only behavioral change is that `processedText` field will now always be non-null for generated content (previously it could theoretically be null with rawText as backup).

## Database Changes

### V9.7.0__immerse_nullable_raw_text.sql
```sql
ALTER TABLE immerse_content ALTER COLUMN raw_text DROP NOT NULL;
```

**Deploy order**: Migration MUST run before application code deploy. The new code inserts null for raw_text on the generate path, which would violate the NOT NULL constraint if migration has not run.

**Rollback**: `ALTER TABLE immerse_content ALTER COLUMN raw_text SET NOT NULL;` (only safe if no null rows exist).

## Testing Strategy

### New Tests
- `ImmerseContentSizingTest` -- 5 unit tests covering all level lookups, null/unknown defaults, boundary values

### Modified Tests
- `GenerateImmerseContentUseCaseTest` -- Update `worksWithoutTopicOrLevel()` to assert rawText is null for generated content
- `StubImmerseAiPort` -- Update ImmerseGenerateResult constructor (no rawText)
- `ImmerseContentMother` -- Update `generated()` and `generatedAudio()` factory methods to new signature

### Unchanged Tests
- `SubmitImmerseContentUseCaseTest` -- Submit path is unaffected (still uses rawText)
- `ImmerseContentTest` -- reconstitute/markProcessed still accept rawText
- All talk tests -- Talk changes are prompt-only, no signature changes
