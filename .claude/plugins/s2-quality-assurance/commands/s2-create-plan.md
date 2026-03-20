---
name: s2-create-plan
description: Creates a test implementation plan with new test definitions and review proposals for existing ones
---

# Create Test Plan

Creates an implementation plan with new test definitions and proposals for reviewing existing tests.

## Usage

```bash
/s2-create-plan
```

## Behavior

1. **Ask** the user for the task to plan. It is important to ask about the **task types** to consider.

   Task types:
   - New Web functionality
   - Modification/Improvement of Web functionality
   - Archival of Web functionality
   - New API functionality
   - Modification/Improvement of API functionality
   - Archival of API functionality

2. **Propose** the plan to the user for approval. IMPORTANT: Do not start creating the plan until the user has accepted the phases and scope.

3. **Save** the plan as a new file inside the `.ai/plans` directory with the current date and a semantic name. Example: `2026_01_29-user_login_tests.md`.

## Plan sections

### Goal
- Short and concise. 1-3 sentences summarizing the objective.

### Context
- List relevant files, folders, and code.
- Link files to the actual repository code.
- Read `CLAUDE.md` and referenced documentation to understand the project conventions.
- Consult relevant s2-quality-assurance skills:
  - **s2-quality-assurance:behave** — Behave framework patterns
  - **s2-quality-assurance:tags-management** — Tag strategy and organization
  - **s2-quality-assurance:bddkit-usage** — BDDKit library patterns

### Phases
- Each phase is a coherent slice of the task
- Each phase must contain its description and a list of actions (checkboxes)
- Split into as many phases as needed to facilitate review
- Don't mix responsibilities in the same phase
- It should be possible to commit and push each phase without breaking the test suite

  Typical phase structure for QA:
  - **Feature files** (.feature) Define the Scenarios
  - **Step definitions** for a specific scenario group (could be new or reused)
  - **Page objects** + **page elements** for a specific page (Web tests)
  - **Test data** (data_test YAML) + **language** files for a scenario group
  - **API resources** (request/response templates) for API scenarios

### Next step
- A single sentence indicating the next phase to complete.

## Example

```bash
/s2-create-plan
```

```
What task do you want to create a plan for?
> Add login functionality tests for the user portal

What task types should we consider?
> New Web functionality

Proposed plan:

Goal: Create BDD test scenarios for user portal login including valid credentials,
invalid credentials, and account lockout cases.

Phases:
- Phase 1: Identify Related Scenarios
- Phase 2: Create new feature or add scenarios to existing feature
- Phase 3: Page Objects + Page Elements Creation
- Phase 4: Steps Creation
- Phase 5: Test data and language files

Do you approve this plan? (yes/no)
```

## Plan file template

```markdown
# Goal

<!-- 1-3 sentences summarizing the objective -->

# Context

<!-- Relevant files, skills, and conventions -->

# Phases

## Phase 1: <description>

- [ ] Action 1
- [ ] Action 2

## Phase 2: <description>

- [ ] Action 1
- [ ] Action 2

# Next step

Execute Phase 1: <description>
```

## References

- Skills: [behave](../skills/behave/SKILL.md), [tags-management](../skills/tags-management/SKILL.md), [bddkit-usage](../skills/bddkit-usage/SKILL.md),[api-testing](../skills/api-testing/SKILL.md),[web-testing](../skills/web-testing/SKILL.md)