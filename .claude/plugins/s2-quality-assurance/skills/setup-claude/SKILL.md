---
name: setup-claude
description: This skill should be used when the user asks to "generate CLAUDE.md", "set up Claude Code", "configure project for Claude", "create project documentation", or invokes the /s2-setup-claude command. Generates CLAUDE.md file with QA project structure, available commands, skill references, and BDD naming conventions.
---

# Skill: Setup Claude

Generates the `CLAUDE.md` file in the project root so Claude Code understands the project structure, conventions, and available tools.

## When to use this skill

- When setting up a new QA project (after `/s2-init`)
- When the `/s2-setup-claude <project>` command is invoked
- When CLAUDE.md needs to be regenerated after plugin updates

## Behavior

1. Use the project provided in command, ask if no project is provided.
2. If `CLAUDE.md` already exists, ask whether to overwrite
3. Generate `CLAUDE.md` using the template below, replacing `{project-name}` with the detected name
4. Display confirmation

## CLAUDE.md template

```markdown
# {project-name}

QA project built with BDDKit and Behave framework.

## Project structure

- `features/api/` — API test scenarios (.feature files)
- `features/web/` — Web UI test scenarios (.feature files)
- `steps/api/` — API step definitions
- `steps/web/` — Web UI step definitions
- `pageobjects/` — Page Object Model classes
- `pageelements/` — Web UI element locators
- `resources/data_test/` — Test data (YAML)
- `resources/language/` — Internationalization (YAML)
- `conf/` — Configuration (logging, environment properties)

## Available commands

| Command | Description |
|---------|-------------|
| `/s2-init <project>` | Initialize a new QA project |
| `/s2-setup-claude` | Generate this CLAUDE.md file |
| `/s2-create-plan` | Create a test implementation plan |
| `/s2-execute-plan` | Execute the current phase of a plan |
| `/s2-create` | Generate test code, steps, and datasets |
| `/s2-review` | Validate structure, naming, and code quality |
| `/s2-bddkit-suggestions` | Suggest common steps/elements for BDDKit library |
| `/s2-youtrack-integration` | Sync test definitions with YouTrack |

## Skill references

When generating or modifying test code, consult these skills:

- **s2-quality-assurance:init-project** — Project structure and file templates
- **s2-quality-assurance:setup-claude** — CLAUDE.md generation
- **s2-quality-assurance:behave** — Behave framework patterns and conventions
- **s2-quality-assurance:tags-management** — Tag strategy and organization
- **s2-quality-assurance:bddkit-usage** — BDDKit library usage patterns

## Naming conventions

| Component | Pattern | Example |
|-----------|---------|---------|
| Feature file | `snake_case.feature` | `user_login.feature` |
| Scenario | Descriptive sentence | `User logs in with valid credentials` |
| Step definition | `snake_case` function | `def step_user_logs_in(context)` |
| Page object | `PascalCase` class | `LoginPage` |
| Page element | `snake_case` locator | `username_input` |
| Data test file | `snake_case.yaml` | `login_data.yaml` |
| Language file | `snake_case.yaml` | `login_messages.yaml` |
| Tag (feature) | `@kebab-case` | `@user-login` |
| Tag (status) | `@lowercase` | `@wip`, `@todo`, `@exe` |

## External library docs

Before generating code that uses external libraries, use Context7 for up-to-date documentation:

```
resolve-library-id: "behave"
get-library-docs: context7CompatibleLibraryID, topic: "step definitions"
```
```

## Process

1. If value is missing or placeholder, ask the user for the project name
2. Check if `CLAUDE.md` exists in the project root
3. If it exists, ask the user whether to overwrite
4. Generate `CLAUDE.md` using the template above
5. Replace `{project-name}` with the actual project name
6. Display confirmation

## Expected output

```
$ /s2-setup-claude {project-name}

Detecting project...
- Name: {project-name}

✓ CLAUDE.md generated
```
