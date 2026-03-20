# Authorization & Tenant Audit

Checklist for auditing RBAC configuration, tenant context management, and RLS setup.

## 1. RBAC audit

### Find configuration
```
Grep: @EnableMethodSecurity glob:**/*.java
Grep: @PreAuthorize glob:**/*.java
Grep: extends SecurityExpressionRoot glob:**/*.java
Grep: MethodSecurityExpressionHandler glob:**/*.java
```

### Checks

#### @PreAuthorize on controllers
- [ ] Every non-public controller has `@PreAuthorize` on its handler method
- [ ] **ERROR** if a controller in an authenticated chain has no `@PreAuthorize`
- [ ] Public chains (actuator, docs) do not need `@PreAuthorize`

#### Never on Use Cases or domain
- [ ] No `@PreAuthorize` on Use Case classes or methods
- [ ] No security annotations (`@Secured`, `@RolesAllowed`, `@PreAuthorize`) in domain layer
- [ ] **ERROR** if security annotations exist outside of infrastructure layer

#### @EnableMethodSecurity present
- [ ] `@EnableMethodSecurity` is declared on a `@Configuration` class
- [ ] **ERROR** if missing — `@PreAuthorize` annotations are silently ignored

#### Custom SpEL registered
- [ ] `CustomMethodSecurityExpressionRoot` (or equivalent) is registered via a `MethodSecurityExpressionHandler` bean
- [ ] Custom methods (`isAdmin()`, `hasScreens()`, etc.) are used consistently across controllers
- [ ] **WARNING** if controllers use raw `hasAuthority(...)` when custom SpEL methods exist

#### Authority naming
- [ ] Roles follow `ROLE_<NAME>` format
- [ ] Screens follow `SCREEN_<RESOURCE>` format
- [ ] No mixed formats (e.g., `ROLE_admin` lowercase, `screen_users` missing prefix)

## 2. Tenant context audit

### Find configuration
```
Grep: TenantContextFilter glob:**/*.java
Grep: TenantContextHolder glob:**/*.java
Grep: TenantContextRunner glob:**/*.java
```

### Checks

#### Filter positioning
- [ ] `TenantContextFilter` is positioned **after** `PostAuthProvisioningFilter` in every chain that uses both
- [ ] `TenantContextFilter` is present on every authenticated chain
- [ ] **ERROR** if `TenantContextFilter` is before `PostAuthProvisioningFilter`

#### requireContext() in repositories
- [ ] Repository implementations use `TenantContextHolder.requireContext()` (not `getContext()`)
- [ ] **WARNING** if repositories use `getContext()` — null context means silent data leaks

#### Clear in finally
- [ ] `TenantContextFilter` clears context in a `finally` block
- [ ] Any code that manually sets context (converter, startup code) clears in `finally`
- [ ] **ERROR** if context could leak between requests on the same servlet thread

#### Async propagation
- [ ] If `@Async` is used: `TenantContextTaskDecorator` is registered on the executor
- [ ] **WARNING** if `@Async` methods access tenant context without propagation configured

## 3. RLS audit (conditional)

> **Only run this section if the project uses RLS.** Check for the presence of `TenantAwareDataSource`. Skip entirely if not found.

### Find configuration
```
Grep: TenantAwareDataSource glob:**/*.java
Grep: @WithRlsBypass glob:**/*.java
Grep: ENABLE ROW LEVEL SECURITY glob:**/*.sql
```

### Checks

#### TenantAwareDataSource wraps real datasource
- [ ] `TenantAwareDataSource` extends `DelegatingDataSource` and overrides `getConnection()`
- [ ] Session variables (`app.rls_bypass`, `app.unit_ids`) are set on every connection
- [ ] Null context defaults to secure values: bypass=false, empty tenantIds
- [ ] **ERROR** if null context defaults to bypass=true (all data visible without auth)

#### @WithRlsBypass only in non-HTTP code
- [ ] `@WithRlsBypass` is only used on `@Scheduled`, `@EventListener`, `@RabbitListener`, `@KafkaListener`, or `@PostConstruct` methods
- [ ] **WARNING** if `@WithRlsBypass` appears on a controller or Use Case method
- [ ] **ERROR** if `@WithRlsBypass` is on a method reachable from an HTTP request

#### TenantContextRunner for programmatic bypass
- [ ] Code that needs bypass outside annotations uses `TenantContextRunner`
- [ ] No direct `TenantContextHolder.setContext(TenantContext.bypass())` in application code
- [ ] Filters and converters are the only valid exceptions (they must clear in finally)

#### RLS policies on tenant-scoped tables
- [ ] Flyway migrations enable RLS on tables that contain tenant-scoped data
- [ ] RLS policies reference `current_tenant_ids()` function
- [ ] `FORCE ROW LEVEL SECURITY` is enabled (applies policies even to table owner)
- [ ] **WARNING** if a tenant-scoped table has no RLS policy

## Severity summary

| Check | Severity |
|-------|----------|
| Controller without @PreAuthorize (authenticated chain) | ERROR |
| Security annotations in domain/application layer | ERROR |
| @EnableMethodSecurity missing | ERROR |
| TenantContextFilter before PostAuthProvisioningFilter | ERROR |
| TenantContext not cleared in finally | ERROR |
| @WithRlsBypass in HTTP-reachable code | ERROR |
| Null context defaults to bypass=true | ERROR |
| Raw hasAuthority when custom SpEL exists | WARNING |
| Repository uses getContext() instead of requireContext() | WARNING |
| @Async without TenantContextTaskDecorator | WARNING |
| @WithRlsBypass on controller or Use Case | WARNING |
| Tenant-scoped table without RLS policy | WARNING |
