-- Add article_marked_words table
CREATE TABLE article_marked_words (
    id                  UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    article_reading_id  UUID         NOT NULL REFERENCES article_readings(id) ON DELETE CASCADE,
    user_id             UUID         NOT NULL REFERENCES user_profiles(id)    ON DELETE CASCADE,
    word_or_phrase      VARCHAR(200) NOT NULL,
    translation         TEXT         NOT NULL,
    context_sentence    TEXT,
    created_at          TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT now()
);
CREATE INDEX idx_article_marked_words_reading_id ON article_marked_words(article_reading_id);
CREATE INDEX idx_article_marked_words_user_id    ON article_marked_words(user_id);
CREATE UNIQUE INDEX idx_article_marked_words_unique
    ON article_marked_words(article_reading_id, user_id, word_or_phrase);

-- Extend review_items to allow ARTICLE source type
ALTER TABLE review_items
    DROP CONSTRAINT IF EXISTS review_items_source_type_check;
ALTER TABLE review_items
    ADD CONSTRAINT review_items_source_type_check
    CHECK (source_type IN ('TALK_ERROR','IMMERSE_VOCAB','PRONUNCIATION','ARTICLE'));
