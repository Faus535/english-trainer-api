package com.faus535.englishtrainer.phonetics.domain;

public enum PhonemeCategory {
    VOWEL("vowel"),
    CONSONANT("consonant");

    private final String value;

    PhonemeCategory(String value) { this.value = value; }

    public String value() { return value; }

    public static PhonemeCategory fromString(String s) {
        return switch (s.toLowerCase()) {
            case "vowel" -> VOWEL;
            case "consonant" -> CONSONANT;
            default -> throw new IllegalArgumentException("Unknown PhonemeCategory: " + s);
        };
    }
}
