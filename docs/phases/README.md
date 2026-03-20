# Plan de Implementacion — English Trainer API

## Resumen

10 fases para llevar el API desde su estado actual (2 modulos parciales, 3 endpoints)
hasta un backend completo que reemplace el localStorage del PWA frontend.

## Estado actual del API

- **Modulos**: user (parcial), vocabulary (parcial), shared (base)
- **Endpoints**: POST /api/profiles, GET /api/profiles/{id}, GET /api/vocab, GET /api/vocab/level/{level}
- **Tablas huerfanas**: `module_progress`, `activity_dates` (creadas en Flyway, sin codigo Java)
- **Sin**: autenticacion, gamificacion, sesiones, tests, repeticion espaciada, curriculum

## Fases

| Fase | Nombre | Modulos/Entidades | Endpoints nuevos | Dependencias |
|------|--------|-------------------|------------------|--------------|
| 1 | [User Profile completo](./PHASE-01-USER-PROFILE.md) | UserProfile (CRUD completo) | 5 | - |
| 2 | [Module Progress](./PHASE-02-MODULE-PROGRESS.md) | ModuleProgress (nuevo aggregate) | 5 | Fase 1 |
| 3 | [Activity Tracking y Streaks](./PHASE-03-ACTIVITY-STREAKS.md) | ActivityDate (nuevo aggregate) | 4 | Fase 1 |
| 4 | [Gamificacion](./PHASE-04-GAMIFICATION.md) | Achievement, GamificationProfile | 5 | Fases 1, 3 |
| 5 | [Vocabulario completo y Seed](./PHASE-05-VOCABULARY.md) | VocabEntry (ampliar), Phrase (nuevo) | 5 | - |
| 6 | [Assessment (Tests)](./PHASE-06-ASSESSMENT.md) | LevelTestResult, MiniTestResult | 6 | Fases 1, 2 |
| 7 | [Session Management](./PHASE-07-SESSIONS.md) | Session (nuevo aggregate) | 5 | Fases 1, 2, 5 |
| 8 | [Spaced Repetition](./PHASE-08-SPACED-REPETITION.md) | SpacedRepetitionItem | 4 | Fases 1, 2 |
| 9 | [Autenticacion y Autorizacion](./PHASE-09-AUTH.md) | User (nuevo), JWT | 4 | Fase 1 |
| 10 | [Curriculum y Contenido](./PHASE-10-CURRICULUM.md) | CurriculumPlan, Module, Unit | 4 | Fase 2 |

## Orden recomendado

```
Fase 1 (User Profile) ──┬── Fase 2 (Module Progress) ──┬── Fase 6 (Assessment)
                         │                              ├── Fase 8 (Spaced Repetition)
                         │                              └── Fase 7 (Sessions) ← Fase 5
                         ├── Fase 3 (Activity) ── Fase 4 (Gamificacion)
                         └── Fase 9 (Auth)

Fase 5 (Vocabulary) ── independiente, puede ir en paralelo
Fase 10 (Curriculum) ── depende de Fase 2
```

## Convencion por fase

Cada documento sigue esta estructura:
1. **Objetivo** — que resuelve
2. **Estado actual** — que existe hoy
3. **Entidades y Value Objects** — modelo de dominio
4. **Migraciones** — nuevas tablas o columnas
5. **Use Cases** — operaciones de aplicacion
6. **Endpoints** — API REST
7. **Criterios de aceptacion** — cuando esta "hecho"
