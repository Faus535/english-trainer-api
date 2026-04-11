ALTER TABLE review_items RENAME COLUMN consecutive_correct TO repetitions;
ALTER TABLE review_items ALTER COLUMN next_review_at TYPE DATE USING next_review_at::DATE;
