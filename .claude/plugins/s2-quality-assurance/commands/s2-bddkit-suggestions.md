---
name: s2-bddkit-suggestions
description: Detects common steps, objects, and elements and suggests contributions to the BDDKit library
---

# BDDKit Suggestions

Analyzes the project for steps, page objects, and page elements that could be promoted to the shared BDDKit library, and creates a Merge Request with the proposed contributions.

## Usage

```bash
/s2-bddkit-suggestions
```

## Behavior

1. **Scan** the project for reusable candidates:
   - Step definitions that are generic enough for cross-project use (e.g., common API assertions, navigation steps)
   - Page elements that represent standard UI components (e.g., login forms, data tables, modals)
   - Page objects that follow common patterns across projects

2. **Classify** each candidate:
   - **Step**: Generic step definition (not project-specific)
   - **Page element**: Reusable UI locator pattern
   - **Page object**: Reusable page interaction pattern

3. **Propose** the list of candidates to the user for approval before proceeding.

4. **Create** a Merge Request against the BDDKit library repository with the approved candidates.

## Detection criteria

### Steps likely to be common
- Steps that don't reference project-specific data or URLs
- Steps that perform generic operations (HTTP assertions, navigation, waits)
- Steps already duplicated across multiple projects

### Page elements likely to be common
- Locators for standard components (buttons, inputs, tables, modals)
- Locators using generic `data-testid` patterns
- Locators not tied to project-specific DOM structure

### Page objects likely to be common
- Pages that represent standard patterns (login, search, list/detail)
- Page objects with generic method signatures

## Example output

```
$ /s2-bddkit-suggestions

Scanning project for common candidates...

=== STEPS ===
  ✓ steps/api/__init__.py:12 — "the response status code is {status_code:d}"
  ✓ steps/api/__init__.py:18 — "the response contains field \"{field}\""
  ✗ steps/api/users_api_steps.py:5 — project-specific, skipped

=== PAGE ELEMENTS ===
  ✓ pageelements/login_elements.py — generic login form locators

=== PAGE OBJECTS ===
  ✗ No common page object candidates found

---
Found 3 candidates for BDDKit contribution.

Proceed with Merge Request? (yes/no)
```

## References

- Skill: [bddkit-usage](../skills/bddkit-usage/SKILL.md)
- Skill: [behave](../skills/behave/SKILL.md)
- Skill: [web-testing](../skills/web-testing/SKILL.md)
- Skill: [api-testing](../skills/api-testing/SKILL.md)