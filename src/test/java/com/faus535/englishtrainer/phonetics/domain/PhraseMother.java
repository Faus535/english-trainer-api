package com.faus535.englishtrainer.phonetics.domain;

import java.util.List;
import java.util.UUID;

public final class PhraseMother {
    private PhraseMother() {}

    public static PhonemePracticePhrase random(PhonemeId phonemeId) {
        return PhonemePracticePhrase.reconstitute(
            new PhonemePracticePhraseId(UUID.randomUUID()), phonemeId,
            "Sit in this ship", "easy", List.of("sit", "ship")
        );
    }

    public static PhonemePracticePhrase withId(PhonemePracticePhraseId id, PhonemeId phonemeId) {
        return PhonemePracticePhrase.reconstitute(
            id, phonemeId, "Sit in this ship", "easy", List.of("sit", "ship")
        );
    }

    public static List<PhonemePracticePhrase> fiveForPhoneme(PhonemeId phonemeId) {
        return List.of(
            random(phonemeId), random(phonemeId), random(phonemeId),
            random(phonemeId), random(phonemeId)
        );
    }
}
