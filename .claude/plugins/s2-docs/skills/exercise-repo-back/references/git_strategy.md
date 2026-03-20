# Git Strategy — Branch Visibility, Repo Structure, and Branch Content

## Solution Branch Visibility Strategy

GitLab does not support hiding branches from users with read access. To keep solutions invisible to students until needed, use **push-on-demand**:

| What | Where | When |
|------|-------|------|
| `start/*` branches | Remote (GitLab) | Pushed **before** the session. Students can see and clone them. |
| `solution/*` branches | Professor's local repo only | Created locally during preparation. **NOT pushed** to the remote. |
| `solution/*` branches | Remote (GitLab) | Pushed **only when the professor decides** (after exercise deadline, during review, or next session). |

### Workflow for the professor

```bash
# Before the session — push only start branches
git push origin start/D01-vulnerability-scoring

# After students finish — publish the solution
git push origin solution/D01-vulnerability-scoring
```

### Alternative: separate repository

For courses where solutions must never leak (e.g., certifications), use two repositories:

| Repo | Access | Content |
|------|--------|---------|
| `course-exercises` | Students (Developer role) | `main` + `start/*` branches only |
| `course-solutions` | Professor only (Maintainer) | Mirror of exercises repo + `solution/*` branches |

The professor publishes solutions into the student repo when appropriate:
```bash
# 'solutions' remote points to the private course-solutions repo
git remote add solutions git@gitlab.com:org/course-solutions.git
git fetch solutions
git push origin solutions/solution/D01-vulnerability-scoring:solution/D01-vulnerability-scoring
```

## Repository Structure

```
main branch:
├── README.md              ← Course overview, how to use branches, exercise index
├── build.gradle.kts       ← Base build configuration (Spring Boot 3.5+, Java 21+)
├── settings.gradle.kts    ← Project settings
├── gradle/
│   └── wrapper/           ← Gradle wrapper
└── src/
    └── (minimal shared config: application.yml, base packages)
```

Each `start/DNN-*` branch adds on top of `main`:

```
src/
├── main/java/.../
│   ├── domain/
│   │   ├── [Stubs or skeleton classes with TODOs]
│   │   └── [Interfaces to implement]
│   └── infrastructure/
│       └── [Pre-configured adapters if needed]
├── test/java/.../
│   └── [Test classes with assertions — tests should FAIL until exercise is complete]
└── EXERCISE.md            ← Exercise instructions (copied from the guide's task section)
```

Each `solution/DNN-*` branch contains:

```
src/
├── main/java/.../
│   ├── domain/
│   │   ├── [Complete implementation with comments]
│   │   └── [All interfaces implemented]
│   └── infrastructure/
│       └── [Complete adapters]
├── test/java/.../
│   └── [All tests passing]
└── EXERCISE.md            ← Same instructions + notes on design decisions taken
```

## What Goes in `start` Branches

| Include | Do NOT include |
|---------|---------------|
| `build.gradle.kts` with all dependencies | Implementation of the exercise's core logic |
| Base package structure per `s2-backend:architecture` | Completed domain classes |
| Stub/skeleton classes with `// TODO` markers | Solution code |
| Interfaces the student must implement | Passing tests (tests should fail or be @Disabled) |
| Test classes following `s2-backend:testing` patterns | |
| Pre-configured infrastructure (DB, configs) if not the focus | |
| `EXERCISE.md` with instructions and estimated time | |

## What Goes in `solution` Branches

| Include | Purpose |
|---------|---------|
| Complete implementation following all `s2-backend` skills | Reference solution |
| All tests passing (Object Mothers, In-Memory Repos) | Verification that solution is correct |
| Inline comments explaining design decisions | Learning material |
| `EXERCISE.md` with added "Design decisions" section | Rationale for the chosen approach |
