---
description: Execute a plan file phase by phase, then update snapshots, commit, push, and deploy
argument-hint: <path to plan .md file>
allowed-tools: Read, Glob, Grep, Write, Edit, Agent, Bash, AskUserQuestion
---

<!--
PURPOSE: Execute an implementation plan file, phase by phase
USAGE: /execute-plan .ai/plans/2026_03_26-feature-name-backend.md
OUTPUT: Implemented code, committed, pushed, and deployed
TOKEN OPTIMIZATION: Skills read once at startup, Gradle output truncated
-->

**Plan file**: $ARGUMENTS

---

## Step 0 — Load, Validate & Preload Skills

**0a. Read the plan file**: Read the file at the path provided in `$ARGUMENTS`.

- If the file does not exist → **STOP** and tell the user the file was not found.

**0b. Parse phases**: Extract all phases from the plan. For each phase identify:

- Phase number and name
- Goal
- Files to create/modify
- Implementation details
- Acceptance criteria (checkboxes)

**0c. Load project snapshot**: Read `.ai/project-snapshot.md` to understand the current project state.

- If it doesn't exist, warn the user but continue (the plan itself has enough context).

**0d. Check plan status**: Look for checked `[x]` checkboxes in the plan to determine which phases are already completed. Resume from the first incomplete phase.

**0e. Preload skills (once for the entire plan)**: Scan ALL phases to determine which skills are needed. Read each matched skill's `SKILL.md` + key `references/*.md` ONCE here — do NOT re-read them in each phase.

Skill mapping (read based on what ANY phase touches):

- Domain classes (aggregates, VOs, events, exceptions) → domain-design/
- Persistence (entities, repositories, migrations) → persistence/
- Controllers, endpoints, DTOs → api-design/
- Error handling, ControllerAdvice → error-handling/
- Tests → testing/
- Use cases, services → modulith-usecases/
- Auth, security, JWT → security/
- Logging, health → logging/
- Package structure, new module → architecture/

For **backend plans**: Read from `/home/faustinoolivas/dev/proyectos/carmen/english-trainer-api/.claude/plugins/s2-backend/skills/<skill>/`

For **frontend plans**: Read from `/home/faustinoolivas/dev/proyectos/carmen/english-trainer-web/.claude/skills/angular/` if they exist.

Show to the user:

```
Plan: <plan file name>
Total phases: N
Completed: X
Remaining: Y (starting from Phase Z)
Skills loaded: <list of skill names>
```

---

## Step 1 — Execute Phases (sequential loop)

For each incomplete phase, execute this cycle:

### 1a. Announce phase

```
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
Phase N: <Phase Name>
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
Goal: <phase goal>
```

### 1b. Implement

Execute all actions listed in the phase:

- Create/modify files as specified
- Follow the architecture conventions from the skills (already loaded in Step 0e)
- Write tests as specified in the phase (this is mandatory — never skip tests)
- **Do NOT re-read skills here** — they were preloaded in Step 0e

### 1c. Verify

Run the verification cycle — this is **non-negotiable**:

**For backend plans**:

```bash
./gradlew compileJava compileTestJava 2>&1 | tail -20
./gradlew test 2>&1 | tail -30
```

**For frontend plans**:

```bash
cd /home/faustinoolivas/dev/proyectos/carmen/english-trainer-web && npm run build 2>&1 | tail -20
cd /home/faustinoolivas/dev/proyectos/carmen/english-trainer-web && npm test 2>&1 | tail -30
```

- If compilation/build fails → read the FULL error output, fix the errors and retry
- If tests fail → read the FULL test output, fix the failing tests and retry
- Do NOT proceed to the next phase until both pass

### 1d. Update plan file

Mark the completed actions as done in the plan file by changing `- [ ]` to `- [x]` for each completed item.

### 1e. Commit the phase

```bash
git add <specific files created/modified in this phase>
git commit -m "<descriptive message for this phase>"
```

Use a descriptive commit message that matches the phase goal. Do NOT use `--amend`.

### 1f. Proceed to next phase

Move to the next incomplete phase and repeat from 1a.

---

## Step 2 — Push & Deploy

**2a. Push to GitHub**:

```bash
git push origin HEAD
```

**2b. Deploy to Railway**:

```bash
railway up --detach
```

**2c. Delete plan file**:
Remove the plan `.md` file that was executed, since it is now fully completed:

```bash
rm <plan file path>
git add <plan file path>
git commit -m "chore: remove completed plan file"
```

**2d. Final output**:

```
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
Plan execution complete
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
Plan: <plan file name>
Phases completed: N/N
Commits: <list of commit hashes and messages>
Pushed to: <branch name>
Deployed to: Railway
```

---

## Rules

### Execution

- Execute phases **sequentially** — never skip ahead
- **NEVER skip tests** — every phase must include and pass its tests
- **NEVER proceed** to the next phase if compilation or tests fail
- Always commit after each phase, not at the end
- Use specific file paths in `git add`, never `git add -A` or `git add .`

### Error handling

- If a phase fails to compile after 3 attempts → stop and ask the user for help
- If tests fail after 3 attempts → stop and show the failing test output to the user
- If the plan file references files or patterns that don't exist → adapt to the current codebase, don't blindly follow outdated instructions

### Skills consultation

- **Read skills ONCE in Step 0e** — never re-read them during phase execution
- Follow the patterns from the skills strictly (naming, structure, testing patterns)
- **Backend skills**: `.claude/plugins/s2-backend/skills/<skill>/`
- **Frontend skills**: `/home/faustinoolivas/dev/proyectos/carmen/english-trainer-web/.claude/skills/angular/`

### Plan file updates

- Always update the plan file checkboxes as you complete actions
- This allows resuming execution if the process is interrupted
- Use `/execute-plan <same-file>` to resume from where it left off

### Commit messages

- One commit per phase
- Message format: descriptive, matching the phase goal
- End with: `Co-Authored-By: Claude Opus 4.6 (1M context) <noreply@anthropic.com>`
