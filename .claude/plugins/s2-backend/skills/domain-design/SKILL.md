---
name: domain-design
description: This skill should be used when the user asks to "create an aggregate", "add a value object", "define a domain event", "create a repository interface", "domain exception", or "DDD patterns". Defines immutable Aggregate Roots, Value Objects as records, Domain Events, Repository interfaces, and checked domain exceptions.
---

# Skill: Domain Design

Defines patterns for modeling the domain following DDD. The domain is the innermost layer and MUST NOT have dependencies
on external frameworks.

## When to use

- When creating a new aggregate or domain entity
- When defining Value Objects for attributes
- When designing domain events
- When defining repository interfaces
- When creating domain exceptions
- When reviewing domain purity

## Key rules

### Aggregate Root

1. Extends `AggregateRoot<T>` base class
2. All fields are `final` (immutable, no setters)
3. State changes return **new instances**
4. No public constructors
5. Registers domain events with `registerEvent()`
6. Factory methods: `create()`, `update()`, `delete()`
7. Getters without prefix: `id()`, `title()` (not `getId()`)

### Value Objects

1. Implemented as Java **records**
2. Validation in the **compact constructor**
3. `value()` method to access the data
4. Located in the `domain/` package

### Domain Events

1. Implemented as **records** with `@DomainEvent`
2. Named in past tense: `{Entity}{Action}Event` (e.g., `TaskCreatedEvent`)
3. Located in `{module}/events/` package
4. Create `package-info.java` for Spring Modulith

### Repository Interface

1. Pure interface in `domain/` (no Spring annotations)
2. Defines domain operations (not generic CRUD)
3. Implementation lives in `infrastructure/`

### Domain Exceptions

1. Extend `Exception` (checked), NEVER `RuntimeException`
2. One base exception per aggregate: `{Aggregate}Exception`
3. Specific exceptions extend the base
4. Located in `domain/error/`
5. The compiler forces their handling

### Domain Services

1. Use when logic involves multiple aggregates or belongs to none of them
2. Suffix: `DomainService` (e.g., `TransferFundsDomainService`)
3. No external dependencies (no Spring, no repositories)
4. Receives already-loaded aggregates as parameters

### Cross-aggregate relations

1. Aggregates reference each other **by ID only** (ID Value Object, never an instance)
2. Value Objects belong to their own aggregate — do not share VOs between aggregates
3. To access data from another aggregate: the Use Case loads both and orchestrates

## References

- [Aggregate Root](references/aggregate-root.md)
- [Value Objects](references/value-objects.md)
- [Domain Events](references/domain-events.md)
- [Repository Interface](references/repository-interface.md)
- [Domain Exceptions](references/domain-exceptions.md)
