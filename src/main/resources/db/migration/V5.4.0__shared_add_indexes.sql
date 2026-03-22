-- Performance indexes for frequent queries
CREATE INDEX IF NOT EXISTS idx_users_email ON users(email);
CREATE INDEX IF NOT EXISTS idx_sessions_user_id ON sessions(user_id);
CREATE INDEX IF NOT EXISTS idx_conversations_user_id ON conversations(user_id);
CREATE INDEX IF NOT EXISTS idx_conversations_status ON conversations(status);
CREATE INDEX IF NOT EXISTS idx_spaced_repetition_user_id ON spaced_repetition_items(user_id);
CREATE INDEX IF NOT EXISTS idx_spaced_repetition_next_review ON spaced_repetition_items(next_review_date);
CREATE INDEX IF NOT EXISTS idx_activity_dates_user_id ON activity_dates(user_id);
CREATE INDEX IF NOT EXISTS idx_user_achievements_user_id ON user_achievements(user_id);
CREATE INDEX IF NOT EXISTS idx_vocab_entries_level ON vocab_entries(level);
CREATE INDEX IF NOT EXISTS idx_phrases_level ON phrases(level);
CREATE INDEX IF NOT EXISTS idx_module_progress_user_id ON module_progress(user_id);
