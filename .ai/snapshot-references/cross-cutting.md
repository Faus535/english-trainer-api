# Cross-Cutting Snapshot

## API Contract Alignment
| Frontend Call | Backend Endpoint | Status |
|--------------|-----------------|--------|
| SessionApiService: advanceBlock | PUT /profiles/{id}/sessions/{sid}/blocks/{idx}/advance | Aligned |
| SessionApiService: getBlockExercises | GET /profiles/{id}/sessions/{sid}/blocks/{idx}/exercises | Aligned |
| MinigameApiService: word-match/fill-gap/unscramble | GET /minigames/* | Aligned |
| ReviewApiService | GET/POST/PUT /profiles/{userId}/reviews/* | Aligned |
| LearningPathApiService | GET /profiles/{id}/learning-path+learning-status | Aligned |
| No frontend phonetics service | GET /phonetics/phonemes, GET+POST+PUT /profiles/{userId}/phonetics/* | Backend ready, frontend pending |

## Security Observations
| Area | Observation |
|------|-------------|
| /api/admin/* | Only adminGuard in frontend; backend has no admin role check |
| /api/phonetics/phonemes/** | Public (permitAll) — catalog data, appropriate |
| /api/profiles/{userId}/* | Protected by @RequireProfileOwnership AOP aspect |

## Risks
- Admin endpoints lack backend authorization (only frontend guard protects them)
- Phonetics module has no frontend integration yet
- GoogleTokenVerifierTest has 3 pre-existing failures (environment-specific)
- Testcontainers Docker API version mismatch blocks integration tests locally
- No E2E test coverage for phonetics or session flows
- 7 cross-module event listeners — ensure all events published after domain changes
