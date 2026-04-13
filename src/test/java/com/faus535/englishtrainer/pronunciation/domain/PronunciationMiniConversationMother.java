package com.faus535.englishtrainer.pronunciation.domain;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public final class PronunciationMiniConversationMother {

    private PronunciationMiniConversationMother() {}

    public static PronunciationMiniConversation active() {
        return PronunciationMiniConversation.reconstitute(
                PronunciationMiniConversationId.generate(),
                UUID.randomUUID(),
                "th-sound",
                "b1",
                MiniConversationStatus.ACTIVE,
                "Let's practice the 'th' sound. What do you think about the weather?",
                "I think the weather is nice today.",
                List.of(),
                Instant.now(),
                null
        );
    }

    public static PronunciationMiniConversation withTurns(int count) {
        List<MiniConversationTurn> turns = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            turns.add(new MiniConversationTurn(
                    UUID.randomUUID(), i + 1,
                    "I think the weather is nice today.",
                    "I think the weather is nice today.",
                    80, Instant.now()));
        }
        return PronunciationMiniConversation.reconstitute(
                PronunciationMiniConversationId.generate(),
                UUID.randomUUID(),
                "th-sound",
                "b1",
                MiniConversationStatus.ACTIVE,
                "Great! Now say: I thought about the theory.",
                "I thought about the theory.",
                turns,
                Instant.now(),
                null
        );
    }

    public static PronunciationMiniConversation nearCompletion() {
        return withTurns(PronunciationMiniConversation.MAX_TURNS - 1);
    }

    public static PronunciationMiniConversation completed() {
        List<MiniConversationTurn> turns = new ArrayList<>();
        for (int i = 0; i < PronunciationMiniConversation.MAX_TURNS; i++) {
            turns.add(new MiniConversationTurn(
                    UUID.randomUUID(), i + 1,
                    "I think the weather is nice today.",
                    "I think the weather is nice today.",
                    85, Instant.now()));
        }
        return PronunciationMiniConversation.reconstitute(
                PronunciationMiniConversationId.generate(),
                UUID.randomUUID(),
                "th-sound",
                "b1",
                MiniConversationStatus.COMPLETED,
                null,
                null,
                turns,
                Instant.now().minusSeconds(300),
                Instant.now()
        );
    }
}
