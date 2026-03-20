# Spring Security Config Audit

Checklist for auditing security chains, filter pipeline, and JWT enrichment.

## 1. Security chains audit

### Find configuration
```
Grep: @EnableWebSecurity glob:**/*.java
Grep: SecurityFilterChain glob:**/*.java
```

### Checks

#### @Order correctness
- [ ] Every `SecurityFilterChain` bean has an explicit `@Order` annotation
- [ ] Order values are sequential with no gaps or duplicates
- [ ] Lower-order chains match more specific paths (e.g., `/actuator/**` before `/**`)

#### Catch-all chain
- [ ] A chain with `securityMatcher("/**")` or no `securityMatcher` exists at the highest order
- [ ] **ERROR** if no catch-all: unmatched requests fall through to Spring defaults

#### securityMatcher coverage
- [ ] Every exposed path is covered by at least one chain's `securityMatcher`
- [ ] No overlapping matchers between chains (first match wins — verify intent)
- [ ] Public endpoints (docs, health) are in a dedicated `permitAll()` chain

#### CSRF policy
- [ ] CSRF disabled on all token-based API chains (stateless APIs do not use cookies)
- [ ] If any chain uses session-based auth, CSRF must be enabled on that chain

#### Session management
- [ ] API chains use `SessionCreationPolicy.STATELESS` or `IF_REQUIRED` (never `ALWAYS`)
- [ ] SSE chains may use `IF_REQUIRED` for long-lived connections

## 2. Filter pipeline audit

### Find filters
```
Grep: extends OncePerRequestFilter glob:**/*.java
Grep: addFilter(Before|After) glob:**/*.java
```

### Checks

#### Filter order
- [ ] `ApiKeyAuthenticationFilter` positioned **before** `BearerTokenAuthenticationFilter`
- [ ] `PostAuthProvisioningFilter` positioned **after** `BearerTokenAuthenticationFilter`
- [ ] `TenantContextFilter` positioned **after** `PostAuthProvisioningFilter`
- [ ] **ERROR** if `TenantContextFilter` is before `PostAuthProvisioningFilter`
- [ ] **ERROR** if filter order contradicts the expected pipeline

#### chain.doFilter() always called
- [ ] Every filter calls `chain.doFilter(request, response)` in its normal path
- [ ] Short-circuiting (writing error response and returning) is intentional and documented
- [ ] **ERROR** if a filter can silently drop the request without calling `chain.doFilter()` or writing a response

#### Pre-tenant filters use TenantContextRunner
- [ ] Filters running before `TenantContextFilter` that need DB access use `TenantContextRunner`
- [ ] No filter calls `TenantContextHolder.setContext()` directly (except `TenantContextFilter` itself)
- [ ] **ERROR** if a filter manipulates `TenantContextHolder` directly instead of using `TenantContextRunner`

#### Error handling in filters
- [ ] Filters log errors at appropriate levels (no swallowed exceptions)
- [ ] Filters never throw unchecked exceptions — log and continue, or write error response and return
- [ ] `PostAuthProvisioningFilter` logs errors but never fails the request

## 3. JWT enrichment audit

### Find converter
```
Grep: implements Converter<Jwt glob:**/*.java
Grep: jwtAuthenticationConverter glob:**/*.java
```

### Checks

#### TenantContext.bypass() usage
- [ ] Converter calls `TenantContextHolder.setContext(TenantContext.bypass())` before DB access
- [ ] **ERROR** if converter accesses DB without setting bypass context

#### Clear in finally
- [ ] `TenantContextHolder.clear()` is called in a `finally` block
- [ ] **ERROR** if bypass context could leak to subsequent filters on the same thread

#### Minimal token on failure
- [ ] On exception, converter returns a basic `JwtAuthenticationToken` (not enriched)
- [ ] Converter logs at ERROR level on failure
- [ ] Converter never throws — would cause 500 instead of 401

#### No internal claims in responses
- [ ] `internalUserId` is never included in HTTP response DTOs
- [ ] `unitIds` (tenant IDs) are never included in HTTP response DTOs
- [ ] Search response records/DTOs for fields that could leak enriched token data
- [ ] **WARNING** if any DTO field name matches internal JWT claim names

## Severity summary

| Check | Severity |
|-------|----------|
| No catch-all chain | ERROR |
| Wrong filter order | ERROR |
| Filter drops request silently | ERROR |
| Direct TenantContextHolder manipulation in filter | ERROR |
| Converter DB access without bypass | ERROR |
| Bypass context not cleared in finally | ERROR |
| Converter throws instead of returning minimal token | WARNING |
| Internal claims in response DTOs | WARNING |
| Missing @Order annotation | WARNING |
| CSRF enabled on stateless API chain | WARNING |
