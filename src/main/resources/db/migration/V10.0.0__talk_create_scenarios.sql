CREATE TABLE talk_scenarios (
    id               UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    title            VARCHAR(200) NOT NULL,
    description      TEXT,
    context_prompt   TEXT NOT NULL,
    category         VARCHAR(50) NOT NULL,
    cefr_level       VARCHAR(5) NOT NULL CHECK (cefr_level IN ('a1','a2','b1','b2','c1','c2')),
    difficulty_order INTEGER NOT NULL DEFAULT 0,
    created_at       TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT now()
);

CREATE INDEX idx_talk_scenarios_cefr_level ON talk_scenarios(cefr_level);
