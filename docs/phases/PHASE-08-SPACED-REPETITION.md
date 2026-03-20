# Fase 8 — Spaced Repetition

## Objetivo

Implementar el sistema de repeticion espaciada para repasar unidades completadas
con intervalos crecientes (1 → 3 → 7 → 14 → 30 dias).

## Estado actual

- **Cero codigo** para repeticion espaciada
- Frontend lo implementa en `state.js` como parte del perfil en localStorage

## Datos del frontend

### Algoritmo (state.js)

```
Intervalos: [1, 3, 7, 14, 30] dias
Graduacion: despues de 5 reviews exitosos, el item se "gradua" y no aparece mas

Estructura por item:
{
    unitId: "listening-a1-3",
    nextReview: timestamp,
    interval: 1,        // indice en array de intervalos
    reviews: 0           // contador de reviews completados
}
```

Cuando se completa una unidad → se agrega al queue de repeticion con nextReview = hoy + 1 dia.
Cuando se repasa un item → reviews++, interval avanza al siguiente, nextReview = hoy + intervalos[interval].
Despues de 5 reviews → item graduado, se elimina del queue.

## Entidades y Value Objects

### Nuevo Aggregate: `SpacedRepetitionItem`

```
SpacedRepetitionItem (Aggregate Root)
├── SpacedRepetitionItemId (Value Object — UUID)
├── UserProfileId
├── unitReference: String (ej: "listening-a1-3")
├── moduleName: String
├── level: String
├── unitIndex: int
├── nextReviewDate: LocalDate
├── intervalIndex: int (0-4, indice en [1,3,7,14,30])
├── reviewCount: int
├── graduated: boolean
└── createdAt: Instant
```

### Metodos de dominio

- `create(userId, moduleName, level, unitIndex)` — factory, nextReview = hoy + 1
- `completeReview()` — incrementa reviewCount, avanza intervalIndex, calcula nextReviewDate
- `isGraduated()` — true si reviewCount >= 5
- `isDueToday()` — true si nextReviewDate <= hoy

## Migraciones

### V7__create_spaced_repetition.sql

```sql
CREATE TABLE spaced_repetition_items (
    id                UUID PRIMARY KEY,
    user_id           UUID NOT NULL REFERENCES user_profiles(id) ON DELETE CASCADE,
    unit_reference    VARCHAR(100) NOT NULL,
    module_name       VARCHAR(50) NOT NULL,
    level             VARCHAR(10) NOT NULL,
    unit_index        INT NOT NULL,
    next_review_date  DATE NOT NULL,
    interval_index    INT NOT NULL DEFAULT 0,
    review_count      INT NOT NULL DEFAULT 0,
    graduated         BOOLEAN NOT NULL DEFAULT FALSE,
    created_at        TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(user_id, unit_reference)
);

CREATE INDEX idx_sr_user_due ON spaced_repetition_items(user_id, next_review_date)
    WHERE graduated = FALSE;
```

## Use Cases

| Use Case | Tipo | Descripcion |
|----------|------|-------------|
| `AddToReviewQueueUseCase` | Command | Anadir unidad completada al queue de repeticion |
| `GetDueReviewsUseCase` | Query | Obtener items pendientes de repaso (nextReviewDate <= hoy, no graduados) |
| `CompleteReviewUseCase` | Command | Marcar item como repasado, avanzar intervalo |
| `GetReviewStatsUseCase` | Query | Estadisticas: total items, pendientes hoy, graduados |

## Endpoints

| Metodo | Ruta | Use Case | Request/Response |
|--------|------|----------|-----------------|
| POST | `/api/profiles/{uid}/reviews` | AddToReviewQueueUseCase | Request: `{ "moduleName": "listening", "level": "a1", "unitIndex": 3 }` Response: 201 |
| GET | `/api/profiles/{uid}/reviews/due` | GetDueReviewsUseCase | List<SpacedRepetitionItemResponse> |
| PUT | `/api/profiles/{uid}/reviews/{itemId}/complete` | CompleteReviewUseCase | SpacedRepetitionItemResponse |
| GET | `/api/profiles/{uid}/reviews/stats` | GetReviewStatsUseCase | `{ "total": 15, "dueToday": 3, "graduated": 8 }` |

## Criterios de aceptacion

- [ ] Intervalos correctos: 1, 3, 7, 14, 30 dias
- [ ] Item se gradua automaticamente al llegar a 5 reviews
- [ ] Items graduados no aparecen en `GetDueReviews`
- [ ] `AddToReviewQueue` es idempotente (misma unidad no se duplica)
- [ ] `CompleteReview` calcula correctamente la siguiente fecha
- [ ] Tests unitarios para:
  - Progresion de intervalos
  - Graduacion despues de 5 reviews
  - Calculo de nextReviewDate
- [ ] Tests de integracion para endpoints
