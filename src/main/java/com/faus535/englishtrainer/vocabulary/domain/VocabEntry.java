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
    private final String category;
    private final Integer block;
    private final String blockTitle;

    private VocabEntry(VocabEntryId id, String en, String ipa, String es, String type, String example,
                       VocabLevel level, String category, Integer block, String blockTitle) {
        this.id = id;
        this.en = en;
        this.ipa = ipa;
        this.es = es;
        this.type = type;
        this.example = example;
        this.level = level;
        this.category = category;
        this.block = block;
        this.blockTitle = blockTitle;
    }

    public static VocabEntry create(VocabEntryId id, String en, String ipa, String es, String type, String example, VocabLevel level) {
        return new VocabEntry(id, en, ipa, es, type, example, level, null, null, null);
    }

    public static VocabEntry create(VocabEntryId id, String en, String ipa, String es, String type, String example,
                                     VocabLevel level, String category, Integer block, String blockTitle) {
        return new VocabEntry(id, en, ipa, es, type, example, level, category, block, blockTitle);
    }

    public VocabEntryId id() { return id; }
    public String en() { return en; }
    public String ipa() { return ipa; }
    public String es() { return es; }
    public String type() { return type; }
    public String example() { return example; }
    public VocabLevel level() { return level; }
    public String category() { return category; }
    public Integer block() { return block; }
    public String blockTitle() { return blockTitle; }
}
