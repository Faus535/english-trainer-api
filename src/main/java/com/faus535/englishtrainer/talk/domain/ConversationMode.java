package com.faus535.englishtrainer.talk.domain;

public enum ConversationMode {
    FULL, QUICK;

    public static ConversationMode fromString(String value) {
        if (value == null || value.isBlank()) return FULL;
        return ConversationMode.valueOf(value.toUpperCase());
    }
}
