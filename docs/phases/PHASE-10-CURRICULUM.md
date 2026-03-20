# Fase 10 — Curriculum y Contenido

## Objetivo

Exponer el plan de estudios de 16 semanas, la estructura de modulos y unidades,
y las sesiones integradoras como API para que el frontend no dependa de
archivos JS embebidos.

## Estado actual

- **Cero codigo** para curriculum
- Frontend tiene todo en `plan-data.js` (600 lineas) y `modules.js` (323 lineas)
- 112 dias de contenido en archivos `.md` bajo `/english/trainer/`

## Datos del frontend

### Plan de 16 semanas (plan-data.js)

4 bloques × 4 semanas:
- **Bloque 1** (sem 1-4): Foundation — foco en listening basico + vocabulario
- **Bloque 2** (sem 5-8): Building — grammar + phrases
- **Bloque 3** (sem 9-12): Expanding — temas intermedios
- **Bloque 4** (sem 13-16): Mastering — consolidacion y produccion

Cada dia tiene: titulo, modulos a trabajar, tipo de actividad, duracion estimada.

### Modulos (modules.js)

5 modulos con unidades por nivel:

| Modulo | A1 | A2 | B1 | B2 | C1 |
|--------|----|----|----|----|-----|
| Listening | 12 units | 12 | 10 | 10 | 8 |
| Vocabulary | 10 units | 10 | 10 | 8 | 8 |
| Grammar | 10 units | 12 | 12 | 10 | 10 |
| Phrases | 8 units | 10 | 10 | 10 | 8 |
| Pronunciation | 10 units | 10 | 10 | 8 | 8 |

Cada unidad tiene: titulo, tipo de ejercicio, contenido de referencia.

### Sesiones integradoras (integrator-sessions.js)

15-18 sesiones tematicas por nivel:
- A1: restaurant, airport, shopping, hotel...
- B1: job interview, doctor visit, debate...
- B2: negotiations, presentations, academic...

## Entidades y Value Objects

### Value Objects (solo lectura, no persisten como aggregates)

```
CurriculumBlock (Value Object)
├── blockNumber: int (1-4)
├── name: String
├── weeks: List<CurriculumWeek>

CurriculumWeek (Value Object)
├── weekNumber: int (1-16)
├── days: List<CurriculumDay>

CurriculumDay (Value Object)
├── dayNumber: int (1-112)
├── title: String
├── modules: List<String>
├── activityType: String
├── estimatedMinutes: int

ModuleDefinition (Value Object)
├── name: String
├── levels: Map<String, List<UnitDefinition>>

UnitDefinition (Value Object)
├── index: int
├── title: String
├── type: String (reduced-forms, dictation, shadowing, grammar-lesson, etc.)
├── contentRef: String (referencia al contenido markdown)

IntegratorSession (Value Object)
├── theme: String
├── level: String
├── description: String
├── modules: List<String> (modulos que combina)
├── exercises: List<String>
```

## Decisiones de diseno

### Opcion A: Contenido en BD (recomendada para escalabilidad)

- Migrar plan-data, modules y integrator-sessions a tablas
- Permite editar curriculum sin redesplegar
- Mayor complejidad de migraciones

### Opcion B: Contenido en archivos estaticos (recomendada para MVP)

- Leer de archivos JSON/YAML en resources/
- Exponer via endpoints read-only
- Mas simple, menor mantenimiento de BD

**Recomendacion**: Opcion B para esta fase. Migrar a BD si se necesita edicion dinamica.

## Estructura de archivos (Opcion B)

```
src/main/resources/curriculum/
├── plan.json          — 112 dias organizados en bloques/semanas
├── modules.json       — 5 modulos con unidades por nivel
└── integrators.json   — sesiones integradoras por nivel
```

## Use Cases

| Use Case | Tipo | Descripcion |
|----------|------|-------------|
| `GetCurriculumPlanUseCase` | Query | Obtener plan completo o un bloque especifico |
| `GetModuleDefinitionUseCase` | Query | Obtener definicion de un modulo con sus unidades por nivel |
| `GetAllModuleDefinitionsUseCase` | Query | Obtener todos los modulos |
| `GetIntegratorSessionsUseCase` | Query | Obtener sesiones integradoras por nivel |

## Endpoints

| Metodo | Ruta | Use Case | Response |
|--------|------|----------|----------|
| GET | `/api/curriculum/plan` | GetCurriculumPlanUseCase | List<CurriculumBlockResponse> |
| GET | `/api/curriculum/plan/blocks/{block}` | GetCurriculumPlanUseCase | CurriculumBlockResponse |
| GET | `/api/curriculum/modules` | GetAllModuleDefinitionsUseCase | List<ModuleDefinitionResponse> |
| GET | `/api/curriculum/modules/{module}` | GetModuleDefinitionUseCase | ModuleDefinitionResponse |
| GET | `/api/curriculum/integrators?level=b1` | GetIntegratorSessionsUseCase | List<IntegratorSessionResponse> |

## Criterios de aceptacion

- [ ] Plan completo de 112 dias accesible via API
- [ ] Filtro por bloque (1-4) funcional
- [ ] 5 modulos con todas sus unidades por nivel
- [ ] Sesiones integradoras filtradas por nivel
- [ ] Archivos JSON en resources/ validados al arrancar la aplicacion
- [ ] Respuestas cacheables (contenido estatico, agregar Cache-Control headers)
- [ ] Tests de integracion para cada endpoint
- [ ] Contenido coincide con lo que tiene el frontend actual
