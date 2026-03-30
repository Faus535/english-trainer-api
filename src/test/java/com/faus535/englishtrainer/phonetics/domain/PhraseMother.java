package com.faus535.englishtrainer.phonetics.domain;

import java.util.ArrayList;
import java.util.List;

public final class PhraseMother {

    private PhraseMother() {}

    public static PhonemePracticePhrase random(PhonemeId phonemeId) {
        return PhonemePracticePhrase.reconstitute(
                PhonemePracticePhraseId.generate(), phonemeId,
                "The quick brown fox", "easy",
                List.of("quick", "brown", "fox")
        );
    }

    public static List<PhonemePracticePhrase> fiveForPhoneme(PhonemeId phonemeId) {
        List<PhonemePracticePhrase> phrases = new ArrayList<>();
        for (int i = 1; i <= 5; i++) {
            String difficulty = i <= 2 ? "easy" : i <= 4 ? "medium" : "hard";
            phrases.add(PhonemePracticePhrase.reconstitute(
                    PhonemePracticePhraseId.generate(), phonemeId,
                    "Practice phrase " + i, difficulty,
                    List.of("word" + i)
            ));
        }
        return phrases;
    }
}
