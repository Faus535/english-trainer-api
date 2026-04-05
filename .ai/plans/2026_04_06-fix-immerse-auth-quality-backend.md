# Backend Plan: Fix code quality issues in immerse, auth, and remaining modules

> Generated: 2026-04-06
> Request: Fix code quality issues in immerse and auth modules, plus minor fixes in review, user, activity, gamification, and home modules

## Decisions Log

| # | Topic | Decision | Alternatives Considered | Why |
|---|-------|----------|------------------------|-----|
| 1 | JwtService/JwtAuthenticationFilter visibility | Stay `public` | Make package-private | SecurityConfig in `shared.infrastructure.config` holds typed field references to both classes -- different package, compilation would break |
| 2 | ReviewItemEntity.updateFrom() | Keep as-is | Refactor to immutable pattern | Acceptable JPA/persistence-layer pattern, no domain leak |
| 3 | GoogleUserInfo location | Move to domain as `GoogleVerifiedUser` record | Keep in infrastructure | UseCase currently imports infrastructure class; domain record breaks the dependency inversion violation |
| 4 | GetActivityDatesController | Keep as single controller | Split into two controllers | Pragmatic; avoids API contract change, single responsibility is not violated enough to justify split |
| 5 | DeleteUserProfileController `final` | Remove `final` keyword | Keep final | Project convention: controllers are package-private, not final. Spring proxying can be affected by final |
| 6 | RefreshTokenController business logic | Extract to `RefreshTokenUseCase` | Keep in controller | Controller currently has token rotation, hashing, and transaction management -- violates thin-controller convention |
| 7 | LogoutUserUseCase annotation | Change `@Service` to `@UseCase`, remove `public final` | Keep @Service | All other use cases use @UseCase; consistency matters |

## Analysis

### What exists

**Immerse module** (8 use cases):
- All 8 use cases have `public` class and constructor declarations. Convention requires package-private (Spring 4.3+ uses reflection for single-constructor injection).
- Only 3 tests exist: `GenerateImmerseContentUseCaseTest`, `SubmitImmerseContentUseCaseTest`, `SubmitExerciseAnswerUseCaseTest`.
- 5 use cases lack tests: `GetImmerseContentUseCase`, `GetImmerseExercisesUseCase`, `GetImmerseHistoryUseCase`, `GetImmerseVocabularyUseCase`, `GetSuggestedImmerseContentUseCase`.
- Test infrastructure: `InMemoryImmerseContentRepository`, `InMemoryImmerseExerciseRepository`, `InMemoryImmerseSubmissionRepository`, `StubImmerseAiPort`, `ImmerseContentMother`, `ImmerseExerciseMother`.

**Auth module** (10 use cases):
- `LogoutUserUseCase`: uses `@Service` instead of `@UseCase`, class is `public final`.
- `GoogleLoginUseCase`: imports `GoogleTokenVerifier` (infrastructure) and `GoogleUserInfo` (infrastructure). Violates dependency inversion.
- `RefreshTokenController`: contains all token rotation logic, SHA-256 hashing, `@Transactional`. Should be a thin delegator.
- `GoogleTokenVerifier`: does not implement a domain port; use case depends directly on infrastructure class.
- `JwtService` and `JwtAuthenticationFilter`: public, used cross-package by `SecurityConfig`. Must stay public.
- Missing tests: `ChangePasswordUseCaseTest`, `ForgotPasswordUseCaseTest`, `ResetPasswordUseCaseTest`, `DeleteAccountUseCaseTest`, `GetCurrentUserUseCaseTest`, `RefreshTokenUseCaseTest`.
- Missing test infra: `InMemoryPasswordResetTokenRepository`, `StubEmailPort`.

**Review module**: `ReviewCompletedEvent` is a plain record, not annotated with `@DomainEvent`. This is fine since Spring Data JDBC `@DomainEvent` is for aggregate-root-level event publishing (which ReviewItem already handles via `registerEvent`). No change needed.

**User module**: `DeleteUserProfileController` is `final`. Should be non-final per convention.

**Home module**: `GetHomeUseCase` has `public` constructor, no test.

**Activity module**: `GetActivityDatesUseCase`, `GetActivityCalendarUseCase`, `GetStreakUseCase` -- all have `public` constructors, no tests.

**Gamification module**: `GetAllAchievementsUseCase`, `GetUserAchievementsUseCase`, `GetXpLevelUseCase` -- all have `public` constructors, no tests.

**Review module**: `GetReviewStatsUseCase` has `public` constructor, no test.

## Phases

### Phase 1: Fix immerse module visibility + add missing tests

**Goal**: Make all 8 immerse use cases package-private and add 5 missing unit tests.

**Files to modify**:
- `src/main/java/com/faus535/englishtrainer/immerse/application/GenerateImmerseContentUseCase.java` -- remove `public` from class and constructor
- `src/main/java/com/faus535/englishtrainer/immerse/application/GetImmerseContentUseCase.java` -- remove `public` from class and constructor
- `src/main/java/com/faus535/englishtrainer/immerse/application/GetImmerseExercisesUseCase.java` -- remove `public` from class and constructor
- `src/main/java/com/faus535/englishtrainer/immerse/application/GetImmerseHistoryUseCase.java` -- remove `public` from class and constructor
- `src/main/java/com/faus535/englishtrainer/immerse/application/GetImmerseVocabularyUseCase.java` -- remove `public` from class and constructor
- `src/main/java/com/faus535/englishtrainer/immerse/application/GetSuggestedImmerseContentUseCase.java` -- remove `public` from class and constructor
- `src/main/java/com/faus535/englishtrainer/immerse/application/SubmitExerciseAnswerUseCase.java` -- remove `public` from class and constructor
- `src/main/java/com/faus535/englishtrainer/immerse/application/SubmitImmerseContentUseCase.java` -- remove `public` from class and constructor

**Files to create**:
- `src/test/java/com/faus535/englishtrainer/immerse/application/GetImmerseContentUseCaseTest.java`
- `src/test/java/com/faus535/englishtrainer/immerse/application/GetImmerseExercisesUseCaseTest.java`
- `src/test/java/com/faus535/englishtrainer/immerse/application/GetImmerseHistoryUseCaseTest.java`
- `src/test/java/com/faus535/englishtrainer/immerse/application/GetImmerseVocabularyUseCaseTest.java`
- `src/test/java/com/faus535/englishtrainer/immerse/application/GetSuggestedImmerseContentUseCaseTest.java`

**Details**:

1. For each of the 8 use case files, change `public class XxxUseCase` to `class XxxUseCase` and `public XxxUseCase(` to `XxxUseCase(`. The `public` on `execute()` methods stays -- controllers in the same package call them.

2. **GetImmerseContentUseCaseTest**: Use `InMemoryImmerseContentRepository` and `ImmerseContentMother`. Tests:
   - `returnsContentWhenFound` -- save content, execute with its ID, assert returned.
   - `throwsImmerseContentNotFoundExceptionWhenMissing` -- execute with random UUID, expect `ImmerseContentNotFoundException`.

3. **GetImmerseExercisesUseCaseTest**: Use `InMemoryImmerseContentRepository` + `InMemoryImmerseExerciseRepository`. Tests:
   - `returnsExercisesForProcessedContent` -- save PROCESSED content + exercises, execute, assert list.
   - `throwsImmerseContentNotProcessedExceptionWhenPending` -- save PENDING content, execute, expect `ImmerseContentNotProcessedException`.
   - `throwsImmerseContentNotFoundExceptionWhenMissing` -- execute with random UUID.

4. **GetImmerseHistoryUseCaseTest**: Tests:
   - `returnsPagedHistory` -- save multiple contents for same user, execute with page/size, assert ordering.
   - `returnsEmptyListForUnknownUser` -- execute with random UUID, assert empty.

5. **GetImmerseVocabularyUseCaseTest**: Tests:
   - `returnsVocabularyForExistingContent` -- save content with vocabulary, execute, assert list.
   - `throwsImmerseContentNotFoundExceptionWhenMissing`.

6. **GetSuggestedImmerseContentUseCaseTest**: Tests:
   - `returnsLatestContentForUser` -- save multiple, assert latest returned.
   - `returnsEmptyOptionalForUnknownUser`.

**Acceptance criteria**:
- [x] All 8 use case classes stay public (cross-package controller usage), constructors are package-private
- [x] All 8 use case constructors are package-private
- [x] 5 new test classes pass
- [x] Existing 3 immerse tests still pass
- [x] Project compiles (`./gradlew compileJava`)

---

### Phase 2: Fix auth module (annotations, GoogleAuthPort, RefreshTokenUseCase)

**Goal**: Fix `LogoutUserUseCase` annotation, extract `GoogleAuthPort` domain interface, create `RefreshTokenUseCase`, add missing tests and test infrastructure.

**Files to modify**:
- `src/main/java/com/faus535/englishtrainer/auth/application/LogoutUserUseCase.java` -- change `@Service` to `@UseCase`, change `public final class` to `class`, change `public LogoutUserUseCase(` to `LogoutUserUseCase(`, change `public void execute(` to `void execute(`
- `src/main/java/com/faus535/englishtrainer/auth/application/GoogleLoginUseCase.java` -- change field type from `GoogleTokenVerifier` to `GoogleAuthPort`, change constructor parameter type, change import from `infrastructure.google.GoogleTokenVerifier` to `domain.GoogleAuthPort`, change import from `infrastructure.google.GoogleUserInfo` to `domain.GoogleVerifiedUser`, update all references to `GoogleUserInfo` to `GoogleVerifiedUser`
- `src/main/java/com/faus535/englishtrainer/auth/infrastructure/google/GoogleTokenVerifier.java` -- add `implements GoogleAuthPort`, change return type of `verify()` to `GoogleVerifiedUser`, update import
- `src/main/java/com/faus535/englishtrainer/auth/infrastructure/controller/RefreshTokenController.java` -- strip to thin delegator: inject only `RefreshTokenUseCase`, delegate to `useCase.execute(request.refreshToken())`, map result to response. Remove all business logic, `@Transactional`, direct repo/jwt imports.
- `src/main/java/com/faus535/englishtrainer/auth/infrastructure/controller/GoogleLoginController.java` -- if it references `GoogleUserInfo`, update to `GoogleVerifiedUser`

**Files to create**:
- `src/main/java/com/faus535/englishtrainer/auth/domain/GoogleAuthPort.java` -- interface with `GoogleVerifiedUser verify(String idToken) throws GoogleAuthException`
- `src/main/java/com/faus535/englishtrainer/auth/domain/GoogleVerifiedUser.java` -- `record GoogleVerifiedUser(String email, String name, boolean emailVerified)`
- `src/main/java/com/faus535/englishtrainer/auth/application/RefreshTokenUseCase.java` -- `@UseCase`, `@Transactional`, method `RefreshResult execute(String refreshToken)` containing all token rotation logic currently in `RefreshTokenController`. Inner record `RefreshResult(String accessToken, String refreshToken, String profileId, String email)`.
- `src/test/java/com/faus535/englishtrainer/auth/infrastructure/InMemoryPasswordResetTokenRepository.java` -- implements `PasswordResetTokenRepository` with `HashMap` store, tracks `save()` calls, implements `countRecentByUserId` returning count of non-used tokens.
- `src/test/java/com/faus535/englishtrainer/auth/infrastructure/StubEmailPort.java` -- implements `EmailPort`, captures `sendPasswordResetEmail` calls in a `List<EmailCall>` for test assertions.
- `src/test/java/com/faus535/englishtrainer/auth/infrastructure/StubGoogleAuthPort.java` -- implements `GoogleAuthPort`, returns configurable `GoogleVerifiedUser` or throws `GoogleAuthException`.
- `src/test/java/com/faus535/englishtrainer/auth/application/ChangePasswordUseCaseTest.java`
- `src/test/java/com/faus535/englishtrainer/auth/application/ForgotPasswordUseCaseTest.java`
- `src/test/java/com/faus535/englishtrainer/auth/application/ResetPasswordUseCaseTest.java`
- `src/test/java/com/faus535/englishtrainer/auth/application/DeleteAccountUseCaseTest.java`
- `src/test/java/com/faus535/englishtrainer/auth/application/GetCurrentUserUseCaseTest.java`
- `src/test/java/com/faus535/englishtrainer/auth/application/RefreshTokenUseCaseTest.java`

**Details**:

1. **GoogleAuthPort** (domain interface):
   ```java
   package com.faus535.englishtrainer.auth.domain;
   import com.faus535.englishtrainer.auth.domain.error.GoogleAuthException;
   public interface GoogleAuthPort {
       GoogleVerifiedUser verify(String idToken) throws GoogleAuthException;
   }
   ```

2. **GoogleVerifiedUser** (domain record):
   ```java
   package com.faus535.englishtrainer.auth.domain;
   public record GoogleVerifiedUser(String email, String name, boolean emailVerified) {}
   ```

3. **GoogleTokenVerifier**: Add `implements GoogleAuthPort`, change return type of `verify()` from `GoogleUserInfo` to `GoogleVerifiedUser`. The `GoogleUserInfo` infra record can be kept temporarily or deleted if no other class references it.

4. **GoogleLoginUseCase**: Replace `GoogleTokenVerifier` field with `GoogleAuthPort`, replace `GoogleUserInfo` references with `GoogleVerifiedUser`. This eliminates the infrastructure import.

5. **RefreshTokenUseCase**: Extract all logic from `RefreshTokenController.handle()` into `execute(String refreshToken)`. Include the `hashToken()` static utility. Inject `JwtService`, `AuthUserRepository`, `RefreshTokenRepository`. Throw checked exceptions (`InvalidCredentialsException` or a new `InvalidRefreshTokenException`) instead of returning `ResponseEntity.status(401)`. The use case returns a `RefreshResult` record or throws.

6. **RefreshTokenController**: Becomes a thin delegator. Injects only `RefreshTokenUseCase`. Maps `RefreshRequest` to use case call, maps `RefreshResult` to `AuthResponse`.

7. **LogoutUserUseCase**: `@Service` -> `@UseCase`, remove `public` and `final`.

8. **Test classes** -- follow existing patterns (see `GenerateImmerseContentUseCaseTest`):

   - **ChangePasswordUseCaseTest**: Use `InMemoryAuthUserRepository` + `BCryptPasswordEncoder`. Tests: `changesPasswordSuccessfully`, `throwsNotFoundForMissingUser`, `throwsInvalidCredentialsForWrongPassword`, `throwsAccountUsesGoogleForGoogleUser`.
   - **ForgotPasswordUseCaseTest**: Use `InMemoryAuthUserRepository` + `InMemoryPasswordResetTokenRepository` + `StubEmailPort`. Tests: `sendsResetEmailForValidUser`, `silentlySucceedsForMissingEmail`, `silentlySucceedsForGoogleAccount`, `throwsTooManyAttemptsAfterThreeRequests`.
   - **ResetPasswordUseCaseTest**: Tests: `resetsPasswordWithValidToken`, `throwsInvalidResetTokenForMissingToken`, `throwsInvalidResetTokenForExpiredToken`, `throwsInvalidResetTokenForUsedToken`.
   - **DeleteAccountUseCaseTest**: Use `InMemoryAuthUserRepository` + `InMemoryUserProfileRepository`. Tests: `deletesAccountWithCorrectPassword`, `deletesGoogleAccountWithoutPassword`, `throwsInvalidCredentialsForWrongPassword`, `throwsNotFoundForMissingUser`.
   - **GetCurrentUserUseCaseTest**: Tests: `returnsUserWhenFound`, `throwsNotFoundForMissingUser`.
   - **RefreshTokenUseCaseTest**: Tests: `refreshesTokenSuccessfully`, `throwsForInvalidJwt`, `throwsForRevokedToken`, `throwsForMissingUser`.

**Acceptance criteria**:
- [x] `LogoutUserUseCase` uses `@UseCase`, is public (cross-package), non-final, constructor package-private
- [x] `GoogleLoginUseCase` imports only domain types (no infrastructure imports)
- [x] `GoogleAuthPort` interface exists in `auth.domain`
- [x] `GoogleVerifiedUser` record exists in `auth.domain`
- [x] `GoogleTokenVerifier` implements `GoogleAuthPort`
- [x] `RefreshTokenUseCase` exists with `@UseCase` and `@Transactional`
- [x] `RefreshTokenController` is a thin delegator (no direct repo access, no `@Transactional`)
- [x] 6 new auth test classes pass
- [x] Existing auth tests (`RegisterUserUseCaseTest`, `LoginUserUseCaseTest`, `LogoutUserUseCaseTest`, `GoogleLoginUseCaseTest`) still pass
- [x] `GoogleLoginUseCaseTest` updated to use `StubGoogleAuthPort` instead of `GoogleTokenVerifier`
- [x] Project compiles (`./gradlew compileJava`)

---

### Phase 3: Fix review, user, activity, gamification, home modules + add missing tests

**Goal**: Fix remaining minor issues and add tests for all untested use cases.

**Files to modify**:
- `src/main/java/com/faus535/englishtrainer/user/infrastructure/controller/DeleteUserProfileController.java` -- remove `final` keyword from class declaration
- `src/main/java/com/faus535/englishtrainer/review/application/GetReviewStatsUseCase.java` -- remove `public` from class and constructor
- `src/main/java/com/faus535/englishtrainer/user/application/GetUserProfileUseCase.java` -- remove `public` from class and constructor
- `src/main/java/com/faus535/englishtrainer/activity/application/GetActivityDatesUseCase.java` -- remove `public` from class and constructor
- `src/main/java/com/faus535/englishtrainer/activity/application/GetActivityCalendarUseCase.java` -- remove `public` from class and constructor
- `src/main/java/com/faus535/englishtrainer/activity/application/GetStreakUseCase.java` -- remove `public` from class and constructor
- `src/main/java/com/faus535/englishtrainer/gamification/application/GetAllAchievementsUseCase.java` -- remove `public` from class and constructor
- `src/main/java/com/faus535/englishtrainer/gamification/application/GetUserAchievementsUseCase.java` -- remove `public` from class and constructor
- `src/main/java/com/faus535/englishtrainer/gamification/application/GetXpLevelUseCase.java` -- remove `public` from class and constructor
- `src/main/java/com/faus535/englishtrainer/home/application/GetHomeUseCase.java` -- remove `public` from class and constructor

**IMPORTANT**: `GetReviewStatsUseCase` is injected by `GetHomeUseCase` (same base package, different module). Verify that both are in the same compilation unit or that Spring can wire them. Since `GetHomeUseCase` is in `home.application` and `GetReviewStatsUseCase` is in `review.application`, package-private would **break injection** because they are in different packages. **Decision**: `GetReviewStatsUseCase` must stay `public` since it is used cross-module. Same analysis applies to any use case injected cross-module.

**Cross-module dependency check before making package-private**:
- `GetReviewStatsUseCase`: used by `GetHomeUseCase` (home module) -- MUST STAY PUBLIC
- `GetUserProfileUseCase`: check if used outside user module -- if not, make package-private
- `GetActivityDatesUseCase`, `GetActivityCalendarUseCase`, `GetStreakUseCase`: check if used outside activity module
- `GetAllAchievementsUseCase`, `GetUserAchievementsUseCase`, `GetXpLevelUseCase`: check if used outside gamification module
- `GetHomeUseCase`: check if used outside home module

**Action**: Before modifying visibility, grep for each class name to verify no cross-module usage. Only make package-private those that are used exclusively within their own module.

**Files to create**:
- `src/test/java/com/faus535/englishtrainer/review/application/GetReviewStatsUseCaseTest.java`
- `src/test/java/com/faus535/englishtrainer/user/application/GetUserProfileUseCaseTest.java`
- `src/test/java/com/faus535/englishtrainer/activity/application/GetActivityDatesUseCaseTest.java`
- `src/test/java/com/faus535/englishtrainer/activity/application/GetActivityCalendarUseCaseTest.java`
- `src/test/java/com/faus535/englishtrainer/activity/application/GetStreakUseCaseTest.java`
- `src/test/java/com/faus535/englishtrainer/gamification/application/GetAllAchievementsUseCaseTest.java`
- `src/test/java/com/faus535/englishtrainer/gamification/application/GetUserAchievementsUseCaseTest.java`
- `src/test/java/com/faus535/englishtrainer/gamification/application/GetXpLevelUseCaseTest.java`
- `src/test/java/com/faus535/englishtrainer/home/application/GetHomeUseCaseTest.java`

**Details**:

1. **DeleteUserProfileController**: Change `final class DeleteUserProfileController` to `class DeleteUserProfileController`.

2. **Visibility changes**: For each use case, verify no cross-module import exists before removing `public`. If cross-module usage is found, keep `public`.

3. **GetReviewStatsUseCaseTest**: Use `InMemoryReviewItemRepository` + `InMemoryReviewResultRepository`. Tests: `returnsStatsForUser`, `returnsZerosForUserWithNoItems`.

4. **GetUserProfileUseCaseTest**: Use `InMemoryUserProfileRepository` + `UserProfileMother`. Tests: `returnsProfileWhenFound`, `throwsUserProfileNotFoundExceptionWhenMissing`.

5. **GetActivityDatesUseCaseTest**: Use `InMemoryActivityDateRepository` + `ActivityDateMother`. Tests: `returnsSortedDates`, `returnsEmptyForUnknownUser`.

6. **GetActivityCalendarUseCaseTest**: Tests: `returnsDatesForSpecificMonth`, `returnsEmptyForMonthWithNoActivity`.

7. **GetStreakUseCaseTest**: Tests: `calculatesCurrentStreak`, `returnsZeroStreakForNoActivity`.

8. **GetAllAchievementsUseCaseTest**: Use `InMemoryAchievementRepository` + `AchievementMother`. Tests: `returnsAllAchievements`, `returnsEmptyListWhenNoAchievements`.

9. **GetUserAchievementsUseCaseTest**: Use `InMemoryUserAchievementRepository` + `UserAchievementMother`. Tests: `returnsUserAchievements`, `returnsEmptyListForUserWithNoAchievements`.

10. **GetXpLevelUseCaseTest**: Use `InMemoryUserProfileRepository` + `UserProfileMother`. Tests: `returnsXpLevelForUser`, `throwsUserProfileNotFoundExceptionWhenMissing`.

11. **GetHomeUseCaseTest**: Use `InMemoryTalkConversationRepository` + `InMemoryImmerseContentRepository` + mock/stub `GetReviewStatsUseCase`. Tests: `returnsSuggestedActionReviewWhenDueItemsExist`, `returnsSuggestedActionTalkWhenNoActiveConversation`, `returnsSuggestedActionImmerseAsDefault`.

**Acceptance criteria**:
- [ ] `DeleteUserProfileController` is non-final
- [ ] All use cases that are only used within their own module are package-private
- [ ] Use cases with cross-module usage remain `public` (e.g., `GetReviewStatsUseCase`)
- [ ] 9 new test classes pass
- [ ] All existing tests still pass
- [ ] Project compiles (`./gradlew compileJava`)

---

### Phase 4: Final validation

**Goal**: Run full test suite and architecture review.

**Details**:
1. Run `./gradlew build` to verify compilation and all tests pass.
2. Run `/revisar` to validate architecture conventions.
3. Verify no infrastructure imports in application/domain layers (except the approved `JwtService` public usage).
4. Verify no remaining `@Service` on use cases (all should use `@UseCase`).
5. Verify no `public` on use case classes/constructors that are module-internal.

**Acceptance criteria**:
- [ ] `./gradlew build` passes with 0 failures
- [ ] No `@Service` annotations on use case classes
- [ ] No infrastructure imports in application-layer use cases (except approved exceptions)
- [ ] `/revisar` reports no critical issues

## API Contract

### No API contract changes -- pure internal refactoring

## Database Changes

### No database changes

## Testing Strategy

- **Pattern**: All tests follow the existing project convention -- package-private test classes in the same package as the use case, `@BeforeEach` setUp with InMemory repositories and stubs, no mocking frameworks.
- **Infrastructure**: Reuse 13 existing InMemory repositories. Create 3 new test doubles: `InMemoryPasswordResetTokenRepository`, `StubEmailPort`, `StubGoogleAuthPort`.
- **Object Mothers**: Reuse existing 10 Object Mothers (`AuthUserMother`, `ImmerseContentMother`, `ImmerseExerciseMother`, `ReviewItemMother`, `ActivityDateMother`, `AchievementMother`, `UserAchievementMother`, `UserProfileMother`, `TalkConversationMother`, `TalkScenarioMother`).
- **Total new tests**: 20 test classes across phases 1-3.
- **Execution**: `./gradlew test` after each phase to ensure no regressions.
