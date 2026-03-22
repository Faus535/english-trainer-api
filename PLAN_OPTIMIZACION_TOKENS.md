# Plan de Optimización de Consumo de Tokens - English Trainer API

## Estado Actual (post-optimización)

| Parámetro | Antes | Después |
|-----------|-------|---------|
| Modelo conversación | `claude-sonnet-4` | `claude-haiku-4-5` (~10-20x más barato) |
| Modelo writing | `claude-sonnet-4` | `claude-haiku-4-5` (configurable por separado) |
| Max tokens conversación | 500 | 300 |
| Max tokens writing | 800 | 600 |
| Historial enviado | 30 turnos | 10 turnos |
| Mensajes antiguos | Completos | Truncados a 150 chars |
| System prompt | Largo (~800 chars) | Compacto (~300 chars) |
| Prompt caching | No | Sí (cache_control ephemeral) |
| Feedback JSON | Cada mensaje | Cada 3 turnos de usuario |
| Greeting cache | No | Sí (in-memory por nivel+topic) |

## Fases Implementadas

### FASE 1 - Quick Wins
- [x] **1.1** Modelo por defecto cambiado a Haiku 4.5
- [x] **1.2** MAX_CONTEXT_TURNS reducido de 30 a 10
- [x] **1.3** Prompt caching activado (header `anthropic-beta` + `cache_control`)

### FASE 2 - Optimización de Prompts
- [x] **2.1** System prompts acortados (~60% menos texto)
- [x] **2.2** Max tokens reducidos: 300 conversación, 600 writing
- [x] **2.3** Feedback JSON solo cada 3 turnos de usuario

### FASE 3 - Arquitectura
- [x] **3.1** Mensajes antiguos truncados a 150 chars (últimos 6 completos)
- [x] **3.2** Cache in-memory del greeting por nivel+topic
- [ ] **3.3** Tool use para writing (pendiente - requiere más análisis)

## Configuración

Variables de entorno para ajustar:

```properties
ANTHROPIC_MODEL=claude-haiku-4-5-20251001        # modelo conversación
ANTHROPIC_MAX_TOKENS=300                          # max tokens conversación
ANTHROPIC_WRITING_MODEL=claude-haiku-4-5-20251001 # modelo writing
ANTHROPIC_WRITING_MAX_TOKENS=600                  # max tokens writing
```

## Estimación de Ahorro

| Escenario | Coste aprox por conversación (10 turnos) |
|-----------|------------------------------------------|
| Antes (Sonnet, 30 turns, sin cache) | ~$0.12 |
| Después (Haiku, optimizado) | ~$0.005-0.01 |
| **Ahorro** | **~90-95%** |
