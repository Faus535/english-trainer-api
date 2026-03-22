package com.faus535.englishtrainer.conversation.domain;

public enum ConversationStatus {
    ACTIVE("active"),
    COMPLETED("completed");

    private final String value;

    ConversationStatus(String value) {
        this.value = value;
    }

    public String value() {
        return value;
    }

    public static ConversationStatus fromString(String value) {
        for (ConversationStatus status : values()) {
            if (status.value.equalsIgnoreCase(value)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Invalid conversation status: " + value);
    }
}
