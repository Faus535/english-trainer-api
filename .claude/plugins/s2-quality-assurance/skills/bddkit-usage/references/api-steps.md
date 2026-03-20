# BDDKit API Steps Catalog

Complete catalog of built-in API step definitions provided by `bddkit.behave.steps.api`.

Import: `from bddkit.behave.steps.api import requests, responses`

Source: `bddkit/behave/steps/api/requests.py` and `bddkit/behave/steps/api/responses.py`

## Request Configuration Steps

All request steps build on an in-context `api_request` dictionary that is lazily initialized.

### Reset Request

```gherkin
Given I reset the api request
```

Reset the in-context API request configuration to start fresh.

### Set HTTP Method

```gherkin
Given I set request method to "{method}"
```

Set the HTTP method (GET, POST, PUT, DELETE, PATCH). Default is GET.

### Set URL

```gherkin
Given I set request url to "{url}"
```

Set the base URL for the request. Supports `map_param` replacements (e.g., `[CONF:api_url]`, `[ENV:BASE_URL]`).

### Set Endpoint

```gherkin
Given I set request endpoint to "{endpoint}"
```

Set the endpoint path, appended to the base URL as `url/endpoint`. Supports `map_param` replacements.

### Add Header

```gherkin
Given I add request header "{name}" with value "{value}"
```

Add a header to the request. Value supports `map_param` replacements.

### Add Query Parameter

```gherkin
Given I add request query param "{name}" with value "{value}"
```

Add a query parameter. Value supports `map_param` replacements.

### Set Timeout

```gherkin
Given I set request timeout to {seconds} seconds
```

Set the request timeout in seconds (integer).

### Set Basic Auth

```gherkin
Given I set basic auth username "{username}" and password "{password}"
```

Set HTTP Basic Authentication. Both values support `map_param` replacements.

### Set JSON Body (docstring)

```gherkin
Given I set request json body to
    """
    {"key": "value", "items": [1, 2, 3]}
    """
```

Set the request JSON body using a docstring. Must be valid JSON. Falls back to raw data if JSON parsing fails. Supports `map_param` in the docstring.

### Set Raw Body (docstring)

```gherkin
Given I set request body to
    """
    raw body content here
    """
```

Set the request body as raw text using a docstring.

### Set Body from JSON File

```gherkin
Given I set request body from json file "{file_path}"
```

Load request JSON body from a file. Path supports `map_param` replacements. Raises AssertionError if file not found or invalid JSON.

## Request Execution Steps

### Send Request and Store Response

```gherkin
When I send the request and store the response
```

Send the configured request using the `requests` library. Stores:
- `context.api_response` - the full `requests.Response` object
- `context.api_response_json` - parsed JSON body (or None if not JSON)

Raises AssertionError if URL is not set.

## Response Validation Steps

### Assert Status Code

```gherkin
Then the response status should be {status}
```

Assert the HTTP status code matches the expected integer value.

### Assert Response Body Equals

```gherkin
Then the response body should equal
    """
    expected body content
    """
```

Assert exact match of response body text. Supports `map_param` in docstring.

### Assert Response JSON Equals

```gherkin
Then the response json should equal
    """
    {"expected": "json"}
    """
```

Assert exact JSON equality. Docstring must be valid JSON.

### Assert JSON Path Equals

```gherkin
Then the response json at "{path}" should equal "{expected}"
```

Assert a value at a dot-separated JSON path. Supports array indexing by integer.
Examples:
- `data.name` - access nested key
- `items.0.id` - access array element by index
- `data.user.email` - deep nested access

### Assert JSON Path List Length

```gherkin
Then the response json at "{path}" is a list of length "{length}"
```

Assert the length of a JSON array at the given path.

### Assert Response Header

```gherkin
Then the response header "{name}" should be "{value}"
```

Assert a specific response header value. Supports `map_param` in value.

### Assert Response Body Contains

```gherkin
Then the response body should contain
    """
    expected substring
    """
```

Assert the response body contains the expected substring. Supports `map_param`.

### Assert Response Body Matches Regex

```gherkin
Then the response body should match regex "{pattern}"
```

Assert the response body matches the given regular expression pattern.

### Assert Response Time

```gherkin
Then the response time should be less than {seconds} seconds
```

Assert that the response time is under the specified threshold (float).

## Complete API Test Example

```gherkin
Feature: User API
  @api @no_driver

  Scenario: Create a new user
    Given I reset the api request
    And I set request url to "[CONF:api_url]"
    And I set request endpoint to "users"
    And I set request method to "POST"
    And I add request header "Content-Type" with value "application/json"
    And I add request header "Authorization" with value "Bearer [CONTEXT:auth_token]"
    And I set request json body to
      """
      {"name": "Test User", "email": "[RANDOM]@test.com"}
      """
    When I send the request and store the response
    Then the response status should be 201
    And the response json at "name" should equal "Test User"
    And the response header "Content-Type" should be "application/json"
    And the response time should be less than 5.0 seconds

  Scenario: Get users list
    Given I reset the api request
    And I set request url to "[CONF:api_url]"
    And I set request endpoint to "users"
    And I set request method to "GET"
    And I add request query param "page" with value "1"
    And I add request query param "size" with value "10"
    When I send the request and store the response
    Then the response status should be 200
    And the response json at "items" is a list of length "10"
```