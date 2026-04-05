package com.faus535.englishtrainer.talk.domain;

public enum TalkStatus {

    ACTIVE("ACTIVE"),
    COMPLETED("COMPLETED");

    private final String value;

    TalkStatus(String value) {
        this.value = value;
    }

    public String value() {
        return value;
    }

    public static TalkStatus fromString(String status) {
        for (TalkStatus s : values()) {
            if (s.value.equalsIgnoreCase(status)) {
                return s;
            }
        }
        throw new IllegalArgumentException("Unknown talk status: " + status);
    }
}
