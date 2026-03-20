# Fase 6: Tests

## Problema

209 ficheros de codigo fuente con 0 tests reales (solo existe `EnglishTrainerApiApplicationTests.java` vacio).

La skill `testing` establece:
- **Object Mothers**: Clases que crean instancias de dominio para tests
- **In-Memory Repositories**: Implementaciones en memoria de los repository interfaces para tests unitarios
- **Constructor injection**: Tests inyectan dependencias manualmente, sin Spring context
- **Testcontainers**: Para tests de integracion con BD real

## Estructura de tests propuesta

```
src/test/java/com/faus535/englishtrainer/
├── shared/
│   └── domain/
│       └── ... (si hay logica compartida que testear)
├── user/
│   ├── domain/
│   │   ├── UserProfileTest.java          ← unit test del aggregate
│   │   ├── UserProfileMother.java        ← Object Mother
│   │   └── UserLevelTest.java            ← unit test del VO
│   ├── application/
│   │   ├── CreateUserProfileUseCaseTest.java
│   │   ├── AddXpUseCaseTest.java
│   │   └── ...
│   └── infrastructure/
│       ├── InMemoryUserProfileRepository.java  ← In-Memory Repo
│       └── UserProfileRepositoryIT.java        ← Integration test (Testcontainers)
├── vocabulary/
│   ├── domain/
│   │   ├── VocabEntryTest.java
│   │   └── VocabEntryMother.java
│   ├── application/
│   │   └── ...
│   └── infrastructure/
│       ├── InMemoryVocabRepository.java
│       └── ...
├── ... (repetir por modulo)
```

## Prioridad de tests

### Tier 1: Dominio (mas valor, cero dependencias)

Tests unitarios puros de aggregates, value objects y domain services:

| Test | Que valida |
|------|-----------|
| `UserProfileTest` | create(), addXp(), markTestCompleted(), updateModuleLevel(), resetWeeklyCounters() |
| `UserLevelTest` | fromString() valido e invalido, defaultLevel() |
| `VocabEntryTest` | create(), reconstitute() |
| `VocabLevelTest` | fromString() valido e invalido |
| `StreakCalculatorTest` | Calculo de rachas con diferentes combinaciones de fechas |
| `SpacedRepetitionItemTest` | Intervalos [1,3,7,14,30], complete() avanza nivel |
| `LevelAssignerTest` | Asignacion de nivel basada en puntuacion |
| `SessionGeneratorTest` | Generacion de sesiones por tipo/modo |
| `XpLevelTest` | Calculo de nivel por XP |

### Tier 2: Aplicacion (Use Cases con In-Memory Repos)

```java
class CreateUserProfileUseCaseTest {

    private final InMemoryUserProfileRepository repository = new InMemoryUserProfileRepository();
    private final CreateUserProfileUseCase useCase = new CreateUserProfileUseCase(repository);

    @Test
    void should_create_a_user_profile() {
        UserProfile profile = useCase.execute();

        assertNotNull(profile.id());
        assertFalse(profile.testCompleted());
        assertEquals(0, profile.xp());
    }
}
```

### Tier 3: Infraestructura (Testcontainers)

```java
@SpringBootTest
@Testcontainers
class UserProfileRepositoryIT {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16");

    @Test
    void should_persist_and_retrieve_a_user_profile() { ... }
}
```

Requiere annadir dependencia en `build.gradle`:
```gradle
testImplementation 'org.testcontainers:postgresql:1.20.4'
testImplementation 'org.testcontainers:junit-jupiter:1.20.4'
```

## Object Mothers

```java
public final class UserProfileMother {

    public static UserProfile random() {
        return UserProfile.create();
    }

    public static UserProfile withXp(int xp) {
        UserProfile profile = UserProfile.create();
        return profile.addXp(xp);
    }

    public static UserProfile withTestCompleted() {
        return UserProfile.create().markTestCompleted();
    }
}
```

## In-Memory Repositories

```java
public class InMemoryUserProfileRepository implements UserProfileRepository {

    private final Map<UUID, UserProfile> store = new HashMap<>();

    @Override
    public UserProfile save(UserProfile profile) {
        store.put(profile.id().value(), profile);
        return profile;
    }

    @Override
    public Optional<UserProfile> findById(UserProfileId id) {
        return Optional.ofNullable(store.get(id.value()));
    }

    @Override
    public void deleteById(UserProfileId id) {
        store.remove(id.value());
    }

    // helpers para asserts en tests
    public int count() { return store.size(); }
    public boolean contains(UserProfileId id) { return store.containsKey(id.value()); }
}
```

## Ficheros a crear (~50+)

### Por cada modulo (x11 modulos):
| Tipo | Cantidad aprox. |
|------|----------------|
| Object Mothers | 1-2 por aggregate |
| In-Memory Repositories | 1 por repository interface |
| Unit tests de dominio | 1-3 por aggregate/VO/service |
| Unit tests de use cases | 1 por use case |
| Integration tests | 1 por repository adapter |

### Prioridad de implementacion:
1. **user** — modulo mas complejo (8 use cases)
2. **spacedrepetition** — logica de intervalos critica
3. **activity** — StreakCalculator tiene logica compleja
4. **assessment** — LevelAssigner tiene reglas de negocio
5. **session** — SessionGenerator tiene logica compleja
6. Resto de modulos

## Depende de

- **Fase 1**: Los tests deben validar que las checked exceptions se lanzan correctamente
- **Fase 2**: Los In-Memory Repos deben seguir el mismo contrato que los adapters JPA

## Criterio de aceptacion

- Cada aggregate tiene al menos 1 unit test
- Cada use case tiene al menos 1 test con In-Memory Repository
- Existe un Object Mother por aggregate
- Existe un In-Memory Repository por repository interface
- `./gradlew test` pasa al 100%
- Cobertura minima: 60% en domain/ y application/
