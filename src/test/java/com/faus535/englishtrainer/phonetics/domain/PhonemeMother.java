package com.faus535.englishtrainer.phonetics.domain;

import java.util.List;

public final class PhonemeMother {

    private PhonemeMother() {}

    public static Phoneme random() {
        return Phoneme.reconstitute(
                PhonemeId.generate(), "/ɪ/", "near-close near-front unrounded",
                PhonemeCategory.VOWEL, "short_vowel", 1,
                List.of("sit", "fish", "bit"),
                "Short vowel sound", "Lips slightly apart", "Relax the tongue"
        );
    }

    public static Phoneme withId(PhonemeId id) {
        return Phoneme.reconstitute(
                id, "/ɪ/", "near-close near-front unrounded",
                PhonemeCategory.VOWEL, "short_vowel", 1,
                List.of("sit", "fish", "bit"),
                "Short vowel sound", "Lips slightly apart", "Relax the tongue"
        );
    }

    public static Phoneme withIdAndOrder(PhonemeId id, int order) {
        return Phoneme.reconstitute(
                id, "/phoneme-" + order + "/", "phoneme " + order,
                PhonemeCategory.VOWEL, "short_vowel", order,
                List.of("word1", "word2", "word3"),
                "Description " + order, "Mouth position " + order, "Tips " + order
        );
    }
}
