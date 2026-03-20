# API Step Definitions

## Rule: One step file per API resource, delegate logic to helpers

### Convention
- Place in `steps/api/` directory
- Naming: `<resource>_api_steps.py`
- Store responses in `context` for assertion steps
- Keep step functions thin — delegate HTTP calls to helpers

### Examples

#### API step definitions
```python
# steps/api/users_api_steps.py
from behave import given, when, then


@given('a user exists with name "{name}"')
def step_user_exists(context, name):
    # TODO: Create user via API or load from test data
    # context.user_id = created_user["id"]
    pass


@when('I send a POST request to "{endpoint}" with body')
def step_send_post(context, endpoint):
    # TODO: Send POST request with context.text as body
    # context.response = api_client.post(endpoint, body=context.text)
    pass


@when('I send a GET request to "{endpoint}"')
def step_send_get(context, endpoint):
    # TODO: Send GET request, resolve path params from context
    # context.response = api_client.get(endpoint)
    pass


@then('the response status code is {status_code:d}')
def step_check_status(context, status_code):
    # TODO: Assert context.response.status_code == status_code
    pass


@then('the response contains field "{field}"')
def step_response_has_field(context, field):
    # TODO: Assert field exists in response JSON
    pass


@then('the response field "{field}" is "{value}"')
def step_response_field_value(context, field, value):
    # TODO: Assert response JSON field matches value
    pass
```