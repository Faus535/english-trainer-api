package com.faus535.englishtrainer.immerse.domain;

public enum ContentType {

    TEXT("TEXT"),
    AUDIO("AUDIO"),
    VIDEO("VIDEO");

    private final String value;

    ContentType(String value) { this.value = value; }

    public String value() { return value; }

    public static ContentType fromString(String type) {
        for (ContentType t : values()) {
            if (t.value.equalsIgnoreCase(type)) return t;
        }
        throw new IllegalArgumentException("Unknown content type: " + type);
    }
}
