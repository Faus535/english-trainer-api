CREATE TABLE writing_exercises (
    id UUID PRIMARY KEY,
    version BIGINT DEFAULT 0,
    prompt TEXT NOT NULL,
    level VARCHAR(10) NOT NULL,
    topic VARCHAR(100),
    min_words INTEGER DEFAULT 50,
    max_words INTEGER DEFAULT 300,
    created_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE TABLE writing_submissions (
    id UUID PRIMARY KEY,
    version BIGINT DEFAULT 0,
    user_id UUID NOT NULL,
    exercise_id UUID NOT NULL REFERENCES writing_exercises(id),
    text TEXT NOT NULL,
    word_count INTEGER NOT NULL,
    grammar_score DOUBLE PRECISION,
    coherence_score DOUBLE PRECISION,
    vocabulary_score DOUBLE PRECISION,
    overall_score DOUBLE PRECISION,
    feedback TEXT,
    corrections TEXT,
    level_assessment VARCHAR(10),
    submitted_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_writing_exercises_level ON writing_exercises(level);
CREATE INDEX idx_writing_submissions_user_id ON writing_submissions(user_id);
