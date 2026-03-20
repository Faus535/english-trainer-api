---
name: api-testing
description: This skill should be used when the user asks to "write API tests", "create API feature files", "test REST API", "validate API responses", "handle HTTP requests in BDD", or works with API test scenarios in `features/api/` or `steps/api/`. Defines how to write API feature files, step definitions, request handling, and response validation for REST API BDD testing.
---

# Skill: API Testing

Defines the patterns for writing BDD tests against REST APIs using BDDKit's built-in API steps.

## Skill Relationships

### Extends

- **behave** - Uses core Behave framework patterns (feature files, Given-When-Then, scenario structure)
- **bddkit-usage** - Uses BDDKit's built-in API steps, parameter replacement system, and context storage

### Related

- **tags-management** - Apply tags to API test scenarios (@api, @no_driver, status tags)

### When to Use

- Choose **api-testing** for REST API endpoint testing (features/api/, steps/api/)
- Consult **behave** for core feature file patterns (Scenario, Scenario Outline, Environment hooks)
- Consult **bddkit-usage** for BDDKit library API reference (built-in steps, map_param, context)

## BDDKit API Steps - Use First

BDDKit provides a complete set of API step definitions. Import them before writing custom steps:

```python
# steps/__init__.py or steps/api/__init__.py
from bddkit.behave.steps.api import requests, responses
```

This gives access to all built-in steps for request configuration, execution, and response validation. See the *
*bddkit-usage** skill's `references/api-steps.md` for the complete catalog.

## Key rules

### API feature files

1. Place all API features in `features/api/`
2. Tag with `@no_driver`, and `@TS_API` at feature level; add a resource tag (e.g., `@users`)
3. Use `Actions Before the Feature` for feature-scoped setup (auth, seed data) — runs once per feature, not per scenario
4. Use `Actions After the Feature` for teardown; use `Actions Before each Scenario` only for scenario-scoped state
5. Split feature files by operation and concern: one file per action (`*_creation.feature`, `*_edition.feature`,
   `*_deletion.feature`) and one for errors (`*_errors.feature`) and unsupported methods (
   `*_undefined_methods.feature`) — see [API Feature Files](references/api-feature-files.md) for the full decision
   framework and minimum coverage checklist
6. Use `Scenario Outline` with `Examples` for field-level validation (missing fields, invalid values)
7. Use BDDKit built-in steps (e.g., `I set request url to`, `I send the request and store the response`)
8. Use `map_param` replacements for dynamic values: `[CONF:api_url]`, `[CONTEXT:token]`, `[UUID]`

### API step definitions

1. Place in `steps/api/` directory
2. One step file per API resource or logical group
3. Naming: `<resource>_api_steps.py` (e.g., `users_api_steps.py`)
4. Reuse BDDKit built-in steps — only write custom steps for project-specific logic
5. Use `map_param()` on all string parameters in custom steps
6. Store responses in `context` using `context.store_key_in_storage()`

### Request handling

1. Use BDDKit's `I set request url to "[CONF:api_url]"` with config references
2. Use `I set request endpoint to` for path segments
3. Use `I set request json body to` with docstrings for JSON payloads
4. Use `I set request body from json file` for complex payloads from `resources/api/`
5. Use `I add request header` for authentication and content types

### Response validation

1. Always validate status code first: `the response status should be 200`
2. Validate JSON paths: `the response json at "data.id" should equal "123"`
3. Validate list lengths: `the response json at "items" is a list of length "10"`
4. Validate headers: `the response header "Content-Type" should be "application/json"`
5. Use `the response body should contain` for substring checks
6. Use `the response time should be less than 5.0 seconds` for performance checks

## References

### Project-Specific Patterns

- [API Feature Files](references/api-feature-files.md) — Decision framework for what feature files an endpoint needs,
  required scenarios per file type, and minimum coverage checklist
- [API Step Definitions](references/api-step-definitions.md)
- [API Request Handling](references/api-request-handling.md)
- [API Response Validation](references/api-response-validation.md)

### BDDKit Library (from bddkit-usage skill)

- **`../bddkit-usage/references/api-steps.md`** - Complete built-in API step catalog
- **`../bddkit-usage/references/mockito-steps.md`** - Mock server steps for component testing
- **`../bddkit-usage/references/dataset-replacements.md`** - Parameter replacement patterns (`[CONF:]`, `[CONTEXT:]`,
  `[UUID]`, etc.)