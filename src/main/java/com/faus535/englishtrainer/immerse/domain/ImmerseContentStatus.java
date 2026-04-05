package com.faus535.englishtrainer.immerse.domain;

public enum ImmerseContentStatus {

    PENDING("PENDING"),
    PROCESSED("PROCESSED"),
    FAILED("FAILED");

    private final String value;

    ImmerseContentStatus(String value) { this.value = value; }

    public String value() { return value; }

    public static ImmerseContentStatus fromString(String status) {
        for (ImmerseContentStatus s : values()) {
            if (s.value.equalsIgnoreCase(status)) return s;
        }
        throw new IllegalArgumentException("Unknown immerse content status: " + status);
    }
}
