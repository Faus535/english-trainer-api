---
name: security-audit
description: This skill should be used when the user asks to "audit security", "review security config", "check security chains", "validate @PreAuthorize", "audit tenant context", "verify RLS setup", "security review", or "check filter pipeline order". Audits that an existing Spring Security implementation follows the patterns defined in the security skill. For implementing security from scratch, use the security skill instead.
---

# Skill: Security Audit

Audits an existing Spring Security implementation against the conventions defined in the `security` skill. Validates
that security is correctly configured, not implements it from scratch.

**Distinction**: The `security` skill defines *how to implement* security. This skill defines *how to verify* that the
implementation is correct.

## When to use

- After implementing security with the `security` skill
- During `/s2-review --security`
- When onboarding to a project to validate its security posture
- Before a release to catch security misconfigurations

## Audit procedure

1. Search for the security configuration entry point: `Grep: @EnableWebSecurity glob:**/*.java`
2. Load [Spring Security Config](references/spring-security-config.md) and run through chains, filter pipeline, and JWT
   enrichment checklists
3. Load [Authorization & Tenant](references/authorization-and-tenant.md) and run through RBAC and tenant context
   checklists
4. Check if RLS is present: `Grep: TenantAwareDataSource glob:**/*.java`. If found, run the conditional RLS audit in the
   same reference
5. Load [General Hygiene](references/general-hygiene.md) and run through injection, sensitive data, and configuration
   checklists
6. Compile findings into ERROR / WARNING / PASS per category

## Audit categories

### 1. Security chains

Verify `@Order` correctness, catch-all chain presence, `securityMatcher` coverage, and CSRF policy.
See [Spring Security Config](references/spring-security-config.md#1-security-chains-audit).

### 2. Filter pipeline

Verify correct filter order (ApiKeyAuth -> BearerToken -> PostAuthProvisioning -> TenantContext), `chain.doFilter()`
always called, and pre-tenant filters use `TenantContextRunner`.
See [Spring Security Config](references/spring-security-config.md#2-filter-pipeline-audit).

### 3. JWT enrichment

Verify converter uses `TenantContext.bypass()`, clears in finally, returns minimal token on failure, and no internal
claims leak in responses.
See [Spring Security Config](references/spring-security-config.md#3-jwt-enrichment-audit).

### 4. RBAC & authorization

Verify `@PreAuthorize` on all non-public controllers, never on Use Cases/domain, `@EnableMethodSecurity` present, and
custom SpEL registered.
See [Authorization & Tenant](references/authorization-and-tenant.md#1-rbac-audit).

### 5. Tenant context

Verify filter positioned after PostAuthProvisioning, `requireContext()` in repos, and clear in finally.
See [Authorization & Tenant](references/authorization-and-tenant.md#2-tenant-context-audit).

### 6. General hygiene

Verify parameterized SQL/logging, no secrets in logs/responses, CORS not using wildcard in production, actuator
restricted, sessions stateless for API chains.
See [General Hygiene](references/general-hygiene.md).

## Conditional audits

- **RLS audit**: Only run if the project uses Row Level Security (`TenantAwareDataSource` present).
  See [Authorization & Tenant](references/authorization-and-tenant.md#3-rls-audit-conditional).

## References

- [Spring Security Config](references/spring-security-config.md)
- [Authorization & Tenant](references/authorization-and-tenant.md)
- [General Hygiene](references/general-hygiene.md)

## Cross-references

- **s2-backend:security** -- Implementation patterns this audit validates against
- **s2-backend:api-review** -- Complements with REST endpoint validation
- **s2-backend:error-handling** -- Validates ApiError does not leak sensitive data
- **s2-backend:logging** -- Validates log statements do not contain secrets or user input
