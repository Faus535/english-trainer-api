# Features de Alto Valor - Roadmap API

Fases API necesarias para soportar las 8 features del roadmap web.
Base package: `com.faus535.englishtrainer`

> Las features 1 (Repaso espaciado) y 2 (Feedback pronunciacion fases 2.1-2.2) **no necesitan cambios en la API** - los endpoints ya existen o son solo frontend.

---

## Feature 2 - Feedback Pronunciacion (Fase 2.3)

**Objetivo**: Persistir errores frecuentes de pronunciacion del usuario.

### Fase API 2.3 - Errores de pronunciacion

**Modulo**: `pronunciation` (nuevo) o extender `moduleprogress`

- [ ] **Dominio**
  - Aggregate: `PronunciationError` (id, userId, word, expectedPhoneme, spokenPhoneme, count, lastOccurred)
  - Value Object: `PronunciationErrorId` (record UUID)
  - Repository interface: `PronunciationErrorRepository`

- [ ] **Application**
  - `RecordPronunciationErrorUseCase` - registrar o incrementar error
  - `GetFrequentErrorsUseCase` - obtener errores mas frecuentes del usuario

- [ ] **Infrastructure**
  - Entity JPA `PronunciationErrorEntity` Persistable<UUID>
  - Spring Data repository adapter
  - `POST /api/profiles/{userId}/pronunciation/errors` - registrar error
  - `GET /api/profiles/{userId}/pronunciation/errors` - listar errores (query: sort=count,desc&limit=20)

- [ ] **Migracion Flyway**
  - Tabla `pronunciation_errors` (id, user_id, word, expected_phoneme, spoken_phoneme, count, last_occurred, created_at)
  - Index en (user_id, count DESC)

---

## Feature 3 - Sesiones Adaptativas (Fase 3.2)

**Objetivo**: Generar sesiones que priorizan modulos debiles.

### Fase API 3.2 - Generacion ponderada

**Modulo**: `session` (modificar existente)

- [ ] **Dominio**
  - Value Object: `ModuleWeight` (record: moduleName, weight 0.0-1.0)
  - Extender `SessionRequest` para aceptar `List<ModuleWeight> weights`
  - Modificar `SessionGenerator` para usar pesos en la distribucion de bloques

- [ ] **Application**
  - Modificar `GenerateSessionUseCase` para aceptar pesos opcionales
  - Si no se envian pesos, mantener comportamiento actual (distribucion uniforme)
  - Validar que los pesos sumen ~1.0 (con tolerancia)
  - Garantizar variedad minima: ningun modulo con peso < 0.1

- [ ] **Infrastructure**
  - Actualizar request DTO de `POST /api/profiles/{userId}/sessions/generate`:
    ```json
    {
      "mode": "full",
      "weights": [
        { "moduleName": "grammar", "weight": 0.35 },
        { "moduleName": "listening", "weight": 0.25 },
        { "moduleName": "vocabulary", "weight": 0.20 },
        { "moduleName": "pronunciation", "weight": 0.10 },
        { "moduleName": "phrases", "weight": 0.10 }
      ]
    }
    ```
  - Campo `weights` es opcional, retrocompatible

- [ ] **Tests**
  - Test unitario: generacion con pesos respeta proporciones
  - Test unitario: sin pesos mantiene comportamiento original
  - Test unitario: pesos invalidos (suma != 1, negativos) lanza excepcion

---

## Feature 4 - Gamificacion Activa (Fase 4.4)

**Objetivo**: Retos diarios con XP bonus.

### Fase API 4.4 - Retos diarios

**Modulo**: `dailychallenge` (nuevo)

- [ ] **Dominio**
  - Aggregate: `DailyChallenge` (id, type, description, target, xpReward, date)
  - Aggregate: `UserChallenge` (id, userId, challengeId, progress, completed, completedAt)
  - Value Objects: `DailyChallengeId`, `UserChallengeId`, `ChallengeType` (enum: FLASHCARDS, SPEAK_MINUTES, TUTOR_MESSAGES, SESSION_COMPLETE, MINI_GAME)
  - Repository interfaces: `DailyChallengeRepository`, `UserChallengeRepository`
  - Domain Event: `ChallengeCompletedEvent`

- [ ] **Application**
  - `GetTodayChallengeUseCase` - obtener reto del dia (genera si no existe)
  - `GetUserChallengeProgressUseCase` - progreso del usuario en el reto
  - `UpdateChallengeProgressUseCase` - incrementar progreso
  - `CompleteChallengeUseCase` - marcar completado y otorgar XP
  - `ChallengeGeneratorService` - logica de generacion aleatoria basada en nivel del usuario

- [ ] **Infrastructure**
  - Entity JPA `DailyChallengeEntity`, `UserChallengeEntity`
  - Spring Data repository adapters
  - `GET /api/challenges/today` - reto del dia
  - `GET /api/profiles/{userId}/challenges/today` - progreso del usuario
  - `PUT /api/profiles/{userId}/challenges/today/progress` - actualizar progreso
  - Event listener: al completar sesion/review/conversacion, actualizar progreso del reto

- [ ] **Migracion Flyway**
  - Tabla `daily_challenges` (id, type, description_es, description_en, target, xp_reward, challenge_date)
  - Tabla `user_challenges` (id, user_id, challenge_id, progress, completed, completed_at)
  - Index en (challenge_date), (user_id, challenge_id) UNIQUE

- [ ] **Tests**
  - Test: generacion de reto por dia
  - Test: no duplicar reto si ya existe para hoy
  - Test: progreso se incrementa correctamente
  - Test: completar otorga XP via evento

---

## Feature 5 - Historial de Errores del Tutor

**Objetivo**: Agregar y servir errores de conversaciones del tutor.

### Fase API 5.1 - Errores agregados

**Modulo**: `conversation` (extender existente)

- [ ] **Dominio**
  - Value Object: `ConversationError` (type: GRAMMAR|VOCABULARY|PRONUNCIATION, original, corrected, rule, count, lastSeen)
  - Extender aggregate o crear `UserErrorSummary` (userId, errors agrupados por tipo)

- [ ] **Application**
  - `GetUserErrorsUseCase` - errores agrupados por tipo, ordenados por frecuencia
  - `GetErrorTrendUseCase` - errores por semana (para grafica de mejora)
  - Modificar flujo de conversacion: al recibir feedback del tutor, persistir errores

- [ ] **Infrastructure**
  - `GET /api/profiles/{userId}/tutor/errors?type=grammar&limit=20` - errores filtrados
  - `GET /api/profiles/{userId}/tutor/errors/trend?weeks=8` - tendencia semanal
  - Respuesta:
    ```json
    {
      "grammar": [
        { "original": "I goed", "corrected": "I went", "rule": "Irregular past", "count": 5, "lastSeen": "2026-03-20" }
      ],
      "vocabulary": [...],
      "pronunciation": [...]
    }
    ```

- [ ] **Migracion Flyway**
  - Tabla `tutor_errors` (id, user_id, type, original_text, corrected_text, rule, count, first_seen, last_seen)
  - Index en (user_id, type, count DESC)

### Fase API 5.2 - Ejercicios de refuerzo

- [ ] **Application**
  - `GenerateErrorExerciseUseCase` - generar ejercicio basado en un error especifico
  - Usar Anthropic Claude para generar fill-the-gap o alternativas

- [ ] **Infrastructure**
  - `POST /api/profiles/{userId}/tutor/errors/{errorId}/exercise` - generar ejercicio
  - Respuesta con tipo de ejercicio y opciones

---

## Feature 6 - Mini-juegos

**Objetivo**: Servir contenido para juegos rapidos adaptados al nivel.

### Fase API 6.1-6.3 - Contenido para juegos

**Modulo**: `minigame` (nuevo)

- [ ] **Application**
  - `GetWordMatchDataUseCase` - 10 pares palabra-traduccion por nivel
  - `GetFillGapDataUseCase` - 8 frases con hueco por nivel
  - `GetUnscrambleDataUseCase` - palabras/frases para desordenar por nivel

- [ ] **Infrastructure**
  - `GET /api/minigames/word-match?level=a2` - datos para word match
  - `GET /api/minigames/fill-gap?level=b1` - datos para fill the gap
  - `GET /api/minigames/unscramble?level=a1` - datos para unscramble
  - Los datos se pueden extraer de `vocabulary` y `phrase` existentes con queries aleatorias

### Fase API 6.4 - Puntuaciones

- [ ] **Dominio**
  - Aggregate: `MiniGameScore` (id, userId, gameType, score, playedAt)
  - Repository interface

- [ ] **Infrastructure**
  - `POST /api/profiles/{userId}/minigames/scores` - guardar puntuacion
  - `GET /api/profiles/{userId}/minigames/scores?type=word-match` - historial
  - Al guardar puntuacion, otorgar XP via `GrantXpUseCase`

- [ ] **Migracion Flyway**
  - Tabla `mini_game_scores` (id, user_id, game_type, score, xp_earned, played_at)
  - Index en (user_id, game_type)

---

## Feature 7 - Pares Minimos de Pronunciacion

**Objetivo**: Banco de pares minimos y persistencia de resultados.

### Fase API 7.3 - Banco de pares minimos

**Modulo**: `pronunciation` (extender o crear)

- [ ] **Dominio**
  - Aggregate: `MinimalPair` (id, word1, word2, phoneme1, phoneme2, soundCategory, level)
  - Value Object: `MinimalPairId`, `SoundCategory` (enum: VOWEL_LENGTH, TH_SOUNDS, V_B, S_Z, SH_CH, etc.)

- [ ] **Application**
  - `GetMinimalPairsByLevelUseCase` - obtener pares por nivel y categoria de sonido
  - `RecordMinimalPairResultUseCase` - guardar resultado de ejercicio

- [ ] **Infrastructure**
  - `GET /api/pronunciation/minimal-pairs?level=a1&sound=vowel_length&limit=10`
  - `POST /api/profiles/{userId}/pronunciation/minimal-pairs/results`
  - `GET /api/profiles/{userId}/pronunciation/minimal-pairs/stats` - precision por categoria

- [ ] **Migracion Flyway**
  - Tabla `minimal_pairs` (id, word1, word2, ipa1, ipa2, sound_category, level) + seed data
  - Tabla `minimal_pair_results` (id, user_id, pair_id, correct, answered_at)
  - Index en (sound_category, level), (user_id)

- [ ] **Seed data**: Pares minimos comunes para hispanohablantes
  - /ɪ/ vs /iː/: ship/sheep, bit/beat, sit/seat, fill/feel, hit/heat
  - /v/ vs /b/: van/ban, vest/best, vine/bine, vet/bet
  - /θ/ vs /t/: think/tink, three/tree, thin/tin, bath/bat
  - /ð/ vs /d/: then/den, they/day, breathe/breed
  - /æ/ vs /ʌ/: cat/cut, bat/but, cap/cup, ran/run
  - /ʃ/ vs /tʃ/: ship/chip, share/chair, shoe/chew

---

## Feature 8 - Vocabulario en Contexto (Fase 8.3)

**Objetivo**: Generar frases de ejemplo con IA para palabras de vocabulario.

### Fase API 8.2 - Flashcards contextuales

**Modulo**: `spacedrepetition` (extender)

- [ ] Extender `SpacedRepetitionItem` para almacenar frases de contexto
- [ ] Al crear item de repaso, opcionalmente incluir `contextSentences`

### Fase API 8.3 - Generacion de ejemplos con IA

**Modulo**: `vocabulary` (extender)

- [ ] **Application**
  - `GenerateContextSentencesUseCase` - generar 2-3 frases con la palabra, adaptadas al nivel
  - Usar Anthropic Claude Haiku (barato y rapido)
  - Cachear resultado en BD para no regenerar

- [ ] **Infrastructure**
  - `POST /api/vocabulary/{wordId}/context?level=a2` - generar/obtener frases de contexto
  - Respuesta:
    ```json
    {
      "word": "achieve",
      "sentences": [
        { "text": "She worked hard to achieve her goals.", "highlight": [25, 32] },
        { "text": "You can achieve anything with practice.", "highlight": [8, 15] }
      ]
    }
    ```

- [ ] **Migracion Flyway**
  - Tabla `vocabulary_context` (id, vocabulary_id, level, sentences_json, generated_at)
  - Index en (vocabulary_id, level) UNIQUE

---

## Resumen de Trabajo API

| Feature | Modulo | Tipo | Tablas nuevas | Endpoints nuevos |
|---------|--------|------|---------------|-----------------|
| 2.3 Pronunciacion errores | pronunciation | Nuevo | 1 | 2 |
| 3.2 Sesiones ponderadas | session | Modificar | 0 | 0 (modifica existente) |
| 4.4 Retos diarios | dailychallenge | Nuevo | 2 | 3 |
| 5.1-5.2 Errores tutor | conversation | Extender | 1 | 3 |
| 6.1-6.4 Mini-juegos | minigame | Nuevo | 1 | 5 |
| 7.3-7.4 Pares minimos | pronunciation | Extender | 2 | 3 |
| 8.2-8.3 Vocabulario contexto | vocabulary | Extender | 1 | 1 |

**Total: 8 tablas nuevas, 17 endpoints nuevos, 2 modulos nuevos, 3 modulos extendidos**

## Orden de Implementacion Sugerido

1. **Feature 3.2** (sesiones ponderadas) - modificacion minima, sin tablas nuevas
2. **Feature 5.1** (errores tutor) - 1 tabla, aprovecha datos de conversaciones existentes
3. **Feature 4.4** (retos diarios) - 2 tablas, modulo independiente
4. **Feature 6** (mini-juegos) - reutiliza vocab/phrases existentes
5. **Feature 7** (pares minimos) - modulo nuevo con seed data
6. **Feature 2.3** (errores pronunciacion) - tabla simple
7. **Feature 8.3** (vocabulario contexto) - integracion con IA
8. **Feature 5.2** (ejercicios refuerzo) - integracion con IA, depende de 5.1
