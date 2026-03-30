# Cross-Cutting Snapshot

## API Contract Alignment (Frontend ↔ Backend)

### Phonetics (NEW — aligned)
| Frontend Call | Backend Endpoint | Status |
|--------------|-----------------|--------|
| getPhonemes() | GET /api/phonetics/phonemes | Aligned |
| getPhonemeDetail(id) | GET /api/phonetics/phonemes/{id} | Aligned |
| getPhrases(id) | GET /api/phonetics/phonemes/{id}/phrases | Aligned |
| getTodayPhoneme(profileId) | GET /api/profiles/{id}/phonetics/today | Aligned (enriched with completedCount/totalCount) |
| getProgress(profileId) | GET /api/profiles/{id}/phonetics/progress | Aligned (NEW endpoint) |
| submitAttempt(...) | POST /api/profiles/{id}/phonetics/phonemes/{pId}/phrases/{phraseId}/attempt | Aligned |
| completePhoneme(profileId, id) | PUT /api/profiles/{id}/phonetics/phonemes/{pId}/complete | Aligned |

### Known Mismatches (pre-existing)
| Frontend Call | Issue |
|--------------|-------|
| Admin CRUD (PUT/DELETE vocab, phrases, reading, writing) | Backend controllers may not implement all CRUD methods |
| tutor/conversations paths | Frontend uses /conversations, backend uses /api/conversations |
| module-progress paths | Frontend uses /module-progress, backend uses /api/profiles/{id}/module-progress |
| content/vocab paths | Possible path format differences |

## Security Observations

| Observation | Severity |
|-------------|----------|
| Admin endpoints lack @PreAuthorize/@Secured | Medium |
| Some PUT/DELETE endpoints missing @RequireProfileOwnership | Medium |
| Phonetics progress endpoint properly secured | OK |

## Risks
- Pre-existing 3 GoogleTokenVerifierTest failures need investigation
- Admin CRUD endpoints may have missing backend implementations
- Frontend path mismatches on tutor/conversation, module-progress need verification
- V9.6.0 migration symbol format mismatch (symbols without slashes in V9.5.0 but with slashes in V9.6.0 SELECT)
