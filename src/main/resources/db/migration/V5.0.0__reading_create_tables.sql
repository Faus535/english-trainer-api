CREATE TABLE reading_passages (
    id UUID PRIMARY KEY,
    version BIGINT DEFAULT 0,
    title VARCHAR(255) NOT NULL,
    content TEXT NOT NULL,
    level VARCHAR(10) NOT NULL,
    topic VARCHAR(100),
    word_count INTEGER NOT NULL DEFAULT 0,
    created_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE TABLE reading_questions (
    id UUID PRIMARY KEY,
    passage_id UUID NOT NULL REFERENCES reading_passages(id) ON DELETE CASCADE,
    question TEXT NOT NULL,
    options TEXT NOT NULL,
    correct_answer INTEGER NOT NULL,
    explanation TEXT
);

CREATE TABLE reading_submissions (
    id UUID PRIMARY KEY,
    version BIGINT DEFAULT 0,
    user_id UUID NOT NULL,
    passage_id UUID NOT NULL REFERENCES reading_passages(id),
    score DOUBLE PRECISION NOT NULL,
    answers TEXT NOT NULL,
    completed_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_reading_passages_level ON reading_passages(level);
CREATE INDEX idx_reading_submissions_user_id ON reading_submissions(user_id);
