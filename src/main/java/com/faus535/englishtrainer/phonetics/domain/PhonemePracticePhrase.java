package com.faus535.englishtrainer.phonetics.domain;

import com.faus535.englishtrainer.shared.domain.AggregateRoot;

import java.util.List;

public final class PhonemePracticePhrase extends AggregateRoot<PhonemePracticePhraseId> {

    private final PhonemePracticePhraseId id;
    private final PhonemeId phonemeId;
    private final String text;
    private final String difficulty;
    private final List<String> targetWords;

    private PhonemePracticePhrase(PhonemePracticePhraseId id, PhonemeId phonemeId,
                                   String text, String difficulty, List<String> targetWords) {
        this.id = id;
        this.phonemeId = phonemeId;
        this.text = text;
        this.difficulty = difficulty;
        this.targetWords = targetWords;
    }

    public static PhonemePracticePhrase reconstitute(PhonemePracticePhraseId id, PhonemeId phonemeId,
                                                      String text, String difficulty, List<String> targetWords) {
        return new PhonemePracticePhrase(id, phonemeId, text, difficulty, targetWords);
    }

    public PhonemePracticePhraseId id() { return id; }
    public PhonemeId phonemeId() { return phonemeId; }
    public String text() { return text; }
    public String difficulty() { return difficulty; }
    public List<String> targetWords() { return targetWords; }
}
