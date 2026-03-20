---
name: s2-po-self-healing
description: Creates and updates page objects in pageobjects/*.py from HTML source files in resources/base_html/. For each HTML file, creates the corresponding PageObject if missing or updates it if elements have changed. Reports locator quality and advises when data-testid or id attributes are absent.
---

# Page Object Self-Healing

Synchronizes `pageobjects/*.py` with HTML source files in `resources/base_html/`. Creates missing page objects and updates existing ones based on the interactive elements found in the HTML. Reads HTML files directly — no external script required.

## Usage

```
/s2-po-self-healing               # Process all HTML files in resources/base_html/
/s2-po-self-healing login         # Process only resources/base_html/login.html
```

## Behavior

### 1. Discover HTML Files

- If a filename argument is provided, target `resources/base_html/<argument>.html` only.
- Otherwise, use the Glob tool to find all `.html` files under `resources/base_html/`.
- Process files one by one.

### 2. Read and Analyze Each HTML File

Use the Read tool to load the HTML content. Scan it directly to extract all interactive elements:

- **Always extract:** `<input>` (all types except `hidden`), `<button>`, `<select>`, `<textarea>`, `<a>`
- **Extract if they have a stable locator:** `<label>`, `<span>`, `<p>`, `<h1>`–`<h6>`
- **Extract as `Group`:** `<div>`, `<section>`, `<header>` only when they contain interactive children and have a `data-testid` or `id`

For each element, determine:

| Property | Source |
|----------|--------|
| **BDDKit type** | Tag + `type` attribute — see mapping table below |
| **Best locator** | `data-testid` > `id` > `name` > CSS class > XPath |
| **Variable name** | snake_case from: `data-testid` > `id` > `name` > `aria-label` > `placeholder` > `<tag>_N` |
| **Locator quality** | excellent / good / fair / poor |

Locator quality tiers:
- **excellent** — `data-testid` present
- **good** — `id` present (not auto-generated like `ng-123`, `react-root-4`)
- **fair** — only `name` or semantic CSS class
- **poor** — only structural CSS or XPath

### 3. Check for Existing Page Object

Use the Read tool to check if `pageobjects/<name>.py` exists.

- **Does not exist** → create the file from scratch with all extracted elements
- **Exists** → read it and compare `init_page_elements()` against extracted elements:
  - Element in HTML but not in Python → **add** it at the end of `init_page_elements()`
  - Element in Python but not in HTML → **flag** with `# ⚠ NOT FOUND IN HTML — review`
  - Best locator changed → **update** the locator, add `# ⚠ Locator updated from HTML`
  - BDDKit type mismatch → **flag** with `# ⚠ HTML indicates type <NewType> — verify`
  - **Never delete** existing entries automatically — flag for human review
  - **Preserve** any custom interaction methods defined outside `init_page_elements()`

### 4. Write the Page Object

```python
# pageobjects/login.py
from selenium.webdriver.common.by import By
from bddkit.pageobjects.page_object import PageObject
from bddkit.pageelements import InputText, Button, Text


class LoginPageObject(PageObject):
    """Page object for login page. Source: resources/base_html/login.html."""

    def init_page_elements(self):
        self.username = InputText(By.CSS_SELECTOR, '[data-testid="username-input"]', wait=True)
        self.password = InputText(By.ID, 'password')
        self.submit_button = Button(By.CSS_SELECTOR, '[data-testid="login-submit"]')
        self.error_message = Text(By.CSS_SELECTOR, '.error-message')
```

Rules:
- Class name: `<PascalCase>PageObject` — `login.html` → `LoginPageObject`
- All elements defined inside `init_page_elements()`, never in `__init__`
- Mark the first key visible element with `wait=True`
- Import only the BDDKit types actually used in the file
- For fair/poor quality locators, add a comment:

```python
# fair — name only; consider adding data-testid="filter-select"
self.filter_select = Select(By.CSS_SELECTOR, 'select[name="filter"]')

# poor — CSS class only; consider adding data-testid="load-more-button"
self.load_more = Button(By.CSS_SELECTOR, '.load-more-btn')
```

### 5. Print Report

After processing all files, output a summary:

```
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
  Page Object Self-Healing Report
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

📄 login.html → pageobjects/login.py  [created]
   Elements: 5  ✅ excellent: 3  ✓ good: 1  ⚠ fair: 1  ❌ poor: 0

📄 dashboard.html → pageobjects/dashboard.py  [updated]
   Elements: 8  ✅ excellent: 6  ✓ good: 2  ⚠ fair: 0  ❌ poor: 0
   ⚠ Stale elements flagged: 1  (header_old_nav)

━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
  Locator Advice (share with developers)
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

  login.html:
  - <input name="search_query">  →  add  data-testid="search-query-input"

  Total: 2 files  |  Created: 1  |  Updated: 1
```

## HTML Element → BDDKit Type Mapping

| HTML element | Condition | BDDKit type |
|---|---|---|
| `<input type="text/email/password/number/search/tel/url/date/...">` | Text input | `InputText` |
| `<textarea>` | Multiline | `InputText` |
| `<input type="checkbox">` | | `Checkbox` |
| `<input type="radio">` | | `InputRadio` |
| `<button>`, `<input type="submit/button/reset">` | | `Button` |
| `<select>` | | `Select` |
| `<a>` | Anchor/navigation | `Link` |
| `<span>`, `<p>`, `<label>`, `<h1>`–`<h6>` | Visible text | `Text` |
| `<div>`, `<section>`, `<header>` with children | Container | `Group` |
| `<input type="hidden">` | | **Skip** |

## References

- Skill: [po-self-healing](../skills/po-self-healing/SKILL.md)
- Skill: [web-testing](../skills/web-testing/SKILL.md)
- BDDKit API: [page-elements-api](../skills/bddkit-usage/references/page-elements-api.md)
- BDDKit API: [page-objects-api](../skills/bddkit-usage/references/page-objects-api.md)