# Plan: Return 204 when user has no learning path

> Date: 2026-03-27
> Scope: FULL_STACK
> Feature: Change GET /api/profiles/{profileId}/learning-status to return 204 (No Content) instead of 404 when the user has no LearningPath

## API Contract

```
GET /api/profiles/{profileId}/learning-status
  200 — User has learning path (body: LearningStatusResponse)
  204 — User has no learning path (no body)
  401 — Unauthorized
```

## Phase 1: Backend — Return 204 when no learning path

### Status: done

### Changes

#### 1. Modify `GetLearningStatusController`

**File**: `src/main/java/com/faus535/englishtrainer/learningpath/infrastructure/controller/GetLearningStatusController.java`

Change the `handle` method to catch `LearningPathNotFoundException` and return 204:

```java
@GetMapping("/api/profiles/{profileId}/learning-status")
@RequireProfileOwnership(pathVariable = "profileId")
ResponseEntity<LearningStatusResponse> handle(@PathVariable UUID profileId) {
    try {
        LearningStatus status = useCase.execute(new UserProfileId(profileId));
        return ResponseEntity.ok(toResponse(status));
    } catch (LearningPathNotFoundException e) {
        return ResponseEntity.noContent().build();
    }
}
```

Remove `throws LearningPathNotFoundException` from the method signature.

#### 2. Add/update unit test

**File**: `src/test/java/com/faus535/englishtrainer/learningpath/infrastructure/controller/GetLearningStatusControllerTest.java`

Test cases:
- `should_return_200_with_status_when_learning_path_exists`
- `should_return_204_when_no_learning_path`

### Verification
```bash
./gradlew compileJava compileTestJava
./gradlew test --tests "*GetLearningStatus*"
```

### Commit message
```
Return 204 instead of 404 when user has no learning path
```
