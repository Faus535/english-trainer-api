package com.faus535.englishtrainer.review.domain;

public enum ReviewSourceType {

    TALK_ERROR("TALK_ERROR"),
    IMMERSE_VOCAB("IMMERSE_VOCAB"),
    PRONUNCIATION("PRONUNCIATION"),
    ARTICLE("ARTICLE");

    private final String value;

    ReviewSourceType(String value) {
        this.value = value;
    }

    public String value() {
        return value;
    }

    public static ReviewSourceType fromString(String type) {
        for (ReviewSourceType t : values()) {
            if (t.value.equalsIgnoreCase(type)) {
                return t;
            }
        }
        throw new IllegalArgumentException("Unknown review source type: " + type);
    }
}
