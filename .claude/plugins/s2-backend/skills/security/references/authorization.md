# Authorization (RBAC)

Authorization uses two authority types and custom SpEL expressions for `@PreAuthorize`.

## Authority types

| Type | Format | Granted by | Meaning |
|---|---|---|---|
| Role | `ROLE_<NAME>` | JWT enrichment | Coarse-grained role (e.g., admin) |
| Screen | `SCREEN_<RESOURCE>` | JWT enrichment | Access to a specific UI resource / feature |
| API key | `SCREEN_thirdparty` | `ApiKeyAuthenticationFilter` | Third-party integrator |

## Role naming convention

`ROLE_{MODULE}_{ACTION}` for module-scoped roles:

| Example | Meaning |
|---|---|
| `ROLE_USER_READ` | Read users |
| `ROLE_USER_WRITE` | Create/update/delete users |
| `ROLE_ADMIN` | Admin across all modules |
| `ROLE_SUPERADMIN` | Bypasses all RLS |

## @PreAuthorize on controllers

```java
// Simple role check
@PreAuthorize("hasAuthority('ROLE_USER_WRITE')")

// Screen check (fine-grained)
@PreAuthorize("hasAuthority('SCREEN_users')")

// Custom SpEL methods (via CustomMethodSecurityExpressionRoot)
@PreAuthorize("isAdmin()")
@PreAuthorize("hasScreens('users')")
@PreAuthorize("isAdminOrHasScreen('users')")
@PreAuthorize("isAdminOrHasScreenOrThirdParty('users')")
```

## Custom SpEL expressions

Extend `SecurityExpressionRoot` to add reusable authorization methods:

```java
class CustomMethodSecurityExpressionRoot extends SecurityExpressionRoot
    implements MethodSecurityExpressionOperations {

    public boolean isAdmin() {
        return hasAuthority("ROLE_ADMIN");
    }

    public boolean hasScreens(String... screens) {
        return Arrays.stream(screens)
            .anyMatch(s -> hasAuthority("SCREEN_" + s));
    }

    public boolean isAdminOrHasScreen(String... screens) {
        return isAdmin() || hasScreens(screens);
    }

    public boolean isAdminOrHasScreenOrThirdParty(String... screens) {
        return isAdminOrHasScreen(screens) || hasAuthority("SCREEN_thirdparty");
    }
}
```

Register with a custom `MethodSecurityExpressionHandler`:

```java
@Configuration
@EnableMethodSecurity
class MethodSecurityConfig {

    @Bean
    MethodSecurityExpressionHandler methodSecurityExpressionHandler() {
        var handler = new DefaultMethodSecurityExpressionHandler() {
            @Override
            protected MethodSecurityExpressionOperations createSecurityExpressionRoot(
                Authentication auth, MethodInvocation mi
            ) {
                var root = new CustomMethodSecurityExpressionRoot(auth);
                root.setPermissionEvaluator(getPermissionEvaluator());
                root.setTrustResolver(getTrustResolver());
                return root;
            }
        };
        return handler;
    }
}
```

## Rules

- `@PreAuthorize` goes on **controllers only** — Use Cases have no security annotations
- Prefer `isAdminOrHasScreen('resource')` over raw `hasAuthority(...)` for consistency
- Never check authority in the domain layer
- `ROLE_SUPERADMIN` bypasses both RLS (via `TenantResolver`) and all `@PreAuthorize` checks — no authorization restriction applies
