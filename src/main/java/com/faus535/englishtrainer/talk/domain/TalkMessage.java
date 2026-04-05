package com.faus535.englishtrainer.talk.domain;

import java.time.Instant;

public record TalkMessage(
        TalkMessageId id,
        String role,
        String content,
        TalkCorrection correction,
        Instant createdAt
) {

    public static TalkMessage userMessage(String content) {
        return new TalkMessage(
                TalkMessageId.generate(), "user", content, null, Instant.now());
    }

    public static TalkMessage assistantMessage(String content, TalkCorrection correction) {
        return new TalkMessage(
                TalkMessageId.generate(), "assistant", content, correction, Instant.now());
    }

    public static TalkMessage reconstitute(TalkMessageId id, String role, String content,
                                            TalkCorrection correction, Instant createdAt) {
        return new TalkMessage(id, role, content, correction, createdAt);
    }
}
