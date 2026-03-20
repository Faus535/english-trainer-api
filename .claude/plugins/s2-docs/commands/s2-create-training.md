---
name: s2-create-training
description: Generates a complete training package (guide + PPTX) with S2GRUPO branding
model: opus
---

# Command: /s2-create-training

Generates a complete training package: a structured markdown guide and a branded S2GRUPO PPTX presentation.

## Usage

```bash
/s2-create-training
```

## Process

1. **Gather information** — Ask the user (use AskUserQuestion):
   - Training topic and specific technologies/stack
   - Target audience (junior, mid, senior; role)
   - Format: 2 sessions morning, 2 sessions full day, or 3 sessions
   - Professor name
   - Day number (DNN) and course context (if part of a series)
   - (Logos are bundled in the pptx skill's `assets/` — copy automatically, no need to ask)
   - Whether to generate an exercise repository (and type: backend / frontend)

2. **Read skills** — Load the following skills:
   - `s2-docs:training` — guide structure, session formats, content rules
   - `s2-docs:pptx` — PPTX pipeline, components, design system
   - `s2-docs:branding` — corporate identity (if needed for color/logo details)
   - `s2-docs:exercise-repo-back` — if exercise repo requested (backend)
   - `s2-docs:exercise-repo-front` — if exercise repo requested (frontend, when available)

3. **Generate the guide** — Create `DNN_titulo.md` in the user's working directory:
   - Use the template from `training/references/template_guide.md`
   - Fill all placeholders with content adapted to the topic and audience
   - Include commented code examples, exercises with design criteria, reflection questions
   - End with a summary table (concept / when to use / key detail)
   - Remove session 3 section entirely if format is 2 sessions (morning or full day)

4. **Generate the build script** — Create `build.js` in the user's working directory:
   - Copy logos to working directory: `cp ${CLAUDE_PLUGIN_ROOT}/skills/pptx/assets/*.b64 ./`
   - Copy helper functions from `pptx/templates/build_presentation.js`
   - Map guide sections to PPTX slides following the guide-to-PPTX mapping:
     - Title slide (`addBlueSlide`) with DNN tag and training title
     - Section slides (`addSectionSlide`) for each session
     - Content slides (`addWhiteSlide`) with theory highlights
     - Terminal slides for code demos
     - Cards for comparisons and patterns
     - Tables for rules and summaries
     - Closing slide (`addBlueSlide`) with "Preguntas"
   - Keep slide count manageable: 2-3 slides per session concept

5. **Install dependencies** — Verify pptxgenjs is available:
   ```bash
   npm list pptxgenjs 2>/dev/null || npm install pptxgenjs
   ```

6. **Run the build** — Generate the presentation:
   ```bash
   node build.js
   ```

7. **Inject animations** — If there are terminal blocks:
   ```bash
   pip install python-pptx lxml 2>/dev/null
   python ${CLAUDE_PLUGIN_ROOT}/skills/pptx/scripts/inject_animations.py \
     --input output.pptx --meta anim_meta.json --output DNN_titulo.pptx
   ```

8. **Visual QA** — Convert to images for review (if LibreOffice is available):
   ```bash
   soffice --headless --convert-to pdf DNN_titulo.pptx 2>/dev/null && \
   pdftoppm -jpeg -r 150 DNN_titulo.pdf slide
   ```

9. **Generate exercise repository** — If the user requested an exercise repo:
   - Read the generated guide (`DNN_titulo.md`) to extract all exercise tasks from the autonomous work blocks
   - Load the appropriate exercise-repo skill (`exercise-repo-back` or `exercise-repo-front`)
   - Follow the skill's generation process:
     1. Identify exercise boundaries — each autonomous work task becomes a `start/solution` pair
     2. **Propose exercise plan and wait for approval** — present the professor with:
        - List of exercises with title, cybersecurity domain, estimated time, concepts practiced
        - What each `start` branch provides (stubs, interfaces, failing tests)
        - What each `solution` branch implements
        - **Do NOT generate code until the professor explicitly approves**
     3. Create `main` branch with base project scaffolding
     4. For each exercise: create `start` branch (skeleton + failing tests + `EXERCISE.md`)
     5. For each exercise: create `solution` branch (complete implementation, all tests passing)
     6. Update `README.md` on `main` with exercise index
   - Time estimates in `EXERCISE.md` must match the training guide
   - Exercise domains must be cybersecurity-related (see `exercise-repo-back` skill)

10. **Deliver** — Confirm the deliverables:
    - `DNN_titulo.md` — the guide
    - `DNN_titulo.pptx` — the presentation
    - Exercise repository (if requested) — with `start/*` and `solution/*` branches
    Ask if adjustments are needed.

## Rules

- ALWAYS use the color constants from the design system. NEVER invent colors outside the palette.
- ALWAYS include branding on all slides: logo, claim, footer.
- Use **Arial** for general text and **Consolas** for code/terminal.
- Code examples in the guide must always be commented.
- Exercises must require design criteria, not just mechanical implementation.
- The summary table at the end of the guide is mandatory.
- Layout is 16:9 (10" x 5.625"). Do not exceed the grid margins.
- File naming convention: `DNN_titulo_snake_case` (e.g., `D05_domain_events.md`).

## Example

```bash
/s2-create-training
```

```
What topic is the training about?
> Domain Events in DDD

Target audience?
> Mid-level Java developers

Format?
> 2 sessions morning / 2 sessions full day / 3 sessions

Professor name?
> Victor Monteagudo

Day number (DNN)?
> D05

Course context?
> Week 2 of DDD fundamentals course, after Aggregates and Value Objects

Generate exercise repository?
> Yes, backend

Generating training package...
 D05_domain_events.md created (guide with 3 sessions + exercises)
 build.js created with 14 slides
 D05_domain_events.pptx generated with animations

Exercise plan proposed — waiting for approval...
 [Professor reviews and approves]

Generating exercise repository...
 main branch created (base Spring Boot project)
 start/D05-domain-event-publish created (skeleton + 3 failing tests)
 solution/D05-domain-event-publish created (complete, all tests passing)
 start/D05-event-driven-workflow created (skeleton + 4 failing tests)
 solution/D05-event-driven-workflow created (complete, all tests passing)
 README.md updated with exercise index

Deliverables:
  ./D05_domain_events.md
  ./D05_domain_events.pptx
  ./exercises/ (Git repo with start/solution branches)
```

## References

- Skill: `s2-docs:training`
- Skill: `s2-docs:pptx`
- Skill: `s2-docs:branding`
- Skill: `s2-docs:exercise-repo-back` (backend exercise repos)
- Skill: `s2-docs:exercise-repo-front` (frontend exercise repos, when available)
