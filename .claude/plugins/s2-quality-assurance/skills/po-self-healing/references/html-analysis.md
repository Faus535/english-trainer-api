# HTML Analysis for Page Object Generation

Rules for reading HTML source files in `resources/base_html/` and extracting the elements needed to generate or update `pageobjects/*.py`.

## Handling Large HTML Files

Frontend frameworks produce minified, compiled HTML files that often exceed the Read tool token limit. Use this strategy:

**Step 1 — try Read.** If the file exceeds ~25 000 tokens, the tool returns an error. Switch to grep.

**Step 2 — extract with grep:**

```bash
# All data-testid values (primary stable locators)
grep -o 'data-testid="[^"]*"' resources/base_html/<name>.html | sort -u

# Multiselect filter labels (for pages with multiple unnamed filters)
grep -oi 'multiselect-filter__label">[^<]*<' resources/base_html/<name>.html | sort -u

# Input-switch labels
grep -oi 's2-input-switch__label[^>]*>[^<]*<' resources/base_html/<name>.html | sort -u

# Exact multiselect wrapper class (critical for locator strategy)
grep -oi 'class="multiselect-filter__wrapper[^"]*"' resources/base_html/<name>.html | sort -u
```

**Step 3 — detect element type for each testid** (when needed):

```bash
python3 - <<'EOF'
import re
with open('resources/base_html/<name>.html', encoding='utf-8') as f:
    content = f.read()
for testid in ['field-testid-1', 'field-testid-2']:
    idx = content.find(f'data-testid="{testid}"')
    if idx >= 0:
        snippet = content[idx:idx+300]
        classes = re.findall(r'class="([^"]*)"', snippet)[:3]
        inputs  = re.findall(r'<input[^>]*>', snippet)[:1]
        print(f"{testid}: classes={classes}, inputs={inputs}")
EOF
```

CSS class → custom element type mapping:

| CSS class | Custom type | Notes |
|---|---|---|
| `s2-input-text` | `InputText` | wrapper div; use `//input` suffix for locator |
| `s2-input-tags` | `MultiSelect` | multi-tag input |
| `s2-input-select` | `InputSelect` | single-select dropdown |
| `s2-input-switch` | `InputSwitch` | toggle switch |
| `s2-input-date` | `InputDate` | date picker |
| `s2-search-engine` | `InputText` + `Button` | search bar; use CLASS_NAME on inner elements |

## Multi-Instance Filters (Same `data-testid` Repeated)

Some pages render multiple filter components that share the same `data-testid` on their inner trigger (e.g., every `MultiSelect` has `data-testid="multiselect-filter"` internally). Differentiate them by their visible label text using an ancestor XPath with `normalize-space()`:

```python
# ✅ Correct — avoids MultiSelect's text() auto-wrapping; targets the exact wrapper class
self.filter_sector = MultiSelect(By.XPATH,
    "//*[@class='multiselect-filter__label'][normalize-space()='Sector']"
    "/ancestor::*[@class='multiselect-filter__wrapper']")

# ❌ Wrong — using text() triggers MultiSelect.__init__ to append w-full ancestor
self.filter_sector = MultiSelect(By.XPATH, "//*[text()='Sector']")
```

**Wrapper class varies by page type — always check before writing the XPath:**

```bash
grep -oi 'class="multiselect-filter__wrapper[^"]*"' resources/base_html/<name>.html | sort -u
# Management/table pages → "multiselect-filter__wrapper w-full"
# View/read-only pages   → "multiselect-filter__wrapper"
```

Use `MultiSelect(By.XPATH, "//*[@data-testid='advanced-search-selector-xxx']")` whenever each filter has its own unique `data-testid` — that is always preferred over label-based XPath.

## InputText Inside Wrapper Divs

When a `data-testid` sits on a wrapper `<div>` (CSS class `s2-input-text`) rather than on the `<input>` itself, append `//input` to reach the actual input:

```python
# data-testid is on the wrapper div → need //input suffix
self.filter_name = InputText(By.XPATH, "//*[@data-testid='advanced-search-text-name']//input")
self.agency_name = InputText(By.XPATH, "//*[@data-testid='edit-panel-agency-agency-name']//input")

# data-testid directly on <input> → no suffix needed
self.search_input = InputText(By.CSS_SELECTOR, '[data-testid="search-input"]')
```

## Elements to Extract

Scan for elements representing user-facing interactions or visible content:

| Tag | Conditions | Notes |
|-----|-----------|-------|
| `<input>` | All types except `hidden` | See type table in SKILL.md |
| `<textarea>` | All | Treat as `InputText` |
| `<button>` | All | Always `Button` |
| `<select>` | All | Always `Select` |
| `<a>` | Any | `Link` |
| `<label>`, `<span>`, `<p>`, `<h1>`–`<h6>` | With meaningful id, data-testid, or class | `Text` |
| `<div>`, `<section>`, `<header>` | Contains interactive children | `Group` |

**Skip:** `<input type="hidden">`, `<script>`, `<style>`, `<meta>`, structural wrappers with no locator.

## Variable Name Generation

Generate a `snake_case` Python variable name using this priority:

1. `data-testid` value
2. `id` value
3. `name` attribute
4. `aria-label`
5. `placeholder` (truncate to 30 chars)
6. Fallback: `<tag>_<index>` (e.g., `button_3`)

**Conversion rules:**
- Replace non-alphanumeric characters with `_`
- Collapse multiple underscores, strip leading/trailing
- Lowercase everything
- Append `_2`, `_3` etc. for duplicates within the same file

**Examples:**
```
data-testid="login-submit"    → login_submit
id="user_email"               → user_email
name="searchQuery"            → search_query
aria-label="Close modal"      → close_modal
placeholder="Enter email..."  → enter_email
```

## Locator Extraction Per Element

Pick the highest quality locator available for each element:

| Attribute | By strategy | Quality |
|---|---|---|
| `data-testid` | `By.CSS_SELECTOR, '[data-testid="value"]'` | excellent |
| `id` | `By.ID, 'value'` | good |
| `name` | `By.CSS_SELECTOR, 'tag[name="value"]'` | fair |
| CSS class (semantic) | `By.CSS_SELECTOR, '.class-name'` | poor |
| Structural XPath | `By.XPATH, '//...'` | poor |

**Auto-generated IDs to treat as `fair`:** patterns like `ng-123`, `react-root-4`, `ember-view-7`.

## Handling Nested Elements (Group)

When a container holds multiple interactive children:

1. Create a `Group` for the container (only if it has a stable locator)
2. Create individual entries for the children
3. Pass the group's locator as `parent` for children that need disambiguation:

```python
def init_page_elements(self):
    self.header = Group(By.CSS_SELECTOR, '[data-testid="main-header"]', wait=True)
    self.nav_home = Link(By.CSS_SELECTOR, '[data-testid="nav-home"]',
                         parent=self.header.locator)
    self.user_name = Text(By.CSS_SELECTOR, '.user-name',
                          parent=self.header.locator)
```

Use `parent` only when the same selector appears in multiple sections and disambiguation is needed.

## Comparing an Existing Page Object with HTML

### New elements (in HTML, not in Python)
- `var_name` from analysis does not appear in `init_page_elements()`
- Action: append them to `init_page_elements()`

### Stale elements (in Python, not in HTML)
- Variable in `init_page_elements()` has no corresponding element in the HTML analysis
- Action: add comment `# ⚠ NOT FOUND IN HTML — review and remove if obsolete`
- Do not delete automatically — element may target dynamic content absent from the static snapshot

### Changed locators
- Variable exists in both but best available locator differs from what is in the file
- Action: update the locator value, add comment `# ⚠ Locator updated from HTML`

### BDDKit type mismatch
- Variable exists but HTML indicates a different element type (e.g., file has `Button`, HTML has `Link`)
- Action: add comment `# ⚠ HTML indicates type <NewType> — verify`
