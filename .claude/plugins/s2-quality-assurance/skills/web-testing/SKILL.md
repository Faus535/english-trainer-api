---
name: web-testing
description: This skill should be used when the user asks to "write web tests", "create page objects", "define page elements", "test web UI", "use Page Object Model", or works with web test scenarios in `features/web/`, `pageobjects/`, or `steps/web/`. Defines how to write Web feature files, use BDDKit's Page Object Model, manage page elements, and create Web step definitions for BDD testing.
---

# Skill: Web Testing

Defines the patterns for writing BDD tests for Web UI applications using BDDKit's page object model and built-in web
steps.

## Skill Relationships

### Extends

- **behave** - Uses core Behave framework patterns (feature files, Given-When-Then, scenario structure)
- **bddkit-usage** - Uses BDDKit's Page Object Model, built-in web steps, and driver lifecycle

### Related

- **tags-management** - Apply tags to web test scenarios (@web, @reuse_driver, @reset_driver, status tags)

### When to Use

- Choose **web-testing** for browser-based UI testing (features/web/, pageobjects/, pageelements/, steps/web/)
- Consult **behave** for core feature file patterns (Secenario, Scenario Outline, Environment hooks)
- Consult **bddkit-usage** for BDDKit library API reference (PageObject, PageElement hierarchy, built-in web steps)

## BDDKit Web Steps & POM - Use First

BDDKit provides built-in web steps and a complete Page Object Model. Import them:

```python
# steps/__init__.py or steps/web/__init__.py
from bddkit.behave.steps.web.driver import actions
from bddkit.behave.steps.web.pom import manager
```

This gives access to: `I open url "{url}" in browser`, `I refresh the page`, `I wait until URL is "{url}"`,
`the "{name}" page is loaded`.

See the **bddkit-usage** skill's `references/web-steps.md` for the complete catalog.

## Key rules

### Web feature files

1. Place all Web features in `features/web/`
2. Tag with `@web` at feature level
3. Use `Background` for common navigation: `Given I open url "[CONF:base_url]" in browser`
4. Scenarios should test one user flow or interaction
5. Step text should describe user actions, not implementation details
6. Use `@reuse_driver` to share browser across scenarios, `@reset_driver` to force restart

### Page Object Model (BDDKit base classes)

1. Extend `bddkit.pageobjects.page_object.PageObject`
2. One page object class per page or major component
3. Place in `pageobjects/` directory
4. Naming: `<Name>PageObject` class in `<name>.py` file
5. Define all elements in `init_page_elements()` method (not `__init__`)
6. Mark key elements with `wait=True` for `wait_until_loaded()` detection
7. Page objects encapsulate interactions — step definitions contain assertions
8. Load dynamically: `context.use_pageobject('Login')` -> `context.page`

### Page elements (BDDKit hierarchy)

Use BDDKit's element types from `bddkit.pageelements`:

- `InputText` - Text inputs (has `.text`, `.clear()`, `.click()`)
- `Button` - Clickable buttons (has `.text`, `.click()`)
- `Select` - Dropdowns (has `.option` getter/setter, `.selenium_select`)
- `Text` - Read-only text elements
- `Link`, `Checkbox`, `InputRadio` - Specialized elements
- `Group` - Container for nested elements

Constructor: `ElementType(By.LOCATOR, 'value', parent=None, order=None, wait=False, shadowroot=None)`

All elements inherit: `is_present()`, `is_visible()`, `wait_until_visible()`, `wait_until_clickable()`,
`get_attribute()`, `scroll_element_into_view()`

### Web step definitions

1. Place in `steps/web/` directory
2. One step file per page or user flow
3. Naming: `<page>_web_steps.py` (e.g., `login_web_steps.py`)
4. Steps delegate to page objects — no direct browser interaction in steps
5. Reuse BDDKit built-in steps and common steps from `steps/web/__init__.py`
6. Use `map_param()` on all string parameters

## References

### Project-Specific Patterns

- [Page Objects](references/page-objects.md)
- [Page Elements](references/page-elements.md)
- [Web Step Definitions](references/web-step-definitions.md)
- [Web Interactions](references/web-interactions.md)

### BDDKit Library (from bddkit-usage skill)

- **`../bddkit-usage/references/web-steps.md`** - Complete built-in web step catalog
- **`../bddkit-usage/references/page-elements-api.md`** - PageElement hierarchy API (Button, InputText, Select, etc.)
- **`../bddkit-usage/references/page-objects-api.md`** - PageObject, CommonObject, POM manager API
- **`../bddkit-usage/references/dataset-replacements.md`** - Parameter replacement patterns
- **`../bddkit-usage/references/environment-hooks.md`** - Driver lifecycle and context attributes