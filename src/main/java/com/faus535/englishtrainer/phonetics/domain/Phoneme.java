package com.faus535.englishtrainer.phonetics.domain;

import com.faus535.englishtrainer.shared.domain.AggregateRoot;

import java.util.List;

public final class Phoneme extends AggregateRoot<PhonemeId> {

    private final PhonemeId id;
    private final String symbol;
    private final String name;
    private final PhonemeCategory category;
    private final String subcategory;
    private final int difficultyOrder;
    private final List<String> exampleWords;
    private final String description;
    private final String mouthPosition;
    private final String tips;

    private Phoneme(PhonemeId id, String symbol, String name, PhonemeCategory category,
                    String subcategory, int difficultyOrder, List<String> exampleWords,
                    String description, String mouthPosition, String tips) {
        this.id = id;
        this.symbol = symbol;
        this.name = name;
        this.category = category;
        this.subcategory = subcategory;
        this.difficultyOrder = difficultyOrder;
        this.exampleWords = exampleWords;
        this.description = description;
        this.mouthPosition = mouthPosition;
        this.tips = tips;
    }

    public static Phoneme reconstitute(PhonemeId id, String symbol, String name, PhonemeCategory category,
                                        String subcategory, int difficultyOrder, List<String> exampleWords,
                                        String description, String mouthPosition, String tips) {
        return new Phoneme(id, symbol, name, category, subcategory, difficultyOrder,
                exampleWords, description, mouthPosition, tips);
    }

    public PhonemeId id() { return id; }
    public String symbol() { return symbol; }
    public String name() { return name; }
    public PhonemeCategory category() { return category; }
    public String subcategory() { return subcategory; }
    public int difficultyOrder() { return difficultyOrder; }
    public List<String> exampleWords() { return exampleWords; }
    public String description() { return description; }
    public String mouthPosition() { return mouthPosition; }
    public String tips() { return tips; }
}
