# Tenant Context

`TenantContext` carries the set of tenant IDs for the current request. It is populated by `TenantContextFilter` after authentication and accessed via a thread-local holder.

## TenantContext

```java
public record TenantContext(
    List<String> tenantIds,
    boolean rlsBypass
) {
    public static TenantContext bypass() {
        return new TenantContext(List.of(), true);
    }

    public static TenantContext forTenants(List<String> tenantIds) {
        return new TenantContext(tenantIds, false);
    }
}
```

## TenantContextHolder

Thread-local holder. Use `requireContext()` in infrastructure code that must always have a context set.

```java
TenantContextHolder.setContext(ctx);    // set
TenantContextHolder.getContext();       // returns null if not set
TenantContextHolder.requireContext();   // throws if null (use in repositories)
TenantContextHolder.clear();            // always call in finally
```

Configure for inheritable propagation (child threads inherit parent context):
```yaml
s2grupo.tenant.contextPropagation.type: INHERITABLE_THREAD_LOCAL
```

## TenantContextFilter

Resolves context from the authenticated request and sets it in the holder. **Throws `IllegalStateException`** if the resolver returns `Optional.empty()` — every authenticated request must have a resolvable tenant context.

```java
public class TenantContextFilter extends OncePerRequestFilter {

    private final TenantResolver tenantResolver;

    @Override
    protected void doFilterInternal(
        HttpServletRequest request, HttpServletResponse response, FilterChain chain
    ) throws ServletException, IOException {
        TenantContext context = tenantResolver.resolve(request)
            .orElseThrow(() -> new IllegalStateException(
                "No tenant context could be resolved for this request"));
        TenantContextHolder.setContext(context);
        try {
            chain.doFilter(request, response);
        } finally {
            TenantContextHolder.clear();
        }
    }
}
```

## TenantResolver

```java
@FunctionalInterface
public interface TenantResolver {
    Optional<TenantContext> resolve(HttpServletRequest request);
}
```

`SecurityContextTenantResolver` wraps a `Function<Authentication, Optional<TenantContext>>` and reads the `Authentication` from `SecurityContextHolder` internally. Wired in the security config:

```java
new SecurityContextTenantResolver(auth -> {
    if (auth instanceof EnrichedJwtAuthenticationToken token) {
        if (token.isSuperadmin()) {
            return Optional.of(TenantContext.bypass());
        }
        return Optional.of(TenantContext.forTenants(token.getUnitIds()));
    }
    return Optional.empty();
});
```

## TenantContextRunner — programmatic bypass

Use `TenantContextRunner` when code needs to run with bypass **without** an HTTP context. Prefer this over manipulating `TenantContextHolder` directly.

```java
// Inject as a Spring bean
private final TenantContextRunner tenantContextRunner;

// Run side-effect with bypass
tenantContextRunner.runWithBypass(() -> someService.doAdminWork());

// Return a value with bypass
String result = tenantContextRunner.supplyWithBypass(() -> service.getData());
```

**Used in**: CORS configuration startup, `PostAuthProvisioningFilter`, any Spring bean that needs DB access before the HTTP tenant context is established.

## Async context propagation

### @Async — TenantContextTaskDecorator

Register as the decorator of your async executor to propagate context across thread boundaries:

```java
@Bean(name = "taskExecutor")
Executor taskExecutor() {
    ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
    executor.setTaskDecorator(new TenantContextTaskDecorator());
    executor.initialize();
    return executor;
}
```

### Virtual threads / WebFlux / CompletableFuture — TenantContextThreadLocalAccessor

Registered automatically via ServiceLoader when `io.micrometer:context-propagation` is on the classpath. Enables context propagation across:
- Virtual threads (Project Loom)
- `@Async` methods
- `CompletableFuture` chains
- Reactive boundaries (WebFlux)

## Rules

- `TenantContextFilter` must be **after** `PostAuthProvisioningFilter`
- Always `clear()` in `finally` — servlet threads are reused
- Use `requireContext()` (not `getContext()`) in repository/infrastructure code
- Use `TenantContextRunner` for programmatic bypass — never call `TenantContextHolder.setContext(TenantContext.bypass())` in application code
- Filters and converters that run before `TenantContextFilter` are the only valid exceptions — they must clear in `finally`
