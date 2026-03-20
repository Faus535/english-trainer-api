---
name: api-review
description: This skill should be used when the user asks to "review my API", "check controllers", "validate endpoints", "API audit", or "REST naming rules". For reviewing and auditing existing REST controllers against design standards. For creating new APIs, use api-design instead. Also used by s2-generate-api-docs as a pre-validation step.
---

# Skill: API Review - REST Controller Validation

Reviews Java REST controllers and reports violations against standard API design rules.

**Note:** For creating new REST APIs, use the **api-design** skill instead. This skill is for reviewing and auditing existing code.

## Rules

### 1. RESTful Naming
- URLs in **plural** for collections: `/units` not `/unit`
- URLs in **kebab-case**: `/user-units` not `/userUnits` or `/user_units`
- **No verbs** in paths: `/units` not `/getUnits`, `/createUnit`, `/fetchUnits`
- Nested resources express relationships: `/users/{userId}/units` not `/user-units?userId=x`
- Maximum 3 nesting levels: `/a/{id}/b/{id}/c` is the limit
- IDs in path, not in query params, to identify a specific resource

### 2. Parameter Consistency
- Query params in **camelCase**: `sortBy`, `orderBy`, not `sort_by`
- Path params in **camelCase**: `{userId}`, not `{user_id}`
- Request/response fields in **camelCase**: `dashChartLastHours`, not `dash_chart_last_hours`
- **Consistent** names across endpoints: if one endpoint uses `unitId`, all must use `unitId` (not `unit_id` or `idUnit`)
- Same entity must have the **same response structure** across all endpoints

### 3. HTTP Semantics
- `GET`: read-only, no body, idempotent
- `POST`: create resource, responds `201 Created`
- `PUT`: replace full resource, responds `200 OK`, idempotent
- `PATCH`: partial update, responds `200 OK`
- `DELETE`: remove resource, responds `204 No Content`, no response body
- Do not use `GET` for operations with side effects
- Do not use `POST` when `PUT` or `PATCH` is more appropriate
- List endpoints must return `200` with empty array, never `404`

### 4. Complete Validations
- Every **required** text field must have `@NotBlank`
- Every **required** non text field must have `@NotNull`
- Strings must have `@Size(max=N)` defined
- Numbers with known range must have `@Min`/`@Max`
- UUIDs must **never** use `@UUID` (Hibernate Validator) because it rejects UUID v7. Instead, use `java.util.UUID` as the field type — Jackson will reject invalid UUIDs automatically
- Enums must be validated with `@ValidEnum` or equivalent
- Required lists must have `@NotEmpty`
- Nested objects in lists must have `@Valid`
- No fields without any validation in Request DTOs

## Report format

For each reviewed controller, generate:

```
## [ControllerName]

### OK
- (list what is correct)

### VIOLATIONS
- [RULE-CATEGORY] Problem description → Fix suggestion

### WARNINGS
- (things that are not violations but could be improved)
```

### 5. Controller Structure
- Each controller must have **exactly one** HTTP handler method (no god controllers)
- A `.java` file with more than one HTTP mapping is a violation
- Report format: `[CONTROLLER-STRUCTURE] GodController: X endpoints in the same class → Split into one controller per action`

## Usage instructions

1. Receive controller names as input
2. Find each controller with `Glob: **/<Name>.java`
3. Read the controller and extract: URL mappings, HTTP methods, parameters, annotations
4. Find and read the referenced Request/Response DTOs
5. Find and read enums referenced in validations
6. Apply the 4 rule categories
7. Generate report with OK / VIOLATIONS / WARNINGS per controller
