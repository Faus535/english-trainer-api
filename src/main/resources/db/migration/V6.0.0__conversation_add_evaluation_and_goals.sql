ALTER TABLE conversations ADD COLUMN IF NOT EXISTS evaluation_json TEXT;
ALTER TABLE conversations ADD COLUMN IF NOT EXISTS goals_json TEXT;
