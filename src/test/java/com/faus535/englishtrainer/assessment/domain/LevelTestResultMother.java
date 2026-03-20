package com.faus535.englishtrainer.assessment.domain;

import com.faus535.englishtrainer.user.domain.UserProfileId;

import java.util.Map;

public final class LevelTestResultMother {

    private LevelTestResultMother() {
    }

    public static LevelTestResult create() {
        return LevelTestResult.create(
                UserProfileId.generate(),
                75,
                70,
                65,
                80,
                Map.of("vocabulary", "b2", "grammar", "b2", "listening", "b1", "pronunciation", "b2")
        );
    }

    public static LevelTestResult withScores(int vocab, int grammar, int listening, int pronunciation) {
        return LevelTestResult.create(
                UserProfileId.generate(),
                vocab,
                grammar,
                listening,
                pronunciation,
                Map.of("vocabulary", LevelAssigner.assignLevel(vocab),
                        "grammar", LevelAssigner.assignLevel(grammar),
                        "listening", LevelAssigner.assignLevel(listening),
                        "pronunciation", LevelAssigner.assignLevel(pronunciation))
        );
    }

    public static LevelTestResult withUserId(UserProfileId userId) {
        return LevelTestResult.create(
                userId,
                75,
                70,
                65,
                80,
                Map.of("vocabulary", "b2", "grammar", "b2", "listening", "b1", "pronunciation", "b2")
        );
    }
}
