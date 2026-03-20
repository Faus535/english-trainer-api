---
name: security
description: This skill should be used when the user asks about "security", "Spring Security", "@SecurityFilterChain", "JWT", "token enrichment", "@PreAuthorize", "RBAC", "roles", "screens", "tenant", "RLS", "row level security", "TenantContext", "@WithRlsBypass", "multi-tenancy", or "filter pipeline". Defines the full security architecture: chains, filters, JWT enrichment, RBAC and database-level multi-tenant filtering.
---

# Skill: Security

Defines the full security architecture for s2-backend: filter chain configuration, JWT enrichment, RBAC authorization,
and database-level multi-tenant filtering (RLS).

## When to use

- When configuring Spring Security filter chains
- When setting up JWT authentication and token enrichment
- When implementing multi-tenancy and RLS
- When defining roles, screens, and @PreAuthorize rules
- When adding filters or interceptors to the security pipeline

## Key rules

### Security chains

- Multiple `@SecurityFilterChain` beans differentiated by path and `@Order` (lower = higher priority)
- Typical chains: public (docs, actuator), integrator (API keys), authenticated (JWT)
- Each chain configures its own session policy, CSRF, and auth mechanism
- See [Security Chains](references/security-chains.md)

### Filter pipeline

- Fixed filter order: `ApiKeyAuthenticationFilter` → `BearerTokenAuthenticationFilter` → `PostAuthProvisioningFilter` →
  `TenantContextFilter`
- Each filter has a single responsibility; custom filters are positioned relative to Spring Security's built-in filters
- See [Filter Pipeline](references/filter-pipeline.md)

### JWT enrichment

- The raw JWT is converted to an `EnrichedJwtAuthenticationToken` via a `Converter<Jwt, AbstractAuthenticationToken>`
- The enriched token carries: `internalUserId`, `unitIds` (tenants), `superadmin` flag, `ROLE_*` and `SCREEN_*`
  authorities
- Never expose internal JWT claims (user ID, tenant IDs) in HTTP response DTOs
- See [JWT Enrichment](references/jwt-enrichment.md)

### Tenant context

- `TenantContextFilter` populates a thread-local `TenantContext` from the enriched token; throws `IllegalStateException`
  if context cannot be resolved
- Superadmins get `TenantContext.bypass()`; regular users get `TenantContext.forTenants(unitIds)`
- Use `TenantContextRunner` for programmatic bypass (startup code, filters before `TenantContextFilter`)
- For `@Async`: register `TenantContextTaskDecorator` on the executor
- See [Tenant Context](references/tenant-context.md)

### Row Level Security (RLS)

- `TenantAwareDataSource` sets PostgreSQL session variables (`app.rls_bypass`, `app.unit_ids`) on every connection
- Null context → secure defaults (bypass=false, empty tenantIds — no data visible)
- Non-HTTP code: use `@WithRlsBypass` (annotation) or `TenantContextRunner.runWithBypass()` (programmatic)
- See [RLS](references/rls.md)

### Elasticsearch multi-tenancy

- Index names are tenant-scoped: `{base}-{tenantId}` — always resolve via `ElasticsearchTenantHelper`
- Direct filtering: document contains tenant field → `wrapWithTenantFilter()`
- Indirect filtering: document references an entity that belongs to a tenant → `ElasticsearchTenantEntityResolver` +
  `wrapWithIndirectTenantFilter()`
- See [Elasticsearch Tenant Filtering](references/elasticsearch-tenant.md)

### RBAC & authorization

- Two authority types: `ROLE_*` (coarse-grained) and `SCREEN_*` (fine-grained, per resource)
- Extend `SecurityExpressionRoot` to add custom SpEL methods for `@PreAuthorize`
- `@PreAuthorize` goes on the controller; Use Cases have no security annotations
- See [Authorization](references/authorization.md)

## References

- [Security Chains](references/security-chains.md)
- [Filter Pipeline](references/filter-pipeline.md)
- [JWT Enrichment](references/jwt-enrichment.md)
- [Tenant Context](references/tenant-context.md)
- [RLS](references/rls.md)
- [Authorization](references/authorization.md)
- [Elasticsearch Tenant Filtering](references/elasticsearch-tenant.md)

## Cross-references

- **s2-backend:architecture** — `infrastructure/security/` package placement
- **s2-backend:api-design** — `@PreAuthorize` on controllers
- **s2-backend:logging** — Log security events without leaking sensitive data
