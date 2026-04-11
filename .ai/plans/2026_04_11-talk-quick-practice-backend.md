# Backend Plan: Talk Quick Practice Mode

> Generated: 2026-04-11
> Request: Add quick 5-minute conversation challenges with auto-end and compact summary

## Decisions Log

| # | Topic | Decision | Alternatives Considered | Why |
|---|-------|----------|------------------------|-----|
| 1 | QUICK mode limit | 6 exchanges (3 user + 3 AI) | 4, 8 | ~5 minutes at natural pace |
| 2 | QuickSummary storage | JSON string in existing `summary` TEXT column | New table | Avoids migration, summary column already nullable TEXT |
| 3 | SummaryResult | Sealed interface (FullSummaryResult | QuickSummaryResult) | Conditional fields | Type-safe, clear contract |
| 4 | QuickChallenge seeding | Constant class with 15 challenges | DB table | No admin UI needed, deterministic, easy to change |
| 5 | Daily selection | Deterministic by date (day-of-year % 15 picks 3) | Random | Same challenges all day for a user |

## Context

- Reference module: `talk` — existing `TalkConversation`, `SendTalkMessageUseCase`
- Modules affected: `talk`

## Public Contracts

### Use Cases

```java
// Modified (new overload or updated method)
StartTalkConversationUseCase.execute(UUID userId, UUID scenarioId, ConversationMode mode) → UUID conversationId

// Modified (auto-end when QUICK limit reached)
SendTalkMessageUseCase.execute(UUID conversationId, UUID userId, String message) → TalkMessageResult

// Sealed interface result
sealed interface TalkConversationSummaryResult permits FullSummaryResult, QuickSummaryResult {}
GetTalkConversationSummaryUseCase.execute(UUID conversationId, UUID userId) → TalkConversationSummaryResult

// New
ListQuickChallengesUseCase.execute(LocalDate date) → List<QuickChallenge>
```

### Domain Events

None new.

### Domain Exceptions

None new.

### Database Schema

```sql
-- V12.0.0
ALTER TABLE talk_conversations ADD COLUMN mode VARCHAR(10) NOT NULL DEFAULT 'FULL';
```

### REST Endpoints

```
GET /api/talk/quick-challenges   → ListQuickChallengesController
POST /api/talk/conversations     (existing, new ?mode=QUICK param)
```

## Analysis

**Existing files relevant to this feature:**
- `talk/domain/TalkConversation.java` — aggregate root, messages list, `end()` method
- `talk/domain/vo/` — `TalkConversationId`, `TalkMessage`, `TalkScenarioId`
- `talk/application/SendTalkMessageUseCase.java` — orchestrates AI call + saves message
- `talk/application/StartTalkConversationUseCase.java` — creates conversation + first AI turn
- `talk/application/EndTalkConversationUseCase.java` — produces summary
- `talk/application/GetTalkConversationSummaryUseCase.java` — returns summary DTO
- `talk/infrastructure/StartTalkConversationController.java` — POST /api/talk/conversations
- `talk/infrastructure/TalkConversationEntity.java` — has `summary` column (TEXT)
- `talk/infrastructure/AnthropicTalkAiAdapter.java` — `chat()` method, prompt builders

**Existing tests impacted:**
- `TalkConversationTest` — add mode + isAtQuickLimit tests
- `SendTalkMessageUseCaseTest` — add auto-end for QUICK mode
- `StartTalkConversationUseCaseTest` — add mode param test
- `GetTalkConversationSummaryUseCaseTest` — add QuickSummaryResult branch test

## Phases

### Phase 1: ConversationMode + migration + TalkConversation domain [M]

**Goal**: `TalkConversation` tracks mode. `isAtQuickLimit()` detects when QUICK mode should auto-end.

**Files to create/modify**:
- `src/main/java/com/faus535/englishtrainer/talk/domain/vo/ConversationMode.java` — new enum: FULL, QUICK
- `src/main/java/com/faus535/englishtrainer/talk/domain/TalkConversation.java` — add `mode` field + `isAtQuickLimit()` method
- `src/main/java/com/faus535/englishtrainer/talk/infrastructure/TalkConversationEntity.java` — add `mode` column
- `src/main/resources/db/migration/V12.0.0__talk_conversations_add_mode.sql` — migration
- `src/main/java/com/faus535/englishtrainer/talk/application/StartTalkConversationUseCase.java` — accept `ConversationMode` parameter
- `src/main/java/com/faus535/englishtrainer/talk/infrastructure/StartTalkConversationController.java` — add `mode` to request body
- `src/test/java/com/faus535/englishtrainer/talk/domain/TalkConversationTest.java` — add mode tests
- `src/test/java/com/faus535/englishtrainer/talk/domain/TalkConversationMother.java` — add `quickMode()` builder
- `src/test/java/com/faus535/englishtrainer/talk/application/StartTalkConversationUseCaseTest.java` — extend

**Details**:
- `ConversationMode` enum: `FULL, QUICK`
- `TalkConversation.isAtQuickLimit()`: `return mode == ConversationMode.QUICK && userMessages().size() >= 3` (3 user exchanges = 6 total messages)
- `userMessages()` helper: filters `messages` where role = USER
- `StartTalkConversationUseCase.execute(UUID userId, UUID scenarioId, ConversationMode mode)` — creates conversation with mode
- Controller request body: add optional `"mode": "QUICK"` field, default `FULL`
- `TalkConversationEntity`: add `@Column("mode") private String mode` with `DEFAULT 'FULL'`

**Test method names**:
- `TalkConversationTest.isAtQuickLimit_returnsFalse_whenFullMode()`
- `TalkConversationTest.isAtQuickLimit_returnsFalse_whenQuickModeUnder3UserMessages()`
- `TalkConversationTest.isAtQuickLimit_returnsTrue_whenQuickModeWith3UserMessages()`
- `StartTalkConversationUseCaseTest.execute_startsConversationWithQuickMode()`

**Acceptance criteria**:
- [x] Migration `V12.0.0` runs without error
- [x] `TalkConversation.isAtQuickLimit()` returns true only in QUICK mode at 3 user messages
- [x] `POST /api/talk/conversations` accepts `{ "scenarioId": "...", "mode": "QUICK" }`
- [x] Verify compilation
- [x] Verify tests pass
- [ ] Human review
- [ ] Commit

---

### Phase 2: Auto-end QUICK mode + QuickSummary [M]

**Goal**: When QUICK mode limit is reached, conversation auto-ends and generates a compact summary.

**Files to create/modify**:
- `src/main/java/com/faus535/englishtrainer/talk/domain/port/TalkAiPort.java` — add `QuickSummary` inner record
- `src/main/java/com/faus535/englishtrainer/talk/infrastructure/AnthropicTalkAiAdapter.java` — add `quickSummarize()` method
- `src/main/java/com/faus535/englishtrainer/talk/application/SendTalkMessageUseCase.java` — add auto-end branch
- `src/main/java/com/faus535/englishtrainer/talk/application/GetTalkConversationSummaryUseCase.java` — return sealed interface
- `src/main/java/com/faus535/englishtrainer/talk/domain/TalkConversationSummaryResult.java` — sealed interface + records
- `src/test/java/com/faus535/englishtrainer/talk/application/SendTalkMessageUseCaseTest.java` — add auto-end test
- `src/test/java/com/faus535/englishtrainer/talk/application/GetTalkConversationSummaryUseCaseTest.java` — extend

**Details**:

`QuickSummary` in `TalkAiPort`:
```java
public record QuickSummary(
    boolean taskCompleted,
    List<String> top3Corrections,    // ["original → corrected"]
    String encouragementNote
) {}
```

`TalkConversationSummaryResult`:
```java
public sealed interface TalkConversationSummaryResult
    permits FullSummaryResult, QuickSummaryResult {}

public record FullSummaryResult(/* existing summary fields */) implements TalkConversationSummaryResult {}
public record QuickSummaryResult(boolean taskCompleted, List<String> top3Corrections, String encouragementNote)
    implements TalkConversationSummaryResult {}
```

`SendTalkMessageUseCase.execute()`:
- After saving AI response, check `conversation.isAtQuickLimit()`
- If true: call `talkAiPort.quickSummarize(conversation)` → `QuickSummary`
- Serialize `QuickSummary` to JSON, store in `conversation.summary` via `conversation.end(corrections)`
- Store mode=QUICK in entity, mark conversation as ENDED

`GetTalkConversationSummaryUseCase`:
- If `conversation.mode() == FULL` → deserialize existing summary → `FullSummaryResult`
- If `conversation.mode() == QUICK` → deserialize JSON from `summary` column → `QuickSummaryResult`

`AnthropicTalkAiAdapter.quickSummarize()`: new system prompt:
```
"Given this short conversation: [messages]. Answer in JSON: 
{ 'taskCompleted': true/false, 'top3Corrections': ['original → corrected'], 'encouragementNote': '...' }"
```

**Test method names**:
- `SendTalkMessageUseCaseTest.execute_autoEndsAndGeneratesQuickSummary_whenQuickLimitReached()`
- `GetTalkConversationSummaryUseCaseTest.execute_returnsQuickSummaryResult_whenModeIsQuick()`
- `GetTalkConversationSummaryUseCaseTest.execute_returnsFullSummaryResult_whenModeIsFull()`

**Acceptance criteria**:
- [ ] After 3rd user message in QUICK mode, conversation is auto-ended with quick summary
- [ ] `GET /api/talk/conversations/{id}/summary` returns `QuickSummaryResult` for QUICK conversations
- [ ] Quick summary JSON persisted in `summary` column
- [ ] Verify compilation
- [ ] Verify tests pass
- [ ] Human review
- [ ] Commit

---

### Phase 3: QuickChallenge catalog + endpoint [M]

**Goal**: 15 seeded quick challenges available. Daily 3 challenges selected deterministically. New GET endpoint.

**Files to create/modify**:
- `src/main/java/com/faus535/englishtrainer/talk/domain/vo/QuickChallengeDifficulty.java` — enum: EASY, MEDIUM, HARD
- `src/main/java/com/faus535/englishtrainer/talk/domain/vo/QuickChallengeCategory.java` — enum: SHOPPING, SOCIAL, TRAVEL, PROFESSIONAL, PROBLEM_SOLVING
- `src/main/java/com/faus535/englishtrainer/talk/domain/QuickChallenge.java` — record VO
- `src/main/java/com/faus535/englishtrainer/talk/domain/QuickChallenges.java` — constant class with 15 seeded challenges
- `src/main/java/com/faus535/englishtrainer/talk/application/ListQuickChallengesUseCase.java` — picks daily 3
- `src/main/java/com/faus535/englishtrainer/talk/infrastructure/ListQuickChallengesController.java` — GET endpoint
- `src/test/java/com/faus535/englishtrainer/talk/application/ListQuickChallengesUseCaseTest.java` — new test
- `src/test/java/com/faus535/englishtrainer/talk/domain/QuickChallengeMother.java` — Object Mother

**Details**:

`QuickChallenge` record:
```java
public record QuickChallenge(
    String id,                         // stable string ID e.g. "order-coffee"
    String title,
    String description,
    QuickChallengeDifficulty difficulty,
    QuickChallengeCategory category
) {}
```

`QuickChallenges.ALL` — 15 entries:
```java
public static final List<QuickChallenge> ALL = List.of(
    new QuickChallenge("order-coffee", "Order a Coffee", "Order your favourite drink at a café", EASY, SHOPPING),
    new QuickChallenge("buy-groceries", "Buy Groceries", "Ask for items and check out", EASY, SHOPPING),
    new QuickChallenge("phone-appointment", "Make a Phone Appointment", "Call to schedule a doctor visit", MEDIUM, SOCIAL),
    new QuickChallenge("talk-weekend", "Talk About Your Weekend", "Chat casually about recent activities", EASY, SOCIAL),
    new QuickChallenge("describe-home", "Describe Your Home", "Walk someone through your living space", EASY, SOCIAL),
    new QuickChallenge("ask-directions", "Ask for Directions", "Find your way in an unfamiliar area", EASY, TRAVEL),
    new QuickChallenge("hotel-checkin", "Check In at a Hotel", "Handle check-in process", MEDIUM, TRAVEL),
    new QuickChallenge("restaurant-order", "Order at a Restaurant", "Full ordering interaction", EASY, SHOPPING),
    new QuickChallenge("billing-error", "Resolve a Billing Error", "Dispute a charge politely", HARD, PROBLEM_SOLVING),
    new QuickChallenge("neighbour-problem", "Explain a Problem to a Neighbour", "Address a noise/parking issue", MEDIUM, SOCIAL),
    new QuickChallenge("return-product", "Return a Faulty Product", "Handle a shop return", MEDIUM, PROBLEM_SOLVING),
    new QuickChallenge("delayed-flight", "Deal with a Delayed Flight", "Negotiate at the airport desk", HARD, TRAVEL),
    new QuickChallenge("room-upgrade", "Negotiate a Room Upgrade", "Ask for a better room politely", HARD, TRAVEL),
    new QuickChallenge("new-job-intro", "Introduce Yourself at a New Job", "First day small talk", MEDIUM, PROFESSIONAL),
    new QuickChallenge("propose-idea", "Propose an Idea in a Meeting", "Present a suggestion confidently", HARD, PROFESSIONAL)
);
```

Daily selection: `int offset = LocalDate.now().getDayOfYear() % (ALL.size() - 3 + 1); return ALL.subList(offset, offset + 3);`

`ListQuickChallengesController`: `GET /api/talk/quick-challenges`, no auth required (public endpoint), returns list of 3 challenges.

**Test method names**:
- `ListQuickChallengesUseCaseTest.execute_returnsThreeChallenges()`
- `ListQuickChallengesUseCaseTest.execute_returnsSameChallengesForSameDay()`
- `ListQuickChallengesUseCaseTest.execute_returnsDifferentChallengesForDifferentDays()`

**Acceptance criteria**:
- [ ] `GET /api/talk/quick-challenges` returns exactly 3 challenges
- [ ] Same date always returns same 3 challenges (deterministic)
- [ ] Each challenge has title, description, difficulty, category
- [ ] Verify compilation
- [ ] Verify tests pass
- [ ] Human review
- [ ] Commit

---

## API Contract

### `GET /api/talk/quick-challenges`
- **Request body**: none
- **Response body**:
```json
[
  {
    "id": "order-coffee",
    "title": "Order a Coffee",
    "description": "Order your favourite drink at a café",
    "difficulty": "EASY",
    "category": "SHOPPING"
  }
]
```
- **Status codes**: 200
- **Auth**: Public (no JWT required)

### `POST /api/talk/conversations`
- **Request body**: `{ "scenarioId": "uuid-or-null", "mode": "QUICK", "challengeId": "order-coffee" }`
- **Response body**: `{ "conversationId": "uuid" }`
- **Status codes**: 201
- **Auth**: Bearer JWT required

### `GET /api/talk/conversations/{id}/summary`
- **Response body (QUICK)**:
```json
{
  "mode": "QUICK",
  "taskCompleted": true,
  "top3Corrections": ["I want coffee → I would like a coffee", "How much is? → How much is it?"],
  "encouragementNote": "Great job! Your politeness was excellent."
}
```
- **Auth**: Bearer JWT required

## Database Changes

```sql
-- V12.0.0__talk_conversations_add_mode.sql
ALTER TABLE talk_conversations ADD COLUMN mode VARCHAR(10) NOT NULL DEFAULT 'FULL';
```

## Testing Strategy

- Phase 1: Domain unit tests for `isAtQuickLimit()`, Object Mother
- Phase 2: Use case tests verifying auto-end branch triggers at correct message count
- Phase 3: Use case test with deterministic daily selection, verifying same input date = same output

## Next step

Execute Phase 1: ConversationMode + migration + TalkConversation domain
