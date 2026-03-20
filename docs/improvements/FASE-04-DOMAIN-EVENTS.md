# Fase 4: Domain Events

## Problema

La skill `domain-design` establece:
- *"Registers domain events with registerEvent()"*
- *"Implemented as records with @DomainEvent"*
- *"Named in past tense: {Entity}{Action}Event"*

La clase `AggregateRoot` ya tiene `registerEvent()` y `pullDomainEvents()`, pero ningun aggregate los usa. Esto significa que no hay forma de reaccionar a cambios de dominio de forma desacoplada.

## Donde son utiles los Domain Events

| Evento | Aggregate | Reaccion posible |
|--------|-----------|-----------------|
| `UserProfileCreatedEvent` | UserProfile | Inicializar modulos de progreso, crear actividad |
| `SessionCompletedEvent` | Session | Registrar actividad, otorgar XP, verificar logros |
| `LevelTestCompletedEvent` | LevelTestResult | Actualizar niveles del perfil |
| `AchievementUnlockedEvent` | UserAchievement | Notificar al usuario (futuro) |
| `ReviewCompletedEvent` | SpacedRepetitionItem | Actualizar estadisticas, otorgar XP |
| `XpGrantedEvent` | UserProfile | Verificar logros de XP |
| `StreakUpdatedEvent` | ActivityDate | Verificar logros de racha |

## Solucion

### Paso 1: Crear annotation @DomainEvent

```java
// shared/domain/event/DomainEvent.java
package com.faus535.englishtrainer.shared.domain.event;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface DomainEvent {}
```

### Paso 2: Crear eventos como records

```java
// user/domain/event/UserProfileCreatedEvent.java
package com.faus535.englishtrainer.user.domain.event;

import com.faus535.englishtrainer.shared.domain.event.DomainEvent;
import com.faus535.englishtrainer.user.domain.UserProfileId;

@DomainEvent
public record UserProfileCreatedEvent(UserProfileId profileId) {}
```

```java
// session/domain/event/SessionCompletedEvent.java
@DomainEvent
public record SessionCompletedEvent(
    SessionId sessionId,
    UserProfileId profileId,
    int xpEarned
) {}
```

### Paso 3: Registrar eventos en los aggregates

```java
// UserProfile.java
public static UserProfile create() {
    UserProfile profile = new UserProfile(...);
    profile.registerEvent(new UserProfileCreatedEvent(profile.id()));
    return profile;
}
```

```java
// Session.java
public Session complete(int score) {
    Session completed = new Session(..., true, ...);
    completed.registerEvent(new SessionCompletedEvent(id, profileId, xpEarned));
    return completed;
}
```

### Paso 4: Publicar eventos despues del save

Los eventos se publican en el Use Case despues de persistir:

```java
@UseCase
public class CompleteSessionUseCase {
    private final ApplicationEventPublisher eventPublisher;

    public void execute(...) {
        Session session = session.complete(score);
        repository.save(session);
        session.pullDomainEvents().forEach(eventPublisher::publishEvent);
    }
}
```

### Paso 5 (futuro): Event Listeners

Los listeners se implementarian cuando haya logica reactiva real. Por ahora, registrar los eventos es suficiente para tener la infraestructura lista.

## Ficheros a crear

| Fichero | Tipo |
|---------|------|
| `shared/domain/event/DomainEvent.java` | Annotation |
| `user/domain/event/UserProfileCreatedEvent.java` | Event |
| `user/domain/event/XpGrantedEvent.java` | Event |
| `session/domain/event/SessionCompletedEvent.java` | Event |
| `assessment/domain/event/LevelTestCompletedEvent.java` | Event |
| `gamification/domain/event/AchievementUnlockedEvent.java` | Event |
| `spacedrepetition/domain/event/ReviewCompletedEvent.java` | Event |
| `activity/domain/event/StreakUpdatedEvent.java` | Event |

## Ficheros a modificar

| Fichero | Cambio |
|---------|--------|
| `user/domain/UserProfile.java` | registerEvent en create(), addXp() |
| `session/domain/Session.java` | registerEvent en complete() |
| `assessment/domain/LevelTestResult.java` | registerEvent en create() |
| `gamification/domain/UserAchievement.java` | registerEvent en create() |
| `spacedrepetition/domain/SpacedRepetitionItem.java` | registerEvent en complete() |
| Use Cases que persisten aggregates | Publicar eventos post-save |

## Criterio de aceptacion

- Existe `@DomainEvent` annotation
- Los eventos clave estan definidos como records
- Los aggregates registran eventos en operaciones significativas
- Los Use Cases publican los eventos despues de persistir
