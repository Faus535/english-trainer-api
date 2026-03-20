# Locator Strategy for Page Object Elements

Reference for selecting locators when generating or updating `pageobjects/*.py`, and for advising developers when stable locators are missing.

## Locator Priority Order

Always use the highest quality locator available:

```
1. data-testid  →  By.CSS_SELECTOR  '[data-testid="value"]'    ← best
2. id           →  By.ID            'value'
3. name         →  By.CSS_SELECTOR  'tag[name="value"]'
4. CSS class    →  By.CSS_SELECTOR  '.class-name'
5. XPath        →  By.XPATH         '//tag[...]'               ← last resort
```

## Locator Patterns

### data-testid (excellent)

```python
self.login_button = Button(By.CSS_SELECTOR, '[data-testid="login-submit"]')
self.username = InputText(By.CSS_SELECTOR, '[data-testid="username-input"]', wait=True)
```

Preferred — survives CSS refactoring and DOM structure changes.

### id (good)

```python
self.email = InputText(By.ID, 'email', wait=True)
self.results = Text(By.ID, 'search-results')
```

Stable unless the developer renames the id. Avoid for auto-generated IDs (e.g., `ng-42`, `react-root-3`).

### name attribute — CSS (fair)

```python
self.password = InputText(By.CSS_SELECTOR, 'input[name="password"]')
self.country = Select(By.CSS_SELECTOR, 'select[name="country"]')
```

Include the tag to avoid ambiguity. Annotate with a comment recommending a `data-testid`.

### CSS class (poor)

```python
# poor — CSS class only; consider adding data-testid="error-message"
self.error_message = Text(By.CSS_SELECTOR, '.error-message')
```

Use only semantic, stable class names. Avoid utility classes (`mt-4`, `text-blue-500`).

### XPath (last resort)

```python
# poor — XPath fallback; consider adding data-testid="accept-button"
self.accept_btn = Button(By.XPATH, '//button[normalize-space()="Accept"]')
```

Document why XPath was necessary with an inline comment.

## Annotating Locator Quality in Generated Code

When the locator quality is `fair` or `poor`, add a comment to make future improvements visible:

```python
def init_page_elements(self):
    # excellent — data-testid
    self.search_input = InputText(By.CSS_SELECTOR, '[data-testid="search-input"]', wait=True)

    # good — id
    self.results_container = Text(By.ID, 'search-results')

    # fair — name only; consider adding data-testid="filter-select"
    self.filter_select = Select(By.CSS_SELECTOR, 'select[name="filter"]')

    # poor — CSS class only; consider adding data-testid="load-more-button"
    self.load_more_button = Button(By.CSS_SELECTOR, '.load-more-btn')
```

## data-testid Naming Convention

When advising developers, follow this naming pattern:

```
<context>-<element-role>
```

| Element | Suggested data-testid |
|---------|----------------------|
| Login submit button | `login-submit` |
| Username text input | `username-input` |
| Password text input | `password-input` |
| Error message | `error-message` |
| Navigation home link | `nav-home-link` |
| Country dropdown | `country-select` |
| Accept terms checkbox | `accept-terms-checkbox` |
| Search input | `search-input` |
| Modal close button | `modal-close` |

**Rules:**
- kebab-case only
- Include element role as suffix: `-input`, `-button`, `-link`, `-select`, `-checkbox`
- Unique per page
- No dynamic IDs (row numbers, record IDs)

## Advice Message Templates

Use these in the locator quality report section of the output.

### No data-testid and no id

```
⚠ <{tag} {key_attrs}> (var: '{var_name}') — no data-testid or id found.
  Current locator: {by}, '{value}' (quality: {quality})
  Recommendation: ask the development team to add:
    data-testid="{suggested_testid}"
  This decouples the locator from CSS and DOM structure changes.
```

### Has id but no data-testid

```
ℹ <{tag} id="{id}"> (var: '{var_name}') — using id (quality: good).
  Optional: add data-testid="{suggested_testid}" for extra resilience.
```

### Only CSS class

```
⚠ <{tag} class="{classes}"> (var: '{var_name}') — only CSS class available (quality: poor).
  Recommendation: ask the development team to add:
    data-testid="{suggested_testid}"
  CSS classes may change during styling updates.
```
## Platform-Specific XPath Patterns

Patterns confirmed on the S2 platform (Vue.js SPA with custom component library):

### Advanced search filters (management/table pages)

```python
# Filter dropdown trigger (opens the filter selector popover)
self.add_filters = PopperSelect(By.XPATH, "//*[@data-testid='advanced-search-dropdown-button']//button")

# Text filter (data-testid on wrapper div, input is a child)
self.filter_name = InputText(By.XPATH, "//*[@data-testid='advanced-search-text-name']//input")

# Multi-select filter (data-testid is directly on the MultiSelect wrapper)
self.filter_status = MultiSelect(By.XPATH, "//*[@data-testid='advanced-search-selector-status']")

# Date filter
self.filter_date = InputDate(By.XPATH, "//*[@data-testid='advanced-search-date-createdOn']")
```

### Aside (slide-in) panels

Panels for create/edit/detail actions follow a shared pattern across all pages:

```python
self.aside_panel   = PageElement(By.XPATH, "//aside[@data-testid='aside']")
self.aside_close   = Button(By.XPATH, "//button[@data-testid='aside-close-button']")
self.aside_cancel  = Button(By.XPATH, "//button[@data-testid='aside-cancel-button']")
self.aside_confirm = Button(By.XPATH, "//button[@data-testid='aside-confirm-button']")
```

### Edit form fields (s2-input-text wrapper)

```python
# Text input — testid on wrapper, input is a descendant
self.name = InputText(By.XPATH, "//*[@data-testid='edit-panel-<entity>-<field>']//input")

# Single-select dropdown (s2-input-select)
self.country = InputSelect(By.XPATH, "//*[@data-testid='edit-panel-<entity>-<field>']")

# Multi-tag input (s2-input-tags)
self.tools = MultiSelect(By.XPATH, "//*[@data-testid='edit-panel-<entity>-<field>']")
```

### Page header / loader / table

```python
self.title  = Text(By.XPATH, "//*[@class='s2-page-header-classic__title-piece']/span")
self.loader = PageElement(By.XPATH, "//*[contains(@class,'v-bounce1')] | //*[contains(@class,'v-bounce2')]")
self.table  = Table(By.XPATH, "//*[@data-testid='<entity>-table']")
```

### Toggle switches

```python
# Single switch on page — CLASS_NAME is sufficient
self.group_by_country = InputSwitch(By.CLASS_NAME, "s2-input-switch")

# Multiple switches — disambiguate by label text
self.show_inactive = InputSwitch(By.XPATH,
    "//*[@class='s2-input-switch__label ml-2'][normalize-space()='Mostrar inactivos']"
    "/ancestor::*[@class='s2-input-switch']")
```

### Buttons with empty or missing data-testid

If a button has `data-testid=""` (empty string), treat it as **no testid** and look for the real testid in the HTML snapshot again. Do not write `//button[@data-testid='']` — this matches all buttons with an empty attribute and is unreliable:

```python
# ❌ Wrong — matches any button with empty testid
self.export_button = Button(By.XPATH, "//button[@data-testid='']")

# ✅ Correct — use the real testid found in the HTML
self.export_button = Button(By.XPATH, "//button[@data-testid='export-agencies']")
```

## Developer Summary Template

When generating the report, include a block for developers if any elements are missing stable locators:

```
⚠ Locator improvement recommendations for resources/base_html/{name}.html:

The following elements would benefit from data-testid attributes to improve
test stability. These attributes have no effect on user-facing behaviour.

  <input name="q">             →  add  data-testid="search-input"
  <button class="btn-primary"> →  add  data-testid="header-cta-button"
  <div class="alert">          →  add  data-testid="form-error-banner"
```