CREATE TABLE IF NOT EXISTS level_test_results (
    id                    UUID PRIMARY KEY,
    user_id               UUID NOT NULL REFERENCES user_profiles(id) ON DELETE CASCADE,
    vocabulary_score      INT NOT NULL,
    grammar_score         INT NOT NULL,
    listening_score       INT NOT NULL,
    pronunciation_score   INT NOT NULL,
    assigned_levels       TEXT NOT NULL,
    completed_at          TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS mini_test_results (
    id              UUID PRIMARY KEY,
    user_id         UUID NOT NULL REFERENCES user_profiles(id) ON DELETE CASCADE,
    module_name     VARCHAR(50) NOT NULL,
    level           VARCHAR(10) NOT NULL,
    score           INT NOT NULL,
    total_questions INT NOT NULL,
    correct_answers INT NOT NULL,
    recommendation  VARCHAR(20) NOT NULL,
    completed_at    TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_level_test_user ON level_test_results(user_id);
CREATE INDEX IF NOT EXISTS idx_mini_test_user ON mini_test_results(user_id);
