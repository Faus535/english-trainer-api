-- Backfill missing user_profiles rows for auth users whose profile was deleted or never created.
INSERT INTO user_profiles (id, xp, created_at, updated_at)
SELECT u.user_profile_id, 0, COALESCE(u.created_at, now()), now()
FROM users u
WHERE u.user_profile_id IS NOT NULL
  AND NOT EXISTS (SELECT 1 FROM user_profiles p WHERE p.id = u.user_profile_id)
ON CONFLICT (id) DO NOTHING;
