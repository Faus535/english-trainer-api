CREATE TABLE IF NOT EXISTS vocabulary_context (
    id UUID PRIMARY KEY,
    version BIGINT NOT NULL DEFAULT 0,
    vocabulary_id UUID NOT NULL,
    level VARCHAR(5) NOT NULL,
    sentences_json TEXT NOT NULL,
    generated_at TIMESTAMP NOT NULL DEFAULT NOW(),
    UNIQUE(vocabulary_id, level)
);
CREATE INDEX IF NOT EXISTS idx_vocabulary_context_vocab ON vocabulary_context(vocabulary_id, level);
