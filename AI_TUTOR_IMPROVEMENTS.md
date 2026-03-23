# AI Tutor - Plan de Mejoras Backend

## Objetivo

Mejorar el tutor IA para que el alumno aprenda ingles de forma mas efectiva: feedback continuo, evaluacion estructurada, objetivos claros, ejercicios generados por IA y tracking de errores.

---

## Fase 1 - Feedback en Streaming

**Problema**: El endpoint streaming (`POST /api/conversations/{id}/messages/stream`) no devuelve feedback. El alumno que usa streaming (la mayoria) no recibe correcciones de gramatica, vocabulario ni pronunciacion.

**Solucion**: Enviar feedback como evento SSE al final del stream, despues del contenido.

### 1.1 - Modificar AnthropicAiTutorStreamAdapter

- [x] Incluir `includeFeedback=true` en el system prompt del stream (igual que el adapter sync)
- [x] Acumular el texto completo del stream en un buffer interno
- [x] Al finalizar el stream, parsear el bloque `<<F>>...<<F>>` del texto acumulado
- [x] Emitir el contenido limpio (sin bloque feedback) como chunks normales
- [x] Emitir un evento SSE final con formato `data: {"type":"feedback","data":{...}}\n\n`

**Archivos**:
- `AnthropicAiTutorStreamAdapter.java` - acumular buffer, parsear feedback, emitir evento final
- `SystemPromptBuilder.java` - sin cambios (ya soporta includeFeedback)

### 1.2 - Persistir feedback del stream

- [x] Modificar `StreamMessageUseCase` para que reciba el feedback parseado
- [x] Crear turno assistant con feedback al finalizar el stream (actualmente no se persiste el turno assistant en streaming)
- [x] Publicar `VocabularyFeedbackEvent` si hay sugerencias de vocabulario (igual que SendMessageUseCase)

**Archivos**:
- `StreamMessageUseCase.java` - persistir turno assistant con feedback
- `AiTutorStreamPort.java` - modificar contrato para devolver feedback junto con el stream

### 1.3 - Nuevo formato SSE

- [x] Definir protocolo de eventos SSE:
  ```
  data: {"type":"chunk","text":"Hello! "}     <- texto parcial
  data: {"type":"chunk","text":"How are "}
  data: {"type":"chunk","text":"you?"}
  data: {"type":"feedback","data":{"g":[...],"v":[...],"p":[...],"e":"..."}}
  data: [DONE]
  ```

**Archivos**:
- `StreamMessageController.java` - ajustar formato de eventos

---

## Fase 2 - Feedback Adaptativo por Nivel

**Problema**: Feedback cada 5 turnos es demasiado espaciado para niveles bajos (A1-A2). Los alumnos principiantes necesitan correccion mas frecuente para no reforzar errores.

**Solucion**: Frecuencia de feedback variable segun nivel CEFR.

### 2.1 - Frecuencia por nivel

- [x] Implementar logica de frecuencia variable:
  | Nivel | Frecuencia feedback | Razon |
  |-------|-------------------|-------|
  | A1-A2 | Cada 2 turnos | Correccion temprana, evitar fossilization |
  | B1-B2 | Cada 3 turnos | Balance entre fluidez y correccion |
  | C1-C2 | Cada 5 turnos | Priorizar fluidez natural |

**Archivos**:
- `AnthropicAiTutorAdapter.java` - reemplazar constante `FEEDBACK_EVERY_N_USER_TURNS` por metodo que reciba nivel
- `AnthropicAiTutorStreamAdapter.java` - misma logica

### 2.2 - Feedback inline para niveles altos

- [x] Para C1-C2, el feedback se integra en la respuesta natural del tutor (no como bloque JSON separado)
- [x] Anadir instruccion al system prompt: "For C1+, weave corrections naturally into your reply"
- [x] El bloque `<<F>>` solo se usa para A1-B2

**Archivos**:
- `SystemPromptBuilder.java` - condicionar formato feedback segun nivel

---

## Fase 3 - Evaluacion Estructurada de Conversacion

**Problema**: Al terminar una conversacion, solo se genera un `summary` de texto. El alumno no recibe puntuacion estructurada de su desempeno.

**Solucion**: Generar evaluacion con scores y metricas al finalizar.

### 3.1 - Modelo de dominio

- [x] Crear `ConversationEvaluation` (record):
  ```
  grammarAccuracy: int (0-100)
  vocabularyRange: int (0-100)
  fluency: int (0-100)
  taskCompletion: int (0-100)
  overallScore: int (0-100)
  ceferLevelDemonstrated: String (a1-c2)
  strengths: List<String>
  areasToImprove: List<String>
  summary: String
  ```

**Archivos**:
- `ConversationEvaluation.java` - nuevo record en domain

### 3.2 - Evaluacion via IA con tool_use

- [x] Anadir metodo `evaluate()` al `AiTutorPort`
- [x] Implementar en `AnthropicAiTutorAdapter` usando tool_use (como WritingEvaluatorAdapter)
- [x] Definir tool `evaluate_conversation` con schema JSON para la evaluacion
- [x] Usar `tool_choice: {type: "tool"}` para forzar respuesta estructurada

**Archivos**:
- `AiTutorPort.java` - anadir `ConversationEvaluation evaluate(ConversationLevel, List<ConversationTurn>)`
- `AnthropicAiTutorAdapter.java` - implementar con tool_use

### 3.3 - Integrar en EndConversationUseCase

- [x] Llamar a `evaluate()` ademas de `summarize()` al terminar
- [x] Persistir evaluacion en la conversacion (nuevo campo o tabla)
- [x] Devolver evaluacion en el response del endpoint

**Archivos**:
- `EndConversationUseCase.java` - llamar evaluate
- `Conversation.java` - anadir campo `evaluation`
- `EndConversationController.java` - incluir evaluacion en response

### 3.4 - Migracion de base de datos

- [x] Crear migracion Flyway: `ALTER TABLE conversations ADD COLUMN evaluation_json JSONB`
- [x] Actualizar entity y adapter de persistencia

**Archivos**:
- `V{next}__add_conversation_evaluation.sql`
- `ConversationEntity.java` - mapear campo evaluation

---

## Fase 4 - Objetivos de Conversacion

**Problema**: Las conversaciones no tienen objetivos de aprendizaje claros. Sin objetivo no hay medicion de logro.

**Solucion**: Permitir definir learning goals al iniciar y medir su cumplimiento.

### 4.1 - Modelo de dominio

- [x] Crear `ConversationGoal` (record):
  ```
  type: String (grammar_focus, vocabulary_theme, skill_practice)
  description: String ("Practice past tense", "Learn restaurant vocabulary")
  targetItems: List<String> (["past simple", "past continuous"] o ["menu", "reservation", "bill"])
  ```
- [x] Anadir `List<ConversationGoal> goals` a `Conversation`

**Archivos**:
- `ConversationGoal.java` - nuevo record en domain
- `Conversation.java` - anadir campo goals

### 4.2 - Generacion automatica de objetivos

- [x] Crear `GenerateGoalsUseCase` que sugiera objetivos basados en:
  - Nivel del alumno
  - Errores frecuentes (de ConversationStats)
  - Topic seleccionado
  - Modulos debiles (de analytics)
- [x] Usar IA para generar 2-3 objetivos relevantes

**Archivos**:
- `GenerateGoalsUseCase.java` - nuevo use case
- `AiTutorPort.java` - anadir metodo generateGoals

### 4.3 - Inyectar objetivos en el prompt

- [x] Modificar SystemPromptBuilder para incluir goals en el system prompt:
  ```
  Goals: 1) Practice past tense 2) Use 5 new restaurant words
  Guide conversation toward these goals. Track progress.
  ```
- [x] El tutor dirige sutilmente la conversacion hacia los objetivos

**Archivos**:
- `SystemPromptBuilder.java` - nuevo metodo appendGoals()
- `StartConversationUseCase.java` - aceptar goals opcionales en request

### 4.4 - Evaluar cumplimiento de objetivos

- [x] En la evaluacion de conversacion (Fase 3), incluir medicion de goals:
  ```
  goalResults: List<GoalResult>
    - goal: ConversationGoal
    - achieved: boolean
    - progress: int (0-100)
    - evidence: String ("Used past tense correctly 4/6 times")
  ```
- [x] Integrar en el tool `evaluate_conversation`

**Archivos**:
- `ConversationEvaluation.java` - anadir goalResults
- `AnthropicAiTutorAdapter.java` - actualizar tool schema

### 4.5 - Endpoints

- [x] `POST /api/conversations` - aceptar `goals` opcionales en el body
- [x] `GET /api/conversations/suggested-goals?level={level}&topic={topic}` - sugerir objetivos

**Archivos**:
- `StartConversationController.java` - actualizar request DTO
- `SuggestGoalsController.java` - nuevo controller

### 4.6 - Migracion

- [x] Crear migracion: `ALTER TABLE conversations ADD COLUMN goals_json JSONB`

**Archivos**:
- `V{next}__add_conversation_goals.sql`
- `ConversationEntity.java` - mapear campo goals

---

## Fase 5 - Ejercicios Post-Conversacion

**Problema**: Despues de una conversacion con errores, el alumno no tiene ejercicios practicos para reforzar lo aprendido. Falta el loop de aprendizaje.

**Solucion**: Generar ejercicios personalizados basados en errores detectados en la conversacion.

### 5.1 - Modelo de dominio

- [x] Crear modulo `exercise` con:
  ```
  ConversationExercise (aggregate):
    id: UUID
    conversationId: UUID
    userId: UUID
    exercises: List<Exercise>
    createdAt: Instant

  Exercise (value object):
    type: ExerciseType (FILL_THE_GAP, SENTENCE_CORRECTION, MULTIPLE_CHOICE, REWRITE)
    instruction: String
    content: String (frase con gap o error)
    options: List<String> (para multiple choice)
    correctAnswer: String
    explanation: String
    relatedError: String (el error original de la conversacion)
  ```

**Archivos**:
- `exercise/domain/ConversationExercise.java`
- `exercise/domain/Exercise.java`
- `exercise/domain/ExerciseType.java`
- `exercise/domain/ConversationExerciseRepository.java`

### 5.2 - Generacion via IA con tool_use

- [x] Crear `GenerateExercisesPort` (domain interface)
- [x] Implementar `AnthropicExerciseGeneratorAdapter`:
  - Recibe: lista de errores de la conversacion + nivel del alumno
  - Genera 3-5 ejercicios con tool_use
  - Tool schema define estructura Exercise
- [x] Tipos de ejercicio segun error:
  | Error | Tipo ejercicio |
  |-------|---------------|
  | Grammar | SENTENCE_CORRECTION, FILL_THE_GAP |
  | Vocabulary | MULTIPLE_CHOICE, FILL_THE_GAP |
  | Prepositions | FILL_THE_GAP |
  | Tense errors | REWRITE |

**Archivos**:
- `exercise/domain/GenerateExercisesPort.java`
- `exercise/infrastructure/ai/AnthropicExerciseGeneratorAdapter.java`

### 5.3 - Use cases

- [x] `GenerateExercisesUseCase` - genera ejercicios al terminar conversacion
- [x] `GetConversationExercisesUseCase` - obtener ejercicios de una conversacion
- [x] `SubmitExerciseAnswersUseCase` - evaluar respuestas del alumno

**Archivos**:
- `exercise/application/GenerateExercisesUseCase.java`
- `exercise/application/GetConversationExercisesUseCase.java`
- `exercise/application/SubmitExerciseAnswersUseCase.java`

### 5.4 - Generacion automatica al terminar conversacion

- [x] En `EndConversationUseCase`, despues de evaluar, disparar evento
- [x] `ConversationEvaluatedEvent` → listener llama a `GenerateExercisesUseCase`
- [x] Los ejercicios se generan async y se persisten

**Archivos**:
- `EndConversationUseCase.java` - publicar evento
- `exercise/infrastructure/event/ConversationEvaluatedEventListener.java`

### 5.5 - Endpoints

- [x] `GET /api/conversations/{id}/exercises` - obtener ejercicios generados
- [x] `POST /api/conversations/{id}/exercises/submit` - enviar respuestas

**Archivos**:
- `exercise/infrastructure/controller/GetConversationExercisesController.java`
- `exercise/infrastructure/controller/SubmitExerciseAnswersController.java`

### 5.6 - Migracion

- [x] Crear tabla `conversation_exercises`:
  ```sql
  id UUID PK
  conversation_id UUID FK
  user_id UUID
  exercises_json JSONB
  created_at TIMESTAMP
  ```

**Archivos**:
- `V{next}__create_conversation_exercises.sql`

---

## Fase 6 - Error Pattern Tracking

**Problema**: Los errores de gramatica son strings libres. No se clasifican ni se trackean patrones recurrentes.

**Solucion**: Clasificar errores por categoria y persistir para analisis longitudinal.

### 6.1 - Modelo de dominio

- [x] Crear `ErrorPattern` (aggregate):
  ```
  id: UUID
  userId: UUID
  category: ErrorCategory (TENSE, ARTICLE, PREPOSITION, WORD_ORDER, SUBJECT_VERB_AGREEMENT, VOCABULARY, SPELLING, PUNCTUATION, OTHER)
  pattern: String ("present simple vs present continuous")
  examples: List<String> ("I go yesterday" → "I went yesterday")
  occurrenceCount: int
  lastOccurred: Instant
  resolved: boolean
  ```

**Archivos**:
- `errorpattern/domain/ErrorPattern.java`
- `errorpattern/domain/ErrorCategory.java`
- `errorpattern/domain/ErrorPatternRepository.java`

### 6.2 - Clasificacion via IA

- [x] Modificar el feedback del tutor para incluir categoria de error
- [x] Actualizar `TutorFeedback` record:
  ```
  grammarCorrections: List<GrammarCorrection>
    original: String
    corrected: String
    category: String (tense, article, preposition, etc.)
    rule: String
  ```
- [x] Actualizar parsing en ambos adapters

**Archivos**:
- `TutorFeedback.java` - evolucionar de List<String> a List<GrammarCorrection>
- `GrammarCorrection.java` - nuevo record
- `SystemPromptBuilder.java` - actualizar formato feedback con categorias
- `AnthropicAiTutorAdapter.java` - actualizar parsing

### 6.3 - Persistencia y actualizacion

- [x] `RecordErrorPatternUseCase` - registrar o incrementar patron
- [x] `GetErrorPatternsUseCase` - obtener patrones del usuario
- [x] Listener en VocabularyFeedbackEventListener (o nuevo listener) para procesar errores

**Archivos**:
- `errorpattern/application/RecordErrorPatternUseCase.java`
- `errorpattern/application/GetErrorPatternsUseCase.java`
- `errorpattern/infrastructure/persistence/`

### 6.4 - Endpoints

- [x] `GET /api/profiles/{userId}/error-patterns` - listar patrones de error
- [x] `GET /api/profiles/{userId}/error-patterns/summary` - resumen por categoria

### 6.5 - Migracion

- [x] Crear tabla `error_patterns`:
  ```sql
  id UUID PK
  user_id UUID
  category VARCHAR(50)
  pattern VARCHAR(255)
  examples_json JSONB
  occurrence_count INT
  last_occurred TIMESTAMP
  resolved BOOLEAN DEFAULT false
  created_at TIMESTAMP
  ```
- [x] Index en (user_id, category, occurrence_count DESC)

**Archivos**:
- `V{next}__create_error_patterns.sql`

---

## Orden de Implementacion Recomendado

| Prioridad | Fase | Dependencias |
|-----------|------|-------------|
| 1 | Fase 1 - Feedback en Streaming | Ninguna |
| 2 | Fase 2 - Feedback Adaptativo | Ninguna |
| 3 | Fase 3 - Evaluacion Conversacion | Ninguna |
| 4 | Fase 6 - Error Patterns | Fase 2 (categorias en feedback) |
| 5 | Fase 4 - Objetivos | Fase 3 (evaluacion incluye goals) |
| 6 | Fase 5 - Ejercicios Post-Conversacion | Fase 3 + Fase 6 |
