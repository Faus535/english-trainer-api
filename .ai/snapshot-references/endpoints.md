# Endpoints Snapshot

## Public Endpoints (9)
| Method | Path | Controller | Auth |
|--------|------|------------|------|
| POST | /api/auth/register | RegisterController | no |
| POST | /api/auth/login | LoginController | no |
| POST | /api/auth/google | GoogleLoginController | no |
| POST | /api/auth/refresh | RefreshTokenController | no |
| POST | /api/auth/forgot-password | ForgotPasswordController | no |
| POST | /api/auth/reset-password | ResetPasswordController | no |
| POST | /api/auth/logout | LogoutController | no |
| GET | /api/phonetics/phonemes | GetAllPhonemesController | no |
| GET | /api/phonetics/phonemes/{id} | GetPhonemeByIdController | no |

## Authenticated Endpoints (~111)

### Phonetics (8 endpoints)
| Method | Path | Controller |
|--------|------|------------|
| GET | /api/profiles/{userId}/phonetics/today | GetTodayPhonemeController |
| GET | /api/profiles/{userId}/phonetics/progress | GetUserPhonemeProgressController |
| GET | /api/phonetics/phonemes/{id}/phrases | GetPhrasesByPhonemeController |
| POST | /api/profiles/{userId}/phonetics/phonemes/{id}/phrases/{phraseId}/attempt | RecordPhraseAttemptController |
| PUT | /api/profiles/{userId}/phonetics/phonemes/{id}/complete | CompletePhonemeController |

### User (9), Auth (2), Session (7), Conversation (9), Vocabulary (7)
### Assessment (5), Gamification (5), LearningPath (3), ModuleProgress (5)
### Analytics (3), Activity (3), DailyChallenge (3), SpacedRepetition (4)
### MiniGame (6), MinimalPair (3), Reading (5), Writing (4)
### Pronunciation (3), TutorError (4), Notification (3), Exercise (1)
### VocabularyContext (1), Curriculum (4), Phrase (2), Admin (5)

**Total: ~120 endpoints** (9 public + ~111 authenticated)
