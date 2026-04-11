# Backend Plan: Talk Grammar & Vocabulary Feedback

> Generated: 2026-04-11
> Request: Add grammar correction and vocabulary feedback to talk conversation summaries
> Slug: 2026_04_11-talk-grammar-feedback

## Decisions Log

| # | Decision | Rationale |
|---|----------|-----------|
| D1 | `GrammarNote` and `VocabItem` as Java records in `talk/domain/` | Consistent with other value objects (TalkCorrection, TalkEvaluation are records). No setters. |
| D2 | Grammar/vocab analysis at read time in `GetTalkConversationSummaryUseCase`, NOT at end-time | Avoids blocking `EndTalkConversationUseCase`. Summaries requested occasionally. Allows retry. |
| D3 | Grammar/vocab data persisted in `talk_conversations` as TEXT columns | Cached after first compute — AI not called on every summary request. |
| D4 | AI call uses tool_use with `analyze_grammar_and_vocabulary` Anthropic tool | Consistent with `evaluate_talk` and `process_content` patterns. Guarantees parseable JSON. |
| D5 | `TalkAiPort` gets `analyzeGrammarAndVocabulary()` method | Single AI gateway; delegates to `AnthropicTalkAiAdapter`. |
| D6 | Migration uses `TEXT` (not JSONB) | Consistent with `evaluation_json TEXT` already in `talk_conversations`. |
| D7 | `TalkConversation.withGrammarFeedback()` returns new immutable instance | Follows established pattern: `end()`, `addMessage()` return new instances. |
| D8 | Only FULL mode conversations get grammar/vocab analysis | QUICK conversations have compact summary structure — grammar analysis adds noise. |
| D9 | `StubTalkAiPort` gets default stubs for new method | All existing tests compile without modification. |
| D10 | Grammar analysis targets user messages only | AI messages are English-correct by design. |

## Context

- **Reference module**: talk (article and immerse for AI call patterns)
- **Modules affected**: talk

The talk module supports FULL and QUICK conversation modes. When FULL ends, `EndTalkConversationUseCase` calls AI for summary + evaluation. The summary endpoint reads stored data. This feature adds grammar correction notes and new vocabulary items to the FULL summary — lazily computed on first summary fetch, then persisted.

## Public Contracts

### New Domain Records

```java
// talk/domain/GrammarNote.java
public record GrammarNote(String originalText, String correction, String explanation) {}

// talk/domain/VocabItem.java
public record VocabItem(String word, String definition, String usedInContext) {}
```

### TalkAiPort additions

```java
// GrammarFeedback nested record
record GrammarFeedback(List<GrammarNote> grammarNotes, List<VocabItem> newVocabulary) {}

// New method
GrammarFeedback analyzeGrammarAndVocabulary(List<TalkMessage> userMessages) throws TalkAiException;
```

### TalkConversation new fields + method

```java
private final List<GrammarNote> grammarNotes;   // null = not yet analyzed
private final List<VocabItem> newVocabulary;    // null = not yet analyzed

// Updated reconstitute() — adds grammarNotes, newVocabulary before startedAt:
public static TalkConversation reconstitute(
    TalkConversationId id, UUID userId, UUID scenarioId,
    TalkLevel level, ConversationMode mode, TalkStatus status,
    String summary, TalkEvaluation evaluation,
    List<GrammarNote> grammarNotes, List<VocabItem> newVocabulary,
    Instant startedAt, Instant endedAt, List<TalkMessage> messages)

// New method:
public TalkConversation withGrammarFeedback(List<GrammarNote> grammarNotes, List<VocabItem> newVocabulary)

// New predicate:
public boolean hasGrammarFeedback()
```

### FullSummaryResult extended

```java
record FullSummaryResult(
    String summary, TalkEvaluation evaluation, int turnCount, int errorCount,
    List<GrammarNote> grammarNotes, List<VocabItem> newVocabulary
) implements TalkConversationSummaryResult {}
```

### Migration

`V13.0.0__talk_conversations_add_grammar_feedback.sql`

### REST Response (same endpoint, extended)

`GET /api/talk/conversations/{id}/summary` — FULL mode response:
```json
{
  "mode": "FULL",
  "summary": "...",
  "evaluation": { ... },
  "turnCount": 8,
  "errorCount": 2,
  "grammarNotes": [
    { "originalText": "I goed to the store", "correction": "I went to the store", "explanation": "'Go' is irregular; past tense is 'went'." }
  ],
  "newVocabulary": [
    { "word": "negotiate", "definition": "To discuss something in order to reach an agreement.", "usedInContext": "They had to negotiate the terms." }
  ]
}
```

## Analysis

### Key Existing Files
- `TalkConversation` — immutable aggregate, all state changes return new instances
- `TalkAiPort` — domain port with `chat`, `summarize`, `evaluate`, `quickSummarize` methods
- `AnthropicTalkAiAdapter` — already uses tool_use pattern for `evaluate()`
- `GetTalkConversationSummaryUseCase` — pure read; returns sealed `TalkConversationSummaryResult`
- `TalkConversationEntity` — has `evaluationJson TEXT` for JSON storage
- `TalkConversationSummaryResult` — sealed interface with `FullSummaryResult` and `QuickSummaryResult`

### Key Risks
- `reconstitute()` has many callers (tests, entity mapper) — new params must be backward-compatible
- `GetTalkConversationSummaryUseCase` becomes transactional (now writes to DB)
- Constructor change requires `TalkConversationMother` updates in ALL factories

### Existing Tests Impacted
- `TalkConversationMother` — ALL `reconstitute()` calls need `null, null` added before `startedAt`
- `StubTalkAiPort` — needs `analyzeGrammarAndVocabulary()` implementation
- `GetTalkConversationSummaryUseCaseTest` — constructor injection changes (adds `TalkAiPort`)
- `TalkConversationTest` — add tests for new predicate and method

---

## Phases

### Phase 1: Domain — Records + Aggregate Extension + Migration + Object Mothers + Tests [M]

**Goal**: Domain correctly models enrichment fields. Migration written. Tests compile. No AI logic yet.

**Files to create**:
- `src/main/java/com/faus535/englishtrainer/talk/domain/GrammarNote.java`
- `src/main/java/com/faus535/englishtrainer/talk/domain/VocabItem.java`
- `src/main/resources/db/migration/V13.0.0__talk_conversations_add_grammar_feedback.sql`

**Files to modify**:
- `src/main/java/com/faus535/englishtrainer/talk/domain/TalkConversation.java`
- `src/main/java/com/faus535/englishtrainer/talk/domain/TalkAiPort.java`
- `src/main/java/com/faus535/englishtrainer/talk/application/TalkConversationSummaryResult.java`
- `src/main/java/com/faus535/englishtrainer/talk/infrastructure/persistence/TalkConversationEntity.java`
- `src/test/java/com/faus535/englishtrainer/talk/domain/TalkConversationMother.java`
- `src/test/java/com/faus535/englishtrainer/talk/infrastructure/StubTalkAiPort.java`

**Details**:

**`GrammarNote.java`**:
```java
package com.faus535.englishtrainer.talk.domain;
import java.util.Objects;

public record GrammarNote(String originalText, String correction, String explanation) {
    public GrammarNote {
        Objects.requireNonNull(originalText, "originalText must not be null");
        Objects.requireNonNull(correction, "correction must not be null");
        Objects.requireNonNull(explanation, "explanation must not be null");
    }
}
```

**`VocabItem.java`**:
```java
package com.faus535.englishtrainer.talk.domain;
import java.util.Objects;

public record VocabItem(String word, String definition, String usedInContext) {
    public VocabItem {
        Objects.requireNonNull(word, "word must not be null");
        Objects.requireNonNull(definition, "definition must not be null");
        Objects.requireNonNull(usedInContext, "usedInContext must not be null");
    }
}
```

**`TalkAiPort.java`** additions:
```java
record GrammarFeedback(List<GrammarNote> grammarNotes, List<VocabItem> newVocabulary) {}
GrammarFeedback analyzeGrammarAndVocabulary(List<TalkMessage> userMessages) throws TalkAiException;
```

**`TalkConversation.java`** — add fields `grammarNotes`, `newVocabulary`; update constructor; update all existing internal factory methods to propagate new fields; add `withGrammarFeedback()` and `hasGrammarFeedback()`:
```java
public boolean hasGrammarFeedback() { return grammarNotes != null; }

public TalkConversation withGrammarFeedback(List<GrammarNote> grammarNotes, List<VocabItem> newVocabulary) {
    return new TalkConversation(id, userId, scenarioId, level, mode, status, summary, evaluation,
            grammarNotes, newVocabulary, startedAt, endedAt, messages);
}
```

`start()` factory: pass `null, null` for new fields.

**`FullSummaryResult`** — add `grammarNotes` and `newVocabulary` fields.

**Migration `V13.0.0__talk_conversations_add_grammar_feedback.sql`**:
```sql
ALTER TABLE talk_conversations
    ADD COLUMN IF NOT EXISTS grammar_notes TEXT,
    ADD COLUMN IF NOT EXISTS vocabulary_used TEXT;
```
NULL = "not yet analyzed". Empty JSON array `[]` = "analyzed, found nothing".

**`TalkConversationEntity.java`** — add `grammarNotesJson TEXT`, `vocabularyUsedJson TEXT` columns; add `serializeList()` / `deserializeList()` helpers; update `fromAggregate()`, `toAggregate()`, `updateFrom()`.

**`TalkConversationMother.java`** — update ALL `reconstitute()` calls to pass `null, null` before `startedAt`; add `completedWithGrammarFeedback()` factory.

**`StubTalkAiPort.java`** — add `analyzeGrammarAndVocabulary()` returning deterministic stub data.

**Acceptance criteria**:
- [x] `./gradlew compileJava` passes
- [x] All existing talk tests still pass
- [x] `TalkConversation.withGrammarFeedback(...)` returns unmodifiable list
- [x] `start()` → `hasGrammarFeedback() == false`
- [x] `completedWithGrammarFeedback()` → `hasGrammarFeedback() == true`
- [x] Verify compilation
- [x] Verify tests pass
- [ ] Human review
- [ ] Commit: `feat(talk): add GrammarNote, VocabItem domain records and TalkConversation grammar fields`

---

### Phase 2: Application — Enhanced Use Case + AI Adapter + Tests [L]

**Goal**: Implement `analyzeGrammarAndVocabulary()` in adapter. Enhance `GetTalkConversationSummaryUseCase` with lazy grammar analysis. Write tests.

**Files to modify**:
- `src/main/java/com/faus535/englishtrainer/talk/infrastructure/ai/AnthropicTalkAiAdapter.java`
- `src/main/java/com/faus535/englishtrainer/talk/application/GetTalkConversationSummaryUseCase.java`
- `src/main/java/com/faus535/englishtrainer/talk/infrastructure/controller/GetTalkConversationSummaryController.java`
- `src/test/java/com/faus535/englishtrainer/talk/application/GetTalkConversationSummaryUseCaseTest.java`

**Details**:

**`AnthropicTalkAiAdapter.java`** — add `TOOL_ANALYZE_GRAMMAR` constant (Anthropic tool with `grammarNotes[]` and `newVocabulary[]` schema), add `analyzeGrammarAndVocabulary()` implementation following the existing `evaluate()` tool-use pattern. System prompt targets only user messages; max 5 grammar notes + 5 vocab items. Max tokens: 600.

Anthropic tool schema summary:
```json
{
  "name": "analyze_grammar_and_vocabulary",
  "input_schema": {
    "grammarNotes": [{ "originalText": "...", "correction": "...", "explanation": "..." }],
    "newVocabulary": [{ "word": "...", "definition": "...", "usedInContext": "..." }]
  }
}
```

**`GetTalkConversationSummaryUseCase.java`** — inject `TalkAiPort`; add `@Transactional` (now writes); lazy analysis logic:
```java
@Transactional
public TalkConversationSummaryResult execute(UUID conversationIdValue)
        throws TalkConversationNotFoundException, TalkAiException {
    // ... load conversation ...
    if (conversation.mode() == ConversationMode.QUICK) {
        return deserializeQuickSummary(conversation.summary());
    }
    if (!conversation.hasGrammarFeedback()) {
        List<TalkMessage> userMessages = conversation.messages().stream()
                .filter(m -> "user".equals(m.role())).toList();
        TalkAiPort.GrammarFeedback feedback = talkAiPort.analyzeGrammarAndVocabulary(userMessages);
        conversation = conversation.withGrammarFeedback(feedback.grammarNotes(), feedback.newVocabulary());
        repository.save(conversation);
    }
    return new TalkConversationSummaryResult.FullSummaryResult(
            conversation.summary(), conversation.evaluation(),
            conversation.messages().size(), conversation.errorCount(),
            conversation.grammarNotes() != null ? conversation.grammarNotes() : List.of(),
            conversation.newVocabulary() != null ? conversation.newVocabulary() : List.of());
}
```

**`GetTalkConversationSummaryController.java`** — update `FullSummaryResponse` record to include `grammarNotes` and `newVocabulary`; add to switch arm mapping.

**`GetTalkConversationSummaryUseCaseTest.java`** — inject `StubTalkAiPort` alongside repository. Test methods:
- `shouldReturnFullSummaryWithGrammarFeedbackWhenFeedbackAlreadyPersisted()`
- `shouldAnalyzeAndPersistGrammarFeedbackWhenNotYetComputed()`
- `shouldNotCallAiWhenGrammarFeedbackAlreadyPersisted()` — uses `CountingStubTalkAiPort` inner class
- `shouldReturnQuickSummaryWhenModeIsQuick()`
- `shouldThrowNotFoundWhenConversationDoesNotExist()`
- `shouldReturnEmptyGrammarNotesWhenUserMessagesAreEmpty()`

```java
// CountingStubTalkAiPort — inner class in test
private static class CountingStubTalkAiPort extends StubTalkAiPort {
    private int analyzeCount = 0;
    @Override
    public GrammarFeedback analyzeGrammarAndVocabulary(List<TalkMessage> messages) throws TalkAiException {
        analyzeCount++;
        return super.analyzeGrammarAndVocabulary(messages);
    }
    int analyzeCallCount() { return analyzeCount; }
}
```

**Acceptance criteria**:
- [x] `./gradlew test --tests "*.talk.*"` all green
- [x] `shouldAnalyzeAndPersistGrammarFeedbackWhenNotYetComputed` confirms AI called + result persisted
- [x] `shouldNotCallAiWhenGrammarFeedbackAlreadyPersisted` confirms no redundant AI call
- [x] `shouldReturnQuickSummaryWhenModeIsQuick` confirms QUICK path unchanged
- [x] `shouldThrowNotFoundWhenConversationDoesNotExist` passes
- [x] Verify compilation
- [x] Verify tests pass
- [ ] Human review
- [ ] Commit: `feat(talk): add lazy grammar and vocabulary analysis to conversation summary`

---

### Phase 3: /revisar + fixes [S]

**Goal**: Run `/revisar`, fix violations.

**Expected convention checks**:
- `GrammarNote`, `VocabItem` — no Spring/JPA imports (pure records)
- Exceptions are checked (extend `Exception`)
- `@UseCase` on `GetTalkConversationSummaryUseCase` preserved
- Controller is package-private
- Constructor injection (no `@Autowired` on fields)
- Flyway format: `V13.0.0__talk_conversations_add_grammar_feedback.sql`
- Getters without `get` prefix

**Acceptance criteria**:
- [ ] `/revisar` reports no violations
- [ ] `./gradlew test` full suite passes
- [ ] Verify compilation
- [ ] Human review
- [ ] Commit: `fix(talk): revisar fixes`

---

## API Contract

### `GET /api/talk/conversations/{id}/summary`
Auth: JWT | Controller: `GetTalkConversationSummaryController` (unchanged route)

FULL response:
```json
{
  "mode": "FULL",
  "summary": "You practiced ordering food at a restaurant...",
  "evaluation": { "grammarAccuracy": 80, "vocabularyRange": 70, "fluency": 75,
    "taskCompletion": 85, "overallScore": 78, "levelDemonstrated": "b1",
    "strengths": ["Good use of past tense"], "areasToImprove": ["Expand vocabulary"] },
  "turnCount": 8,
  "errorCount": 2,
  "grammarNotes": [
    { "originalText": "I goed to the store yesterday", "correction": "I went to the store yesterday",
      "explanation": "'Go' is an irregular verb. Its simple past tense is 'went', not 'goed'." }
  ],
  "newVocabulary": [
    { "word": "negotiate", "definition": "To have a discussion in order to reach an agreement.",
      "usedInContext": "We had to negotiate the price before buying the car." }
  ]
}
```

QUICK response: unchanged (no `grammarNotes`/`newVocabulary`).

Errors:
- 404: `TALK_CONVERSATION_NOT_FOUND`
- 500: `TALK_AI_ERROR` (from `TalkAiException`)

---

## Database Changes

File: `src/main/resources/db/migration/V13.0.0__talk_conversations_add_grammar_feedback.sql`
```sql
ALTER TABLE talk_conversations
    ADD COLUMN IF NOT EXISTS grammar_notes TEXT,
    ADD COLUMN IF NOT EXISTS vocabulary_used TEXT;
```

- `TEXT` (not JSONB) — consistent with `evaluation_json TEXT`
- `NULL` = not yet computed; `[]` = analyzed, nothing found
- No backfill — existing conversations trigger analysis on first summary request

---

## Testing Strategy

| Layer | File | Type | Key Tests |
|-------|------|------|-----------|
| Domain | `TalkConversationTest` (extend) | Unit | `withGrammarFeedback()`, `hasGrammarFeedback()`, immutability |
| Application | `GetTalkConversationSummaryUseCaseTest` | Unit | 6 test methods above |
| AI port | `AnthropicTalkAiAdapter` | Manual/IT | Verify with real Anthropic key |

## Next Step

Execute Phase 1:
```
/execute-plan .ai/plans/2026_04_11-talk-grammar-feedback-backend.md
```
