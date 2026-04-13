CREATE TABLE IF NOT EXISTS pronunciation_drills (
    id UUID PRIMARY KEY,
    version BIGINT NOT NULL DEFAULT 0,
    phrase TEXT NOT NULL,
    focus VARCHAR(100) NOT NULL,
    difficulty VARCHAR(10) NOT NULL,
    cefr_level VARCHAR(5) NOT NULL
);

CREATE TABLE IF NOT EXISTS pronunciation_drill_attempts (
    id UUID PRIMARY KEY,
    drill_id UUID NOT NULL REFERENCES pronunciation_drills(id),
    user_id UUID NOT NULL,
    score INTEGER NOT NULL,
    recognized_text TEXT,
    attempted_at TIMESTAMP WITH TIME ZONE NOT NULL
);

CREATE INDEX IF NOT EXISTS idx_drill_attempts_drill_user ON pronunciation_drill_attempts(drill_id, user_id);

INSERT INTO pronunciation_drills (id, version, phrase, focus, difficulty, cefr_level) VALUES
  ('a0000000-0000-0000-0000-000000000001', 0, 'I thought about the theory', 'th-sound', 'MEDIUM', 'b1'),
  ('a0000000-0000-0000-0000-000000000002', 0, 'Three thin threads', 'th-sound', 'HARD', 'b2'),
  ('a0000000-0000-0000-0000-000000000003', 0, 'She sells seashells', 'sh-vs-s', 'MEDIUM', 'a2'),
  ('a0000000-0000-0000-0000-000000000004', 0, 'The weather is rather cold today', 'th-sound', 'EASY', 'b1'),
  ('a0000000-0000-0000-0000-000000000005', 0, 'Thirty-three thousand', 'th-sound', 'HARD', 'b2')
ON CONFLICT DO NOTHING;
