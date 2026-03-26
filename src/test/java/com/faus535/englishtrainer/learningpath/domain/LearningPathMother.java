package com.faus535.englishtrainer.learningpath.domain;

import com.faus535.englishtrainer.user.domain.UserProfileId;

import java.util.List;

public final class LearningPathMother {

    private LearningPathMother() {}

    public static LearningPath create() {
        return LearningPath.create(
                UserProfileId.generate(),
                "A1",
                List.of(
                        LearningUnitId.generate(),
                        LearningUnitId.generate(),
                        LearningUnitId.generate()
                )
        );
    }

    public static LearningPath create(UserProfileId userId) {
        return LearningPath.create(
                userId,
                "A1",
                List.of(
                        LearningUnitId.generate(),
                        LearningUnitId.generate(),
                        LearningUnitId.generate()
                )
        );
    }

    public static LearningPath create(UserProfileId userId, String level, List<LearningUnitId> unitIds) {
        return LearningPath.create(userId, level, unitIds);
    }
}
