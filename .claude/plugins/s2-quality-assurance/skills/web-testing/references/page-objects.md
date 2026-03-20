# Page Objects

## Rule: One page object per page, encapsulate all interactions

### Convention
- One class per page or major UI component
- Place in `pageobjects/` directory
- Class naming: `PascalCase` (e.g., `LoginPage`)
- Methods represent user actions (e.g., `login`, `search`, `submit_form`)
- Page objects do NOT contain assertions
- Return new page objects for navigation (e.g., `login()` returns `DashboardPage`)

### Benefits
- **Maintainability**: UI changes only require updating one file
- **Reusability**: Multiple scenarios use the same page object
- **Readability**: Steps read like user actions, not DOM manipulation

### Examples

#### Page object class
```python
# pageobjects/login_page.py
from pageelements.login_elements import LoginElements


class LoginPage:
    def __init__(self, driver):
        self.driver = driver
        self.elements = LoginElements()

    def open(self):
        """Navigate to the login page."""
        # TODO: self.driver.get(base_url + "/login")
        pass

    def enter_username(self, username):
        """Type username into the username field."""
        # TODO: self.driver.find_element(*self.elements.username_input).send_keys(username)
        pass

    def enter_password(self, password):
        """Type password into the password field."""
        # TODO: self.driver.find_element(*self.elements.password_input).send_keys(password)
        pass

    def click_login_button(self):
        """Click the login submit button."""
        # TODO: self.driver.find_element(*self.elements.login_button).click()
        pass

    def login(self, username, password):
        """Perform full login flow."""
        self.enter_username(username)
        self.enter_password(password)
        self.click_login_button()
        # TODO: return DashboardPage(self.driver)

    def get_error_message(self):
        """Get the displayed error message text."""
        # TODO: return self.driver.find_element(*self.elements.error_message).text
        pass
```
