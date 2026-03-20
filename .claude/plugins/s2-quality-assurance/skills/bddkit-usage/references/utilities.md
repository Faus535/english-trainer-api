# Utilities

## Rule: Use BDDKit utilities for common testing operations

Source: `bddkit/utils/`

### Module Overview

| Module | Import | Purpose |
|--------|--------|---------|
| `dataset` | `bddkit.utils.dataset` | Parameter mapping, replacements, context storage |
| `data_generator` | `bddkit.utils.data_generator` | Random data generation (phone numbers, etc.) |
| `download_files` | `bddkit.utils.download_files` | File download helpers for test artifacts |
| `driver_utils` | `bddkit.utils.driver_utils` | Low-level Selenium driver utilities |
| `driver_wait_utils` | `bddkit.utils.driver_wait_utils` | Explicit wait utilities (prefer over sleeps) |
| `path_utils` | `bddkit.utils.path_utils` | Path resolution for test artifacts and repo layout |
| `pom_manager` | `bddkit.utils.pom_manager` | Dynamic page object loading |
| `youtrack/` | `bddkit.utils.youtrack` | YouTrack TMS integration client |

### dataset - Parameter Mapping

The most used utility module. See `references/dataset-replacements.md` for the complete reference.

Key functions:

```python
from bddkit.utils.dataset import map_param, replace_param

# Resolve context/config/env references
url = map_param('[CONF:api_url]')
token = map_param('[CONTEXT:auth_token]')
env_var = map_param('[ENV:MY_VAR]')
test_data = map_param('[DATA_TEST:users::admin.username]')

# Generate dynamic values
random_str = replace_param('[RANDOM]')
uuid = replace_param('[UUID]')
future_date = replace_param('[NOW + 30 DAYS]')
```

### driver_wait_utils - Waiting Utilities

Always prefer explicit waits over `time.sleep()`:

```python
from bddkit.utils.driver_wait_utils import (
    wait_until_element_visible,
    wait_until_element_not_visible,
    wait_until_element_clickable
)

# Usage through page elements (preferred)
element.wait_until_visible(timeout=10)
element.wait_until_clickable(timeout=5)
element.wait_until_not_visible(timeout=3)

# Or through context.utils
context.utils.wait_until_element_visible(element, timeout=10)
```

### pom_manager - Dynamic Page Object Loading

```python
from bddkit.utils.pom_manager import load_pageobject

# Registered on context in before_all:
context.use_pageobject = lambda name, alias='page': load_pageobject(context, name, alias)

# Usage from steps:
context.use_pageobject('Login')        # Loads as context.page
context.use_pageobject('Cart', 'cart') # Loads as context.cart

# Search order: test.pageobjects.<name> -> pageobjects.<name> -> bddkit.pageobjects.<name>
```

### download_files - File Downloads

```python
from bddkit.utils.download_files import download

# Download test artifacts
download(url, '/tmp/test-report.pdf')
```

### path_utils - Path Resolution

```python
from bddkit.utils.path_utils import get_project_root, get_resource_path

# Resolve paths relative to project root
root = get_project_root()
resource = get_resource_path('data_test/users.yaml')
```

### YouTrack Integration

```python
from bddkit.utils.youtrack.youtrack_client import YouTrackClient

# Initialized automatically in before_all when [YouTrack] enabled = true
client = YouTrackClient(url, token, project)

# Create a test run
run_id = client.create_runner_from_params(summary, scope, environment, layer, release)

# Report test case status
client.set_run_test_case_status(tc_id, run_id, 'PASSED')
client.set_test_case_last_status(tc_id, 'PASSED')

# Attach artifacts
client.attach_to_issue(run_id, 'allure-report.zip')
```

### Context Storage Helper

Store values at different scopes from step definitions:

```python
# Scenario-level (default, cleared each scenario)
context.store_key_in_storage(context, 'response_id', value)

# Feature-level (persists across scenarios in a feature)
context.store_key_in_storage(context, '[FEATURE:auth_token]', token)

# Run-level (persists across all features)
context.store_key_in_storage(context, '[RUN:global_session_id]', session_id)
```

Access stored values:
```python
# From steps
val = context.storage['response_id']

# From feature files via map_param
# [CONTEXT:response_id]
# [CONTEXT:auth_token]
```

### Test Data and Language Files

```yaml
# resources/data_test/users.yaml
admin:
  username: admin_user
  password: SecurePass123
  role: ADMIN

viewer:
  username: viewer_user
  password: ViewerPass456
  role: VIEWER
```

```yaml
# resources/language/messages.yaml
login_success:
  es: "Inicio de sesion correcto"
  en: "Login successful"
login_error:
  es: "Credenciales invalidas"
  en: "Invalid credentials"
```

Access from feature files:
```gherkin
# Test data
Given I set request json body to
    """
    {"username": "[DATA_TEST:users::admin.username]", "password": "[DATA_TEST:users::admin.password]"}
    """

# Language strings
Then the message is "[LANG:messages::login_success]"
```