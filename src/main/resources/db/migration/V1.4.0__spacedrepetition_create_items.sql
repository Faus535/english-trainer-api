CREATE TABLE IF NOT EXISTS spaced_repetition_items (
    id                UUID PRIMARY KEY,
    user_id           UUID NOT NULL REFERENCES user_profiles(id) ON DELETE CASCADE,
    unit_reference    VARCHAR(100) NOT NULL,
    module_name       VARCHAR(50) NOT NULL,
    level             VARCHAR(10) NOT NULL,
    unit_index        INT NOT NULL,
    next_review_date  DATE NOT NULL,
    interval_index    INT NOT NULL DEFAULT 0,
    review_count      INT NOT NULL DEFAULT 0,
    graduated         BOOLEAN NOT NULL DEFAULT FALSE,
    created_at        TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(user_id, unit_reference)
);

CREATE INDEX IF NOT EXISTS idx_sr_user_due ON spaced_repetition_items(user_id, next_review_date);
