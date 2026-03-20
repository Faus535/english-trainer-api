---
name: bddkit-usage
description: This skill should be used when the user asks to "use bddkit", "configure bddkit", "write test steps", "create step definitions", "use common steps", "set up environment hooks", "use page objects", "use page elements", "use map_param", "use replace_param", "use dataset utilities", "import bddkit steps", or mentions BDDKit library features, built-in steps, parameter replacements, or test utilities.
---

# Skill: BDDKit Usage

Defines the patterns for using the BDDKit library as the common foundation for BDD testing projects. BDDKit is a
Behave-based Python tool that simplifies the testing process with built-in steps, page object patterns, driver
management, and data utilities.

## BDDKit Library Source

Repository: `https://gitlab01.s2grupo.es/product/commons/chameleon/bddkit.git`

When detailed source code inspection is needed beyond what the reference files provide, read the actual source from the
local clone. Search for it relative to the project being tested (typically a sibling directory or installed as a pip
dependency).

## Skill Relationships

### Provides Library Support For

- **api-testing** - Provides built-in API steps (requests, responses, parameter replacement)
- **web-testing** - Provides Page Object Model, PageElement hierarchy, built-in web steps
- **behave** - Extends Behave framework with BDDKit-specific features

### Works With

- **tags-management** - Driver lifecycle tags (@no_driver, @reuse_driver, @reset_driver)

### When to Use

- Consult **bddkit-usage** for BDDKit library API reference (built-in steps, map_param, PageObject API)
- Consult **api-testing** for API testing workflow using BDDKit steps
- Consult **web-testing** for Web testing workflow using BDDKit POM
- Consult **behave** for core Behave patterns that BDDKit builds upon

## Library Structure

```
bddkit/
├── behave/
│   ├── environment.py          # Full lifecycle hooks (before_all, after_scenario, etc.)
│   ├── env_utils.py            # DynamicEnvironment for feature-description steps
│   ├── behave_exec.py          # CLI execution entry point
│   └── steps/
│       ├── api/                # Built-in API steps (requests + responses)
│       ├── web/                # Built-in Web steps (driver actions + POM)
│       ├── mockito/            # Built-in mock server steps
│       └── performance/        # Load testing steps (Locust-based)
├── pageobjects/                # PageObject base classes
├── pageelements/               # PageElement hierarchy (Button, InputText, Select, etc.)
├── utils/
│   ├── dataset.py              # map_param, replace_param, context storage
│   ├── pom_manager.py          # Dynamic page object loading
│   ├── driver_utils.py         # Selenium driver helpers
│   ├── driver_wait_utils.py    # Explicit wait utilities
│   ├── data_generator.py       # Random data generation
│   └── youtrack/               # YouTrack TMS integration
├── config_driver.py            # Driver configuration
├── config_files.py             # Config file management
├── config_parser.py            # Extended config parser
└── driver_wrapper.py           # Driver wrapper and pool
```

## Core Concepts

### 1. Built-in Steps - Always Reuse First

BDDKit provides complete step catalogs. Import them in `steps/__init__.py`:

```python
from bddkit.behave.steps.api import requests, responses
from bddkit.behave.steps.web.driver import actions
from bddkit.behave.steps.web.pom import manager
from bddkit.behave.steps.mockito import mockito
```

See reference files for complete step catalogs by domain.

### 2. Parameter Replacement System

All BDDKit steps pass string parameters through `map_param()` for dynamic resolution:

| Tag                       | Source               | Example                             |
|---------------------------|----------------------|-------------------------------------|
| `[CONF:key]`              | Project config JSON  | `[CONF:api.base_url]`               |
| `[BDDKIT:section_option]` | properties.cfg       | `[BDDKIT:Server_api_url]`           |
| `[CONTEXT:key]`           | Context storage      | `[CONTEXT:auth_token]`              |
| `[ENV:var]`               | Environment variable | `[ENV:API_TOKEN]`                   |
| `[DATA_TEST:file::key]`   | YAML test data       | `[DATA_TEST:users::admin.username]` |
| `[LANG:file::key]`        | Language YAML        | `[LANG:messages::login_success]`    |

Generation patterns via `replace_param()`: `[UUID]`, `[RANDOM]`, `[NOW]`, `[TODAY]`, `[STRING_WITH_LENGTH_XX]`, etc.

### 3. Environment Hooks

Import BDDKit's environment directly or extend it:

```python
# Minimal: full BDDKit lifecycle
from bddkit.behave.environment import *

# Extended: add project-specific logic
from bddkit.behave import environment as bddkit_env
def before_all(context):
    bddkit_env.before_all(context)
    # project setup here
```

### 4. Page Object Model

Hierarchy: `CommonObject` -> `PageObject` / `PageElement` -> specialized elements.

```python
from bddkit.pageobjects.page_object import PageObject
from bddkit.pageelements import InputText, Button, Text

class LoginPageObject(PageObject):
    def init_page_elements(self):
        self.username = InputText(By.ID, 'username', wait=True)
        self.password = InputText(By.ID, 'password')
        self.submit = Button(By.CSS_SELECTOR, 'button[type="submit"]')
```

Load dynamically: `context.use_pageobject('Login')` -> `context.page`

### 5. Context Storage (3 Levels)

| Level    | Scope            | Store with                     |
|----------|------------------|--------------------------------|
| Scenario | Current scenario | `context.storage['key'] = val` |
| Feature  | Across scenarios | `[FEATURE:key]`                |
| Run      | Across features  | `[RUN:key]`                    |

`context.storage` is a `ChainMap`: scenario -> feature -> run.

### 6. Driver Lifecycle Tags

| Tag             | Scope            | Effect                        |
|-----------------|------------------|-------------------------------|
| `@no_driver`    | Feature/Scenario | Skip browser driver           |
| `@reuse_driver` | Feature          | Reuse driver across scenarios |
| `@reset_driver` | Scenario         | Force driver restart          |

## Key Rules

1. Always check BDDKit built-in steps before writing custom ones
2. Use `map_param()` on all string parameters in custom steps
3. Import BDDKit environment hooks, do not rewrite them from scratch
4. Define page elements in `init_page_elements()`, not `__init__`
5. Mark key elements with `wait=True` for automatic `wait_until_loaded()` detection
6. Use `context.store_key_in_storage()` with proper scope prefixes
7. Prefer explicit waits (`wait_until_visible`) over `time.sleep()`
8. Use `@no_driver` tag for API-only tests to avoid browser startup

## References

### Step Catalogs (from BDDKit source)

- **[`references/api-steps.md`](references/api-steps.md)** - Complete API step definitions (request config, execution,
  response validation)
- **[`references/web-steps.md`](references/web-steps.md)** - Web driver actions and POM manager steps
- **[`references/mockito-steps.md`](references/mockito-steps.md)** - Mock server lifecycle, stub registration,
  verification

### API Reference (from BDDKit source)

- **[`references/page-elements-api.md`](references/page-elements-api.md)** - PageElement hierarchy: Button, InputText,
  Select, Link, Checkbox, etc.
- **[`references/page-objects-api.md`](references/page-objects-api.md)** - PageObject, CommonObject, POM manager
- **[`references/dataset-replacements.md`](references/dataset-replacements.md)** - Complete map_param tags and
  replace_param patterns

### Configuration & Patterns

- **[`references/configuration.md`](references/configuration.md)** - Properties files, environment selection, execution
  commands
- **[`references/environment-hooks.md`](references/environment-hooks.md)** - BDDKit environment lifecycle, context
  attributes, DynamicEnvironment
- **[`references/common-steps.md`](references/common-steps.md)** - Step reuse conventions and project-specific step
  patterns
- **[`references/utilities.md`](references/utilities.md)** - Utility modules overview (dataset, waits, POM manager,
  YouTrack)