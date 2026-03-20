# Page Elements

## Rule: Centralize locators per page, prefer stable selectors

### Convention
- One file per page in `pageelements/` directory
- Naming: `<page>_elements.py` (e.g., `login_elements.py`)
- Locator variables use `snake_case`
- Prefer `data-testid` > CSS selectors > XPath
- Group related elements together

### Benefits
- **Single source of truth**: All locators for a page in one place
- **Easy maintenance**: When UI changes, update locators in one file
- **Stable tests**: Using `data-testid` avoids brittle selectors

### Examples

#### Page elements file
```python
# pageelements/login_elements.py


class LoginElements:
    """Locators for the login page."""

    # Form inputs
    username_input = ("css selector", "[data-testid='username-input']")
    password_input = ("css selector", "[data-testid='password-input']")

    # Buttons
    login_button = ("css selector", "[data-testid='login-submit']")
    forgot_password_link = ("css selector", "a.forgot-password")

    # Messages
    error_message = ("css selector", ".error-message")
    success_message = ("css selector", ".success-message")

    # TODO: Add locators as needed for the login page
```

#### Fallback to XPath (when CSS isn't sufficient)
```python
# Use XPath only for complex cases like text-based selection
submit_button_by_text = ("xpath", "//button[contains(text(), 'Submit')]")
```