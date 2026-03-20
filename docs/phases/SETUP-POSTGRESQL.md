# Setup PostgreSQL — Plan de implementacion

## Objetivo

Migrar de H2 (base de datos en memoria) a PostgreSQL como unica base de datos
en desarrollo y produccion. Esto garantiza que lo que pruebas en local es
exactamente lo que correra en produccion.

## Por que este cambio

- **H2 no es PostgreSQL**: hay diferencias en tipos de datos, funciones SQL y
  comportamiento de JSON que pueden causar bugs que solo aparecen en produccion
- **Flyway ya esta configurado**: las migraciones se ejecutaran igual en PostgreSQL
- **El codigo Java no cambia**: Spring Data JPA abstrae la base de datos, solo
  cambian las properties de conexion
- **Preparacion para publicar**: cuando despliegues en Railway, ya estara todo probado
  contra PostgreSQL real

## Fases

### Fase 1 — Levantar PostgreSQL con Docker

**Que**: Crear el contenedor de PostgreSQL en un puerto que no colisione con los
contenedores corporativos de carmen.

**Por que**: Necesitamos una instancia de PostgreSQL corriendo para que la app se
conecte. Puerto 45432 para evitar conflictos con los existentes (5432, 5433, 25432, 35432).

**Que se hace**:
- Lanzar contenedor `postgres-english` en puerto 45432
- Verificar que arranca y acepta conexiones

### Fase 2 — Configurar perfiles de Spring

**Que**: Crear un sistema de perfiles (`dev` y `prod`) para que la app sepa a que
base de datos conectarse segun el entorno.

**Por que**: En desarrollo conectas a tu Docker local (localhost:45432). En produccion
conectaras a Railway. Separar la configuracion evita hardcodear credenciales y permite
cambiar de entorno sin tocar codigo.

**Ficheros**:

| Fichero | Proposito |
|---------|-----------|
| `application.properties` | Config comun (Flyway, JPA, servidor) |
| `application-dev.properties` | Conexion a PostgreSQL local (Docker, puerto 45432) |
| `application-prod.properties` | Conexion a PostgreSQL en produccion (Railway, variables de entorno) |

**Que se hace**:
- Mover la config de base de datos de `application.properties` a los perfiles
- Configurar `dev` como perfil activo por defecto
- Usar variables de entorno en `prod` para no exponer credenciales

### Fase 3 — Eliminar H2

**Que**: Quitar la dependencia de H2 del proyecto.

**Por que**: Si mantenemos H2 disponible, existe el riesgo de usarlo por error.
Trabajar solo con PostgreSQL fuerza a que todo se pruebe contra la BD real.

**Que se hace**:
- Eliminar `runtimeOnly 'com.h2database:h2'` de `build.gradle`
- Eliminar las properties de H2 console
- Mantener H2 solo como `testRuntimeOnly` si queremos tests rapidos (opcional)

### Fase 4 — Verificar migraciones y arranque

**Que**: Arrancar la app y confirmar que Flyway ejecuta las 9 migraciones contra
PostgreSQL y la aplicacion arranca sin errores.

**Por que**: Algunas sentencias SQL pueden no ser compatibles entre H2 y PostgreSQL.
Si hay errores, los corregimos ahora.

**Que se hace**:
- Ejecutar la app con perfil `dev`
- Verificar que las 9 migraciones se aplican
- Verificar que los endpoints responden (health check basico)
- Corregir SQL incompatible si lo hay
