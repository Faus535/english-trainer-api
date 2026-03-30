# Activity Snapshot

## Recent Commits (backend)
```
2ae1a17 feat(phonetics): progress endpoint and enriched today response
f39281b feat(phonetics): V9.7.0 migration - Spanish content and 660 new phrases
dede7c9 Add phonetics module: domain, use cases, controllers, persistence, migrations V9.4.0-V9.6.0
54451c5 Update project snapshots after daily exercises restructure
e62319b Add @Transactional to block use cases, update CLAUDE.md with commands documentation
9468d11 Add block controllers, enrich session responses with exercises and block progress
74bed30 Add AdvanceBlock and GetBlockExercises use cases, extend RecordResult with block progress
631bd04 Assign blockIndex to exercises in SessionGenerator.buildExercises
f3afbf1 Add block-level domain logic: blockIndex on exercises, block completion tracking
8973e25 Fix startup crash: remove final from AddXpController to allow CGLIB proxying
```

## Active Areas
- **Phonetics module**: New module with Spanish-oriented content, progress tracking, daily assignments
- **Session/block system**: Block-level exercise management and progress tracking
- **Learning paths**: Structured CEFR-based learning with content selection and mastery tracking
