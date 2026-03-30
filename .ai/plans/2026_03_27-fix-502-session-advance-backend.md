# Backend Plan: Fix 502 Bad Gateway on Session Advance

> Generated: 2026-03-27
> Request: Fix 502 Bad Gateway on PUT /api/profiles/{id}/sessions/{sessionId}/blocks/0/advance — backend doesn't handle invalid UUID path variables gracefully

## Decisions Log

| ID | Decision | Alternatives Considered | Why |
|----|----------|------------------------|-----|
| CD1 | Error response: `{"code":"invalid_parameter","message":"Invalid value for parameter 'sessionId'"}` | Include submitted value in message | Parameter names are public (in URL); values could be used for enumeration |
| CD3 | Response includes parameter **name** only, never the **value** | Include ex.getValue() in body | Security: prevents information leakage; value logged at WARN level only |
| CD5 | Test named `InvalidUuidPathVariableIT` in shared infrastructure test package | `AdvanceBlockInvalidUuidIT` in session package | Fix is global (40+ endpoints), not session-specific |
| CD7 | Backend and frontend tracks can execute in parallel | Sequential backend-first | Frontend guards work independently of backend state |

## Analysis

### Existing code

- **`GlobalControllerAdvice.java`** (`src/main/java/com/faus535/englishtrainer/shared/infrastructure/error/GlobalControllerAdvice.java`): Handles `NotFoundException` (404), `AlreadyExistsException` (409), `MethodArgumentNotValidException` (422), `InvalidValueException` (400), `ProfileOwnershipException` (403), `ObjectOptimisticLockingFailureException` (409). Uses `ApiError` record (already defined). **Missing**: handler for `MethodArgumentTypeMismatchException`.
- **`SessionControllerAdvice.java`** (`src/main/java/com/faus535/englishtrainer/session/infrastructure/controller/SessionControllerAdvice.java`): Scoped to `com.faus535.englishtrainer.session`. Handles session-domain checked exceptions. Not relevant for this fix.
- **`AdvanceBlockController.java`**: Uses `@PathVariable UUID profileId` and `@PathVariable UUID sessionId`. When Spring can't convert `session-1774613567696` to UUID, throws `MethodArgumentTypeMismatchException` — unhandled → 502.

### Root cause

Spring's type conversion layer throws `MethodArgumentTypeMismatchException` (wrapping `IllegalArgumentException`) when a path variable string can't be converted to `UUID`. No `@ExceptionHandler` exists for this exception. The unhandled exception propagates past all `@ControllerAdvice` classes, and Railway proxies the resulting 500 as 502 Bad Gateway.

### Impact

~40+ endpoints across 15+ modules have `@PathVariable UUID` parameters. All share this vulnerability. One handler in `GlobalControllerAdvice` fixes all of them.

## Phases

### Phase 1: Add Global Exception Handler + Integration Test

**Goal**: Return 400 Bad Request (instead of 502) when any UUID path variable receives an invalid format. Verify with integration test.

**Files to modify**:
- `src/main/java/com/faus535/englishtrainer/shared/infrastructure/error/GlobalControllerAdvice.java` — add `MethodArgumentTypeMismatchException` handler

**Files to create**:
- `src/test/java/com/faus535/englishtrainer/shared/infrastructure/error/InvalidUuidPathVariableIT.java` — integration test

**Details**:

1. **Add import** to `GlobalControllerAdvice.java`:
   ```java
   import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
   ```

2. **Add handler method** (after existing `handleOptimisticLock`, before closing brace):
   ```java
   @ExceptionHandler(MethodArgumentTypeMismatchException.class)
   ResponseEntity<ApiError> handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
       log.warn("Type mismatch for parameter '{}': {}", ex.getName(), ex.getValue());
       return ResponseEntity.status(HttpStatus.BAD_REQUEST)
               .body(new ApiError("invalid_parameter",
                       "Invalid value for parameter '" + ex.getName() + "'"));
   }
   ```

3. **Create integration test** `InvalidUuidPathVariableIT.java`:
   - Extend `IntegrationTestBase`
   - Register a test user to get a valid JWT token and profileId
   - Send `PUT /api/profiles/{validProfileId}/sessions/not-a-uuid/blocks/0/advance` with valid auth
   - Assert HTTP 400
   - Assert response body `code` equals `"invalid_parameter"`

4. **Security notes**:
   - Response body: parameter name only (`sessionId`), never the submitted value
   - Logging: WARN level with both name and value (logs are internal-only)
   - No exception class names or stack traces in response

5. **Verification cycle**:
   - Run `./gradlew test` — all tests pass, zero regressions
   - Run `./gradlew test --tests "*InvalidUuidPathVariableIT*"` — new test passes
   - Human review: verify handler method placement and response format
   - Commit

**Acceptance criteria**:
- [x] `PUT /api/profiles/{id}/sessions/not-a-uuid/blocks/0/advance` returns HTTP 400 with `{"code":"invalid_parameter","message":"Invalid value for parameter 'sessionId'"}`
- [x] No existing tests break
- [x] WARN log line emitted with parameter name and invalid value
- [x] Response body does NOT contain the submitted invalid value, exception class, or stack trace

### Phase 2: Deploy + Final Validation

**Goal**: Deploy to production and validate the fix is live. Run architecture validation.

**Details**:

1. Commit changes with descriptive message
2. Push to remote: `git push`
3. Deploy: `railway up`
4. **Manual verification**: Call `PUT /api/profiles/{id}/sessions/not-a-uuid/blocks/0/advance` against production — should return 400, not 502
5. Run `/revisar` to validate architecture, naming conventions, and code quality

**Acceptance criteria**:
- [ ] Production returns 400 for invalid UUID path variables
- [ ] `/revisar` passes with no errors

## API Contract

### Changed behavior on ALL endpoints with `@PathVariable UUID`

**Before (broken)**:
```
HTTP 502 Bad Gateway
(Spring fails to deserialize non-UUID string, exception propagates unhandled)
```

**After (fixed)**:
```
HTTP 400 Bad Request
Content-Type: application/json

{
  "code": "invalid_parameter",
  "message": "Invalid value for parameter 'sessionId'"
}
```

- `code`: always `"invalid_parameter"` for type mismatch errors
- `message`: includes the `@PathVariable` name from the controller method signature
- Applies to any `MethodArgumentTypeMismatchException`, not only UUID mismatches

## Database Changes

None. This is a pure HTTP-adapter-layer fix.

## Testing Strategy

- **Integration test**: `InvalidUuidPathVariableIT` sends a real HTTP request with invalid UUID, verifies 400 response through the full Spring stack (security filters, controller advice, content negotiation)
- **Implicit coverage**: All 40+ endpoints with `@PathVariable UUID` are protected by the single global handler
- **No unit test needed**: The handler is a simple one-liner; the integration test provides sufficient confidence
