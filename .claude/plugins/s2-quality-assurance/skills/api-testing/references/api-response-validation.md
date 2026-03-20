# API Response Validation

## Rule: Validate status code first, then structure, then values

### Convention
- Always assert HTTP status code as the first check
- Validate that required fields exist in the response
- Validate field values against expected data
- Store response in `context` for multi-step assertions

### Examples

#### Validation step patterns
```python
# steps/api/validation_steps.py
import json
from behave import then


@then('the response status code is {status_code:d}')
def step_status_code(context, status_code):
    # TODO: assert context.response.status_code == status_code
    pass


@then('the response body is a list')
def step_response_is_list(context):
    # TODO: body = context.response.json()
    # TODO: assert isinstance(body, list)
    pass


@then('the response body is a list with {count:d} items')
def step_response_list_count(context, count):
    # TODO: body = context.response.json()
    # TODO: assert len(body) == count
    pass


@then('the response contains field "{field}"')
def step_has_field(context, field):
    # TODO: body = context.response.json()
    # TODO: assert field in body
    pass


@then('the response field "{field}" is "{expected}"')
def step_field_equals(context, field, expected):
    # TODO: body = context.response.json()
    # TODO: assert str(body[field]) == expected
    pass


@then('the response field "{field}" is not empty')
def step_field_not_empty(context, field):
    # TODO: body = context.response.json()
    # TODO: assert body.get(field)
    pass
```