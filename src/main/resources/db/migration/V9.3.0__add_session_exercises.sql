ALTER TABLE sessions ADD COLUMN exercises_data TEXT;
CREATE INDEX IF NOT EXISTS idx_sessions_user_completed ON sessions(user_id, completed);
