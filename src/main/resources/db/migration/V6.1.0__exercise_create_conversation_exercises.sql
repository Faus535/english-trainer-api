CREATE TABLE IF NOT EXISTS conversation_exercises (
    id UUID PRIMARY KEY,
    conversation_id UUID NOT NULL,
    user_id UUID NOT NULL,
    exercises_json TEXT NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE INDEX IF NOT EXISTS idx_conversation_exercises_conversation_id ON conversation_exercises(conversation_id);
CREATE INDEX IF NOT EXISTS idx_conversation_exercises_user_id ON conversation_exercises(user_id);
