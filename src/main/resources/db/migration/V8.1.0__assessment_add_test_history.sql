-- Phase 3: Add last_test_at to user_profiles for retake sync
-- Phase 4: Question history to avoid repeated questions

ALTER TABLE user_profiles ADD COLUMN IF NOT EXISTS last_test_at TIMESTAMP;

CREATE TABLE IF NOT EXISTS test_question_history (
    id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id     UUID        NOT NULL,
    question_id UUID        NOT NULL REFERENCES test_questions(id),
    asked_at    TIMESTAMP   NOT NULL DEFAULT NOW()
);

CREATE INDEX IF NOT EXISTS idx_test_question_history_user ON test_question_history(user_id);
