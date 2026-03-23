CREATE TABLE IF NOT EXISTS daily_challenges (
    id UUID PRIMARY KEY,
    version BIGINT NOT NULL DEFAULT 0,
    challenge_type VARCHAR(30) NOT NULL,
    description_es VARCHAR(500) NOT NULL,
    description_en VARCHAR(500) NOT NULL,
    target INT NOT NULL,
    xp_reward INT NOT NULL,
    challenge_date DATE NOT NULL
);
CREATE INDEX IF NOT EXISTS idx_daily_challenges_date ON daily_challenges(challenge_date);

CREATE TABLE IF NOT EXISTS user_challenges (
    id UUID PRIMARY KEY,
    version BIGINT NOT NULL DEFAULT 0,
    user_id UUID NOT NULL,
    challenge_id UUID NOT NULL REFERENCES daily_challenges(id),
    progress INT NOT NULL DEFAULT 0,
    completed BOOLEAN NOT NULL DEFAULT false,
    completed_at TIMESTAMP,
    UNIQUE(user_id, challenge_id)
);
CREATE INDEX IF NOT EXISTS idx_user_challenges_user ON user_challenges(user_id);
