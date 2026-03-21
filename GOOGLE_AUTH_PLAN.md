# Google Sign-In — Plan de implementación Backend

## Contexto

Añadir autenticación con Google al sistema actual de email+password. El objetivo es que el endpoint `POST /api/auth/google` reciba un ID token de Google, lo valide, y devuelva el mismo `AuthResponse` que ya usan `/auth/login` y `/auth/register`. El frontend se encarga de obtener el ID token mediante Google Identity Services.

---

## Fase 0: Configuración Google Cloud Console

- [ ] Crear proyecto en Google Cloud Console (o usar uno existente)
- [ ] Habilitar "Google Identity Services"
- [ ] Crear credenciales OAuth 2.0 (tipo "Web application")
  - Authorized JavaScript origins: `http://localhost:4200`, dominio de producción
- [ ] Obtener el **Client ID** (`xxxx.apps.googleusercontent.com`)
- [ ] Guardar el Client ID como propiedad en `application.properties`:
  ```properties
  google.client-id=xxxx.apps.googleusercontent.com
  ```

---

## Fase 1: Dependencias

- [ ] Añadir la librería de Google para validar ID tokens en `build.gradle`:
  ```groovy
  implementation 'com.google.api-client:google-api-client:2.7.2'
  ```
  Esta librería incluye `GoogleIdTokenVerifier` que valida firma, audience y expiración del token.

---

## Fase 2: Dominio — Preparar el modelo para múltiples proveedores

### 2.1 Migración de base de datos

- [ ] Crear migración Flyway `V1.X.0__auth_add_provider.sql`:
  ```sql
  ALTER TABLE users ADD COLUMN auth_provider VARCHAR(20) NOT NULL DEFAULT 'LOCAL';
  ALTER TABLE users ALTER COLUMN password_hash DROP NOT NULL;
  ```
  - `auth_provider`: `LOCAL` (email+password) o `GOOGLE`
  - `password_hash` pasa a ser nullable (los usuarios de Google no tienen contraseña)

### 2.2 Actualizar AuthUser (Aggregate Root)

- [ ] Añadir campo `authProvider` (String: `"LOCAL"` | `"GOOGLE"`) a `AuthUser.java`
- [ ] Hacer `passwordHash` nullable (puede ser `null` para usuarios de Google)
- [ ] Añadir factory method:
  ```java
  public static AuthUser createFromGoogle(String email, UserProfileId profileId) {
      return new AuthUser(
          AuthUserId.generate(),
          email,
          null,           // sin password
          profileId,
          "USER",
          true,
          "GOOGLE",
          Instant.now(),
          Instant.now()
      );
  }
  ```
- [ ] Actualizar `AuthUser.create()` existente para setear `authProvider = "LOCAL"`
- [ ] Actualizar `AuthUser.reconstitute()` para incluir `authProvider`

### 2.3 Actualizar AuthUserEntity (Persistencia)

- [ ] Añadir campo `authProvider` a `AuthUserEntity.java`
- [ ] Actualizar métodos `toAggregate()` y `fromAggregate()` para mapear el nuevo campo

### 2.4 Nueva excepción de dominio

- [ ] Crear `GoogleAuthException.java` en `auth/domain/error/`:
  ```java
  public class GoogleAuthException extends Exception {
      public GoogleAuthException(String message) {
          super(message);
      }
  }
  ```

---

## Fase 3: Infraestructura — Servicio de validación de Google

- [ ] Crear `GoogleTokenVerifier.java` en `auth/infrastructure/google/`:
  ```java
  @Service
  class GoogleTokenVerifier {

      private final GoogleIdTokenVerifier verifier;

      GoogleTokenVerifier(@Value("${google.client-id}") String clientId) {
          this.verifier = new GoogleIdTokenVerifier.Builder(
              new NetHttpTransport(),
              GsonFactory.getDefaultInstance()
          )
          .setAudience(List.of(clientId))
          .build();
      }

      GoogleUserInfo verify(String idToken) throws GoogleAuthException {
          try {
              GoogleIdToken token = verifier.verify(idToken);
              if (token == null) {
                  throw new GoogleAuthException("Invalid Google ID token");
              }
              GoogleIdToken.Payload payload = token.getPayload();
              return new GoogleUserInfo(
                  payload.getEmail(),
                  (String) payload.get("name"),
                  payload.getEmailVerified()
              );
          } catch (Exception e) {
              throw new GoogleAuthException("Failed to verify Google token: " + e.getMessage());
          }
      }
  }
  ```
- [ ] Crear record `GoogleUserInfo.java` en `auth/infrastructure/google/`:
  ```java
  record GoogleUserInfo(String email, String name, boolean emailVerified) {}
  ```

---

## Fase 4: Aplicación — Use Case de autenticación con Google

- [ ] Crear `GoogleLoginUseCase.java` en `auth/application/`:
  ```java
  @Service
  class GoogleLoginUseCase {

      private final GoogleTokenVerifier googleTokenVerifier;
      private final AuthUserRepository authUserRepository;
      private final UserProfileRepository userProfileRepository;

      // constructor injection

      @Transactional
      AuthUser execute(String idToken) throws GoogleAuthException {
          GoogleUserInfo googleUser = googleTokenVerifier.verify(idToken);

          if (!googleUser.emailVerified()) {
              throw new GoogleAuthException("Google email not verified");
          }

          Optional<AuthUser> existingUser = authUserRepository.findByEmail(googleUser.email());

          if (existingUser.isPresent()) {
              AuthUser user = existingUser.get();
              // Si ya existe con LOCAL, vincular (o rechazar, según decisión de negocio)
              return user;
          }

          // Nuevo usuario: crear perfil + auth user
          UserProfile profile = UserProfile.create(googleUser.email());
          userProfileRepository.save(profile);

          AuthUser newUser = AuthUser.createFromGoogle(googleUser.email(), profile.id());
          return authUserRepository.save(newUser);
      }
  }
  ```

### Decisión de negocio: cuentas duplicadas

- [ ] Definir qué pasa si un usuario LOCAL intenta hacer login con Google (mismo email):
  - **Opción A**: Vincular automáticamente → actualizar `authProvider` a `GOOGLE` (o permitir ambos)
  - **Opción B**: Rechazar → lanzar excepción indicando que ya existe cuenta con email+password
  - **Recomendación**: Opción A, vincular automáticamente. Es la mejor UX.

---

## Fase 5: Infraestructura — Controller

### 5.1 Request DTO

- [ ] Crear `GoogleLoginRequest.java` en `auth/infrastructure/controller/`:
  ```java
  record GoogleLoginRequest(
      @NotBlank(message = "idToken is required")
      String idToken
  ) {}
  ```

### 5.2 Controller

- [ ] Crear `GoogleLoginController.java` en `auth/infrastructure/controller/`:
  ```java
  @RestController
  class GoogleLoginController {

      private final GoogleLoginUseCase googleLoginUseCase;
      private final JwtService jwtService;

      // constructor injection

      @PostMapping("/api/auth/google")
      ResponseEntity<AuthResponse> handle(@Valid @RequestBody GoogleLoginRequest request)
              throws GoogleAuthException {

          AuthUser user = googleLoginUseCase.execute(request.idToken());

          String token = jwtService.generateToken(user);
          String refreshToken = jwtService.generateRefreshToken(user);

          return ResponseEntity.ok(new AuthResponse(
              token,
              refreshToken,
              user.userProfileId().value().toString(),
              user.email()
          ));
      }
  }
  ```

### 5.3 Manejo de errores

- [ ] Añadir handler en `AuthControllerAdvice.java`:
  ```java
  @ExceptionHandler(GoogleAuthException.class)
  ResponseEntity<ErrorResponse> handleGoogleAuth(GoogleAuthException e) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
          .body(new ErrorResponse("google_auth_error", e.getMessage()));
  }
  ```

---

## Fase 6: Configuración de seguridad

- [ ] Actualizar `SecurityConfig.java` para permitir acceso público a `/api/auth/google`:
  ```java
  .requestMatchers("/api/auth/**").permitAll()  // ya cubre /api/auth/google
  ```
  (Si ya tienes `/api/auth/**` como público, no hace falta cambiar nada)

- [ ] Verificar que CORS permite el origen del frontend

---

## Fase 7: Actualizar LoginUserUseCase (login con email+password)

- [ ] Cuando un usuario con `authProvider = "GOOGLE"` intente hacer login con email+password:
  - Si no tiene `passwordHash` → lanzar excepción específica: "Esta cuenta usa Google Sign-In"
  - Crear nueva excepción `AccountUsesGoogleException.java` si se necesita diferenciar del 401 genérico
- [ ] Manejar en `AuthControllerAdvice` con un mensaje claro para el frontend

---

## Fase 8: Testing

### 8.1 Tests unitarios

- [ ] `GoogleTokenVerifierTest`: mock de `GoogleIdTokenVerifier`, verificar que parsea correctamente el payload
- [ ] `GoogleLoginUseCaseTest`:
  - Token válido, usuario nuevo → crea cuenta y devuelve AuthUser
  - Token válido, usuario existente → devuelve AuthUser existente
  - Token inválido → lanza GoogleAuthException
  - Email no verificado → lanza GoogleAuthException

### 8.2 Tests de integración

- [ ] `GoogleAuthIntegrationTest` con Testcontainers:
  - `POST /api/auth/google` con token válido (mock del verifier) → 200 + AuthResponse
  - `POST /api/auth/google` con token inválido → 401
  - `POST /api/auth/google` con email que ya existe como LOCAL → comportamiento esperado según decisión de negocio
  - `POST /api/auth/google` sin body → 422

### 8.3 Test manual

- [ ] Obtener un ID token real de Google (desde el frontend o con OAuth Playground)
- [ ] Probar `POST /api/auth/google` con curl/Postman
- [ ] Verificar que el JWT devuelto funciona en endpoints protegidos

---

## Fase 9: Producción

- [ ] Configurar `google.client-id` en `application-prod.properties` (o variable de entorno)
- [ ] Asegurar que el Client ID de producción tiene los origins correctos
- [ ] Añadir rate limiting a `/api/auth/google` si el resto de auth ya lo tiene
- [ ] Verificar logs: no loguear tokens de Google en producción
- [ ] Desplegar y validar flujo completo con el frontend

---

## Resumen de archivos a crear/modificar

| Acción   | Archivo                                                  |
| -------- | -------------------------------------------------------- |
| Crear    | `db/migration/V1.X.0__auth_add_provider.sql`             |
| Modificar| `AuthUser.java` (añadir authProvider, nullable password) |
| Modificar| `AuthUserEntity.java` (nuevo campo)                      |
| Crear    | `GoogleAuthException.java`                               |
| Crear    | `GoogleTokenVerifier.java`                               |
| Crear    | `GoogleUserInfo.java`                                    |
| Crear    | `GoogleLoginUseCase.java`                                |
| Crear    | `GoogleLoginRequest.java`                                |
| Crear    | `GoogleLoginController.java`                             |
| Modificar| `AuthControllerAdvice.java` (nuevo handler)              |
| Modificar| `LoginUserUseCase.java` (validar provider)               |
| Modificar| `application.properties` (google.client-id)              |
| Modificar| `build.gradle` (dependencia google-api-client)           |
