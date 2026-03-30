package com.faus535.englishtrainer.phonetics.domain;

import java.util.List;
import java.util.UUID;

public final class PhonemeMother {
    private PhonemeMother() {}

    public static Phoneme random() {
        return Phoneme.reconstitute(
            new PhonemeId(UUID.randomUUID()), "/i/", "near-close near-front unrounded",
            PhonemeCategory.VOWEL, "short_vowel",
            List.of("sit", "fish", "bit"),
            "Short i as in sit", "Lips slightly spread", "Quick relaxed ee", 1
        );
    }

    public static Phoneme withId(PhonemeId id) {
        return Phoneme.reconstitute(id, "/i/", "near-close near-front unrounded",
            PhonemeCategory.VOWEL, "short_vowel",
            List.of("sit", "fish", "bit"),
            "Short i as in sit", "Lips slightly spread", "Quick relaxed ee", 1
        );
    }

    public static Phoneme withIdAndOrder(PhonemeId id, int order) {
        return Phoneme.reconstitute(id, "/test" + order + "/", "test phoneme " + order,
            PhonemeCategory.VOWEL, "short_vowel",
            List.of("word1", "word2"),
            "Description", "Mouth position", "Tips", order
        );
    }
}
