---
name: behave
description: This skill should be used when the user asks to "write feature files", "create scenario outlines", "configure behave hooks", "structure Given-When-Then", "write step definitions", or needs guidance on Behave framework conventions, environment.py hooks, or feature file organization.
---

# Skill: Behave Framework

Defines the patterns and conventions for writing BDD tests using the Behave framework with BDDKit.

## Skill Relationships

### Foundation For
- **api-testing** - Extends this skill with API-specific patterns
- **web-testing** - Extends this skill with Web UI-specific patterns

### Works With
- **bddkit-usage** - BDDKit library provides built-in steps that follow these Behave patterns
- **tags-management** - Tag strategy applies to features and scenarios structured by this skill

### When to Use
- Consult **behave** for core Behave framework patterns (feature files, scenarios, hooks)
- Consult **api-testing** for API-specific testing patterns
- Consult **web-testing** for Web UI-specific testing patterns
- Consult **bddkit-usage** for BDDKit library features and built-in steps

## When to use this skill

- When writing or reviewing `.feature` files
- When creating or modifying step definitions
- When structuring scenarios and scenario outlines
- When configuring Behave hooks in `environment.py`

## Key rules

### Feature files
1. One feature per `.feature` file
2. Feature name matches the file name: `user_login.feature` → `Feature: User login`
3. Place API features in `features/api/`, Web features in `features/web/`
4. Write scenarios in **Given-When-Then** format
5. Keep scenarios independent — no shared state between scenarios
6. Use **Background** for common setup steps shared across all scenarios in a feature

### Scenario outlines
1. Use `Scenario Outline` with `Examples` table for data-driven tests
2. Reference data from `resources/data_test/` YAML files for test data
3. Reference data from `resources/language/` YAML files for expected messages
4. Keep examples tables readable — use descriptive column names

### Step definitions
1. One step file per feature or logical group: `steps/api/login_steps.py`
2. Step functions use `snake_case` naming
3. Use `@given`, `@when`, `@then` decorators from `behave`
4. Store shared state in `context` object
5. Keep steps reusable — parameterize with `{placeholders}`
6. Avoid logic in step definitions — delegate to page objects or API helpers

### Tags
1. Apply feature-level tags for categorization: `@user-login`, `@api`, `@web`
2. Apply scenario-level tags for status: `@wip`, `@todo`, `@exe`
3. Use `@skip` to temporarily disable a scenario
4. See `s2-quality-assurance:tags-management` for full tag conventions

### Hooks (environment.py)
1. `before_all` — Initialize BDDKit, load configuration
2. `before_scenario` — Set up test data, authentication
3. `after_scenario` — Clean up test data, capture screenshots on failure
4. `after_all` — Tear down global resources

## Examples

### Feature file
```gherkin
@user-login @web
Feature: User login

  Background:
    Given the login page is open

  Scenario: Successful login with valid credentials
    Given a registered user with valid credentials
    When the user enters their credentials
    And clicks the login button
    Then the dashboard page is displayed

  Scenario Outline: Failed login with invalid credentials
    When the user enters "<username>" and "<password>"
    And clicks the login button
    Then an error message "<error_message>" is displayed

    Examples:
      | username      | password  | error_message         |
      | invalid_user  | pass123   | Invalid credentials   |
      | valid_user    | wrongpass | Invalid credentials   |
```

### Step definition
```python
from behave import given, when, then


@given('the login page is open')
def step_login_page_open(context):
    # TODO: Use page object to navigate to login
    pass


@when('the user enters "{username}" and "{password}"')
def step_enter_credentials(context, username, password):
    # TODO: Use page object to fill credentials
    pass


@then('the dashboard page is displayed')
def step_dashboard_displayed(context):
    # TODO: Assert dashboard page is visible
    pass
```