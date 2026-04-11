CREATE TABLE IF NOT EXISTS study_sessions (
    id               UUID        PRIMARY KEY,
    user_id          UUID        NOT NULL REFERENCES user_profiles(id) ON DELETE CASCADE,
    module           VARCHAR(20) NOT NULL,
    duration_seconds INT         NOT NULL,
    created_at       TIMESTAMPTZ NOT NULL DEFAULT now()
);
CREATE INDEX IF NOT EXISTS idx_study_sessions_user_id ON study_sessions(user_id);
CREATE INDEX IF NOT EXISTS idx_study_sessions_created_at ON study_sessions(created_at);
