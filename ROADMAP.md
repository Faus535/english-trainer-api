# English Trainer API - Roadmap

## Estado Actual

El backend cuenta con 13 modulos, 56 endpoints y 51 use cases implementados:
- Auth (login, registro, Google OAuth, JWT)
- User Profiles (CRUD, XP, niveles por modulo)
- Activity (racha, calendario)
- Sessions (generacion, historial)
- Vocabulary y Phrases (CRUD, seed data)
- Assessment (level test, mini test)
- Module Progress (progreso por unidad)
- Spaced Repetition (cola de repaso, algoritmo SM-2)
- Gamification (XP, logros, niveles)
- Conversations (tutor IA con Anthropic)
- Curriculum (plan de estudio, definiciones de modulos)

---

## Fase 1 - Seguridad y Gestion de Cuenta

**Objetivo**: Cerrar gaps de seguridad y dar al usuario control sobre su cuenta.

- [x] `PUT /api/auth/change-password` - Cambio de contrasena (usuario autenticado, requiere contrasena actual)
- [x] Activar autenticacion obligatoria en `/api/**` (quitar el `permitAll` del `SecurityConfig`)
- [x] Validar que cada usuario solo accede a sus propios recursos (authorization por profileId)
- [x] Rate limiting en endpoints de auth (login, registro) para prevenir brute force
- [x] Expirar refresh tokens en BD (tabla `refresh_tokens` con revocacion)

---

## Fase 2 - Recuperacion de Contrasena

**Objetivo**: Flujo completo de "olvide mi contrasena" via email.

- [x] Configurar servicio de email (Spring Mail + proveedor SMTP: SendGrid, Mailgun o similar)
- [x] `POST /api/auth/forgot-password` - Genera token temporal y envia email con enlace
- [x] `POST /api/auth/reset-password` - Valida token y actualiza contrasena
- [x] Migracion: tabla `password_reset_tokens` (token, user_id, expires_at, used)
- [x] Template de email para reset de contrasena
- [x] Limitar intentos de forgot-password por email (anti-spam)

---

## Fase 3 - Mejoras del Tutor IA

**Objetivo**: Enriquecer las conversaciones con IA.

- [x] Streaming de respuestas via SSE (Server-Sent Events) para respuesta progresiva
- [x] Endpoint de estadisticas de conversacion (errores frecuentes, progreso, temas)
- [x] Sugerencia de temas basada en historial y debilidades del usuario
- [x] Guardar metricas por conversacion (precision gramatical, vocabulario usado, etc.)
- [x] Limite configurable de conversaciones por dia/semana (freemium)

---

## Fase 4 - Modulos de Aprendizaje: Reading y Writing

**Objetivo**: Ampliar las habilidades cubiertas mas alla de speaking/listening.

### Reading
- [x] Modelo de dominio: `ReadingPassage` (texto, nivel, preguntas de comprension)
- [x] Seed de textos por nivel (A1-C1)
- [x] `GET /api/reading/passages` - Obtener textos por nivel
- [x] `POST /api/profiles/{userId}/reading/submit` - Evaluar respuestas de comprension
- [x] Integracion con spaced repetition (vocabulario nuevo encontrado en textos)

### Writing
- [x] Modelo de dominio: `WritingExercise` (prompt, nivel, criterios de evaluacion)
- [x] `POST /api/profiles/{userId}/writing/submit` - Enviar texto escrito
- [x] Evaluacion con IA (Anthropic): gramatica, coherencia, vocabulario, nivel
- [x] `GET /api/profiles/{userId}/writing/history` - Historial de ejercicios
- [x] Feedback detallado con correcciones y sugerencias

---

## Fase 5 - Analytics y Metricas

**Objetivo**: Dashboard de progreso detallado para el usuario.

- [x] `GET /api/profiles/{userId}/analytics/summary` - Resumen general (tiempo total, sesiones, precision)
- [x] `GET /api/profiles/{userId}/analytics/progress` - Evolucion de nivel por modulo a lo largo del tiempo
- [x] `GET /api/profiles/{userId}/analytics/weaknesses` - Areas debiles detectadas
- [x] `GET /api/profiles/{userId}/analytics/activity-heatmap` - Mapa de calor de actividad
- [x] Almacenar historico de niveles (migracion: tabla `level_history`)

---

## Fase 6 - Notificaciones

**Objetivo**: Mantener al usuario enganchado con recordatorios.

- [x] Servicio de notificaciones push (Web Push API / Firebase Cloud Messaging)
- [x] Recordatorio diario si no ha practicado
- [x] Alerta de racha en peligro
- [x] Notificacion de items pendientes en repaso espaciado
- [x] Preferencias de notificacion por usuario (`notification_preferences`)

---

## Fase 7 - Admin y Contenido

**Objetivo**: Panel de administracion para gestionar contenido sin tocar BD.

- [x] Rol ADMIN en auth (ya existe campo `role` en `users`)
- [x] CRUD de vocabulario desde admin
- [x] CRUD de frases desde admin
- [x] CRUD de textos de reading
- [x] CRUD de ejercicios de writing
- [x] Dashboard admin: metricas de uso (usuarios activos, sesiones/dia, etc.)

---

## Fase 8 - Optimizacion y Escalabilidad

**Objetivo**: Preparar para mas usuarios y mejorar rendimiento.

- [x] Cache con Redis (vocabulario, frases, curriculum - datos que cambian poco)
- [x] Paginacion en endpoints que devuelven listas (historial, conversaciones, etc.)
- [x] Indices en BD para queries frecuentes
- [x] Health checks detallados (BD, Anthropic API, Redis)
- [x] Metricas con Micrometer + Prometheus
- [x] Documentacion API con SpringDoc/OpenAPI
