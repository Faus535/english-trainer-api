package com.faus535.englishtrainer.article.domain;

public enum ArticleLevel {

    B1("B1"),
    B2("B2"),
    C1("C1");

    private final String value;

    ArticleLevel(String value) {
        this.value = value;
    }

    public String value() {
        return value;
    }

    public int minWords() {
        return switch (this) {
            case B1 -> 20;
            case B2 -> 30;
            case C1 -> 40;
        };
    }

    public String questionStyle() {
        return switch (this) {
            case B1 -> "GUIDED";
            case B2 -> "MIXED";
            case C1 -> "ANALYSIS";
        };
    }

    public static ArticleLevel fromString(String level) {
        for (ArticleLevel l : values()) {
            if (l.value.equalsIgnoreCase(level)) {
                return l;
            }
        }
        throw new IllegalArgumentException("Unknown article level: " + level);
    }
}
