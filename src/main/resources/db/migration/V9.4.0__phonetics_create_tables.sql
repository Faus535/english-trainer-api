-- Phoneme catalog (44 IPA phonemes)
CREATE TABLE IF NOT EXISTS phonemes (
    id UUID PRIMARY KEY,
    symbol VARCHAR(10) NOT NULL UNIQUE,
    name VARCHAR(50) NOT NULL,
    category VARCHAR(20) NOT NULL,
    subcategory VARCHAR(30) NOT NULL,
    example_words TEXT NOT NULL,
    description TEXT,
    mouth_position TEXT,
    tips TEXT,
    difficulty_order INT NOT NULL DEFAULT 0
);
CREATE INDEX IF NOT EXISTS idx_phonemes_category ON phonemes(category);
CREATE INDEX IF NOT EXISTS idx_phonemes_difficulty ON phonemes(difficulty_order);

-- Practice phrases per phoneme
CREATE TABLE IF NOT EXISTS phoneme_practice_phrases (
    id UUID PRIMARY KEY,
    phoneme_id UUID NOT NULL REFERENCES phonemes(id),
    text VARCHAR(200) NOT NULL,
    difficulty VARCHAR(10) NOT NULL DEFAULT 'easy',
    target_words TEXT NOT NULL
);
CREATE INDEX IF NOT EXISTS idx_phoneme_phrases_phoneme ON phoneme_practice_phrases(phoneme_id);

-- User progress tracking per phoneme+phrase
CREATE TABLE IF NOT EXISTS user_phoneme_progress (
    id UUID PRIMARY KEY,
    version BIGINT NOT NULL DEFAULT 0,
    user_id UUID NOT NULL,
    phoneme_id UUID NOT NULL REFERENCES phonemes(id),
    phrase_id UUID NOT NULL REFERENCES phoneme_practice_phrases(id),
    attempts_count INT NOT NULL DEFAULT 0,
    correct_attempts_count INT NOT NULL DEFAULT 0,
    best_score INT NOT NULL DEFAULT 0,
    phrase_completed BOOLEAN NOT NULL DEFAULT false,
    last_attempt_at TIMESTAMP,
    UNIQUE(user_id, phoneme_id, phrase_id)
);
CREATE INDEX IF NOT EXISTS idx_user_phoneme_progress_user ON user_phoneme_progress(user_id);
CREATE INDEX IF NOT EXISTS idx_user_phoneme_progress_user_phoneme ON user_phoneme_progress(user_id, phoneme_id);

-- Daily assignment of phoneme to user
CREATE TABLE IF NOT EXISTS phoneme_daily_assignments (
    id UUID PRIMARY KEY,
    version BIGINT NOT NULL DEFAULT 0,
    user_id UUID NOT NULL,
    phoneme_id UUID NOT NULL REFERENCES phonemes(id),
    assigned_date DATE NOT NULL,
    completed BOOLEAN NOT NULL DEFAULT false,
    completed_at TIMESTAMP,
    UNIQUE(user_id, assigned_date)
);
CREATE INDEX IF NOT EXISTS idx_phoneme_daily_user ON phoneme_daily_assignments(user_id, assigned_date);
