ALTER TABLE talk_conversations
    ADD COLUMN IF NOT EXISTS grammar_notes TEXT,
    ADD COLUMN IF NOT EXISTS vocabulary_used TEXT;
