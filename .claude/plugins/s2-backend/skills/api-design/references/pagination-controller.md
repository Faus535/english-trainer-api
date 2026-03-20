# Pagination Controller Pattern (s2-commons)

Controller receives loose query params and builds a `Criteria` object from `com.s2grupo.commons`.

## Types (all from `com.s2grupo.commons`)

| Type | Package |
|---|---|
| `Criteria` | `com.s2grupo.commons.data.domain.Criteria` |
| `Filters` | `com.s2grupo.commons.data.domain.Filters` |
| `Filter` | `com.s2grupo.commons.data.domain.Filter` |
| `FilterOperator` | `com.s2grupo.commons.data.domain.FilterOperator` |
| `Page<T>` | `com.s2grupo.commons.shared.Page` |

## Example controller

```java
@RestController
class FindAllUsersGetController {
    private final FindAllUsersUseCase useCase;

    FindAllUsersGetController(FindAllUsersUseCase useCase) {
        this.useCase = useCase;
    }

    @GetMapping("/users")
    ResponseEntity<Page<UserResponse>> handle(
        @RequestParam(defaultValue = "0") @Min(0) Integer page,
        @RequestParam(defaultValue = "20") @Min(1) Integer size,
        @RequestParam(defaultValue = "DESC") String orderBy,
        @RequestParam(defaultValue = "createdAt") String sortBy
    ) {
        List<Filter> filters = List.of(); // add filters from params as needed
        Criteria criteria = new Criteria(new Filters(filters), page, size, getOrder(sortBy, orderBy));

        Page<UserDto> dto = useCase.execute(criteria);

        return ResponseEntity.ok(new Page<>(
            dto.total(), dto.page(), dto.size(), dto.maxResults(),
            dto.collection().stream().map(UserResponse::fromDto).toList()
        ));
    }

    private Order getOrder(String sortBy, String orderBy) {
        return new Order(Order.Type.valueOf(orderBy.toUpperCase()), sortBy);
    }
}
```

## Rules

- **Do NOT** use Spring's `Pageable` as a controller parameter
- The controller maps `{Entity}Dto → {Entity}Response` via a static `fromDto()` method
- The Use Case returns `Page<{Entity}Dto>` — it has no knowledge of `Response` types
