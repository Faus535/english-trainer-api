---
name: tags-management
description: This skill should be used when the user asks to "tag features", "tag scenarios", "filter tests by tags", "organize test execution", "use @wip @todo @exe tags", "apply status tags", "categorize tests", or needs guidance on BDD tag strategy, tag naming conventions, or execution filters.
---

# Skill: Tags Management

Defines the tag strategy for organizing and filtering BDD test scenarios.

## When to use this skill

- When tagging features or scenarios
- When configuring test execution filters
- When reviewing tag consistency across the project
- When deciding which tests to include in a run

## Key rules

### Tag categories

1. **Status tags** — Control execution flow:
    - `@todo` — Scenario defined but not yet implemented
    - `@wip` — Work in progress, not ready for CI
    - `@exe` — Ready for execution in the current cycle
    - `@skip` — Temporarily disabled

2. **Type tags** — Classify by test type:
    - `@api` — API test scenario
    - `@web` — Web UI test scenario
    - `@smoke` — Smoke test (quick validation)
    - `@regression` — Regression test suite

3. **Feature tags** — Categorize by functionality:
    - Use `@kebab-case` naming: `@user-login`, `@order-checkout`
    - Apply at feature level to tag all scenarios in the file
    - Apply at scenario level for specific categorization

4. **Priority tags** — Indicate test priority:
    - `@critical` — Must pass before any release
    - `@high` — Important for release quality
    - `@low` — Nice to have, can be deferred

### Placement rules

1. **Feature-level tags** apply to all scenarios in the file
2. **Scenario-level tags** apply only to that scenario
3. Combine tags — a scenario inherits feature tags plus its own
4. Status tags (`@todo`, `@wip`, `@exe`) go at **scenario level**
5. Type tags (`@api`, `@web`) go at **feature level**
6. Feature tags (`@user-login`) go at **feature level**

### Execution filters

Standard BDDKit execution with tag filters:

```bash
# Run only @exe tests, exclude @todo and @wip
bddkit behave-exec -D "BDDKIT_CONFIG_ENVIRONMENT=<env>" -t "~@todo" -t "~@wip" -t "@exe"

# Run smoke tests only
bddkit behave-exec -D "BDDKIT_CONFIG_ENVIRONMENT=<env>" -t "@smoke"

# Run all API tests
bddkit behave-exec -D "BDDKIT_CONFIG_ENVIRONMENT=<env>" -t "@api"
```

## References

- [Tag Strategy](references/tag-strategy.md)
- [Tag Conventions](references/tag-conventions.md)