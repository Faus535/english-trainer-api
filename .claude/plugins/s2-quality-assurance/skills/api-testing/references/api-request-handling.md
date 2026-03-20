# API Request Handling

## Rule: Centralize HTTP configuration, use context for state

### Convention
- Set up base URL and authentication in `before_all` or `before_scenario` hooks
- Store API client or session in `context`
- Load complex request bodies from `resources/api/` files
- Use step text (docstrings or tables) for simple inline data

### Examples

#### Setting up API context in hooks
```python
# environment.py
def before_scenario(context, scenario):
    # TODO: Initialize API client with base URL from config
    # context.api_base_url = config.get("api_url")
    # context.api_headers = {"Content-Type": "application/json"}
    pass
```

#### Loading request body from resources
```python
import json
import os


def load_api_resource(resource_name):
    """Load a JSON resource from resources/api/."""
    path = os.path.join("resources", "api", resource_name)
    with open(path) as f:
        return json.load(f)
    # TODO: Adapt path resolution for your project structure
```

#### Request resource file
```json
// resources/api/create_user_request.json
{
  "name": "{{name}}",
  "email": "{{email}}",
  "role": "user"
}
```