package com.faus535.englishtrainer.vocabulary.domain;

import com.faus535.englishtrainer.user.domain.UserProfileId;

public final class VocabMasteryMother {

    private VocabMasteryMother() {}

    public static VocabMastery create() {
        return VocabMastery.create(
                UserProfileId.generate(),
                VocabEntryId.generate(),
                "hello",
                new MasterySource("game", "word-match")
        );
    }

    public static VocabMastery create(UserProfileId userId) {
        return VocabMastery.create(
                userId,
                VocabEntryId.generate(),
                "hello",
                new MasterySource("game", "word-match")
        );
    }

    public static VocabMastery create(UserProfileId userId, String word) {
        return VocabMastery.create(
                userId,
                VocabEntryId.generate(),
                word,
                new MasterySource("session", "vocabulary")
        );
    }

    public static VocabMastery create(UserProfileId userId, VocabEntryId vocabEntryId, String word) {
        return VocabMastery.create(
                userId,
                vocabEntryId,
                word,
                new MasterySource("game", "word-match")
        );
    }

    public static VocabMastery withoutVocabEntry() {
        return VocabMastery.create(
                UserProfileId.generate(),
                null,
                "ephemeral",
                new MasterySource("game", "word-match")
        );
    }
}
