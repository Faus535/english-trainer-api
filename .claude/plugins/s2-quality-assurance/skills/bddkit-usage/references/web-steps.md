# BDDKit Web Steps Catalog

Complete catalog of built-in Web step definitions provided by `bddkit.behave.steps.web`.

Source: `bddkit/behave/steps/web/driver/actions.py` and `bddkit/behave/steps/web/pom/manager.py`

## Driver Action Steps

Import: `from bddkit.behave.steps.web.driver import actions`

### Open URL

```gherkin
Given I open url "{url}" in browser
```

Navigate the browser to the specified URL. Supports `map_param` replacements (e.g., `[CONF:base_url]`, `[ENV:APP_URL]`).

### Refresh Page

```gherkin
When I refresh the page
```

Refresh the current browser page.

### Wait Until URL

```gherkin
Then I wait until URL is "{url}"
```

Wait (up to 2 seconds) until the browser URL matches the expected value. Supports `map_param` replacements. Raises AssertionError if URL does not match after timeout.

### Switch to New Window

```gherkin
Then a new page with url "{url}" is opened
```

Switch to the last opened browser window/tab and assert its URL matches. Supports `map_param`. Raises `NoSuchElementException` if no new window exists.

## Page Object Manager Steps

Import: `from bddkit.behave.steps.web.pom import manager`

### Load Page Object

```gherkin
Given the "{pageobject_name}" page is loaded
```

Dynamically load and instantiate a page object by name. Calls `context.use_pageobject(name)` followed by `context.page.wait_until_loaded()`. The page object is resolved from these packages (in order):
1. `test.pageobjects.<name>`
2. `pageobjects.<name>`
3. `bddkit.pageobjects.<name>`

The page object instance is stored as `context.page`.

## Integration with Page Objects and Page Elements

### Creating a Page Object for Use with Steps

```python
# pageobjects/login.py
from selenium.webdriver.common.by import By
from bddkit.pageobjects.page_object import PageObject
from bddkit.pageelements import InputText, Button, Text


class LoginPageObject(PageObject):
    def init_page_elements(self):
        self.username_input = InputText(By.ID, 'username', wait=True)
        self.password_input = InputText(By.ID, 'password')
        self.login_button = Button(By.CSS_SELECTOR, 'button[type="submit"]')
        self.error_message = Text(By.CLASS_NAME, 'error-msg')

    def login(self, user, password):
        self.username_input.text = user
        self.password_input.text = password
        self.login_button.click()
```

### Using Page Objects in Feature Files

```gherkin
Feature: Login
  @web

  Background:
    Given I open url "[CONF:base_url]/login" in browser

  Scenario: Successful login
    Given the "Login" page is loaded
    # Custom steps interact with context.page
    When I enter username "admin" and password "secret"
    Then I wait until URL is "[CONF:base_url]/dashboard"
    And the "Dashboard" page is loaded

  Scenario: Failed login
    Given the "Login" page is loaded
    When I enter username "wrong" and password "wrong"
    Then the error message is displayed
```

## Tags for Driver Configuration

| Tag | Effect |
|-----|--------|
| `@web` | Feature-level tag indicating web tests |
| `@no_driver` | Skip browser driver initialization (for API-only scenarios) |
| `@reuse_driver` | Reuse the browser driver across scenarios in the feature |
| `@reset_driver` | Force driver restart before the scenario |