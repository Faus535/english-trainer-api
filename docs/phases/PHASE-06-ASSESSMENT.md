# Fase 6 — Assessment (Level Test y Mini-Tests)

## Objetivo

Implementar el sistema de evaluacion: test inicial de nivel (placement test) y
mini-tests periodicos que ajustan la dificultad adaptativamente.

## Estado actual

- **Cero codigo** para evaluaciones
- Frontend tiene logica completa en `level-test.js` (30 vocab + 24 grammar + 10 listening + 7 pronunciation)
- Frontend tiene `mini-test.js` (15 preguntas cada ~8 unidades completadas)

## Datos del frontend

### Level Test (level-test.js)

4 secciones, preguntas organizadas por dificultad creciente:
- **Vocabulary** (30 preguntas): traduccion en→es con 4 opciones
- **Grammar** (24 preguntas): completar frase con opcion correcta
- **Listening** (10 frases): escuchar TTS y transcribir
- **Pronunciation** (7 preguntas): identificar fonema correcto

Resultado: nivel por modulo (A1-C1) basado en porcentaje de aciertos por tramo.

### Mini-Test (mini-test.js)

15 preguntas mixtas, se dispara cada 8 unidades completadas en un modulo.
Si score < 60% → bajar nivel. Si score > 85% → confirmar subida.

## Entidades y Value Objects

### Nuevo Aggregate: `LevelTestResult`

```
LevelTestResult (Aggregate Root)
├── LevelTestResultId (Value Object — UUID)
├── UserProfileId
├── vocabularyScore: int (porcentaje)
├── grammarScore: int
├── listeningScore: int
├── pronunciationScore: int
├── assignedLevels: Map<String, String> (module → level)
└── completedAt: Instant
```

### Nuevo Aggregate: `MiniTestResult`

```
MiniTestResult (Aggregate Root)
├── MiniTestResultId (Value Object — UUID)
├── UserProfileId
├── moduleName: String
├── level: String
├── score: int (porcentaje)
├── totalQuestions: int
├── correctAnswers: int
├── recommendation: String (maintain/promote/demote)
└── completedAt: Instant
```

### Value Object: `TestQuestion`

```
TestQuestion (Value Object — no persiste, generado dinamicamente)
├── id: String
├── type: String (vocabulary/grammar/listening/pronunciation)
├── question: String
├── options: List<String> (para multiple choice)
├── correctAnswer: String
├── level: String
└── audioText: String (para listening, el texto que TTS debe leer)
```

## Migraciones

### V5__create_assessment_tables.sql

```sql
CREATE TABLE level_test_results (
    id                    UUID PRIMARY KEY,
    user_id               UUID NOT NULL REFERENCES user_profiles(id) ON DELETE CASCADE,
    vocabulary_score      INT NOT NULL,
    grammar_score         INT NOT NULL,
    listening_score       INT NOT NULL,
    pronunciation_score   INT NOT NULL,
    assigned_levels       TEXT NOT NULL,  -- JSON: {"listening":"b1","vocabulary":"a2",...}
    completed_at          TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE mini_test_results (
    id              UUID PRIMARY KEY,
    user_id         UUID NOT NULL REFERENCES user_profiles(id) ON DELETE CASCADE,
    module_name     VARCHAR(50) NOT NULL,
    level           VARCHAR(10) NOT NULL,
    score           INT NOT NULL,
    total_questions INT NOT NULL,
    correct_answers INT NOT NULL,
    recommendation  VARCHAR(20) NOT NULL,  -- maintain, promote, demote
    completed_at    TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_mini_test_user ON mini_test_results(user_id);
```

### V5_1__seed_test_questions.sql (opcional)

Insertar banco de preguntas si se decide persistirlas en BD en vez de generarlas.

## Use Cases

| Use Case | Tipo | Descripcion |
|----------|------|-------------|
| `GetLevelTestQuestionsUseCase` | Query | Generar set de preguntas para test de nivel |
| `SubmitLevelTestUseCase` | Command | Evaluar respuestas, calcular niveles, guardar resultado, actualizar perfil |
| `GetMiniTestQuestionsUseCase` | Query | Generar preguntas de mini-test para un modulo/nivel |
| `SubmitMiniTestUseCase` | Command | Evaluar respuestas, dar recomendacion, guardar resultado |
| `GetTestHistoryUseCase` | Query | Obtener historial de tests de un usuario |
| `ShouldTakeMiniTestUseCase` | Query | Verificar si el usuario debe tomar un mini-test (cada ~8 units) |

## Endpoints

| Metodo | Ruta | Use Case | Request/Response |
|--------|------|----------|-----------------|
| GET | `/api/assessments/level-test` | GetLevelTestQuestionsUseCase | List<TestQuestionResponse> |
| POST | `/api/profiles/{uid}/assessments/level-test` | SubmitLevelTestUseCase | Request: `{ "answers": {...} }` Response: LevelTestResultResponse |
| GET | `/api/assessments/mini-test?module=listening&level=b1` | GetMiniTestQuestionsUseCase | List<TestQuestionResponse> |
| POST | `/api/profiles/{uid}/assessments/mini-test` | SubmitMiniTestUseCase | Request: `{ "module": "listening", "level": "b1", "answers": {...} }` Response: MiniTestResultResponse |
| GET | `/api/profiles/{uid}/assessments/history` | GetTestHistoryUseCase | List<TestResultResponse> |
| GET | `/api/profiles/{uid}/assessments/mini-test/check?module=listening` | ShouldTakeMiniTestUseCase | `{ "shouldTake": true, "unitsSinceLastTest": 9 }` |

## Criterios de aceptacion

- [ ] Level test genera preguntas de dificultad creciente por seccion
- [ ] Submit level test calcula nivel por modulo y actualiza `UserProfile` automaticamente
- [ ] Submit level test marca `testCompleted = true`
- [ ] Mini-test genera 15 preguntas del modulo/nivel indicado
- [ ] Recomendacion correcta: <60% demote, 60-85% maintain, >85% promote
- [ ] `ShouldTakeMiniTest` cuenta unidades completadas desde ultimo mini-test
- [ ] Tests unitarios para logica de calculo de nivel
- [ ] Tests de integracion para flujo completo
