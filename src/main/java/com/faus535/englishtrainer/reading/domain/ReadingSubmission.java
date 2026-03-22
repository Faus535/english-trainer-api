package com.faus535.englishtrainer.reading.domain;

import com.faus535.englishtrainer.shared.domain.AggregateRoot;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public final class ReadingSubmission extends AggregateRoot<UUID> {

    private final UUID id;
    private final UUID userId;
    private final ReadingPassageId passageId;
    private final double score;
    private final List<Integer> answers;
    private final Instant completedAt;

    private ReadingSubmission(UUID id, UUID userId, ReadingPassageId passageId,
                               double score, List<Integer> answers, Instant completedAt) {
        this.id = id;
        this.userId = userId;
        this.passageId = passageId;
        this.score = score;
        this.answers = List.copyOf(answers);
        this.completedAt = completedAt;
    }

    public static ReadingSubmission create(UUID userId, ReadingPassageId passageId,
                                            double score, List<Integer> answers) {
        return new ReadingSubmission(UUID.randomUUID(), userId, passageId, score, answers, Instant.now());
    }

    public static ReadingSubmission reconstitute(UUID id, UUID userId, ReadingPassageId passageId,
                                                  double score, List<Integer> answers, Instant completedAt) {
        return new ReadingSubmission(id, userId, passageId, score, answers, completedAt);
    }

    public UUID id() { return id; }
    public UUID userId() { return userId; }
    public ReadingPassageId passageId() { return passageId; }
    public double score() { return score; }
    public List<Integer> answers() { return answers; }
    public Instant completedAt() { return completedAt; }
}
