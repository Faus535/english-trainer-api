# Fase 7 — Session Management

## Objetivo

Implementar la generacion y registro de sesiones de aprendizaje, incluyendo
sesiones normales (3 modos) y sesiones integradoras (cada 5ta sesion).

## Estado actual

- **Cero codigo** para sesiones
- Frontend tiene logica completa en `session.js` (1036 lineas) e `integrator-sessions.js`
- Frontend genera sesiones con bloques: warmup, listening, secondary module, practice

## Datos del frontend

### Modos de sesion (session.js)

| Modo | Duracion | Bloques |
|------|----------|---------|
| short | ~14 min | warmup (2min) + listening (5min) + secondary (5min) + practice (2min) |
| full | ~21 min | warmup (3min) + listening (7min) + secondary (7min) + practice (4min) |
| extended | ~31 min | warmup (4min) + listening (10min) + secondary (10min) + practice (7min) |

### Bloques

- **Warmup**: items de repaso (spaced repetition) + sound of the day
- **Listening**: unidad actual del modulo listening
- **Secondary**: rota entre vocabulary, grammar, phrases, pronunciation
- **Practice**: flashcards + speak quiz

### Sesiones integradoras (integrator-sessions.js)

Cada 5ta sesion es tematica (restaurante, aeropuerto, reunion de trabajo, etc.)
y combina ejercicios de multiples modulos bajo un mismo tema.

## Entidades y Value Objects

### Nuevo Aggregate: `Session`

```
Session (Aggregate Root)
├── SessionId (Value Object — UUID)
├── UserProfileId
├── mode: SessionMode (Value Object — short/full/extended)
├── type: SessionType (Value Object — normal/integrator)
├── blocks: List<SessionBlock>
├── listeningModule: String (modulo principal)
├── secondaryModule: String (modulo secundario)
├── integratorTheme: String (nullable, solo para integradoras)
├── completed: boolean
├── startedAt: Instant
├── completedAt: Instant (nullable)
└── durationMinutes: int (nullable)
```

### Value Object: `SessionBlock`

```
SessionBlock (Value Object)
├── type: String (warmup/listening/secondary/practice)
├── moduleName: String
├── unitIndex: int
├── durationMinutes: int
└── items: List<SessionBlockItem>
```

## Migraciones

### V6__create_sessions.sql

```sql
CREATE TABLE sessions (
    id                UUID PRIMARY KEY,
    user_id           UUID NOT NULL REFERENCES user_profiles(id) ON DELETE CASCADE,
    mode              VARCHAR(20) NOT NULL,    -- short, full, extended
    type              VARCHAR(20) NOT NULL,    -- normal, integrator
    listening_module  VARCHAR(50),
    secondary_module  VARCHAR(50),
    integrator_theme  VARCHAR(100),
    completed         BOOLEAN NOT NULL DEFAULT FALSE,
    started_at        TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    completed_at      TIMESTAMP,
    duration_minutes  INT,
    blocks_data       TEXT                     -- JSON con estructura de bloques
);

CREATE INDEX idx_sessions_user ON sessions(user_id);
CREATE INDEX idx_sessions_user_date ON sessions(user_id, started_at);
```

## Use Cases

| Use Case | Tipo | Descripcion |
|----------|------|-------------|
| `GenerateSessionUseCase` | Command | Genera una sesion segun modo, progreso del usuario y rotacion de modulos |
| `CompleteSessionUseCase` | Command | Marca sesion como completada, registra duracion, otorga XP, registra actividad |
| `GetSessionHistoryUseCase` | Query | Obtener historial de sesiones de un usuario |
| `GetCurrentSessionUseCase` | Query | Obtener sesion activa (no completada) si existe |
| `GenerateIntegratorSessionUseCase` | Command | Genera sesion integradora tematica |

## Endpoints

| Metodo | Ruta | Use Case | Request/Response |
|--------|------|----------|-----------------|
| POST | `/api/profiles/{uid}/sessions/generate` | GenerateSessionUseCase | Request: `{ "mode": "full" }` Response: SessionResponse |
| PUT | `/api/profiles/{uid}/sessions/{sid}/complete` | CompleteSessionUseCase | Request: `{ "durationMinutes": 22 }` Response: SessionResponse |
| GET | `/api/profiles/{uid}/sessions` | GetSessionHistoryUseCase | List<SessionSummaryResponse> |
| GET | `/api/profiles/{uid}/sessions/current` | GetCurrentSessionUseCase | SessionResponse o 404 |
| POST | `/api/profiles/{uid}/sessions/integrator` | GenerateIntegratorSessionUseCase | Request: `{ "level": "b1" }` Response: SessionResponse |

## Logica de generacion

1. Consultar `sessionCount` del usuario → si es multiplo de 5, generar integradora
2. Obtener progreso de listening → determinar unidad actual
3. Rotar modulo secundario (vocabulary → grammar → phrases → pronunciation → ...)
4. Consultar spaced repetition → items de warmup
5. Ensamblar bloques segun modo (short/full/extended)

## Criterios de aceptacion

- [ ] Generacion respeta el modo (short/full/extended) con duraciones correctas
- [ ] Cada 5ta sesion es integradora automaticamente
- [ ] Rotacion de modulo secundario funciona correctamente
- [ ] `CompleteSession` otorga XP (50 base + bonus streak), registra actividad, incrementa sessionCount
- [ ] Warmup incluye items pendientes de spaced repetition
- [ ] No se puede generar nueva sesion si hay una activa sin completar
- [ ] Tests unitarios para logica de generacion y rotacion
- [ ] Tests de integracion para flujo completo
