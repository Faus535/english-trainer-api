# Filter Pipeline

Custom filters are positioned relative to Spring Security's `BearerTokenAuthenticationFilter`. Each has a single responsibility.

## Pipeline (default chain)

```
Request
  │
  ▼
ApiKeyAuthenticationFilter          (BEFORE BearerTokenAuthenticationFilter)
  │  Reads X-API-KEY header
  │  Sets SecurityContext if valid; returns 429 if rate exceeded
  │  Sets authority: SCREEN_thirdparty
  │
  ▼
BearerTokenAuthenticationFilter     (Spring Security built-in)
  │  Validates Bearer JWT
  │  Calls UserServiceEnrichedJwtAuthConverter
  │    → fetches roles + screens from external service (cached)
  │    → runs DB lookup with TenantContextRunner.supplyWithBypass()
  │    → returns EnrichedJwtAuthenticationToken (userId, unitIds, superadmin)
  │
  ▼
PostAuthProvisioningFilter          (AFTER BearerTokenAuthenticationFilter)
  │  Reads JWT claims (sub, email, preferred_username, given_name, family_name)
  │  Upserts user in local DB via TenantContextRunner.runWithBypass()
  │  Logs on error, never fails the request
  │
  ▼
TenantContextFilter                 (AFTER PostAuthProvisioningFilter)
  │  Calls TenantResolver with current Authentication
  │  Superadmin → TenantContext.bypass()
  │  Regular user → TenantContext.forTenants(unitIds)
  │  Throws IllegalStateException if resolver returns Optional.empty()
  │  Sets TenantContextHolder; clears in finally
  │
  ▼
Controller / Use Case / Repository
  (TenantAwareDataSource reads TenantContextHolder on every getConnection())
```

## Filter implementation pattern

```java
@Component
class PostAuthProvisioningFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(PostAuthProvisioningFilter.class);

    private final CommandService commandService;
    private final TenantContextRunner tenantContextRunner;

    @Override
    protected void doFilterInternal(
        HttpServletRequest request,
        HttpServletResponse response,
        FilterChain chain
    ) throws ServletException, IOException {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth instanceof JwtAuthenticationToken jwt) {
            try {
                // TenantContextFilter hasn't run yet → use TenantContextRunner for bypass
                tenantContextRunner.runWithBypass(() ->
                    commandService.dispatch(buildUpsertCommand(jwt))
                );
            } catch (Exception ex) {
                log.error("Post-auth provisioning failed for user: {}", auth.getName());
                // Do NOT rethrow — provisioning failure must not block the request
            }
        }

        chain.doFilter(request, response);
    }
}
```

## Rules

- Filters must always call `chain.doFilter()` unless intentionally short-circuiting (401, 429)
- Filters needing DB access before `TenantContextFilter` must use `TenantContextRunner` — never manipulate `TenantContextHolder` directly in application filters
- `SecurityContextHolder` is populated from `BearerTokenAuthenticationFilter` onwards
- Never throw unchecked exceptions from a filter — log and continue, or write an error response and return
