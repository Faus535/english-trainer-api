# BDDKit Page Elements

Complete reference for the page element hierarchy provided by `bddkit.pageelements`.

Source: `bddkit/pageelements/`

## Import

```python
from bddkit.pageelements import (
    PageElement, Text, InputText, Select, Button, Link,
    Checkbox, InputRadio, Group,
    PageElements, Texts, InputTexts, Selects, Buttons, Links,
    Checkboxes, InputRadios, Groups
)
```

## Class Hierarchy

```
CommonObject (base)
├── PageElement (single element)
│   ├── Text
│   ├── InputText
│   ├── Select
│   ├── Button
│   ├── Link
│   ├── Checkbox
│   ├── InputRadio
│   └── Group (container)
└── PageObject (page)
```

## PageElement (Base Class)

Constructor: `PageElement(by, value, parent=None, order=None, wait=False, shadowroot=None)`

| Parameter | Type | Description |
|-----------|------|-------------|
| `by` | `By` | Selenium locator type (`By.ID`, `By.CSS_SELECTOR`, `By.XPATH`, etc.) |
| `value` | `str` | Locator value |
| `parent` | `WebElement/PageElement/tuple` | Parent element for nested searches |
| `order` | `int` | Index when locator returns multiple elements |
| `wait` | `bool` | If True, element is checked in `wait_until_loaded()` |
| `shadowroot` | `str` | CSS selector of shadow root host element |

### Properties

| Property | Returns | Description |
|----------|---------|-------------|
| `web_element` | `WebElement` | Find and return the Selenium WebElement |
| `driver` | `WebDriver` | Browser driver instance |
| `config` | `ConfigParser` | BDDKit configuration |
| `utils` | `Utils` | Driver utility methods |

### Methods

| Method | Returns | Description |
|--------|---------|-------------|
| `is_present()` | `bool` | Check if element exists in DOM |
| `is_visible()` | `bool` | Check if element is present and displayed |
| `wait_until_visible(timeout)` | `self` | Wait until element is visible |
| `wait_until_not_visible(timeout)` | `self` | Wait until element disappears |
| `wait_until_clickable(timeout)` | `self` | Wait until element is clickable |
| `scroll_element_into_view()` | `self` | Scroll element into viewport |
| `get_attribute(name)` | `str` | Get HTML attribute value |
| `set_focus()` | `self` | Focus the element |
| `reset_object(driver_wrapper)` | `None` | Reset element state |

## Text

Extends `PageElement` for read-only text elements.

```python
from bddkit.pageelements import Text

label = Text(By.CSS_SELECTOR, '.label-class')
# Access text
print(label.text)  # Returns element's visible text
```

## InputText

Extends `PageElement` for text input fields.

```python
from bddkit.pageelements import InputText

username = InputText(By.ID, 'username', wait=True)
```

### Properties & Methods

| Member | Type | Description |
|--------|------|-------------|
| `text` (getter) | `str` | Get input value via `get_attribute("value")` |
| `text` (setter) | - | Type text via `send_keys(value)`. Handles shadowroot |
| `clear()` | `self` | Clear the input field |
| `click()` | `self` | Wait until clickable, then click (handles stale elements) |
| `set_focus()` | `self` | Focus and click the input field |

## Button

Extends `PageElement` for clickable buttons.

```python
from bddkit.pageelements import Button

submit = Button(By.CSS_SELECTOR, 'button[type="submit"]')
```

### Properties & Methods

| Member | Type | Description |
|--------|------|-------------|
| `text` (getter) | `str` | Get button visible text (handles stale elements) |
| `click()` | `self` | Wait until clickable, then click (handles stale elements) |

## Select

Extends `PageElement` for dropdown/select elements.

```python
from bddkit.pageelements import Select

country = Select(By.ID, 'country-select')
```

### Properties

| Member | Type | Description |
|--------|------|-------------|
| `option` (getter) | `str` | Get text of first selected option |
| `option` (setter) | - | Select option by visible text |
| `selenium_select` | `SeleniumSelect` | Access full Selenium Select API (`select_by_index`, `deselect_all`, etc.) |

## Link

Extends `PageElement` for anchor elements.

```python
from bddkit.pageelements import Link

nav_link = Link(By.CSS_SELECTOR, 'a.nav-link')
```

## Checkbox

Extends `PageElement` for checkbox inputs.

```python
from bddkit.pageelements import Checkbox

accept_terms = Checkbox(By.ID, 'accept-terms')
```

## InputRadio

Extends `PageElement` for radio button inputs.

```python
from bddkit.pageelements import InputRadio

option_a = InputRadio(By.CSS_SELECTOR, 'input[value="a"]')
```

## Group

Extends both `PageElement` and `PageObject`. Represents a component containing other page elements.

```python
from bddkit.pageelements import Group

header = Group(By.CSS_SELECTOR, '.header-component', wait=True)
```

## Collection Types (PageElements)

For locators that match multiple elements:

| Class | Element Type | Description |
|-------|-------------|-------------|
| `PageElements` | `PageElement` | Generic collection |
| `Texts` | `Text` | Collection of text elements |
| `InputTexts` | `InputText` | Collection of input fields |
| `Selects` | `Select` | Collection of dropdowns |
| `Buttons` | `Button` | Collection of buttons |
| `Links` | `Link` | Collection of links |
| `Checkboxes` | `Checkbox` | Collection of checkboxes |
| `InputRadios` | `InputRadio` | Collection of radio buttons |
| `Groups` | `Group` | Collection of groups |

## Usage in Page Objects

```python
from selenium.webdriver.common.by import By
from bddkit.pageobjects.page_object import PageObject
from bddkit.pageelements import InputText, Button, Text, Select


class RegistrationPageObject(PageObject):
    def init_page_elements(self):
        # wait=True -> checked in wait_until_loaded()
        self.email = InputText(By.ID, 'email', wait=True)
        self.password = InputText(By.ID, 'password')
        self.country = Select(By.ID, 'country')
        self.submit = Button(By.CSS_SELECTOR, 'button[type="submit"]')
        self.error = Text(By.CLASS_NAME, 'error-msg')

    def register(self, email, password, country):
        self.email.clear().text = email
        self.password.text = password
        self.country.option = country
        self.submit.click()
```