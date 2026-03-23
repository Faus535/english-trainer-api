# Fix: POST /api/writing/submissions devuelve 403

## Problema

El frontend llama a `POST /api/writing/submissions` pero ese endpoint **no existe** en el backend. El backend tiene `POST /api/profiles/{userId}/writing/submit`.

Spring Security devuelve **403** para endpoints autenticados que no existen.

Ademas hay diferencias en el body:
- **Frontend** envia: `{ exerciseId: string, content: string }`
- **Backend** espera: `{ exerciseId: UUID, text: string }` + userId en la URL

El frontend no incluye `userId` en la URL ni en el body; lo obtiene del JWT token.

## Solucion

### Fase 1: Backend - Crear endpoint que coincida con el frontend

Crear un nuevo controller `POST /api/writing/submissions` que:
1. Acepte `{ exerciseId: string, content: string }`
2. Extraiga el `userId` del JWT (Authentication principal)
3. Mapee `content` a `text` al delegar al use case existente
4. Devuelva la respuesta en el formato que espera el frontend:
   `{ id, exerciseId, score, corrections, suggestions, summary, submittedAt }`

**Archivo a crear:**
- `writing/infrastructure/controller/SubmitWritingSubmissionController.java`

**Archivo a revisar:**
- `writing/application/SubmitWritingUseCase.java` - Verificar que el response incluye corrections, suggestions, summary
- `writing/domain/WritingFeedback.java` - Verificar estructura del feedback

### Fase 2: Verificar respuesta del use case

El frontend espera un `WritingFeedbackResponse` con:
```
{ id, exerciseId, score, corrections: [{original, corrected, type, explanation}], suggestions: [], summary, submittedAt }
```

Verificar que `WritingSubmission` y `WritingFeedback` tienen todos estos campos. Si faltan, adaptar el response en el controller.

## Orden de ejecucion

```
Fase 1 (Crear endpoint) ← Fix principal, resuelve el 403
Fase 2 (Verificar response) ← Asegurar que la respuesta es compatible
```

## Nota

No se necesitan cambios en el frontend. La URL que usa (`/api/writing/submissions`) es correcta y RESTful. El userId se debe extraer del JWT en el backend.
