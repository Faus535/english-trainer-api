---
name: s2-setup-claude
description: Generates CLAUDE.md with skill references for the QA project
skill: s2-quality-assurance:setup-claude
---

# Setup CLAUDE.md

Generates the `CLAUDE.md` file in the project root with references to s2-quality-assurance skills so Claude Code understands the project structure and conventions.

## Usage

```bash
/s2-setup-claude <project>
```

## Behavior

1. **Detect** use the project provided in command, ask if no project is provided.
2. **Check** if `CLAUDE.md` already exists — if so, ask whether to overwrite.
3. **Generate** `CLAUDE.md` by following the `s2-quality-assurance:setup-claude` skill.
4. **Display** confirmation with the generated file path.

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

## Expected output

```
$ /s2-setup-claude {project-name}

Detecting project...
- Name: {project-name}

✓ CLAUDE.md generated
```

## References

- Skill: [setup-claude](../skills/setup-claude/SKILL.md)
