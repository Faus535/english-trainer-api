---
name: exercise-repo-back
description: "This skill should be used when the user asks to 'create backend exercise repo',
  'create exercise repository', 'generate exercise branches', 'prepare start/solution
  for backend exercises', 'scaffold training exercises', 'Git structure for backend
  training', or needs to set up a Git repository with start and solution branches for
  backend (Java/Spring Boot) training exercises. Generates branch structure, starter
  code, and completed solutions following s2-backend conventions."
---

# Exercise Repository (Backend) — Git Structure for Training Exercises

Define and generate a Git repository structure where each training exercise has a `start` branch (scaffolding for students) and a `solution` branch (completed solution, hidden until the professor publishes it).

## Concept

Students receive a repository where:

1. They checkout a `start` branch to begin an exercise
2. They create their own `feature/{nombre-alumno}/DNN-exercise-slug` branch from `start` and work there
3. They push their feature branch for the professor to review
4. When the professor decides, the `solution` branch is pushed so they can compare

This eliminates setup friction, ensures everyone starts from the same baseline, and keeps solutions hidden until the right moment.

## Student Workflow

```bash
# 1. Checkout the exercise starting point
git checkout start/D01-aggregate-root

# 2. Create a personal feature branch (includes exercise identifier)
git checkout -b feature/juan-garcia/D01-aggregate-root

# 3. Work on the exercise...

# 4. Push the feature branch
git push origin feature/juan-garcia/D01-aggregate-root

# 5. When solutions are published, compare
git diff feature/juan-garcia/D01-aggregate-root..solution/D01-aggregate-root
```

## Branch Naming Convention

```
start/DNN-exercise-slug                        ← scaffolding (professor creates)
solution/DNN-exercise-slug                     ← completed solution (professor creates, pushes on demand)
feature/{nombre-alumno}/DNN-exercise-slug      ← student's work (student creates from start)
```

Examples:
```
start/D01-aggregate-root
solution/D01-aggregate-root
feature/juan-garcia/D01-aggregate-root
feature/ana-lopez/D01-aggregate-root

start/D02-domain-events
solution/D02-domain-events
feature/juan-garcia/D02-domain-events
```

- `DNN` matches the day number from the training guide
- `exercise-slug` is a kebab-case short name for the exercise
- One pair of `start/solution` branches per exercise task (not per session)
- Each student creates one `feature/{nombre-alumno}/DNN-exercise-slug` branch per exercise

## Exercise Domain

All exercises must be based on **cybersecurity domains**. Examples of suitable domains:

- Vulnerability management (CVEs, assets, severity scoring)
- Incident response (alerts, triage, escalation workflows)
- Threat intelligence (IOCs, threat actors, campaigns)
- Access control (permissions, roles, audit logs)
- Security operations center — SOC (event correlation, playbooks)
- Compliance and auditing (policies, evidence, controls)

The specific domain for each exercise must be proposed and **validated with the professor before implementation**. See the plan validation step in the generation process.

## Technology Stack

| Technology | Version | Notes |
|-----------|---------|-------|
| Java | 21+ | Use records, sealed interfaces, pattern matching where appropriate |
| Spring Boot | 3.5+ | Latest stable |
| Gradle | Kotlin DSL | `build.gradle.kts`. No Maven. |
| Test | JUnit 5 + AssertJ | Spring Boot test starter |

## s2-backend Skills Reference

When generating `start` and `solution` branch code, **always follow the conventions defined in the `s2-backend` plugin skills**. Load the relevant skills before writing any code:

| Skill | Use for |
|-------|---------|
| `s2-backend:init-project` | Base project scaffolding for `main` branch |
| `s2-backend:architecture` | Package-per-aggregate structure, module organization |
| `s2-backend:domain-design` | Aggregate Roots, Value Objects (records), Domain Events, Repository interfaces |
| `s2-backend:modulith-usecases` | Use Case classes, `@UseCase` annotation, execute method pattern |
| `s2-backend:persistence` | Spring Data JDBC/JPA entities, Flyway migrations |
| `s2-backend:api-design` | REST controllers, request/response DTOs, validation |
| `s2-backend:error-handling` | Exception hierarchy, ControllerAdvice, ApiError mapping |
| `s2-backend:testing` | Object Mothers, In-Memory Repositories, constructor injection |
| `s2-backend:logging` | SLF4J conventions, log levels, actuator health checks |

See details on how to apply and compliance rules in [references/s2_backend_and_templates.md](references/s2_backend_and_templates.md).

## References

- [references/git_strategy.md](references/git_strategy.md) — Solution branch visibility, professor workflow, repo structure, branch content rules
- [references/s2_backend_and_templates.md](references/s2_backend_and_templates.md) — s2-backend application rules, EXERCISE.md template, generation process, README.md template

## File Structure

```
exercise-repo-back/
├── SKILL.md                              ← this file
└── references/
    ├── git_strategy.md                   ← visibility strategy, repo structure, branch content
    └── s2_backend_and_templates.md       ← s2-backend rules, templates, generation process
```
