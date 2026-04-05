ALTER TABLE immerse_content
    ADD COLUMN content_type VARCHAR(10)
    CHECK (content_type IN ('TEXT','AUDIO','VIDEO'));
