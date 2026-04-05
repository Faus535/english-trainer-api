# Plan: Alinear Backend con Frontend - Endpoints faltantes y respuesta /auth/me

**Fecha**: 2026-04-05
**Problema**: El frontend hace llamadas a rutas del backend que no existen o devuelven datos incompletos, causando 401/404 en produccion.

---

## Fase 1: Ampliar respuesta de GET /api/auth/me

**Objetivo**: El frontend espera `UserAccountResponse` con campos `name`, `avatarUrl`, `provider`, `createdAt` pero el backend solo devuelve `userId`, `email`, `profileId`, `role`.

### Archivos a modificar

| Archivo | Cambio |
|---------|--------|
| `auth/infrastructure/controller/GetCurrentUserController.java` | Ampliar `CurrentUserResponse` con campos `name`, `avatarUrl`, `provider`, `createdAt` |
| `auth/application/GetCurrentUserUseCase.java` | Devolver datos adicionales del AuthUser |

### Detalle

El `AuthUser` ya tiene `authProvider` y `createdAt`. Los campos `name` y `avatarUrl` no existen en el dominio auth. Opciones:

**Opcion A (recomendada)**: Devolver lo que el backend tiene y adaptar el frontend:
```java
record CurrentUserResponse(String userId, String email, String profileId, String role,
                           String provider, String createdAt) {}
```
- `provider` = `authUser.authProvider().toLowerCase()` → "local" | "google"
- `createdAt` = `authUser.createdAt().toString()`
- `name` y `avatarUrl` → no existen, el frontend debe adaptarse

**Opcion B**: Añadir `name` y `avatarUrl` al dominio Auth (mas invasivo, requiere migracion SQL).

### Tests
- Verificar que GET /api/auth/me devuelve los nuevos campos
- Test unitario del use case

---

## Fase 2: Crear PUT /api/auth/change-password (ya existe, verificar path)

**Estado**: El backend ya tiene `PUT /api/auth/change-password` pero el frontend llama a `PUT /api/auth/password`.

**Accion backend**: Ninguna. El endpoint ya existe. El cambio es solo en frontend.

---

## Fase 3: Verificar DELETE account

**Estado**: El backend tiene `DELETE /api/profiles/{id}` pero el frontend llama a `DELETE /api/auth/account` con body `{ password }`.

**Opciones**:
- **Opcion A**: Crear `DELETE /api/auth/account` en el backend que valide password y elimine auth + profile
- **Opcion B**: Adaptar frontend a usar `DELETE /api/profiles/{id}`

**Recomendacion**: Opcion A es mejor UX (validar password antes de borrar). Requiere:

| Archivo | Cambio |
|---------|--------|
| `auth/application/DeleteAccountUseCase.java` | Nuevo use case: validar password, eliminar AuthUser + UserProfile |
| `auth/infrastructure/controller/DeleteAccountController.java` | Nuevo controller: `DELETE /api/auth/account` |
| `auth/domain/AuthUserRepository.java` | Añadir metodo delete si no existe |

### Tests
- Test que valida password correcta → elimina cuenta
- Test que valida password incorrecta → 400

---

## Fase 4: Alinear endpoints de Review

**Estado actual backend**:
- `GET /api/profiles/{userId}/review/queue` (front llama `/reviews/due`)
- `GET /api/profiles/{userId}/review/stats` (front llama `/reviews/stats`)
- `POST /api/profiles/{userId}/review/items/{itemId}/result` (front llama `PUT /reviews/{itemId}/complete`)

**Accion backend**: Ninguna. Los endpoints existen. Solo hay que alinear el frontend.

---

## Fase 5: Eliminar endpoint GET /api/immerse/content/suggested (o crearlo)

**Estado**: El frontend llama a `GET /api/immerse/content/suggested` pero no existe en el backend. Se usa en `immerse-hub.ts`.

**Opciones**:
- **Opcion A**: Eliminar la llamada del frontend y usar el `suggestedAction` del Home endpoint
- **Opcion B**: Crear el endpoint en backend

**Recomendacion**: Opcion A - el Home endpoint ya sugiere actividades. No necesitamos duplicar logica.

---

## Resumen de acciones Backend

| # | Accion | Prioridad |
|---|--------|-----------|
| 1 | Ampliar CurrentUserResponse con `provider`, `createdAt` | Alta |
| 2 | Crear DELETE /api/auth/account (validar password + eliminar) | Alta |
| 3 | Nada para review (frontend se adapta) | - |
| 4 | Nada para immerse suggested (frontend se adapta) | - |
| 5 | Nada para vocab/progress/assessment/learning-path/admin (legacy, frontend los elimina) | - |
