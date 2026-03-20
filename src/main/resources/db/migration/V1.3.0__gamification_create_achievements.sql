CREATE TABLE IF NOT EXISTS achievements (
    id          VARCHAR(50) PRIMARY KEY,
    name        VARCHAR(100) NOT NULL,
    description VARCHAR(255) NOT NULL,
    icon        VARCHAR(50),
    xp_reward   INT NOT NULL DEFAULT 0
);

CREATE TABLE IF NOT EXISTS user_achievements (
    id              UUID PRIMARY KEY,
    user_id         UUID NOT NULL REFERENCES user_profiles(id) ON DELETE CASCADE,
    achievement_id  VARCHAR(50) NOT NULL REFERENCES achievements(id),
    unlocked_at     TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(user_id, achievement_id)
);
