# Cross-Cutting Snapshot

## API Contract Mismatches (Frontend vs Backend)

| Frontend Call | Frontend Path | Backend Path | Status |
|--------------|---------------|--------------|--------|
| Admin vocab PUT | PUT /api/admin/vocab/{id} | (missing) | BROKEN |
| Admin vocab DELETE | DELETE /api/admin/vocab/{id} | (missing) | BROKEN |
| Admin phrases POST | POST /api/admin/phrases | (missing) | BROKEN |
| Admin phrases PUT | PUT /api/admin/phrases/{id} | (missing) | BROKEN |
| Admin phrases DELETE | DELETE /api/admin/phrases/{id} | (missing) | BROKEN |
| Admin reading PUT | PUT /api/admin/reading/{id} | (missing) | BROKEN |
| Admin reading DELETE | DELETE /api/admin/reading/{id} | (missing) | BROKEN |
| Admin writing PUT | PUT /api/admin/writing/{id} | (missing) | BROKEN |
| Admin writing DELETE | DELETE /api/admin/writing/{id} | (missing) | BROKEN |
| Admin stats | GET /api/admin/stats | (missing) | BROKEN |
| Admin reading GET | GET /api/admin/reading | (missing) | BROKEN |
| Admin writing GET | GET /api/admin/writing | (missing) | BROKEN |
| Module progress | GET /api/profiles/{id}/module-progress | GET /api/profiles/{id}/modules | PATH MISMATCH |
| Conversations | GET /api/tutor/conversations | GET /api/conversations | PATH MISMATCH |
| Vocab by level+block | GET /api/content/vocab/level/{l}?block={b} | GET /api/vocab/level/{l} | PATH MISMATCH |
| Phrases | GET /api/content/phrases | GET /api/phrases | PATH MISMATCH |
| Assessment submit | POST /api/profiles/{id}/assessments/submit | POST /api/profiles/{id}/assessments/level-test | PATH MISMATCH |
| Assessment questions | GET /api/assessments/test-questions | GET /api/profiles/{id}/assessments/level-test/questions | PATH MISMATCH |
| XP grant | POST /api/profiles/{id}/xp/grant | POST /api/profiles/{id}/xp | PATH MISMATCH |
| Review complete | PUT /api/profiles/{id}/reviews/{itemId} | PUT /api/profiles/{id}/reviews/{itemId}/complete | PATH MISMATCH |
| Activity dates | GET /api/profiles/{id}/activity/dates | GET /api/profiles/{id}/activity | PATH MISMATCH |
| Learning path | GET /api/learning-path | GET /api/profiles/{id}/learning-path | MISSING profileId |
| Session block advance | (not yet implemented in frontend) | PUT /api/profiles/{id}/sessions/{sid}/blocks/{bi}/advance | NEW — needs frontend |
| Session block exercises | (not yet implemented in frontend) | GET /api/profiles/{id}/sessions/{sid}/blocks/{bi}/exercises | NEW — needs frontend |

## Security Observations

| Area | Observation |
|------|-------------|
| Admin endpoints | No @PreAuthorize/@Secured — any authenticated user can access admin CRUD |
| Conversation ownership | Missing ownership validation — any user can read/send messages to any conversation |
| Reading submissions | Missing ownership check on submit |
| PUT /api/profiles/{id}/levels | Missing @RequireProfileOwnership |
| DELETE /api/profiles/{id} | Missing @RequireProfileOwnership |
| Account deletion | Frontend UI exists but backend endpoint lacks ownership guard |

## Risks
- 12 admin CRUD endpoints missing (PUT/DELETE for vocab, phrases, reading, writing + stats + GET reading/writing)
- Frontend uses different base paths for several APIs (content/vocab vs vocab, tutor/conversations vs conversations)
- New block advance/exercises endpoints need frontend integration
- Session completion now requires all exercises completed — frontend must adapt workflow
- Admin endpoints lack role-based authorization
