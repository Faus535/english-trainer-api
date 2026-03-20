package com.faus535.englishtrainer.phrase.domain;

import com.faus535.englishtrainer.vocabulary.domain.VocabLevel;

public final class PhraseMother {

    public static Phrase create() {
        return Phrase.create("How are you?", "Como estas?", new VocabLevel("a1"));
    }

    public static Phrase withLevel(String level) {
        return Phrase.create("How are you?", "Como estas?", new VocabLevel(level));
    }
}
