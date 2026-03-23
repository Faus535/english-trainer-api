-- FASE 3: Add category, block, block_title to vocab_entries

ALTER TABLE vocab_entries ADD COLUMN IF NOT EXISTS category VARCHAR(50);
ALTER TABLE vocab_entries ADD COLUMN IF NOT EXISTS block INTEGER;
ALTER TABLE vocab_entries ADD COLUMN IF NOT EXISTS block_title VARCHAR(100);

CREATE INDEX IF NOT EXISTS idx_vocab_entries_level_block ON vocab_entries(level, block);
