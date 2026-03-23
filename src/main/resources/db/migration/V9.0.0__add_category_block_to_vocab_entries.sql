ALTER TABLE vocab_entries ADD COLUMN category VARCHAR(50);
ALTER TABLE vocab_entries ADD COLUMN block INTEGER;
ALTER TABLE vocab_entries ADD COLUMN block_title VARCHAR(100);
CREATE INDEX idx_vocab_entries_level_block ON vocab_entries(level, block);
