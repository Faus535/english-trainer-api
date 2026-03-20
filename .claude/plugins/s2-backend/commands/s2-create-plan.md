---
name: s2-create-plan
description: Creates an implementation plan with vertical phases and public contracts
model: opus
---

# Command: /s2-create-plan

Creates an implementation plan for a task, organized in vertical phases where each phase delivers end-to-end functionality.

## Usage

```bash
/s2-create-plan
```

## Process

1. **Ask** the user for the task to plan.
   It's important to ask about the **public contracts** to consider. If the user does not provide them, suggest them based on the task description. Do the same for the implementation phases.

   We understand public contracts as:
   - Application services (Use Cases) to add, modify, or remove, and their method signatures
   - Domain events to add, modify, or remove, and their attributes
   - Test suites to add, modify, or remove, and the test cases within each
   - Database schemas to add, modify, or remove, and the tables within each
   - REST endpoints to add, modify, or remove

2. **Propose** the plan to the user for approval. IMPORTANT: Do not start creating the plan until the user has accepted the contracts and phases.

3. **Save** the plan in a new file inside the `.ai/plans` directory with the current date and a semantic name. Example: `2026_01_29-create_user_module.md`.

4. **Prepare git branch**:
   - Verify there are no uncommitted changes (`git status`). If there are, warn the user and ask them to resolve them before continuing.
   - Update develop: `git checkout develop && git pull origin develop`
   - Suggest a branch name based on the plan following the convention `feature/#TICKET_brief-description` (e.g., `feature/#PROJ-123_create-user-module`). Ask the user for the final name.
   - Create the branch: `git checkout -b <branch-name>`

5. **Recommend next steps**: **CRITICAL: Do NOT execute the plan and do NOT ask the user if they want to execute it. The conversation MUST end here.** The user needs to run `/clear` first to free context window space before execution. Show the next steps as follows:
   ```
   ✅ Plan saved in .ai/plans/2026_01_29-create_user_module.md
   ✅ Branch feature/#PROJ-123_create-user-module created from develop

   To start execution, run:
     /clear
     /s2-execute-plan .ai/plans/2026_01_29-create_user_module.md
   ```

## Plan sections

### Goal
- Short and concise. 1-3 sentences summarizing the objective.

### Context
- List relevant files, folders, and code.
- Link files to the actual repository code.
- Read CLAUDE.md and referenced documentation to understand the architecture and conventions.
- Consult relevant s2-backend skills:
  - **s2-backend:architecture** - Package structure
  - **s2-backend:domain-design** - Domain patterns
  - **s2-backend:persistence** - Persistence
  - **s2-backend:api-design** - Controllers
  - **s2-backend:error-handling** - Exceptions and ControllerAdvice
  - **s2-backend:testing** - Testing strategy
  - **s2-backend:modulith-usecases** - Modulith style (or other depending on project)
  - **s2-backend:security** - Authentication and authorization
  - **s2-backend:logging** - Logging and health checks

### Phases
- Each phase is a **vertical slice** of the task
- Avoid creating the controller in one phase and the use case in another
- **MANDATORY**: Each phase must include its corresponding tests (unit and/or integration). NEVER create a phase without its tests.
- **MANDATORY**: Each phase must end with: verify compilation, verify tests pass, human review, and commit. This cycle is non-negotiable.
- Each phase must contain its description and list of actions (checkboxes)
- Split into as many phases as needed to facilitate review and merge
- Do not mix responsibilities in the same phase
- It must be possible to commit and push each phase without breaking the build
- **The last phase must include running `/s2-review`** to validate architecture, naming conventions, and code quality across the entire plan.

### Next step
- A single sentence indicating the next phase to complete.

## Example

```bash
/s2-create-plan
```

```
What task do you want to create the plan for?
> Create a user module with full CRUD

Proposed public contracts:
- Use Cases: CreateUserUseCase, GetUserByIdUseCase, FindAllUsersUseCase, UpdateUserUseCase, DeleteUserUseCase
- Events: UserCreatedEvent, UserUpdatedEvent, UserDeletedEvent
- Tests: CreateUserUseCaseTest, GetUserByIdUseCaseTest, ...
- Schema: users table (id, name, email, status, created_at)
- Endpoints: POST /users, GET /users/{id}, GET /users, PUT /users/{id}, DELETE /users/{id}

Agreed? Adjust as needed.
> OK

Proposed phases:
1. Create user — domain (User, VOs, UserCreatedEvent, UserRepository) + app (CreateUserUseCase, UserDto) + infra (UserEntity, UserRepositoryImpl, migration) + API (CreateUserController, UserControllerAdvice) + tests (CreateUserUseCaseTest, UserRepositoryIT, CreateUserControllerIT)
2. Get user by ID — GetUserByIdUseCase + GetUserByIdController + tests
3. List users with pagination — FindAllUsersUseCase + FindAllUsersController + tests
4. Update user — UpdateUserUseCase + UpdateUserController + tests
5. Delete user — DeleteUserUseCase + DeleteUserController + tests

Agreed?
> OK

Checking git status... ✅ No pending changes
Updating develop... ✅ develop updated

Suggested branch name: feature/#PROJ-123_create-user-module
Do you want to use this name or prefer another?
> OK

✅ Plan saved in .ai/plans/2026_01_29-create_user_module.md
✅ Branch feature/#PROJ-123_create-user-module created from develop

To start execution, run:
  /clear
  /s2-execute-plan .ai/plans/2026_01_29-create_user_module.md
```

## References

- Skills: architecture, domain-design, persistence, api-design, testing, modulith-usecases
