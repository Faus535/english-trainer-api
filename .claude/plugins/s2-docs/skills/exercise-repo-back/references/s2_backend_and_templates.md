# s2-backend Application Rules, Templates, and Generation Process

## How to Apply s2-backend Skills

- **`main` branch setup**: Use `s2-backend:init-project` + `s2-backend:architecture` to scaffold the base project with correct package structure and `build.gradle.kts`.
- **`start` branches (skeleton)**: Use `s2-backend:domain-design` for stub Aggregates/Value Objects, `s2-backend:testing` for test structure with Object Mothers and failing tests, `s2-backend:architecture` for package layout.
- **`solution` branches (complete)**: Use all relevant skills to implement the full solution following s2-backend conventions. The solution must be exemplary — it's what students compare their work against.

## Compliance Rules

- All code in both `start` and `solution` branches **must comply with s2-backend naming conventions** (package structure, class suffixes, annotation usage).
- Tests in `solution` branches must follow the `s2-backend:testing` patterns (Object Mothers, In-Memory Repositories, constructor injection).
- Domain model in `solution` branches must follow `s2-backend:domain-design` (immutable Aggregates, Value Objects as records, checked exceptions).

## EXERCISE.md Template

```markdown
# [DNN] — [Exercise Title]

**Tiempo estimado:** ~[X] min
**Concepto:** [Which session concept this reinforces]
**Rama de inicio:** `start/DNN-exercise-slug`

## Objetivo

[What the student should achieve]

## Cómo empezar

\`\`\`bash
git checkout start/DNN-exercise-slug
git checkout -b feature/{tu-nombre}/DNN-exercise-slug
\`\`\`

## Punto de partida

[Description of what's already provided in this branch]

- `ClassName.java` — [what it contains, what's missing]
- `InterfaceName.java` — [contract to implement]
- `ClassNameTest.java` — [tests that validate the solution]

## Tareas

1. [Step 1 — specific, actionable]
2. [Step 2]
3. [Step 3]

## Validación

Ejecutar los tests para verificar:
\`\`\`bash
./gradlew test
\`\`\`

Todos los tests en `[TestClass]` deben pasar.

## Entrega

\`\`\`bash
git add .
git commit -m "DNN exercise-slug: [brief description]"
git push origin feature/{tu-nombre}/DNN-exercise-slug
\`\`\`
```

## Generation Process

**IMPORTANT: The generation process requires human validation before creating any code.**

When creating an exercise repository for a training:

1. **Read the training guide** (`DNN_titulo.md`) to extract all exercise tasks
2. **Load s2-backend skills** — at minimum: `init-project`, `architecture`, `domain-design`, `testing`. Add `persistence`, `api-design`, `modulith-usecases`, `error-handling` as needed by the exercise topic.
3. **Identify exercise boundaries** — each autonomous work task becomes a `start/solution` pair
4. **Propose exercise plan and get approval** — Before writing any code, present a plan to the professor with:
   - List of exercises (one per autonomous work block)
   - For each exercise: title, cybersecurity domain, estimated time, key concepts practiced
   - The Aggregates, Value Objects, and entities involved
   - What the `start` branch will contain (stubs, interfaces, tests)
   - What the `solution` branch will implement
   - **Wait for explicit professor approval before proceeding.** Adjust the plan based on feedback.
5. **Create `main` branch** using `s2-backend:init-project` conventions (`build.gradle.kts`, Spring Boot 3.5+, Java 21+, Gradle wrapper, base package structure)
6. **For each exercise**, create the `start` branch from `main`:
   - Add skeleton classes following `s2-backend:domain-design` and `s2-backend:architecture`
   - Add failing tests following `s2-backend:testing` patterns
   - Add `EXERCISE.md` with instructions and time estimate
7. **For each exercise**, create the `solution` branch from its `start`:
   - Implement the complete solution following all applicable `s2-backend` skills
   - Make all tests pass
   - Add design decision comments
   - **Do NOT push** — keep local until professor decides to publish
8. **Update `README.md`** on `main` with the exercise index (only `start` branches listed)

## README.md Template (main branch)

```markdown
# [Course Name] — Ejercicios

## Requisitos

- Java 21+
- Gradle 8+ (incluido vía wrapper)
- IDE con soporte Spring Boot (IntelliJ IDEA recomendado)

## Cómo trabajar

1. Clona este repositorio
2. Checkout la rama del ejercicio: `git checkout start/DNN-exercise-name`
3. Crea tu rama de trabajo: `git checkout -b feature/{tu-nombre}/DNN-exercise-name`
4. Lee `EXERCISE.md` para las instrucciones
5. Trabaja en el ejercicio
6. Ejecuta los tests: `./gradlew test`
7. Sube tu rama: `git push origin feature/{tu-nombre}/DNN-exercise-name`

## Índice de ejercicios

| Día | Ejercicio | Rama | Tiempo |
|-----|-----------|------|--------|
| D01 | [Exercise title] | `start/D01-slug` | ~30 min |
| D01 | [Exercise title] | `start/D01-slug` | ~45 min |
| ... | ... | ... | ... |
```

## Relation to Training Skill

This skill complements `s2-docs:training`. When generating a training:

- **Between-session exercises** → smaller `start` branches with focused scope
- **End-of-day exercises** → larger `start` branches with more scaffolding but less guidance, requiring design decisions
- Time estimates in `EXERCISE.md` must match the time estimates in the training guide
- Solution branches are created locally but only pushed when the professor decides
