# Elasticsearch Repository Pattern

Elasticsearch repositories follow the same domain-infrastructure separation as JDBC repositories. The infrastructure layer uses the Java Elasticsearch client (`co.elastic.clients`) directly — not Spring Data Elasticsearch.

## Package structure

```
infrastructure/
└── elasticsearch/
    ├── {Entity}Document.java          # Deserialization target (record)
    ├── {Entity}ElasticsearchRepository.java  # Elasticsearch adapter
    └── {Entity}RepositoryImpl.java    # Implements domain repository interface
```

## Document (deserialization target)

Use a record or class that mirrors the Elasticsearch document structure. Nested structures are inner records.

```java
public record TaskResultDocument(
    String id,
    @JsonProperty("task_id")   String taskId,
    String name,
    @JsonProperty("computer_name") String computerName,
    String agent,
    @JsonProperty("updated_at") Instant updatedAt,
    ResultStructure result
) {
    public record ResultStructure(
        String msg,
        Object data,
        @JsonProperty("result_code") Integer resultCode
    ) {}
}
```

## Index naming

Indices are tenant-scoped: `{base-name}-{tenantId}`. Always resolve via `ElasticsearchTenantHelper`:

```java
// Single tenant → "tasks-result-abc123"
// Multiple tenants / bypass → "tasks-result-*"
String index = ElasticsearchTenantHelper.resolveIndexWithTenant("tasks-result");
```

## Query patterns

### Term (exact keyword match)
```java
Query.of(q -> q.term(t -> t.field("task_id.keyword").value(taskId)))
```

### IDs query
```java
Query.of(q -> q.ids(i -> i.values(id)))
```

### Bool must (compose multiple conditions)
```java
Query.of(q -> q.bool(b -> b.must(queryA, queryB)))
```

### Match none (return zero results)
```java
Query.of(q -> q.matchNone(m -> m))
```

## Search patterns

### Single result with sort
```java
SearchResponse<TaskResultDocument> response = client.search(s -> s
    .index(index)
    .query(query)
    .sort(sort -> sort.field(f -> f
        .field("updated_at")
        .order(SortOrder.Desc)
    ))
    .size(1),
    TaskResultDocument.class
);
return response.hits().hits().stream()
    .findFirst()
    .map(Hit::source);
```

### List results
```java
SearchResponse<AgentSessionDocument> response = client.search(s -> s
    .index(index)
    .query(query),
    AgentSessionDocument.class
);
return response.hits().hits().stream()
    .map(hit -> mapToAgentSession(hit.source(), hit.id()))
    .toList();
```

### Paginated results with Criteria (via ElasticsearchAbstractRepository)

Extend `ElasticsearchAbstractRepository` for pagination + sorting utilities:

```java
class MyElasticsearchRepository extends ElasticsearchAbstractRepository {

    Page<MyDto> findAll(Criteria criteria) {
        List<SortOptions> sort = getSortOptions(criteria, "createdAt", List.of(index));

        SearchResponse<MyDocument> response = client.search(s -> s
            .index(index)
            .query(buildQuery(criteria))
            .sort(sort)
            .from(criteria.page() * criteria.size())
            .size(criteria.size()),
            MyDocument.class
        );

        long total = response.hits().total().value();
        List<MyDto> items = response.hits().hits().stream()
            .map(hit -> toDto(hit.source(), hit.id()))
            .toList();

        return new Page<>(total, criteria.page(), criteria.size(),
            criteria.size(), items);
    }
}
```

- `getSortOptions()` maps field names to `.keyword` subfields for text fields automatically
- Always add `id` as a tiebreaker for stable pagination

## Indexing (write)

```java
Map<String, Object> document = new HashMap<>();
document.put("id", entity.id());
document.put("task_id", entity.taskId());
document.put("agent", entity.agentId());
document.put("updated_at", entity.updatedAt());

IndexRequest<Map<String, Object>> request = IndexRequest.of(i -> i
    .index(resolveIndexWithTenant("tasks-result"))
    .id(entity.id())
    .document(document)
    .refresh(Refresh.WaitFor)   // wait for document to be searchable
);
client.index(request);
```

Use `Refresh.WaitFor` when the caller needs to read immediately after write. Avoid in high-throughput paths.

## Tenant filtering

Always apply tenant filtering. See the full pattern in [Elasticsearch Tenant Filtering](../../security/references/elasticsearch-tenant.md).

```java
// Direct: document has tenant field
Query filtered = ElasticsearchTenantHelper.wrapWithTenantFilter(
    baseQuery, "agency.id.keyword"
);

// Indirect: document references an entity that belongs to a tenant
Optional<List<String>> agentIds = tenantEntityResolver.resolveEntityIds(
    "agents", "agency.id.keyword"
);
Query filtered = ElasticsearchTenantHelper.wrapWithIndirectTenantFilter(
    baseQuery, "agent.keyword", agentIds
);
```

## Error handling

```java
try {
    SearchResponse<MyDocument> response = client.search(..., MyDocument.class);
    return response.hits().hits().stream()...;
} catch (IOException e) {
    log.error("Elasticsearch search failed on index {}: {}", index, e.getMessage());
    return List.of();  // or Optional.empty() for single results
}
```

For index-not-found resilience on searches, use `.ignoreUnavailable(true)`:
```java
client.search(s -> s
    .index(index)
    .ignoreUnavailable(true)
    .query(query),
    MyDocument.class
)
```

## Domain → infrastructure adapter (RepositoryImpl)

The domain repository interface is implemented by a `RepositoryImpl` that delegates to the Elasticsearch adapter:

```java
@Repository
class TaskResultRepositoryImpl implements TaskResultRepository {

    private final TaskResultElasticsearchRepository elasticsearchRepository;

    @Override
    public Optional<TaskResult> findByTaskId(String taskId) {
        return elasticsearchRepository.findByTaskId(taskId)
            .map(this::toDomain);
    }

    private TaskResult toDomain(TaskResultDocument doc) {
        // map fields, handle nulls, throw on missing required data
    }
}
```

## Rules

- Never use Spring Data Elasticsearch annotations (`@Document`, `@Field`) — use plain records + the Java client directly
- Always use `.keyword` suffix for exact matches and sorting on text fields
- Always resolve index via `ElasticsearchTenantHelper.resolveIndexWithTenant()` — never hardcode tenant suffixes
- Always apply tenant filtering — direct or indirect depending on index structure
- `requireContext()` throws if no context — repositories run inside HTTP requests or `@WithRlsBypass` annotated methods
