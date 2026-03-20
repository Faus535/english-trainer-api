# Web Step Definitions

## Rule: Steps delegate to page objects, no direct browser interaction

### Convention
- Place in `steps/web/` directory
- One step file per page or user flow
- Naming: `<page>_web_steps.py`
- Steps call page object methods — never interact with the browser directly
- Store page object instances in `context`

### Examples

#### Web step definitions
```python
# steps/web/login_web_steps.py
from behave import given, when, then
from pageobjects.login_page import LoginPage


@given('the login page is open')
def step_login_page_open(context):
    context.login_page = LoginPage(context.driver)
    context.login_page.open()


@given('a registered user with valid credentials')
def step_registered_user(context):
    # TODO: Load user credentials from test data
    # context.username = test_data["valid_user"]["username"]
    # context.password = test_data["valid_user"]["password"]
    pass


@when('the user enters their credentials')
def step_enter_credentials(context):
    context.login_page.enter_username(context.username)
    context.login_page.enter_password(context.password)


@when('clicks the login button')
def step_click_login(context):
    context.login_page.click_login_button()


@then('the dashboard page is displayed')
def step_dashboard_displayed(context):
    # TODO: Assert dashboard page loaded
    # assert context.driver.current_url.endswith("/dashboard")
    pass


@then('an error message "{message}" is displayed')
def step_error_message(context, message):
    actual = context.login_page.get_error_message()
    assert actual == message, f"Expected '{message}', got '{actual}'"
```