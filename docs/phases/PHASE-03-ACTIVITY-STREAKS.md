# Fase 3 — Activity Tracking y Streaks

## Objetivo

Implementar el registro de actividad diaria y calculo de rachas (streaks).
La tabla `activity_dates` ya existe pero no tiene codigo Java.

## Estado actual

- Tabla `activity_dates` creada en `V2__create_user_profiles.sql`
- **Cero codigo**: no hay aggregate, repository, use cases ni endpoints
- Frontend no implementa streaks de forma robusta (lo calcula en gamification.js)

## Entidades y Value Objects

### Nuevo Aggregate: `ActivityDate`

```
ActivityDate (Aggregate Root)
├── ActivityDateId (Value Object — UUID)
├── UserProfileId (referencia al usuario)
└── activityDate: LocalDate
```

### Value Object para respuesta de racha

```
StreakInfo (Value Object — solo para respuesta, no persiste)
├── currentStreak: int
├── bestStreak: int
└── lastActiveDate: LocalDate
```

### Logica de dominio (en servicio o aggregate)

- `calculateStreak(List<LocalDate> dates)` — recorre fechas ordenadas descendentes,
  cuenta dias consecutivos desde hoy
- `calculateBestStreak(List<LocalDate> dates)` — recorre todas las fechas y encuentra
  la racha mas larga

## Migraciones

No se necesitan. La tabla ya existe:

```sql
-- Ya creada en V2
activity_dates (
    id UUID PK,
    user_id UUID FK → user_profiles,
    activity_date DATE,
    UNIQUE(user_id, activity_date)
)
```

## Use Cases

| Use Case | Tipo | Descripcion |
|----------|------|-------------|
| `RecordActivityUseCase` | Command | Registrar actividad para hoy (idempotente) |
| `GetStreakUseCase` | Query | Calcular racha actual y mejor racha |
| `GetActivityDatesUseCase` | Query | Obtener todas las fechas de actividad de un usuario |
| `GetActivityCalendarUseCase` | Query | Obtener fechas de actividad de un mes especifico |

## Endpoints

| Metodo | Ruta | Use Case | Request Body | Response |
|--------|------|----------|-------------|----------|
| POST | `/api/profiles/{uid}/activity` | RecordActivityUseCase | `{ "date": "2026-03-20" }` (opcional, default hoy) | 201 o 200 si ya existe |
| GET | `/api/profiles/{uid}/streak` | GetStreakUseCase | - | `{ "current": 5, "best": 12, "lastActive": "2026-03-20" }` |
| GET | `/api/profiles/{uid}/activity` | GetActivityDatesUseCase | - | List<LocalDate> |
| GET | `/api/profiles/{uid}/activity?month=2026-03` | GetActivityCalendarUseCase | - | List<LocalDate> filtrada |

## Criterios de aceptacion

- [ ] `RecordActivityUseCase` es idempotente: registrar dos veces el mismo dia no falla ni duplica
- [ ] Calculo de streak correcto: dias consecutivos hasta hoy (si hoy no tiene actividad, empieza desde ayer)
- [ ] Best streak recorre todo el historial
- [ ] 404 si el usuario no existe
- [ ] Tests unitarios para `calculateStreak` y `calculateBestStreak` con edge cases:
  - Sin actividad → streak 0
  - Solo hoy → streak 1
  - Hoy y ayer → streak 2
  - Gap de un dia → streak se rompe
- [ ] Tests de integracion para endpoints
