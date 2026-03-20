---
name: s2-execute-plan
description: Executes the current phase of an implementation plan
---

# Execute Plan

Executes the current phase of an existing implementation plan.

## Usage

```bash
/s2-execute-plan                    # Executes the most recent plan
/s2-execute-plan <path-to-plan>     # Executes a specific plan
```

## Behavior

IMPORTANT: Only execute the actions in the current phase.

1. **Ask** the user which plan to execute if not specified. Search `.ai/plans/` for the most recent plan.
2. **Read** the plan and locate the current phase (indicated in the "Next step" section).
3. **Consult** the relevant s2-quality-assurance skills for the phase:
   - **s2-quality-assurance:behave** — Behave framework patterns
   - **s2-quality-assurance:tags-management** — Tag strategy and organization
   - **s2-quality-assurance:bddkit-usage** — BDDKit library patterns
4. **Execute** the actions of the current phase following the skill patterns.
5. **Check off** completed items in the plan file.
6. **Update** the "Next step" section with the next phase.

## Example

```bash
/s2-execute-plan .ai/plans/2026_01_29-user_login_tests.md
```

```
Executing Phase 1: Login page objects and elements

- [x] Create LoginPage in pageobjects/
- [x] Define login element locators in pageelements/
- [x] Add __init__.py imports

✓ Phase 1 complete

Next step: Execute Phase 2 — Happy path feature + steps (valid credentials)
```

## Notes

- Do NOT execute actions from future phases
- Each phase should be independently committable
- After completing a phase, run `/s2-execute-plan` again to continue with the next one

## References

- Skills: [behave](../skills/behave/SKILL.md), [tags-management](../skills/tags-management/SKILL.md), [bddkit-usage](../skills/bddkit-usage/SKILL.md)