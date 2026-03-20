package com.faus535.englishtrainer.spacedrepetition.domain;

import com.faus535.englishtrainer.user.domain.UserProfileId;

public final class SpacedRepetitionItemMother {

    private SpacedRepetitionItemMother() {}

    public static SpacedRepetitionItem create() {
        return SpacedRepetitionItem.create(
                UserProfileId.generate(),
                "vocabulary",
                "A1",
                0
        );
    }

    public static SpacedRepetitionItem create(UserProfileId userId) {
        return SpacedRepetitionItem.create(userId, "vocabulary", "A1", 0);
    }

    public static SpacedRepetitionItem create(UserProfileId userId, String moduleName, String level, int unitIndex) {
        return SpacedRepetitionItem.create(userId, moduleName, level, unitIndex);
    }
}
