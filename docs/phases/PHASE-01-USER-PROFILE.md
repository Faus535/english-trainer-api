# Fase 1 — User Profile completo

## Objetivo

Completar el CRUD del aggregate `UserProfile` para que el frontend pueda delegar
toda la gestion del perfil al API en lugar de localStorage.

## Estado actual

- `UserProfile` aggregate existe con factory methods `create()` y `reconstitute()`
- Metodos de dominio existen: `markTestCompleted()`, `updateModuleLevel()`, `recordSession()`, `addXp()`
- Solo 2 endpoints: `POST /api/profiles` y `GET /api/profiles/{id}`
- **Falta**: endpoints para actualizar campos, resetear semana, eliminar perfil

## Entidades y Value Objects

Ya existentes (revisar y ajustar si hace falta):
- `UserProfile` — Aggregate Root
- `UserProfileId` — Value Object (UUID)
- `UserLevel` — Value Object (a1, a2, b1, b2, c1, c2)

## Migraciones

No se necesitan nuevas tablas. La tabla `user_profiles` ya tiene todas las columnas.

## Use Cases

| Use Case | Tipo | Descripcion |
|----------|------|-------------|
| `MarkTestCompletedUseCase` | Command | Marca el test inicial como completado |
| `UpdateModuleLevelUseCase` | Command | Actualiza el nivel de un modulo especifico |
| `RecordSessionUseCase` | Command | Incrementa sessionCount y sessionsThisWeek |
| `AddXpUseCase` | Command | Suma XP al perfil |
| `ResetWeeklyCountersUseCase` | Command | Resetea sessionsThisWeek y actualiza weekStart |
| `DeleteUserProfileUseCase` | Command | Elimina un perfil |

## Endpoints

| Metodo | Ruta | Use Case | Request Body | Response |
|--------|------|----------|-------------|----------|
| PUT | `/api/profiles/{id}/test-completed` | MarkTestCompletedUseCase | - | 204 |
| PUT | `/api/profiles/{id}/modules/{module}/level` | UpdateModuleLevelUseCase | `{ "level": "b1" }` | 204 |
| POST | `/api/profiles/{id}/sessions` | RecordSessionUseCase | - | 200 UserProfileResponse |
| POST | `/api/profiles/{id}/xp` | AddXpUseCase | `{ "amount": 50 }` | 200 UserProfileResponse |
| DELETE | `/api/profiles/{id}` | DeleteUserProfileUseCase | - | 204 |

## Criterios de aceptacion

- [ ] Todos los metodos de dominio de `UserProfile` tienen su endpoint correspondiente
- [ ] Validacion: nivel debe ser uno de a1/a2/b1/b2/c1/c2
- [ ] Validacion: modulo debe ser uno de listening/vocabulary/grammar/phrases/pronunciation
- [ ] Validacion: XP amount debe ser positivo
- [ ] 404 cuando el perfil no existe
- [ ] Tests unitarios para cada use case
- [ ] Tests de integracion para cada endpoint
