# Fase 2 — Module Progress

## Objetivo

Implementar el tracking de progreso por modulo/nivel para cada usuario.
La tabla `module_progress` ya existe en la BD pero no tiene codigo Java asociado.

## Estado actual

- Tabla `module_progress` creada en `V2__create_user_profiles.sql`
- **Cero codigo**: no hay aggregate, repository, use cases ni endpoints
- Frontend almacena esto como `moduleProgress['listening-a1'] = { currentUnit: 3, completedUnits: [0,1,2], scores: {0: 85} }`

## Entidades y Value Objects

### Nuevo Aggregate: `ModuleProgress`

```
ModuleProgress (Aggregate Root)
├── ModuleProgressId (Value Object — UUID)
├── UserProfileId (referencia al usuario)
├── ModuleName (Value Object — enum: listening, vocabulary, grammar, phrases, pronunciation)
├── VocabLevel (Value Object — reutilizar o crear ModuleLevel)
├── currentUnit: int
├── completedUnits: List<Integer>
└── scores: Map<Integer, Integer> (unitIndex → score)
```

### Metodos de dominio

- `create(userId, moduleName, level)` — factory con currentUnit=0
- `completeUnit(unitIndex, score)` — anade a completedUnits, guarda score, avanza currentUnit
- `isLevelComplete()` — true si todas las units del nivel estan completadas
- `getAverageScore()` — media de scores

## Migraciones

No se necesitan. La tabla ya existe:

```sql
-- Ya creada en V2
module_progress (
    id UUID PK,
    user_id UUID FK → user_profiles,
    module_name VARCHAR(50),
    level VARCHAR(10),
    current_unit INT DEFAULT 0,
    completed_units TEXT,  -- JSON array
    scores TEXT,           -- JSON object
    UNIQUE(user_id, module_name, level)
)
```

## Use Cases

| Use Case | Tipo | Descripcion |
|----------|------|-------------|
| `GetModuleProgressUseCase` | Query | Obtener progreso de un modulo/nivel para un usuario |
| `GetAllModuleProgressUseCase` | Query | Obtener todo el progreso de un usuario |
| `InitModuleProgressUseCase` | Command | Crear registro inicial cuando usuario empieza un modulo/nivel |
| `CompleteUnitUseCase` | Command | Marcar unidad como completada con score |
| `CheckLevelUpUseCase` | Query | Verificar si el usuario puede subir de nivel en un modulo |

## Endpoints

| Metodo | Ruta | Use Case | Request Body | Response |
|--------|------|----------|-------------|----------|
| GET | `/api/profiles/{uid}/modules` | GetAllModuleProgressUseCase | - | List<ModuleProgressResponse> |
| GET | `/api/profiles/{uid}/modules/{module}/levels/{level}` | GetModuleProgressUseCase | - | ModuleProgressResponse |
| POST | `/api/profiles/{uid}/modules/{module}/levels/{level}` | InitModuleProgressUseCase | - | 201 ModuleProgressResponse |
| PUT | `/api/profiles/{uid}/modules/{module}/levels/{level}/units/{unit}` | CompleteUnitUseCase | `{ "score": 85 }` | ModuleProgressResponse |
| GET | `/api/profiles/{uid}/modules/{module}/levels/{level}/level-up` | CheckLevelUpUseCase | - | `{ "eligible": true, "nextLevel": "b1" }` |

## Criterios de aceptacion

- [ ] Aggregate `ModuleProgress` con todos los value objects
- [ ] Persistencia: adapter JPA que serializa/deserializa completedUnits (JSON) y scores (JSON)
- [ ] UNIQUE constraint respetado: solo un registro por (user, module, level)
- [ ] `completeUnit` es idempotente (completar la misma unit dos veces no duplica)
- [ ] 404 si el usuario o el progreso no existe
- [ ] Tests unitarios para logica de dominio (completeUnit, isLevelComplete, getAverageScore)
- [ ] Tests de integracion para endpoints
