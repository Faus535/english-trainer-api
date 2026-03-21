# Google Sign-In — Configuración de credenciales

## Fase 0: Google Cloud Console

### 1. Crear o seleccionar proyecto

1. Ir a [Google Cloud Console](https://console.cloud.google.com/)
2. Crear un proyecto nuevo o seleccionar uno existente

### 2. Habilitar Google Identity Services

1. Ir a **APIs & Services > Library**
2. Buscar "Google Identity Services" o "Google Sign-In"
3. Hacer clic en **Enable**

### 3. Configurar pantalla de consentimiento (OAuth Consent Screen)

1. Ir a **APIs & Services > OAuth consent screen**
2. Seleccionar **External** (o Internal si es Google Workspace)
3. Rellenar:
   - App name: `English Trainer`
   - User support email: tu email
   - Authorized domains: tu dominio de producción
4. En **Scopes**, añadir: `email`, `profile`, `openid`
5. Guardar

### 4. Crear credenciales OAuth 2.0

1. Ir a **APIs & Services > Credentials**
2. Hacer clic en **Create Credentials > OAuth Client ID**
3. Tipo: **Web application**
4. Nombre: `English Trainer Web`
5. **Authorized JavaScript origins**:
   - `http://localhost:4200` (desarrollo)
   - `https://tu-dominio.com` (producción)
6. Hacer clic en **Create**
7. Copiar el **Client ID** generado (formato: `xxxx.apps.googleusercontent.com`)

---

## Configuración en desarrollo

Opción A — Variable de entorno:

```bash
export GOOGLE_CLIENT_ID=tu-client-id.apps.googleusercontent.com
```

Opción B — Archivo `.env` (si usas dotenv):

```env
GOOGLE_CLIENT_ID=tu-client-id.apps.googleusercontent.com
```

Opción C — En `application-dev.properties`:

```properties
google.client-id=tu-client-id.apps.googleusercontent.com
```

---

## Configuración en producción (Railway)

1. Ir al dashboard de Railway
2. Seleccionar el servicio `english-trainer-api`
3. Ir a **Variables**
4. Añadir:

| Variable | Valor |
|----------|-------|
| `GOOGLE_CLIENT_ID` | `tu-client-id.apps.googleusercontent.com` |

---

## Verificación

Una vez configurado, probar con curl:

```bash
curl -X POST http://localhost:8081/api/auth/google \
  -H "Content-Type: application/json" \
  -d '{"idToken": "token-obtenido-del-frontend"}'
```

Respuesta esperada (200):

```json
{
  "token": "eyJhbGciOi...",
  "refreshToken": "eyJhbGciOi...",
  "profileId": "uuid",
  "email": "usuario@gmail.com"
}
```

Respuesta con token inválido (401):

```json
{
  "code": "google_auth_error",
  "message": "Invalid Google ID token"
}
```
