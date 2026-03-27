# Activity Snapshot

## Recent Commits
```
ddee057 Return 204 instead of 404 when user has no learning path
54451c5 Update project snapshots after daily exercises restructure
e62319b Add @Transactional to block use cases, update CLAUDE.md with commands documentation
9468d11 Add block controllers, enrich session responses with exercises and block progress, add 422 error handlers
74bed30 Add AdvanceBlock and GetBlockExercises use cases, extend RecordResult with block progress, enforce completion validation
631bd04 Assign blockIndex to exercises in SessionGenerator.buildExercises
f3afbf1 Add block-level domain logic: blockIndex on exercises, block completion tracking, validation exceptions
8973e25 Fix startup crash: remove final from AddXpController to allow CGLIB proxying
de9c452 Add learning journey redesign: structured learning paths, smart content selection, per-word mastery tracking, and security hardening
8cf3754 Fix level differentiation: rich CEFR profiles, per-level feedback, C2 support, expanded question bank
776a1c8 Fix concurrency bugs: optimistic locking on UserProfile and duplicate refresh tokens
bf3b94e Allow logout without authentication so expired tokens can still revoke refresh tokens
165a7aa Auth robustness: ownership checks, JWT logging, and 60min token expiration
e41f6af Add logout endpoint, reduce JWT to 30min, persist Google refresh tokens
6a83cb8 Fix 403 on expired JWT and add profile ownership checks
a802a46 Disable anonymous auth so expired tokens return 401 not 403
6e1cb16 Add batch levels endpoint to fix optimistic locking on concurrent PUTs
98cb9a0 Complete vocabulary seeds: A1 blocks 6-10 and A2 blocks 1-10
9a5edfe Remove duplicate V9.x migrations (already covered by V8.x)
68c98d1 Implement PLAN_MEJORAS_BACKEND phases 3, 4, and 5
```

## Active Areas
- Learning path & status: returning 204 for users without learning paths
- Session block system: block-level exercise tracking, advance validation, enriched responses
- Auth hardening: ownership checks, JWT tuning, optimistic locking
