CREATE TABLE immerse_content (
    id                   UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id              UUID NOT NULL REFERENCES user_profiles(id) ON DELETE CASCADE,
    source_url           TEXT,
    title                VARCHAR(500) NOT NULL,
    raw_text             TEXT NOT NULL,
    processed_text       TEXT,
    cefr_level           VARCHAR(5) CHECK (cefr_level IN ('a1','a2','b1','b2','c1','c2')),
    extracted_vocabulary TEXT,
    status               VARCHAR(20) NOT NULL DEFAULT 'PENDING' CHECK (status IN ('PENDING','PROCESSED','FAILED')),
    created_at           TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT now(),
    version              BIGINT NOT NULL DEFAULT 0
);

CREATE INDEX idx_immerse_content_user_id ON immerse_content(user_id);
CREATE INDEX idx_immerse_content_user_created ON immerse_content(user_id, created_at DESC);

CREATE TABLE immerse_exercises (
    id             UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    content_id     UUID NOT NULL REFERENCES immerse_content(id) ON DELETE CASCADE,
    exercise_type  VARCHAR(50) NOT NULL,
    question       TEXT NOT NULL,
    correct_answer TEXT NOT NULL,
    options        TEXT,
    order_index    INTEGER NOT NULL DEFAULT 0
);

CREATE INDEX idx_immerse_exercises_content_id ON immerse_exercises(content_id);

CREATE TABLE immerse_submissions (
    id           UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    exercise_id  UUID NOT NULL REFERENCES immerse_exercises(id) ON DELETE CASCADE,
    user_id      UUID NOT NULL REFERENCES user_profiles(id) ON DELETE CASCADE,
    user_answer  TEXT NOT NULL,
    is_correct   BOOLEAN NOT NULL,
    feedback     TEXT,
    submitted_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT now()
);

CREATE INDEX idx_immerse_submissions_exercise ON immerse_submissions(exercise_id);
CREATE INDEX idx_immerse_submissions_user ON immerse_submissions(user_id);
