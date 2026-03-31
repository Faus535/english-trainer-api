# Backend Plan: Token Optimization for Claude Commands

> Generated: 2026-03-30
> Request: Optimize /plan (6→2 layers), /analyze (compact prompts), /execute-plan (selective skills, remove /analyze)

## Decisions Log

| # | Topic | Decision | Alternatives Considered | Why |
|---|-------|----------|------------------------|-----|
| 1 | Specialist fusion 10→6 | Merge Architect+Analyst (backend & frontend), Data+Security, UX+QA | Keep 10 separate | Overlap is >70% between merged pairs; 6 agents cover all perspectives |
| 2 | Layer elimination | Remove layers 2,3,5 (Synthesizers, Coordinator, Validator) | Keep Coordinator only | Writer (opus) can absorb synthesis+coordination+validation in one pass |
| 3 | Template file | Delete templates.md | Keep as reference | 100% redundant with template already in layer4-writers.md |
| 4 | /analyze verbosity | Cut agent prompts ~50% | Keep verbose | Haiku follows concise instructions well; verbose prompts waste tokens |
| 5 | /execute-plan skills | Selective skill reading based on phase type | Keep reading all | Reading all 10 skills per phase wastes ~10-15K tokens per phase |
| 6 | /execute-plan /analyze | Remove automatic /analyze at end | Keep it | User can run /analyze manually; saves a full analysis cycle (~20K+ tokens) |
| 7 | Scope-based agent count | BACKEND_ONLY: 4, FRONTEND_ONLY: 4, FULL_STACK: 6 | Keep 6/6/10 | Fewer agents per scope since merged specialists cover more ground |

## Analysis

### Current state (10 files, ~84K chars)

| File | Lines | ~Chars | Status |
|------|-------|--------|--------|
| `plan.md` | 283 | 13.5K | Rewrite |
| `plan-references/layer0-scope.md` | 28 | 1.1K | Keep as-is |
| `plan-references/layer1-specialists.md` | 349 | 16.5K | Rewrite (10→6 agents) |
| `plan-references/layer2-synthesizers.md` | 123 | 6.2K | Delete |
| `plan-references/layer3-coordinator.md` | 74 | 3.7K | Delete |
| `plan-references/layer4-writers.md` | 163 | 8.1K | Rewrite (absorb synthesis+coordination+validation) |
| `plan-references/layer5-validator.md` | 62 | 3.1K | Delete |
| `plan-references/templates.md` | 105 | 4.5K | Delete |
| `analyze.md` | 359 | 17.5K | Rewrite (compact prompts) |
| `execute-plan.md` | 182 | 9.2K | Rewrite (selective skills, remove /analyze) |

### Expected state (6 files, ~45-50K chars)

| File | Lines | ~Chars |
|------|-------|--------|
| `plan.md` | ~180 | ~8K |
| `plan-references/layer0-scope.md` | 28 | 1.1K |
| `plan-references/layer1-specialists.md` | ~210 | ~10K |
| `plan-references/layer4-writers.md` | ~180 | ~9K |
| `analyze.md` | ~180 | ~9K |
| `execute-plan.md` | ~160 | ~8K |

## Phases

### Phase 1: Simplify /plan pipeline (plan.md)

**Goal**: Rewrite plan.md from 9-step/6-layer pipeline to 7-step/2-layer pipeline

**Files to modify**:
- `.claude/commands/plan.md` — Rewrite pipeline: remove Steps 4,5,7 (Synthesizers, Coordinator, Validator). Renumber remaining steps. Update model assignment table, rules, and token limits.

**Details**:

New pipeline structure:
```
Step 0: Load Project Snapshots (unchanged)
Step 1: Context Gathering & Slug (unchanged)
Step 1.5: Scope Detector (haiku) (unchanged)
Step 2: Public Contracts & Phases Proposal (unchanged)
Step 3: Specialists (sonnet, parallel) — now 6 agents
Step 4: Writers (opus, parallel) — absorbs synthesis+coordination+validation
Step 5: Git Branch Preparation (unchanged, was Step 8)
```

Changes to plan.md:
1. Update HTML comment block (lines 7-22): Replace "9-step pipeline" with "7-step pipeline", remove Steps 4,5,7
2. Remove Step 4 section entirely (lines 141-153): Synthesizers
3. Remove Step 5 section entirely (lines 155-164): Coordinator
4. Remove Step 7 section entirely (lines 180-189): Validator
5. Update Step 3 (lines 127-138): Change agent counts to match new 6-agent structure
   - BACKEND_ONLY: Agents 1, 2, 5, 6 (4 agents)
   - FRONTEND_ONLY: Agents 3, 4, 5, 6 (4 agents)
   - FULL_STACK: All 6 agents
6. Update Step 6→4 (lines 167-178): Writers now receive specialist outputs directly (no Coordinator/Synthesizer intermediary). Remove mention of Coordinator's SCOPE_ASSESSMENT; use SCOPE from Step 1.5 instead
7. Renumber Step 8→5
8. Update Rules section:
   - Pipeline execution: `0 → 1 → 1.5 → 2 (approval) → 3 → 4 → 5`
   - Model assignment table: remove Synthesizers/Coordinator/Validator rows, update specialist count to 6
   - Remove "Output injection between layers" section (no longer needed — specialists output goes directly to Writers)
   - Token limits: remove Synthesizers/Coordinator/Validator rows
   - Fallback: simplify to "Specialist fails → Writer works with remaining, flags gap"

**Acceptance criteria**:
- [x] plan.md has 7 steps (0, 1, 1.5, 2, 3, 4, 5)
- [x] No references to Synthesizers, Coordinator, or Validator
- [x] Model assignment table shows: haiku (scope), sonnet (6 specialists), opus (1-2 writers)
- [x] Step 2 still gates on user approval before proceeding
- [x] Rules section is updated and consistent

### Phase 2: Rewrite specialists (layer1-specialists.md)

**Goal**: Merge 10 specialists into 6 by combining overlapping roles

**Files to modify**:
- `.claude/commands/plan-references/layer1-specialists.md` — Rewrite with 6 merged agents

**Details**:

New agent structure:

| # | Name | Merges | Perspective |
|---|------|--------|-------------|
| 1 | Backend Architect | Agent 1 (Architect) + Agent 2 (Analyst) | Structure + gaps + impact |
| 2 | Backend Developer | Agent 3 (Developer) | Concrete implementation (unchanged) |
| 3 | Frontend Architect | Agent 4 (Architect) + Agent 5 (Analyst) | Structure + gaps + impact |
| 4 | Frontend Developer | Agent 6 (Developer) | Concrete implementation (unchanged) |
| 5 | Data & Security | Agent 7 (Data) + Agent 9 (Security) | Schema + migrations + auth + validation |
| 6 | QA & UX | Agent 10 (QA) + Agent 8 (UX) | Testing + user flows + edge cases |

Per agent prompt changes:
- **Agent 1 (Backend Architect)**: Merge return sections from old Agent 1 + Agent 2. Include: MODULE_STRUCTURE, AGGREGATE_DESIGN, PATTERNS, DEPENDENCIES, EXISTING_CODE, GAPS, MODIFICATIONS, IMPACT, RISKS, REUSABLE. Combine "Steps" from both into 4-5 concise bullets.
- **Agent 2 (Backend Developer)**: Keep as-is from old Agent 3.
- **Agent 3 (Frontend Architect)**: Merge return sections from old Agent 4 + Agent 5. Include: COMPONENT_TREE, SERVICES, STATE_MANAGEMENT, ROUTING, EXISTING_CODE, GAPS, MODIFICATIONS, IMPACT, API_DEPENDENCIES, REUSABLE.
- **Agent 4 (Frontend Developer)**: Keep as-is from old Agent 6.
- **Agent 5 (Data & Security)**: Merge old Agent 7 + Agent 9. Include: EXISTING_SCHEMA, PROPOSED_SCHEMA, DATA_FLOW, INTEGRITY, PERFORMANCE, MIGRATION_STRATEGY, AUTH_REQUIREMENTS, OWNERSHIP_CHECKS, INPUT_VALIDATION, VULNERABILITIES, DATA_PROTECTION.
- **Agent 6 (QA & UX)**: Merge old Agent 10 + Agent 8. Include: UNIT_TESTS, INTEGRATION_TESTS, FRONTEND_TESTS, EDGE_CASES, TEST_DATA, COVERAGE_PRIORITIES, USER_FLOWS, UI_STATES, RESPONSIVE, ACCESSIBILITY.

Update header to reflect new scope-based launching:
- BACKEND_ONLY: Agents 1, 2, 5, 6 (4 agents)
- FRONTEND_ONLY: Agents 3, 4, 5, 6 (4 agents)
- FULL_STACK: All 6 agents

Compact each prompt: remove verbose "Steps:" numbered lists, use 3-4 bullet points. Remove repeated boilerplate ("IMPORTANT: Check this context...") — state once at top of file.

**Acceptance criteria**:
- [x] File has exactly 6 agent prompts
- [x] Merged agents cover all output sections from their source agents
- [x] Scope-based launching is correct (4/4/6)
- [x] Boilerplate is stated once at top, not repeated per agent
- [x] Each agent prompt is ~25-30 lines (down from ~35-40)
- [x] Word limit remains 3000 per agent

### Phase 3: Rewrite writers with integrated synthesis+validation (layer4-writers.md)

**Goal**: Writers receive specialist outputs directly and absorb synthesis, coordination, and validation responsibilities

**Files to modify**:
- `.claude/commands/plan-references/layer4-writers.md` — Rewrite to absorb layers 2, 3, and 5

**Details**:

Each Writer prompt now includes:
1. **Direct specialist injection**: Instead of Coordinator/Synthesizer output, inject raw specialist outputs
2. **Synthesis instructions**: "Resolve conflicts between specialists, deduplicate, merge into coherent plan"
3. **Coordination instructions**: "Order phases globally, integrate cross-cutting concerns into each phase"
4. **Validation checklist**: Inline the checks from layer5-validator.md as a self-check before writing

**Backend Writer** receives:
- Agent 1 (Backend Architect) output
- Agent 2 (Backend Developer) output
- Agent 5 (Data & Security) output
- Agent 6 (QA & UX) output

**Frontend Writer** receives:
- Agent 3 (Frontend Architect) output
- Agent 4 (Frontend Developer) output
- Agent 5 (Data & Security) output
- Agent 6 (QA & UX) output

New Writer prompt structure:
```
You are the **Backend Writer**. You receive specialist analyses and must synthesize, coordinate, validate, and write the final plan.

Feature: "$ARGUMENTS"

=== SPECIALIST OUTPUTS ===
[Insert Backend Architect output]
[Insert Backend Developer output]
[Insert Data & Security output]
[Insert QA & UX output]

Your job (in order):
1. SYNTHESIZE: Resolve conflicts, deduplicate, merge into coherent plan
2. COORDINATE: Order phases by dependency, integrate cross-cutting into each phase
3. WRITE: Produce the plan file using the template below
4. VALIDATE: Before finishing, verify these checks:
   - Every phase has Goal, Files, Details, Acceptance criteria
   - Every file path uses correct base package
   - Every endpoint has auth requirements
   - Every migration has version number not conflicting with existing
   - Every phase includes tests
   - Every phase is a vertical slice
   - Last phase includes /revisar
   - Phase dependency order is correct

[Template follows...]
```

Keep the existing template structure from current layer4-writers.md (lines 33-83 for backend, lines 111-163 for frontend).

Add `DECISIONS_LOG` section to the template (currently only in Coordinator output, needs to be in final plan).

**Acceptance criteria**:
- [x] Backend Writer receives 4 specialist outputs directly (no Synthesizer/Coordinator)
- [x] Frontend Writer receives 4 specialist outputs directly
- [x] Each Writer prompt includes synthesis, coordination, and validation instructions
- [x] Validation checklist from layer5-validator.md is embedded in each Writer
- [x] Template includes Decisions Log section
- [x] No references to Synthesizer, Coordinator, or Validator agents

### Phase 4: Delete obsolete files

**Goal**: Remove the 4 files that are no longer needed

**Files to delete**:
- `.claude/commands/plan-references/layer2-synthesizers.md` — Absorbed by Writers
- `.claude/commands/plan-references/layer3-coordinator.md` — Absorbed by Writers
- `.claude/commands/plan-references/layer5-validator.md` — Absorbed by Writers
- `.claude/commands/plan-references/templates.md` — Redundant with Writers

**Details**:
```bash
rm .claude/commands/plan-references/layer2-synthesizers.md
rm .claude/commands/plan-references/layer3-coordinator.md
rm .claude/commands/plan-references/layer5-validator.md
rm .claude/commands/plan-references/templates.md
```

**Acceptance criteria**:
- [x] 4 files deleted
- [x] Only `layer0-scope.md`, `layer1-specialists.md`, `layer4-writers.md` remain in `plan-references/`
- [x] No remaining references to deleted files in plan.md

### Phase 5: Compact /analyze prompts (analyze.md)

**Goal**: Reduce analyze.md agent prompts by ~50% without losing coverage

**Files to modify**:
- `.claude/commands/analyze.md` — Compact all 4 agent prompts

**Details**:

Optimization strategies:
1. **Remove verbose instructions**: "Be thorough — scan every module. Do NOT skip modules." → unnecessary for haiku
2. **Compact output format descriptions**: Instead of explaining each section with examples, just list section headers
3. **Merge repeated patterns**: "Prefer Glob+Grep over Read" and "Prefer Glob over Read" → state once at top
4. **Shorten step lists**: Each agent has 4-5 numbered steps → reduce to 2-3 bullet points
5. **Remove path explanations**: "(path: src/main/java/com/faus535/englishtrainer)" → agent can infer from base package

Example compression — Agent 1 (Domain Layer Scanner):

Current (~20 lines):
```
Scan the Spring Boot project at ...
Base package: com.faus535.englishtrainer (path: src/main/java/com/faus535/englishtrainer).

For EACH module directory (top-level subdirectory...):

1. List all Java classes in `domain/` (aggregate roots, entities)
2. Count and list Value Objects — files matching `*Id.java` or in `domain/vo/` or records
3. List all `*Event.java` or `*DomainEvent.java` classes
4. List all exception classes in `domain/` (files matching `*Exception.java`)
5. List repository interfaces in `domain/` (files matching `*Repository.java`)

Output format — ONE LINE per module, pipe-separated:
MODULE_NAME | aggregates: ClassA, ClassB | vos: IdA, IdB | events: EventA | exceptions: ExA | repos: RepoA

Also report the total module count and the list of all module names found.

Be thorough — scan every module. Do NOT skip modules. Do NOT read file contents unless needed...
```

Compacted (~10 lines):
```
Scan /home/faustinoolivas/dev/proyectos/carmen/english-trainer-api.
Base package: com.faus535.englishtrainer.

For each module (subdirectory under base package, excluding "shared"), list:
- Java classes in domain/ (aggregates, entities)
- Value Objects (*Id.java, domain/vo/, records)
- Events (*Event.java), Exceptions (*Exception.java), Repos (*Repository.java)

Output ONE LINE per module:
MODULE_NAME | aggregates: A, B | vos: X, Y | events: E | exceptions: Ex | repos: R

Report total module count + names. Use Glob over Read.
```

Apply same compression pattern to Agents 2, 3, and 4.

Also compact Steps 3-5 (non-agent steps) — remove verbose markdown template examples, use brief descriptions of expected sections.

**Acceptance criteria**:
- [x] analyze.md is ~227 lines (down from 359, ~37% reduction)
- [x] All 4 agent prompts are compacted (~50% shorter)
- [x] Output format requirements are preserved
- [x] Step 3 snapshot-references format is preserved but with shorter descriptions
- [x] Step 4 index template is preserved
- [x] Rules section is compacted and preserved

### Phase 6: Optimize /execute-plan (execute-plan.md)

**Goal**: Selective skill reading + remove automatic /analyze at end

**Files to modify**:
- `.claude/commands/execute-plan.md` — Add skill mapping, remove Step 3

**Details**:

**Change 1 — Selective skills (Step 1b)**:

Replace current Step 1b (lines 57-59):
```
Before writing code, read the relevant skill files for the patterns needed in this phase:
- **Backend plans**: Read `SKILL.md` and key `references/*.md` from `.claude/plugins/s2-backend/skills/` (domain-design, persistence, api-design, testing, etc.)
```

With skill mapping logic:
```
Before writing code, read ONLY the skills relevant to this phase's content:

Skill mapping (read based on what the phase touches):
- Domain classes (aggregates, VOs, events, exceptions) → domain-design/
- Persistence (entities, repositories, migrations) → persistence/
- Controllers, endpoints, DTOs → api-design/
- Error handling, ControllerAdvice → error-handling/
- Tests → testing/
- Use cases, services → modulith-usecases/
- Auth, security, JWT → security/
- Logging, health → logging/
- Package structure, new module → architecture/

Read `SKILL.md` + key `references/*.md` from `.claude/plugins/s2-backend/skills/<skill>/` for each matched skill.
Do NOT read all skills — only those matching the phase content.
```

**Change 2 — Remove Step 3 (lines 111-115)**:

Delete Step 3 entirely:
```
## Step 3 — Update Project Snapshot
Run `/analyze` to regenerate the project snapshots...
```

Renumber Step 4→3.

Update final output (Step 4d→3d) to remove "Project snapshot updated." line.

**Change 3 — Update rules section**:
Add under "Skills consultation":
```
- Only read skills that match the phase content (see mapping in Step 1b)
- Never read all skills for every phase
```

**Acceptance criteria**:
- [ ] Step 1b has skill mapping table
- [ ] No Step 3 (/analyze) — goes directly from Step 2 (/revisar) to Step 3 (Push & Deploy)
- [ ] Final output does not mention "Project snapshot updated"
- [ ] Rules mention selective skill reading
- [ ] execute-plan.md is ~160 lines (down from 182)

### Phase 7: Verify consistency and run /revisar

**Goal**: Ensure all files are internally consistent and cross-reference correctly

**Details**:
1. Read all modified/remaining files and verify:
   - plan.md references only layer0-scope.md, layer1-specialists.md, layer4-writers.md
   - No file references deleted files (layer2, layer3, layer5, templates)
   - Agent numbering is consistent across plan.md and layer1-specialists.md
   - Scope-based agent selection matches between plan.md and layer1-specialists.md
   - Writer input injection in plan.md matches specialist numbering
2. Run `/revisar` to validate overall quality

**Acceptance criteria**:
- [ ] No broken references to deleted files
- [ ] Agent numbering consistent across all files
- [ ] Scope variants consistent (plan.md ↔ layer1-specialists.md)
- [ ] Writer injection matches specialist outputs
- [ ] /revisar passes

## API Contract

N/A — No API changes. This is a tooling/command optimization.

## Database Changes

N/A — No database changes.

## Testing Strategy

No automated tests — these are command prompt files. Validation is:
1. Manual consistency check (Phase 7)
2. Run `/plan <test feature>` after implementation to verify the optimized pipeline works end-to-end
3. Run `/execute-plan` on a small plan to verify selective skills work
