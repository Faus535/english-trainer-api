DELETE FROM user_achievements WHERE achievement_id IN (
    'first_session', 'sessions_10', 'sessions_25', 'sessions_50', 'sessions_100',
    'first_test', 'flashcards_50', 'flashcards_200', 'module_levelup',
    'global_b1', 'global_b2', 'global_c1', 'all_modules', 'perfect_score'
);

DELETE FROM achievements WHERE id IN (
    'first_session', 'sessions_10', 'sessions_25', 'sessions_50', 'sessions_100',
    'first_test', 'flashcards_50', 'flashcards_200', 'module_levelup',
    'global_b1', 'global_b2', 'global_c1', 'all_modules', 'perfect_score'
);
