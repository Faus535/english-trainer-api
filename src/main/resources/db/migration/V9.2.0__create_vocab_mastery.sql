CREATE TABLE vocab_mastery (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL REFERENCES user_profiles(id) ON DELETE CASCADE,
    vocab_entry_id UUID REFERENCES vocab_entries(id) ON DELETE CASCADE,
    word VARCHAR(100) NOT NULL,
    correct_count INT NOT NULL DEFAULT 0 CHECK (correct_count >= 0),
    incorrect_count INT NOT NULL DEFAULT 0 CHECK (incorrect_count >= 0),
    total_attempts INT NOT NULL DEFAULT 0 CHECK (total_attempts >= 0),
    status VARCHAR(20) NOT NULL DEFAULT 'LEARNING',
    source_context VARCHAR(50) NOT NULL,
    source_detail VARCHAR(50),
    last_practiced_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    learned_at TIMESTAMP,
    version BIGINT NOT NULL DEFAULT 0,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(user_id, vocab_entry_id)
);

CREATE INDEX idx_vocab_mastery_user ON vocab_mastery(user_id);
CREATE INDEX idx_vocab_mastery_user_status ON vocab_mastery(user_id, status);
