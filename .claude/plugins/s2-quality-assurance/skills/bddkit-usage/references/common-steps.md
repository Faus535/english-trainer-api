# Common Steps

## Rule: Reuse BDDKit built-in steps before writing custom ones

BDDKit provides complete step catalogs for API, Web, Mockito, and Performance testing. Always check the built-in steps first.

### Built-in Step Modules

| Module | Import | Steps |
|--------|--------|-------|
| API Requests | `bddkit.behave.steps.api.requests` | HTTP request configuration and execution |
| API Responses | `bddkit.behave.steps.api.responses` | Response validation (status, body, headers, JSON paths) |
| Web Actions | `bddkit.behave.steps.web.driver.actions` | Browser navigation (`open url`, `refresh`, `wait until URL`) |
| Web POM | `bddkit.behave.steps.web.pom.manager` | Page object loading (`the "X" page is loaded`) |
| Mockito | `bddkit.behave.steps.mockito.mockito` | Mock server lifecycle, stub registration, verification |
| Performance | `bddkit.behave.steps.performance.*` | Load testing steps (Locust-based) |

### Step Import Convention

BDDKit steps are auto-imported when the modules are imported in `steps/__init__.py`:

```python
# steps/__init__.py - Import BDDKit built-in steps
from bddkit.behave.steps.api import requests, responses
from bddkit.behave.steps.web.driver import actions
from bddkit.behave.steps.web.pom import manager
from bddkit.behave.steps.mockito import mockito
```

### Project-Specific Common Steps

Extract reusable project-specific steps to shared locations:

| Location | Scope |
|----------|-------|
| `steps/__init__.py` | Project-wide common steps |
| `steps/api/__init__.py` | API-specific common steps |
| `steps/web/__init__.py` | Web-specific common steps |

### Example: Project API Common Steps

```python
# steps/api/__init__.py
from behave import given, when, then
from bddkit.utils.dataset import map_param


@given('I am authenticated as "{role}"')
def step_authenticated(context, role):
    """Set up authentication headers based on role."""
    token = context.storage.get(f'{role}_token')
    if not token:
        # Perform login and store token
        from bddkit.behave.steps.api.requests import (
            api_reset_request, api_set_url, api_set_method,
            api_set_json_body, api_send_request_store_response
        )
        context.api_request = None
        cfg = {'method': 'POST', 'url': map_param('[CONF:api_url]'),
               'endpoint': 'auth/login', 'headers': {},
               'params': {}, 'json': {'role': role}, 'data': None,
               'timeout': None, 'auth': None}
        context.api_request = cfg
        api_send_request_store_response(context)
        token = context.api_response_json.get('token')
        context.store_key_in_storage(context, f'[RUN:{role}_token]', token)


@then('the response should be a valid list')
def step_valid_list(context):
    """Assert response is a non-empty JSON array."""
    assert context.api_response_json is not None, "No JSON response"
    assert isinstance(context.api_response_json, list), "Response is not a list"
    assert len(context.api_response_json) > 0, "Response list is empty"
```

### Example: Project Web Common Steps

```python
# steps/web/__init__.py
from behave import given, when, then


@given('I am logged in as "{username}"')
def step_logged_in(context, username):
    """Navigate to login page, authenticate, and verify dashboard."""
    context.use_pageobject('Login')
    context.page.wait_until_loaded()
    password = context.storage.get(f'{username}_password', 'default_pass')
    context.page.login(username, password)


@then('a toast message "{message}" is displayed')
def step_toast_message(context, message):
    """Assert a toast notification with the expected message appears."""
    context.use_pageobject('Toast')
    context.page.wait_until_loaded()
    assert context.page.message.text == message
```

### Rules for Custom Steps

1. Check BDDKit built-in steps first (see `references/api-steps.md`, `references/web-steps.md`, `references/mockito-steps.md`)
2. Use `map_param()` on all string parameters to support dynamic replacements
3. Store reusable data with `context.store_key_in_storage()` for cross-scenario access
4. Keep step granularity: one logical action per step
5. Place assertions only in `Then` steps, not in page objects