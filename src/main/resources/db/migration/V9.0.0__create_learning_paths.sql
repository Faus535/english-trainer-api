CREATE TABLE learning_paths (
    id UUID PRIMARY KEY,
    user_profile_id UUID NOT NULL REFERENCES user_profiles(id) ON DELETE CASCADE,
    current_level VARCHAR(10) NOT NULL,
    current_unit_index INT NOT NULL DEFAULT 0,
    version BIGINT NOT NULL DEFAULT 0,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(user_profile_id)
);

CREATE TABLE learning_units (
    id UUID PRIMARY KEY,
    learning_path_id UUID NOT NULL REFERENCES learning_paths(id) ON DELETE CASCADE,
    unit_index INT NOT NULL,
    unit_name VARCHAR(100) NOT NULL,
    target_level VARCHAR(10) NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'NOT_STARTED',
    mastery_score INT NOT NULL DEFAULT 0 CHECK (mastery_score >= 0 AND mastery_score <= 100),
    contents_data TEXT,
    version BIGINT NOT NULL DEFAULT 0,
    completed_at TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(learning_path_id, unit_index)
);

CREATE INDEX idx_learning_paths_user ON learning_paths(user_profile_id);
CREATE INDEX idx_learning_units_path ON learning_units(learning_path_id);
CREATE INDEX idx_learning_units_path_status ON learning_units(learning_path_id, status);
