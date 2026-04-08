-- Add XP tracking column
ALTER TABLE article_readings ADD COLUMN IF NOT EXISTS xp_earned INTEGER NOT NULL DEFAULT 0;

-- Index for XP leaderboard queries
CREATE INDEX IF NOT EXISTS idx_article_readings_user_xp_earned
    ON article_readings(user_id, xp_earned DESC);
