# Cross-Cutting Snapshot

## API Contract Alignment (Frontend vs Backend)

| Area | Frontend Pattern | Backend Pattern | Status |
|------|-----------------|-----------------|--------|
| Session advance | PUT /profiles/{id}/sessions/{sid}/blocks/{idx}/advance | AdvanceBlockController same path | Aligned |
| Block exercises | GET /profiles/{id}/sessions/{sid}/blocks/{idx}/exercises | GetBlockExercisesController same path | Aligned |
| Minigame results | POST /profiles/{id}/minigames/results | SaveGameResultsController same path | Aligned |
| Gamification XP | POST /profiles/{id}/xp | GrantXpController matches | Aligned |
| Learning path | GET/POST /profiles/{id}/learning-path | GetLearningPath/Generate controllers | Aligned |
| Activity paths | POST /activity/{id} (FE) vs POST /profiles/{id}/activity (BE) | Different path patterns | Needs check |
| Assessment paths | POST /assessments/test-results (FE) vs POST /profiles/{id}/assessments/level-test (BE) | Different path patterns | Needs check |
| Review update | POST /profiles/{id}/reviews (FE) vs PUT /profiles/{id}/reviews/{itemId}/complete (BE) | Method mismatch | Needs check |

## Security Observations

| Area | Observation |
|------|-------------|
| Admin endpoints | Frontend-only guard (adminGuard); backend lacks role-based authorization on /api/admin/* |
| Profile ownership | AOP-based @RequireProfileOwnership on profile endpoints; good server-side enforcement |
| Rate limiting | Applied to auth endpoints (login, register, forgot-password); 10 req/60s |
| Invalid UUIDs | Now returns 400 via GlobalControllerAdvice (previously caused 502) |

## Risks
- Admin endpoints may lack backend authorization (only frontend guard protects them)
- Some frontend API paths may diverge from backend — activity and assessment paths need verification
- Frontend review API uses POST where backend expects PUT for completion
- 7 cross-module event listeners — ensure all events published after domain changes
- No E2E test coverage for session generate -> advance -> complete flow
- GoogleTokenVerifierTest has 3 pre-existing failures (environment-specific, not blocking)
- Testcontainers Docker API version mismatch (1.32 vs 1.44 minimum) blocks integration tests locally
