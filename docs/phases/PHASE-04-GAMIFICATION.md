# Fase 4 — Gamificacion

## Objetivo

Implementar el sistema de XP, niveles y logros (achievements) que el frontend
tiene en `gamification.js`. Centralizar la logica en el backend para consistencia
y anti-trampas.

## Estado actual

- `UserProfile` ya tiene campo `xp` y use case `AddXpUseCase` (Fase 1)
- Frontend define 8 niveles de XP y 23 logros
- **Cero codigo backend** para niveles o logros

## Datos del frontend a replicar

### Niveles XP (de gamification.js)

| Nivel | Nombre | XP minimo |
|-------|--------|-----------|
| 0 | Beginner | 0 |
| 1 | Elementary | 200 |
| 2 | Pre-Intermediate | 600 |
| 3 | Intermediate | 1200 |
| 4 | Upper-Intermediate | 2200 |
| 5 | Advanced | 3500 |
| 6 | Proficient | 5000 |
| 7 | Master | 7000 |

### XP por accion

| Accion | XP |
|--------|-----|
| Completar sesion | 50 |
| Flashcard estudiada | 5 |
| Subir de nivel en modulo | 200 |
| Bonus por racha (3+ dias) | 20 extra |
| Completar mini-test | 100 |

### Logros (23 definidos en frontend)

Ejemplos: first_session, week_active, sessions_10, sessions_25, streak_3, streak_7,
flashcards_50, module_levelup, global_b1, global_b2, review_session, etc.

## Entidades y Value Objects

### Value Object: `XpLevel`

```
XpLevel (Value Object — calculado, no persiste)
├── level: int (0-7)
├── name: String
├── currentXp: int
├── xpForNextLevel: int
└── progress: double (0.0 - 1.0)
```

### Nuevo Aggregate: `Achievement`

```
Achievement (Aggregate Root — definicion)
├── AchievementId (Value Object — String key como "first_session")
├── name: String
├── description: String
├── icon: String
└── condition: String (descripcion legible)
```

### Nuevo Aggregate: `UserAchievement`

```
UserAchievement (Entity)
├── UserAchievementId (Value Object — UUID)
├── UserProfileId
├── AchievementId
└── unlockedAt: Instant
```

## Migraciones

### V3__create_achievements.sql

```sql
CREATE TABLE achievements (
    id          VARCHAR(50) PRIMARY KEY,
    name        VARCHAR(100) NOT NULL,
    description VARCHAR(255) NOT NULL,
    icon        VARCHAR(50),
    condition   VARCHAR(255)
);

CREATE TABLE user_achievements (
    id              UUID PRIMARY KEY,
    user_id         UUID NOT NULL REFERENCES user_profiles(id) ON DELETE CASCADE,
    achievement_id  VARCHAR(50) NOT NULL REFERENCES achievements(id),
    unlocked_at     TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(user_id, achievement_id)
);
```

### V3_1__seed_achievements.sql

Insertar las 23 definiciones de logros.

## Use Cases

| Use Case | Tipo | Descripcion |
|----------|------|-------------|
| `GetXpLevelUseCase` | Query | Calcular nivel actual a partir del XP del perfil |
| `GetAllAchievementsUseCase` | Query | Listar todas las definiciones de logros |
| `GetUserAchievementsUseCase` | Query | Listar logros desbloqueados de un usuario |
| `CheckAndUnlockAchievementsUseCase` | Command | Evaluar condiciones y desbloquear logros pendientes |
| `GrantXpUseCase` | Command | Otorgar XP por una accion especifica (con bonus de streak) |

## Endpoints

| Metodo | Ruta | Use Case | Response |
|--------|------|----------|----------|
| GET | `/api/profiles/{uid}/xp-level` | GetXpLevelUseCase | XpLevelResponse |
| GET | `/api/achievements` | GetAllAchievementsUseCase | List<AchievementResponse> |
| GET | `/api/profiles/{uid}/achievements` | GetUserAchievementsUseCase | List<UserAchievementResponse> |
| POST | `/api/profiles/{uid}/achievements/check` | CheckAndUnlockAchievementsUseCase | List<AchievementResponse> (nuevos desbloqueados) |
| POST | `/api/profiles/{uid}/xp` | GrantXpUseCase | `{ "xpGranted": 70, "totalXp": 1270, "newAchievements": [...] }` |

## Criterios de aceptacion

- [ ] Calculo de nivel correcto segun tabla de XP
- [ ] `GrantXpUseCase` aplica bonus de streak automaticamente (consulta ActivityDate)
- [ ] `CheckAndUnlockAchievementsUseCase` evalua todas las condiciones:
  - Condiciones basadas en sessionCount, streak, flashcardCount, moduleProgress, xp
- [ ] Logro no se desbloquea dos veces (UNIQUE constraint)
- [ ] Seed de achievements en migracion Flyway
- [ ] Tests unitarios para calculo de nivel y evaluacion de condiciones
- [ ] Tests de integracion para endpoints
