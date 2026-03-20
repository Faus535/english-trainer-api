# Fase 1: Checked Exceptions

## Problema

La skill `domain-design` establece: *"Extend Exception (checked), NEVER RuntimeException"*.

Actualmente el dominio lanza `IllegalArgumentException` (RuntimeException) en:

### Value Objects (IDs) — 12 ficheros
Todos los ID records lanzan `IllegalArgumentException` en su compact constructor:
- `user/domain/UserProfileId.java:9`
- `vocabulary/domain/VocabEntryId.java:9`
- `phrase/domain/PhraseId.java:9`
- `activity/domain/ActivityDateId.java:9`
- `moduleprogress/domain/ModuleProgressId.java:9`
- `gamification/domain/AchievementId.java:7`
- `gamification/domain/UserAchievementId.java:9`
- `spacedrepetition/domain/SpacedRepetitionItemId.java:9`
- `assessment/domain/LevelTestResultId.java:9`
- `assessment/domain/MiniTestResultId.java:9`
- `session/domain/SessionId.java:9`
- `auth/domain/AuthUserId.java:9`

### Value Objects (Enums con fromString) — 6 ficheros
- `user/domain/UserLevel.java:11,15`
- `vocabulary/domain/VocabLevel.java:11,15`
- `moduleprogress/domain/ModuleName.java:11,15`
- `moduleprogress/domain/ModuleLevel.java:11,15`
- `session/domain/SessionMode.java:11`
- `session/domain/SessionType.java:11`

### Aggregates — 1 fichero
- `user/domain/UserProfile.java:88` — `updateModuleLevel()` lanza `IllegalArgumentException`
- `user/domain/UserProfile.java:99` — `addXp()` lanza `IllegalArgumentException`

### Use Cases — 1 fichero
- `session/application/GenerateSessionUseCase.java:31` — `IllegalStateException`

### Persistence Adapters — 3 ficheros
- `session/infrastructure/persistence/JpaSessionRepositoryAdapter.java:95,106`
- `assessment/infrastructure/persistence/JpaLevelTestResultRepositoryAdapter.java:47,66`
- `moduleprogress/infrastructure/persistence/JpaModuleProgressRepositoryAdapter.java:81,92,100,111`

## Solucion

### Paso 1: Crear excepciones base en `shared/domain/error/`

```java
// InvalidValueException.java — para validaciones de Value Objects
public class InvalidValueException extends Exception {
    public InvalidValueException(String message) {
        super(message);
    }
}

// InvalidOperationException.java — para operaciones de dominio invalidas
public class InvalidOperationException extends Exception {
    public InvalidOperationException(String message) {
        super(message);
    }
}
```

### Paso 2: Crear excepciones especificas por modulo en `{module}/domain/error/`

Ejemplo para `user`:
```java
// UserProfileException.java — base del aggregate
public class UserProfileException extends Exception {
    public UserProfileException(String message) { super(message); }
}

// InvalidModuleException.java
public class InvalidModuleException extends UserProfileException {
    public InvalidModuleException(String module) {
        super("Unknown module: " + module);
    }
}

// InvalidXpAmountException.java
public class InvalidXpAmountException extends UserProfileException {
    public InvalidXpAmountException(int amount) {
        super("XP amount cannot be negative: " + amount);
    }
}
```

### Paso 3: Actualizar Value Objects

Los IDs con UUID son un caso especial: la validacion `null` en un constructor de record se puede mantener como `IllegalArgumentException` porque es un error de programacion (precondition), no de negocio. **Pero** los `fromString()` de enums si deben ser checked:

```java
// Antes
public static UserLevel fromString(String value) {
    // throws IllegalArgumentException
}

// Despues
public static UserLevel fromString(String value) throws InvalidValueException {
    // throws InvalidValueException
}
```

### Paso 4: Propagar `throws` en cascada

Los metodos que llamen a dominio deben declarar `throws`:
- Use Cases: `execute() throws XxxException`
- Controllers: `handle() throws XxxException` (Spring lo captura via ControllerAdvice)

## Ficheros a modificar

| Fichero | Cambio |
|---------|--------|
| `shared/domain/error/InvalidValueException.java` | NUEVO |
| `shared/domain/error/InvalidOperationException.java` | NUEVO |
| `user/domain/error/UserProfileException.java` | NUEVO |
| `user/domain/error/InvalidModuleException.java` | NUEVO |
| `user/domain/error/InvalidXpAmountException.java` | NUEVO |
| `session/domain/error/SessionException.java` | NUEVO |
| `session/domain/error/NoActiveSessionException.java` | NUEVO |
| `user/domain/UserProfile.java` | Cambiar throws a checked |
| `user/domain/UserLevel.java` | fromString throws checked |
| `vocabulary/domain/VocabLevel.java` | fromString throws checked |
| `moduleprogress/domain/ModuleName.java` | fromString throws checked |
| `moduleprogress/domain/ModuleLevel.java` | fromString throws checked |
| `session/domain/SessionMode.java` | fromString throws checked |
| `session/domain/SessionType.java` | fromString throws checked |
| Todos los Use Cases que llaman a estos metodos | Propagar throws |
| Todos los Controllers que llaman a estos Use Cases | Propagar throws |
| Todos los Repository Adapters con IllegalStateException | Cambiar a checked |

## Criterio de aceptacion

- `grep -r "IllegalArgumentException\|IllegalStateException" src/main/java/` no devuelve resultados en dominio ni aplicacion
- El proyecto compila sin errores
- Los controllers propagan las excepciones y el ControllerAdvice las mapea
