CREATE TABLE article_readings (
    id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id     UUID NOT NULL REFERENCES user_profiles(id) ON DELETE CASCADE,
    topic       VARCHAR(100) NOT NULL,
    level       VARCHAR(5)   NOT NULL CHECK (level IN ('B1','B2','C1')),
    title       VARCHAR(300) NOT NULL DEFAULT '',
    status      VARCHAR(20)  NOT NULL DEFAULT 'IN_PROGRESS'
                    CHECK (status IN ('IN_PROGRESS','COMPLETED')),
    created_at  TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT now(),
    version     BIGINT NOT NULL DEFAULT 0
);
CREATE INDEX idx_article_readings_user_id         ON article_readings(user_id);
CREATE INDEX idx_article_readings_user_created    ON article_readings(user_id, created_at DESC);

CREATE TABLE article_paragraphs (
    id                  UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    article_reading_id  UUID    NOT NULL REFERENCES article_readings(id) ON DELETE CASCADE,
    content             TEXT    NOT NULL,
    order_index         INTEGER NOT NULL,
    speaker             VARCHAR(5) NOT NULL CHECK (speaker IN ('AI','USER'))
);
CREATE INDEX idx_article_paragraphs_reading_id ON article_paragraphs(article_reading_id);
CREATE UNIQUE INDEX idx_article_paragraphs_order ON article_paragraphs(article_reading_id, order_index);

CREATE TABLE article_questions (
    id                  UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    article_reading_id  UUID    NOT NULL REFERENCES article_readings(id) ON DELETE CASCADE,
    question_text       TEXT    NOT NULL,
    order_index         INTEGER NOT NULL,
    min_words           INTEGER NOT NULL DEFAULT 40,
    hint_text           TEXT
);
CREATE INDEX idx_article_questions_reading_id ON article_questions(article_reading_id);
CREATE UNIQUE INDEX idx_article_questions_order ON article_questions(article_reading_id, order_index);

CREATE TABLE article_question_answers (
    id                      UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    article_question_id     UUID    NOT NULL REFERENCES article_questions(id) ON DELETE CASCADE,
    user_answer             TEXT    NOT NULL,
    is_content_correct      BOOLEAN,
    grammar_feedback        TEXT,
    style_feedback          TEXT,
    correction_summary      TEXT,
    created_at              TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT now()
);
CREATE UNIQUE INDEX idx_article_question_answers_unique ON article_question_answers(article_question_id);
