CREATE TABLE IF NOT EXISTS pronunciation_errors (
    id UUID PRIMARY KEY,
    version BIGINT NOT NULL DEFAULT 0,
    user_id UUID NOT NULL,
    word VARCHAR(100) NOT NULL,
    expected_phoneme VARCHAR(50) NOT NULL,
    spoken_phoneme VARCHAR(50),
    occurrence_count INT NOT NULL DEFAULT 1,
    last_occurred TIMESTAMP NOT NULL DEFAULT NOW(),
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    UNIQUE(user_id, word, expected_phoneme)
);
CREATE INDEX IF NOT EXISTS idx_pronunciation_errors_user ON pronunciation_errors(user_id, occurrence_count DESC);
