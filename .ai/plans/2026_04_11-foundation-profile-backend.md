# Backend Plan: Foundation & Profile Cleanup

> Generated: 2026-04-11
> Request: Deepen every module, create missing pieces, finish all modules (backend + frontend)

## Decisions Log

| # | Topic | Decision | Alternatives Considered | Why |
|---|-------|----------|------------------------|-----|
| 1 | EnglishLevel placement | Domain VO in `user/domain/vo/` | Shared enum | Per-module domain, follows existing pattern |
| 2 | StudySession storage | New aggregate + migration | Add column to user_profiles | Clean DDD, extensible |
| 3 | max-tokens fix | Raise to 1500 | 1000, 2000 | Avoids truncation, balanced cost |
| 4 | Speech stub removal | Delete controller + test | Leave as stub | Dead code removed, no frontend uses it |

## Context

- Reference module: `user` — most similar for profile-level changes
- Modules affected: `user`, `talk`, `shared` (config)

## Public Contracts

### Use Cases

```java
// Phase 1
UpdateEnglishLevelUseCase.execute(UUID userId, String englishLevel) throws UserNotFoundException

// Phase 2
RecordStudySessionUseCase.execute(UUID userId, String module, int durationSeconds) throws UserNotFoundException
```

### Domain Events

None new — phases are internal CRUD operations.

### Domain Exceptions

```java
UserNotFoundException extends Exception
InvalidEnglishLevelException extends Exception
```

### Database Schema

```sql
-- V11.6.0
ALTER TABLE user_profiles ADD COLUMN english_level VARCHAR(2);

-- V11.7.0
CREATE TABLE study_sessions (
  id UUID PRIMARY KEY,
  user_id UUID NOT NULL REFERENCES user_profiles(id) ON DELETE CASCADE,
  module VARCHAR(20) NOT NULL,
  duration_seconds INT NOT NULL,
  created_at TIMESTAMPTZ NOT NULL DEFAULT now()
);
CREATE INDEX idx_study_sessions_user_id ON study_sessions(user_id);
```

### REST Endpoints

```
PUT  /api/profiles/{id}/english-level   → UpdateEnglishLevelController
POST /api/profiles/{id}/sessions        → RecordStudySessionController
```

## Analysis

**Existing files relevant to this feature:**
- `user/domain/UserProfile.java` — aggregate root, has `xpPoints`, `level` fields
- `user/application/UpdateUserXpUseCase.java` — execute() pattern to follow
- `user/infrastructure/UserProfileJpaRepository.java` — existing repository
- `user/infrastructure/UserProfileController.java` — existing GET/PUT endpoints
- `talk/infrastructure/SubmitTalkSpeechController.java` — stub to DELETE
- `src/main/resources/application.properties` — `anthropic.max-tokens=300` to fix

**Existing tests impacted:**
- `UserProfileTest` — must add test for `updateEnglishLevel()`
- `UserProfileMother` — must add `withEnglishLevel()` builder
- `UpdateUserXpUseCaseTest` — no change needed

## Phases

### Phase 1: EnglishLevel VO + PUT endpoint [M]

**Goal**: Users can store and update their CEFR English level (A1–C2), persisted to DB.

**Files to create/modify**:
- `src/main/java/com/faus535/englishtrainer/user/domain/vo/EnglishLevel.java` — new enum VO
- `src/main/java/com/faus535/englishtrainer/user/domain/UserProfile.java` — add `englishLevel` field + `updateEnglishLevel()` method
- `src/main/java/com/faus535/englishtrainer/user/application/UpdateEnglishLevelUseCase.java` — new use case
- `src/main/java/com/faus535/englishtrainer/user/infrastructure/UpdateEnglishLevelController.java` — new controller
- `src/main/java/com/faus535/englishtrainer/user/infrastructure/UserProfileEntity.java` — add `englishLevel` column mapping
- `src/main/resources/db/migration/V11.6.0__user_add_english_level.sql` — migration
- `src/test/java/com/faus535/englishtrainer/user/domain/UserProfileTest.java` — add level tests
- `src/test/java/com/faus535/englishtrainer/user/application/UpdateEnglishLevelUseCaseTest.java` — new test
- `src/test/java/com/faus535/englishtrainer/user/domain/UserProfileMother.java` — add `withEnglishLevel()`

**Details**:
- `EnglishLevel` enum: `A1, A2, B1, B2, C1, C2` — implements `DomainEvent` is NOT needed, plain enum VO
- `UserProfile.updateEnglishLevel(EnglishLevel level)` returns new immutable instance
- `UpdateEnglishLevelUseCase`: `@Service`, package-private, `execute(UUID userId, EnglishLevel level)` — loads profile, calls `updateEnglishLevel()`, saves, no event published
- `UpdateEnglishLevelController`: `@RestController`, package-private, `PUT /api/profiles/{id}/english-level`, request body `{ "level": "B1" }`, validates with `@NotNull`, throws `UserNotFoundException → 404`
- `UserProfileEntity`: add `@Column("english_level") private String englishLevel` — mapped as String, converted to enum in domain
- Object Mother: `UserProfileMother.withEnglishLevel(EnglishLevel level)` static factory

**Test method names**:
- `UserProfileTest.updateEnglishLevel_storesNewLevel()`
- `UpdateEnglishLevelUseCaseTest.execute_updatesLevel_whenUserExists()`
- `UpdateEnglishLevelUseCaseTest.execute_throwsUserNotFoundException_whenUserNotFound()`

**Acceptance criteria**:
- [x] `PUT /api/profiles/{id}/english-level` returns 200 and persists level to DB
- [x] Invalid level (e.g. "X5") returns 400
- [x] Non-existent user returns 404
- [x] Verify compilation
- [x] Verify tests pass
- [ ] Human review
- [ ] Commit

---

### Phase 2: StudySession aggregate + POST endpoint [M]

**Goal**: Record time spent per module per session to enable analytics and smart suggestions.

**Files to create/modify**:
- `src/main/java/com/faus535/englishtrainer/user/domain/StudySession.java` — new aggregate root
- `src/main/java/com/faus535/englishtrainer/user/domain/vo/StudySessionId.java` — UUID VO record
- `src/main/java/com/faus535/englishtrainer/user/domain/vo/StudyModule.java` — enum: ARTICLE, IMMERSE, TALK, REVIEW
- `src/main/java/com/faus535/englishtrainer/user/domain/StudySessionRepository.java` — interface
- `src/main/java/com/faus535/englishtrainer/user/application/RecordStudySessionUseCase.java` — new use case
- `src/main/java/com/faus535/englishtrainer/user/infrastructure/StudySessionEntity.java` — JPA entity
- `src/main/java/com/faus535/englishtrainer/user/infrastructure/StudySessionJpaRepository.java` — Spring Data JPA
- `src/main/java/com/faus535/englishtrainer/user/infrastructure/StudySessionRepositoryJpa.java` — adapter
- `src/main/java/com/faus535/englishtrainer/user/infrastructure/RecordStudySessionController.java` — new controller
- `src/main/resources/db/migration/V11.7.0__user_create_study_sessions.sql` — migration
- `src/test/java/com/faus535/englishtrainer/user/application/RecordStudySessionUseCaseTest.java` — new test
- `src/test/java/com/faus535/englishtrainer/user/domain/StudySessionMother.java` — Object Mother

**Details**:
- `StudySession`: immutable record with `StudySessionId id`, `UUID userId`, `StudyModule module`, `int durationSeconds`, `Instant createdAt`
- `RecordStudySessionUseCase`: `@Service`, `execute(UUID userId, StudyModule module, int durationSeconds)` — validates user exists via `UserProfileRepository`, creates new `StudySession`, saves via `StudySessionRepository`
- `RecordStudySessionController`: `POST /api/profiles/{id}/sessions`, request body `{ "module": "ARTICLE", "durationSeconds": 600 }`, returns 201 Created
- Object Mother: `StudySessionMother.create()`, `StudySessionMother.forArticle(UUID userId)`

**Test method names**:
- `RecordStudySessionUseCaseTest.execute_savesSession_whenUserExists()`
- `RecordStudySessionUseCaseTest.execute_throwsUserNotFoundException_whenUserNotFound()`

**Acceptance criteria**:
- [x] `POST /api/profiles/{id}/sessions` returns 201
- [x] Session persisted to `study_sessions` table
- [x] Verify compilation
- [x] Verify tests pass
- [ ] Human review
- [ ] Commit

---

### Phase 3: Cleanup — remove speech stub + fix max-tokens [S]

**Goal**: Remove dead code and fix AI truncation bottleneck.

**Files to create/modify**:
- `src/main/java/com/faus535/englishtrainer/talk/infrastructure/SubmitTalkSpeechController.java` — DELETE
- `src/main/resources/application.properties` — change `anthropic.max-tokens=300` to `anthropic.max-tokens=1500`
- `src/test/java/com/faus535/englishtrainer/talk/...SpeechControllerTest.java` — DELETE if exists

**Details**:
- Grep for `SubmitTalkSpeechController` references before deleting
- Grep for `SpeechController` in test files
- Change exactly `anthropic.max-tokens=300` → `anthropic.max-tokens=1500`
- Run `/revisar` after

**Acceptance criteria**:
- [ ] No `SubmitTalkSpeechController` file exists
- [ ] `anthropic.max-tokens=1500` in application.properties
- [ ] All tests still pass (no compilation errors from removed controller)
- [ ] Verify compilation
- [ ] Verify tests pass
- [ ] Human review
- [ ] Commit

---

## API Contract

### `PUT /api/profiles/{id}/english-level`
- **Request body**: `{ "level": "B1" }`
- **Response body**: `204 No Content`
- **Status codes**: 204, 400, 404
- **Auth**: Bearer JWT required

### `POST /api/profiles/{id}/sessions`
- **Request body**: `{ "module": "ARTICLE", "durationSeconds": 600 }`
- **Response body**: `201 Created` (no body)
- **Status codes**: 201, 400, 404
- **Auth**: Bearer JWT required

## Database Changes

```sql
-- V11.6.0__user_add_english_level.sql
ALTER TABLE user_profiles ADD COLUMN english_level VARCHAR(2);

-- V11.7.0__user_create_study_sessions.sql
CREATE TABLE study_sessions (
    id          UUID        PRIMARY KEY,
    user_id     UUID        NOT NULL REFERENCES user_profiles(id) ON DELETE CASCADE,
    module      VARCHAR(20) NOT NULL,
    duration_seconds INT    NOT NULL,
    created_at  TIMESTAMPTZ NOT NULL DEFAULT now()
);
CREATE INDEX idx_study_sessions_user_id ON study_sessions(user_id);
CREATE INDEX idx_study_sessions_created_at ON study_sessions(created_at);
```

## Testing Strategy

- Phase 1: Unit tests for `UserProfile.updateEnglishLevel()` + use case with In-Memory repo
- Phase 2: Unit tests for use case, Object Mother for session creation
- Phase 3: Smoke test that compilation succeeds after deletion

## Next step

Execute Phase 1: EnglishLevel VO + PUT endpoint
