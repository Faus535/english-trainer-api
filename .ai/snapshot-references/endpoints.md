# Endpoints Snapshot

## Public Endpoints (~15)
| Method | Path | Controller | Module |
|--------|------|------------|--------|
| POST | /api/auth/login | LoginController | auth |
| POST | /api/auth/register | RegisterController | auth |
| POST | /api/auth/google | GoogleLoginController | auth |
| POST | /api/auth/refresh | RefreshTokenController | auth |
| POST | /api/auth/forgot-password | ForgotPasswordController | auth |
| POST | /api/auth/reset-password | ResetPasswordController | auth |
| POST | /api/auth/logout | LogoutController | auth |
| GET | /api/phonetics/phonemes | GetAllPhonemesController | phonetics |
| GET | /api/phonetics/phonemes/{phonemeId} | GetPhonemeByIdController | phonetics |
| GET | /api/phonetics/phonemes/{phonemeId}/phrases | GetPhrasesByPhonemeController | phonetics |
| GET | /api/assessments/mini-test | GetMiniTestQuestionsController | assessment |
| GET | /api/challenges/today | GetTodayChallengeController | dailychallenge |

## Authenticated Endpoints (~108)
See [modules.md](modules.md) for full endpoint list per module. Key patterns:
- `/api/profiles/{userId}/...` — user-scoped, `@RequireProfileOwnership`
- `/api/admin/...` — admin-only
- `/api/conversations/...` — conversation management
- `/api/curriculum/...` — curriculum read-only
- `/api/minigames/...` — game data
- `/api/pronunciation/...` — pronunciation catalog
- `/api/reading/...` — reading content
- `/api/writing/...` — writing content
- `/api/vocab/...` — vocabulary catalog
- `/api/phrases/...` — phrase catalog
