-- Drop tables from archived modules (child tables first, then parents)

-- Assessment
DROP TABLE IF EXISTS test_question_history CASCADE;
DROP TABLE IF EXISTS test_questions CASCADE;
DROP TABLE IF EXISTS mini_test_results CASCADE;
DROP TABLE IF EXISTS level_test_results CASCADE;

-- Conversation
DROP TABLE IF EXISTS conversation_exercises CASCADE;
DROP TABLE IF EXISTS conversation_turns CASCADE;
DROP TABLE IF EXISTS conversations CASCADE;

-- Reading
DROP TABLE IF EXISTS reading_submissions CASCADE;
DROP TABLE IF EXISTS reading_questions CASCADE;
DROP TABLE IF EXISTS reading_passages CASCADE;

-- Writing
DROP TABLE IF EXISTS writing_submissions CASCADE;
DROP TABLE IF EXISTS writing_exercises CASCADE;

-- Session
DROP TABLE IF EXISTS sessions CASCADE;

-- Spaced Repetition
DROP TABLE IF EXISTS spaced_repetition_items CASCADE;

-- Vocabulary
DROP TABLE IF EXISTS vocabulary_context CASCADE;
DROP TABLE IF EXISTS vocab_mastery CASCADE;
DROP TABLE IF EXISTS vocab_entries CASCADE;

-- Phrases
DROP TABLE IF EXISTS phrases CASCADE;

-- Analytics
DROP TABLE IF EXISTS level_history CASCADE;

-- Error tracking
DROP TABLE IF EXISTS error_patterns CASCADE;
DROP TABLE IF EXISTS tutor_errors CASCADE;
DROP TABLE IF EXISTS pronunciation_errors CASCADE;

-- Pronunciation
DROP TABLE IF EXISTS minimal_pair_results CASCADE;
DROP TABLE IF EXISTS minimal_pairs CASCADE;

-- Daily challenge
DROP TABLE IF EXISTS user_challenges CASCADE;
DROP TABLE IF EXISTS daily_challenges CASCADE;

-- Minigame
DROP TABLE IF EXISTS mini_game_scores CASCADE;

-- Learning paths
DROP TABLE IF EXISTS learning_units CASCADE;
DROP TABLE IF EXISTS learning_paths CASCADE;

-- Phonetics
DROP TABLE IF EXISTS phoneme_daily_assignments CASCADE;
DROP TABLE IF EXISTS user_phoneme_progress CASCADE;
DROP TABLE IF EXISTS phoneme_practice_phrases CASCADE;
DROP TABLE IF EXISTS phonemes CASCADE;
