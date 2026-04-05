package com.faus535.englishtrainer.talk.domain;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public final class TalkConversationMother {

    private static final UUID DEFAULT_USER_ID = UUID.fromString("00000000-0000-0000-0000-000000000001");
    private static final UUID DEFAULT_SCENARIO_ID = UUID.fromString("00000000-0000-0000-0000-000000000010");

    public static TalkConversation active() {
        return TalkConversation.reconstitute(
                TalkConversationId.generate(), DEFAULT_USER_ID, DEFAULT_SCENARIO_ID,
                new TalkLevel("b1"), TalkStatus.ACTIVE, null, null,
                Instant.now(), null, List.of());
    }

    public static TalkConversation withMessages(int count) {
        List<TalkMessage> messages = new java.util.ArrayList<>();
        for (int i = 0; i < count; i++) {
            if (i % 2 == 0) {
                messages.add(TalkMessage.assistantMessage("Hello " + i, TalkCorrection.empty()));
            } else {
                messages.add(TalkMessage.userMessage("Hi " + i));
            }
        }
        return TalkConversation.reconstitute(
                TalkConversationId.generate(), DEFAULT_USER_ID, DEFAULT_SCENARIO_ID,
                new TalkLevel("b1"), TalkStatus.ACTIVE, null, null,
                Instant.now(), null, messages);
    }

    public static TalkConversation completed() {
        return TalkConversation.reconstitute(
                TalkConversationId.generate(), DEFAULT_USER_ID, DEFAULT_SCENARIO_ID,
                new TalkLevel("b1"), TalkStatus.COMPLETED, "Good session.",
                new TalkEvaluation(80, 70, 75, 85, 78, "b1", List.of("Good grammar"), List.of("More vocabulary")),
                Instant.now().minusSeconds(3600), Instant.now(), List.of());
    }

    public static TalkConversation withCorrections(int count) {
        List<TalkMessage> messages = new java.util.ArrayList<>();
        for (int i = 0; i < count; i++) {
            messages.add(TalkMessage.userMessage("Test message " + i));
            TalkCorrection correction = new TalkCorrection(
                    List.of("Fix grammar " + i), List.of(), List.of(), "Good try!");
            messages.add(TalkMessage.assistantMessage("Response " + i, correction));
        }
        return TalkConversation.reconstitute(
                TalkConversationId.generate(), DEFAULT_USER_ID, DEFAULT_SCENARIO_ID,
                new TalkLevel("b1"), TalkStatus.ACTIVE, null, null,
                Instant.now(), null, messages);
    }
}
