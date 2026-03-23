CREATE TABLE IF NOT EXISTS error_patterns (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL,
    category VARCHAR(50) NOT NULL,
    pattern VARCHAR(255) NOT NULL,
    examples_json TEXT,
    occurrence_count INT NOT NULL DEFAULT 1,
    last_occurred TIMESTAMP NOT NULL DEFAULT NOW(),
    resolved BOOLEAN NOT NULL DEFAULT false,
    created_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE INDEX IF NOT EXISTS idx_error_patterns_user_id ON error_patterns(user_id);
CREATE INDEX IF NOT EXISTS idx_error_patterns_user_category ON error_patterns(user_id, category, occurrence_count DESC);
