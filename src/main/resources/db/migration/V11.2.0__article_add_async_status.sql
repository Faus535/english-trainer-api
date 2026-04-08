-- Update status enum to include new async states
ALTER TABLE article_readings DROP CONSTRAINT IF EXISTS article_readings_status_check;
ALTER TABLE article_readings ADD CONSTRAINT article_readings_status_check
    CHECK (status IN ('PENDING', 'PROCESSING', 'READY', 'FAILED', 'IN_PROGRESS', 'COMPLETED'));

-- Performance index for history queries
CREATE INDEX IF NOT EXISTS idx_article_readings_user_status_created
    ON article_readings(user_id, status, created_at DESC);
