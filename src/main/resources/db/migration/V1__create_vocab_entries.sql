CREATE TABLE vocab_entries (
    id       UUID PRIMARY KEY,
    en       VARCHAR(255) NOT NULL,
    ipa      VARCHAR(255),
    es       VARCHAR(255) NOT NULL,
    type     VARCHAR(50),
    example  TEXT,
    level    VARCHAR(10) NOT NULL
);

CREATE INDEX idx_vocab_entries_level ON vocab_entries(level);
