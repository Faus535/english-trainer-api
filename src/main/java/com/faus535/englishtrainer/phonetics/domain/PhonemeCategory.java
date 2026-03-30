package com.faus535.englishtrainer.phonetics.domain;

public enum PhonemeCategory {
    VOWEL("vowel"),
    CONSONANT("consonant");

    private final String value;

    PhonemeCategory(String value) {
        this.value = value;
    }

    public String value() {
        return value;
    }

    public static PhonemeCategory fromString(String text) {
        for (PhonemeCategory category : values()) {
            if (category.value.equalsIgnoreCase(text)) {
                return category;
            }
        }
        throw new IllegalArgumentException("Unknown phoneme category: " + text);
    }
}
