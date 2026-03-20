# Fase 5: Flyway Naming + Seed de Vocabulario

## Problema

### Naming de migraciones

La skill `persistence` establece: `V{MAJOR}.{MINOR}.{PATCH}__{module}_{description}.sql`

Actual:
```
V1__create_vocab_entries.sql
V2__create_user_profiles.sql
V3__create_phrases.sql
V4__create_achievements.sql
V4_1__seed_achievements.sql
V5__create_spaced_repetition.sql
V6__create_assessment_tables.sql
V7__create_sessions.sql
V8__create_users.sql
```

Problemas:
- No incluye el nombre del modulo
- No usa versionado semantico
- `V4_1` no sigue la convencion

### Seed de vocabulario

La PWA del frontend tiene 1209 palabras de vocabulario definidas en JSON. No hay migracion de seed para cargarlas en la BD.

## Solucion

### Parte A: Renombrar migraciones (SOLO si la BD esta limpia)

> **IMPORTANTE**: Flyway guarda checksums de las migraciones ya aplicadas. Renombrar migraciones que ya se ejecutaron rompe Flyway. Solo se puede hacer si:
> 1. Se borra la BD y se recrea (`docker compose down -v && docker compose up -d`)
> 2. O se usa `flyway repair` (no recomendado en produccion)
>
> Como el proyecto esta en desarrollo y no hay datos reales, se puede hacer.

Nuevo naming:
```
V1.0.0__vocabulary_create_vocab_entries.sql
V1.1.0__user_create_user_profiles.sql
V1.2.0__phrase_create_phrases.sql
V1.3.0__gamification_create_achievements.sql
V1.3.1__gamification_seed_achievements.sql
V1.4.0__spacedrepetition_create_items.sql
V1.5.0__assessment_create_tables.sql
V1.6.0__session_create_sessions.sql
V1.7.0__auth_create_users.sql
```

### Parte B: Seed de vocabulario

Crear una migracion que inserte las 1209 palabras del vocabulario:

```sql
-- V1.0.1__vocabulary_seed_entries.sql

INSERT INTO vocab_entries (id, en, ipa, es, type, example, level) VALUES
(gen_random_uuid(), 'hello', '/həˈloʊ/', 'hola', 'interjection', 'Hello, how are you?', 'A1'),
(gen_random_uuid(), 'goodbye', '/ɡʊdˈbaɪ/', 'adios', 'interjection', 'Goodbye, see you tomorrow.', 'A1'),
-- ... 1207 filas mas ...
;
```

Para generar el SQL, se leera el JSON de vocabulario de la PWA (`/home/faustinoolivas/dev/proyectos/carmen/untitled/`) y se convertira automaticamente.

## Pasos de ejecucion

1. Parar la app
2. `docker compose down -v` (elimina volumen de datos)
3. Renombrar los ficheros de migracion
4. Crear la migracion de seed de vocabulario leyendo el JSON de la PWA
5. `docker compose up -d`
6. Arrancar la app (Flyway aplica las migraciones con los nuevos nombres)

## Ficheros a modificar

| Fichero | Cambio |
|---------|--------|
| 9 migraciones existentes | Renombrar siguiendo convencion |
| `V1.0.1__vocabulary_seed_entries.sql` | NUEVO - seed de vocabulario |

## Criterio de aceptacion

- Todas las migraciones siguen el formato `V{MAJOR}.{MINOR}.{PATCH}__{module}_{description}.sql`
- La BD contiene las 1209+ palabras de vocabulario despues de migrar
- `flyway info` muestra todas las migraciones como aplicadas
