package com.faus535.englishtrainer.phrase.domain;

import com.faus535.englishtrainer.shared.domain.AggregateRoot;
import com.faus535.englishtrainer.vocabulary.domain.VocabLevel;

public final class Phrase extends AggregateRoot<PhraseId> {

    private final PhraseId id;
    private final String en;
    private final String es;
    private final VocabLevel level;

    private Phrase(PhraseId id, String en, String es, VocabLevel level) {
        this.id = id;
        this.en = en;
        this.es = es;
        this.level = level;
    }

    public static Phrase create(String en, String es, VocabLevel level) {
        return new Phrase(PhraseId.generate(), en, es, level);
    }

    public static Phrase reconstitute(PhraseId id, String en, String es, VocabLevel level) {
        return new Phrase(id, en, es, level);
    }

    public PhraseId id() {
        return id;
    }

    public String en() {
        return en;
    }

    public String es() {
        return es;
    }

    public VocabLevel level() {
        return level;
    }
}
