# API Feature Files

## Endpoint Coverage Planning

To determine the required feature files for an endpoint, follow this process:

1. **Identify the operation**: Map each HTTP method to its operation type
2. **List required and optional fields**: From the API contract (OpenAPI spec, docs, or source code)
3. **Identify auth requirements**: Which roles and permissions the endpoint enforces
4. **Map to feature files**: Each operation and concern gets its own dedicated feature file

### Operation to Feature File Map

| HTTP Method    | Operation Type     | Feature Files to Create                                                                |
|----------------|--------------------|----------------------------------------------------------------------------------------|
| POST           | Creation           | `*_creation.feature`, `*_creation_errors.feature`                                     |
| GET (by ID)    | Read single item   | `*_read.feature`, `*_read_errors.feature`                                              |
| GET (list)     | List / search      | `*_list.feature`, `*_list_filters.feature`, `*_list_errors.feature`                   |
| PUT / PATCH    | Edition / Update   | `*_edition.feature`, `*_edition_errors.feature`                                        |
| DELETE         | Deletion           | `*_deletion.feature`, `*_deletion_errors.feature`                                      |
| Any            | Unsupported methods | `*_undefined_methods.feature`                                                         |

> For a complete CRUD resource, expect between 6 and 10 feature files.

---

## Required Scenarios per Feature File Type

### `*_creation.feature` / `*_edition.feature`

Cover happy path scenarios: valid inputs that produce successful responses.

| Scenario | Method | Action | Expected Code | Response Check |
|---|---|---|---|---|
| Operation with mandatory fields only | POST / PUT / PATCH | Body with only required fields | 201 / 200 | Response contains all mandatory fields |
| Operation with all fields | POST / PUT / PATCH | Body with required + optional fields | 201 / 200 | Response reflects all submitted fields |
| Read after creation/edition | GET | Retrieve resource by stored ID | 200 | Matches submitted data |

For optional field combinations or format variations, add a `Scenario Outline` with `Examples` covering each valid combination.

---

### `*_read.feature`

Cover single-item retrieval.

| Scenario | Method | Action | Expected Code | Response Check |
|---|---|---|---|---|
| Read existing item by ID | GET | Valid existing ID | 200 | Response contains expected fields and values |
| Read and verify all fields | GET | Valid existing ID | 200 | All expected fields present and correct |

---

### `*_list.feature` / `*_list_filters.feature`

Cover list retrieval and filtering capabilities.

| Scenario | Method | Action | Expected Code | Response Check |
|---|---|---|---|---|
| List all items (no filters) | GET | Request without query params | 200 | Response is a non-empty list |
| List with pagination | GET | `?page=1&size=10` | 200 | List length matches page size |
| Filter by valid field | GET | `?field=value` (valid value) | 200 | All items match filter |
| Filter returns empty results | GET | `?field=nonexistent_value` | 200 | Empty list |
| Sort ascending | GET | `?sort=field&order=asc` | 200 | Items in expected ascending order |
| Sort descending | GET | `?sort=field&order=desc` | 200 | Items in expected descending order |

---

### `*_deletion.feature`

| Scenario | Method | Action | Expected Code | Response Check |
|---|---|---|---|---|
| Delete existing item | DELETE | Valid ID of existing resource | 204 | No body or empty body |
| Delete non-existent item | DELETE | Non-existent ID | 404 | Error message in body |
| Delete already-deleted item | DELETE | ID of a previously deleted resource | 404 | Error message in body |

---

### `*_errors.feature` (per operation)

Cover all validation and error cases. Group by operation or create a single `*_errors.feature` per resource. Use `Scenario Outline` for field-level validations.

| Scenario | Method | Action | Expected Code | Expected Message |
|---|---|---|---|---|
| Missing required field `<field>` | POST / PUT | Omit each mandatory field (one per outline row) | 400 | `The '<field>' field is required.` |
| Invalid value for `<field>` | POST / PUT | Each field with an invalid value | 400 | `Invalid <field>: <value>` |
| Invalid field type in body | POST / PUT | Wrong type (e.g., string where integer expected) | 400 | TBD |
| Unsupported content type | POST / PUT | `Content-Type: text/plain` | 415 or 400 | TBD |
| Resource not found | GET / PUT / DELETE | Non-existent resource ID | 404 | Resource not found |
| Unauthorized — no token | Any | Request without `Authorization` header | 401 | TBD |
| Unauthorized — invalid token | Any | Expired or malformed token | 401 | TBD |
| Forbidden — insufficient role | Any | Valid token with a role that lacks permission | 403 | TBD |

> Cover **every required field** as a separate `Scenario Outline` row for missing-field validation.
> Cover **each invalid input class** (empty string, wrong type, out-of-range value, boundary values) as separate rows.

---

### `*_undefined_methods.feature`

Cover every HTTP method not explicitly supported by the endpoint. Derive the list by subtracting the supported methods from all standard methods.

| Scenario | Method | Action | Expected Code | Expected Message |
|---|---|---|---|---|
| Unsupported method on endpoint | Each unsupported method (one per outline row) | Request to the endpoint URL | 405 | `Method '<method>' is not supported.` |

**Deriving unsupported methods:**

| Endpoint supports | Methods to test as unsupported |
|---|---|
| POST | GET, PUT, DELETE, PATCH, OPTIONS |
| GET (by ID) | POST, PUT, DELETE, PATCH, OPTIONS |
| GET (list) | PUT, DELETE, PATCH, OPTIONS |
| PUT / PATCH | POST, DELETE, OPTIONS |
| DELETE | POST, GET, PUT, PATCH, OPTIONS |

---

## Minimum Coverage Checklist

For every endpoint, confirm that coverage exists for:

- [ ] Happy path with mandatory fields only → success code (201 / 200)
- [ ] Happy path with all fields including optional → success code
- [ ] Each required field missing individually → 400
- [ ] At least one invalid value per field type → 400
- [ ] Unauthorized request (missing or invalid token) → 401
- [ ] Forbidden request (wrong role, if role-based access applies) → 403
- [ ] Resource not found (if the endpoint operates on a resource by ID) → 404
- [ ] At least one unsupported HTTP method → 405

---

## File Structure and Naming Conventions

- One feature file per operation and concern
- Place all API feature files under `features/api/`
- Group by resource in subdirectories for large APIs
- Tag with `@api` and `@no_driver` at feature level (no browser driver needed)
- Use `@TS_API` for test suite identification
- Use `Actions Before the Feature` for feature-scoped setup (authentication, seed data)
- Use `Actions After the Feature` for teardown (delete test data)
- Use `Actions Before each Scenario` only for scenario-scoped state that must reset per scenario

### Directory layout example

```
features/api/
└── users/
    ├── user_creation.feature
    ├── user_creation_errors.feature
    ├── user_read.feature
    ├── user_read_errors.feature
    ├── user_list.feature
    ├── user_list_filters.feature
    ├── user_list_errors.feature
    ├── user_edition.feature
    ├── user_edition_errors.feature
    ├── user_deletion.feature
    ├── user_deletion_errors.feature
    └── user_undefined_methods.feature
```

---

## Examples

### `user_creation.feature`

```gherkin
@api @no_driver @users
Feature: User Creation API

  @TS_API
  Actions Before the Feature:
    Given the API is available
    And the user is authenticated as admin

  Actions After the Feature:
    Given I delete testing users

  @exe
  Scenario: Create user with mandatory fields
    When I create new user
      | param    | value                                 |
      | username | [DATA_TEST:users::test_user.username] |
      | email    | [DATA_TEST:users::test_user.email]    |
      | password | [DATA_TEST:users::test_user.password] |
    Then the response status code is 201
    And the response json contains the following values
      | username | value                                 | validation_type |
      | id       | -                                     | exists          |
      | username | [DATA_TEST:users::test_user.username] | equals          |
      | email    | [DATA_TEST:users::test_user.email]    | equals          |

  @exe
  Scenario: Create user with all fields
    When I create new user
      | param    | value                                 |
      | username | [DATA_TEST:users::test_user.username] |
      | email    | [DATA_TEST:users::test_user.email]    |
      | password | [DATA_TEST:users::test_user.password] |
      | role     | [DATA_TEST:users::test_user.role]     |
    Then the response status code is 201
    And the response json contains the following values
      | username | value                                 | validation_type |
      | id       | -                                     | exists          |
      | username | [DATA_TEST:users::test_user.username] | equals          |
      | email    | [DATA_TEST:users::test_user.email]    | equals          |
      | role     | [DATA_TEST:users::test_user.role]     | equals          |
```

---

### `user_creation_errors.feature`

```gherkin
@api @no_driver @users
Feature: User Creation API - Validation Errors

  @TS_API
  Actions Before the Feature:
    Given the API is available

  @exe
  Scenario Outline: Create user with missing required field
    When I create new user
      | param    | value      |
      | username | <username> |
      | email    | <email>    |
      | password | <password> |
    Then the response status code is 400
    And the response body contains "required"

    Examples:
      | username | email            | password  |
      |          | john@example.com | secret123 |
      | johndoe  |                  | secret123 |
      | johndoe  | john@example.com |           |

  @exe
  Scenario Outline: Create user with invalid field values
    When I create new user
      | param    | value      |
      | username | <username> |
      | email    | <email>    |
      | password | <password> |
    Then the response status code is 400

    Examples:
      | username | email         | password  |
      | 1234     | j@example.com | secret123 |
      | johndoe  | not-an-email  | secret123 |
      | johndoe  | j@example.com | short     |

  @exe
  Scenario: Create user without authentication
    Given I remove the authentication header
    When I create new user
      | param    | value                                 |
      | username | [DATA_TEST:users::test_user.username] |
      | email    | [DATA_TEST:users::test_user.email]    |
      | password | [DATA_TEST:users::test_user.password] |
    Then the response status code is 401

  @exe
  Scenario: Create user with insufficient permissions
    Given the user is authenticated as "viewer"
    When I create new user
      | param    | value                                 |
      | username | [DATA_TEST:users::test_user.username] |
      | email    | [DATA_TEST:users::test_user.email]    |
      | password | [DATA_TEST:users::test_user.password] |
    Then the response status code is 403
```

---

### `user_undefined_methods.feature`

```gherkin
@api @no_driver @users
Feature: User Creation Endpoint - Undefined Methods

  @TS_API
  Actions Before the Feature:
    Given the API is available
    And the user is authenticated as admin

  @exe
  Scenario Outline: Call user creation endpoint with unsupported method
    When I send a <method> request to "/api/v1/users"
    Then the response status code is 405

    Examples:
      | method  |
      | GET     |
      | PUT     |
      | DELETE  |
      | PATCH   |
      | OPTIONS |
```