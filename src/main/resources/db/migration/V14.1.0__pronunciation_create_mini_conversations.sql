CREATE TABLE IF NOT EXISTS pronunciation_mini_conversations (
    id UUID PRIMARY KEY,
    version BIGINT NOT NULL DEFAULT 0,
    user_id UUID NOT NULL,
    focus VARCHAR(100) NOT NULL,
    level VARCHAR(5) NOT NULL,
    status VARCHAR(20) NOT NULL,
    current_prompt TEXT,
    current_target_phrase TEXT,
    started_at TIMESTAMP WITH TIME ZONE NOT NULL,
    completed_at TIMESTAMP WITH TIME ZONE
);

CREATE TABLE IF NOT EXISTS pronunciation_mini_conversation_turns (
    id UUID PRIMARY KEY,
    conversation_id UUID NOT NULL REFERENCES pronunciation_mini_conversations(id),
    turn_number INTEGER NOT NULL,
    target_phrase TEXT NOT NULL,
    recognized_text TEXT,
    score INTEGER NOT NULL,
    evaluated_at TIMESTAMP WITH TIME ZONE NOT NULL
);

CREATE INDEX IF NOT EXISTS idx_mini_conv_turns_conversation
    ON pronunciation_mini_conversation_turns(conversation_id);
