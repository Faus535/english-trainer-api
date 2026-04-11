ALTER TABLE article_marked_words
    ADD COLUMN IF NOT EXISTS definition TEXT,
    ADD COLUMN IF NOT EXISTS phonetics VARCHAR(100),
    ADD COLUMN IF NOT EXISTS synonyms TEXT[],
    ADD COLUMN IF NOT EXISTS example_sentence TEXT,
    ADD COLUMN IF NOT EXISTS part_of_speech VARCHAR(50);
