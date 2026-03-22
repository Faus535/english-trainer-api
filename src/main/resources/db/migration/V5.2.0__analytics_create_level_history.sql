CREATE TABLE level_history (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL,
    module VARCHAR(50) NOT NULL,
    level VARCHAR(10) NOT NULL,
    changed_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_level_history_user_id ON level_history(user_id);
CREATE INDEX idx_level_history_user_module ON level_history(user_id, module);
