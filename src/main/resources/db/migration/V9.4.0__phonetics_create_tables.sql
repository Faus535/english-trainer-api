-- Phonetics module: DDL

CREATE TABLE phonemes (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    symbol VARCHAR(10) NOT NULL UNIQUE,
    name VARCHAR(100) NOT NULL,
    category VARCHAR(20) NOT NULL,
    subcategory VARCHAR(50),
    difficulty_order INTEGER NOT NULL,
    example_words TEXT NOT NULL DEFAULT '[]',
    description TEXT,
    mouth_position TEXT,
    tips TEXT
);

CREATE TABLE phoneme_practice_phrases (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    phoneme_id UUID NOT NULL REFERENCES phonemes(id),
    text TEXT NOT NULL,
    difficulty VARCHAR(20) NOT NULL,
    target_words TEXT NOT NULL DEFAULT '[]'
);

CREATE TABLE user_phoneme_progress (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL REFERENCES user_profiles(id),
    phoneme_id UUID NOT NULL REFERENCES phonemes(id),
    phrase_id UUID NOT NULL REFERENCES phoneme_practice_phrases(id),
    correct BOOLEAN NOT NULL DEFAULT false,
    attempted_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT now(),
    version BIGINT NOT NULL DEFAULT 0
);

CREATE TABLE phoneme_daily_assignments (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL REFERENCES user_profiles(id),
    phoneme_id UUID NOT NULL REFERENCES phonemes(id),
    assigned_date DATE NOT NULL,
    completed BOOLEAN NOT NULL DEFAULT false,
    completed_at TIMESTAMP WITH TIME ZONE,
    version BIGINT NOT NULL DEFAULT 0,
    UNIQUE(user_id, assigned_date)
);

CREATE INDEX idx_phoneme_practice_phrases_phoneme_id ON phoneme_practice_phrases(phoneme_id);
CREATE INDEX idx_user_phoneme_progress_user_phoneme ON user_phoneme_progress(user_id, phoneme_id);
CREATE INDEX idx_phoneme_daily_assignments_user_date ON phoneme_daily_assignments(user_id, assigned_date);
CREATE INDEX idx_phoneme_daily_assignments_user_phoneme ON phoneme_daily_assignments(user_id, phoneme_id);
