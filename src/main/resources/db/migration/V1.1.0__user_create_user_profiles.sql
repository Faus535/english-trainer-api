CREATE TABLE IF NOT EXISTS user_profiles (
    id                UUID PRIMARY KEY,
    test_completed    BOOLEAN NOT NULL DEFAULT FALSE,
    level_listening   VARCHAR(10) NOT NULL DEFAULT 'a1',
    level_vocabulary  VARCHAR(10) NOT NULL DEFAULT 'a1',
    level_grammar     VARCHAR(10) NOT NULL DEFAULT 'a1',
    level_phrases     VARCHAR(10) NOT NULL DEFAULT 'a1',
    level_pronunciation VARCHAR(10) NOT NULL DEFAULT 'a1',
    session_count     INT NOT NULL DEFAULT 0,
    sessions_this_week INT NOT NULL DEFAULT 0,
    week_start        DATE,
    xp                INT NOT NULL DEFAULT 0,
    created_at        TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at        TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS module_progress (
    id              UUID PRIMARY KEY,
    user_id         UUID NOT NULL REFERENCES user_profiles(id),
    module_name     VARCHAR(50) NOT NULL,
    level           VARCHAR(10) NOT NULL,
    current_unit    INT NOT NULL DEFAULT 0,
    completed_units TEXT,
    scores          TEXT,
    UNIQUE(user_id, module_name, level)
);

CREATE TABLE IF NOT EXISTS activity_dates (
    id          UUID PRIMARY KEY,
    user_id     UUID NOT NULL REFERENCES user_profiles(id),
    activity_date DATE NOT NULL,
    UNIQUE(user_id, activity_date)
);
