# JPA Entity & Repository Pattern

Spring Data JPA is used for aggregates that need optimistic locking, complex relations, or dynamic Specification-based queries. Domain classes never import `jakarta.persistence` — JPA is confined to `infrastructure/jpa/`.

## Package structure

```
infrastructure/
└── jpa/
    ├── {Entity}Entity.java          # JPA entity
    ├── {Entity}JpaRepository.java   # Spring Data JPA interface
    └── {Entity}RepositoryImpl.java  # Implements domain repository interface
```

## JPA Entity

```java
@Entity
@Table(name = "yara")
public class YaraEntity {

    @Id
    @Column(name = "id")
    private UUID id;                        // No @GeneratedValue — UUID from domain

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "description")
    private String description;

    @Version
    @Column(name = "version", nullable = false)
    private Integer version;                // Optimistic locking

    @Column(name = "update_date")
    @OptimisticLock(excluded = true)        // Does not increment version
    private Date updateDate;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "yara_mitre", joinColumns = @JoinColumn(name = "yara_id"))
    @Column(name = "mitre_tag", nullable = false)
    private Set<String> mitreTags = new HashSet<>();

    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.DETACH)
    @JoinTable(
        name = "yara_tag",
        joinColumns = @JoinColumn(name = "yara_id", referencedColumnName = "id"),
        inverseJoinColumns = @JoinColumn(name = "tag_id", referencedColumnName = "id")
    )
    private Set<YaraTagTagEntity> tags = new HashSet<>();

    @Column(name = "rule_content", nullable = false)
    @Lob
    private byte[] ruleContent;

    // Calculated read-only field via native SQL subquery — not persisted
    @Formula("(select exists (select 1 from knowledge_schedules ks where ks.knowledge_id = id and ks.end_date > CURRENT_TIMESTAMP))")
    private Boolean scheduled;

    YaraEntity() {}                         // Required by Hibernate

    public static YaraEntity fromAggregate(Yara yara) { ... }
    public static Yara toAggregate(YaraEntity entity) { ... }
}
```

### Key annotations

| Annotation | Purpose |
|---|---|
| `@Entity` + `@Table` | Explicit table mapping |
| `@Id` (no `@GeneratedValue`) | UUID generated in domain before save |
| `@Version` | Optimistic locking — Hibernate throws `OptimisticLockException` on conflict |
| `@OptimisticLock(excluded=true)` | Hibernate: field updates don't increment version |
| `@ElementCollection` + `@CollectionTable` | Collection of scalars in a separate table |
| `@ManyToMany` + `@JoinTable` | Many-to-many via join table |
| `CascadeType.DETACH` | Read-only relation — never creates or deletes the related entity |
| `@Lob` | Binary content (`byte[]` → `bytea` in PostgreSQL) |
| `@Formula` | Read-only field computed by a native SQL subquery |

### equals / hashCode

Base `equals`/`hashCode` only on `id` — never on mutable fields:

```java
@Override
public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof YaraEntity that)) return false;
    return Objects.equals(id, that.id);
}

@Override
public int hashCode() {
    return Objects.hash(id);
}
```

## JPA Repository interface

```java
@Repository
interface YaraJpaRepository
        extends JpaRepository<YaraEntity, UUID>,
                JpaSpecificationExecutor<YaraEntity> {
    // No @Query — dynamic filtering via Specification
}
```

## Repository implementation

```java
@Repository
class YaraRepositoryImpl implements YaraRepository {

    private final YaraJpaRepository jpaRepository;

    @Override
    public Yara save(Yara yara) {
        try {
            // saveAndFlush: flushes immediately so constraint violations are caught here
            return YaraEntity.toAggregate(
                jpaRepository.saveAndFlush(YaraEntity.fromAggregate(yara))
            );
        } catch (DataIntegrityViolationException e) {
            throw new YaraNameDuplicatedException(); // UK violation → domain exception
        }
    }

    @Override
    public Optional<Yara> findById(UUID id) {
        return jpaRepository.findById(id).map(YaraEntity::toAggregate);
    }

    @Override
    public void delete(UUID id) {
        jpaRepository.deleteById(id);
    }

    @Override
    public Page<Yara> findAll(Criteria criteria) {
        Pageable pageable = CriteriaToJPASpecification.toPageable(criteria);
        Specification<YaraEntity> specification;
        try {
            specification = CriteriaToJPASpecification.toSpecification(criteria);
        } catch (InvalidFilter e) {
            throw new YaraException("Invalid filter in criteria", e);
        }
        org.springframework.data.domain.Page<YaraEntity> result =
            jpaRepository.findAll(specification, pageable);
        return new Page<>(
            result.getTotalElements(), result.getNumber(),
            result.getSize(), result.getNumberOfElements(),
            result.getContent().stream().map(YaraEntity::toAggregate).toList()
        );
    }
}
```

## Pagination with Criteria (s2-commons)

The repository receives a `Criteria` object (from `com.s2grupo.commons.data.domain.Criteria`) and uses `CriteriaToJPASpecification` to convert it into a Spring `Specification<Entity>` and `Pageable`:

```java
@Override
public Page<AuditEvent> getEvents(Criteria criteria) {
    log.debug("Query AuditEntity: criteria={}", criteria);

    Pageable pageable = CriteriaToJPASpecification.toPageable(criteria);
    Specification<AuditEntity> specification;
    try {
        specification = CriteriaToJPASpecification.toSpecification(criteria);
    } catch (InvalidFilter e) {
        throw new AuditException("Invalid filter in criteria", e);
    }

    org.springframework.data.domain.Page<AuditEntity> page =
        jpaRepository.findAll(specification, pageable);

    log.debug("Retrieved {} events from AuditEntity", page.getTotalElements());

    return new Page<>(
        page.getTotalElements(),
        page.getNumber(),
        page.getSize(),
        page.getNumberOfElements(),
        page.getContent().stream().map(AuditEntityMapper::toDomain).toList()
    );
}
```

- `CriteriaToJPASpecification.toPageable(criteria)` — builds `PageRequest` from `page`, `size`, `sortBy`, `orderBy`
- `CriteriaToJPASpecification.toSpecification(criteria)` — converts `Filters` to a Spring `Specification<Entity>`; throws `InvalidFilter` on invalid filter definitions
- Map the Spring `Page` fields to s2-commons `Page<T>`: `totalElements`, `number`, `size`, `numberOfElements`, `content`

## Rules

- Never put `jakarta.persistence` imports in domain classes
- Always `@Id` without `@GeneratedValue` — UUID is assigned in the domain before calling `save`
- Use `saveAndFlush` (not `save`) when constraint violations must be caught in the same method
- Catch `DataIntegrityViolationException` in the repository impl and rethrow as a domain exception
- Use `CascadeType.DETACH` for read-only `@ManyToMany` relations owned by another aggregate
- Use `CriteriaToJPASpecification` for dynamic filtering — no `@Query`; catch `InvalidFilter` and rethrow as domain exception
- `@Version` is mandatory on aggregates that can be concurrently modified
