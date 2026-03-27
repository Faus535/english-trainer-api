# Cross-Cutting Snapshot

## API Contract Mismatches (Frontend vs Backend)

| Frontend Call | Frontend Path | Backend Path | Status |
|--------------|--------------|-------------|--------|
| VocabApiService.getAllVocab | /profiles/{id}/vocab | /api/vocab | PATH MISMATCH |
| VocabApiService.getVocabByLevel | /profiles/{id}/vocab/level/{level} | /api/vocab/level/{level} | PATH MISMATCH |
| VocabApiService.searchVocab | /profiles/{id}/vocab/search | /api/vocab/search | PATH MISMATCH |
| VocabApiService.getPhrases | /profiles/{id}/phrases | /api/phrases | PATH MISMATCH |
| VocabApiService.getRandomPhrases | /profiles/{id}/phrases/random | /api/phrases/random | PATH MISMATCH |
| MinigameApiService.getWordMatchData | /profiles/{id}/minigames/word-match | /api/minigames/word-match | PATH MISMATCH |
| MinigameApiService.getFillGapData | /profiles/{id}/minigames/fill-gap | /api/minigames/fill-gap | PATH MISMATCH |
| MinigameApiService.getUnscrambleData | /profiles/{id}/minigames/unscramble | /api/minigames/unscramble | PATH MISMATCH |
| ProgressApiService.getAllProgress | /profiles/{id}/progress | /api/profiles/{userId}/modules | PATH MISMATCH |
| ProgressApiService.checkLevelUp | /profiles/{id}/level-up | /api/profiles/{userId}/modules/{module}/levels/{level}/level-up | PATH MISMATCH |
| SessionApiService.getSessionExercises | /profiles/{id}/sessions/{id}/exercises | No direct endpoint | MISSING |
| ProfileApiService.updateProfileImage | /profiles/{id}/image | No endpoint | MISSING |
| ProfileApiService.updateAccount | /profiles/{id} (PUT) | No endpoint | MISSING |
| ProfileApiService.deleteAccount | /auth/account (DELETE) | No endpoint | MISSING |
| ProfileApiService.updatePassword | /auth/password (PUT) | /api/auth/change-password (PUT) | PATH MISMATCH |
| ExerciseResultApiService | /profiles/{id}/exercise-results | /api/profiles/{profileId}/sessions/{sessionId}/exercises/{exerciseIndex}/result | PATH+STRUCTURE MISMATCH |

## Security Observations

| Area | Observation |
|------|-------------|
| Admin endpoints | No @PreAuthorize/@Secured — any authenticated user can access /api/admin/* |
| Conversations | No ownership validation — users can access other users' conversations |
| Reading submissions | No ownership check on passage submissions |
| DELETE /api/profiles/{id} | Missing @RequireProfileOwnership |
| PUT /api/profiles/{id}/levels | Missing @RequireProfileOwnership |

## Risks
- Frontend minigame services add /profiles/{id} prefix but backend minigame endpoints are public (no profile in path)
- Frontend vocab/phrase services add /profiles/{id} prefix but backend endpoints are public
- Admin CRUD endpoints (PUT, DELETE for vocab, phrases, reading, writing) not implemented in backend
- Session exercise result submission has different path structure between frontend and backend
- No rate limiting on AI endpoints (conversation streaming, exercise generation)
