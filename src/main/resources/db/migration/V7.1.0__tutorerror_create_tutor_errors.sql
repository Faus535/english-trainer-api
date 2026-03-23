CREATE TABLE IF NOT EXISTS tutor_errors (
    id UUID PRIMARY KEY,
    version BIGINT NOT NULL DEFAULT 0,
    user_id UUID NOT NULL,
    error_type VARCHAR(20) NOT NULL,
    original_text VARCHAR(500) NOT NULL,
    corrected_text VARCHAR(500) NOT NULL,
    rule VARCHAR(255),
    occurrence_count INT NOT NULL DEFAULT 1,
    first_seen TIMESTAMP NOT NULL DEFAULT NOW(),
    last_seen TIMESTAMP NOT NULL DEFAULT NOW()
);
CREATE INDEX IF NOT EXISTS idx_tutor_errors_user_type ON tutor_errors(user_id, error_type, occurrence_count DESC);
