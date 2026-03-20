# BDDKit Mockito Steps Catalog

Complete catalog of built-in mock server step definitions provided by `bddkit.behave.steps.mockito`.

Source: `bddkit/behave/steps/mockito/mockito.py`

The mock server is an in-process threaded HTTP server that allows stubbing HTTP endpoints for component and integration testing without external dependencies.

## Server Lifecycle Steps

### Start Mock Server

```gherkin
Given I start mock server on "{base_url}"
```

Start a threaded HTTP mock server on the specified URL (e.g., `http://localhost:8080`). Stores server info, stubs list, and call recordings in `context.mock_server`.

### Stop Mock Server

```gherkin
Given I stop mock server
```

Shutdown and clean up the mock server. Clears `context.mock_server`.

### Clear Mock Server

```gherkin
Given I clear mock server
```

Clear all registered stubs and recorded calls without stopping the server.

## Stub Registration Steps

### Add Mock with Status

```gherkin
Given I add mock "{method}" for path "{path}" with status {status}
```

Register a stub for the given HTTP method and path with the specified status code. Optional JSON response body via docstring:

```gherkin
Given I add mock "GET" for path "/api/users" with status 200
    """
    [{"id": 1, "name": "Alice"}, {"id": 2, "name": "Bob"}]
    """
```

### Add Mock with Body and Headers

```gherkin
Given I add mock "{method}" for path "{path}" with:
    """
    {"response": "body"}
    """
    | status | 201 |
    | X-Custom-Header | custom-value |
```

Register a stub with response body (docstring), status code, and custom headers (table). If no status row is provided in the table, defaults to 200.

## Call Verification Steps

### Verify Call Count

```gherkin
Then I verify mock "{method}" for path "{path}" was called {times} time(s)
```

Assert that the mock server received exactly `{times}` requests matching the given method and path.

### Get Last Request

```gherkin
When I get the last mock request to "{path}"
```

Retrieve the last recorded request to the given path. Stores it as `context.last_mockito_request` with keys:
- `method` - HTTP method
- `path` - request path
- `headers` - dict of request headers
- `body` - parsed JSON body (or raw string)
- `raw_body` - raw bytes
- `time` - timestamp

### Assert Last Request Body

```gherkin
Then the last mock request body should equal
    """
    {"expected": "request body"}
    """
```

Assert the body of the last recorded mock request matches the expected value. Supports both JSON and plain text comparison.

## Complete Mock Test Example

```gherkin
Feature: Payment processing with mock
  @api @no_driver

  Background:
    Given I start mock server on "http://localhost:9090"

  Scenario: Process payment through external gateway
    # Stub the external payment gateway
    Given I add mock "POST" for path "/payments/charge" with status 200
      """
      {"transaction_id": "TXN-123", "status": "approved"}
      """
    # Call our API that internally calls the payment gateway
    And I reset the api request
    And I set request url to "[CONF:api_url]"
    And I set request endpoint to "orders/1/pay"
    And I set request method to "POST"
    And I set request json body to
      """
      {"amount": 99.99, "currency": "EUR"}
      """
    When I send the request and store the response
    Then the response status should be 200
    # Verify the mock was called
    And I verify mock "POST" for path "/payments/charge" was called 1 time(s)
    # Inspect what was sent to the mock
    When I get the last mock request to "/payments/charge"
    Then the last mock request body should equal
      """
      {"amount": 99.99, "currency": "EUR"}
      """

  Scenario: Handle gateway failure
    Given I add mock "POST" for path "/payments/charge" with status 503
    And I reset the api request
    And I set request url to "[CONF:api_url]"
    And I set request endpoint to "orders/1/pay"
    And I set request method to "POST"
    And I set request json body to
      """
      {"amount": 50.00, "currency": "EUR"}
      """
    When I send the request and store the response
    Then the response status should be 502
```

## Stub Matching Rules

- Stubs are matched by **method + path** (exact match).
- When multiple stubs match, the **last registered** stub wins.
- Unmatched requests return **404**.
- Path must start with `/` (auto-prepended if missing).