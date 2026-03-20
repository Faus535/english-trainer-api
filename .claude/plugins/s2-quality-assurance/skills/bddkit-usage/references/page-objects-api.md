# BDDKit Page Objects

Complete reference for the Page Object Model provided by `bddkit.pageobjects`.

Source: `bddkit/pageobjects/`

## Class Hierarchy

```
CommonObject
└── PageObject
```

## CommonObject (Base)

Source: `bddkit/pageobjects/common_object.py`

Base class for all page objects and page elements.

### Properties

| Property | Type | Description |
|----------|------|-------------|
| `logger` | `logging.Logger` | Logger instance |
| `driver_wrapper` | `DriverWrapper` | BDDKit driver wrapper |
| `driver` | `WebDriver` | Selenium/Appium driver (shortcut) |
| `config` | `ConfigParser` | BDDKit configuration |
| `utils` | `Utils` | Driver utility methods |

## PageObject

Source: `bddkit/pageobjects/page_object.py`

Constructor: `PageObject(driver_wrapper=None, wait=False)`

| Parameter | Type | Description |
|-----------|------|-------------|
| `driver_wrapper` | `DriverWrapper` | Optional driver wrapper (defaults to pool default) |
| `wait` | `bool` | If True, loaded in parent's `wait_until_loaded()` |

### Methods

| Method | Returns | Description |
|--------|---------|-------------|
| `init_page_elements()` | `None` | Override to define page elements. Called in `__init__` |
| `wait_until_loaded(timeout)` | `self` | Wait until all elements with `wait=True` are visible/loaded |
| `reset_object(driver_wrapper)` | `None` | Reset all child page elements with new driver wrapper |

### How wait_until_loaded Works

Iterates all child elements with `wait=True`:
- `PageElement` subclasses: calls `wait_until_visible(timeout)`
- `PageObject` subclasses: calls `wait_until_loaded(timeout)` recursively

## POM Manager (Dynamic Loading)

Source: `bddkit/utils/pom_manager.py`

### load_pageobject(context, name, alias='page')

Dynamically import and instantiate a page object by name.

Search order:
1. `test.pageobjects.<name>` (project-specific test directory)
2. `pageobjects.<name>` (project root)
3. `bddkit.pageobjects.<name>` (library defaults)

The name is normalized: `LoginPageObject` -> `login`, `DashboardPage` -> `dashboardpage`.

The instance is stored as `context.<alias>` (default: `context.page`).

Usage from `environment.py` (already configured by BDDKit):
```python
# Registered in before_all:
context.use_pageobject = lambda name, alias='page': load_pageobject(context, name, alias)
```

Usage from steps:
```python
@given('the "{page}" page is loaded')
def step_page_loaded(context, page):
    context.use_pageobject(page)
    context.page.wait_until_loaded()
```

## Creating Page Objects

### Basic Page Object

```python
# pageobjects/login.py
from selenium.webdriver.common.by import By
from bddkit.pageobjects.page_object import PageObject
from bddkit.pageelements import InputText, Button, Text


class LoginPageObject(PageObject):
    def init_page_elements(self):
        self.username = InputText(By.ID, 'username', wait=True)
        self.password = InputText(By.ID, 'password')
        self.submit_button = Button(By.CSS_SELECTOR, 'button[type="submit"]')
        self.error_message = Text(By.CLASS_NAME, 'error-msg')

    def login(self, username, password):
        self.username.clear()
        self.username.text = username
        self.password.text = password
        self.submit_button.click()
```

### Page Object with Nested Components (Group)

```python
# pageobjects/dashboard.py
from selenium.webdriver.common.by import By
from bddkit.pageobjects.page_object import PageObject
from bddkit.pageelements import Group, Text, Button, Link


class DashboardPageObject(PageObject):
    def init_page_elements(self):
        # Header as a nested Group
        self.header = Group(By.CSS_SELECTOR, 'header.main-header', wait=True)
        self.nav_home = Link(By.CSS_SELECTOR, 'a[data-testid="nav-home"]',
                             parent=self.header.locator)
        self.user_name = Text(By.CSS_SELECTOR, '.user-display-name',
                              parent=self.header.locator)
        self.logout_button = Button(By.ID, 'logout-btn')
```

### Page Object with Shadow DOM

```python
# pageobjects/custom_component.py
from selenium.webdriver.common.by import By
from bddkit.pageobjects.page_object import PageObject
from bddkit.pageelements import InputText, Button


class CustomComponentPageObject(PageObject):
    def init_page_elements(self):
        # Elements inside shadow DOM
        self.search_input = InputText(
            By.CSS_SELECTOR, 'input.search',
            shadowroot='my-custom-element'
        )
        self.search_button = Button(
            By.CSS_SELECTOR, 'button.search-btn',
            shadowroot='my-custom-element'
        )
```

## Step Definitions with Page Objects

```python
# steps/web/login_web_steps.py
from behave import given, when, then


@given('I am on the login page')
def step_login_page(context):
    context.use_pageobject('Login')
    context.page.wait_until_loaded()


@when('I login with user "{user}" and password "{password}"')
def step_login(context, user, password):
    context.page.login(user, password)


@then('the error message "{message}" is displayed')
def step_error_message(context, message):
    assert context.page.error_message.is_visible(), "Error message not visible"
    assert context.page.error_message.text == message
```

## Conventions

1. One page object per page or major UI component
2. File naming: `pageobjects/<name>.py` with class `<Name>PageObject`
3. Define all elements in `init_page_elements()`, not in `__init__`
4. Mark key elements with `wait=True` for `wait_until_loaded()` to detect
5. Page objects encapsulate interactions; step definitions contain assertions
6. Use `parent` parameter for elements nested inside containers
7. Use `Group` for reusable UI components (headers, sidebars, modals)