---
name: init-project
description: This skill should be used when the user asks to "initialize QA project", "create new BDD project", "scaffold test project", "set up BDDKit project", "generate project structure", or invokes the /s2-init command. Initializes a Python BDD project from scratch with BDDKit, Behave framework, Allure reporting, and standard directory structure for API and Web testing.
---

# Skill: Init Project

Generates a complete QA project structure for BDD testing using the BDDKit library.

## When to use this skill

- When creating a new QA project from scratch
- When the `/s2-init` command is invoked

## Parameters

| Parameter | Description | Example |
|-----------|-------------|---------|
| project-name | Project name (kebab-case) | `iris`, `user-portal` |

## Generated structure

```
<project-name>/quality-assurance/
├── conf/
│   ├── logging.conf
│   └── properties.cfg
├── features/
│   ├── api/
│   └── web/
├── pageelements/
│   └── __init__.py
├── pageobjects/
│   └── __init__.py
├── resources/
│   ├── api/
│   ├── data_test/
│   └── language/
├── steps/
│   ├── api/
│   ├── web/
│   └── __init__.py
├── .gitignore
├── __init__.py
├── behave.ini
├── environment.py
├── README.md
└── requirements.txt
```

## Directory purposes

| Directory | Purpose |
|-----------|---------|
| `conf/` | Configuration files (logging, environment properties) |
| `features/api/` | `.feature` files for API test scenarios |
| `features/web/` | `.feature` files for Web UI test scenarios |
| `pageelements/` | Web UI element locators (CSS selectors, XPaths) |
| `pageobjects/` | Page Object Model classes for Web UI testing |
| `resources/api/` | API request/response templates and schemas |
| `resources/data_test/` | Test data YAML files |
| `resources/language/` | Internationalization YAML files |
| `steps/api/` | Step definitions for API scenarios |
| `steps/web/` | Step definitions for Web UI scenarios |

## File templates

Try to copy template from local file `../bddkit/bddkit-template`

## Dependencies

| Dependency | Purpose | Notes |
|------------|---------|-------|
| **BDDKit** | Core testing library (wraps Behave) | Defined in `requirements.txt` |
| **Allure-Behave** | Test report formatter | Included via BDDKit |
| **Java (runtime)** | Allure CLI report generation | External tool, not project code |

## Process

1. Validate project name is kebab-case
2. Verify target directory does not already exist
3. Create the full directory tree
4. Generate all files using the templates above
5. Replace `<project-name>` placeholders with the actual project name
6. Display summary

## Expected output

```
Initializing project '<project-name>'...

✓ Directory structure created
✓ Configuration files generated (behave.ini, logging.conf, properties.cfg)
✓ Environment hooks generated (environment.py)
✓ Dependencies defined (requirements.txt)
✓ .gitignore configured

Next steps:
  1. Review conf/properties.cfg and set your YouTrack project key
  2. Run /s2-setup-claude to generate CLAUDE.md
  3. Run /s2-create-plan to create your first test plan
```

## Validations

- Target directory (`<project-name>/quality-assurance/`) must not already exist
- Project name must be kebab-case (lowercase letters, digits, and hyphens only)