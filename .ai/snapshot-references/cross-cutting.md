# Cross-cutting Snapshot

## API Contract Alignment — Frontend vs Backend

### Path Mismatches (frontend path != backend path)

| Frontend Call | Backend Endpoint | Issue |
|--------------|-----------------|-------|
| POST `/profiles/{id}/test-completed` | PUT `/profiles/{id}/test-completed` | Method mismatch |
| POST `/profiles/{id}/reset-test` | PUT `/profiles/{id}/reset-test` | Method mismatch |
| PUT `/profiles/{id}/levels/{module}` | PUT `/profiles/{id}/modules/{module}/level` | Path mismatch |
| POST `/profiles/{id}/levels` | PUT `/profiles/{id}/levels` | Method mismatch |
| POST `/sessions/generate` | POST `/profiles/{userId}/sessions/generate` | Missing profileId prefix |
| GET `/sessions/current` | GET `/profiles/{userId}/sessions/current` | Missing profileId prefix |
| POST `/sessions/{id}/complete` | PUT `/profiles/{userId}/sessions/{id}/complete` | Method + path mismatch |
| GET `/sessions/history` | GET `/profiles/{userId}/sessions` | Path mismatch |
| PUT `/sessions/{sessionId}/blocks/{blockIndex}` | PUT `/profiles/{profileId}/sessions/{sessionId}/blocks/{blockIndex}/advance` | Missing prefix + /advance |
| GET `/progress/{module}/{level}` | GET `/profiles/{userId}/modules/{module}/levels/{level}` | Path mismatch |
| POST `/review/units` | POST `/profiles/{userId}/reviews` | Path mismatch |
| GET `/review/due` | GET `/profiles/{userId}/reviews/due` | Missing profileId prefix |
| GET `/learning-path/{profileId}/status` | GET `/profiles/{profileId}/learning-status` | Path mismatch |
| GET `/learning-path/{profileId}` | GET `/profiles/{profileId}/learning-path` | Path mismatch |
| POST `/learning-path/{profileId}/generate` | POST `/profiles/{profileId}/learning-path/generate` | Path mismatch |
| GET `/assessment/questions` | GET `/profiles/{userId}/assessments/level-test/questions` | Path mismatch |
| POST `/assessment/submit` | POST `/profiles/{userId}/assessments/level-test` | Path mismatch |
| GET `/activity/{profileId}/dates` | GET `/profiles/{userId}/activity` | Path mismatch |
| POST `/activity/record` | POST `/profiles/{userId}/activity` | Path mismatch |
| POST `/tutor/conversations/start` | POST `/conversations` | Path mismatch |
| POST `/tutor/conversations/{id}/end` | PUT `/conversations/{id}/end` | Method + path mismatch |
| GET `/achievements/{profileId}` | GET `/profiles/{userId}/achievements` | Path mismatch |
| POST `/xp/{profileId}` | POST `/profiles/{userId}/xp` | Path mismatch |
| GET `/phrases/{level}` | GET `/api/phrases?level={level}` | Path vs query param |
| PUT `/profiles/{id}` | (no endpoint) | Missing backend endpoint |
| POST `/profiles/{id}/password` | PUT `/auth/change-password` | Path + method mismatch |

## Security Observations

| Endpoint | Observation |
|----------|-------------|
| PUT /api/profiles/{id}/levels | Missing @RequireProfileOwnership |
| DELETE /api/profiles/{id} | Missing @RequireProfileOwnership |
| Admin endpoints (/api/admin/*) | No @PreAuthorize or role check |
| GET /api/conversations/{id} | No ownership validation |
| POST /api/reading/passages/{textId}/answers | No ownership validation |

## Risks
- ~26 frontend-backend path mismatches — frontend API services use different URL patterns than backend controllers
- Admin endpoints lack role-based security (no ADMIN role enforcement)
- Several profile endpoints missing @RequireProfileOwnership
- Conversation endpoints lack user-scoped access control
- Frontend uses both profileId-prefixed and non-prefixed paths inconsistently
