-- FASE 4: Add item_type to spaced_repetition_items

ALTER TABLE spaced_repetition_items ADD COLUMN IF NOT EXISTS item_type VARCHAR(30) DEFAULT 'module-unit';

UPDATE spaced_repetition_items SET item_type = 'vocabulary-word' WHERE module_name = 'vocabulary-word';

CREATE INDEX IF NOT EXISTS idx_srs_item_type ON spaced_repetition_items(user_id, item_type);
