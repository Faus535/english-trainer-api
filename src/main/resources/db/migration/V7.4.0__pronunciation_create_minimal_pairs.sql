CREATE TABLE IF NOT EXISTS minimal_pairs (
    id UUID PRIMARY KEY,
    word1 VARCHAR(50) NOT NULL,
    word2 VARCHAR(50) NOT NULL,
    ipa1 VARCHAR(50) NOT NULL,
    ipa2 VARCHAR(50) NOT NULL,
    sound_category VARCHAR(30) NOT NULL,
    level VARCHAR(5) NOT NULL
);
CREATE INDEX IF NOT EXISTS idx_minimal_pairs_category ON minimal_pairs(sound_category, level);

CREATE TABLE IF NOT EXISTS minimal_pair_results (
    id UUID PRIMARY KEY,
    version BIGINT NOT NULL DEFAULT 0,
    user_id UUID NOT NULL,
    pair_id UUID NOT NULL REFERENCES minimal_pairs(id),
    correct BOOLEAN NOT NULL,
    answered_at TIMESTAMP NOT NULL DEFAULT NOW()
);
CREATE INDEX IF NOT EXISTS idx_minimal_pair_results_user ON minimal_pair_results(user_id);
