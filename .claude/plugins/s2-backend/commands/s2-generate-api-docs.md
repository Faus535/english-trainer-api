---
name: s2-generate-api-docs
description: Generates API documentation in Markdown for the frontend team from Java controllers
allowed-tools: [Read, Glob, Grep, Write, Bash]
model: sonnet
---

# API Documentation Generator for Frontend

Generates complete REST API documentation in Markdown format from the specified Java controllers.

## Arguments

Java controller names are passed as arguments: $ARGUMENTS

If no arguments are provided, ask the user which controllers they want to document.

## Instructions

1. **Find the controllers**: For each controller name provided in `$ARGUMENTS`, find the corresponding Java file in the project using Glob with the pattern `**/<ControllerName>.java`. Read each file found.

2. **API Review (BEFORE documenting)**: Apply the rules from the `s2-backend:api-review` skill on the found controllers:
   - Find and read the referenced Request/Response DTOs and enums
   - Validate: RESTful Naming, Parameter Consistency, HTTP Semantics, Complete Validations
   - Show the report to the user with OK / VIOLATIONS / WARNINGS per controller
   - If there are **VIOLATIONS**: show the report, inform the user of the problems found and do NOT generate the documentation. The user will decide whether to fix them first or continue anyway.
   - If there are only WARNINGS or everything is OK: continue with documentation generation.

3. **Extract endpoint information**: From each controller, extract:
   - HTTP method (`@GetMapping`, `@PostMapping`, `@PutMapping`, `@DeleteMapping`, `@PatchMapping`)
   - URL path (from mapping + `@RequestMapping` on the class if present)
   - Security permissions (`@PreAuthorize`)
   - HTTP response code (`@ResponseStatus` or `ResponseEntity.status(...)`)
   - Swagger/OpenAPI tags (`@Tag`, `@Operation`)

4. **Find related models**: For each controller:
   - Find Request DTOs (records used as `@RequestBody`)
   - Find Response DTOs (records returned in `ResponseEntity`)
   - Read the classes to extract fields, types, validations (`@NotNull`, `@NotBlank`, `@Size`, `@Min`, `@Max`, `@Pattern`, `@ValidEnum`, etc.)
   - If there are enums referenced (`@ValidEnum`), find and read the enum to list valid values
   - Read the related advices and extract the codes and messages of the errors that can be thrown in the controller's context.

5. **Generate the documentation**: Create a Markdown file in `.ai/api-docs/` with the following structure:

   ### Title
   `# [Module] API - S2 Backend`

   ### Table of Contents
   Links to each endpoint

   ### Authentication
   Describe that a Bearer token is required in the Authorization header

   ### Shared Models

   #### Page<T> (Paginated Response)
   - total: Long
   - page: Integer
   - size: Integer
   - maxResults: Integer
   - collection: T[]

   Also include other models reused across endpoints.

   ### Endpoints (repeat for each one)

   #### [Endpoint name]
   - **URL**: `METHOD /path`
   - **Description**: (from @Operation summary)
   - **Authorization**: (from @PreAuthorize)

   **Path Parameters**

   | Parameter | Type | Required | Description |

   **Query Parameters**

   | Parameter | Type | Default | Validation | Description |

   **Request Body**

   JSON example with types and restrictions, followed by table:

   | Field | Type | Required | Validation | Description |

   **Response (code)**

   JSON response example.

   **Error Codes**

   | Code | Description |

   **curl example**

   ```bash
   curl -X METHOD '{{BASE_URL}}/path' \
     -H 'Authorization: Bearer {{AUTH_TOKEN}}' \
     -H 'Content-Type: application/json' \
     -d '{ json body }'
   ```

   ### Enums
   For each referenced enum:
   | Value | Description |

5. **Write the file**: Use Write to save the file in `.ai/api-docs/api-<module>.md` where `<module>` is the module name inferred from the controllers' package.
   - If the file already exists, ask the user for confirmation before overwriting.

## Example output

The generated file `.ai/api-docs/api-users.md` looks like:

```markdown
# Users API - S2 Backend

## Table of Contents
- [Create User](#create-user)
- [Get User by ID](#get-user-by-id)

## Authentication
All endpoints require `Authorization: Bearer {{AUTH_TOKEN}}` header.

## Shared Models

### Page<T> (Paginated Response)
| Field | Type | Description |
|---|---|---|
| total | Long | Total number of records |
| page | Integer | Current page (0-indexed) |
| size | Integer | Page size |
| maxResults | Integer | Max allowed page size |
| collection | T[] | Items on this page |

## Endpoints

### Create User
- **URL**: `POST /users`
- **Description**: Creates a new user
- **Authorization**: `ROLE_USER_WRITE`

**Request Body**
| Field | Type | Required | Validation | Description |
|---|---|---|---|---|
| name | String | Yes | @NotBlank, @Size(max=100) | User full name |
| email | String | Yes | @NotBlank, @Email | User email |

**Response (201 Created)**
```json
{ "id": "uuid", "name": "John Doe", "email": "john@example.com" }
```

**Error Codes**
| Code | Description |
|---|---|
| USER_ALREADY_EXISTS | A user with that email already exists |
```

## Important notes
- Write the documentation in English
- Use `{{BASE_URL}}` and `{{AUTH_TOKEN}}` as placeholders in curl examples
- Include ALL validations for each field (min, max, size, pattern, notNull, etc.)
- If a controller implements an interface (e.g., `CreateController`, `GetController`), also find the interface to understand the signature
- Make sure to create the `.ai/api-docs/` directory if it does not exist (use Bash: mkdir -p)
