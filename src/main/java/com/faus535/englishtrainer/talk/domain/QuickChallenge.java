package com.faus535.englishtrainer.talk.domain;

public record QuickChallenge(
        String id,
        String title,
        String description,
        QuickChallengeDifficulty difficulty,
        QuickChallengeCategory category
) {}
