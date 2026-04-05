package com.faus535.englishtrainer.immerse.domain;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public final class ImmerseContentMother {

    private static final UUID DEFAULT_USER_ID = UUID.fromString("00000000-0000-0000-0000-000000000001");

    public static ImmerseContent pending() {
        return ImmerseContent.reconstitute(
                ImmerseContentId.generate(), DEFAULT_USER_ID, null, "Test Article",
                "The quick brown fox jumps over the lazy dog.", null, null,
                List.of(), ImmerseContentStatus.PENDING, Instant.now());
    }

    public static ImmerseContent processed() {
        return ImmerseContent.reconstitute(
                ImmerseContentId.generate(), DEFAULT_USER_ID,
                "https://example.com/article", "Climate Change",
                "Climate change affects everyone...", "Climate change affects everyone...",
                "b1", List.of(
                        new VocabularyItem("drought", "A long period without rain", "The drought lasted months.", "b2"),
                        new VocabularyItem("emission", "Gas released into the atmosphere", "Carbon emissions are rising.", "b2")
                ), ImmerseContentStatus.PROCESSED, Instant.now());
    }

    public static ImmerseContent withUserId(UUID userId) {
        return ImmerseContent.reconstitute(
                ImmerseContentId.generate(), userId, null, "User Article",
                "Some text.", "Some text.", "b1",
                List.of(), ImmerseContentStatus.PROCESSED, Instant.now());
    }
}
