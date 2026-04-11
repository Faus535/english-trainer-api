ALTER TABLE article_readings ADD COLUMN IF NOT EXISTS current_paragraph_index INTEGER NOT NULL DEFAULT 0;
ALTER TABLE article_readings ADD COLUMN IF NOT EXISTS current_question_index INTEGER NOT NULL DEFAULT 0;
