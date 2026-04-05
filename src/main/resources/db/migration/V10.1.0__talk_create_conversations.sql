CREATE TABLE talk_conversations (
    id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id     UUID NOT NULL REFERENCES user_profiles(id) ON DELETE CASCADE,
    scenario_id UUID REFERENCES talk_scenarios(id) ON DELETE SET NULL,
    level       VARCHAR(5) NOT NULL CHECK (level IN ('a1','a2','b1','b2','c1','c2')),
    status      VARCHAR(20) NOT NULL DEFAULT 'ACTIVE' CHECK (status IN ('ACTIVE','COMPLETED')),
    summary     TEXT,
    evaluation_json TEXT,
    started_at  TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT now(),
    ended_at    TIMESTAMP WITH TIME ZONE,
    version     BIGINT NOT NULL DEFAULT 0
);

CREATE INDEX idx_talk_conversations_user_id ON talk_conversations(user_id);
CREATE INDEX idx_talk_conversations_user_status ON talk_conversations(user_id, status);

CREATE TABLE talk_messages (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    conversation_id UUID NOT NULL REFERENCES talk_conversations(id) ON DELETE CASCADE,
    role            VARCHAR(10) NOT NULL CHECK (role IN ('user','assistant')),
    content         TEXT NOT NULL,
    correction_json TEXT,
    created_at      TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT now()
);

CREATE INDEX idx_talk_messages_conversation_id ON talk_messages(conversation_id);
CREATE INDEX idx_talk_messages_conversation_created ON talk_messages(conversation_id, created_at);
