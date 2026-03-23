CREATE TABLE IF NOT EXISTS mini_game_scores (
    id UUID PRIMARY KEY,
    version BIGINT NOT NULL DEFAULT 0,
    user_id UUID NOT NULL,
    game_type VARCHAR(30) NOT NULL,
    score INT NOT NULL,
    xp_earned INT NOT NULL DEFAULT 0,
    played_at TIMESTAMP NOT NULL DEFAULT NOW()
);
CREATE INDEX IF NOT EXISTS idx_mini_game_scores_user ON mini_game_scores(user_id, game_type);
