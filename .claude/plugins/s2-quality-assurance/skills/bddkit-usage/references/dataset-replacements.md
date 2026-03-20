# BDDKit Dataset & Parameter Replacements

Complete reference for the `map_param` and `replace_param` functions provided by `bddkit.utils.dataset`.

Source: `bddkit/utils/dataset.py`

These functions power dynamic parameter replacement in step definitions. All BDDKit steps that accept string parameters pass them through `map_param()` for dynamic resolution.

## map_param(param) - Context & Config Mapping

Replaces tags in strings with values from context, config files, or environment variables.

### Available Tags

| Tag Pattern | Source | Description |
|-------------|--------|-------------|
| `[CONF:key]` | `project_config` dict | Value from project configuration JSON (dot notation for nesting) |
| `[LANG:file::key]` | YAML in `resources/language/` | Language/i18n string from YAML. Format: `[LANG:filename::key.subkey]` returns value for configured language |
| `[BDDKIT:section_option]` | `bddkit_config` | BDDKit config value. Format: `Section_propertyName` (e.g., `Driver_type`) |
| `[CONTEXT:key]` | `context.storage` | Value from Behave context storage or context attribute. Supports dot notation for nested access |
| `[ENV:variable]` | Environment variables | OS environment variable value |
| `[FILE:path]` | File system | Content of a file at the given path |
| `[DATA_TEST:file::key]` | YAML in `resources/data_test/` | Test data from YAML. Format: `[DATA_TEST:filename::key.subkey]` |
| `[BASE64:path]` | File system | Base64-encoded content of a file |

### CONTEXT Tag - Advanced Access Patterns

The `[CONTEXT:key]` tag supports rich access patterns:

```gherkin
# Simple key from context.storage
[CONTEXT:auth_token]

# Nested dict access (dot notation)
[CONTEXT:user.profile.email]

# Array access by index
[CONTEXT:items.0.name]          # First item's name
[CONTEXT:items.-1.name]         # Last item's name

# Array access by key=value expression
[CONTEXT:users.role=admin]      # First user with role "admin"
[CONTEXT:items.'id'='123']      # Supports quoted keys/values
```

Resolution order for initial key:
1. `context.storage` (ChainMap of scenario, feature, and run storage)
2. `context.<attribute>` (direct context attribute)

### LANG Tag - Language File Access

```gherkin
# resources/language/login.yaml contains:
# success:
#   es: "Inicio de sesion correcto"
#   en: "Login successful"

Then the message is "[LANG:login::success]"
# Returns "Inicio de sesion correcto" if language=es
```

### DATA_TEST Tag - Test Data Access

```gherkin
# resources/data_test/users.yaml contains:
# valid_admin:
#   username: admin_user
#   password: SecurePass123

Given I set request json body to
    """
    {"username": "[DATA_TEST:users::valid_admin.username]"}
    """
```

### Recursive Replacement

`map_param` is applied recursively, so tags can contain other tags:

```gherkin
# [CONF:environments.[ENV:CURRENT_ENV].api_url]
# First resolves [ENV:CURRENT_ENV] -> "staging"
# Then resolves [CONF:environments.staging.api_url]
```

## replace_param(param) - Value Generation

Generates dynamic values from special replacement patterns.

### Data Generation

| Pattern | Output | Example |
|---------|--------|---------|
| `[STRING_WITH_LENGTH_XX]` | Fixed-length string (repeated 'a') | `[STRING_WITH_LENGTH_10]` -> `aaaaaaaaaa` |
| `[INTEGER_WITH_LENGTH_XX]` | Fixed-length integer (repeated '1') | `[INTEGER_WITH_LENGTH_5]` -> `11111` |
| `[STRING_ARRAY_WITH_LENGTH_XX]` | Array of 'a' strings | `[STRING_ARRAY_WITH_LENGTH_3]` -> `['a', 'a', 'a']` |
| `[INTEGER_ARRAY_WITH_LENGTH_XX]` | Array of 1 integers | `[INTEGER_ARRAY_WITH_LENGTH_2]` -> `[1, 1]` |
| `[JSON_WITH_LENGTH_XX]` | Dict with numbered keys | `[JSON_WITH_LENGTH_2]` -> `{'0': '0', '1': '1'}` |

### Special Values

| Pattern | Output | Type |
|---------|--------|------|
| `[MISSING_PARAM]` | `None` | `NoneType` |
| `[NULL]` | `None` | `NoneType` |
| `[TRUE]` | `True` | `bool` |
| `[FALSE]` | `False` | `bool` |
| `[EMPTY]` | `""` | `str` |
| `[B]` | `" "` (space) | `str` |
| `[UUID]` | Random UUID v4 | `str` |
| `[RANDOM]` | 8-char alphanumeric (starts with letter) | `str` |
| `[RANDOM_PHONE_NUMBER]` | Random phone number for configured country | `str` |
| `[SHARP]` | `#` | `str` |

### Date & Time

| Pattern | Output | Description |
|---------|--------|-------------|
| `[TIMESTAMP]` | Unix timestamp | Current UTC time as integer |
| `[DATETIME]` | `%Y-%m-%d %H:%M:%S.%f` | Current UTC datetime with microseconds |
| `[NOW]` | `%d/%m/%Y %H:%M:%S` (es) or `%Y/%m/%d %H:%M:%S` | Current UTC datetime |
| `[TODAY]` | `%d/%m/%Y` (es) or `%Y/%m/%d` | Current UTC date only |
| `[NOW + 2 DAYS]` | Offset datetime | Supports: DAYS, HOURS, MINUTES, SECONDS |
| `[NOW - 1 MINUTES]` | Offset datetime | Negative offsets for past dates |
| `[NOW(%Y-%m-%dT%H:%M:%SZ)]` | Custom formatted datetime | Any Python strftime format |
| `[NOW(%Y-%m-%dT%H:%M:%SZ) - 7 DAYS]` | Custom format with offset | Combined custom format and offset |
| `[TODAY + 30 DAYS]` | Offset date | Date-only with offset |

### Type Casting

| Pattern | Output | Description |
|---------|--------|-------------|
| `[STR:value]` | `str` | Cast to string |
| `[INT:value]` | `int` | Cast to integer |
| `[FLOAT:value]` | `float` | Cast to float |
| `[LIST:value]` | `list` | Parse as JSON/Python list |
| `[DICT:value]` | `dict` | Parse as JSON/Python dict |

### String Transformations

| Pattern | Output | Description |
|---------|--------|-------------|
| `[UPPER:text]` | Uppercase | `[UPPER:hello]` -> `HELLO` |
| `[LOWER:TEXT]` | Lowercase | `[LOWER:HELLO]` -> `hello` |
| `[TITLE:some text]` | Title case | `[TITLE:some text]` -> `Some Text` |
| `[REPLACE:source::find::replace]` | Replaced string | `[REPLACE:hello world::world::earth]` -> `hello earth` |
| `[ROUND:3.14159::2]` | Rounded float | `[ROUND:3.14159::2]` -> `3.14` |

## Context Storage

BDDKit manages three levels of storage:

| Storage | Scope | Access from steps |
|---------|-------|-------------------|
| `context.storage` | Current scenario | Default for `[CONTEXT:key]` |
| `context.feature_storage` | Current feature | Stored with `[FEATURE:key]` |
| `context.run_storage` | Entire test run | Stored with `[RUN:key]` |

Store values from steps:
```python
context.store_key_in_storage(context, 'my_key', value)           # scenario storage
context.store_key_in_storage(context, '[FEATURE:my_key]', value)  # feature storage
context.store_key_in_storage(context, '[RUN:my_key]', value)      # run storage
```

`context.storage` is a `ChainMap` that cascades: scenario -> feature -> run.

## Usage in Feature Files

```gherkin
Scenario: Dynamic parameter example
  # Generate data
  Given I set request url to "[CONF:api_url]"
  And I add request header "X-Request-Id" with value "[UUID]"
  And I add request header "X-Timestamp" with value "[TIMESTAMP]"
  And I set request json body to
    """
    {
      "name": "[STRING_WITH_LENGTH_20]",
      "email": "[RANDOM]@test.com",
      "created_at": "[NOW(%Y-%m-%dT%H:%M:%SZ)]",
      "expires_at": "[NOW(%Y-%m-%dT%H:%M:%SZ) + 30 DAYS]"
    }
    """
  When I send the request and store the response
  Then the response status should be 201
  # Use response data in next request
  And the response json at "id" should equal "[CONTEXT:expected_id]"
```