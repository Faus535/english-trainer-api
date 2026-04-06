# Recent Activity

## Last 20 Commits

| Hash | Message |
|------|---------|
| 03d76b8 | chore: remove completed plan file |
| 6cd1418 | fix(immerse): remove rawText duplication from generate path |
| 856bedf | chore: add claude commands and remove completed plan |
| 65205f4 | feat: reduce AI token consumption across immerse + talk |
| fdaaf9c | chore: remove completed async-immerse plan file |
| 3732766 | test(immerse): add ProcessImmerseContentAsyncServiceTest |
| d58da10 | fix(immerse): add IDOR protection on GET content/exercises |
| 9662005 | feat(immerse): rewire generate use case to return PENDING + async |
| dcff3f5 | Remove completed plan file |
| 2dbf17d | feat(immerse): add AsyncConfig + ProcessImmerseContentAsyncService |
| 738c376 | fix: restore GoogleTokenVerifier test constructor |
| 704d9af | fix(immerse): include title and rawText in entity updateFrom() |
| 1f28014 | feat(immerse): make generate() return PENDING, extend markProcessed |
| 1cbe490 | fix: make use case constructors package-private, add 9 tests |
| 22c8936 | fix: extract GoogleAuthPort, RefreshTokenUseCase, add 6 tests |
| f37ab67 | fix: make immerse use case constructors package-private + 5 tests |
| fbcf312 | fix: guard Google OAuth returning-user path + backfill migration |
| f34d329 | fix: use profileId from JWT in immerse/talk controllers |
| e94ac7e | feat(auth): add DELETE /api/auth/account endpoint |
| 15a57b5 | fix: indent build.gradle, add provider/createdAt to /auth/me |

## Active Areas

- **Immerse module**: Async content generation, IDOR protection, AI token optimization
- **Auth module**: Google OAuth hardening, account deletion, token management
- **Testing**: Expanding test coverage across all modules with Object Mothers + failing stubs
