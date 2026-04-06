# Database & Migrations

- **Database**: PostgreSQL
- **Migration tool**: Flyway
- **Total migrations**: 64
- **Latest version**: V10.9.0__backfill_missing_user_profiles.sql

## Migration History

| Version Range | Purpose |
|--------------|---------|
| V1.0.0–V1.8.0 | Initial schema: vocabulary, users, phrases, gamification, SpacedRepetition, assessment, sessions, auth |
| V2.0.0–V2.2.0 | Seed data & auth enhancements (provider) |
| V3.0.0–V4.2.0 | Conversation & metrics, refresh tokens, password reset tokens |
| V5.0.0–V5.6.0 | Reading, writing, analytics, notifications, indexing, seed data |
| V6.0.0–V6.4.0 | Conversation evaluation/goals, exercises, error patterns, reseed |
| V7.0.0–V7.5.0 | Pronunciation, tutor errors, daily challenges, minigames, minimal pairs |
| V8.0.0–V8.9.0 | Test questions, history, vocab categories/blocks, SpacedRepetition items |
| V9.0.0–V9.7.0 | Learning paths, vocab mastery, session exercises, phonetics, immerse nullable |
| V10.0.0–V10.9.0 | Talk scenarios & conversations, Immerse tables, Review tables, content type, drop orphaned tables, user profile fixes |

## Notes

- Many V1-V9 tables were dropped in V10.5-V10.8 (notification, orphaned legacy tables)
- Active tables support: auth, user_profiles, activity_dates, achievements, user_achievements, immerse (content/exercises/submissions), talk (scenarios/conversations), review (items/results)
