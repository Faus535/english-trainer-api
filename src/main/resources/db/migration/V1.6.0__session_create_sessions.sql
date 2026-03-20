CREATE TABLE IF NOT EXISTS sessions (
    id                UUID PRIMARY KEY,
    user_id           UUID NOT NULL REFERENCES user_profiles(id) ON DELETE CASCADE,
    mode              VARCHAR(20) NOT NULL,
    session_type      VARCHAR(20) NOT NULL,
    listening_module  VARCHAR(50),
    secondary_module  VARCHAR(50),
    integrator_theme  VARCHAR(100),
    completed         BOOLEAN NOT NULL DEFAULT FALSE,
    started_at        TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    completed_at      TIMESTAMP,
    duration_minutes  INT,
    blocks_data       TEXT
);

CREATE INDEX IF NOT EXISTS idx_sessions_user ON sessions(user_id);
CREATE INDEX IF NOT EXISTS idx_sessions_user_date ON sessions(user_id, started_at);
