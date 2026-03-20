---
name: training
description: "This skill should be used when the user asks to 'create a training',
  'guide of the day', 'create a workshop', 'generate training materials',
  'create a formation', 'build training slides', 'DNN guide', or mentions
  training sessions with theory, exercises, and presentations. Generates
  complete training packages: structured markdown guide, hands-on exercises,
  and branded S2GRUPO PPTX presentation."
---

# Training — Guide of the Day Skill

Generate complete training packages: a structured markdown guide + a branded S2GRUPO PPTX presentation.

## Output

Each training generates up to three deliverables:

| File | Format | Content |
|------|--------|---------|
| `DNN_titulo.md` | Markdown | Full guide with theory, code, exercises, summary |
| `DNN_titulo.pptx` | PPTX | Branded slides mapped from the guide sections |
| Exercise repo | Git repo | `start/*` and `solution/*` branches per exercise (optional) |

## Session Formats

| Format | Sessions | Schedule |
|--------|----------|----------|
| 2 sessions (morning) | Theory + Work + Theory + Work | 9:30-10:30, 12:00-13:00 |
| 2 sessions (full day) | Theory + Autonomous work + Closing | 9:30-11:30, 15:30-16:30 |
| 3 sessions | Theory + Work + Theory + Work + Closing + Work | 9:30-10:30, 12:00-13:00, 15:30-16:30 |

### 2-Session Morning Schedule Detail

| Block | Time | Content |
|-------|------|---------|
| Session 1 | 9:30 - 10:30 | Core concept + first examples |
| Autonomous work | 10:30 - 12:00 | Exercises on session 1 |
| Session 2 | 12:00 - 13:00 | Advanced concept or pattern + closing |
| Autonomous work | 13:00 onwards | Exercises on session 2 + integrating exercise |

### 2-Session Full Day Schedule Detail

| Block | Time | Content |
|-------|------|---------|
| Session 1 | 9:30 - 11:30 | Core concept + examples + exercises |
| Session 2 | 15:30 - 16:30 | Advanced patterns + closing exercise |

### 3-Session Schedule Detail

| Block | Time | Content |
|-------|------|---------|
| Session 1 | 9:30 - 10:30 | Core concept + first examples |
| Autonomous work | 10:30 - 12:00 | Exercises on session 1 |
| Session 2 | 12:00 - 13:00 | Advanced concept or pattern |
| Autonomous work | 13:00 - 15:30 | Exercises on session 2 |
| Session 3 | 15:30 - 16:30 | Design criteria, edge cases, when NOT to use |
| Autonomous work | 16:30 - 18:00 | Integrating exercise on different domain |

## Guide Structure

The guide follows the template in [references/template_guide.md](references/template_guide.md):

1. **Header** — DNN, title, professor, format, schedule
2. **Objective** — Context, what they'll learn, why it matters
3. **Sessions** — Theory blocks with code examples and explanations
4. **Autonomous work** — Exercises with criteria (not just mechanical tasks)
5. **Summary table** — Concept / When to use / Key implementation detail

## Guide-to-PPTX Mapping

| Guide section | PPTX slide type |
|--------------|----------------|
| Header / title | `addBlueSlide()` with DNN tag and title |
| Each session start | `addSectionSlide(tag, title)` |
| Theory with code | `addWhiteSlide()` + `addTerminal()` |
| Concepts / comparisons | `addWhiteSlide()` + `addCard()` |
| Rules / tables | `addWhiteSlide()` + `addTable()` |
| Exercises | `addWhiteSlide()` with numbered steps |
| Summary | `addWhiteSlide()` with summary table |
| Closing | `addBlueSlide()` with "Preguntas" |

## PPTX Color Rules

When generating slides, follow the `s2-docs:pptx` color guidelines strictly:

- **Agenda slides**: All session and autonomous work cards use `BLUE` accent. Never green.
- **Theory cards**: Default to `BLUE`. Use `AMBER` for a second color if needed.
- **Before/After code comparisons**: This is the **only** context where `RED`/`GREEN` accents are appropriate.
- **Objective text**: Use `BLUE` or `TEXT` color, never red or green.

The primary palette is **blue + yellow/amber**. Red and green are reserved exclusively for good/bad comparisons.

## Content Rules

1. **Code must always be commented** — every example needs inline explanations
2. **Exercises require design criteria** — not just "implement X", but "decide how and justify"
3. **Summary table is mandatory** — concept / when to use / key detail
4. **Integrating exercise** — final exercise uses a different domain than the session examples
5. **Reflection questions** — at least one per autonomous work block (written as code comment)
6. **Exercise sizing must match available time** — see rules below

## Exercise Sizing Rules

Exercises must be realistic for the time slot they occupy. Calculate available time from the schedule and size accordingly.

| Slot type | Typical duration | Exercise scope |
|-----------|-----------------|----------------|
| Between sessions | 1h - 2.5h | 1-2 focused tasks. Achievable in the time gap. No large refactors or multi-class designs. Prefer small, incremental steps that reinforce the session concept. |
| End of day | Open-ended | Can be as comprehensive as needed. May integrate all concepts from the day. Design decisions, multi-class implementations, and justification are encouraged. |

### Sizing guidelines

- **Between-session exercises**: Estimate 30-45 min of effective coding per task. A 1.5h gap means 2 tasks max. Include the reflection question time. Students also need time to read, think, and re-read session material.
- **End-of-day exercises**: No time cap. These are the integrating exercises where students apply the full day's learning. Can span multiple classes, require architectural decisions, and use a different domain.
- **Always state estimated time per task** — add `(~30 min)` or `(~45 min)` next to each task name so students can self-pace.
- **If a format has no afternoon session** (e.g., 2 sessions morning), the last autonomous work block after session 2 is the end-of-day block and should be sized accordingly.

## Exercise Repository Integration

When the user requests an exercise repository as part of the training, the exercises from the guide's autonomous work blocks become `start/solution` branch pairs:

| Guide element | Exercise repo mapping |
|---------------|----------------------|
| Between-session autonomous work tasks | Focused `start` branches (small scope, failing tests) |
| End-of-day integrating exercise | Larger `start` branch (more scaffolding, design decisions required) |
| Time estimate in guide (`~30 min`) | Must match `EXERCISE.md` time estimate |
| Cybersecurity domain (required) | Each exercise uses a cybersecurity domain (vulnerabilities, incidents, IOCs, etc.) |

### Process

1. Generate the guide first — exercises are extracted from its autonomous work blocks
2. Load the appropriate exercise-repo skill (`exercise-repo-back` or `exercise-repo-front`)
3. **Propose plan and wait for professor approval** before writing any code
4. Generate `main` + `start/*` + `solution/*` branches following the skill's conventions
5. Ensure time estimates and exercise scope are consistent between guide and repo

### Available exercise-repo skills

| Skill | Stack | Conventions |
|-------|-------|-------------|
| `s2-docs:exercise-repo-back` | Java 21+, Spring Boot 3.5+, Gradle | Follows `s2-backend` plugin conventions |
| `s2-docs:exercise-repo-front` | *(not yet available)* | Will follow frontend conventions when created |

## Dependencies

- Skill `s2-docs:pptx` — for PPTX generation pipeline
- Skill `s2-docs:branding` — for corporate identity reference
- Skill `s2-docs:exercise-repo-back` — for Git repository structure with start/solution branches per backend exercise (Java/Spring Boot, follows s2-backend conventions)
- Skill `s2-docs:exercise-repo-front` — *(not yet available)* for frontend exercise repos

## File Structure

```
training/
├── SKILL.md                    ← this file
└── references/
    └── template_guide.md       ← markdown template with placeholders
```
