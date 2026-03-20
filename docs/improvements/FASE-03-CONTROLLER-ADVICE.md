# Fase 3: ControllerAdvice por Modulo

## Problema

La skill `api-design` establece:
- *"One `{Aggregate}ControllerAdvice` per module"*
- *"Package-private visibility"*

Actualmente existe un unico `GlobalControllerAdvice` (publico) en `shared/infrastructure/error/` que maneja todas las excepciones. Esto viola dos reglas:
1. Deberia haber un ControllerAdvice por modulo
2. Deberia ser package-private

Ademas, `GlobalControllerAdvice` importa `InvalidCredentialsException` del modulo `auth`, creando un acoplamiento entre `shared` y `auth`.

## Solucion

### Paso 1: Mantener GlobalControllerAdvice solo para excepciones compartidas

```java
// shared/infrastructure/error/GlobalControllerAdvice.java
@RestControllerAdvice
class GlobalControllerAdvice {

    record ApiError(String code, String message) {}

    @ExceptionHandler(NotFoundException.class)
    ResponseEntity<ApiError> handleNotFound(NotFoundException ex) { ... }

    @ExceptionHandler(AlreadyExistsException.class)
    ResponseEntity<ApiError> handleAlreadyExists(AlreadyExistsException ex) { ... }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    ResponseEntity<ApiError> handleValidation(MethodArgumentNotValidException ex) { ... }

    @ExceptionHandler(InvalidValueException.class)
    ResponseEntity<ApiError> handleInvalidValue(InvalidValueException ex) { ... }
}
```

### Paso 2: Crear ControllerAdvice por modulo

```java
// auth/infrastructure/controller/AuthControllerAdvice.java
@RestControllerAdvice(basePackages = "com.faus535.englishtrainer.auth")
class AuthControllerAdvice {

    record ApiError(String code, String message) {}

    @ExceptionHandler(InvalidCredentialsException.class)
    ResponseEntity<ApiError> handleInvalidCredentials(InvalidCredentialsException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new ApiError("unauthorized", ex.getMessage()));
    }
}
```

```java
// user/infrastructure/controller/UserProfileControllerAdvice.java
@RestControllerAdvice(basePackages = "com.faus535.englishtrainer.user")
class UserProfileControllerAdvice {

    record ApiError(String code, String message) {}

    @ExceptionHandler(UserProfileException.class)
    ResponseEntity<ApiError> handleUserProfileError(UserProfileException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ApiError("user_profile_error", ex.getMessage()));
    }
}
```

### Paso 3: Repetir para cada modulo que tenga excepciones propias

Modulos que necesitan ControllerAdvice propio (depende de la Fase 1):
- `auth` — InvalidCredentialsException
- `user` — UserProfileException y subclases
- `session` — SessionException y subclases
- `moduleprogress` — ModuleProgressException y subclases
- `assessment` — AssessmentException y subclases (si aplica)

Modulos que NO necesitan ControllerAdvice propio (solo usan excepciones de `shared`):
- `vocabulary`, `phrase`, `activity`, `gamification`, `spacedrepetition`, `curriculum`

## Ficheros a modificar

| Fichero | Cambio |
|---------|--------|
| `shared/infrastructure/error/GlobalControllerAdvice.java` | Eliminar handlers de modulos, hacer package-private |
| `auth/infrastructure/controller/AuthControllerAdvice.java` | NUEVO |
| `user/infrastructure/controller/UserProfileControllerAdvice.java` | NUEVO |
| `session/infrastructure/controller/SessionControllerAdvice.java` | NUEVO |
| `moduleprogress/infrastructure/controller/ModuleProgressControllerAdvice.java` | NUEVO |

## Depende de

- **Fase 1**: Las excepciones especificas de cada modulo deben existir antes de crear los ControllerAdvice que las mapean.

## Criterio de aceptacion

- `GlobalControllerAdvice` no importa clases de ningun modulo (solo `shared`)
- `GlobalControllerAdvice` es package-private
- Cada modulo con excepciones propias tiene su ControllerAdvice
- Cada ControllerAdvice usa `basePackages` para limitar su scope
- Los errores HTTP se devuelven correctamente para cada tipo de excepcion
