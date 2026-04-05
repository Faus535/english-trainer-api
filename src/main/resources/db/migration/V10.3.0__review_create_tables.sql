CREATE TABLE review_items (
    id                  UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id             UUID NOT NULL REFERENCES user_profiles(id) ON DELETE CASCADE,
    source_type         VARCHAR(30) NOT NULL CHECK (source_type IN ('TALK_ERROR','IMMERSE_VOCAB','PRONUNCIATION')),
    source_id           UUID NOT NULL,
    front_content       TEXT NOT NULL,
    back_content        TEXT NOT NULL,
    next_review_at      TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT now(),
    interval_days       INTEGER NOT NULL DEFAULT 1 CHECK (interval_days >= 1),
    ease_factor         NUMERIC(4,2) NOT NULL DEFAULT 2.50 CHECK (ease_factor >= 1.30),
    consecutive_correct INTEGER NOT NULL DEFAULT 0 CHECK (consecutive_correct >= 0),
    created_at          TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT now(),
    version             BIGINT NOT NULL DEFAULT 0,
    UNIQUE(user_id, source_type, source_id)
);

CREATE INDEX idx_review_items_user_due ON review_items(user_id, next_review_at);

CREATE TABLE review_results (
    id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    item_id     UUID NOT NULL REFERENCES review_items(id) ON DELETE CASCADE,
    user_id     UUID NOT NULL REFERENCES user_profiles(id) ON DELETE CASCADE,
    quality     INTEGER NOT NULL CHECK (quality BETWEEN 0 AND 5),
    reviewed_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT now()
);

CREATE INDEX idx_review_results_item ON review_results(item_id);
CREATE INDEX idx_review_results_user_date ON review_results(user_id, reviewed_at);
