CREATE TABLE IF NOT EXISTS phrases (
    id    UUID PRIMARY KEY,
    en    VARCHAR(500) NOT NULL,
    es    VARCHAR(500) NOT NULL,
    level VARCHAR(10) NOT NULL
);

CREATE INDEX IF NOT EXISTS idx_phrases_level ON phrases(level);
