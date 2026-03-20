---
name: s2-review
description: Validates test structure, naming conventions, and code quality
---

# Review Test Quality

Validates that the test code follows the patterns defined in the s2-quality-assurance skills.

## Usage

```bash
/s2-review tags                    # Review tag consistency and placement
/s2-review steps                   # Review step definitions for duplicates and conventions
/s2-review features                # Review feature file structure and Gherkin quality
/s2-review all                     # Run all checks
```

## Validation checks

### 1. Tag checks (consult skill `s2-quality-assurance:tags-management`)

- Every feature file has a **type tag** (`@api` or `@web`)
- Every feature file has a **feature tag** (`@user-login`, etc.)
- Every scenario has a **status tag** (`@todo`, `@wip`, `@exe`) or none (stable)
- Tags follow naming conventions (`@kebab-case` for features, `@lowercase` for status/type)
- No orphaned tags (tags that don't match any known category)
- No contradictory tags on the same scenario (e.g., `@todo` and `@exe`)

### 2. Step checks (consult skill `s2-quality-assurance:behave`)

- No **duplicate step definitions** across files (same regex in multiple files)
- Steps use **parameterized placeholders** instead of hardcoded values
- API steps are in `steps/api/`, Web steps are in `steps/web/`
- Common steps are extracted to `__init__.py` files
- Step functions follow `snake_case` naming
- No business logic in step definitions (should delegate to page objects or helpers)

### 3. Feature file checks (consult skill `s2-quality-assurance:behave`)

- One feature per `.feature` file
- Feature name matches file name (`user_login.feature` → `Feature: User login`)
- Scenarios follow **Given-When-Then** structure
- Scenarios are **independent** (no shared state)
- `Background` is used for common setup, not duplicated in each scenario
- `Scenario Outline` is used when 3+ scenarios only differ in data

### 4. Data checks (consult skill `s2-quality-assurance:bddkit-usage`)

- All YAML files in `resources/data_test/` are valid YAML
- All YAML files in `resources/language/` are valid YAML
- No unused data entries (referenced in YAML but not in any feature)
- No missing data entries (referenced in features but not in any YAML)

### 5. Page object checks (consult skill `s2-quality-assurance:web-testing`)

- One page object per page in `pageobjects/`
- Page objects use `PascalCase` naming
- Page elements are in `pageelements/`, not mixed into page objects
- Element locators use `snake_case` naming
- No assertions inside page objects (assertions belong in step definitions)

## Naming conventions

| Component | Expected pattern | Example |
|-----------|-----------------|---------|
| Feature file | `snake_case.feature` | `user_login.feature` |
| Scenario | Descriptive sentence | `Successful login with valid credentials` |
| Step function | `snake_case` | `def step_user_logs_in(context)` |
| Step file (API) | `<resource>_api_steps.py` | `users_api_steps.py` |
| Step file (Web) | `<page>_web_steps.py` | `login_web_steps.py` |
| Page object | `PascalCase` class | `LoginPage` |
| Page element | `snake_case` variable | `username_input` |
| Data test file | `snake_case.yaml` | `login_data.yaml` |
| Language file | `snake_case.yaml` | `login_messages.yaml` |
| Tag (feature) | `@kebab-case` | `@user-login` |
| Tag (status) | `@lowercase` | `@wip`, `@todo`, `@exe` |

## Severity levels

### ERROR (Critical)
Must be fixed before commit.
- Duplicate step definitions (same step regex in multiple files)
- Feature file without type tag (`@api` or `@web`)
- Missing step definition for a scenario step
- Invalid YAML in data files

### WARNING (Recommended)
Should be fixed. Creates technical debt.
- Naming convention violations
- Hardcoded values in step definitions (should be parameterized)
- Business logic in step definitions (should be in page objects/helpers)
- Assertions inside page objects
- Unused data entries in YAML files

### PASS (Correct)
Everything is fine.

## Quality score

```
Score = (points earned / total points) x 100

Ranges:
- 90-100: Excellent
- 80-89: Good (minor improvements)
- 70-79: Acceptable (needs corrections)
- <70: Insufficient (significant work needed)
```

## Example output

```
$ /s2-review all

Running QA review checks...

=== TAG CHECKS ===
✓ All features have type tags
✗ ERROR: features/web/checkout.feature — missing feature tag
✗ WARNING: features/api/users.feature — scenario "Create user" has both @todo and @exe

=== STEP CHECKS ===
✓ No duplicate step definitions found
✗ WARNING: steps/api/users_api_steps.py:15 — hardcoded URL "/api/v1/users"
✗ WARNING: steps/web/login_web_steps.py:8 — function name "loginStep" should be snake_case

=== FEATURE CHECKS ===
✓ All features follow Given-When-Then structure
✓ All features have one feature per file

=== DATA CHECKS ===
✓ All YAML files are valid
✗ WARNING: resources/data_test/old_users.yaml — 2 unused entries

=== PAGE OBJECT CHECKS ===
✓ All page objects use PascalCase
✗ WARNING: pageobjects/login_page.py:42 — assertion found in page object

---
Results: 2 errors, 4 warnings, 5 passed
Score: 72/100 (Acceptable)
```

## References

- Skill: [tags-management](../skills/tags-management/SKILL.md)
- Skill: [behave](../skills/behave/SKILL.md)
- Skill: [bddkit-usage](../skills/bddkit-usage/SKILL.md)
- Skill: [api-testing](../skills/api-testing/SKILL.md)
- Skill: [web-testing](../skills/web-testing/SKILL.md)