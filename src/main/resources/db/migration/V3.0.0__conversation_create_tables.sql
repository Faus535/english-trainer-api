CREATE TABLE IF NOT EXISTS conversations (
    id         UUID PRIMARY KEY,
    user_id    UUID NOT NULL REFERENCES users(id),
    level      VARCHAR(2) NOT NULL,
    topic      VARCHAR(255),
    status     VARCHAR(20) NOT NULL DEFAULT 'active',
    summary    TEXT,
    started_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    ended_at   TIMESTAMP,
    version    BIGINT
);

CREATE TABLE IF NOT EXISTS conversation_turns (
    id              UUID PRIMARY KEY,
    conversation_id UUID NOT NULL REFERENCES conversations(id) ON DELETE CASCADE,
    role            VARCHAR(10) NOT NULL,
    content         TEXT NOT NULL,
    feedback_json   JSONB,
    confidence      FLOAT,
    created_at      TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_conversations_user_id ON conversations(user_id);
CREATE INDEX idx_conversation_turns_conversation_id ON conversation_turns(conversation_id);
