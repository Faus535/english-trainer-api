# Elasticsearch Tenant Filtering

`ElasticsearchTenantHelper` and `ElasticsearchTenantEntityResolver` apply the current `TenantContext` to Elasticsearch queries and index resolution. Both use `TenantContextHolder.requireContext()` — the context **must** be set before any call.

## Index resolution

Tenant-scoped indices follow the pattern `{base-name}-{tenantId}`:

```java
// Single tenant → "agents-abc123"
// Multiple tenants → "agents-*" (wildcard)
// Bypass → "agents-*"
String index = ElasticsearchTenantHelper.resolveIndexWithTenant("agents");

// For multi-index queries
List<String> indices = ElasticsearchTenantHelper.resolveIndicesWithTenant(
    List.of("agents", "tasks")
);
// bypass → ["agents-*", "tasks-*"]
// single tenant → ["agents-abc123", "tasks-abc123"]
// multi-tenant → ["agents-abc123", "agents-def456", "tasks-abc123", "tasks-def456"]
```

## Direct tenant filtering

Use when the index document contains the tenant ID directly:

```java
// Adds bool.must([original_query, terms(field IN tenantIds)])
// In bypass mode: returns original query unchanged
Query filtered = ElasticsearchTenantHelper.wrapWithTenantFilter(
    originalQuery,
    "agency.id.keyword"   // field that holds the tenant ID
);
```

Build filter independently (e.g., to compose manually):
```java
Optional<Query> tenantFilter = ElasticsearchTenantHelper.buildTenantFilter("agency.id.keyword");
// Optional.empty() in bypass mode
```

Check if a single ID is allowed for the current tenant:
```java
boolean allowed = ElasticsearchTenantHelper.isTenantAllowed(agencyId);
```

## Indirect tenant filtering

Use when the document references an entity (e.g., `agent`) that belongs to a tenant via a separate index:

```
tasks-result index
  └── agent field (agent ID)
          │
          └── agents index
                └── agency.id.keyword (tenant ID)
```

```java
// Step 1: resolve which agent IDs belong to the current tenant
Optional<List<String>> agentIds = tenantEntityResolver.resolveEntityIds(
    "agents",               // intermediate index
    "agency.id.keyword"     // tenant field in that index
);
// Optional.empty() → bypass mode (no filtering needed)
// Optional.of([]) → no agents for this tenant (must return nothing)
// Optional.of([...]) → filter by these agent IDs

// Step 2: wrap the main query with the resolved IDs
Query filtered = ElasticsearchTenantHelper.wrapWithIndirectTenantFilter(
    mainQuery,
    "agent.keyword",   // field in tasks-result that holds the agent ID
    agentIds
);
// bypass → original query unchanged
// empty list → matchNone query (no results)
// with IDs → bool.must([mainQuery, terms(agent.keyword IN agentIds)])
```

## Usage in a repository

```java
@Component
class TaskResultElasticsearchRepository {

    private final ElasticsearchClient client;
    private final ElasticsearchTenantEntityResolver tenantEntityResolver;

    Optional<TaskResultDocument> findByTaskId(String taskId) {
        Query baseQuery = Query.of(q -> q.term(t -> t
            .field("task_id.keyword")
            .value(taskId)
        ));

        // Indirect: tasks-result has agent field → filter via agents index
        Optional<List<String>> agentIds = tenantEntityResolver.resolveEntityIds(
            "agents", "agency.id.keyword"
        );
        Query query = ElasticsearchTenantHelper.wrapWithIndirectTenantFilter(
            baseQuery, "agent.keyword", agentIds
        );

        try {
            SearchResponse<TaskResultDocument> response = client.search(s -> s
                .index(ElasticsearchTenantHelper.resolveIndexWithTenant("tasks-result"))
                .query(query)
                .sort(sort -> sort.field(f -> f.field("updated_at").order(SortOrder.Desc)))
                .size(1),
                TaskResultDocument.class
            );
            return response.hits().hits().stream()
                .findFirst()
                .map(Hit::source);
        } catch (IOException e) {
            log.error("Error searching tasks-result: {}", e.getMessage());
            return Optional.empty();
        }
    }
}
```

## Rules

- Always call `resolveIndexWithTenant()` — never hardcode a tenant suffix
- `requireContext()` throws if no context is set — repositories must run inside an HTTP request or a `@WithRlsBypass` method
- `resolveEntityIds()` capped at 10,000 entities per call — design indices to avoid needing more
- Indirect filtering returning empty list → use `matchNone` (handled automatically by `wrapWithIndirectTenantFilter`)
