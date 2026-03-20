# Tag Strategy

## Rule: Use a layered tag strategy for filtering and organization

### Convention
Apply tags at the appropriate level (feature or scenario) to enable flexible test execution filtering. Tags serve three purposes: execution control, categorization, and prioritization.

### Benefits
- **Flexible execution**: Run specific subsets of tests via tag filters
- **Clear status**: Instantly see which tests are ready, in progress, or pending
- **Traceability**: Link tests to features and requirements
- **CI integration**: Configure pipelines to run specific tag combinations

### Examples

#### Feature with layered tags
```gherkin
@user-login @web @regression
Feature: User login

  @exe @critical
  Scenario: Successful login with valid credentials
    Given a registered user with valid credentials
    When the user submits the login form
    Then the dashboard is displayed

  @wip
  Scenario: Login with expired password
    Given a user with an expired password
    When the user submits the login form
    Then a password renewal prompt is displayed

  @todo
  Scenario: Login with two-factor authentication
    # TODO: Implement when 2FA is available in staging
```

#### Tag inheritance
The first scenario above effectively has tags: `@user-login @web @regression @exe @critical`
- `@user-login`, `@web`, `@regression` inherited from feature
- `@exe`, `@critical` applied at scenario level