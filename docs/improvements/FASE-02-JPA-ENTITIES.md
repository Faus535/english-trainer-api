# Fase 2: JPA Entities (Persistable, @Version, fromAggregate/toAggregate)

## Problema

La skill `persistence` establece tres reglas que ninguna entity cumple:

1. **`Persistable<UUID>`**: Las entities deben implementar esta interfaz con un campo `@Transient boolean exists` para controlar si JPA hace INSERT o UPDATE (sin `@GeneratedValue`, JPA no sabe si el registro es nuevo).

2. **`@Version`**: Todas las entities deben tener optimistic locking para evitar lost updates.

3. **`fromAggregate()`/`toAggregate()`**: Metodos estaticos de mapeo en la entity, en vez de tener la logica de mapeo dispersa en el adapter.

## Entities afectadas (12)

| Entity | Modulo |
|--------|--------|
| `UserProfileEntity` | user |
| `VocabEntryEntity` | vocabulary |
| `PhraseEntity` | phrase |
| `ActivityDateEntity` | activity |
| `ModuleProgressEntity` | moduleprogress |
| `AchievementEntity` | gamification |
| `UserAchievementEntity` | gamification |
| `SpacedRepetitionItemEntity` | spacedrepetition |
| `LevelTestResultEntity` | assessment |
| `MiniTestResultEntity` | assessment |
| `SessionEntity` | session |
| `AuthUserEntity` | auth |

## Solucion

### Paso 1: Patron base para todas las entities

```java
@Entity
@Table(name = "user_profiles")
class UserProfileEntity implements Persistable<UUID> {

    @Id
    private UUID id;

    @Version
    private Long version;

    @Transient
    private boolean isNew;

    // ... campos ...

    protected UserProfileEntity() {}

    static UserProfileEntity fromAggregate(UserProfile aggregate) {
        UserProfileEntity entity = new UserProfileEntity();
        entity.id = aggregate.id().value();
        entity.isNew = true; // nuevo registro
        // ... mapear campos ...
        return entity;
    }

    static UserProfileEntity fromAggregateForUpdate(UserProfileEntity existing, UserProfile aggregate) {
        UserProfileEntity entity = new UserProfileEntity();
        entity.id = aggregate.id().value();
        entity.version = existing.version; // mantener version para optimistic locking
        entity.isNew = false;
        // ... mapear campos ...
        return entity;
    }

    UserProfile toAggregate() {
        return UserProfile.reconstitute(
            new UserProfileId(id),
            testCompleted,
            UserLevel.fromString(levelListening),
            // ...
        );
    }

    @Override
    public UUID getId() { return id; }

    @Override
    public boolean isNew() { return isNew; }
}
```

### Paso 2: Actualizar Repository Adapters

```java
@Repository
class JpaUserProfileRepositoryAdapter implements UserProfileRepository {

    private final JpaUserProfileRepository jpa;

    // ...

    @Override
    public UserProfile save(UserProfile profile) {
        Optional<UserProfileEntity> existing = jpa.findById(profile.id().value());
        UserProfileEntity entity = existing
            .map(e -> UserProfileEntity.fromAggregateForUpdate(e, profile))
            .orElseGet(() -> UserProfileEntity.fromAggregate(profile));
        return jpa.save(entity).toAggregate();
    }
}
```

### Paso 3: Migracion Flyway para columna `version`

```sql
-- V9__add_version_columns.sql
ALTER TABLE user_profiles ADD COLUMN version BIGINT DEFAULT 0;
ALTER TABLE vocab_entries ADD COLUMN version BIGINT DEFAULT 0;
ALTER TABLE phrases ADD COLUMN version BIGINT DEFAULT 0;
-- ... todas las tablas ...
```

## Ficheros a modificar

| Fichero | Cambio |
|---------|--------|
| 12 `*Entity.java` | Implementar Persistable, annadir @Version, fromAggregate/toAggregate |
| 12 `*RepositoryAdapter.java` | Simplificar usando fromAggregate/toAggregate |
| 1 nueva migracion Flyway | Annadir columna `version` a todas las tablas |

## Criterio de aceptacion

- Todas las entities implementan `Persistable<UUID>`
- Todas las entities tienen `@Version`
- El mapeo domain<->entity esta encapsulado en la entity con metodos estaticos
- Los adapters no contienen logica de mapeo directa
- La migracion Flyway aplica correctamente
