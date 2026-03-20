# General Hygiene Audit

Checklist for auditing injection prevention, sensitive data protection, and security configuration.

## 1. Injection prevention

### SQL injection
- [ ] All SQL queries use parameterized statements (`PreparedStatement`, `@Query` with `:param`)
- [ ] No string concatenation in SQL queries (`"SELECT * FROM " + table` is forbidden)
- [ ] Native queries in Spring Data use named parameters, not positional string interpolation
- [ ] **ERROR** if any query builds SQL by concatenating user input

### Log injection
- [ ] All log statements use parameterized logging: `log.info("User: {}", userId)`
- [ ] No string concatenation in log messages: `log.info("User: " + userId)` is forbidden
- [ ] User-controlled input is never logged without parameterization
- [ ] **WARNING** if log statements concatenate variables instead of using `{}`

### SpEL injection
- [ ] No user input is passed into SpEL expressions at runtime
- [ ] `@PreAuthorize` expressions use only static strings, never dynamic evaluation
- [ ] `@Value("#{...}")` does not reference user-controllable properties
- [ ] **ERROR** if user input could reach SpEL evaluation

## 2. Sensitive data protection

### No secrets in logs
- [ ] Passwords, tokens, API keys are never logged (even at DEBUG level)
- [ ] JWT token values are not logged — only log subject/username
- [ ] Search log statements for fields named `password`, `secret`, `token`, `apiKey`, `credential`
- [ ] **ERROR** if secret values are logged

### No secrets in responses
- [ ] HTTP response DTOs do not contain passwords, tokens, or internal IDs
- [ ] `ApiError` responses use generic messages — no stack traces, no internal details
- [ ] Exception messages returned to clients do not reveal system internals
- [ ] **ERROR** if stack traces are returned in HTTP responses

### No secrets in configuration
- [ ] `application.yml` / `application.properties` do not contain hardcoded secrets
- [ ] Secrets are injected via environment variables or a vault
- [ ] **WARNING** if configuration files contain values that look like secrets

### ApiError response format
- [ ] Error responses follow the `ApiError` pattern with code + generic message
- [ ] `ControllerAdvice` maps exceptions to appropriate HTTP status codes
- [ ] Internal exception messages are logged server-side, not forwarded to client
- [ ] Validation errors return field-level details but no internal state

## 3. Configuration audit

### CORS
- [ ] CORS `allowedOrigins` does not use `"*"` wildcard in production profiles
- [ ] CORS is configured per environment (dev may use `*`, production must not)
- [ ] `allowCredentials(true)` is not combined with `allowedOrigins("*")`
- [ ] **WARNING** if CORS uses wildcard origins without profile restriction

### Actuator
- [ ] Actuator endpoints are in a dedicated `permitAll()` chain with restricted paths
- [ ] Sensitive actuator endpoints (`/env`, `/configprops`, `/heapdump`) are disabled or secured
- [ ] Only health and info endpoints are exposed by default
- [ ] **WARNING** if sensitive actuator endpoints are exposed without authentication

### Session management
- [ ] API chains use `STATELESS` or `IF_REQUIRED` session creation policy
- [ ] No chain uses `SessionCreationPolicy.ALWAYS` for token-based auth
- [ ] SSE chains may use `IF_REQUIRED` for long-lived connections

### Dependency hygiene
- [ ] No known vulnerable dependencies (check with `./gradlew dependencyCheckAnalyze` if OWASP plugin is present)
- [ ] Spring Security version is current and supported
- [ ] **WARNING** if OWASP dependency check plugin is not configured

## Severity summary

| Check | Severity |
|-------|----------|
| SQL injection (string concatenation in queries) | ERROR |
| SpEL injection (user input in expressions) | ERROR |
| Secrets logged | ERROR |
| Stack traces in HTTP responses | ERROR |
| Log injection (concatenation in log statements) | WARNING |
| CORS wildcard in production | WARNING |
| Sensitive actuator endpoints exposed | WARNING |
| No OWASP dependency check plugin | WARNING |
| Hardcoded secrets in config files | WARNING |
