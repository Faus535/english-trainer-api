# JWT Enrichment

The raw JWT from the identity provider (e.g., Keycloak) does not contain application-specific data. A `Converter<Jwt, AbstractAuthenticationToken>` enriches it with internal user data before the `SecurityContext` is populated.

## EnrichedJwtAuthenticationToken

Extends `JwtAuthenticationToken` with application data:

```java
public class EnrichedJwtAuthenticationToken extends JwtAuthenticationToken {
    private final String internalUserId;  // Internal DB user ID (not IdP subject)
    private final List<String> unitIds;   // Tenant unit IDs accessible to this user
    private final boolean superadmin;     // Bypasses all RLS filtering
}
```

## Converter

```java
@Component
class UserServiceEnrichedJwtAuthConverter implements Converter<Jwt, AbstractAuthenticationToken> {

    private static final Logger log = LoggerFactory.getLogger(UserServiceEnrichedJwtAuthConverter.class);

    private final RolePermissionService rolePermissionService;
    private final UserRepository userRepository;
    private final UserUnitPermissionCacheService unitPermissionCache;

    @Override
    public AbstractAuthenticationToken convert(Jwt jwt) {
        String principal = jwt.getClaimAsString("preferred_username");

        // 1. Fetch roles and screens from external permissions service (cached)
        List<GrantedAuthority> authorities = resolveAuthorities(principal, jwt.getTokenValue());

        // 2. Load internal user ID — bypass RLS, auth context not set yet
        TenantContextHolder.setContext(TenantContext.bypass());
        try {
            String internalUserId = userRepository.findByUsername(principal)
                .map(User::id)
                .orElse(null);

            boolean superadmin = authorities.stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_SUPERADMIN"));

            List<String> unitIds = superadmin
                ? List.of()
                : unitPermissionCache.getUnitIds(internalUserId);

            return new EnrichedJwtAuthenticationToken(jwt, authorities, internalUserId, unitIds, superadmin);
        } catch (Exception ex) {
            log.error("JWT enrichment failed for user {}: {}", principal, ex.getMessage());
            // Return minimal token — downstream filters/controllers may reject if incomplete
            return new JwtAuthenticationToken(jwt, authorities);
        } finally {
            TenantContextHolder.clear();
        }
    }

    private List<GrantedAuthority> resolveAuthorities(String userId, String jwtToken) {
        // Roles → "ROLE_<NAME>", Screens → "SCREEN_<RESOURCE>"
        return rolePermissionService.resolveRolesAndScreens(userId, jwtToken)
            .stream()
            .map(SimpleGrantedAuthority::new)
            .collect(toList());
    }
}
```

## Authority naming

| Type | Format | Example |
|---|---|---|
| Role | `ROLE_<NAME>` | `ROLE_ADMIN`, `ROLE_SUPERADMIN` |
| Screen (resource) | `SCREEN_<RESOURCE>` | `SCREEN_users`, `SCREEN_reports` |
| API key | `SCREEN_thirdparty` | (set by ApiKeyAuthenticationFilter) |

## Rules

- The converter **must** use `TenantContext.bypass()` during enrichment — `TenantContextFilter` has not run yet
- Never expose `internalUserId` or `unitIds` in HTTP responses
- On failure: log at ERROR and return a minimal token; do not throw (would return 500 instead of 401)
- Roles and screens are cached (e.g., 5 min TTL) — provide a cache-invalidation mechanism
