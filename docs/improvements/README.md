# Plan de Mejoras Arquitectonicas

Fases de mejora para alinear el codigo con las convenciones definidas en `.claude/plugins/s2-backend/skills/`.

## Orden de ejecucion

```
FASE 1 ─── Checked Exceptions (dominio puro, sin RuntimeException)
   │
FASE 2 ─── JPA Entities (Persistable<UUID>, @Version, fromAggregate/toAggregate)
   │
FASE 3 ─── ControllerAdvice por modulo (sustituir GlobalControllerAdvice)
   │
FASE 4 ─── Domain Events (activar registerEvent en aggregates)
   │
FASE 5 ─── Flyway naming + seed vocabulario
   │
FASE 6 ─── Tests (Object Mothers, In-Memory Repos, unit + integration)
```

## Dependencias

| Fase | Depende de | Razon |
|------|-----------|-------|
| 1 | - | Base: el dominio no debe lanzar RuntimeException |
| 2 | - | Independiente: solo toca infraestructura |
| 3 | 1 | Los ControllerAdvice por modulo mapean las nuevas checked exceptions |
| 4 | - | Independiente: solo annade eventos a los aggregates |
| 5 | - | Independiente: solo Flyway |
| 6 | 1, 2 | Los tests validan el nuevo comportamiento |

## Resumen de impacto

| Fase | Ficheros afectados | Riesgo |
|------|-------------------|--------|
| 1 | ~30 (VOs, aggregates, use cases, controllers) | Medio - cambia firmas de metodos |
| 2 | ~12 entities + ~12 adapters | Bajo - solo infraestructura |
| 3 | ~12 nuevos + 1 eliminado | Bajo - solo manejo de errores HTTP |
| 4 | ~12 aggregates + eventos nuevos | Bajo - aditivo |
| 5 | ~10 migraciones renombradas + 1 seed nueva | Bajo - requiere BD limpia |
| 6 | ~50+ ficheros nuevos de test | Nulo - solo tests |
