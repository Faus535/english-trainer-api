package com.faus535.englishtrainer.learningpath.domain;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public final class LearningUnitMother {

    private LearningUnitMother() {}

    public static LearningUnit create() {
        return LearningUnit.create(
                LearningPathId.generate(),
                0,
                "Greetings & Introductions",
                "A1",
                List.of(
                        new UnitContent(ContentType.VOCAB, UUID.randomUUID(), false, null),
                        new UnitContent(ContentType.PHRASE, UUID.randomUUID(), false, null),
                        new UnitContent(ContentType.GRAMMAR, UUID.randomUUID(), false, null)
                )
        );
    }

    public static LearningUnit create(LearningPathId pathId) {
        return LearningUnit.create(
                pathId,
                0,
                "Greetings & Introductions",
                "A1",
                List.of(
                        new UnitContent(ContentType.VOCAB, UUID.randomUUID(), false, null),
                        new UnitContent(ContentType.PHRASE, UUID.randomUUID(), false, null),
                        new UnitContent(ContentType.GRAMMAR, UUID.randomUUID(), false, null)
                )
        );
    }

    public static LearningUnit create(LearningPathId pathId, int unitIndex, String unitName) {
        return LearningUnit.create(
                pathId,
                unitIndex,
                unitName,
                "A1",
                List.of(
                        new UnitContent(ContentType.VOCAB, UUID.randomUUID(), false, null),
                        new UnitContent(ContentType.PHRASE, UUID.randomUUID(), false, null)
                )
        );
    }

    public static LearningUnit withContents(LearningPathId pathId, List<UnitContent> contents) {
        return LearningUnit.create(
                pathId,
                0,
                "Custom Unit",
                "A1",
                contents
        );
    }
}
