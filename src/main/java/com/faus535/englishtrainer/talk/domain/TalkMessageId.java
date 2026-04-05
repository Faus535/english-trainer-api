package com.faus535.englishtrainer.talk.domain;

import java.util.UUID;

public record TalkMessageId(UUID value) {

    public TalkMessageId {
        if (value == null) {
            throw new IllegalArgumentException("TalkMessageId cannot be null");
        }
    }

    public static TalkMessageId generate() {
        return new TalkMessageId(UUID.randomUUID());
    }
}
