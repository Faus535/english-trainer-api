package com.faus535.englishtrainer.writing.domain;

import com.faus535.englishtrainer.shared.domain.AggregateRoot;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public final class WritingSubmission extends AggregateRoot<UUID> {

    private final UUID id;
    private final UUID userId;
    private final WritingExerciseId exerciseId;
    private final String text;
    private final int wordCount;
    private final WritingFeedback feedback;
    private final Instant submittedAt;

    private WritingSubmission(UUID id, UUID userId, WritingExerciseId exerciseId, String text,
                               int wordCount, WritingFeedback feedback, Instant submittedAt) {
        this.id = id;
        this.userId = userId;
        this.exerciseId = exerciseId;
        this.text = text;
        this.wordCount = wordCount;
        this.feedback = feedback;
        this.submittedAt = submittedAt;
    }

    public static WritingSubmission create(UUID userId, WritingExerciseId exerciseId,
                                            String text, WritingFeedback feedback) {
        int wordCount = text.split("\\s+").length;
        return new WritingSubmission(UUID.randomUUID(), userId, exerciseId, text, wordCount, feedback, Instant.now());
    }

    public static WritingSubmission reconstitute(UUID id, UUID userId, WritingExerciseId exerciseId,
                                                  String text, int wordCount, WritingFeedback feedback,
                                                  Instant submittedAt) {
        return new WritingSubmission(id, userId, exerciseId, text, wordCount, feedback, submittedAt);
    }

    public UUID id() { return id; }
    public UUID userId() { return userId; }
    public WritingExerciseId exerciseId() { return exerciseId; }
    public String text() { return text; }
    public int wordCount() { return wordCount; }
    public WritingFeedback feedback() { return feedback; }
    public Instant submittedAt() { return submittedAt; }
}
