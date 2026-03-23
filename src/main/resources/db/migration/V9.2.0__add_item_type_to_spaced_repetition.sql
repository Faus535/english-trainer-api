ALTER TABLE spaced_repetition_items ADD COLUMN item_type VARCHAR(30) DEFAULT 'module-unit';
UPDATE spaced_repetition_items SET item_type = 'vocabulary-word' WHERE module_name = 'vocabulary-word';
CREATE INDEX idx_srs_item_type ON spaced_repetition_items(user_id, item_type);
