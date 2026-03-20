---
name: persistence
description: This skill should be used when the user asks to "create an entity", "implement a repository", "write a database migration", "Flyway migration", "map domain to entity", "Spring Data JDBC", "Spring Data JPA", "JPA entity", "@Entity", "@Version", "optimistic locking", "@ManyToMany", "@ElementCollection", "CriteriaToJPASpecification", "Elasticsearch repository", "index a document", "search Elasticsearch", or "ElasticsearchClient". Defines persistence entities, repository implementations, Flyway migrations, and Elasticsearch repositories.
---

# Skill: Persistence

Defines patterns for the persistence layer using Spring Data JDBC, Spring Data JPA, and Flyway.

## When to use

- When implementing a repository for an aggregate
- When creating a persistence entity
- When writing database migrations
- When mapping between domain and persistence layers

## Key rules

### Persistence Entity

1. Annotated with `@Table(name = "table_name")`
2. Implements `Persistable<UUID>` for isNew() control
3. `@Transient boolean exists` field for update detection
4. Constructor from domain + `toDomain()` method
5. Empty constructor for Spring Data

### Repository Implementation

1. Annotated with `@Repository`
2. **Package-private** visibility (no `public`)
3. Constructor injection of Spring Data's CrudRepository
4. Bidirectional mapping: Domain <-> Entity
5. exists/save logic for insert vs update control

### Spring Data JDBC Repository

1. Extends `CrudRepository<Entity, UUID>`
2. **Package-private** visibility
3. Annotated with `@Repository`
4. Custom queries with `@Query`

### Flyway Migrations

1. Location: `src/main/resources/db/migration/`
2. Format: `V{MAJOR}.{MINOR}.{PATCH}__{module}_{description}.sql`
3. Use `IF NOT EXISTS` / `IF EXISTS` for idempotency
4. One migration per feature/change

### JPA Entity

1. `@Entity` + `@Table(name = "table_name")`
2. `@Id` **without** `@GeneratedValue` — UUID generated in domain, not by the DB
3. `@Version` for optimistic locking (Hibernate throws `OptimisticLockException` on conflict)
4. `fromAggregate()` / `toAggregate()` static methods for bidirectional mapping
5. No-arg constructor required by Hibernate proxy
6. Domain classes never import `jakarta.persistence` — JPA stays in `infrastructure/jpa/`

### JPA Repository

1. Interface extending `JpaRepository<Entity, UUID>` + `JpaSpecificationExecutor<Entity>`
2. Suffix `JpaRepository` (e.g., `YaraJpaRepository`)
3. No `@Query` — use `Specification` for dynamic filtering
4. Use `saveAndFlush` (not `save`) to detect constraint violations immediately
5. Catch `DataIntegrityViolationException` in the repository impl → convert to domain exception

### JPA Pagination with Criteria

1. Repository receives `Criteria` (from `com.s2grupo.commons.data.domain.Criteria`)
2. Convert with `CriteriaToJPASpecification.toPageable(criteria)` and `toSpecification(criteria)`
3. Catch `InvalidFilter` → rethrow as domain exception
4. Map `org.springframework.data.domain.Page` → `com.s2grupo.commons.shared.Page`
5. See [JPA](references/jpa.md) for full examples

### Elasticsearch repositories

1. Use the Java Elasticsearch client (`co.elastic.clients`) directly — not Spring Data Elasticsearch
2. Documents are plain records or classes; never use `@Document` / `@Field` annotations
3. Index names are tenant-scoped: always resolve via `ElasticsearchTenantHelper.resolveIndexWithTenant()`
4. Always apply tenant filtering (direct or indirect) — see the security skill
5. Extend `ElasticsearchAbstractRepository` for pagination + sort utilities

## References

- [Persistence Entity (JDBC)](references/persistence-entity.md)
- [Repository Implementation (JDBC)](references/repository-implementation.md)
- [JPA Entity & Repository](references/jpa.md)
- [Flyway Migrations](references/flyway-migrations.md)
- [Elasticsearch](references/elasticsearch.md)
