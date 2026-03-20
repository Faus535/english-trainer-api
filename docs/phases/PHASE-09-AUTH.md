# Fase 9 — Autenticacion y Autorizacion

## Objetivo

Implementar autenticacion con JWT para que multiples usuarios puedan usar la API
de forma segura, cada uno accediendo solo a sus propios datos.

## Estado actual

- `SecurityConfig` existe con `permitAll()` en todos los endpoints
- `SessionCreationPolicy.STATELESS` — ya preparado para JWT
- CORS configurado para `localhost:4200`
- **Sin**: entidad User, login, registro, JWT, tenant isolation

## Entidades y Value Objects

### Nuevo Aggregate: `User`

```
User (Aggregate Root)
├── UserId (Value Object — UUID)
├── email: String (unique)
├── passwordHash: String (BCrypt)
├── userProfileId: UserProfileId (1:1 con UserProfile)
├── role: UserRole (Value Object — USER, ADMIN)
├── active: boolean
├── createdAt: Instant
└── updatedAt: Instant
```

### Value Objects

- `UserId` — UUID wrapper
- `UserRole` — enum (USER, ADMIN)
- `JwtToken` — record con token string y expiracion

## Migraciones

### V8__create_users.sql

```sql
CREATE TABLE users (
    id              UUID PRIMARY KEY,
    email           VARCHAR(255) NOT NULL UNIQUE,
    password_hash   VARCHAR(255) NOT NULL,
    user_profile_id UUID REFERENCES user_profiles(id),
    role            VARCHAR(20) NOT NULL DEFAULT 'USER',
    active          BOOLEAN NOT NULL DEFAULT TRUE,
    created_at      TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE UNIQUE INDEX idx_users_email ON users(email);
```

## Dependencias nuevas (build.gradle)

```gradle
implementation 'io.jsonwebtoken:jjwt-api:0.12.6'
runtimeOnly 'io.jsonwebtoken:jjwt-impl:0.12.6'
runtimeOnly 'io.jsonwebtoken:jjwt-jackson:0.12.6'
```

## Componentes de infraestructura

### JwtService

- `generateToken(User user)` → String (JWT con userId, email, role, profileId)
- `validateToken(String token)` → Claims
- `getUserIdFromToken(String token)` → UUID

### JwtAuthenticationFilter

- `OncePerRequestFilter` que extrae JWT del header `Authorization: Bearer <token>`
- Valida token y establece `SecurityContext`

### SecurityConfig (actualizar)

- Rutas publicas: `/api/auth/**`, `/h2-console/**`, `/actuator/**`
- Rutas protegidas: `/api/**` requiere autenticacion
- Agregar `JwtAuthenticationFilter` antes de `UsernamePasswordAuthenticationFilter`

## Use Cases

| Use Case | Tipo | Descripcion |
|----------|------|-------------|
| `RegisterUserUseCase` | Command | Crear usuario + perfil asociado, devolver JWT |
| `LoginUserUseCase` | Command | Autenticar con email/password, devolver JWT |
| `RefreshTokenUseCase` | Command | Generar nuevo JWT a partir de refresh token |
| `GetCurrentUserUseCase` | Query | Obtener usuario autenticado desde SecurityContext |

## Endpoints

| Metodo | Ruta | Use Case | Request/Response |
|--------|------|----------|-----------------|
| POST | `/api/auth/register` | RegisterUserUseCase | Request: `{ "email": "...", "password": "..." }` Response: `{ "token": "...", "profileId": "..." }` |
| POST | `/api/auth/login` | LoginUserUseCase | Request: `{ "email": "...", "password": "..." }` Response: `{ "token": "...", "profileId": "..." }` |
| POST | `/api/auth/refresh` | RefreshTokenUseCase | Request: `{ "refreshToken": "..." }` Response: `{ "token": "..." }` |
| GET | `/api/auth/me` | GetCurrentUserUseCase | Response: `{ "userId": "...", "email": "...", "profileId": "...", "role": "USER" }` |

## Tenant Isolation

Una vez implementada la auth, **TODOS** los endpoints existentes deben:

1. Extraer `userId` del JWT (no del path parameter)
2. Verificar que el recurso pertenece al usuario autenticado
3. Devolver 403 si intenta acceder a datos de otro usuario

### Cambios en controllers existentes

- Eliminar `{uid}` de los paths donde sea posible → usar `/api/profiles/me/...`
- O mantener `{uid}` pero validar que coincide con el usuario del JWT
- Agregar `@AuthenticationPrincipal` a los controllers

## Criterios de aceptacion

- [ ] Registro crea User + UserProfile en una transaccion
- [ ] Password hasheado con BCrypt (nunca en texto plano)
- [ ] JWT con expiracion configurable (ej: 24h)
- [ ] Refresh token con expiracion mas larga (ej: 7 dias)
- [ ] Login devuelve 401 con credenciales incorrectas
- [ ] Registro devuelve 409 si email ya existe
- [ ] Todos los endpoints protegidos devuelven 401 sin token
- [ ] Tenant isolation: usuario A no puede ver datos de usuario B (403)
- [ ] CORS actualizado para dominio de produccion
- [ ] Tests de integracion para flujo completo de auth
- [ ] Tests para tenant isolation (intentar acceder a recurso ajeno)
