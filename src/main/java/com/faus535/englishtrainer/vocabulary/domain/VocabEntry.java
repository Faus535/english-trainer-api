package com.faus535.englishtrainer.vocabulary.domain;

import com.faus535.englishtrainer.shared.domain.AggregateRoot;

public final class VocabEntry extends AggregateRoot<VocabEntryId> {

    private final VocabEntryId id;
    private final String en;
    private final String ipa;
    private final String es;
    private final String type;
    private final String example;
    private final VocabLevel level;

    private VocabEntry(VocabEntryId id, String en, String ipa, String es, String type, String example, VocabLevel level) {
        this.id = id;
        this.en = en;
        this.ipa = ipa;
        this.es = es;
        this.type = type;
        this.example = example;
        this.level = level;
    }

    public static VocabEntry create(VocabEntryId id, String en, String ipa, String es, String type, String example, VocabLevel level) {
        return new VocabEntry(id, en, ipa, es, type, example, level);
    }

    public VocabEntryId id() {
        return id;
    }

    public String en() {
        return en;
    }

    public String ipa() {
        return ipa;
    }

    public String es() {
        return es;
    }

    public String type() {
        return type;
    }

    public String example() {
        return example;
    }

    public VocabLevel level() {
        return level;
    }
}
