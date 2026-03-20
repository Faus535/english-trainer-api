# Fase 5 — Vocabulario completo y Seed de datos

## Objetivo

Ampliar el modulo de vocabulario con CRUD completo, busqueda, paginacion,
y cargar las 1209 palabras del frontend. Ademas, crear el modulo de frases (phrases).

## Estado actual

- `VocabEntry` aggregate existe con `findAll()` y `findByLevel()`
- Tabla `vocab_entries` creada en V1
- **Falta**: crear/actualizar/eliminar, busqueda, paginacion, seed de datos
- **Falta**: modulo de frases (75 frases del speak-quiz.js del frontend)

## Vocabulario del frontend

El archivo `vocab-data.js` contiene 1209 entradas con esta estructura:

```javascript
{ en: "hello", ipa: "/hɛˈloʊ/", es: "hola", type: "interjection", ex: "Hello, how are you?" }
```

Distribucion por nivel: A1 (~200), A2 (~250), B1 (~300), B2 (~250), C1 (~200+)

## Frases del frontend

El archivo `speak-quiz.js` contiene 75 frases organizadas por nivel:

```javascript
{ en: "Can I have the bill, please?", es: "¿Me puede traer la cuenta, por favor?" }
```

## Entidades y Value Objects

### VocabEntry — ya existe, sin cambios

### Nuevo Aggregate: `Phrase`

```
Phrase (Aggregate Root)
├── PhraseId (Value Object — UUID)
├── en: String
├── es: String
└── level: VocabLevel (reutilizar)
```

## Migraciones

### V4__create_phrases.sql

```sql
CREATE TABLE phrases (
    id    UUID PRIMARY KEY,
    en    VARCHAR(500) NOT NULL,
    es    VARCHAR(500) NOT NULL,
    level VARCHAR(10) NOT NULL
);

CREATE INDEX idx_phrases_level ON phrases(level);
```

### V4_1__seed_vocab_entries.sql

INSERT de las 1209 palabras del frontend (generar desde vocab-data.js).

### V4_2__seed_phrases.sql

INSERT de las 75 frases del frontend.

## Use Cases — Vocabulario

| Use Case | Tipo | Descripcion |
|----------|------|-------------|
| `SearchVocabUseCase` | Query | Buscar por palabra en ingles o espanol |
| `GetVocabPaginatedUseCase` | Query | Obtener vocabulario paginado con filtro opcional por nivel/tipo |
| `CreateVocabEntryUseCase` | Command | Crear nueva entrada de vocabulario |
| `GetRandomVocabUseCase` | Query | Obtener N palabras aleatorias (para flashcards) |

## Use Cases — Frases

| Use Case | Tipo | Descripcion |
|----------|------|-------------|
| `GetPhrasesByLevelUseCase` | Query | Obtener frases filtradas por nivel |
| `GetRandomPhrasesUseCase` | Query | Obtener N frases aleatorias de un nivel (para speak quiz) |

## Endpoints

| Metodo | Ruta | Use Case | Response |
|--------|------|----------|----------|
| GET | `/api/vocab?search=hello` | SearchVocabUseCase | List<VocabResponse> |
| GET | `/api/vocab?page=0&size=20&level=b1` | GetVocabPaginatedUseCase | Page<VocabResponse> |
| POST | `/api/vocab` | CreateVocabEntryUseCase | 201 VocabResponse |
| GET | `/api/vocab/random?count=10&level=a1` | GetRandomVocabUseCase | List<VocabResponse> |
| GET | `/api/phrases?level=b1` | GetPhrasesByLevelUseCase | List<PhraseResponse> |
| GET | `/api/phrases/random?count=5&level=a1` | GetRandomPhrasesUseCase | List<PhraseResponse> |

## Criterios de aceptacion

- [ ] Busqueda case-insensitive por `en` o `es`
- [ ] Paginacion funcional con Spring Pageable
- [ ] Seed de 1209 palabras cargadas correctamente (verificar con GET /api/vocab count)
- [ ] Seed de 75 frases cargadas correctamente
- [ ] Endpoint random devuelve resultados distintos en cada llamada
- [ ] Tests de integracion para busqueda y paginacion
