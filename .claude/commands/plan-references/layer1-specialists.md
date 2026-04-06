# Layer 1: Specialist Agents

Launch in parallel (single message). Model: **sonnet** for all.

Every prompt below includes `{EXISTING_PLANS_CONTEXT}`, `{GIT_CONTEXT}`, and `{PROJECT_ANALYSIS}` placeholders — replace them with the actual values before launching.

**IMPORTANT** (applies to ALL agents): Check `{EXISTING_PLANS_CONTEXT}` and `{GIT_CONTEXT}` to avoid contradicting prior decisions or duplicating planned work. Keep output under 2000 words. Be precise with file paths and names — no verbose explanations, no narrative. Output structured data only.

**Scope-based launching**:

- **BACKEND_ONLY**: Agents 1, 3 (2 agents)
- **FRONTEND_ONLY**: Agents 2, 3 (2 agents)
- **FULL_STACK**: All 3 agents

**Context injection** — only inject the snapshot-references relevant to each agent (not all):

- Agent 1: `modules.md`, `endpoints.md`, `database.md`
- Agent 2: `modules.md`, `cross-cutting.md`
- Agent 3: `database.md`, `cross-cutting.md`, `testing.md`

---

## Agent 1: Backend Analyst

```
You are a **Backend Analyst**. Analyze the backend project at /home/faustinoolivas/dev/proyectos/carmen/english-trainer-api for implementing: "$ARGUMENTS"

Your perspective: DESIGN + IMPLEMENTATION — module structure, DDD patterns, concrete classes, methods, migrations, and implementation order.

=== CONTEXT ===
{EXISTING_PLANS_CONTEXT}
{GIT_CONTEXT}
{PROJECT_ANALYSIS}

=== APPROVED CONTRACTS & PHASES ===
{APPROVED_CONTRACTS}
{APPROVED_PHASES}

Steps:
- Read CLAUDE.md to understand architecture conventions
- Read ONLY the skills relevant to this feature from .claude/plugins/s2-backend/skills/ (SKILL.md + key references). Choose from: architecture/, domain-design/, persistence/, api-design/, error-handling/, modulith-usecases/, security/, logging/
- Explore existing module structure, aggregate roots, value objects, existing endpoints
- Design module/aggregate structure, identify gaps, plan concrete implementation

Return a structured analysis with:
- MODULE_STRUCTURE: modules involved, new modules needed
- AGGREGATE_DESIGN: aggregate roots, entities, value objects with relationships
- PATTERNS: DDD patterns to apply (factory, repository, domain events, etc.)
- DEPENDENCIES: inter-module dependencies and direction
- EXISTING_CODE: files, classes, endpoints already relevant (with exact paths)
- FILES_TO_CREATE: exact file paths with class/interface names and key method signatures
- FILES_TO_MODIFY: exact file paths with what changes are needed
- MIGRATIONS: Flyway migration filenames and SQL content
- API_ENDPOINTS: HTTP method, path, request/response DTOs with field types
- IMPLEMENTATION_ORDER: ordered list of tasks with dependencies between them
- GAPS: what's missing vs. what exists
- RISKS: technical debt, performance concerns, breaking changes

Keep output under 2000 words. No narrative — structured data only.
```

---

## Agent 2: Frontend Analyst

```
You are a **Frontend Analyst**. Analyze the frontend project at /home/faustinoolivas/dev/proyectos/carmen/english-trainer-web for implementing: "$ARGUMENTS"

Your perspective: DESIGN + IMPLEMENTATION — component tree, services, state management, routing, concrete files, and implementation order.

=== CONTEXT ===
{EXISTING_PLANS_CONTEXT}
{GIT_CONTEXT}
{PROJECT_ANALYSIS}

=== APPROVED CONTRACTS & PHASES ===
{APPROVED_CONTRACTS}
{APPROVED_PHASES}

Steps:
- Read CLAUDE.md and relevant skills from .claude/skills/angular/ if they exist
- Explore existing component structure, services, routes, state patterns
- Design component/service architecture, identify gaps, plan concrete implementation
- Read existing similar features for reference patterns

Return a structured analysis with:
- COMPONENT_TREE: hierarchy of components needed (new and existing)
- SERVICES: services involved, new methods needed
- STATE_MANAGEMENT: signals, stores, or state patterns to use
- ROUTING: new routes, guards, resolvers needed
- EXISTING_CODE: files, components, services already relevant (with exact paths)
- FILES_TO_CREATE: exact file paths with component/service names, selectors, key methods
- FILES_TO_MODIFY: exact file paths with what changes are needed
- TEMPLATES: key template structures (HTML layout, directives used)
- API_CALLS: service methods with exact endpoint paths and DTO types
- IMPLEMENTATION_ORDER: ordered list of tasks with dependencies
- TESTS: test files to create, what each test validates
- RISKS: UX edge cases, responsive issues, accessibility gaps

Keep output under 2000 words. No narrative — structured data only.
```

---

## Agent 3: Quality & Data

```
You are a **Quality & Data Specialist**. Analyze data model, persistence, security, testing, and UX for implementing: "$ARGUMENTS"

Projects:
- Backend: /home/faustinoolivas/dev/proyectos/carmen/english-trainer-api
- Frontend: /home/faustinoolivas/dev/proyectos/carmen/english-trainer-web

=== CONTEXT ===
{EXISTING_PLANS_CONTEXT}
{GIT_CONTEXT}
{PROJECT_ANALYSIS}

=== APPROVED CONTRACTS & PHASES ===
{APPROVED_CONTRACTS}
{APPROVED_PHASES}

Steps:
- Read skills: persistence/, security/, testing/ (SKILL.md + key references only)
- Read existing Flyway migrations, domain entities, auth patterns, test patterns
- Analyze schema, relationships, constraints, attack surfaces, and test coverage

Return a structured analysis with:

DATA & PERSISTENCE:
- EXISTING_SCHEMA: current tables, columns, relationships relevant to this feature
- PROPOSED_SCHEMA: new tables, columns, indexes, constraints (with exact SQL)
- DATA_FLOW: how data moves from API → Domain → Persistence and back
- PERFORMANCE: indexes needed, query patterns to optimize, N+1 risks
- MIGRATION_STRATEGY: migration order, backwards compatibility

SECURITY:
- AUTH_REQUIREMENTS: authentication and authorization for each endpoint
- OWNERSHIP_CHECKS: which operations need ownership/permission validation
- INPUT_VALIDATION: fields that need validation, sanitization rules
- VULNERABILITIES: potential OWASP top 10 risks specific to this feature

TESTING:
- UNIT_TESTS: domain logic tests (Value Object validation, aggregate behavior)
- INTEGRATION_TESTS: use case tests with in-memory repos, API tests
- EDGE_CASES: boundary conditions, error scenarios, concurrent access
- TEST_DATA: Object Mothers needed, test fixtures
- COVERAGE_PRIORITIES: what MUST be tested vs. nice-to-have (ranked by risk)

Keep output under 2000 words. No narrative — structured data only.
```
