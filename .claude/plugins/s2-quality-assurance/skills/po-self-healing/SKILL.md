---
name: po-self-healing
description: This skill should be used when the user asks to "self-heal page objects", "sync pageobjects from HTML", "create pageobject from HTML", "update page objects from HTML", "generate pageobjects from HTML", "heal page objects", or needs to keep `pageobjects/*.py` in sync with HTML source files in `resources/base_html/`. Reads HTML files directly to extract interactive elements, maps them to BDDKit types, creates or updates the corresponding Python page object file, and warns when elements lack stable locators (data-testid or id).
---

# Skill: po-self-healing

Keeps `pageobjects/*.py` synchronized with HTML source files in `resources/base_html/`. For each HTML file, create or update the associated Python page object using BDDKit conventions by reading the HTML directly. Identify elements that lack stable locators (`data-testid` or `id`) and advise on improvements.

## Context

This skill operates within a BDD testing project using BDDKit and Behave. Page objects extend `bddkit.pageobjects.PageObject` and define all elements inside `init_page_elements()`.

**Related skills:**
- **web-testing** — Page object naming and structural conventions
- **bddkit-usage** — BDDKit `PageObject` and `PageElement` API

## File Naming Convention

The HTML filename (without extension) determines the page object file and class name:

```
resources/base_html/login.html      →  pageobjects/login.py      (LoginPageObject)
resources/base_html/dashboard.html  →  pageobjects/dashboard.py  (DashboardPageObject)
```

## Self-Healing Workflow

### Step 1: Discover HTML Files

Use the Glob tool to find all HTML sources:

```
Glob: resources/base_html/**/*.html
```

Process each file individually, or only the file specified by the user.

### Step 2: Read and Analyze the HTML
**Large / compiled HTML files:** Frontend frameworks (Vue, React, Angular) produce minified single-file HTML snapshots that can exceed the Read tool token limit (25 000 tokens). Always try `Read` first; if it fails with a token-limit error, switch to targeted `Bash grep` commands instead:

```bash
# 1. All data-testid values (stable locators)
grep -o 'data-testid="[^"]*"' resources/base_html/<name>.html | sort -u

# 2. All multiselect filter labels
grep -oi 'multiselect-filter__label">[^<]*<' resources/base_html/<name>.html | sort -u

# 3. All input-switch labels
grep -oi 's2-input-switch__label[^>]*>[^<]*<' resources/base_html/<name>.html | sort -u

# 4. Exact CSS class on multiselect wrapper (affects locator strategy)
grep -oi 'class="multiselect-filter__wrapper[^"]*"' resources/base_html/<name>.html | sort -u
```

Then use Python to inspect element types when needed:

```bash
python3 - <<'EOF'
import re
with open('resources/base_html/<name>.html', encoding='utf-8') as f:
    content = f.read()
for testid in ['my-field-testid']:
    idx = content.find(f'data-testid="{testid}"')
    if idx >= 0:
        snippet = content[idx:idx+300]
        print(testid, re.findall(r'class="([^"]*)"', snippet)[:3])
EOF
```

Use the Read tool to load the HTML file content. Then scan it directly for interactive elements:

- `<input>` — all types except `hidden`
- `<button>`, `<textarea>`, `<select>`, `<a>`
- `<label>`, `<span>`, `<p>`, `<h1>`–`<h6>` — only those with a meaningful `id`, `data-testid`, or stable class
- `<div>`, `<section>`, `<header>` — only when acting as containers for interactive children

For each element, determine:
1. **BDDKit type** — from the element tag and attributes (see mapping table below)
2. **Best locator** — from the attribute priority order (see `references/locator-strategy.md`)
3. **Variable name** — snake_case derived from `data-testid` > `id` > `name` > `aria-label` > `placeholder`
4. **Locator quality tier** — excellent / good / fair / poor

See `references/html-analysis.md` for detailed extraction rules and variable naming.

### Step 3: Check Existing Page Object

Use the Read tool to load `pageobjects/<name>.py` if it exists:

- **Does not exist** → create the file from scratch with all extracted elements
- **Exists** → compare `init_page_elements()` body against extracted elements; add new ones, flag stale ones

### Step 4: Create or Update the Page Object

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

**Rules:**
- Class: `<PascalCase>PageObject` — all elements in `init_page_elements()`, never in `__init__`
- Mark the first key visible element `wait=True` for `wait_until_loaded()` detection
- Use `Group` for container elements that hold nested interactive children
- Import only the BDDKit types actually used

**When updating an existing file:**
- Append new elements at the end of `init_page_elements()`
- Flag elements absent from HTML with `# ⚠ NOT FOUND IN HTML — review`
- Never delete existing entries automatically — always flag for human review
- Preserve any custom interaction methods defined outside `init_page_elements()`

### Step 5: Report Locator Quality

Produce a report for each processed file:

```
📄 login.html → pageobjects/login.py  [created | updated]
   Elements: 5  ✅ excellent: 3  ✓ good: 1  ⚠ fair: 1  ❌ poor: 0

   ⚠ Locator advice (share with developers):
   - <input name="search_query"> — no data-testid or id
     Suggestion: data-testid="search-query-input"
```

## HTML Element → BDDKit Type Mapping

| HTML element | Condition | BDDKit type |
|---|---|---|
| `<input type="text/email/password/number/search/tel/url/date/...">` | Text input | `InputText` |
| `<textarea>` | Multiline input | `InputText` |
| `<input type="checkbox">` | | `Checkbox` |
| `<input type="radio">` | | `InputRadio` |
| `<button>`, `<input type="submit/button/reset">` | | `Button` |
| `<select>` | | `Select` |
| `<a>` | Anchor/navigation | `Link` |
| `<span>`, `<p>`, `<label>`, `<h1>`–`<h6>` | Visible text | `Text` |
| `<div>`, `<section>`, `<header>` with children | Container | `Group` |
| `<input type="hidden">` | | **Skip** |

## Project-Specific Custom Elements (`pageelements/`)

Before using generic BDDKit types, check whether the project has a `pageelements/` folder with custom element classes. When present, prefer them over `Group` or `PageElement`:

| CSS class on element | Custom type | Import |
|---|---|---|
| `s2-input-tags` | `MultiSelect` | `from pageelements.multi_select import MultiSelect` |
| `s2-input-select` | `InputSelect` | `from pageelements.input_select import InputSelect` |
| `s2-input-switch` | `InputSwitch` | `from pageelements.input_switch import InputSwitch` |
| `s2-input-date` | `InputDate` | `from pageelements.input_date import InputDate` |
| Table container | `Table` | `from pageelements.table_element import Table` |
| Filter dropdown trigger | `PopperSelect` | `from pageelements.popper_select import PopperSelect` |

Detect the CSS class by inspecting the element in the HTML snapshot (see grep commands in Step 2).

### Multi-instance filters (same `data-testid` repeated)

When multiple filter components share the same `data-testid` (e.g., `data-testid="multiselect-filter"` appears on every `MultiSelect` on the page), differentiate them by their visible label text using an ancestor XPath:

```python
# Use normalize-space() instead of text() to avoid MultiSelect's auto-wrapping behaviour
self.filter_sector = MultiSelect(By.XPATH,
    "//*[@class='multiselect-filter__label'][normalize-space()='Sector']"
    "/ancestor::*[@class='multiselect-filter__wrapper']")
```

> **Important:** Check the exact wrapper CSS class first — it differs between page types:
> - Filter sidebar / management pages: `multiselect-filter__wrapper w-full`
> - View/read-only pages: `multiselect-filter__wrapper` (no `w-full`)
>
> Using `text()` in the XPath string triggers `MultiSelect.__init__`'s auto-wrapping with the hard-coded `w-full` class, which will fail on view pages. Always use `normalize-space()`.

### InputText inside wrapper divs

When a `data-testid` is on the wrapper `<div>` rather than on the `<input>` itself (e.g., `s2-input-text`), append `//input` to reach the actual input element:

```python
self.filter_agency = InputText(By.XPATH, "//*[@data-testid='advanced-search-text-name']//input")
self.agency_name   = InputText(By.XPATH, "//*[@data-testid='edit-panel-agency-agency-name']//input")
```

### Aside panels (common pattern)

Pages with slide-in panels (create/edit forms, detail views) use `data-testid="aside"` as the root container and standard button testids:

```python
self.aside_panel  = PageElement(By.XPATH, "//aside[@data-testid='aside']")
self.aside_close  = Button(By.XPATH, "//button[@data-testid='aside-close-button']")
self.aside_cancel = Button(By.XPATH, "//button[@data-testid='aside-cancel-button']")
self.aside_confirm = Button(By.XPATH, "//button[@data-testid='aside-confirm-button']")
```

## Additional Resources

### Reference Files
- **`references/html-analysis.md`** — Element extraction rules, variable naming, locator quality tiers, and diff logic for existing page objects
- **`references/locator-strategy.md`** — Locator priority guide, data-testid naming conventions, and advice message templates

### BDDKit API
- **`../bddkit-usage/references/page-elements-api.md`** — BDDKit PageElement hierarchy (Button, InputText, Select, etc.)
- **`../bddkit-usage/references/page-objects-api.md`** — PageObject API (init_page_elements, wait_until_loaded)