# Level Test Improvements - Backend

## Problema actual

1. **El test siempre es el mismo**: Las preguntas estan hardcodeadas en `GetLevelTestQuestionsUseCase`. Un usuario puede memorizar las respuestas y obtener C1 sin saber ingles.
2. **Retomar el test no actualiza el nivel**: `SubmitLevelTestUseCase` si sobreescribe los niveles, pero el frontend no sincroniza correctamente el estado tras retomar.
3. **Listening solo cambia velocidad**: Las frases son igual de simples en todos los niveles; solo varia la velocidad del audio (0.8x-1.2x). Deberian ser frases mas complejas.

---

## Fase 1: Banco de preguntas dinamico

**Objetivo**: Que cada test sea diferente, evitando que el usuario memorice respuestas.

### Cambios

1. **Crear tabla `test_questions` en BD** (migration Flyway)
   - Columnas: `id`, `type` (vocabulary/grammar/listening/pronunciation), `level` (A1-C1), `question`, `options` (jsonb), `correct_answer`, `active`, `created_at`
   - Seed inicial con las preguntas actuales hardcodeadas + nuevas preguntas

2. **Crear dominio `TestQuestion`**
   - `TestQuestionRepository` con metodo `findRandomByTypeAndLevel(type, level, count)`
   - Query nativa: `SELECT * FROM test_questions WHERE type = ? AND level = ? AND active = true ORDER BY RANDOM() LIMIT ?`

3. **Modificar `GetLevelTestQuestionsUseCase`**
   - En lugar de devolver preguntas fijas, seleccionar aleatoriamente del banco
   - Mantener la distribucion por nivel (ej: 5 A1, 3 A2, 2 B1, 2 B2, 3 C1 para vocabulario)
   - Endpoint devuelve preguntas diferentes en cada llamada

4. **Seed de preguntas**
   - Minimo 3x preguntas por slot (si necesitas 5 de A1 vocabulary, tener al menos 15 en el banco)
   - Cubrir listening y pronunciation con frases de complejidad variable por nivel

### Archivos a crear/modificar

| Archivo | Accion |
|---------|--------|
| `V8.0.0__assessment_create_test_questions.sql` | Crear tabla + seed |
| `assessment/domain/TestQuestion.java` | Nuevo aggregate |
| `assessment/domain/TestQuestionRepository.java` | Nuevo repositorio |
| `assessment/infrastructure/persistence/TestQuestionEntity.java` | Entidad JPA |
| `assessment/infrastructure/persistence/JpaTestQuestionRepository.java` | Implementacion |
| `assessment/application/GetLevelTestQuestionsUseCase.java` | Modificar: usar repositorio |

---

## Fase 2: Listening con complejidad progresiva

**Objetivo**: Que el listening evalúe comprension real, no solo velocidad de audio.

### Cambios

1. **Ampliar `test_questions` con metadata de listening**
   - Nuevas columnas: `audio_speed` (decimal), `complexity_tags` (jsonb)
   - Tags: `simple_sentence`, `compound_sentence`, `idiom`, `phrasal_verb`, `passive_voice`, `conditional`, `relative_clause`

2. **Definir complejidad por nivel**

   | Nivel | Tipo de frase | Velocidad | Ejemplo |
   |-------|--------------|-----------|---------|
   | A1 | Oraciones simples, SVO | 0.8x | "The cat is on the table." |
   | A2 | Coordinadas (and, but, or) | 0.85x | "I like coffee, but she prefers tea." |
   | B1 | Subordinadas, phrasal verbs comunes | 0.9x | "If it rains tomorrow, we'll stay home." |
   | B2 | Pasiva, condicionales, idioms | 1.0x | "Had I known earlier, I would have told you." |
   | C1 | Oraciones complejas, vocabulario avanzado, contracciones naturales | 1.1x | "She's been meaning to bring it up, but it never quite comes up in conversation." |

3. **Seed de frases por nivel** (minimo 15 por nivel)
   - A1: 15 frases simples (3-6 palabras)
   - A2: 15 frases coordinadas (5-8 palabras)
   - B1: 15 frases con subordinadas (8-12 palabras)
   - B2: 15 frases complejas (10-15 palabras)
   - C1: 15 frases avanzadas (12-20 palabras, con idioms y contracciones)

### Archivos a crear/modificar

| Archivo | Accion |
|---------|--------|
| `V8.1.0__assessment_add_listening_complexity.sql` | Alter table + seed frases |
| `assessment/domain/TestQuestion.java` | Anadir campos complexity |
| `GetLevelTestQuestionsUseCase.java` | Incluir metadata de velocidad y complejidad |

---

## Fase 3: Corregir sincronizacion de niveles al retomar test

**Objetivo**: Que al retomar el test, los nuevos niveles se persistan correctamente.

### Cambios

1. **Verificar `SubmitLevelTestUseCase`**
   - Confirmar que `userProfile.updateLevels()` sobreescribe los 5 niveles
   - Asegurar que se devuelve el perfil actualizado en la respuesta HTTP

2. **Nuevo endpoint: `PUT /api/user-profiles/{id}/reset-test`**
   - Pone `testCompleted = false` en backend
   - Actualmente solo se hace en frontend (localStorage), lo que causa desincronizacion

3. **Respuesta de `submitLevelTest` debe incluir el perfil actualizado**
   - El frontend necesita recibir los nuevos niveles para actualizar su estado local

### Archivos a crear/modificar

| Archivo | Accion |
|---------|--------|
| `user/application/ResetTestUseCase.java` | Nuevo use case |
| `user/infrastructure/controller/ResetTestController.java` | Nuevo endpoint PUT |
| `assessment/application/SubmitLevelTestUseCase.java` | Verificar respuesta completa |
| `assessment/infrastructure/controller/SubmitLevelTestController.java` | Incluir perfil en response |

---

## Fase 4: Evitar gaming del test

**Objetivo**: Dificultar que un usuario suba de nivel memorizando respuestas.

### Cambios

1. **Cooldown entre tests**
   - Guardar `last_test_at` en `user_profiles`
   - No permitir retomar antes de 24h (configurable)
   - Devolver 429 con tiempo restante si intenta antes

2. **Historial de preguntas usadas**
   - Tabla `test_question_history`: `user_id`, `question_id`, `asked_at`
   - Al generar test, priorizar preguntas no vistas por el usuario
   - `findRandomByTypeAndLevelExcluding(type, level, count, excludeIds)`

3. **Limite de intentos con aviso**
   - Tras 3 intentos en 30 dias, mostrar aviso (no bloquear)
   - El frontend muestra "Llevas X intentos este mes"

### Archivos a crear/modificar

| Archivo | Accion |
|---------|--------|
| `V8.2.0__assessment_add_test_history.sql` | Crear tabla historial + cooldown |
| `user/domain/UserProfile.java` | Anadir `lastTestAt` |
| `assessment/domain/TestQuestionHistoryRepository.java` | Nuevo repositorio |
| `assessment/application/GetLevelTestQuestionsUseCase.java` | Excluir preguntas vistas |
| `assessment/application/SubmitLevelTestUseCase.java` | Validar cooldown |

---

## Orden de ejecucion

```
Fase 1 (Banco dinamico)     ← Prerequisito para todo lo demas
  ↓
Fase 2 (Listening complejo) ← Puede ir en paralelo con Fase 3
Fase 3 (Sync niveles)       ← Puede ir en paralelo con Fase 2
  ↓
Fase 4 (Anti-gaming)        ← Depende de Fase 1
```
