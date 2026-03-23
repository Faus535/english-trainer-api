package com.faus535.englishtrainer.minimalpair.domain;

import com.faus535.englishtrainer.shared.domain.AggregateRoot;

public final class MinimalPair extends AggregateRoot<MinimalPairId> {

    private final MinimalPairId id;
    private final String word1;
    private final String word2;
    private final String ipa1;
    private final String ipa2;
    private final String soundCategory;
    private final String level;

    private MinimalPair(MinimalPairId id, String word1, String word2,
                        String ipa1, String ipa2, String soundCategory, String level) {
        this.id = id;
        this.word1 = word1;
        this.word2 = word2;
        this.ipa1 = ipa1;
        this.ipa2 = ipa2;
        this.soundCategory = soundCategory;
        this.level = level;
    }

    public static MinimalPair reconstitute(MinimalPairId id, String word1, String word2,
                                            String ipa1, String ipa2, String soundCategory, String level) {
        return new MinimalPair(id, word1, word2, ipa1, ipa2, soundCategory, level);
    }

    public MinimalPairId id() { return id; }
    public String word1() { return word1; }
    public String word2() { return word2; }
    public String ipa1() { return ipa1; }
    public String ipa2() { return ipa2; }
    public String soundCategory() { return soundCategory; }
    public String level() { return level; }
}
