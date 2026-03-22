# Plan de Optimizacion de Tokens - English Trainer API

## Estado Actual

| Parametro | Valor actual |
|-----------|-------------|
| Modelo conversacion | `claude-haiku-4-5` (~10-20x mas barato que Sonnet) |
| Modelo writing | `claude-haiku-4-5` |
| Max tokens conversacion | 300 |
| Max tokens writing | 600 |
| Historial enviado | 10 turnos |
| Mensajes antiguos | Truncados a 150 chars (ultimos 6 completos) |
| Prompt caching | Si (`cache_control ephemeral`) |
| Feedback JSON | Cada 3 turnos de usuario |
| Greeting cache | In-memory por nivel+topic |

### Coste estimado actual

| Escenario | Input tokens | Output tokens | Coste aprox |
|-----------|-------------|---------------|-------------|
| Greeting (cacheado) | 0 | 0 | $0.00 |
| Turno sin feedback | ~400-500 | ~100-200 | ~$0.0001 |
| Turno con feedback | ~500-600 | ~200-300 | ~$0.0002 |
| Conversacion 10 turnos | ~4,000-5,000 | ~1,500-2,000 | ~$0.005-0.01 |

---

## Optimizaciones Ya Implementadas

### FASE 1 - Quick Wins (completada)
- [x] Modelo por defecto: Haiku 4.5 (90% ahorro vs Sonnet)
- [x] MAX_CONTEXT_TURNS: 30 -> 10
- [x] Prompt caching activado (header `anthropic-beta` + `cache_control`)

### FASE 2 - Prompt Optimization (completada)
- [x] System prompts compactados (~60% menos texto)
- [x] Max tokens: 300 conversacion, 600 writing
- [x] Feedback JSON solo cada 3 turnos de usuario

### FASE 3 - Arquitectura (completada parcialmente)
- [x] Mensajes antiguos truncados a 150 chars (ultimos 6 completos)
- [x] Cache in-memory del greeting por nivel+topic
- [ ] Tool use para writing (pendiente)

---

## Nuevas Optimizaciones Propuestas

### FASE 4 - Reduccion de Tokens de Output (completada)

**Objetivo**: Reducir tokens generados por la AI sin perder funcionalidad.

#### 4.1 - Limitar longitud de respuesta en el system prompt [HECHO]

**Problema**: La AI a veces genera respuestas largas (150-250 tokens) cuando una respuesta de 50-80 tokens seria suficiente para una conversacion natural.

**Solucion**: Anadir instruccion explicita de longitud maxima al system prompt segun nivel.

```
A1/A2: "Reply in max 2 sentences."
B1/B2: "Reply in max 3 sentences."
C1/C2: "Reply in max 4 sentences."
```

**Ahorro estimado**: 30-40% tokens de output.

**Archivo**: `SystemPromptBuilder.java` - anadir en `appendLevelRules()`

#### 4.2 - Simplificar formato de feedback JSON [HECHO]

**Problema**: El bloque feedback tiene 4 campos con arrays y strings que consumen muchos tokens de output (~80-120 tokens). Ademas, los delimitadores `|||FEEDBACK|||...|||END_FEEDBACK|||` son 6 tokens desperdiciados.

**Solucion**: Formato compacto con delimitador corto y campos abreviados:

```
Actual:   |||FEEDBACK|||{"grammarCorrections":[],"vocabularySuggestions":[],"pronunciationTips":[],"encouragement":""}|||END_FEEDBACK|||
Compacto: <<F>>{"g":[],"v":[],"p":[],"e":""}<<F>>
```

**Ahorro estimado**: 20-30 tokens por turno con feedback.

**Archivos**:
- `SystemPromptBuilder.java` - cambiar `appendFeedbackFormat()`
- `AnthropicAiTutorAdapter.java` - cambiar `FEEDBACK_PATTERN` y `parseResponse()`

#### 4.3 - Reducir max_tokens segun contexto [HECHO]

**Problema**: `max_tokens=300` es fijo, pero el greeting y turnos sin feedback necesitan menos.

**Solucion**: Max tokens dinamico:

| Contexto | max_tokens actual | max_tokens propuesto |
|----------|------------------|---------------------|
| Greeting | 300 | 150 |
| Turno sin feedback | 300 | 200 |
| Turno con feedback | 300 | 300 |
| Summary | 300 | 200 |

**Ahorro estimado**: La AI usa menos tokens cuando el limite es mas bajo.

**Archivo**: `AnthropicAiTutorAdapter.java` - `callClaude()` con parametro maxTokens variable

#### 4.4 - Feedback cada 5 turnos en vez de 3 [HECHO]

**Problema**: Feedback cada 3 turnos genera un bloque JSON extra 3 veces en una conversacion de 10 turnos.

**Solucion**: Aumentar a cada 5 turnos. En una conversacion de 10 turnos: 2 feedbacks en vez de 3.

**Ahorro estimado**: ~80-120 tokens por conversacion.

**Archivo**: `AnthropicAiTutorAdapter.java` - `FEEDBACK_EVERY_N_USER_TURNS = 5`

---

### FASE 5 - Reduccion de Tokens de Input (completada)

#### 5.1 - Comprimir system prompt con abreviaciones [HECHO]

**Problema**: El system prompt actual usa frases completas que se repiten en cada llamada (~200-300 tokens).

**Solucion**: System prompt ultra-compacto estilo "shorthand":

```
Actual (~280 chars):
  "English practice scenario. Role: waiter. Help order food, describe menu, handle requests.
   Stay in character + tutor. Student level: B1.
   B1 rules: moderate vocab (10-15 words), varied tenses + conditionals, gentle grammar corrections
   with brief explanations, open-ended questions, introduce idioms.
   Keep responses concise and natural. No feedback block needed."

Compacto (~160 chars):
  "EN tutor+waiter roleplay. Lvl:B1. Vocab:moderate,10-15w. Tenses:varied+conditionals.
   Fix grammar gently+brief. Open Qs,idioms. Max 3 sent. Concise,natural."
```

**Ahorro estimado**: 40-50% tokens de input en system prompt (que se repite CADA llamada).

**Archivo**: `SystemPromptBuilder.java` - reescribir todo el builder

#### 5.2 - Reducir historial: 8 turnos max, ultimos 4 completos [HECHO]

**Problema**: 10 turnos con ultimos 6 completos sigue siendo mucho contexto.

**Solucion**: Bajar a 8 turnos con ultimos 4 completos y truncar antiguos a 100 chars.

| Parametro | Actual | Propuesto |
|-----------|--------|-----------|
| MAX_CONTEXT_TURNS | 10 | 8 |
| RECENT_TURNS_FULL | 6 | 4 |
| OLD_MESSAGE_MAX_CHARS | 150 | 100 |

**Ahorro estimado**: 15-25% tokens de input en conversaciones largas.

**Archivos**:
- `SendMessageUseCase.java` - `MAX_CONTEXT_TURNS = 8`
- `StreamMessageUseCase.java` - `MAX_CONTEXT_TURNS = 8`
- `AnthropicAiTutorAdapter.java` - constantes
- `AnthropicAiTutorStreamAdapter.java` - constantes

#### 5.3 - Condensar historial antiguo en resumen inline [HECHO]

**Problema**: Los mensajes antiguos del assistant (truncados) aportan poco contexto y consumen tokens.

**Solucion**: En turnos antiguos (antes del cutoff), enviar solo los mensajes del `user` resumidos. Los mensajes del assistant antiguos se omiten.

**Ahorro estimado**: 50% tokens en la parte truncada del historial.

**Archivo**: `buildMessages()` en ambos adapters

---

### FASE 6 - Optimizaciones Avanzadas (completada)

#### 6.1 - Cache de prompts por nivel (no solo greetings) [HECHO]

**Problema**: El system prompt se reconstruye en cada llamada aunque es identico para mismo nivel+topic+includeFeedback.

**Solucion**: Cache del string del system prompt (ya es inmutable por combinacion de parametros). Esto no ahorra tokens de API, pero ahorra CPU y reduce latencia.

**Archivo**: `SystemPromptBuilder.java` - cache estatico

#### 6.2 - Deteccion de conversacion finalizada [HECHO]

**Problema**: Si el usuario dice "bye/thanks/goodbye", la AI responde y luego el usuario puede seguir mandando mensajes innecesarios.

**Solucion**: Detectar patrones de despedida y sugerir cerrar la conversacion (evita turnos extra).

**Ahorro estimado**: 1-3 turnos menos por conversacion en media.

#### 6.3 - Resumen de conversacion como contexto [HECHO]

**Problema**: Enviar 8 turnos de historial consume muchos tokens.

**Solucion**: Cuando hay >5 turnos, generar un resumen compacto de los primeros turnos y enviarlo como un unico mensaje del sistema en vez de los turnos individuales.

```
En vez de enviar turnos 1-4 (4 mensajes ~400 tokens):
  Enviar 1 resumen (~50 tokens): "Discussed restaurant ordering. Student struggles with past tense."
```

**Ahorro estimado**: Hasta 70% tokens de input en conversaciones largas.

**Contrapartida**: Requiere una llamada extra a la API para generar el resumen (pero se puede cachear).

---

## Resumen de Impacto

| Fase | Ahorro input | Ahorro output | Esfuerzo |
|------|-------------|---------------|----------|
| **4 - Output** | - | 30-40% | Bajo |
| **5 - Input** | 30-50% | - | Medio |
| **6 - Avanzadas** | 40-70% | 10-20% | Alto |

### Proyeccion de coste por conversacion (10 turnos)

| Estado | Coste aprox |
|--------|------------|
| Actual (post Fase 1-3) | ~$0.005-0.01 |
| Post Fase 4+5 | ~$0.002-0.004 |
| Post Fase 6 | ~$0.001-0.002 |

---

## Orden de Implementacion Recomendado

**Prioridad 1** (mayor impacto, menor esfuerzo):
1. 4.1 - Limitar longitud de respuesta en prompt
2. 4.3 - Max tokens dinamico
3. 5.1 - Comprimir system prompt
4. 4.4 - Feedback cada 5 turnos

**Prioridad 2** (impacto medio):
5. 4.2 - Formato feedback compacto
6. 5.2 - Reducir historial a 8 turnos
7. 5.3 - Omitir assistant messages antiguos

**Prioridad 3** (mayor esfuerzo):
8. 6.2 - Deteccion de despedida
9. 6.3 - Resumen como contexto
10. 6.1 - Cache de system prompts

---

## Configuracion

Variables de entorno:

```properties
ANTHROPIC_MODEL=claude-haiku-4-5-20251001        # modelo conversacion
ANTHROPIC_MAX_TOKENS=300                          # max tokens conversacion
ANTHROPIC_WRITING_MODEL=claude-haiku-4-5-20251001 # modelo writing
ANTHROPIC_WRITING_MAX_TOKENS=600                  # max tokens writing
```
