# Tag Conventions

## Rule: Follow consistent naming and placement for all tags

### Convention
- Status tags: `@lowercase` single word — `@todo`, `@wip`, `@exe`, `@skip`
- Type tags: `@lowercase` single word — `@api`, `@web`, `@smoke`, `@regression`
- Feature tags: `@kebab-case` — `@user-login`, `@order-checkout`, `@product-search`
- Priority tags: `@lowercase` single word — `@critical`, `@high`, `@low`

### Tag lifecycle

A typical scenario moves through these status tags during development:

```
@todo → @wip → @exe → (tag removed, test is part of regular suite)
```

1. **`@todo`**: Scenario is defined in the feature file but step definitions are not yet implemented
2. **`@wip`**: Step definitions are being written, test may fail
3. **`@exe`**: Test is ready and should be executed in the current cycle
4. Once validated, remove the status tag — the test becomes part of the regular suite

### Standard exclusion pattern

Default test execution should exclude incomplete tests:
```bash
-t "~@todo" -t "~@wip"
```

To run only tests marked for the current cycle:
```bash
-t "~@todo" -t "~@wip" -t "@exe"
```

### Naming rules

| Do | Don't |
|----|-------|
| `@user-login` | `@UserLogin`, `@user_login` |
| `@api` | `@API`, `@Api` |
| `@order-checkout` | `@checkout-order-flow` (too verbose) |
| `@critical` | `@p1`, `@prio-critical` |