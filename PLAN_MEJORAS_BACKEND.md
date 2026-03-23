# Plan de Mejoras — Backend (english-trainer-api)

## Contexto

Este plan acompana al `PLAN_MEJORAS.md` del frontend. Define los cambios necesarios en el backend para soportar las 5 fases de mejora del English Trainer.

### Infraestructura existente que se reutiliza

| Modulo | Estado | Uso |
|--------|--------|-----|
| `spacedrepetition` | Completo | SRS con intervalos [1,3,7,14,30]. Tiene: add, getDue, complete, stats |
| `vocabulary` | Completo | CRUD vocab_entries (en, ipa, es, type, example, level) |
| `pronunciation` | Parcial | Solo errores (word, expectedPhoneme, spokenPhoneme). Falta contenido fonetico |
| `moduleprogress` | Completo | Tracking de unidades completadas por modulo/nivel/usuario |
| `session` | Completo | Generacion y completado de sesiones |
| `activity` | Completo | Streaks, actividad por fecha |
| `gamification` | Completo | XP, achievements |

---

## FASE 3 — Ampliar Vocabulario: Migracion BD + Endpoint

### 3.1 Migracion Flyway: agregar `category` y `block` a `vocab_entries`

```sql
-- V39__add_category_block_to_vocab_entries.sql

ALTER TABLE vocab_entries ADD COLUMN category VARCHAR(50);
ALTER TABLE vocab_entries ADD COLUMN block INTEGER;
ALTER TABLE vocab_entries ADD COLUMN block_title VARCHAR(100);

-- Indice para consultas por nivel + bloque
CREATE INDEX idx_vocab_entries_level_block ON vocab_entries(level, block);
```

### 3.2 Actualizar dominio `VocabEntry`

```java
// vocabulary/domain/VocabEntry.java — agregar campos
public final class VocabEntry extends AggregateRoot<VocabEntryId> {
    // ... campos existentes ...
    private final String category;    // NUEVO: 'people', 'body', 'food'
    private final Integer block;      // NUEVO: 1-10
    private final String blockTitle;  // NUEVO: 'Yo y mi mundo'
}
```

### 3.3 Nuevo Use Case: `GetVocabByLevelAndBlockUseCase`

```java
// vocabulary/application/GetVocabByLevelAndBlockUseCase.java
@Service
public final class GetVocabByLevelAndBlockUseCase {
    private final VocabRepository repository;

    public GetVocabByLevelAndBlockUseCase(VocabRepository repository) {
        this.repository = repository;
    }

    public List<VocabEntry> execute(VocabLevel level, int block) {
        return repository.findByLevelAndBlock(level, block);
    }
}
```

### 3.4 Ampliar `VocabRepository`

```java
// vocabulary/domain/VocabRepository.java — agregar metodo
List<VocabEntry> findByLevelAndBlock(VocabLevel level, int block);
```

### 3.5 Nuevo Controller: `GetVocabByLevelAndBlockController`

```java
// vocabulary/infrastructure/controller/GetVocabByLevelAndBlockController.java
@RestController
class GetVocabByLevelAndBlockController {
    private final GetVocabByLevelAndBlockUseCase useCase;

    // GET /api/vocab/level/{level}?block={block}
    @GetMapping("/api/vocab/level/{level}")
    ResponseEntity<List<VocabEntryResponse>> handle(
        @PathVariable String level,
        @RequestParam(required = false) Integer block
    ) {
        // Si block != null → filtrar por bloque
        // Si block == null → comportamiento actual (todas del nivel)
    }
}
```

**Nota**: Este endpoint ya existe como `GetVocabByLevelController`. Se MODIFICA para aceptar el parametro opcional `block` sin romper el contrato actual.

### 3.6 Respuesta ampliada

```java
// vocabulary/infrastructure/controller/VocabEntryResponse.java
record VocabEntryResponse(
    String id,
    String en,
    String ipa,
    String es,
    String type,
    String example,
    String level,
    String category,    // NUEVO
    Integer block,      // NUEVO
    String blockTitle   // NUEVO
) {}
```

### 3.7 Seed de datos: insertar vocabulario por bloques

```sql
-- V40__seed_a1_vocabulary_blocks.sql

-- Bloque 1: Yo y mi mundo (20 palabras)
INSERT INTO vocab_entries (id, en, ipa, es, type, example, level, category, block, block_title) VALUES
(gen_random_uuid(), 'person', '/ˈpɜːrsn/', 'persona', 'noun', 'That person is very tall.', 'a1', 'people', 1, 'Yo y mi mundo'),
(gen_random_uuid(), 'man', '/mæn/', 'hombre', 'noun', 'The man is reading a book.', 'a1', 'people', 1, 'Yo y mi mundo'),
(gen_random_uuid(), 'woman', '/ˈwʊmən/', 'mujer', 'noun', 'The woman works in a hospital.', 'a1', 'people', 1, 'Yo y mi mundo'),
-- ... 17 palabras mas del bloque 1 ...

-- Bloque 2: Mi casa (20 palabras)
-- Bloque 3: Comida y bebida (20 palabras)
-- ... hasta Bloque 10 ...

-- Total: 200 palabras A1
```

### Bloques A1 (referencia para el seed)

| Bloque | Tema | Categorias |
|--------|------|-----------|
| 1 | Yo y mi mundo | people, body, emotions, adjectives |
| 2 | Mi casa | rooms, furniture, household |
| 3 | Comida y bebida | food, drinks, meals |
| 4 | Rutina diaria | time, actions, frequency |
| 5 | La ciudad | places, transport, directions |
| 6 | Compras y dinero | shops, prices, clothes |
| 7 | Tiempo libre | hobbies, sports, weekend |
| 8 | Trabajo y escuela | jobs, classroom, office |
| 9 | Clima y naturaleza | weather, seasons, animals |
| 10 | Comunicacion | phone, internet, social |

Palabras totales por nivel: A1=200, A2=300, B1=400, B2=300, C1=200 → **1400 total**.

---

## FASE 4 — SRS: Alinear API Frontend/Backend

### Problema actual

El frontend `ReviewApiService` envia:
```typescript
addToReviewQueue(profileId, itemType: string, itemId: string)
// Body: { itemType, itemId }
```

El backend `AddToReviewQueueController` espera:
```java
record AddReviewRequest(@NotBlank String moduleName, @NotBlank String level, @NotNull @Min(0) Integer unitIndex) {}
```

**Estan desalineados.** Hay que decidir un contrato unico.

### Solucion: unificar con contrato flexible

El SRS maneja dos tipos de items:
1. **Unidades de modulo**: `moduleName + level + unitIndex` (ej: pronunciation-a1-2)
2. **Palabras individuales**: `word + level` (ej: vocab-think)

### 4.1 Nuevo request unificado

```java
// spacedrepetition/infrastructure/controller/AddToReviewQueueController.java
record AddReviewRequest(
    @NotBlank String itemType,     // 'module-unit' | 'vocabulary-word'
    String moduleName,             // requerido si itemType = 'module-unit'
    String level,                  // requerido siempre
    Integer unitIndex,             // requerido si itemType = 'module-unit'
    String word                    // requerido si itemType = 'vocabulary-word'
) {}
```

### 4.2 Logica en el Use Case

```java
// spacedrepetition/application/AddToReviewQueueUseCase.java
public SpacedRepetitionItem execute(UserProfileId userId, String itemType,
                                     String moduleName, String level,
                                     int unitIndex, String word) {
    return switch (itemType) {
        case "module-unit" -> SpacedRepetitionItem.create(userId, moduleName, level, unitIndex);
        case "vocabulary-word" -> SpacedRepetitionItem.createForVocabulary(userId, word, level);
        default -> throw new IllegalArgumentException("Unknown itemType: " + itemType);
    };
}
```

**Nota**: `createForVocabulary` ya existe en `SpacedRepetitionItem.java`.

### 4.3 Frontend alineado

```typescript
// review-api.service.ts — metodos actualizados

addUnitToReview(profileId: string, moduleName: string, level: string, unitIndex: number): Observable<SpacedRepetitionItemResponse> {
  return this.http.post<SpacedRepetitionItemResponse>(
    `${this.baseUrl}/${profileId}/reviews`,
    { itemType: 'module-unit', moduleName, level, unitIndex }
  );
}

addWordToReview(profileId: string, word: string, level: string): Observable<SpacedRepetitionItemResponse> {
  return this.http.post<SpacedRepetitionItemResponse>(
    `${this.baseUrl}/${profileId}/reviews`,
    { itemType: 'vocabulary-word', word, level }
  );
}
```

### 4.4 SpacedRepetitionItemResponse ampliado

Agregar `itemType` a la respuesta para que el frontend sepa que tipo de repaso es:

```java
record SpacedRepetitionItemResponse(
    String id,
    String userId,
    String itemType,        // NUEVO: 'module-unit' | 'vocabulary-word'
    String unitReference,
    String moduleName,
    String level,
    int unitIndex,
    String nextReviewDate,
    int intervalIndex,
    int reviewCount,
    boolean graduated
) {}
```

Frontend:
```typescript
export interface SpacedRepetitionItemResponse {
  id: string;
  userId: string;
  itemType: string;        // NUEVO
  unitReference: string;
  moduleName: string;
  level: string;
  unitIndex: number;
  nextReviewDate: string;
  intervalIndex: number;
  reviewCount: number;
  graduated: boolean;
}
```

### 4.5 Flujo completo SRS

```
USUARIO COMPLETA VOCABULARIO (10 palabras)
         │
         ▼
Frontend: POST /api/profiles/{id}/reviews (x10)
  Body: { itemType: 'vocabulary-word', word: 'house', level: 'a1' }
         │
         ▼
Backend: SpacedRepetitionItem.createForVocabulary()
  → unitReference: 'vocab-house'
  → nextReviewDate: manana
  → intervalIndex: 0
         │
         ▼
DIA SIGUIENTE — WARMUP
         │
Frontend: GET /api/profiles/{id}/reviews/due
  → [{ unitReference: 'vocab-house', moduleName: 'vocabulary-word', ... }]
         │
         ▼
Frontend muestra flashcard/quiz con la palabra
  → Acierta: PUT /reviews/{id}/complete → { quality: 5 }
  → Falla:   PUT /reviews/{id}/complete → { quality: 1 }
         │
         ▼
Backend recalcula:
  Acierto → intervalIndex++ → siguiente repaso en 3 dias
  Fallo   → intervalIndex=0 → repaso manana otra vez
```

---

## FASE 5 — Feedback: Registrar Errores Foneticos

### 5.1 El modulo `pronunciation` ya existe

Endpoints actuales:
- `POST /api/profiles/{userId}/pronunciation/errors` — Registrar error
- `GET /api/profiles/{userId}/pronunciation/errors` — Obtener errores frecuentes

### 5.2 Contrato para registrar error desde dictado

Cuando el usuario escribe "tink" en vez de "think" en el dictado:

```
POST /api/profiles/{userId}/pronunciation/errors
{
  "word": "think",
  "expectedPhoneme": "/θ/",
  "spokenPhoneme": "/t/"
}
```

El backend ya soporta esto con `RecordPronunciationErrorController`. Si el error ya existe para este usuario+palabra, incrementa el `occurrenceCount`.

### 5.3 Nuevo endpoint: sonidos problematicos del usuario

Para personalizar el warmup con sonidos que el usuario mas falla:

```java
// pronunciation/infrastructure/controller/GetProblematicSoundsController.java
// GET /api/profiles/{userId}/pronunciation/problematic-sounds

@GetMapping("/api/profiles/{userId}/pronunciation/problematic-sounds")
ResponseEntity<List<ProblematicSoundResponse>> handle(@PathVariable String userId) {
    // Agrupa errores por expectedPhoneme, ordena por count desc
}
```

```java
record ProblematicSoundResponse(
    String phoneme,          // '/θ/'
    int errorCount,          // 15
    List<String> topWords,   // ['think', 'three', 'birthday']
    String lastOccurred      // '2026-03-20'
) {}
```

Frontend:
```typescript
export interface ProblematicSoundResponse {
  phoneme: string;
  errorCount: number;
  topWords: string[];
  lastOccurred: string;
}
```

### 5.4 Personalizar sesiones con errores del usuario

El frontend puede usar estos datos para:
1. **Warmup**: Priorizar sonidos problematicos
2. **Dictado**: Elegir frases que contengan el sonido mas fallado
3. **Pronunciacion**: Sugerir repasar la unidad del sonido problematico

---

## Contrato API Completo — Frontend ↔ Backend

### Endpoints NUEVOS

| Metodo | Endpoint | Request | Response | Fase |
|--------|----------|---------|----------|------|
| GET | `/api/vocab/level/{level}?block={n}` | query param opcional | `VocabEntryResponse[]` (con category, block, blockTitle) | 3 |
| GET | `/api/profiles/{userId}/pronunciation/problematic-sounds` | — | `ProblematicSoundResponse[]` | 5 |

### Endpoints MODIFICADOS

| Metodo | Endpoint | Cambio | Fase |
|--------|----------|--------|------|
| GET | `/api/vocab/level/{level}` | Response ampliado con `category`, `block`, `blockTitle` | 3 |
| POST | `/api/profiles/{userId}/reviews` | Request unificado con `itemType` | 4 |
| GET | `/api/profiles/{userId}/reviews/due` | Response ampliado con `itemType` | 4 |

### Endpoints SIN CAMBIOS (se reutilizan tal cual)

| Metodo | Endpoint | Uso |
|--------|----------|-----|
| PUT | `/api/profiles/{userId}/reviews/{itemId}/complete` | Completar repaso SRS |
| GET | `/api/profiles/{userId}/reviews/stats` | Stats para dashboard |
| POST | `/api/profiles/{userId}/pronunciation/errors` | Registrar error fonetico |
| GET | `/api/profiles/{userId}/pronunciation/errors` | Errores frecuentes |
| GET | `/api/profiles/{userId}/modules` | Progreso por modulo |
| PUT | `/api/profiles/{userId}/modules/{m}/levels/{l}/units/{u}` | Completar unidad |
| GET | `/api/profiles/{userId}/streak` | Racha actual |
| GET | `/api/profiles/{userId}/xp-level` | Nivel XP |
| GET | `/api/achievements` | Logros disponibles |

---

## Migraciones Flyway necesarias

| # | Archivo | Que hace | Fase |
|---|---------|----------|------|
| V39 | `V39__add_category_block_to_vocab_entries.sql` | Agrega category, block, block_title a vocab_entries | 3 |
| V40 | `V40__seed_a1_vocabulary_blocks.sql` | 200 palabras A1 en 10 bloques | 3 |
| V41 | `V41__seed_a2_vocabulary_blocks.sql` | 300 palabras A2 en 15 bloques | 3 |
| V42 | `V42__add_item_type_to_spaced_repetition.sql` | Agrega item_type a spaced_repetition_items | 4 |

### V42 detalle:

```sql
-- V42__add_item_type_to_spaced_repetition.sql

ALTER TABLE spaced_repetition_items ADD COLUMN item_type VARCHAR(30) DEFAULT 'module-unit';

-- Marcar items existentes de vocabulario
UPDATE spaced_repetition_items
SET item_type = 'vocabulary-word'
WHERE module_name = 'vocabulary-word';

-- Indice para filtrar por tipo
CREATE INDEX idx_srs_item_type ON spaced_repetition_items(user_id, item_type);
```

---

## Archivos a crear/modificar por fase

### FASE 3 — Vocabulario

| Accion | Archivo |
|--------|---------|
| Crear | `resources/db/migration/V39__add_category_block_to_vocab_entries.sql` |
| Crear | `resources/db/migration/V40__seed_a1_vocabulary_blocks.sql` |
| Modificar | `vocabulary/domain/VocabEntry.java` — agregar category, block, blockTitle |
| Modificar | `vocabulary/domain/VocabRepository.java` — agregar `findByLevelAndBlock` |
| Modificar | `vocabulary/infrastructure/persistence/VocabEntryEntity.java` — nuevos campos |
| Modificar | `vocabulary/infrastructure/persistence/JpaVocabRepositoryAdapter.java` |
| Modificar | `vocabulary/infrastructure/persistence/JpaVocabRepository.java` — query method |
| Crear | `vocabulary/application/GetVocabByLevelAndBlockUseCase.java` |
| Modificar | `vocabulary/infrastructure/controller/GetVocabByLevelController.java` — param block |

### FASE 4 — SRS

| Accion | Archivo |
|--------|---------|
| Crear | `resources/db/migration/V42__add_item_type_to_spaced_repetition.sql` |
| Modificar | `spacedrepetition/domain/SpacedRepetitionItem.java` — agregar itemType |
| Modificar | `spacedrepetition/infrastructure/controller/AddToReviewQueueController.java` — request unificado |
| Modificar | `spacedrepetition/infrastructure/controller/SpacedRepetitionItemResponse.java` — agregar itemType |
| Modificar | `spacedrepetition/application/AddToReviewQueueUseCase.java` — router por tipo |
| Modificar | `spacedrepetition/infrastructure/persistence/SpacedRepetitionItemEntity.java` — nuevo campo |

### FASE 5 — Feedback

| Accion | Archivo |
|--------|---------|
| Crear | `pronunciation/application/GetProblematicSoundsUseCase.java` |
| Crear | `pronunciation/infrastructure/controller/GetProblematicSoundsController.java` |
| Crear | `pronunciation/infrastructure/controller/ProblematicSoundResponse.java` |

---

## Resumen de impacto

| Fase | Modulos backend tocados | Migraciones | Endpoints nuevos | Endpoints modificados |
|------|------------------------|-------------|-------------------|-----------------------|
| 1 | Ninguno | 0 | 0 | 0 |
| 2 | Ninguno | 0 | 0 | 0 |
| 3 | vocabulary | 2 (schema + seed) | 0 | 1 (param opcional) |
| 4 | spacedrepetition | 1 (item_type) | 0 | 2 (request + response) |
| 5 | pronunciation | 0 | 1 (problematic-sounds) | 0 |
| **Total** | **2 modulos** | **3** | **1** | **3** |

> **Las fases 1 y 2 son 100% frontend** — no requieren cambios en el backend. Se pueden empezar de inmediato.
