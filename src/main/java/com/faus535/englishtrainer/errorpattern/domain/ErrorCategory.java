package com.faus535.englishtrainer.errorpattern.domain;

public enum ErrorCategory {
    TENSE,
    ARTICLE,
    PREPOSITION,
    WORD_ORDER,
    SUBJECT_VERB_AGREEMENT,
    VOCABULARY,
    SPELLING,
    PUNCTUATION,
    OTHER;

    public static ErrorCategory fromString(String value) {
        if (value == null) return OTHER;
        try {
            return valueOf(value.toUpperCase());
        } catch (IllegalArgumentException e) {
            return OTHER;
        }
    }
}
