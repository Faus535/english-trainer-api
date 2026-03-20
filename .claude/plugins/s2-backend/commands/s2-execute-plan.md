---
name: s2-execute-plan
description: Executes the current phase of the implementation plan
model: sonnet
---

# Command: /s2-execute-plan

Executes the current phase of an existing implementation plan.

## Usage

```bash
/s2-execute-plan                    # Executes the most recent plan
/s2-execute-plan <path-to-plan>     # Executes a specific plan
```

## Process

IMPORTANT: Only perform the actions of the current phase.

### Start: Clean context

Ignore any previous conversation context (e.g., discussions from `/s2-create-plan`). The only source of truth is the plan file in `.ai/plans/`. ALWAYS read the plan from disk, do not rely on what is in the conversation context.

### Pre-execution: Git checks

Before executing any phase, verify:

1. **Correct branch**: Check that we are NOT on `develop`, `master`, or `main`. If on one of these branches, warn the user and do NOT continue until they move to a feature branch.
2. **Branch up to date**: Ensure the current branch is up to date with the base branch (`git pull origin <base-branch>`). If not, ask the user to update before continuing.
3. **Pending changes**: Verify there are no uncommitted changes from a previous phase (`git status`). If there are, ask the user to commit or discard them before continuing.

### Phase execution

1. **Locate** the plan. If not specified, search `.ai/plans/` for the most recent plan.
2. **Read** the plan and locate the current phase (indicated in "Next step").
3. **Consult** the relevant s2-backend skills for the phase:
   - **s2-backend:architecture** - If creating packages or modules
   - **s2-backend:domain-design** - If implementing domain
   - **s2-backend:persistence** - If implementing persistence
   - **s2-backend:api-design** - If implementing controllers
   - **s2-backend:error-handling** - If implementing exceptions or ControllerAdvice
   - **s2-backend:testing** - If writing tests
   - **s2-backend:modulith-usecases** - If implementing Use Cases
   - **s2-backend:security** - If implementing authentication or authorization
   - **s2-backend:logging** - If implementing logging or health checks
4. **Execute** the actions of the current phase following the skill patterns.
5. **Mark** the completed checkboxes in the plan file.

### Post-phase: Verification, review, and commit

7. **Verify compilation**: Run `./gradlew build` (or equivalent). If it fails, fix the errors before continuing.
8. **Verify tests**: Ensure all tests pass. If any test fails, fix it before continuing.
9. **Show summary** of changes with `git status`.
10. **Request user review**: Present the changes and wait for approval. Do NOT continue until the user confirms the changes are correct.
11. **Commit**: Once the user approves, suggest a descriptive commit message and ask for confirmation to commit. Do NOT commit automatically.
12. **Update** the "Next step" section with the next phase.

The complete per-phase cycle is: **Execute → Compile → Tests → Human review → Commit**. Do not advance to the next phase without completing this cycle.

### Last phase: Integration flow

When the **last phase** of the plan is completed, execute the integration flow:

1. **Run `/s2-review`** on the entire plan's code.
2. **Request user validation** of the review result. Do NOT continue until the user confirms.
3. **Run `/s2-generate-api-docs`** to generate updated API documentation for the frontend team.
4. **Request user validation** of the generated documentation. Do NOT continue until the user confirms.

## Example

### Intermediate phase

```bash
/s2-execute-plan .ai/plans/2026_01_29-create_user_module.md
```

```
📄 Plan: 2026_01_29-create_user_module.md — Phase 1: Domain Layer
Execute?
> Yes

🔍 Checking git status...
✅ Current branch: feature/#PROJ-123_create-user-module
✅ No pending changes

Executing Phase 1: Domain Layer...

✅ User.java (Aggregate Root)
✅ UserName.java (Value Object)
✅ UserEmail.java (Value Object)
✅ UserStatus.java (Enum)
✅ UserRepository.java (Interface)
✅ UserCreatedEvent.java (Domain Event)
✅ UserUpdatedEvent.java (Domain Event)
✅ UserDeletedEvent.java (Domain Event)
✅ package-info.java (Events)

🔨 Verifying compilation... ✅ BUILD SUCCESSFUL
🧪 Verifying tests... ✅ All tests passed

📋 Changes summary (git status):
  new file: src/main/java/com/example/user/domain/User.java
  new file: src/main/java/com/example/user/domain/UserName.java
  ...

Review the changes. Are they correct to proceed?
> Yes

Suggested message: feat(user): add domain layer - aggregate, VOs, events, repository interface
Commit?
> Yes

✅ Commit done.
Next phase: Phase 2 - Application Layer + Unit Tests
```

### Last phase (integration flow)

```
✅ Phase 4 completed.

Running full /s2-review...
📊 SCORE: 95/100 (1 minor WARNING)

Do you approve the review result to continue?
> Yes

Running /s2-generate-api-docs...
📄 API documentation generated in docs/api/

Do you approve the generated documentation to continue?
> Yes

✅ Plan completed. You can now merge and push the branch manually.
```

## Notes

- Do NOT execute actions from future phases
- **Mandatory per-phase cycle**: Execute → Compile → Tests → Human review → Commit. Do not skip any step.
- If compilation or tests fail, fix them BEFORE requesting user review
- Each phase must be independently committable
- NEVER commit automatically - always ask for user confirmation
- NEVER work directly on develop, master, or main
- The full `/s2-review` is only run on the last phase, not on intermediate phases
- Merge and push are the responsibility of the human — do NOT offer to do them
- Ignore previous conversation context — always read the plan from disk
